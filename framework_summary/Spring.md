# Spring(5.1.x)
## 1.bean实例化过程（BeanFactory#getBean方法看）

### 1.1 循环引用

#### 1.1.1 三级缓存

* 一级缓存（**DefaultSingletonBeanRegistry#singletonObjects**）：存放的是完全初始化好的bean，包括已实例化、填充内部依赖的bean，运行完初始化方法（@PostConstruct、afterPropertiesSet方法、指定的init-method方法）
* 二级缓存（**DefaultSingletonBeanRegistry#earlySingletonObjects**）：存放bean缓存（如果能被AOP，就是AOP对象），但还未填充属性和允许初始化方法
* 三级缓存（**DefaultSingletonBeanRegistry#singletonFactories**）：存放bean的工厂对象，使用这个工厂对象，可提前暴露出bean的引用（专用来提前暴露AOP对象的方法）

#### 1.1.2 **为什么要使用三级缓存**

三级缓存专门来解决AOP对象的暴露问题。如果没用AOP是可以只用一级缓存和二级缓存就解决的。但如果使用了AOP且没有三级缓存，那么必须在实例化后就马上完成AOP代理，但这和spring的设计初衷不同，AOP代理的完成时使用了bean的后置处理器**AnnotationAwareAspectJAutoProxyCreator**来完成的，也就是在初始化bean后执行的bean后置处理器方法（**AbstractAutowireCapableBeanFactory#initializeBean**），就不可能再实例化bean后进行代理，所以才有了三级缓存，仅用来提前暴露AOP对象

#### 1.1.3 三层级缓存真能完美解决吗？

如果有3个bean分别为A、B、C，A依赖B和C，B只依赖A，C什么都不依赖，但提供一个方法sayHello使用。

​		当开始实例化A时，实例化A后将其工厂对象放入三级缓存中，开始填充A属性，发现了B需要填充，开始实例化B，实例化B对象过程中又需要填充其属性A，这时能从三级缓存中取出了A的引用（但此时A不完整），如果B对象有一个初始化方法（@PostConstruct），调用A对象里的C对象的sayHello方法，但由于A此时只是个空壳，就会抛出空指针异常。

​		总的来说，就是在循环引用期间的调用初始化方法时，调用了尚未完全创建好的bean（空壳bean）的某个字段的方法，导致抛出NPE，导致服务启动失败

### 1.2 bean的创建流程

1. 实例化bean
2. 放入三级缓存（根据**AbstractAutowireCapableBeanFactory#allowCircularReferences**字段决定）
3. 填充依赖bean（**AbstractAutowireCapableBeanFactory#populateBean**）
4. 初始化bean（**AbstractAutowireCapableBeanFactory#initializeBean**）
   - 4.1 调用**BeanPostProcessor#postProcessBeforeInitialization方法**
   - 4.2 调用各种初始化方法（**@PostConstruct、afterPropertiesSet方法、指定的init-method方法**）
   - 4.3 调用**BeanPostProcessor#postProcessAfterInitialization**

## 2. 容器的refresh(AbstractApplicationContext#refresh)

1. 准备BeanFactory
2. 获取并实例化容器中的**BeanDefinitionRegistryPostProcessor**，随后调用**postProcessBeanDefinitionRegistry**方法
3. 获取并实例化容器中的**BeanFactoryPostProcessor**，随后调用**postProcessBeanFactory**方法
4. 获取并实例化容器中的**BeanPostProcessor**，放入BeanFactory中，以便后续其他bean实例化使用
5. 为上下文初始化Message源，国际化处理
6. 初始化事件广播器，注册这个bean到容器中（**ApplicationEventMulticaster**）
7. onRefresh（留给子类实现，比如初始化web环境）
8. 注册各种**ApplicationListener**
9. 初始化剩下的**非lazy bean**
10. 完成刷新，实现**SmartLifecycle**接口的bean开始启动，随后发布**ContextRefreshedEvent**事件

## 3.重要的后置处理器

### 3.1BeanPostProcessor

#### 3.1.1 CommonAnnotationBeanPostProcessor

​		用来处理**@javax.annotation.Resource**、**@javax.annotation.PostConstruct**、**@javax.annotation.PreDestroy**、**@javax.ejb.EJB**、**@javax.xml.ws.WebServiceRef**注解的处理器

使用地点：

  * **CommonAnnotationBeanPostProcessor#postProcessProperties**：在实例化bean时populateBean调用，用来**处理@Resource、@EJB、@WebServiceRef注解**，填充bean中的属性
  * **InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization**： 应用**@PostConstruct**注解
  * **InitDestroyAnnotationBeanPostProcessor#postProcessBeforeDestruction**： 应用**@PreDestroy**注解

> @Resource重点解析（ResourceElement为其解析后对应的数据结构）：
>
> 主要是name和type属性，lookup、mappedName用于JDNI，就不分析了，其余那几个没啥用。
>
> * name: 就是beanName，如果不填，ResourceElement.name会解析为字段名，且ResourceElement.isDefaultName为true
> * type: 默认为字段的Class类型

> @Resource很强大，既可以支持按beanName匹配，也可以按类型匹配。
>
> **按类型匹配条件**：CommonAnnotationBeanPostProcessor#fallbackToDefaultTypeMatch为true（默认就为true），且ResourceElement.isDefaultName为true（即不指定@Resource的name属性），且不能存在这个字段名的bean
>
> **按beanName匹配**：指定了@Resource的name属性 或 CommonAnnotationBeanPostProcessor#fallbackToDefaultTypeMatch为false或存在这个字段名的bean（就是上面的反向情况）

#### 3.1.2 AutowiredAnnotationBeanPostProcessor

用来处理**@org.springframework.beans.factory.annotation.Autowired**、**@org.springframework.beans.factory.annotation.Value**、**@javax.inject.Inject**注解的处理器

#### 3.1.3 AnnotationAwareAspectJAutoProxyCreator（非常重要）

spring实现动态代理的Bean后置处理器

**Advisor**：增强器，一个Advisor对应一个切面。既包含Advice，也包含过滤器（判断bean是否需要增强的东西），所以能用一个Advisor来判断任意一个bean是否能被它增强，并提供增强的Advice。

AbstractAutoProxyCreator#postProcessAfterInitialization方法，在bean初始化（各种初始化方法调用完）后调用，用来增强bean，返回的bean可能已经是一个新的代理bean了。

该方法作用：找到能对这个bean进行增强的Advisor，并增强这个bean

> 判断能否对bean增强阶段：
>
> * 1. 获取容器中所有的Advisor（既有spring的，也有我们定义的）
>      * @EnableTransactionManagement注解导入的BeanFactoryTransactionAttributeSourceAdvisor，专门用来处理事物注解的
>      * @EnableCaching注解导入的BeanFactoryCacheOperationSourceAdvisor，专门用来处理spring提供的缓存注解
>      * 自定的：我们自己用@org.aspectj.lang.annotation.Aspect注解定义的切面类，org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory#getAdvisors方法用来将@Aspect注解定义的切面类解析为一系列的Advisor，一个注解切面对应一个InstantiationModelAwarePointcutAdvisorImpl（Advisor的实现类）
>   2. 用这些Advisor，依次对bean进行匹配，IntroductionAdvisor只需匹配类（ClassFilter），PointcutAdvisor既需匹配类（ClassFilter），也需要匹配方法（MethodMatcher）。只要能匹配上，就代表这个bean可以用这个Advisor进行增强。再收集到能对当前bean进行增强的Advisor，准备下一步的增强
>
> 增强bean阶段（创建bean的代理）：
>
> * 1. ProxyFactory是创建代理bean的核心，代理bean创建出来就是一个新的bean，新的bean只提供切面逻辑的实现，用到原始bean的方法时，还是会交给原始bean去执行，原始bean并没有做任何改变，将原来的bean封装为TargetSource以供去执行真正的原始方法。
>   2. 开始增强
>      * cglib的实现（**CglibAopProxy#getProxy(java.lang.ClassLoader)**）：
>        * 1. 校验final方法（只是打个日志，final方法不能增强）
>          
>          2. 创建**org.springframework.cglib.proxy.Enhancer**（核心的cglib增强器）
>          
>          3. 对Enhancer进行一系列的填充，包括设置当前Class为增强类的父类。当前Class的所有接口，增强类也要实现。
>          
>          4. **对Enhancer设置一些Callback，并设置固定的CallbackFilter（ProxyCallbackFilter）。**非常重要：
>          
>             ![Callback数组](img/cglib_callbacks.png)
>          
>             ​									Callback数组（每一个Callback都是方法的拦截器）
>          
>             ![Callback数组索引](img/callback_index.png)
>          
>             ​									Callback数组的索引（**ProxyCallbackFilter#accept**实现），用来确定被增强的类的每一个方法该使用具体的某个拦截器，返回的是拦截器的数组索引

#### 3.1.4 AsyncAnnotationBeanPostProcessor

用来处理注解**@org.springframework.scheduling.annotation.Async**和**@javax.ejb.Asynchronous**，用来支持异步，也是通过动态代理实现的

#### 3.1.5 ScheduledAnnotationBeanPostProcessor

用来处理**@org.springframework.scheduling.annotation.Scheduled**和**@org.springframework.scheduling.annotation.Schedules**注解的，提供定时任务支持，非动态代理实现

### 3.3 BeanFactoryPostProcessor和BeanDefinitionRegistryPostProcessor

* **ConfigurationClassPostProcessor（非常重要，一切的开始）**

功能：处理**@Configuration、@PropertySource、@ComponentScan、@Import、@ImportResource、@Bean、@Conditional**等重要注解

**@Configuration增强**：被@Configuration标注的类，被叫做**full Configuration class**，spring会对这种类进行增强，将这个类中标注了@Bean的方法进行拦截（**BeanMethodInterceptor**拦截），调用@Bean方法时会从BeanFactory中获取bean，这样就不用担心本类中调用@Bean方法而导致生成了多个bean。这个类的增强和aop增强有区别，并不是包装关系，而是完全在@Configuration标注的类的基础上，对这个Class进行代理，只有这样才能保证本类方法调用才会走代理，才会从BeanFactory中获取bean，而不用担心多创建了对象。

​		对@Configuration类和下面的注解![](img/lite-configuration-class.png)标记的类都在**org.springframework.context.annotation.ConfigurationClassParser#parse(java.util.Set<org.springframework.beans.factory.config.BeanDefinitionHolder>)**方法中（@Component同样会被解析，和@Configuration的区别就是被上面4个注解标注的类（**lite Configuration class**）不会进行ConfigurationClass的增强）

​		每一个配置类（上面5个注解标注的类）都会封装为ConfigurationClass，在对其进行递归解析。解析主要过程：

1. 判断@Condition注解并根据配置选择是否跳过
2. 处理包含元注解@Component（注解的父注解相关意思）里的所有内部类
3. 处理@PropertySource注解
4. 处理@ComponentScan和@ComponentScans注解，并对解析出来的BeanDefinition进行递归解析
   1. 扫描指定包下的所有组件时，通过路劲搜索，将指定包下的所有Class文件每个都封装为org.springframework.core.io.Resource对象（此时还没加载这个Class）。
   2. 利用ASM，将Class文件读取到内存里进行解析
   3. 判断是否能成为一个BeanDefinition（比如是否被@Component注解标注，是否被排除等等）
   4. 将合格的BeanDefinition返回
   5. 为什么要用ASM，而不是直接加载这个Class：因为在Class加载前，我们还不确定他是否会被容器管理，甚至它都不会被被使用，一个从来不被使用的Class文件，我们就没必要加载到虚拟机里了，所以利用ASM，直接解析它的字节码文件，看看是否包含指定的注解，在加载到虚拟机中
5. 处理@Import注解。这个注解就经常用在各种@Enable...前缀开头的注解里，用来导入指定的BeanDefinition来开启某种功能。**DeferredImportSelector是一种特别的Import，用来延迟导入。**在springboot种@EnableAutoConfiguration注解就是导入的实现DeferredImportSelector的类。**DeferredImportSelector专门用在最后才处理（等解析都处理完了后）**，为什么有这个，不妨设想一下，如果你要自己配置一个DataSource的bean，但容器中只允许存在一个，而springboot的DataSourceAutoConfiguration也帮你准备了一个DataSource，这时@ConditionalOnMissingBean注解可以发挥作用，但如果不是DeferredImportSelector起作用了，你就不能保证解析顺序，可能就忽略了你的DatsSource，而使用DataSourceAutoConfiguration里的了。所以重点：**我们没必要担心自动配置和我们的配置解析的顺序问题，始终都是我们配置的会先被解析，所以，我们可以放心的做一些定制来覆盖自动配置里的Bean。**
6. 处理@ImportResource注解
7. 处理带有@Bean注解的类
8. 处理第6点提到的DeferredImportSelector

等解析完后，就将@Bean注解标注的方法封装为BeanDefinition放入容器中，等待后续的实例化



## 4.重要的注解处理

### 4.1 注解事物的处理（@Transactional）

#### 4.1.1 由BeanFactoryTransactionAttributeSourceAdvisor进行代理，具体的拦截器：TransactionInterceptor

步骤

> 1. 将@Transactional注解封装为TransactionAttribute
> 2. 获取BeanFactory中的PlatformTransactionManager（可以由@Transactional指定）
> 3. 构建TransactionStatus（每个@Transactional都有一个TransactionStatus）
>    1. 获取当前线程中的ConnectionHolder，并以此判断是否时嵌套事务（ConnectionHolder不为空，就代表肯定时嵌套事务）
>    2. apply 事务的传播策略（PROPAGATION），主要逻辑就在org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction方法中
>       1. **PROPAGATION_REQUIRED**（默认）：不存在就新建，存在就加入
>       2. **PROPAGATION_SUPPORTS**：源码中并没有处理这种的传播策略。所以，它的作用是：存在事务就加入，不存在就以非事务的方式运行
>       3. **PROPAGATION_MANDATORY**：支持已存在的事务，不存在则直接抛异常
>       4. **PROPAGATION_REQUIRES_NEW**：没事务就创建一个，有事务就新建一个，这样就会存在两个事务，也就至少有两此commit或rollback。新建事务时会将原事务封装为SuspendedResourcesHolder，在重新获取一个新的数据库连接开启事务，等这部分运行完，在把SuspendedResourcesHolder复原
>       5. **PROPAGATION_NOT_SUPPORTED**：以非事务的方式运行（有事务就暂停事务，没有就啥也不做），实现和上面的差不多，只是这是不需要新建事务了
>       6. **PROPAGATION_NEVER**：强制性的不支持事务，要是当前存在事务，就直接抛异常
>       7. **PROPAGATION_NESTED**：不存在就新建，存在就新开一个嵌套的事务。（先检查**nestedTransactionAllowed，为false就抛异常了，代表不支持嵌套事务。不过默认为true**。）和PROPAGATION_REQUIRES_NEW的区别在于这是用数据库的Savepoint实现，至始至终只会存在一个事务，如果当前回滚，也只会退回到Savepoint，不会对外层的事务造成影响，如果都能提交，最终也只有一次真正的事务commit。所以，注册的TransactionSynchronization钩子函数也只会等待整个事务的结束来回调。如果不支持安全点（JTA），那实现就完全等于PROPAGATION_REQUIRES_NEW
> 4. 执行业务代码
> 5. 处理异常和commit和判断是否需要提交或回滚，并回调各种钩子函数：只有最初的事务开启者才能够真正的提交或回滚

#### 4.1.2 如何判断当前事务是否是新事物的创建者呢？（事务的创建者才具有真正的提交或回滚能力）

> org.springframework.transaction.support.DefaultTransactionStatus#newTransaction为true就代表当前@Transactional是新事物的创建者，但这不代表它运行在事务下的。因为像PROPAGATION_SUPPORTS和PROPAGATION_NOT_SUPPORTED这种传播策略，运行在非事务的状态下的话，也会创建newTransaction为true的TransactionStatus，所以还需要根据org.springframework.transaction.support.DefaultTransactionStatus#transaction是否存在来判断当前@Transactional是否真正运行在事务下，且是新事物的开启者。

1. 动态代理（@Aspect、@Transactional、@Async和xml配置等）
2. 事件发布的处理（ApplicationEventPublisher）
3. bean的作用域（session、refresh等）