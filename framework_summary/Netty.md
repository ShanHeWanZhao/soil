# Netty(4.1.34.Final)

## 1 Channel

通道连接的顶层抽象，定义了SocketChannel和服务端的ServerSocketChannel的通用接口

### 1.1 SocketChannel

当客户端和服务端建立连接后产生的Channel，用来在客户端和服务端之间传送信息

实现类：NioSocketChannel，利用java的NIO实现

### 1.2 ServerSocketChannel

服务端启动时产生的channel，用来绑定到本地端口并对外提供连接服务。**唯一的作用就是当连接产生时，创建一个SocketChannel保存在服务端，用来接受客户端的信息并对其发送信息。**

实现类：NioServerSocketChannel，利用java的NIO实现

## 2 ChannelHandler

​		定义了Channel被事件触发后的顶层处理接口，不论是客户端Channel还是服务端Channel，都会有对应的ChannelPipeline，所以这个顶层接口只定义了最简单的当前Channel添加和移除ChannelPipeline和发生异常的方法，**且因为这里面的方法都是和当前handler相关的，所以当某个方法触发时，是不会向后传播的**

- **入站事件（Inbound）：当客户端或服务端接收对方发来的数据触发的事件（即read操做，即从网卡在读到数据后触发的一系列操作）**
- **出战事件（Outbound）：当客户端和服务端开始发送数据时触发的事件（即write操作，终点就是从网卡把这个数据发送出去）**

### 2.1 ChannelInboundHandler

​		定义了入站事件的接口，其中大部分接口只会触发一次，比如注册和激活等接口，而read和userEventTriggered等接口能触发多次。

​		channelRead作为业务的主要实现接口，和channelReadComplete都是当从Channel中读取数据时的顶层抽象接口（**对于SocketChannel来说，分别是读取一次和读取完毕时触发。对于ServerSocketChannel来说，就是接收连接后，产生的客户端Channel并将其注册到EventLoop**），所以，这两个接口是能多次触发的。而又**因为netty在读取从客户端Channel传来的数据时，并不能确定数据的大小，所以netty会创建一个估值大小的ByteBuf进行重复读取，所以，一次客户端Channel发送的数据可能触发多次channelRead接口，当这次读取完毕后，再触发一次channelReadComplete接口**

- 客户端Channel的channelRead和channelReadComplete触发地点：NioByteUnsafe#read
- 服务端Channel的channelRead和channelReadComplete触发地点：NioMessageUnsafe#read

```java
// 当前Channel注册到Selector时触发（此时还未注册感兴趣的事件）
void channelRegistered(ChannelHandlerContext ctx) throws Exception;
// 当前Channel从Selector注销时触发
void channelUnregistered(ChannelHandlerContext ctx) throws Exception;
// 当前Channel激活时触发。
// 对于ServerSocketChannel，表示绑定了地址和端口。对于SocketChannel，代表已经连接了服务端
void channelActive(ChannelHandlerContext ctx) throws Exception;

void channelInactive(ChannelHandlerContext ctx) throws Exception;

/**
	客户端Channel：
 * 从Channel中读取了数据。在读取channel的数据时，可能由于我们分配的ByteBuf容量不够导致不能一次读取完毕，
 * 致使我们需要循坏创建ByteBuf来承接上一次的读取，然后就会触发多次的channelRead事件，当Channel数据读取完毕后，
 * 最终再触发一次channelReadComplete事件
 * 为什么这个msg是Object类型的？ 
 * 因为这可能是解码后的结果，也可能是SocketChannel对象（对于ServerBootstrapAcceptor来说）。
 * 但对于第一次触发channelRead时，这个msg一定是个ByteBuf。
 * 所以，当触发了这个方法后，msg一定不为空，且有可能已经是解码后的任意数据类型了
 
 	服务端Channel: 将产生的客户端Channel注册到EventLoop中并设置好参数和handler
 */
void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

/**
 * 当所有可读的字节都已经从 Channel 中读取之后，将会调用该回调方法；
 * 所以，可能在 channelReadComplete()被调用之前看到多次调用 channelRead() 
 * channelReadComplete和上面的channelRead方法触发都在AbstractNioByteChannel.NioByteUnsafe#read()里（SocketChannel）
 *
 */
void channelReadComplete(ChannelHandlerContext ctx) throws Exception;

/**
 * 用户定义的事件触发（比如检测心跳连接事件）
 */
void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;
void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception;
// 异常触发
@Override
@SuppressWarnings("deprecation")
void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
```

### 2.3 ChannelOutboundHandler

定义了出站事件，流经方向是从客户端到服务端。出站事件接口没有入站事件接口用的多。

```java
// Channel绑定到本地地址时调用（ServerSocketChannel触发一次）
void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception;
// 只会由SocketChannel触发一次，当SocketChannel连接上服务端是触发
// 触发地点：Bootstrap#doConnect
void connect(
        ChannelHandlerContext ctx, SocketAddress remoteAddress,
        SocketAddress localAddress, ChannelPromise promise) throws Exception;

void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;

void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;

void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;
// 当 当前Channel设置为autoRead时触发，用来监听当前Channel感兴趣的事件
// ServerSocketChannel是ACCEPT事件，SocketChannel是READ事件
void read(ChannelHandlerContext ctx) throws Exception;
// 向channel发送数据时触发（会触发多次，只会用在SocketChannel）
void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception;

void flush(ChannelHandlerContext ctx) throws Exception;
```

### 2.3 ByteToMessageDecoder

​		作为解码器的顶级抽象类，**主要作用是用来解决网络拆包中导致的一次或多次读取到的数据不完整（内置了一个merge cumulator类来组合多次碎片化的ByteBuf数据），但具体的数据是否完整应该交给子类去实现并判断（即子类实现decode接口）**。实现了ChannelInboundHandler接口，且重写了channelRead方法用来准备解码操作。

​		**不论是tcp的拆包，还是netty内部在将Channel中的数据封装到ByteBuf中时，都可能导致channelRead方法的第一次ByteBuf参数不是一次完整的数据。所以，这时解码器的另一个作用就出现了，将不足一次完整的数据缓存起来，等待下次channelRead时将新的ByteBuf数据和上次的组合，依次反复操作，直到构成一次完整的数据。所以，解码器是一定不能在Channel中共享的**

​		**io.netty.handler.codec.ByteToMessageDecoder#decode是子类需要唯一实现的接口。子类自己判断当前ByteBuf是否够一次的数据，如果够了，则进行解码，并将结果保存在out中。如果不够，则不进行解码操作，等待下次的channelRead在重复如此操作**

​		**所以可以推断，解码器应该放在handler队列的首位来进行解码操作，只有解码成功的才会继续向后传播channelRead事件（此时参数可以不是ByteBuf了，可以是我们解码出的任意对象）**

#### 2.3.1 FixedLengthFrameDecoder

​		固定长度解码器，内部的字段frameLength表示了读取一次的固定字节数。

#### 2.3.2 LineBasedFrameDecoder

​		行分隔符解码器，找到行标志（\n或\r\n）。一次读取指挥读取一整行的数据

#### 2.3.3 DelimiterBasedFrameDecoder

​		自定义分隔符解码器，允许传入一个自定义字符的ByteBuf作为分隔符来读取数据

#### 2.3.4 LengthFieldBasedFrameDecoder

​		比上面三个都更加灵活的解码器，**基于某个字段长度的解码器**。重要参数如下

- maxFrameLength：最大帧长度。也就是可以接收的数据的最大长度。如果超过，此次数据会被丢弃。
- lengthFieldOffset：长度域偏移。就是说数据开始的几个字节可能不是表示数据长度，需要后移几个字节才是长度域。
- lengthFieldLength：长度域字节数。用几个字节来表示数据长度。
- lengthAdjustment：数据长度修正。因为长度域指定的长度可以是header + body的整个长度，也可以只是body的长度。如果表示header+body的整个长度，那么我们需要修正数据长度。
- initialBytesToStrip：跳过的字节数。如果你需要接收header+body的所有数据，此值就是0，如果你只想接收body数据，那么需要跳过header所占用的字节数。

## 3 ChannelPipeline

​		配合Channel抽象出来的管道，一个Channel对应一个ChannelPipeline（一对一）。ChannelPipeline是用来串联并触发ChannelHandler的数据结构（**责任链模式**），可以看做是ChannelHandler的一种链表结构，内部存储了固定的head（HeadContext）和tail（TailContext）（这两种都是ChannelHandler）。

​		ChannelPipeline实现了ChannelInboundInvoker和ChannelOutboundInvoker，任何一个入站或出站事件的触发都是由调用ChannelPipeline的fire相关接口开始的，会根据是入站事件还是出站事件交由内部的head或tail调用

​		ChannelPipeline具备各种将ChannelHandler构造成ChannelHandlerContext并链接到对应的节点的接口，操作完毕后再触发对应ChannelHandler的**handlerAdded**事件

### 3.1 HeadContext

​		同时实现了ChannelInboundHandler和ChannelOutboundHandler，是**处理入站事件的源头**，所有入站事件的触发都将从HeadContext发起，将事件依次向后传播，然后走过我们配置的所有ChannelInboundHandler，直到tail结束

​		同时**HeadContext也实现了ChannelOutboundHandler，也是出站事件的末尾。内部有个Unsafe对象，Unsafe对象封装了真正的IO操作，比如绑定本地端口，连接服务器，关闭操作等。所以，出站事件（比如bind到本地端口，connect到远程端口等）从tail出发，一直传到head，再由head的Unsafe对象进行真正的绑定和连接IO操作**

### 3.2 TailContext

​		实现了ChannelInboundHandler接口，作为入站事件的末尾来处理对应的入站事件（**基本都是不做什么，或者打个日志而已**）。例如，入站事件的异常（exceptionCaught接口）如果不做任何处理，继续向后传播的话，最终也只会记录日志，并不会抛出异常

​		**因为没有实现ChannelOutboundHandler接口，所以不具备处理出站事件的能力。虽然从它开始触发出站事件，但它却处理不了出站事件（只能不断的交给前面的HandlerContext，直到实现了ChannelOutboundHandler的handler才开始真正处理出站事件）**

​		**当通过ChannelPipeline调用出站事件时，会交给tail开始触发出站事件。tail向前不断传播出站事件，事件从后向前流经我们提供的各个ChannelOutboundHandler，并过滤出Outbound类型的handler来处理出站事件，最后走到head，再由head进行正正的IO操作（绑定本地端口、连接远程端口等等出站事件）**

### 3.3  ChannelHandlerContext

​		ChannelHandlerContext是ChannelHandler的容器，且包含了前一个和后一个ChannelHandlerContext的引用，可以看做时链表数据结构里的那个Node。默认实现类是DefaultChannelHandlerContext

主要字段：

```java
private final ChannelHandler handler;
// 上一个HandlerContext节点
volatile AbstractChannelHandlerContext next;
// 下一个HandlerContext节点
volatile AbstractChannelHandlerContext prev;
// 此时handler已经添加到pipeline中了，但handlerAdded事件还未调用
private static final int ADD_PENDING = 1;
// 当前handler已经添加到Pipeline中了且对应的Channel已经注册（handlerAdded事件已经调用）
private static final int ADD_COMPLETE = 2;
// 当前handler已从pipeline中移除
private static final int REMOVE_COMPLETE = 3;
// 初始化状态
private static final int INIT = 0;
// 当前的handler是入站事件
private final boolean inbound;
// 当前的handler是出站事件
private final boolean outbound;
private final DefaultChannelPipeline pipeline;
// 当前handler的名字
private final String name;
/**
     * 是否按顺序调用当前handler的事件 
     * 比如：handler首先是被加入到pipeline中，触发handlerAdded事件. 
     * 如果刚添加到pipeline中，还未触发handlerAdded事件时，当前channel就触发了其他事件（比如read），
     * 就会检查当前字段来确定使用调用channel触发事件。
     */
private final boolean ordered;
// 当前context的状态
private volatile int handlerState = INIT;
```

### 3.4 总结

​		**ChannelPipeline是一种责任链模式的实现，默认实现是DefaultChannelPipeline，跟随着对应的Channel实例化时而创建**。**每个ChannelHandlerContext都有能力终结当前事件的传播，如果想把当前事件记录传播给后面（入站事件）or前面（出站事件）的处理器时，就调用fire开头的对应方法**

- **入站事件：head -> 我们按正向顺序配置的ChannelInboundHandler -> tail（数据的最终处理方式，打日志或release）**
- **出站事件：tail -> 我们按反向顺序配置的ChannelOutboundHandler -> head（真正的IO操作）**

## 4 ByteBuf

​		盛装SocketChannel里传来的数据，扩展了jdk提供的ByteBuffer，读写都有各自的指针，不用在进行翻转操作。同时**提供了nioBuffers等接口，能够很方便的将当前ByteBuf转为jdk的ByteBuffer**。**把ByteBuf看作是一个字节数组实现的，readerIndex和writerIndex初始都是0，随着向ByteBuf中写入数据，writerIndex不断增加。随着不断从ByteBuf中读取数据，readerIndex不断增加，当readerIndex增加到writerIndex时，代表ByteBuf所有数据读取完毕，将不能在读取到新数据了。**

​		**所以，内部读写指针：0 <= readerIndex <= writerIndex**

- **slice**相关的方法返回的ByteBuf将会共享当前的ByteBuf。
- **copy**相关的方法则会返回一个新的ByteBuf
- **discardReadBytes**方法将会丢弃掉已读取得字节，减少空间的浪费

​		ByteBuf的实现分为直接内存和堆内存，同时这两个又可以按是否池化区分。使用池化的ByteBuf使用引用计数来释放使用完的内存，当被引用数减为0时，就将回收这部分数据，所以，大多数接口最终都是使用io.netty.util.ReferenceCountUtil#release方法来减少引用方便最后的释放数据。

## 5 EventLoopGroup

​		EventLoopGroup实现了ScheduledExecutorService接口，并提供了将Channel注册到EventLoop的接口。

​		**EventLoopGroup可以类比为线程池。作为管理EventLoop的组件，在NioEventLoopGroup中，利用构造函数实例化了[可通过系统参数io.netty.eventLoopThreads配置，否则为n个处理器 * 2]个EventLoop，EventLoopGroup的线程池接口和注册Channel接口等的实现都是依赖EventLoop去处理的，EventLoopGroup实际上只提供选择哪个EventLoop去执行注册或任务而已（默认是轮询）**

### 5. 1 EventLoop

​		EventLoop也实现了EventLoopGroup（用来真正处理EventLoopGroup委托的任务）和SingleThreadEventLoop。所以，EventLoop才是真正做事的那个，且默认时单线程（即一个EventLoop对应一个线程）

​		在NioEventLoopGroup的构造函数中实例化，实现类为NioEventLoop，传入的Executor为ThreadPerTaskExecutor。

​		在NioEventLoop中还保存NIO需要用的的选择器提供者SelectorProvider，因为每个EventLoop都可能注册多个Channel，这些Channel需要注册到当前NioEventLoop中的选择器上。**且netty在内部会利用反射优化jdk提供的Selector的实现类sun.nio.ch.SelectorImpl。将其内部的selectedKeys和publicSelectedKeys字段的结构使用netty定义的SelectedSelectionKeySet结构代替（也实现了Set接口）， 在SelectedSelectionKeySet内部又使用数组实现，且不支持直接remove，每次select都reset，以此提高速度**

#### 5.1.1 NioEventLoop重要字段

```java
// 选择器相关
private Selector selector;
private Selector unwrappedSelector;
private SelectedSelectionKeySet selectedKeys;

private final SelectorProvider provider;
/**
 * 处理io事件的时间所用的百分比 
 * 因为当前EventLoop既要处理Channel相关的事件（io事件），也要处理我们提交的线程池接口任务和定时任务
 * 所以，这个数字代表了一次处理io事件的时间占总时间的百分比,默认是平均的
 */
private volatile int ioRatio = 50;
// ============== 下面的为其父类SingleThreadEventExecutor中的字段
// 核心的任务队列（当调用线程池的execute方法时，会将任务投递到这个队列里）
private final Queue<Runnable> taskQueue;
// 当前的线程
private volatile Thread thread;
// 获取线程状态
private volatile ThreadProperties threadProperties;
// 默认为 ThreadPerTaskExecutor（每次execute都创建新的线程）
private final Executor executor;
// 中断标志
private volatile boolean interrupted;
// shutdown hook(关闭的钩子函数)
private final Set<Runnable> shutdownHooks = new LinkedHashSet<Runnable>();
/**
*  在不考虑这个字段的前提下，如果添加任务能主动唤醒线程，则该值为true，否则为false。
*  意思就是添加任务是一定要唤醒线程的。但是，不同的实现子类阻塞当前线程的原因不一样。 
*  1、对于仅执行Runnable任务的（如：DefaultEventLoop），
*      控制线程的阻塞是由阻塞队列实现的，所以向阻塞队列添加新任务时，阻塞队列自己会唤醒线程，
*      从而addTaskWakesUp为true，代表添加任务就能自己唤醒线程，不需要我们额外操作了
*  2、对于执行网络事件的线程（如：NioEventLoop），它也会执行其他非网络IO的任务。
*      控制线程阻塞是由Selector的select方法实现的，任务的阻塞队列将不再能阻塞此线程。
*      所以，当添加任务到taskQueue时，此taskQueue不能唤醒线程，addTaskWakesUp就设为false，
*      代表需要由子类去实现自己唤醒线程（Selector的wakeup方法） 
* 
*  所以，不由taskQueue阻塞线程的类都应该设置为false，自己去实现唤醒 
* NioEventLoop默认为false
* */
private final boolean addTaskWakesUp;
// 拒绝策略
private final RejectedExecutionHandler rejectedExecutionHandler;
// 当前EventLoop的状态
private volatile int state = ST_NOT_STARTED;
// ================= 父抽象类AbstractScheduledEventExecutor的字段
// 定时任务队列（数组的最小顶堆实现），当该EventLoop执行定时任务时，就会将定时任务封装成ScheduledFutureTask投递到当前队列中
PriorityQueue<ScheduledFutureTask<?>> scheduledTaskQueue;
```

#### 5.1.2 NioEventLoop总结

​		NioEventLoop提供了Channel注册和运行对应的事件，也提供了线程池的执行Runnable和定时任务线程池的运行定时任务的能力。**NioEventLoop在执行Runnable方法中，会先将Runnable扔进taskQueue中，在启动当前EventLoop线程，运行到io.netty.channel.nio.NioEventLoop#run方法进入死循环**（所以它是单线程）。

**NioEventLoop的run方法主要逻辑（一次事件循环）**：

- **在一个死循环里运行选择器监听感兴趣的事件（超时等待），如果有准备好的IO事件或任务队列和定时任务队列存在任务时或被用户唤醒时，就跳出循环**
- **先处理IO事件（选择器准备就绪的事件，一般就是服务器接受连接或通道可读事件）**
- **再处理taskQueue中的任务和scheduledTaskQueue中的定时任务（依次将scheduledTaskQueue中运行时间到达的定时任务转入到taskQueue中，并最终根据ioRation的配置来计算能运行taskQueue中任务的事件并以此运行taskQueue中的任务）。尽管ioRation是用来计算非IO任务的可执行时间，但当前版本还是会每执行64个Runnable才检查可执行时间是否用完，所以，就算ioRation设置的再大，在一次轮询中也至少会执行64个我们投递的Runnable（或定时任务），官方的解释时觉得System.nanoTime()花费昂贵，不应该每执行一个Runnable就去判断时间**
- 执行Runnable运行完的钩子函数，也就是一次事件循环结束后需要执行的Runnable，这些Runnable保存在io.netty.channel.SingleThreadEventLoop#tailTasks里
- 事件循环的末尾总要**判断当前NioEventLoop是否SHUTDOWN**（根据**SingleThreadEventExecutor#state**字段判断）。如果shutdown，则close当前NioEventLoop所持有的所有Channel，并取消所有定时任务，再执行完毕所有普通Runnable任务，在执行ShutdownHook等等

**所以，ServerSocketChannel接受连接和SocketChannel读取数据的触发点都在上面的IO事件处理中**

## 6 Future和Promise

​	Netty的Future既实现了jdk的Future接口，也提供了一些新的接口，比如添加监听器，这样我们想操作Future的结果时就不用阻塞等待，而是添加一个监听器，当Future的结果产生时，由产生结果的那个线程去调用监听器（类比为一个回调函数）

而Promise继承了Future，实现了我们可以手动设置结果或设置失败的场景。（比如一个超时场景，我们不想再等待结果了，想直接抛出超时异常，就可以使用Promise的setFailure接口）

## 7 AbstractBootstrap

AbstractBootstrap是启动类的实现抽象，定义了客户端启动器和服务端启动器的公共部分，内部字段：

```java
/**
 * 对于服务端的socket来说是用来构建ServerSocketChannel，处理和客户端的连接事件 
 * 对于客户端的socket来说，是用来构建SocketChannel，处理和服务端的读写
 */
volatile EventLoopGroup group;
/**
 * channel实例化工厂，默认为ReflectiveChannelFactory
 */
@SuppressWarnings("deprecation")
private volatile ChannelFactory<? extends C> channelFactory;
// 本地绑定的地址
private volatile SocketAddress localAddress;
/**
 * 当前socket参数设置
 */
private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<ChannelOption<?>, Object>();
private final Map<AttributeKey<?>, Object> attrs = new LinkedHashMap<AttributeKey<?>, Object>();
/**
 * 当前socket类型产生的Channel会使用的ChannelHandler 
 * 客户端：就是用在SocketChannel上的Handler 
 * 服务端：使用在ServerSocketChannel上的Handler 
 * 因为这里只有一个ChannelHandler，所以最好使用ChannelInitializer，用来注册多个ChannelHandler
 */
private volatile ChannelHandler handler;
```

### 7.1 ServerBootstrap

​		ServerBootstrap启动服务端的工具，用于将我们设置的各种ChannelHandler、EventLoopGroup、socket参数等组合起来，最终启动。当配置好各种参数后，调用bind方法，启动服务端

内部字段

```java
// SocketChannel的socket参数
private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap<ChannelOption<?>, Object>();
private final Map<AttributeKey<?>, Object> childAttrs = new LinkedHashMap<AttributeKey<?>, Object>();
private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);
/**
 * child group，用来处理SocketChannel，处理客户端和服务器的读写
 */
private volatile EventLoopGroup childGroup;
/**
 * 接受连接后产生的SocketChannel应该使用的Handler
 */
private volatile ChannelHandler childHandler;
```

**服务端的启动流程（bind方法）主要逻辑**

- 创建ServerSocketChannel实例
- 设置我们配置的各种socket参数到ChannelConfig里
- 向ServerSocketChannel的ChannelPipeline添加我们指定的handler
- **再向pipeline中添加一个特殊的ServerBootstrapAcceptor**
- 将当前的ServerSocketChannel注册到EventLoop中（原生的Channel注册到Selector上，但先不监听事件。触发ChannelRegistered事件等）
- **绑定端口，触发出站事件的bind接口，走到head后再通过Unsafe触发真正的bind动作**
  - 将jdk原生的ServerSocketChannel绑定到指定端口
  - 触发入站事件**channelActive**
  - 当传播完所有的channelActive事件后，如果开启了**autoRead（默认开启），又开始通过pipeline触发出站事件read**
  - **当出站事件read走到head时，调用Unsafe，利用Selector监听感兴趣的事件（SocketChannel是READ，ServerSocketChannel是ACCEPT）**

#### 7.1.1 ServerBootstrapAcceptor

ServerBootstrapAcceptor实现了ChannelInboundHandler接口，能传播和处理入站事件。

​		**ServerBootstrapAcceptor主要是用来处理ServerSocketChannel接受连接后产生的SocketChannel。在上面介绍的NioEventLoop处理事件中，当Selector监听到ACCEPT或READ事件时，都会调用io.netty.channel.nio.AbstractNioChannel.NioUnsafe#read来处理，而ServerSocketChannel的实现是io.netty.channel.nio.AbstractNioMessageChannel.NioMessageUnsafe#read。在这个方法内部，ServerSocketChannel会接受新的连接，并将其作为参数传入到channelRead中并向后传播，当传播到ServerBootstrapAcceptor的channelRead方法时，将我们设置的child参数和child handler设置到这个SocketChannel中，最终，将这个SocketChannel注册到EventLoop中**

### 7.2 Bootstrap

ServerBootstrap启动客户端的工具，用于将我们设置的各种ChannelHandler、EventLoopGroup、socket参数等组合起来，最终启动。当配置好各种参数后，**调用connect方法，连接服务端**

**客户端启动流程（connect方法）主要逻辑：**

- 反射创建SocketChannel实例
- 添加handler、将我们配置的socket参数设置到ChannelConfig等
- 将当前的SocketChannel注册到EventLoop中（原生的Channel注册到Selector上，但先不监听事件。触发ChannelRegistered事件等）
- 触发出站事件connect，走到head后再通过Unsafe触发真正的connect操作
  - 如果提供了本地端口，先绑定本地端口
  - 用jdk原生的SocketChannel，连接远程的服务端
  - 触发入站事件channelActive
  - 当传播完所有的channelActive事件后，如果开启了**autoRead（默认开启），又开始通过pipeline触发出站事件read**
  - **当出站事件read走到head时，调用Unsafe，利用Selector监听READ事件**

## 8 Netty中的零拷贝场景

- **使用直接内存，在进行IO数据传输时避免了ByteBuf从堆外内存拷贝到堆内内存的步骤**（而如果使用堆内内存分配ByteBuf的话，那么发送数据时需要将IO数据从堆内内存拷贝到堆外内存才能通过Socket发送）
- Netty 使用 FileRegion 实现文件传输的零拷贝。默认的**DefaultFileRegion内部封装了FileChannel** ，**使用FileChannel的transferTo方法实现了CPU零拷贝**
- Netty中提供**CompositeByteBuf类，用于将多个ByteBuf合并成逻辑上的ByteBuf（原数据并没有物理意义上的合并）**，避免了将多个ByteBuf拷贝成一个ByteBuf的过程
- ByteBuf支持**slice方法可以将ByteBuf分解成多个共享内存区域的ByteBuf（数据还是共享的原ByteBuf）**，避免了内存拷贝

## 9 Netty架构

​		Netty是**异步事件驱动**的socket框架，基于Reactor架构，使用jdk的NIO的多路复用（当然不止NIO，这里主要讨论NIO）

​		客户端的Netty从创建EventLoopGroup开始，每个与服务端连接后产生的SocketChannel都将注册到NioEventLoopGroup中。NioEventLoopGroup是用来管理NioEventLoop的组件，在NioEventLoopGroup内部，NioEventLoopGroup将SocketChannel轮询的注册到内部的NioEventLoop中。每个NioEventLoop都是单线程的，既提供SocketChannel的IO处理，也提供线程池和定时任务的Runnable处理。**且在每个NioEventLoop中，都会创建自己的Selector，注册到NioEventLoop的SocketChannel或ServerSocketChannel都会把自己注册到这个Selector中，并监听感兴趣的事件（通过NioServerSocketChannel和NioSocketChannel的构造参数可知，感兴趣的事件分别是ACCEPT和READ）**。且在整个SocketChannel的生命周期内，它始终绑定在这个NioEventLoop里。

​		服务端既要接受客户端的连接，又要处理从SocketChannel中读取数据和发送数据。所以，一般创建两个NioEventLoopGroup，boss group只用设置一个NioEventLoop线程，专门用来处理ServerSocketChannel的ACCEPT事件，并将产生的SocketChannel注册到child group中。child group需要配置多个NioEventLoop线程，用来处理和客户端产生连接后的SocketChannel。

​	**在NioEventLoop线程内部，线程一直死循环处理IO事件和Runnable任务，且根据内部的ioRation来分配处理时间**。从Channel注册后，Channel的整个生命周期都会在当前NioEventLoop中。所以，一个NioEventLoop可能管理多个SocketChannel。**同时，因为单线程处理IO事件和Runnable任务，所以，不要添加执行时间过长的Runnable，否则，都会影响到线程处理其他Channel的时间，特别是在IO事件频繁的情况下**

## 10 FastThreadLocal

​		FastThreadLocal是ThreadLocal的一种变体，和ThreadLocal设计理念一致，都是将数据存取在当前线程环境中，不同的线程从ThreadLocal中只能获取到当前线程对应的那个数据，避免了加锁，典型的空间换时间理念。

​		但FastThreadLocal有比ThreadLocal更快的设置和取值方法，原因是FastThreadLocal维护了自己的InternalThreadLocalMap结构（如果当前线程被FastThreadLocalThread包装了，那这个InternalThreadLocalMap很好取出，就在FastThreadLocalThread里。如果当前线程是Thread，就是取得UnpaddedInternalThreadLocalMap#slowThreadLocalMap里的那个InternalThreadLocalMap，所以，这种取出InternalThreadLocalMap的方法要慢点，毕竟是从ThreadLocal中取，并不是线程里直接拿出来）。

​		**在InternalThreadLocalMap内部，使用数组来维护FastThreadLocal需要保存的值，每个FastThreadLocal在创建时，都会设置一个固定的数组索引（InternalThreadLocalMap#nextIndex字段自增获取）保存在内部的index字段，在利用FastThreadLocal设置值时，直接将我们需要保存的value设置在io.netty.util.internal.UnpaddedInternalThreadLocalMap#indexedVariables这个数组的索引位置，取出是也是一样的道理，直接从这个索引取。所以，FastThreadLocal的设置和取出复杂度很低（基本没有，因为索引位置时已知的，利用数组的索引直接定位到我们需要的数据）**

### 10.1 回忆一下在ThreadLocal中是怎么设置并取值的？为什么要出现这种差异？以及这两种结构的用法

​		ThreadLocal的设值和取值是通过其内部的ThreadLocalMap实现的，在ThreadLocalMap内部用数组保存了Entry节点，Entry接口内部保存了当前ThreadLocal和我们设置的值。但在设置时，会先通过ThreadLocal的hash值和table长度计算出在ThreadLocalMap#table数组的索引位置。

​		**所以，不同的ThreadLocal完全有可能由相同的数组索引值，ThreadLocalMap解决hash冲突的方法是使用开放寻址法中的线性探查法。设置时依次查找下一个索引位置里不存在Entry的索引，再将构造的Entry放入其中。而查找时则依次查找下一个索引位置里不为空的Entry，依次比对ThreadLocal，直到找到完全相等的那个ThreadLocal。**

​		**所以，很明显ThreadLocal在设值和取值时有很大可能花费的时间要比FastThreadLocal更久，最坏的情况就是要比较整个数组里的所有数据，最好的情况就是hash和table长度计算出来得那个索引位置。更不用说ThreadLocal在设值和取值时，如果碰到泄露的Entry，还需要清理**

​		ThreadLocal之所以设计成这样是考虑到程序中可能会回收掉ThreadLocal，在一个具有很短生命周期的ThreadLocal里，即使它被回收了，我们也能通过弱引用的特性来探测到其对应的Entry结构，并回收其对于的Entry。而FastThreadLocal并没有使用弱引用，**所以如果FastThreadLocal不能被程序直接访问到而它又没有做remove操作，那就会触发这部分的内存泄露了**。

​		**所以，FastThreadLocal的正确使用方式应该是在程序中使用FastThreadLocal的地方，就应该保持FastThreadLocal的持久性（不要被垃圾回收）。但如果FastThreadLocal生命周期比当前线程短，那么它使用完毕后就应该操作remove来删除数据（netty设计了FastThreadLocalRunnable来对Runnable进行包装，这样我们就不用主动操作remove了）**。如果你在程序中创建了一个会被回收的ThreadLocal，记得调用remove方法或对它进行FastThreadLocalRunnable包装

### 10.2 FastThreadLocal和ThreadLocal优劣对比

#### 10.2.1 ThreadLocal

- 优势
  - 弱引用设计，不需要主动调用remove也能避免内存泄漏
- 劣势
  - 弱引用也会增加垃圾回收工作的压力
  - 基于线性探测发解决hash冲突，也会导致查询和插入时间增加（相比链地址法会使用连续且紧凑的内存空间来存储数据，寻址时也会更快），甚至在删除时也要重新排列这后的数据

#### 10.2.2 FastThreadLocal

- 优势
  - 查询和删除快（毕竟已知数据index，查询，删除和重新set的复杂度很低）
- 劣势
  - 非弱引用设计，必须主动调用remove，否则可能引发内存泄漏
  - 空间使用多（因为index是递增的，当一个FastThreadLocal不再使用了且被remove掉后，数组对应的位置也不会在放其他数据）

## 11 NIO相关

| Origin |       Channel       | OP_ACCEPT | OP_CONNECT | OP_WRITE | OP_READ |
| :----: | :-----------------: | :-------: | :--------: | :------: | :-----: |
| client |    SocketChannel    |           |     Y      |    Y     |    Y    |
| server | ServerSocketChannel |     Y     |            |          |         |
| server |    SocketChannel    |           |            |    Y     |    Y    |

- `OP_ACCEPT`：当收到一个客户端的连接请求时，该操作就绪。这是`ServerSocketChannel`上唯一有效的操作。

- `OP_CONNECT`：只有客户端`SocketChannel`会注册该操作，当客户端调用`SocketChannel.connect()`时，该操作会就绪。

- `OP_READ`：该操作对客户端和服务端的`SocketChannel`都有效，当OS的读缓冲区中有数据可读时，该操作就绪。

- `OP_WRITE`：该操作对客户端和服务端的`SocketChannel`都有效，当OS的写缓冲区中有空闲的空间时(大部分时候都有)，该操作就绪。

  >  OP_WRITE  事件相对特殊，一般情况，不应该注册`OP_WRITE事件`，**`OP_WRITE`的就绪条件为操作系统内核缓冲区有空闲空间**(`OP_WRITE事件`是在`Socket`发送缓冲区中的可用字节数大于或等于其低水位标记`SO_SNDLOWAT`时发生)，而写缓冲区绝大部分事件都是有空闲空间的，所以当你注册写事件后，写操作一z直就是就绪的，这样会导致`Selector`处理线程会占用整个CPU的资源。所以最佳实践是当你确实有数据写入时再注册`OP_WRITE事件`，并且在写完以后马上取消注册

