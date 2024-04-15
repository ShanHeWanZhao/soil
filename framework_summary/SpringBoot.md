# SpringBoot

## 1. 启动流程

- 创建计时器，设置当前环境的headless模式
- 从spring.factories读取配置的SpringApplicationRunListener（默认为EventPublishingRunListener），从来进行springboot启动过程中各种事件的发布
- 发布ApplicationStartingEvent事件，表示springboot项目开启启动
- 实例化Environment，并且发布Environment准备好的事件
- 根据需要决定打印banner
- 创建ApplicationContext（默认实现为AnnotationConfigServletWebServerApplicationContext）
  - 准备ApplicationContext（主要包括将上面实例化好的Environment设置进去，和将启动类构造为BeanDefinition放入IOC中）
- 注册ShutdownHook线程，用来destory单例bean的
- 容器的refresh阶段
  - 主要包括注册并调用BeanFactoryPostProcessor，BeanDefinitionRegistryPostProcessor。再实例化BeanPostProcessor缓存起来等待bean实例化调用
  - 国际化处理，事件广播，注册各种ApplicationListener等
  - web环境初始化（创建Tomcat类，并配置内部的Service、Connector、Host、Engine等组件，最后在启动tomcat）
  - 实例化容器中所有的单例bean
  - 发布ContextRefreshedEvent事件，表示容器刷新完成
- 发布ApplicationStartedEvent事件，表示springboot启动完成
- 最后再运行容器中ApplicationRunner和CommandLineRunner相关bean

## 2. jar包直接运行程序的解析

利用spring提供的spring-boot-maven-plugin插件会将当前项目打包为jar包，并且将当前项目所依赖的其他jar包也放在其中。这个打包后的jar层级结构如下

```txt
spring-boot-maven-plugin插件打包出来的jar包里面的目录结构
├── BOOT-INF
│   ├── classes
│   │   ├── 当前项目编译后的class文件和各种resource资源
│   └── lib
│       ├── 当前项目依赖的其他jar包
├── META-INF
│   ├── MANIFEST.MF（jar包元信息，这个文件里有启动类的配置等等）
├── org
│   └── springframework
│       └── boot
│           └── loader（spring提供的jar包解析器和自定义的类加载器）
│               ├── ExecutableArchiveLauncher.class
│               ├── JarLauncher.class
│               ├── LaunchedURLClassLoader$UseFastConnectionExceptionsEnumeration.class
│               ├── LaunchedURLClassLoader.class
│               ├── Launcher.class
│               ├── MainMethodRunner.class
│               ├── ...
```

> springboot之所以能直接运行jar包，核心就是这个自定义的类加载器LaunchedURLClassLoader
>
> ​		java -jar命令执行会触发org.springframework.boot.loader.JarLauncher#main方法的运行，会走到这个方法里org.springframework.boot.loader.Launcher#launch，代码如下。

```java
// org.springframework.boot.loader.Launcher类
protected void launch(String[] args) throws Exception {
    if (!isExploded()) {
       JarFile.registerUrlProtocolHandler();
    }
    // 创建LaunchedURLClassLoader类加载器
    /*
       jvm三大类加载器加载路径：
       1.BootstrapClassLoader : System.getProperty("sun.boot.class.path") -- 主要是rt.jar的class
       1.ExtClassLoader : System.getProperty("java.ext.dirs") -- 主要是jdk里lib/ext目录下的jar
       1.AppClassLoader : System.getProperty("java.class.path") --classpath的class（我们写的class）

       总结一下，为什么需要这个自定义的LaunchedURLClassLoader来加载class：
       springboot以jar包方式运行时，项目的其他依赖jar其实都已经整合到这个jar包里了，
          就在 BOOT-INF/lib/ 目录下，它不同于其他maven项目，本质就是jar包的嵌套。
       如果我们用jdk的类加载器加载项目中的其他三方包是根本找不到的，因为这些三方包不在classpath下，
       spring自定义了LaunchedURLClassLoader，并且启动时将所有 BOOT-INF/lib/ 目录下的jar以URL的形式保存，这样在加载三方class
       时，就会在LaunchedURLClassLoader保存的URL种去查找，再加载
     */
    ClassLoader classLoader = createClassLoader(getClassPathArchivesIterator());
    String jarMode = System.getProperty("jarmode");
    // 获取Start-Class，也就是我们主程序的启动类
    String launchClass = (jarMode != null && !jarMode.isEmpty()) ? JAR_MODE_LAUNCHER : getMainClass();
    // 设置LaunchedURLClassLoader到当前thread的环境中，以便在后续加载class时使用到，并启动主启动类
    launch(args, launchClass, classLoader);
}
```

​		上诉代码主要就是将当前可运行jar包里BOOT-INF/lib/目录下的所有依赖jar抽象为一个个的org.springframework.boot.loader.archive.Archive，因为LaunchedURLClassLoader本质也是个URLClassLoader，而URL是统一资源定位符，所以，前面jar抽象出来的Archive会转换成java.net.URL对象，从而让LaunchedURLClassLoader在搜索class的时候从这些URL里查找，达到找到calss文件的目的