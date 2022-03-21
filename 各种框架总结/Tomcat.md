# Tomcat（8.5）

## 1 Tomcat架构

​	Tomcat类似与树形结构，根节点为Server，代表当前tomcat进程。Server下可以有多个Service，代表多种服务。每个Service可以有多个Connector，代表每个Service可以监听多个端口，但只会有一个Engine。Engine可管理多个Host，Host又可以管理多个Context，Context又可以管理多个Wrapper（一个Wrapper即一个Servlet）。

​		以一次http请求来分析各个组件的作用，假如url为：http://www.github.com/demo/user/get，这个请求首先请求到Service下对应端口的Connector，通过这个Connector拿到关联的Service，Service内部又一个路由器Mapper，通过这个org.apache.catalina.mapper.Mapper#map方法解析解析url找出最符合条件的Host、Context、Wrapper。在这个例子中www.github.com对应Host，demo对应Context，user/get对应Wrapper

## 2 Server

​		**tomcat最顶层的组件，代表整个tomcat进程，默认实现为StandardServer。Server是Tomcat架构中最开始启动的，tomcat内部组件启动时先通过init启动Server，Server将init事件传播给子组件，子组件在递归传播。当所有子组件init后，再start Server，再如此传播给子组件，完成程序的启动**

生命周期方法：

- initInternal：主要就是初始化内部所有Service
- startInternal：主要时启动内部所有的Service

## 3 Service

​		tomcat提供的服务抽象，一个Service可以有多个Connector，比如既可以添加http协议Connector，也可以添加https协议Connector，默认实现类为StandService

StandService的重要字段：

```java
// 父组件的Server
private Server server = null;
// Connector数组，Service下可以有多个Connector
protected Connector connectors[] = new Connector[0];
private final Object connectorsLock = new Object();
// server.xml里再Service节点里配置的Executor节点，共享线程池（默认没配置）
protected final ArrayList<Executor> executors = new ArrayList<>();
// 关联的Engine
private Engine engine = null;

private ClassLoader parentClassLoader = null;
// 非常重要，我把它叫路由器，将协助connector来定位一个请求具体该使用哪个wrapper来处理
protected final Mapper mapper = new Mapper();
// Mapper的监听器，负责向Mapper里添加并组合Host、Context、Wrapper
protected final MapperListener mapperListener = new MapperListener(this);
```

生命周期方法：

- initInternal：初始化内部的Engine、配置的Executor、MapperListener（这三个在初始化中基本没做啥，就注册MBean之类的），初始化内部所有的Connector（会构建ProtocolHandler和Endpoint，绑定服务器端口，服务端Socket在这里就准备好了）
- startInternal：依次启动内部的Engine、配置的Executor、MapperListener和所有的Connector

### 3.1 Connector

用来处理Socket的抽象，使用内部的ProtocolHandler来初始化ServeSocketChannel

### 3.2 ProtocolHandler

​	tomcat默认使用Http11NioProtocol，即NIO。

生命周期方法：

- init：触发NioEndpoint的初始化
- start：触发NioEndpoint初始化和启动AsyncTimeout线程

### 3.3 NioEndpoint

tomcat里和Socket直接打交道的类，封装了Socket相关操作

生命周期方法：

- init：实例化ServerSocketChannel并绑定端口
- **start**
  - 创建Processor、Event、Nio相关缓存
  - 构建tomcat的IO线程池（即事件处理线程池）
  - 初始化LimitLatch，用来限制连接个数
  - **创建并启动Poller线程（Poller是事件轮询器，SocketChannel会将READ事件注册，并让其轮询）**
  - **创建并启动Acceptor线程，用来处理连接**

### 3.4 Acceptor

Acceptor是tomcat用来处理连接的线程，类比Netty里的ServerBootstrapAcceptor。Acceptor启动后，死循环监听端口产生的连接，并在连接到来时构造SocketChannel并注册到Poller中

一次循环主要流程：

- 调用java.nio.channels.ServerSocketChannel#accept阻塞（在构建ServerSocketChannel时，并没有将其配置为非阻塞，这样当没有连接时，Acceptor线程就会阻塞在accept方法里）
- 获取到SocketChannel，配置socket相关参数，并将这个SocketChannel封装为NioChannel
- 用NioSocketWrapper在疯转一次NioChannel，设置感兴趣事件为READ。封装为PollerEvent，注册到Poller中

### 3.5 Poller

​		Poller为轮询器线程，将PollerEvent注册到内部的Selector，并监听到事件触发后，交给Processor去处理。

Poller在内部会将PollerEvent中对应的SocketChannel和感兴趣的事件注册到内部的Selecotr上，并死循环进行select，当事件到达时，使用SocketProcessor在tomcat的IO线程池中去处理对应的事件

### 3.2 Mapper

​		路由器，当http请求发送过来后，Mapperf负责通过请求的url找到对应的Host、Context、Wrapper，最终就是要找到具体由哪个Servlet来转发请求。Host、Context、Wrapper可以看做是多分支的树形结构。都存在多个。所以，Mapper内部是存了多个Host，每个Host又存了多个Context，每个Context又存了多个Wrapper

## 4 容器Container

​		Container接口时tomcat容器的标准接口，ContainerBase为Container接口的实现抽象类，构造了容器需要的公共字段，tomcat的每个容器都通过继承ContainerBase来实现容器。每个容器都有属于自己的Pipeline管道，也存在自己的子容器集和（child Container）。当一个http请求到达后，解析完http数据，通过最顶级的容器Engine的管道，调用内部的Valve阀门，依次将请求传递到对应子容器的管道中的阀门里（Engine -> Host -> Context -> Wrapper），最后再通过过滤器到达Servlet

ContainerBase的一些重要字段和方法：

```java
// 子容器，key为容器名，value的容器引用
// 每个Container都会有name
protected final HashMap<String, Container> children = new HashMap<>();
/**
 * tomcat后台线程ContainerBackgroundProcessor的执行间隔时间，
 * 这个后台线程专门用来处理过期的session等 
 * 这个值小于0时，就不会启动后台线程 
 * 默认情况下，只有engine才会初始化这个值为10。
 * 其余的容器（host,context,wrapper都不会修改这个值）
 * 所以，只有engine在start时才会启动ContainerBackgroundProcessor线程 
 * 因为每一个Container都可能会处理自身的后台任务，所以直接由最顶级的engine容器来开启这个线程，
 * 递归的处理子Container，这样就能处理所有Container的后台任务了
 */
protected int backgroundProcessorDelay = -1;
// 容器的监听器
protected final List<ContainerListener> listeners = new CopyOnWriteArrayList<>();
// 当前容器名
protected String name = null;
// 当前容器的父容器（Engine为最顶层的容器，所以为null）
protected Container parent = null;
// 当前容器的管道
protected final Pipeline pipeline = new StandardPipeline(this);
// 上面运行background的线程
private Thread thread = null;
private int startStopThreads = 1;
// 启动或暂停当前容器的子容器线程池（默认线程数就为上面的1，且允许核心线程过期。一般只有在启动才需要用到，所以启动完毕后这个线程池就没啥用了，让里面的线程过期）
protected ThreadPoolExecutor startStopExecutor;
// 容器的初始化方法，构建startStopExecutor，为启动子容器做准备
@Override
protected void initInternal() throws LifecycleException {
    BlockingQueue<Runnable> startStopQueue = new LinkedBlockingQueue<>();
    // 默认一个线程
    startStopExecutor = new ThreadPoolExecutor(
        getStartStopThreadsInternal(),
        getStartStopThreadsInternal(), 10, TimeUnit.SECONDS,
        startStopQueue,
        new StartStopThreadFactory(getName() + "-startStop-"));
    startStopExecutor.allowCoreThreadTimeOut(true);
    super.initInternal();
}
// 重点是使用startStopExecutor启动子容器并阻塞等待结束，再触发当前容器里监听器lifecycleListeners的start事件（这点很有用，HostConfig和ContextConfig机会做一些事）
@Override
protected synchronized void startInternal() throws LifecycleException {

    // Start our subordinate components, if any
    logger = null;
    getLogger();
    Cluster cluster = getClusterInternal();
    if (cluster instanceof Lifecycle) {
        ((Lifecycle) cluster).start();
    }
    Realm realm = getRealmInternal();
    if (realm instanceof Lifecycle) {
        ((Lifecycle) realm).start();
    }

    // Start our child containers, if any
    Container children[] = findChildren();
    List<Future<Void>> results = new ArrayList<>();
    for (Container child : children) {
        results.add(startStopExecutor.submit(new StartChild(child)));
    }

    MultiThrowable multiThrowable = null;

    for (Future<Void> result : results) {
        try {
            result.get();
        } catch (Throwable e) {
            log.error(sm.getString("containerBase.threadedStartFailed"), e);
            if (multiThrowable == null) {
                multiThrowable = new MultiThrowable();
            }
            multiThrowable.add(e);
        }

    }
    if (multiThrowable != null) {
        throw new LifecycleException(sm.getString("containerBase.threadedStartFailed"),
                                     multiThrowable.getThrowable());
    }

    // Start the Valves in our pipeline (including the basic), if any
    if (pipeline instanceof Lifecycle) {
        ((Lifecycle) pipeline).start();
    }

    // 触发当前组件里监听器lifecycleListeners的start事件
    setState(LifecycleState.STARTING);

    // Start our thread
    threadStart();
}
```

### 4.1 Engine

​	Engine是Tomcat最顶层的容器，和Service是一对一的关系，在他们内部互相保存了对方的引用，默认实现类时StandardEngine

StandardEngine重点字段（Engine这个容器本身没有什么特殊的意义，所以初始化和启动方法都是直接使用父类ContainerBase去启动子容器的，就没啥好说的了）

```java
/**
 * 默认主机名，当请求没有精确或模糊匹配到指定的主机名，就会使用这个
 * server.xml里的Engine标签里的defaultHost属性值（默认为localhost）
 */
private String defaultHost = null;
```

### 4.2 Host

​	Host作为Engine的子容器，默认实现为StandardHost，代表了请求host部分的抽象。当一个http请求到达时，会根据器url的host部分，找到对应名字的StandardHost，如果没有，则会使用默认的Host（即localhost）

StandardHost重点字段（初始化和启动方法还是调用父类）

```java
/**
 * 当前Host的别名，匹配别名也能匹配到当前Host
 */
private String[] aliases = new String[0];

/**
 * 当前Host的web目录（这个目录下就应该存放web应用）
 */
private String appBase = "webapps";

/**
 * 默认：${catalina.base}/webapps
 */
private volatile File appBaseFile = null;

/**
 * 默认为：conf/Catalina/localhost目录
 */
private volatile File hostConfigBase = null;

/**
 * 是否自动部署
 */
private boolean autoDeploy = true;

/**
 * 添加到当前Host的子容器Context里的监听器（ContextConfig）
 */
private String configClass =
    "org.apache.catalina.startup.ContextConfig";
/**
 * 当前Host的子容器Context的实现类
 */
private String contextClass =
    "org.apache.catalina.core.StandardContext";
/**
 * 启动时部署Context
 */
private boolean deployOnStartup = true;
/**
 * （应该被忽略的Context）默认为null
 */
private Pattern deployIgnore = null;

```

#### 4.2.1 HostConfig

​		**查看默认的server.xml可知，Host标签下并没有配置Context标签。所以，解析出来后StandardHost并没有子容器。而StandardHost有配置的web启动目录（webapps），目录下有各种文件夹或war包等，HostConfig的作用就是将当前Host的web目录下的文件夹、war包等构造成各自Context，并将其添加到StandardHost作为其子容器。所以，这就是就算不在server.xml目录下配置Context标签，但webapps目录下的文文件夹或war包等还是会启动的原因。**

​		HostConfig实现了LifecycleListener接口，在tomcat解析server.xml中的构建StandardHost时，就会实例化一个HostConfig放到lifecycleListeners中，作为StandardHost启动阶段的监听器并完成一些列的工作。

​		StandardHost在startInternal期间中的子容器启动完成（默认没有子容器），但后台线程还未启动时，会触发HostConfig的start，在这个start里根据是否自动部署来构造Context。

StandardHost的主要方法：

```java
/**
	部署web应用
	不论是xml、文件夹、war包部署，在内部都是将其解析为Context，并为每个Context添加监听器ContextConfig，在放入当前Host的子容器中，也会根据当前状态来进行对应的操作
*/
protected void deployApps() {
    File appBase = host.getAppBaseFile();
    // conf/Catalina/localhost目录
    File configBase = host.getConfigBaseFile();
    // 过滤器默认为null，所以还是返回全部
    String[] filteredAppPaths = filterAppPaths(appBase.list()); // 过滤webapps文件夹下的所有文件
    // xml方式部署（IDEA的war exploded就是这种方式）
    // 每一个xml其实就是一个context，文件名就是context的ptah。ROOT.xml就是默认的context，即path为空字符串
    deployDescriptors(configBase, configBase.list()); // 以xml方式解析并部署conf/Catalina/localhost目录下所有的xml文件
    // war包的形式部署
    // 部署${catalina.base}/webapps目录下的所有war包
    deployWARs(appBase, filteredAppPaths);
    // 直接以文件夹的形式部署
    // 部署${catalina.base}/webapps目录下的所有文件夹形式的context
    deployDirectories(appBase, filteredAppPaths);
}
```

### 4.2 Context

Context时toncat中最重要的一个组件，一个Context就代表一个web环境，有最重要的path属性（默认为/，即代表根路径），对应一个ServletContext，默认实现为StandardContext。

StandardContext重点字段：

```java
// 关联的ServletContext实现
protected ApplicationContext context = null;
// 当前Context的路径
private String path = null;
// Fillter配置
private HashMap<String, ApplicationFilterConfig> filterConfigs = new HashMap<>();
// Filter配置的抽象集和（在wel.xml里配置的Filter）
private HashMap<String, FilterDef> filterDefs = new HashMap<>();
// 默认为StandardManager（管理过期的Session）
protected Manager manager = null;
// servlet映射
private HashMap<String, String> servletMappings = new HashMap<>();
// session超时事件（分钟单位）
private int sessionTimeout = 30;
// ============ Cookie相关配置 ================
private String sessionCookieName;
private boolean useHttpOnly = true;
private String sessionCookieDomain;
private String sessionCookiePath;
// 是否在cookie1路径最后面添加/，默认否（比如路径为/foo，避免请求/foobar也带上这个cookie）
private boolean sessionCookiePathUsesTrailingSlash = false;
// 如果客户端提供了一个不存在的seesionId，是否使用这个sessionId构建对应的session，设为false和context path为/ 才会生效
private boolean validateClientProvidedNewSessionId = true;
```

StandardContext的start重要流程：

- 设置Loader为WebappLoader（和当前Context的使用的类加载器有关）
- 设置CookieProcessor（默认为Rfc6265CookieProcessor）
- 启动WebappLoader（创建ParallelWebappClassLoader）
- 将ParallelWebappClassLoader绑定到当前线程环境
- **发送configure_start事件，让内部的监听器ContextConfig开始解析web.xml和其他jar包里的web-fragment.xml，并将解析出来的WebXml结构的信息封装一下导入到StandardContext中（比如将servlet配置构造成Warpper作为Context的子容器）**
- 启动子容器（上面一步导入的Wrapper，StandardWrapper启动阶段不会做什么）
- 构造StandardManager作为session管理器等
- **触发WebXml的listener标签里配置的各种监听器（Spring + Tomcat组合时用到的ContextLoaderListener就会在这时启动。所以，这里就是spring开始实例化的开始）**
- 启动在WebXml里配置的各种filter
- **加载并实例化各种loadOnStartup > 0的Servlet**
- **启动ContainerBackgroundProcessor线程，周期性的处理容器的后台任务（其中最重要的2个后台任务如下）**
  - **WebappLoader的后台任务检测：如果当前Context开启了重载检测（默认不开启），那么就会检测ParallelWebappClassLoader所加载的Class和jar是否有修改，如果有修改，就会重启当前Context（先stop，再重新start）**
  - **StandardManager的后台任务检测：清理过期session**

### 4.3 Wrapper

​		Wrapper就是Servlet的抽象，一个Wrapper对应一个Servlet，在ContextConfig监听器解析web.xml和web-fragment.xml后，会将解析到的Servlet封装为StandardWrapper，并将其作为Context的子容器。

​		**Servlet的实例化都在org.apache.catalina.core.StandardWrapper#load（实例化Servlet和调用init接口）里，而在StandardContext启动期间，只会load在xml里配置loadOnStartup > 0 的Servlet**

## 5 http请求到达tomcat时的主要流程

- 通过Acceptor线程接受连接，构造成PollerEvent注册到Poller中
- Poller注册感兴趣的事件到Selecotr并监听，当事件到达时，使用SocketProcessor在tomcat的IO线程池中（AbstractEndpoint#executor）处理事件
- 利用SocketProcessor里的ConnectionHandler处理对应的socket事件
- org.apache.coyote.AbstractProcessorLight#process
- org.apache.coyote.http11.Http11Processor#service
- 通过Http11InputBuffer解析http请求
- 解析http协议，构造成Request和Response
- org.apache.catalina.connector.CoyoteAdapter#service
- org.apache.catalina.connector.CoyoteAdapter#postParseRequest方法找到当前url具体要使用Host、Context、Wrapper
- 请求首先转发到Engine里面的Pipeline中的Value中去（默认是StandardEngineValve）
- **StandardEngineValve -> StandardHostValve -> StandardContextValve -> StandardWrapperValve**
- 到达StandardWrapperValve阀门后，会构造ApplicationFilterChain（web.xml里过滤器的集和抽象），依次将Request和Response传入，最终通过后便走到Servlet的service方法中

## 6 ParallelWebappClassLoader类加载器

loadClass方法流程

- 查找当前类加载器是否已经加载了这个class（加载过的class会缓存在resourceEntries里）
- 从当前类加载器的native中查找是否已经缓存过
- 从系统类加载器中查找当前Class是否加载过（加载过就直接返回）
- 设置了delegate为true，则尝试使用tomcat的common类加载器加载
- 使用当前类加载器从WEB-INF/classes和WEB-INF/lib目录中加载

## 7 springboot + 嵌入式tomcat的启动

​		在ApplicationContext的refresh里，子类ServletWebServerApplicationContext实现了onRefresh方法。在这个方法里，就是构造各种Tomcat所需要的组件并组合起来（比如创建Service、Connector、Context、Host、WebappLoader等，再将他们放在合适的位置）。最终调用Tomcat类的start方法，依次从Server开始启动，一直递归向子组件传播，最终达到启动tomcat的目的（在调用真正的startInternal之前，如果还没有调用初始化方法，就会先调用初始化方法）



