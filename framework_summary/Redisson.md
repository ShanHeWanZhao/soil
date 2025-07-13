# Redisson(3.16.8)

## 1 RLock（分布式互斥锁）

实现类为**RedissonLock**，互斥锁获取锁可大致分为3类，分别是：

- **尝试获取锁**：最简单的一种情况，只需判断一下锁是否存在，再做对应的操作就行
- **定时尝试获取锁**：利用redis的发布和订阅机制，收到解锁的消息后会唤醒一个阻塞的线程再尝试获取锁，而本地则使用AQS中的定时尝试。
- **无限制的阻塞获取锁**：和上面一个很像，本质是一种定时自旋再尝试获取锁

### 1.1 tryAcquireOnceAsync

#### **加锁脚本**

``` lua
if (redis.call('exists', KEYS[1]) == 0) then -- 初次加锁
   redis.call('hset', KEYS[1], ARGV[2], 1);
   redis.call('pexpire', KEYS[1], ARGV[1]);
   return nil;
end;
if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then -- 锁重入
   redis.call('hincrby', KEYS[1], ARGV[2], 1);
   redis.call('pexpire', KEYS[1], ARGV[1]);
   return nil;
end;
return redis.call('pttl', KEYS[1]); -- 锁存在，返回过期时间
--[[ 
    keys:
        keys[1]：锁名
    argv:
        argv[1]：30000
        argv[2]：真实的lockName（客户端id:线程id），客户端id就是uuid
]]
```

尝试加锁逻辑：

​	先判断redis中是否存在锁名为keys[1]的键

​	如果不存在，就代表是第一次加锁，构造一个hash结构的key，key为我们指定的锁名。key中只有一对Dict，Dict的key为（客户端id:线程id），根据这个Dict里的key我们可以唯一确定某台客户端的某个线程，value就设为1就行，设为数字好自增用来支持重入。

​	如果存在，就代表不是第一次加锁了，需要先判断是否是当前客户端的当前线程获取到了锁，如果是就代表是锁重入，让Dict的value + 1就行（所以后续解锁是要 -1，重入多少次，就要解锁多少次）。如果不是当前线程获取得锁，就表示有锁冲突，返回锁得过期时间，交给客户端处理

​	所以，返回null就代表获取锁成功，返回数字就表示获取锁失败，显示的是剩余锁的有效时间（这个值参考性不强，因为在获取锁的线程执行过程子，还会不断的重置过期时间，以免锁过期）

#### **定时任务重置key的过期时间**

​		在获取锁成功后，还需要设置一个定时任务，用来无限制的重置key的过期时间（默认时间间隔为lockWatchdogTimeout的三分之一，也就是10秒）。要设置key有过期时间是怕客户端如果宕机，这个key就没法删除，造成其他客户端再也获取不到这个锁。而获取锁后的线程又不清楚要执行多久，所以，在获取锁成功后这段时间内，就需要无限制的重复重置功能。

### 1.2 lock（阻塞获取锁）

```java
private void lock(long leaseTime, TimeUnit unit, boolean interruptibly) throws InterruptedException {
        long threadId = Thread.currentThread().getId();
        // 先尝试获取锁，获取到就可直接返回
        Long ttl = tryAcquire(leaseTime, unit, threadId);
        // lock acquired
        if (ttl == null) {
            return;
        }
        // 异步订阅频道：redisson_lock__channel:{锁名}
        RFuture<RedissonLockEntry> future = subscribe(threadId);
        // 同步等待订阅成功
        commandExecutor.syncSubscription(future);

        try {
            while (true) { // 走到这，就代表订阅成功
                // 再次尝试获取锁
                ttl = tryAcquire(leaseTime, unit, threadId);
                // lock acquired
                if (ttl == null) { // 获取锁成功，直接返回
                    break;
                }

                // waiting for message
                if (ttl >= 0) {
                    // 利用Semaphore，定时尝试获取锁，时间为ttl。
                    // (因为这是定时获取锁，是可自唤醒的，所以类似自旋获取锁)
                    /*
                        这个Semaphore初始值就是0，是获取不到的。
                            只有等待其他线程解锁，向这些订阅的频道发送一条解锁消息，
                            Semaphore的有效值才加一，这时，
                            当前客户端阻塞在这等待获取锁的线程会被立即唤醒一个，继续尝试获取锁 */
                    try {
                        getEntry(threadId).getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        if (interruptibly) {
                            throw e;
                        }
                        getEntry(threadId).getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
                    }
                } else {
                    if (interruptibly) {
                        getEntry(threadId).getLatch().acquire();
                    } else {
                        getEntry(threadId).getLatch().acquireUninterruptibly();
                    }
                }
            }
        } finally { // 根据当前客户端是否还有其它线程在阻塞获取锁来决定是否需要取消订阅（引用计数思想）
            unsubscribe(future, threadId);
        }
    }
// 尝试取消订阅
public void unsubscribe(E entry, String entryName, String channelName) {
    AsyncSemaphore semaphore = service.getSemaphore(new ChannelName(channelName));
    semaphore.acquire(new Runnable() {
        @Override
        public void run() {
            // 减少锁持有的引用计数，如果变为0，代表这个客户端没有线程在阻塞获取这把锁了，就取消订阅
            if (entry.release() == 0) {
                // just an assertion
                boolean removed = entries.remove(entryName) == entry;
                if (!removed) {
                    throw new IllegalStateException();
                }
                service.unsubscribe(new ChannelName(channelName), semaphore);
            } else {
                semaphore.release();
            }
        }
    });

}
```

​		lock操作的主要逻辑就是先尝试获取锁，获取不到锁时就会订阅一个频道，名为：**redisson_lock__channel:{锁名}**，然后定时自旋的去尝试获取锁，在这期间解锁成功的线程会像这个频道发送一条解锁消息，增加Semaphore的值，Semaphore利用自身的功能会唤醒一个阻塞的线程，再来尝试获取锁，依次这么循环下去，直到获取锁成功。

​		当跳出死循环时，需要**尝试取消订阅**，这里是用了引用计数的思想，只有在当前客户端没有线程在获取这把锁的情况下，才会取消订阅。

### 1.3 tryLock（定时获取锁）

```java
public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        long time = unit.toMillis(waitTime);
        long current = System.currentTimeMillis();
        long threadId = Thread.currentThread().getId();
        // 先尝试获取或，获取成功就直接返回
        Long ttl = tryAcquire(leaseTime, unit, threadId);
        // lock acquired
        if (ttl == null) {
            return true;
        }
        // 第一次获取锁失败，判断剩余等待时间
        time -= System.currentTimeMillis() - current;
        if (time <= 0) { // 没有可用的等待时间了，直接返回失败
            acquireFailed(threadId);
            return false;
        }
        
        current = System.currentTimeMillis();
        RFuture<RedissonLockEntry> subscribeFuture = subscribe(threadId);
        // 定时阻塞等待订阅成功（这个时间就为剩余尝试时间），如果这期间连频道都没订阅成功，那也可以构造失败并返回了
        if (!subscribeFuture.await(time, TimeUnit.MILLISECONDS)) {
            if (!subscribeFuture.cancel(false)) {
                subscribeFuture.onComplete((res, e) -> {
                    if (e == null) {
                        unsubscribe(subscribeFuture, threadId);
                    }
                });
            }
            acquireFailed(threadId);
            return false;
        }

        try {
            // 继续计算剩余尝试时间
            time -= System.currentTimeMillis() - current;
            if (time <= 0) {
                acquireFailed(threadId);
                return false;
            }
        
            while (true) { // 到者就代表既没获取锁成功，剩余尝试时间还没用完
                // 在死循环初始流程尝试
                long currentTime = System.currentTimeMillis();
                ttl = tryAcquire(leaseTime, unit, threadId);
                // lock acquired
                if (ttl == null) {
                    return true;
                }

                time -= System.currentTimeMillis() - currentTime;
                if (time <= 0) { // 没获取到锁，并且剩余尝试时间用完，返回失败
                    acquireFailed(threadId);
                    return false;
                }

                // waiting for message
                // 最终还是利用Semaphore的超时等待获取锁来实现的
                currentTime = System.currentTimeMillis();
                if (ttl >= 0 && ttl < time) {
                    getEntry(threadId).getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
                } else {
                    getEntry(threadId).getLatch().tryAcquire(time, TimeUnit.MILLISECONDS);
                }

                time -= System.currentTimeMillis() - currentTime;
                if (time <= 0) {
                    acquireFailed(threadId);
                    return false;
                }
            }
        } finally {
            unsubscribe(subscribeFuture, threadId);
        }
    }
```

​		和lock很像，直接尝试获取锁失败后，就开始订阅频道，在每次唤醒时，测试是否还有剩余等待时间，否则就利用AQS（Semaphore）的定时等待

### 1.4 unlock

虽然加锁的接口可分为3种，但解锁始终都是这个unlock，是配合获取锁成功后，要释放锁时的操作。

#### lua的解锁脚本

```lua
if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then -- 锁不存在
    return nil;
end; 
local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1);
if (counter > 0) then -- 锁重入
    redis.call('pexpire', KEYS[1], ARGV[2]);
    return 0; 
else -- 删除所并publish信号
    redis.call('del', KEYS[1]); 
    redis.call('publish', KEYS[2], ARGV[1]);
    return 1;
end; 
return nil;
--[[
    keys:
        keys[1]：锁名
        keys[2]：channelName，频道名。格式：redisson_lock__channel:{锁名}

    argv:
        argv[1]：0 （解锁信号）
        argv[2]：30000
        argv[3]：真实的lockName（客户端id:线程id）
]]
```

解锁逻辑：

​		既然是在解锁，那么之前一定是获取到锁的。所以，直接判断hash结构的Dict是否存在，不存在就返回null（客户端就可以抛异常了，你锁都没加，解个毛的锁）

​		之后再将对应的hash结构里Dict的value - 1，如果还大于0，代表锁重入了，返回0，表示解锁失败

​		如果value不大于0，代表解锁成功。**通过redis的publish，给对应订阅的频道发送一个解锁成功的消息（这里为0，代表解锁信号），通知其他阻塞的想获取锁的线程可以解阻了**（可以再次尝试获取锁，这种情况是针对直接调用lock方法，不加超时）。

​	所以，综上，返回null表示有异常，返回0表示还未完全解锁，返回1表示解锁成功

```java
// 解锁返回的Future<Boolean>
future.onComplete((opStatus, e) -> {
    if (e != null) { // redis执行有异常
        cancelExpirationRenewal(threadId);
        result.tryFailure(e);
        return;
    }

    if (opStatus == null) { // 还没加锁就想解锁，构建一个异常作为Future的失败结果，让上层去处理
        IllegalMonitorStateException cause = new IllegalMonitorStateException("attempt to unlock lock, not locked by current thread by node id: "
                + id + " thread-id: " + threadId);
        result.tryFailure(cause);
        return;
    }
    // 判断是否需要移除重置key的tll任务（如果锁有重入，是不需要移除的）
    cancelExpirationRenewal(threadId);
    result.trySuccess(null);
});
```

## 2 RSemaphore（分布式共享锁之信号量）

​		实现类为**RedissonSemaphore**，主要实现思想就是**在redis存一个string类型的key，key名为信号量名，value为一个数字，代表可获取的信号量值，并利用jdk的Semaphore和redis的pub/sub机制实现线程的阻塞和唤醒**。

​		这里不像RedissonLock用hash结构来存是因为这是共享锁，根本不需要考虑哪个线程获取了量值（permits），就算是重入，也把线程像第一次获取量值时对待就行。**value的值就代表可获取的量值，代表了在分布式的多节点下最多允许同时运行同步块代码的线程数**（毕竟如果一个线程一次获取了2个量值，就达不到value各线程同时运行了）

​		使用时需要先**org.redisson.api.RSemaphore#trySetPermits方法来设置premit的个数**，其余方法就和jdk的Semaphore一致

## 3 RCountDownLatch（分布式共享锁之门闩）

​		实现类为RedissonCountDownLatch，和RSemaphore类似都用redis的string保存信息，再**利用jdk的AQS和redis的pub/sub机制实现线程的阻塞和唤醒**。在使用前需要org.redisson.api.RCountDownLatchAsync#trySetCountAsync方法来设置计数值，且RCountDownLatch支持重复使用

## 4 RedLock

### 4.1 **为什么会存在红锁？**

>  		Redis集群模式下，对master节点保存锁信息成功，但同步到从节点redis服务器是一个异步过程，在同步过程中，如果master节点宕机导致同步失败，这个从节点晋升为master节点就会导致锁丢失，如果此时还有对这个锁进行上锁的线程也会上锁成功，分布式互斥锁就失效了。
>	
>  		所以，存在红锁就是为了防止以上情况出现。就算加锁成功后宕机一个分锁的master节点导致这个分锁的所信息丢失，也会有红锁的规则束缚（超过半数加锁成功才算成功）着让其他线程不会加锁成功。由此也可以推断，分锁数量越多，能容忍宕机的分锁master节点也就越多，锁也更可靠，但同时也会消耗更多的资源		

### 4.2 红锁如果实现

​		基于在多个redis服务端的具有相同key的互斥锁实现。使用方式就是对每个redis服务端都构造相同key的RedissonLock，将这些RedissonLock封装到RedissonRedLock里，在尝试对RedissonRedLock进行加锁解锁操作

​	解释下分锁和总锁，会用在下面的代码注释里

- 分锁：每个RedissonLock（互斥锁）
- 总锁：总体的RedissonRedLock

核心代码如下

#### 4.2.1 tryLock

​		依次尝试对每个分锁加锁，如果分锁成功总数超过((分锁个数 / 2) + 1)个的话，就代表总锁加锁成功，每个分锁在操作完毕后（不论是枷锁成功还是失败），都需要重新计算剩余可用的等待时间，以免总体超时。

RedLock的加锁本质就是实现分布式锁的高可用，就算某台redis服务宕机，也不影响加锁。再通过如下代码，可总结出RedLock的使用方式：

- 分锁个数至少应该大于2。否则就要保证分锁全部加锁成功才能总锁成功，一台redis服务宕机就会失败，这和高可用相违背
- 分锁个数越多，能容忍的redis宕机个数也就越多，就越符合高可用。但同时产生的问题也就是加锁时间会越长，最主要的还是要准备更多的redis服务。所以，本质就是时间和金钱换可用性。

代码实现：

```java
 @Override
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        long newLeaseTime = -1;
        if (leaseTime != -1) {
            if (waitTime == -1) {
                newLeaseTime = unit.toMillis(leaseTime);
            } else {
                newLeaseTime = unit.toMillis(waitTime)*2;
            }
        }
        
        long time = System.currentTimeMillis();
        long remainTime = -1;
        if (waitTime != -1) {
            remainTime = unit.toMillis(waitTime);
        }
        // 获取每个锁时的超时等待时间（我们指定的等待时间均等分）
        long lockWaitTime = calcLockWaitTime(remainTime);
        // 获取分锁失败数阈值（分锁个数的小一半）
        int failedLocksLimit = failedLocksLimit();
        List<RLock> acquiredLocks = new ArrayList<>(locks.size());
        for (ListIterator<RLock> iterator = locks.listIterator(); iterator.hasNext();) {
            RLock lock = iterator.next();
            boolean lockAcquired;
            try {
                if (waitTime == -1 && leaseTime == -1) {
                    lockAcquired = lock.tryLock();
                } else {
                    long awaitTime = Math.min(lockWaitTime, remainTime);
                    lockAcquired = lock.tryLock(awaitTime, newLeaseTime, TimeUnit.MILLISECONDS);
                }
            } catch (RedisResponseTimeoutException e) {
                unlockInner(Arrays.asList(lock));
                lockAcquired = false;
            } catch (Exception e) {
                lockAcquired = false;
            }
            
            if (lockAcquired) { // 获取锁成功
                acquiredLocks.add(lock);
            } else { // 当前分锁失败，但不代表总锁获取失败
                if (locks.size() - acquiredLocks.size() == failedLocksLimit()) {
          // 虽然当前RLock获取失败，但成功总数已经到达了最低成功个数阈值，直接break，返回获取锁成功
                    break;
                }

                if (failedLocksLimit == 0) { // 不允许由分锁获取失败的情况，但还是分锁获取失败
         // 出现这种情况就是分锁个数小于3，但分锁又获取失败，直接释放获取成功的分锁，返回获取锁失败
                    unlockInner(acquiredLocks);
                    if (waitTime == -1) {
                        return false;
                    }
                    failedLocksLimit = failedLocksLimit();
                    acquiredLocks.clear();
                    // reset iterator
                    while (iterator.hasPrevious()) {
                        iterator.previous();
                    }
                } else { // 减小分锁失败最大阈值
                    failedLocksLimit--;
                }
            }
            
            if (remainTime != -1) { 
                // 计算剩余等待时间，如果没有了（超时），释放获取到的锁，再返回失败
                remainTime -= System.currentTimeMillis() - time;
                time = System.currentTimeMillis();
                if (remainTime <= 0) {
                    unlockInner(acquiredLocks);
                    return false;
                }
            }
        }

        if (leaseTime != -1) {
            List<RFuture<Boolean>> futures = new ArrayList<>(acquiredLocks.size());
            for (RLock rLock : acquiredLocks) {
                RFuture<Boolean> future = ((RedissonLock) rLock).expireAsync(unit.toMillis(leaseTime), TimeUnit.MILLISECONDS);
                futures.add(future);
            }
            
            for (RFuture<Boolean> rFuture : futures) {
                rFuture.syncUninterruptibly();
            }
        }
        
        return true;
    }
```

#### 4.2.2 unlock

​		依次对每个分锁尝试解锁，就算解锁失败（分锁如果根本就没获取到锁，会出现IllegalMonitorStateException异常）也不会抛出异常。所以，放心解锁，最好就算总锁没获取成功，也操作unlock，比如程序中出现什么Error问题，导致获取成功的分锁没解锁就抛了出去，导致这几十秒时间这个redis节点不能加锁成功

```java
public void unlock() {
    unlockInner(locks);
}
protected void unlockInner(Collection<RLock> locks) {
    List<RFuture<Void>> futures = new ArrayList<>(locks.size());
    for (RLock lock : locks) {
        futures.add(lock.unlockAsync());
    }

    for (RFuture<Void> unlockFuture : futures) {
        // 同步等待解锁的Future，就算Future里有异常，也不会抛出去。所以，放心解锁吧
        unlockFuture.awaitUninterruptibly();
    }
}
```

## 5 redisoon对分布式锁的优化（RLock添加了WAIT命令代替红锁）

### 5.1 为什么不推荐使用红锁？

> ​		红锁的实现方式就限制了加锁时需要使用多个完全独立的redis服务器。但现在大多数都是redis集群，redis集群模式要使用红锁必须设置不同的key，保证这些不同的key通过 CRC16 计算后分布在不同的槽，从而保存在不同的redis分片中，才能实现相互独立。这样既要多消耗服务器的资源，也需要开发小心设置key。且redisson对普通分布式锁也有如下优化，所以redis集群比较稳定就是用普通分布式锁就行

### 5.2 RLock + WAIT命令

命令格式：WAIT numreplicas(同步成功的从节点数目)  timeout(超时时间)

>  	**Redis的WAIT 命令用来阻塞当前客户端，直到所有先前的写入命令成功传输并且至少由指定数量的从节点复制完成**。 如果执行超过超时时间(以毫秒为单位)，则即使尚未完成指定数量的从结点复制，该命令也会返回。 WAIT 命令总是**返回在WAIT 命令之前发送的写入命令被复制到的从结点数量**

核心代码如下（**org.redisson.RedissonBaseLock#evalWriteAsync**方法）

```java
protected <T> RFuture<T> evalWriteAsync(String key, Codec codec, RedisCommand<T> evalCommandType, String script, List<Object> keys, Object... params) {
    MasterSlaveEntry entry = commandExecutor.getConnectionManager().getEntry(getRawName());
    // 执行当前命令的master节点的从节点数量
    int availableSlaves = entry.getAvailableSlaves();

    // 创建批量命令执行器（会添加WAIT命令，等待数据同步到从节点。但使用了批量命令执行器，就用不了script cache了）
    CommandBatchService executorService = createCommandBatchService(availableSlaves);
    // 这里并不会直接执行命令，而是将命令保存起来，等待后续添加其他命令（比如WAIT）后再一起执行
    RFuture<T> result = executorService.evalWriteAsync(key, codec, evalCommandType, script, keys, params);
    if (commandExecutor instanceof CommandBatchService) {
        return result;
    }
    // 真正的执行：根据配置再适当添加命令，然后一起执行（WAIT命令就会在这里面添加）
    RFuture<BatchResult<?>> future = executorService.executeAsync();

    // 设置一个等待这个future完成（正常结束或异常结束）的listener，用来判断redis实际同步的从节点数量是否是我们指定的从节点数量，并抛出异常（感觉这种实现方式不好，所以最新的版本已经让用户来选择是否抛异常了）
    CompletionStage<T> f = future.handle((res, ex) -> {
        if (ex == null && res.getSyncedSlaves() != availableSlaves) {
            throw new CompletionException(new IllegalStateException("Only "
                                                    + res.getSyncedSlaves() + " of " + availableSlaves + " slaves were synced"));
        }

        return result.getNow();
    });
    return new CompletableFutureWrapper<>(f);
}
```

> ​		redisson在执行这些加锁命令时，会构建一个批量命令执行器（CommandBatchService），然后内部再添加了一些优化命令（如WAIT命令）后再一起执行这些命令。
>
> ​		所以，redisson添加普通锁的时候默认在lua脚本执行后再执行了WAIT命令（前提是当前redis节点要有从节点），等待锁信息相关数据同步到从节点后再返回，这样锁信息主从同步时就基本不会丢失

## 6 redisson提供的工具类

### 6.1 RLocalCachedMap（分布式本地缓存工具）

​		**本地缓存和redis缓存同时存在的Map，牺牲客户端内存的方式，换取在频繁获取某些常用数据时消耗在网络上的时间。非常适合适合分布式多节点场景下读取频繁，更新不频繁的缓存数据**

 		主要是基于redis的pub/sub功能实现RLocalCachedMap里数据添加、更新、删除等写数据操作时对其他节点的同等写操作，主要实现类为**RedissonLocalCachedMap**，重点字段和方法分析

```java
/**
* 当前缓存实例的id，用来做过滤操作（比如是当前节点中的缓存实例更新的数据，那么当前实例就可以对pub/sub的更新消息不做任何操作）
*/
private byte[] instanceId;
/**
 * 本地缓存，提供了三方的caffeine缓存和redisson内部自己设计的缓存<p/>
 * 默认用redisson自己设计的缓存：{@link org.redisson.api.LocalCachedMapOptions.EvictionPolicy}
 */
private ConcurrentMap<CacheKey, CacheValue> cache;
private int invalidateEntryOnChange;
/**
 * 同步策略，默认 {@link SyncStrategy#INVALIDATE}
 */
private SyncStrategy syncStrategy;
/**
 * 存储模式<p/>
 * 默认{@link org.redisson.api.LocalCachedMapOptions.StoreMode#LOCALCACHE_REDIS}
 */
private LocalCachedMapOptions.StoreMode storeMode;
/**
 * 当map中key对应的value为null时，是否储存在本地 <p/>
 * 默认为false，代表value为null就不储存再本地缓存中
 */
private boolean storeCacheMiss;

/**
 * 基于redis的pub/sub实现的监听器，处理缓存的禁用，启用，删除和更新操作
 */
private LocalCacheListener listener;
/**
 * 本地缓存的视图工具，主要生成cacheKey和遍历本地缓存的
 */
private LocalCacheView<K, V> localCacheView;

// put方法
@Override
protected RFuture<V> putOperationAsync(K key, V value) {
    ByteBuf mapKey = encodeMapKey(key);
    CacheKey cacheKey = localCacheView.toCacheKey(mapKey);
    // 先放到本地缓存
    CacheValue prevValue = cachePut(cacheKey, key, value);
    broadcastLocalCacheStore(value, mapKey, cacheKey);

    if (storeMode == LocalCachedMapOptions.StoreMode.LOCALCACHE) {
        V val = null;
        if (prevValue != null) {
            val = (V) prevValue.getValue();
        }
        return RedissonPromise.newSucceededFuture(val);
    }

    ByteBuf mapValue = encodeMapValue(value);
    byte[] entryId = generateLogEntryId(cacheKey.getKeyHash());
    ByteBuf msg = createSyncMessage(mapKey, mapValue, cacheKey);
    // 更新redis端hash数据结构里的缓存数据，并publish消息
    return commandExecutor.evalWriteAsync(getRawName(), codec, RedisCommands.EVAL_MAP_VALUE,
                                          "local v = redis.call('hget', KEYS[1], ARGV[1]); "
                                          + "redis.call('hset', KEYS[1], ARGV[1], ARGV[2]); "
                                          + "if ARGV[4] == '1' then "
                                          + "redis.call('publish', KEYS[2], ARGV[3]); "
                                          + "end;"
                                          + "if ARGV[4] == '2' then "
                                          + "redis.call('zadd', KEYS[3], ARGV[5], ARGV[6]);"
                                          + "redis.call('publish', KEYS[2], ARGV[3]); "
                                          + "end;"
                                          + "return v; ",
                                          Arrays.<Object>asList(getRawName(), listener.getInvalidationTopicName(),listener.getUpdatesLogName()),
                                          mapKey, mapValue, msg, invalidateEntryOnChange, System.currentTimeMillis(), entryId);
}
/**
* 根据参数构建消息，并publish到对应的topic中，让订阅这个topic的客户端来处理缓存更新操作
*/
private void broadcastLocalCacheStore(V value, ByteBuf mapKey, CacheKey cacheKey) {
    if (storeMode != LocalCachedMapOptions.StoreMode.LOCALCACHE) {
        return;
    }

    if (invalidateEntryOnChange != 0) {
        Object msg;
        if (syncStrategy == SyncStrategy.UPDATE) { // 缓存更新消息
            ByteBuf mapValue = encodeMapValue(value);
            msg = new LocalCachedMapUpdate(instanceId, mapKey, mapValue);
        } else { // 缓存失效消息
            msg = new LocalCachedMapInvalidate(instanceId, cacheKey.getKeyHash());
        }
        // publish消息
        listener.getInvalidationTopic().publishAsync(msg);
    }
    mapKey.release();
}
// get方法
public RFuture<V> getAsync(Object key) {
    checkKey(key);

    // 先从本地缓存中拿
    CacheKey cacheKey = localCacheView.toCacheKey(key);
    CacheValue cacheValue = cache.get(cacheKey);
    if (cacheValue != null && (storeCacheMiss || cacheValue.getValue() != null)) {
        return RedissonPromise.newSucceededFuture((V) cacheValue.getValue());
    }

    if (storeMode == LocalCachedMapOptions.StoreMode.LOCALCACHE) { // 当前缓存模式仅为本地缓存
        if (hasNoLoader()) { // 直接返回空
            return RedissonPromise.newSucceededFuture(null);
        }

        CompletableFuture<V> future = loadValue((K) key, false);
        CompletableFuture<V> f = future.thenApply(value -> {
            if (storeCacheMiss || value != null) {
                cachePut(cacheKey, key, value);
            }
            return value;
        });
        return new CompletableFutureWrapper<>(f);
    }

    RPromise<V> result = new RedissonPromise<>();
    // 从redis中从redis中获取
    RFuture<V> future = super.getAsync((K) key);
    future.onComplete((value, e) -> {
        if (e != null) {
            result.tryFailure(e);
            return;
        }
        // 放入本地缓存中
        if (storeCacheMiss || value != null) {
            cachePut(cacheKey, key, value);
        }
        result.trySuccess(value);
    });
    return result;
}
```

### 6.2 redis基本数据类型映射

- RBucket -- string

- RMap -- hash

- RList  -- list

- RSet  -- set

- RScoredSortedSet  -- sorted set




​		使用RedissonClient获取这些API都提供了Codec参数，且Redisson提供了很多默认的Codec（TypedJsonJacksonCodec、MarshallingCodec(默认)、SerializationCodec、FstCodec等），方便且提供了多种序列化方式