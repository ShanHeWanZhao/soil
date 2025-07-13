# Seata(1.7.1版本)

## 1 增强机制

SeataAutoConfiguration中的GlobalTransactionScanner，主要用来代理@GlobalTransactional注解和客户端RM和TM的注册，同时代理TCC的bean

### 1.1 GlobalTransactionScanner的实例化

#### 1.1.1 bean的自动代理

​		**GlobalTransactionScanner继承了AbstractAutoProxyCreator抽象类，具有了自动代理bean的功能**。作为一个BeanPostProcessor，将在postProcessAfterInitialization方法调用时判断该bean是否应该被代理（对于AT模式，就是当前bean的class或method必须有@GlobalTransactional或@GlobalLock注解），最终被GlobalTransactionalInterceptor拦截。

#### 1.1.2 初始化

​		**GlobalTransactionScanner也实现了InitializingBean接口，在afterPropertiesSet方法中，将会对TM和RM进行初始化**

##### 1.1.2.1 TM初始化

- 实例化TmNettyRemotingClient对象（会创建好netty客户端的Bootstrap相关配置，等待后续init使用）

- 开始**初始化TmNettyRemotingClient，先注册一些ResponseProcessor，用来处理server端回传的respons**e（因为时TM，所以只会注册和全局事务的begin，commit，rolback等相关response的Processor，具体类型如下）

  - ```txt
    MessageType.TYPE_SEATA_MERGE_RESULT
    MessageType.TYPE_GLOBAL_BEGIN_RESULT
    MessageType.TYPE_GLOBAL_COMMIT_RESULT
    MessageType.TYPE_GLOBAL_REPORT_RESULT
    MessageType.TYPE_GLOBAL_ROLLBACK_RESULT
    MessageType.TYPE_GLOBAL_STATUS_RESULT
    MessageType.TYPE_REG_CLT_RESULT
    ```

- 开启一些定时任务，包括与服务端Channel的reconnect任务、**MergedSend任务（处理批量消息发送）**、future的超时检测任务。

- 启动和TM相关的netty客户端的Bootstrap，添加对应的ChannelHandler，包括IdleStateHandler（心跳检测，会进行ping-pong操作）、解码和编码器、**ClientHandler（双向handler，开始真正处理数据，同时也处理IdleStateHandler发来的IdleStateEvent事件，判断后是否进行ping-pong，以此维护长连接）**

##### 1.1.2.2 RM初始化

- 实例化RmNettyRemotingClient对象，和上面类似，也会创建好netty客户端的Bootstrap相关配置

- 开始**初始化RmNettyRemotingClient，也会先注册一些ResponseProcessor，用来处理server端回传的response**（因为时RM，所以只会注册资源（可以理解成数据库）相关的操作，具体类型如下）

  - ```java
    MessageType.TYPE_BRANCH_COMMIT
    MessageType.TYPE_BRANCH_ROLLBACK
    MessageType.TYPE_RM_DELETE_UNDOLOG
    
    MessageType.TYPE_SEATA_MERGE_RESUL
    MessageType.TYPE_BRANCH_REGISTER_RESULT
    MessageType.TYPE_BRANCH_STATUS_REPORT_RESULT
    MessageType.TYPE_GLOBAL_LOCK_QUERY_RESULT
    MessageType.TYPE_REG_RM_RESULT
    ```

- 和上面一样，开启RM相关的netty bootstrap并注册相同的ChannelHandler

### 1.2 相关表

- **undo_log**：客户端的表，保存修改数据sql操作前后镜像值，主要用于全局事务回滚来构造回滚sql以回滚数据（
- **global_session**：服务端表，每一条记录都代表一个全局事务
- **branch_session**：服务端表，每一条记录都代表一个从客户端注册的分支事务
- **lock_table**：服务端表，客户端在全局事务期间修改的数据，每一条记录了某个数据库每个表的具体某个行的主键值，用在多个全局事务期间解决写冲突。
- **tcc_fence_log**：客户端表。用来解决TCC阶段可能出现的空回滚、悬挂和幂等判断等操作，维持TCC的健壮性（且只有**@TwoPhaseBusinessAction的useTCCFence为true才会使用**）

- AT使用的表：**undo_log**、**global_session**、**branch_session**、**lock_table**
- TCC使用的表：**tcc_fence_log**、**global_session**、**branch_session**



## 2 重要术语

### 2.1 RM（Resource Manager）

​		资源管理器，管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。

​		**作用在seata客户端，代码实现中和DatsSource的代理绑定，负责和数据库相关的操作打交道，默认实现为DefaultResourceManager（仅是一个适配器，针对不同的模式适配了不同的ResourceManager实现。比如AT模式的默认实现是DataSourceManager，TCC模式的实现是TCCResourceManager）**。在Connection的commit中，负责向server端注册当前分支事务；在Connection的rollback中，负责向server端报告当前分支事务的错误状态。同时也处理服务端发送过来的分支事务commit（AT为删除undo_log，TCC为触发confirm操作）或rollback（AT为生成补偿sql并执行，TCC为触发cancel操作）操作。

### 2.2 TM（Transaction Manager）

​		 事务管理器，开始全局事务、提交或回滚全局事务。

​		**作用在seata客户端，代码实现中和@GlobalTransactional的拦截打交道，，默认实现为DefaultTransactionManager。负责开启、commit或rollback全局事务（commit和rollback操作只是向server发起这个请求了，具体的还需要TC和RM来执行真正的数据处理）**。三个核心方法如下

- **begin**：向server发送开启全局事务的请求。**server端会创建一条global_session数据来表示这个全局事务**，并回传给TM对应的xid
- **commit**：向server端发起全局事务的commit。server端具体做法如下（**从第二个操作开始，就会异步处理了，因为这之后的操作基本就是删除些用来支持全局事务的数据，不会再对分支事务修改的数据有影响了**）。**所以，commit操作是很快的，TM只需等待TC删除掉锁信息即可**
  - 修改global_session状态，**释放锁资源（即删除lock_table对应的记录）**
  - 拿到这个global_session关联的branch_session，依次**触发branch_session的commit（即发送请求到RM，RM会删除掉branch_session对应的undo_log数据）**
  - **server端删除对应的branch_session记录**
  - 全部branch_session操作完成后，TC开始修改global_session状态为完毕然后**删除这个global_session**
  - 返回对应的GlobalStatus状态给TM。至此，全局事务commit完毕
- **rollback**：向server端发起全局事务的rollback。server端具体做法如下
  - 设置对应的global_session状态为关闭，避免新分支的注册
  - 拿到这个global_session关联的branch_session，依次**触发branch_session的rollback（即发送请求到RM，RM会拿出这个branch_session对应的undo_log数据，并进行undo操作，复原数据）**
  - **server端删除对应的branch_session记录**
  - **全部branch_session操作rollback成功后，TC再释放锁资源（即删除lock_table对应的记录），并删除对应的global_session数据**
  - 返回对应的GlobalStatus状态给TM。至此，全局事务rollback完毕

### 2.3 TC（Transaction Coordinator）

​	事务协调者，作用在seata服务端，维护全局和分支事务的状态，驱动全局事务提交或回滚，**默认实现为DefaultCoordinator**。

​	与RM打交道，实现分支事务的注册等，并在TM发起全局事务的commit和rollback后再驱动分支事务的commit和rollback。

​	和TM打交道，实现全局事务的开启、commit和rollback等操作

### 2.4 全局事务的发起者

​		**全局事务的起点，微服务调用链走到@GlobalTransactional时，线程上线文不存在xid（有可能是第一次发起，也有可能时事务传播策略导致新建的全局事务等）。类比spring提供的事务传播策略中的新建事务。只有全局事务的发起者才能开启一个全局事务，并根据是否产生回滚异常来决定是否全局事务的提交或全局事务的回滚**

### 2.5 全局事务的参与者

​		**处于微服务调用链中全局事务内部，参与到了其他上游服务发起的全局事务（这时线程上下文存在xid）。类比spring提供的事务传播策略中的加入了已存在的事务，所以，全局事务的参与者不能触发全局事务的回滚或提交。只能由RM来报告当前分支事务的状态（触发rollback后，分支事务状态变为PhaseOne_Failed）**

### 2.6 分支事务和全局事务

全局事务：整个分布式事务，由多个分支事务组成

分支事务：分布式事务中整个链路里每个服务单独的事务，不可能跨服务存在。运行在spring事务的支持下，一个本地事务就是一个分支事务。没有被本地事务支持但运行在全局事务下，则一个dml sql就会生成一个分支事务

## 3、服务端初始化

服务端就是一个springboot项目，启动类是**ServerApplication，核心启动方法是io.seata.server.Server#start**

```java
// io.seata.server.Server#start 方法里的一些主要功能（删除了些不重要的）
public static void start(String[] args) {

    // 统计相关功能初始化
    MetricsManager.get().init();
    // 设置数据保存模式（db，file，或redis）
    System.setProperty(ConfigurationKeys.STORE_MODE, parameterParser.getStoreMode());

    ThreadPoolExecutor workingThreads = new ThreadPoolExecutor(NettyServerConfig.getMinServerPoolSize(),
            NettyServerConfig.getMaxServerPoolSize(), NettyServerConfig.getKeepAliveTime(), TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(NettyServerConfig.getMaxTaskQueueSize()),
            new NamedThreadFactory("ServerHandlerThread", NettyServerConfig.getMaxServerPoolSize()), new ThreadPoolExecutor.CallerRunsPolicy());

    // 创建netty相关对象
    NettyRemotingServer nettyRemotingServer = new NettyRemotingServer(workingThreads);
    nettyRemotingServer.setListenPort(parameterParser.getPort());
    // 创建TC
    DefaultCoordinator coordinator = new DefaultCoordinator(nettyRemotingServer);
    // 启动一些定时任务，包括 异步全局事务commit操作等
    coordinator.init();
    nettyRemotingServer.setHandler(coordinator);
    //127.0.0.1 and 0.0.0.0 are not valid here.
    // 设置服务端netty的ip和端口
    if (NetUtil.isValidIp(parameterParser.getHost(), false)) {
        XID.setIpAddress(parameterParser.getHost());
    } else {
        XID.setIpAddress(NetUtil.getLocalIp());
    }
    XID.setPort(nettyRemotingServer.getListenPort());

    // 注册消息处理器并构建netty的ServerBootstrap，开启端口的监听
    nettyRemotingServer.init();
}
```

## 3、客户端初始化

​		客户端初始化主要是通过两个自动代理bean来实现的

- **GlobalTransactionScanner**：代理**@GlobalTransactional和@GlobalLock注解标志的类或方法，统一使用GlobalTransactionalInterceptor进行拦截**
- **SeataAutoDataSourceProxyCreator**：代理DataSource类。**原来执行sql的流程 DataSource -> Connection -> Statement -> 开始执行sql，变为了DataSourceProxy -> ConnectionProxy -> StatementProxy**（**DataSourceProxy里代理commit相关操作，和server通信。StatementProxy则代理执行sql的逻辑，处理执行前后镜像数据并生成undo数据，以便后续的rollback操作**）

## 5、一个完整的全局事务流程（db模式）

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

1. 普通的select语句因为不会涉及到数据修改，就不会申请锁资源。所以直接走原Statement直接执行。

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

## 6、全局事务锁冲突和重试的实现

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

## 7 思考

### 7.1 必须保证全局事务范围大于等于分支事务（spring提供的事务注解等）

​		假如全局事务的范围小于分支事务：全局事务的发起者先是进行全局事务的提交，再进行本地事务的提交。在全局事务提交时，本地事务都还没提交（本地事务提交时才会注册当前分支事务到server端），所以在server端不会有任何当前全局事务发起者客户端的分支事务，在server端触发分支事务提交时也不会发送给当前客户端，所以，对于当前客户端来说全局事务的提交就是个空提交（但对其他全局事务的参与者不是）。而全局事务在server端的提交又是个异步的过程，所以当客户端提交本地事务时，可能会出现以下错误

- 全局事务提交完毕**：server的全局事务操作完毕后会删掉GlobalSessiob，所以，当客户端的本地事务提交，向server端注册分支事务时，会导致xid不存在异常（这个xid已经随着全局事务的终结给删掉了）**
- 全局事务正在提交：**虽然在server端全局事务是异步提交的，但在这之前全局事务的状态会先设为AsyncCommitting（这一步和客户端提交全局事务是同步的）。所以，在客户端向server注册分支事务时，server端会先校验全局事务xid的状态，发现全局事务状态不为Begin，抛出全局事务异常**

​		为什么范围可以等于，因为全局事务拦截器的**io.seata.spring.annotation.GlobalTransactionalInterceptor#getPosition接口默认将@GlobalTransactional拦截放在了@Transactional的前面，拦截器也类似栈结构（先进后出）**。先进入@GlobalTransactional拦截方法，再进入@Transactional拦截方法，先从@Transactional拦截方法出，之后再从@GlobalTransactional方法出来。保证了在全局事务提交前，本地事务已经注册了。

**综上，@GlobalTransactional和spring的@Transactional的正确搭配：**

​	@GlobalTransactional范围要大于等于@Transactional，最好是两个作用域一样（即可保证事务正常进行，又可减少客户端的分支事务，减少与server端的通信等）

### 7.2 全局事务的参与者回滚异常一定让全局事务的发起者感知到

​		由于全局事务的参与者本地rollback时，只会通过RM向服务端报告当前分支是的状态为PhaseOne_Failed。如果参与者本地回滚后，全局事务的发起者不能感知到参与者的回滚，而触发了全局事务发起者的commit，这时在服务端的io.seata.server.coordinator.DefaultCore#doGlobalCommit方法中（全局事务发起者向server端发送全局事务提交），TC获取到全局事务的所有分支事务时，如果某个分支事务状态为PhaseOne_Failed（即这个分支事务本地rollback了），TC也仅仅是删除这个分支事务，不会触发全局回滚，全局事务从整体上来说还是提交，就造成了参发起者commit，这个参与者rollback，分布式事务逻辑上就失败了。

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

### 7.4 seata的分布式事务隔离级别

​		传统数据库如oracle默认读已提交，mysql则通过mvcc和undo log实现了可重复读隔离级别，而**seata实现的分布式事务也可工作在读已提交的隔离级别下**

如何实现的：

> ​		seata的分支事务进行本地commit操作时，会将改动的的数据构造成lockKeys，在分支事务注册到server时带上这些lockKeys，server端会对这些lockKey进行解析构建，并对其进行上锁，上锁成功的标志是成功将全部lockKeys保存到server端的lock_table表（可上锁的前提是lock_table表没有对应的lockKey数据）。在之后的分支事务的数据修改都会先对这些数据在server端上锁判断，以此实现类似数据库的互斥锁。
>
> ​		但是，当分支事务A本地commit后，但此时全局事务还未commit。如果有另一个事务B读取到了事务A修改的数据结果，就会产生脏读（因为这时全局事务还未commit，甚至可能触发rollback操作），解决脏读的办法就是事务B读取使用select for update语句，并且被@GlobalTransactional或@GlobalLock注解拦截。拦截后seata内部会对select for update的操作进行锁判断，生成一个SelectForUpdateExecutor执行器（核心执行方法doExecute代码如下）。所以直到分支事务A对应的全局事务结束释放了锁资源后，事务B才会读到数据并返回，以此实现读已提交

注意**，@GlobalTransactional + select for update**和**@GlobalLock + select for update都可实现select操作的读已提交隔离级别**，**但GlobalLock 更轻量级，它不会注册分支事务和加锁等操作，只会进行锁检查。所以，如果上述事务B不需要运行在全局事务或本地事务的模式先下而又想实现全局事务的读已提交隔离级别，就可使用@GlobalLock + select for update组合**

```java
// SelectForUpdateExecutor核心执行方法
public T doExecute(Object... args) throws Throwable {
    Connection conn = statementProxy.getConnection();
    DatabaseMetaData dbmd = conn.getMetaData();
    T rs;
    Savepoint sp = null;
    boolean originalAutoCommit = conn.getAutoCommit();
    try {
        // 在这里单独开启事务。如果没有原事务则直接开启，如果有则新建一个savepoint来开启事务
        if (originalAutoCommit) { // 没有原事务
            conn.setAutoCommit(false);
        } else if (dbmd.supportsSavepoints()) { // 有原事务，新建一个savepoint来开启内嵌事务
            sp = conn.setSavepoint();
        } else {
            throw new SQLException("not support savepoint. please check your db version");
        }

        LockRetryController lockRetryController = new LockRetryController();
        ArrayList<List<Object>> paramAppenderList = new ArrayList<>();
        String selectPKSQL = buildSelectSQL(paramAppenderList);
        while (true) {
            try {
                // #870
                // execute return
                // executeQuery return ResultSet
                // 执行sql语句，会获取本地锁
                rs = statementCallback.execute(statementProxy.getTargetStatement(), args);

                // Try to get global lock of those rows selected
                TableRecords selectPKRows = buildTableRecords(getTableMeta(), selectPKSQL, paramAppenderList);
                // 构建lock key
                String lockKeys = buildLockKey(selectPKRows);
                if (StringUtils.isNullOrEmpty(lockKeys)) { // lock key不存在，代表没有锁冲突，直接就break了
                    break;
                }

                if (RootContext.inGlobalTransaction() || RootContext.requireGlobalLock()) { 
                    // 全局锁的获取和判断必须在@GlobalTransactional or @GlobalLock注解下
                    // 只进行锁检查，不加锁（检查失败内部会抛异常）
                    statementProxy.getConnectionProxy().checkLock(lockKeys);
                } else {
                    throw new RuntimeException("Unknown situation!");
                }
                // 走到这就代表全局锁没有冲突（要查询的数据没有被多个事务同时处理），可直接返回了
                break;
            } catch (LockConflictException lce) {
                // 全局锁冲突，本地事务先rollback，释放select .. for update获取到的锁
                if (sp != null) {
                    conn.rollback(sp);
                } else {
                    conn.rollback();
                }
                // sleep后会再进行重试
                // trigger retry
                lockRetryController.sleep(lce);
            }
        }
    } finally {
        // 复原现场
        if (sp != null) {
            try {
                if (!JdbcConstants.ORACLE.equalsIgnoreCase(getDbType())) {
                    conn.releaseSavepoint(sp);
                }
            } catch (SQLException e) {
                LOGGER.error("{} release save point error.", getDbType(), e);
            }
        }
        if (originalAutoCommit) {
            conn.setAutoCommit(true);
        }
    }
    return rs;
}
```

# 8 TCC模式

​		可以简单理解**AT模式为一种特殊的TCC模式。AT和TCC共用TM和TC，但AT模式的RM为DataSourceManager，这部分完全由seata实现，以帮助我们资源的commit和rollback。TCC模式的RM为TCCResourceManager，需要我们自己实现一部分代码**。

​		TCC模式也有分支事务和全局事务，且同样基于代理实现。**TCC的拦截为TccActionInterceptor，用来在try阶段注册当前分支事务**。全局事务的操作和AT一致（begin,commit,rollback），都依赖@GlobalTransactional注解的代理实现。

​		TCC全称为**try-confirm-cancel**。和AT不同的是业务入侵大，需要我们写预留资源和回滚相关的代码。将TCC和AT做类比看，TCC的预留资源即AT中undo_log的解析和保存（但TCC中的预留资源我们不需要这么复杂，往往只用增加一个字段来记录被操作数据的修改空间即可），回滚即AT中undo_log的回滚。

- try：**@TwoParseBusinessAction注解**的方法，用来运行业务代码，并且**预留业务资源**
- confirm：commit操作，并**释放我们try阶段预留的业务资源**
- cancel：rollback操作，将**try阶段预留的资源进行回滚操作**

### 8.1 TCC可能出现的问题和Seata是如何解决的？

依赖tcc_fence_log表，实现在TCCFenceHandler类里

```sql
CREATE TABLE IF NOT EXISTS `tcc_fence_log`
(
    `xid`           VARCHAR(128)  NOT NULL COMMENT 'global id',    
    `branch_id`     BIGINT        NOT NULL COMMENT 'branch id',    
    `action_name`   VARCHAR(64)   NOT NULL COMMENT 'action name',    
    `status`        TINYINT       NOT NULL COMMENT 'status(tried:1;committed:2;rollbacked:3;suspended:4)',    
    `gmt_create`    DATETIME(3)   NOT NULL COMMENT 'create time',    
    `gmt_modified`  DATETIME(3)   NOT NULL COMMENT 'update time',    
    PRIMARY KEY (`xid`, `branch_id`),    
    KEY `idx_gmt_modified` (`gmt_modified`),    
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4;
```

#### 8.1.1 幂等保证

**出现场景**：TC端向分支事务发起commit操作，且分支事务commit成功。但因网络相关原因TC端没有接收到分支事务的commit响应，导致重试

**如何解决**：分支事务commit时判断tcc_fence_log#status的状态，如果为committed则已经提交了，直接返回ture即可，不再执行confirm操作

```java
// TCCFenceHandler的commitFence方法，这里只保留了幂等校验
public static boolean commitFence(Method commitMethod, Object targetTCCBean,
                                  String xid, Long branchId, Object[] args) {
    // 在一个事务里进行幂等相关操作校验和业务的commit方法执行
    return transactionTemplate.execute(status -> {
        try {
            Connection conn = DataSourceUtils.getConnection(dataSource);
            TCCFenceDO tccFenceDO = TCC_FENCE_DAO.queryTCCFenceDO(conn, xid, branchId);
            // 幂等校验：造成的原因可能时TC因网络没有收到分支事务commit的响应结果，导致TC进行重试，而分支事务实际已经commit了
            if (TCCFenceConstant.STATUS_COMMITTED == tccFenceDO.getStatus()) { 
                // tcc_fence_log状态为已提交，说明已操作了全局commit，直接返回true，就不用执行业务commit代码，用来保证幂等
                LOGGER.info("Branch transaction has already committed before. idempotency rejected. xid: {}, branchId: {}, status: {}", xid, branchId, tccFenceDO.getStatus());
                return true;
            }
            // tcc_fence_log状态更新为committed，在执行业务commit方法
            return updateStatusAndInvokeTargetMethod(conn, commitMethod, targetTCCBean, xid, branchId, TCCFenceConstant.STATUS_COMMITTED, status, args);
        } catch (Throwable t) {
            status.setRollbackOnly();
            throw new SkipCallbackWrapperException(t);
        }
    });
}
```

#### 8.1.2 空回滚

**出现场景**：try阶段出现异常，全局事务操作分支事务进行回滚，分支事务执行cancel阶段

**如何解决**：分支事务rollback时，判断如果没有tcc_fence_log记录，则不再执行cancel阶段

````java
// TCCFenceHandler的rollbackFence方法，这里只保留空回滚处理相关
public static boolean rollbackFence(Method rollbackMethod, Object targetTCCBean,
                                        String xid, Long branchId, Object[] args, String actionName) {
        // 在一个事务里进行幂等相关操作校验和业务的rollback方法执行
        return transactionTemplate.execute(status -> {
            try {
                Connection conn = DataSourceUtils.getConnection(dataSource);
                TCCFenceDO tccFenceDO = TCC_FENCE_DAO.queryTCCFenceDO(conn, xid, branchId);
                // non_rollback
                // 出现空回滚：可能是TCC中的try阶段执行出现了异常，就没有保存tcc_fence_log记录。此时全局事务需要操作rollback
                if (tccFenceDO == null) {
                    // 避免悬挂问题
                    // 还是要插入一条状态为SUSPENDED的tcc_fence_log记录，这样就算TCC中的cancel先于try执行，也不用担心try会被触发
                    boolean result = insertTCCFenceLog(conn, xid, branchId, actionName, TCCFenceConstant.STATUS_SUSPENDED);
                    LOGGER.info("Insert tcc fence record result: {}. xid: {}, branchId: {}", result, xid, branchId);
                    if (!result) {
                        throw new TCCFenceException(String.format("Insert tcc fence record error, rollback fence method failed. xid= %s, branchId= %s", xid, branchId),
                                FrameworkErrorCode.InsertRecordError);
                    }
                    // 返回rollback成功，不执行cancel操作
                    return true;
                } 
                }
                return updateStatusAndInvokeTargetMethod(conn, rollbackMethod, targetTCCBean, xid, branchId, TCCFenceConstant.STATUS_ROLLBACKED, status, args);
            } catch (Throwable t) {
                status.setRollbackOnly();
                throw new SkipCallbackWrapperException(t);
            }
        });
    }
````

#### 8.1.3 悬挂

**出现场景**：RM执行try太慢，导致全局事务都发起rollback操作了，但之后又执行到了try阶段

**如何解决**：try阶段就插入一条tcc_fence_log唯一记录，如果记录存在，则不再执行try代码

代码在上面的空回滚中，tcc_fence_log为null，则插入了一条状态为TCCFenceConstant.STATUS_SUSPENDED的tcc_fence_log记录，表示要终止当前分支事务

  

