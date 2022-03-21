# Seata的AT模式分析(1.5.0版本)

## 1 增强机制

SeataAutoConfiguration中的GlobalTransactionScanner，主要用来代理@GlobalTransactional注解和客户端RM和TM的注册

### 1.1 GlobalTransactionScanner的实例化

### 1.2 相关表

- **undo_log**：客户端的表，保存修改数据sql操作前后镜像值，主要用于全局事务回滚是构造回滚sql来回滚数据
- **global_session**：服务端表，每一条记录都代表一个全局事务
- **branch_session**：服务端表，每一条记录都代表一个从客户端注册的分支事务
- **lock_table**：服务端表，客户端在全局事务期间修改的数据，每一条记录了某个数据库每个表的具体某个行的主键值，用在多个全局事务期间解决写冲突。

## 2 重要术语

### 2.1 RM

​		Resource Manager，资源管理器，管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。

​		作用在seata客户端，代码实现中和DatsSource的代理绑定，负责和数据库相关的操作打交道，默认实现为DefaultResourceManager。在Connection的commit中，负责向server端注册当前分支事务；在Connection的rollback中，负责向server端报告当前分支事务的错误状态。同时也处理服务端发送过来的分支事务提交（删除undo_log）或回滚（生成补偿sql并执行）操作。

### 2.2 TM

​		Transaction Manager ，事务管理器，开始全局事务、提交或回滚全局事务。

​		作用在seata客户端，代码实现中和@GlobalTransactional的拦截打交道，负责开启、提交或回滚全局事务，默认实现为DefaultTransactionManager。

### 2.3 TC

​	Transaction Coordinator，事务协调者，维护全局和分支事务的状态，驱动全局事务提交或回滚。

​	作用在seata服务端，在服务端实分支事务的注册，全局事务的开启、回滚等操作

### 2.4 全局事务的发起者

​		**全局事务的起点，微服务调用链走到@GlobalTransactional时，线程上线文不存在xid（有可能是第一次发起，也有可能时事务传播策略导致新建的全局事务等）。类比spring提供的事务传播策略中的新建事务。只有全局事务的发起者才能开启一个全局事务，并根据是否产生回滚异常来决定是否全局事务的提交或全局事务的回滚**

### 2.5 全局事务的参与者

​		**处于微服务调用链中全局事务内部，参与到了其他上游服务发起的全局事务（这是线程上下文存在xid）。类比spring提供的事务传播策略中的加入了已存在的事务，所以，全局事务的参与者不能触发全局事务的回滚或提交。只能由RM来报告当前分支事务的状态（触发rollback后，分支事务状态变为PhaseOne_Failed）**

## 三、服务端初始化



## 四、客户端初始化



## 五、一个完整的全局事务流程（db模式）

### 5.1 @GlobalTransactional注解拦截 

拦截器：GlobalTransactionalInterceptor

> ​	这里有个参数：**client.tm.degradeCheck**（是否开启降级检查，默认为false）。
>
> ​	当启用该参数时，会开启一个定时任务，间隔时间受参数**client.tm.degradeCheckPeriod**控制（默认为2000毫秒），向服务端发送全局事务的开启和提交测试，以此来判断服务端的全局事务当前是否支持。
>
> ​	如果出现**client.tm.degradeCheckAllowTimes**次连续检查失败，则禁用当前全局事务支持，不走GlobalTransactionalInterceptor的代理逻辑。
>
> ​	而当降级后，又需要连续检查成功**client.tm.degradeCheckAllowTimes**次才会再次开启全局事务的支持

1. 通过上面的参数检查，判断当前请求是否支持全局事务，当支持全局事务时，优先走@GlobalTransactional逻辑，因为全局事务也包括了数据库锁，其次再走@GlobalLock逻辑。

2. 解析@GlobalTransactional注解，获取当前环境的xid（如果存在xid则代表当前全局事务是参与者，发起者为上游服务，创建DefaultGlobalTransaction为参与者角色。不存在则代表当前全局事务是发起者）

3. 传播策略的检查和支持，类似spring，锁重试设置，开启全局事务

### 5.2 全局事务开启

**io.seata.tm.api.GlobalTransaction#begin** 方法

1. 判断当前全局事务的角色，全局事务的发起者才需要真正的开启全局事务，而全局事务的参与者不需要开启全局事务，只需要等待分支事务提交时加入到当前xid的全局事务就行
2. 全局事务的发起者此时还未向服务端发起的，所以xid肯定为空，先校验一下
3. 向服务端发送全局事务开启的请求
4. 服务端收到这个全局事务的开启请求后，创建GlobalSession（根据ID生成器生成唯一的transactionId和xid），并将GlobalSession转换为GlobalTransactionDO在保存到数据库的global_table表中，至此，全局事务开启，信息保存成功，最后再将xid返回给客户端
5. 客户端收到服务端传来的xid，将其保存到当前线程的上下文中。并应该在后续的调用其他业务服务时传递该xid，串联起来这个全局事务

### 5.3 执行业务代码

​		任何一个SQL的执行操作，无非就是先获取DataSource，在通过DataSource获取Connection，通过Connection获取Statement，Statement准备sql和参数，再执行Statement并处理结果集，返回结果

​		所以，追根溯源，要支持分布式事务，就需要控制DataSource的一系列的行为，**SeataAutoDataSourceProxyCreator**就在这里发挥了作用

#### 1、数据源的自动代理，SeataAutoDataSourceProxyCreator类，wrapIfNecessary方法

逻辑：只对DataSource代理，且会进行重复代理的判断，构造切面：SeataAutoDataSourceProxyAdvice。最终将原DataSource和SeataDataSourceProxy的关系放入DataSourceProxyHolder类中，方便后续的取出

#### 2、数据源方法的切入 

**SeataAutoDataSourceProxyAdvice**代理了DataSource类的任何操作，其代理逻辑：

		1. 先判断是否需要走切面逻辑（全局锁需要，且存在AT模式的全局事务），不需要走切面逻辑的直接走原逻辑，通过原DataSource完成一系列操作。
  		2. 需要走代理逻辑的流程：通过当前**DataSource**拿到对应的**SeataDataSourceProxy**（**DataSourceProxyHolder**发挥作用），走对应代理类的相同方法，获取代理的**Connection（ConnectionProxy）**，创建当前Connection的**ConnectionContext**，用来保存当前Connection的全局事务信息。创建**StatementProxy**， 拦截Statement的execute方法，根据druid或其他工具解析sql。最终会走到**io.seata.rm.datasource.exec.AbstractDMLBaseExecutor#doExecute**这个方法执行

#### 3、Statement的执行

1. select语句因为不会涉及到数据修改，所以直接走原Statement直接执行。

2. 而其他会修改数据的sql（比如 insert，update，delete，select_for_update等），走代理。具体逻辑：在执行原SQL之前，通过原sql构造出select语句，来查询更新字段的执行前镜像值，封装成TableRecords。之后再执行原SQL，执行后再查询更新字段的执行后镜像值，封装成TableRecords，最后再将两次的TableRecords封装成SQLUndoLog，暂放内存中，等待分支事务commit再写入库

**io.seata.rm.datasource.exec.AbstractDMLBaseExecutor#doExecute会对当前Connection是否自动提交做判断，为什么呢？**

> ​	假如在一个分布式事务中，我们只使用了seata的@GlobalTransactional注解而没有用spring的@Transactional注解控制本地事务，其实也是支持的。
>
> ​	当不存在本地事务时，事务都是走的autoCommit的。seata在这里先判断了一下，如果没开启本地事务，单个语句就为一个事务，seata其实还是设置当前事务为手动提交，设置autoCommit为false（因为还有undo_log的写入），然后走seata流程的分支事务提交。所以，当不存在本地事务，任何一个语句就是一个分支事务。
>
> ​	在一个服务中，开启了本地事务的地方算一个分支事务，有相同的xid，而未开启本地事务的代码里，每一个update,delete,insert等语句都算一个分支事务，由seata来控制全局事务的一致性。所以，未开启本地事务但只用seata的全局事务也是可以的，但是性能影响会比较大，因为这里由一个分支事务变成多个分支事务了，每一个分支事务都会涉及到与服务端的通信，数据库锁检查，undo_log的写库等操作。

#### 4、分支事务的提交（rpc通过Netty发送commit到服务端）

1. 锁检查（lock_table表），将变化的数据构造为row_key，一个row_key能唯一确定某一行数据

2. 已经被其他全局事务占用了数据锁，则所失败，抛出LockConflictException异常并开始重试，默认重试30次，重试间隔为10ms

3. 数据未被其他全局事务上锁，则当前全局事务对数据上锁（写入到lock_table表），创建BranchSession，关联xid，再转换为BranchTransactionDO并保存到branch_table表，在返回给客户端branchId。

4. 刷新当前分支事务内存中所有的undo_log到数据库的undo_log表，分支事务commit，清除当前Connection的ConnectionContext里全局事务相关信息。到这里，分支事务就已经commit成功了，且进行了写隔离

#### 5、调用其他业务服务的接口

1. 调用其他服务，需要将xid在服务间传递（当前版本好像还没支持feign的，自己实现也简单，请求头加个TX_XID，把xid传过去就行），其他服务设置了spring的拦截器，获取TX_XID，并绑定到当前线程，等待后续判断

2. 其他业务服务的流程和上面一样：解析sql，构造镜像，保存undo_log，锁检查，注册分支事务，commit



### 5.4 业务代码执行完毕，全局事务的回滚或提交流程

#### 1、全局事务回滚

1. 业务代码抛出异常，且命中回滚异常，触发全局回滚（io.seata.tm.api.DefaultGlobalTransaction#rollback），但这里**只支持全局事务的发起者调用回滚，全局事务的参与者是不能全局事务回滚的**。所以，只有全局事务的发起者出现了异常并命中回滚策略才会触发全局回滚，全局事务的参与者则不能，所以可能导致参与者回滚了，但不抛异常，发起者没有命中回滚，导致发起者全局事务正常commit了，但部分参与者却回滚了，导致没有全部保证一致性。

2. 发送全局事务回滚请求到服务端，回滚逻辑

> 1. GlobalSession的状态校验
> 2. 获取这个GlobalSession的所有BranchSession，遍历触发每一个分支事务的回滚
> 3. 分支事务回滚，发送请求给客户端，找到当前分支事务的undo_log，对镜像前后进行比较，不同的需要补偿（update就用update，insert用delete，delete用insert等），还原事务前的数据
> 4. 分支事务的undo_log删除，返回状态给服务端
> 5. 服务端对状态的判断和处理，删除分支事务（branch_session）

#### 2、全局事务提交

1. 类似全局事务回滚，也只有全局事务的发起者才能真正的提交

2.  发送全局事务提交请求到服务端，提交逻辑

> 1. 释放相关的锁资源：遍历当前全局事务的所有分支事务，逐个将每个分支事务关联的锁资源删除（lock_table表相关的记录）
> 2. 判断是否支持异步提交（AT模式是支持的，因为现在锁资源已经释放了，不用在担心会影响到其他全局事务，剩下的只需要把相关的undo_log删除即可），将globalSession状态更新为异步提交中，并更新到数据库中（global_table表）去，在直接返回给客户端已提交
> 3. 异步提交：由定时任务线程池触发（Server启动时创建的DefaultCoordinator初始化异步提交定时任务下线程池，默认定时任务时间间隔为1秒），查询所有的异步提交状态的global_session，并将其关联的branch_session全部查出来，遍历每个global_session并开始全局事务的提交
> 4. 全局事务的真正提交：遍历global_session中关联的每一个branch_session，发送提交任务到对应的客户端（根据resourceId和clientId查找对应客户端的Channel），客户端收到全局事务的commit请求，将请求封装成Phase2Context对象并扔进待提交队列，等待客户端的定时任务线程池提交，然后直接返回给服务端分支事务已提交的状态
> 5. 客户端定时任务线程池提交全局事务逻辑：将所有的Phase2Context按资源分组（在seata的AT模式下，一个资源就可以代表一个数据库），具有相同资源的待提交任务处于同一个数据库里，可以用相同的Connection。通过资源管理器获取获取数据源代理，再通过这个数据源代理拿到原Connection（以免这些更新语句又走了代理，创建了undo_log），在批量删除对应的undo_log。
> 6. 当所有分支事务提交完成后（undo_log删除完成），最后再server端删除对应的GlobalSession记录

### 5.5 全局事务结束

1. 如果当前全局事务是嵌套的，需要恢复上一个全局事务的状态（包括全局锁配置：锁失败的重试次数和时间间隔，恢复上一个全局事务的xid）

2. 触发回调函数，和清除当前全局事务的钩子函数

至此，全局事务的流程已全部结束

## 六、全局事务锁冲突和重试的实现

### 6.1 写隔离和锁重试的实现

​		seata在客户端执行更新数据相关的sql时，会记录sql操作前后的镜像值到undo_log中，同时据此准备lockKeys暂存起来，lockKeys就是变化的数据唯一标识（比如表明，主键列的值等）。在客户端的本地事务提交时，先注册当前分支事务（**利用RM的branchRegister接口**）。seata服务端的TC开始尝试注册分支事务，尝试步骤：

- 先对全局事务进行状态检查

- 利用客户端传来的信息构造BranchSession

- **对BranchSession尝试加锁（AT模式才会）**

  - **通过对锁信息的一系列检查和转化，到达LockStoreDataBaseDAO#acquireLock(java.util.List<io.seata.core.store.LockDO>)接口**

  - 查询lock_table中对应row_key存在的数据（这个row_key就是行锁，能唯一标识客户端某个数据库里某个表的某行数据）

    - 如果没查询出来，代表写锁不冲突，将这些锁资源保存在lock_table中，直接返回加锁成功

    - 如果查询出来了，且这些数据还是当前全局事务（xid一致），代表重复枷锁而已，只需要将原本不存在的所数据保存到lock_table中，就可返回枷锁成功

    - 查询出来，且有其他全局事务占用当前资源，直接返回加锁失败。由io.seata.server.transaction.at.ATCore#branchSessionLock直接抛出异常码为LockKeyConflict的异常，代表写锁冲突

- 加锁成功就不说了，直接向下走。**而加锁失败，由服务端的AbstractCallback捕获异常，并转化为对应消息，发送给客户端**

- **客户端的ConnectionProxy#recognizeLockKeyConflictException方法检测到是锁冲突异常，又抛出LockConflictException异常**

- **客户端的LockRetryPolicy#doRetryOnLockConflict捕获到LockConflictException异常，开始重试（默认重试30次，间隔时间为10毫秒）**

- **如果30次还是重试失败，转为LockWaitTimeoutException异常，触发客户端本地事务的回滚**

## 7 问题

### 7.1 必须保证全局事务范围大于等于分支事务（spring提供的事务注解等）

​		假如全局事务的范围小于分支事务：全局事务的发起者先是进行全局事务的提交，再进行本地事务的提交。在全局事务提交时，本地事务都还没提交（本地事务提交时才会注册当前分支事务到server端），所以在server端不会有任何当前全局事务发起者客户端的分支事务，在server端触发分支事务提交时也不会发送给当前客户端，所以，对于当前客户端来说全局事务的提交就是个空提交（但对其他全局事务的参与者不是）。而全局事务在server端的提交又是个异步的过程，所以当客户端提交本地事务时，可能会出现以下错误

- 全局事务提交完毕**：server的全局事务操作完毕后会删掉GlobalSessiob，所以，当客户端的本地事务提交，向server端注册分支事务时，会导致xid不存在异常（这个xid已经随着全局事务的终结给删掉了）**
- 全局事务正在提交：**虽然在server端全局事务是异步提交的，但在这之前全局事务的状态会先设为AsyncCommitting（这一步和客户端提交全局事务是同步的）。所以，在客户端向server注册分支事务时，server端会先校验全局事务xid的状态，发现全局事务状态不为Begin，抛出全局事务异常**

​		为什么范围可以等于，因为全局事务拦截器的**io.seata.spring.annotation.GlobalTransactionalInterceptor#getPosition接口默认将@GlobalTransactional拦截放在了@Transactional的前面，拦截器也类似栈结构（先进后出）**。先进入@GlobalTransactional拦截方法，再进入@Transactional拦截方法，先从@Transactional拦截方法出，之后再从@GlobalTransactional方法出来。保证了在全局事务提交前，本地事务已经注册了。

**综上，@GlobalTransactional和spring的@Transactional的正确搭配：**

​	@GlobalTransactional范围要大于等于@Transactional，最好是两个作用域一样（即可保证事务正常进行，又可减少客户端的分支事务，减少与server端的通信等）

### 7.2 全局事务的参与者回滚异常一定让全局事务的发起者感知到

​		由于全局事务的参与者本地tollback时，只会通过RM向服务端报告当前分支是的状态为PhaseOne_Failed。如果参与者本地回滚后，全局事务的发起者不能感知到参与者的回滚，就commit了全局事务。在服务端的io.seata.server.coordinator.DefaultCore#doGlobalCommit方法中（全局事务发起者向server端发送全局事务提交），获取到全局事务的所有分支事务时，如果某个分支事务状态为PhaseOne_Failed，也仅仅是删除这个分支事务，不会触发全局回滚，全局事务从整体上来说还是提交，就造成了参发起者commit，参与者rollback，分布式事务就失败了。

​		所以，参与者的回滚一定要让发起者感知到，并让发起者触发全局事务的回滚，这样，全局事务才是原子性的操作。

### 7.3 seata做的一系列程序健壮性支持

#### 7.3.1 降级检查

​	降级检查是否开启由配置参数client.tm.degradeCheck管理，应用到GlobalTransactionalInterceptor#degradeCheck字段上，且支持热修改（比如consul上修改了这个参数，可以同步到客户端，实现不重启就可以开启或关闭降级检查），默认为false，表示不开启降级检查

​		当开启降级检查时，在GlobalTransactionalInterceptor的构造器里会读取配置参数degradeCheckPeriod和degradeCheckAllowTimes，分别表示降级检查一次的时间间隔和连续超过指定次数后，将关闭或开启全局事务代理模式。再提交一个定时任务，去执行降级检查（代码实现GlobalTransactionalInterceptor#startDegradeCheck里）

GlobalTransactionalInterceptor#startDegradeCheck逻辑：

​		按指定的degradeCheckPeriod毫秒时间间隔执行这个任务，里面开启一个空的全局事务，并马上提交（测试全局事务是否正常），根据是否通过来利用事件发布和通知机制（DegradeCheckEvent事件），通知对应得事件监听器处理对应得逻辑。监听代码在io.seata.spring.annotation.GlobalTransactionalInterceptor#onDegradeCheck里实现，连续失败degradeCheckAllowTimes次会开启降级，而在降级期间内，若连续成功degradeCheckAllowTimes则会取消降级。降级具体应用在io.seata.spring.annotation.GlobalTransactionalInterceptor#invoke，如果降级开启，则会禁用全局事务

#### 7.3.2 写锁冲突重试机制

​	参考6.1

#### 7.3.3 全局事务commit和rollback失败重试机制

​	全局事务提交和回滚的重试机制在io.seata.tm.api.DefaultGlobalTransaction里的commot和rollback里实现。受client.tm.commitRetryCount配置参数控制，默认重试5次。



  

