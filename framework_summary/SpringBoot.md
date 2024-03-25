# SpringBoot启动流程

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