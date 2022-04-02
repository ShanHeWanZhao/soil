# OpenFeign(2.1.1)

## 1. feign接口的注册

### 1.1 @EnableFeignClients

​		@EnableFeignClients使用@Import导入了FeignClientsRegistrar，@Import将在ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry方法中解析，并实例化对应import的配置类并调用对应的方法。FeignClientsRegistrar是用来设置默认的feign配置类和扫描@FeignClient，并将扫描出来带有@FeignClient注解的接口构造成FeignClientFactoryBean的BeanDefinition，注入到容器中，交给容器来实例化feign接口

**FeignClientsRegistrar主要源码解析**

```java
// ImportBeanDefinitionRegistrar接口提供的方法,用来注册BeanDefinition
@Override
public void registerBeanDefinitions(AnnotationMetadata metadata,
      BeanDefinitionRegistry registry) {
   registerDefaultConfiguration(metadata, registry);
   registerFeignClients(metadata, registry);
}
/**
* 解析@EnableFeignClients注解的defaultConfiguration字段，并将其注册为默认的feign配置类
*/
private void registerDefaultConfiguration(AnnotationMetadata metadata,
                                          BeanDefinitionRegistry registry) {
    Map<String, Object> defaultAttrs = metadata
        .getAnnotationAttributes(EnableFeignClients.class.getName(), true);

    if (defaultAttrs != null && defaultAttrs.containsKey("defaultConfiguration")) {
        String name;
        if (metadata.hasEnclosingClass()) {
            name = "default." + metadata.getEnclosingClassName();
        }
        else {
            name = "default." + metadata.getClassName();
        }
        registerClientConfiguration(registry, name,
                                    defaultAttrs.get("defaultConfiguration"));
    }
}
/**
* 解析并注册@FeignClient
*/
public void registerFeignClients(AnnotationMetadata metadata,
                                 BeanDefinitionRegistry registry) {
    // 还是使用spring提供的包扫描工具
    ClassPathScanningCandidateComponentProvider scanner = getScanner();
    scanner.setResourceLoader(this.resourceLoader);

    Set<String> basePackages;

    Map<String, Object> attrs = metadata
        .getAnnotationAttributes(EnableFeignClients.class.getName());
    // 使用注解过滤器，对应的注解为@FeignClient
    AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
        FeignClient.class);
    final Class<?>[] clients = attrs == null ? null
        : (Class<?>[]) attrs.get("clients");
    if (clients == null || clients.length == 0) { // 默认会走这
        // @EnableFeignClients的clients字段为空，就是用包扫描
        scanner.addIncludeFilter(annotationTypeFilter);
        // 默认包为使用@@EnableFeignClients注解的Class的包
        basePackages = getBasePackages(metadata);
    }
    else {
        final Set<String> clientClasses = new HashSet<>();
        basePackages = new HashSet<>();
        for (Class<?> clazz : clients) {
            basePackages.add(ClassUtils.getPackageName(clazz));
            clientClasses.add(clazz.getCanonicalName());
        }
        AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                return clientClasses.contains(cleaned);
            }
        };
        scanner.addIncludeFilter(
            new AllTypeFilter(Arrays.asList(filter, annotationTypeFilter)));
    }

    for (String basePackage : basePackages) {
        // 扫描指定包下的带有@FeignClient注解的Class，并将每个符合条件的Class封装为BeanDefinition
        Set<BeanDefinition> candidateComponents = scanner
            .findCandidateComponents(basePackage);
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                // 校验@FeignClient注解的Class只能是接口
                Assert.isTrue(annotationMetadata.isInterface(),
                              "@FeignClient can only be specified on an interface");

                Map<String, Object> attributes = annotationMetadata
                    .getAnnotationAttributes(
                    FeignClient.class.getCanonicalName());

                String name = getClientName(attributes);
                // 注册服务自定义的feign配置类
                registerClientConfiguration(registry, name,
                                            attributes.get("configuration"));
                registerFeignClient(registry, annotationMetadata, attributes);
            }
        }
    }
}
/**
	开始注册@FeignClient的BeanDefinition
*/
private void registerFeignClient(BeanDefinitionRegistry registry,
                                 AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
    String className = annotationMetadata.getClassName();
    // 很重要，使用FeignClientFactoryBean作为bean的class
    BeanDefinitionBuilder definition = BeanDefinitionBuilder
        .genericBeanDefinition(FeignClientFactoryBean.class);
    // fallback相关的校验
    validate(attributes);
    // 将@FeignClient解析出来的全部字段添加到FeignClientFactoryBean中
    definition.addPropertyValue("url", getUrl(attributes));
    definition.addPropertyValue("path", getPath(attributes));
    String name = getName(attributes);
    definition.addPropertyValue("name", name);
    String contextId = getContextId(attributes);
    definition.addPropertyValue("contextId", contextId);
    definition.addPropertyValue("type", className);
    definition.addPropertyValue("decode404", attributes.get("decode404"));
    definition.addPropertyValue("fallback", attributes.get("fallback"));
    definition.addPropertyValue("fallbackFactory", attributes.get("fallbackFactory"));
    definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

    String alias = contextId + "FeignClient";
    AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

    boolean primary = (Boolean) attributes.get("primary"); // has a default, won't be
    // null

    beanDefinition.setPrimary(primary);

    String qualifier = getQualifier(attributes);
    if (StringUtils.hasText(qualifier)) {
        alias = qualifier;
    }

    BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                                                           new String[] { alias });
    // 将BeanDefinition注入到容器中
    BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
}
```

### 1.2  NamedContextFactory -> FeignContext

​		FeignContext是构造feign实例最重要的一部分，用来对不同服务进行配置隔离（Encoder，Decoder，Contract等重要组件）。FeignContext继承了NamedContextFactory，NamedContextFactory是用来

重点部分源码解析

```java
// feign环境的配置类
public class FeignContext extends NamedContextFactory<FeignClientSpecification> {

    public FeignContext() {
        // 默认配置类指定为FeignClientsConfiguration
        super(FeignClientsConfiguration.class, "feign", "feign.client.name");
    }

}

/**
	非常重要的东西，专为不同服务的配置做隔离的
*/
public abstract class NamedContextFactory<C extends NamedContextFactory.Specification>
    implements DisposableBean, ApplicationContextAware {

    private final String propertySourceName;

    private final String propertyName;
    // 服务和配置容器的map缓存
    private Map<String, AnnotationConfigApplicationContext> contexts = new ConcurrentHashMap<>();

    private Map<String, C> configurations = new ConcurrentHashMap<>();
    // 作为各个服务的配置容器的父容器（该字段就是我们程序中的主容器）
    private ApplicationContext parent;
    // 默认配置类的类型
    private Class<?> defaultConfigType;

    public NamedContextFactory(Class<?> defaultConfigType, String propertySourceName,
                               String propertyName) {
        this.defaultConfigType = defaultConfigType;
        this.propertySourceName = propertySourceName;
        this.propertyName = propertyName;
    }
    
    /**
    	destory方法，负责关闭所有配置类容器
    */
    @Override
    public void destroy() {
        Collection<AnnotationConfigApplicationContext> values = this.contexts.values();
        for (AnnotationConfigApplicationContext context : values) {
            // This can fail, but it never throws an exception (you see stack traces
            // logged as WARN).
            context.close();
        }
        this.contexts.clear();
    }
    
	/**
		获取指定服务的配置容器（没有就会新建）
	*/
    protected AnnotationConfigApplicationContext getContext(String name) {
        if (!this.contexts.containsKey(name)) {
            synchronized (this.contexts) {
                if (!this.contexts.containsKey(name)) {
                    this.contexts.put(name, createContext(name));
                }
            }
        }
        return this.contexts.get(name);
    }

    /**
    	对指定的服务创建专属的配置容器，并刷新容器
    	
    	其容器中配置类的优先级为: 指定服务的配置类 > 指定默认的配置类 > spring提供的默认配置类
    	指定服务的配置类只会对当前服务生效，指定的和spring提供的默认配置类对所有服务生效
    	
    	1.对于feign来说，上面三个的配置类依次在
    	@FeignClient的configuration指定
    	@EnableFeignClients的defaultConfiguration指定
    	FeignContext构造器中指定的FeignClientsConfiguration
    	
    	2.对于ribbon来说，上面三个的配置类依次在
    	@RibbonClient的configuration指定
    	@RibbonClients的defaultConfiguration指定
    	SpringClientFactory构造器中指定的RibbonClientConfiguration
    	
    */
    protected AnnotationConfigApplicationContext createContext(String name) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 对服务创建专属的配置类容器，注册的配置类顺序非常重要，表示了一种优先级
        // 如果服务存在指定的配置类，注册指定配置类
        if (this.configurations.containsKey(name)) {
            for (Class<?> configuration : this.configurations.get(name)
                 .getConfiguration()) {
                context.register(configuration);
            }
        }
        // 存在指定的默认配置类，随后再注册这个指定的默认配置类
        for (Map.Entry<String, C> entry : this.configurations.entrySet()) {
            if (entry.getKey().startsWith("default.")) {
                for (Class<?> configuration : entry.getValue().getConfiguration()) {
                    context.register(configuration);
                }
            }
        }
        // 最后在注册spring自动配置自己提供的配置类
        context.register(PropertyPlaceholderAutoConfiguration.class,
                         this.defaultConfigType);
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
            this.propertySourceName,
            Collections.<String, Object>singletonMap(this.propertyName, name)));
        // 将项目的主容器作为当前服务配置容器的父容器
        if (this.parent != null) {
            context.setParent(this.parent);
            context.setClassLoader(this.parent.getClassLoader());
        }
        context.setDisplayName(generateDisplayName(name));
        // 刷新环境
        context.refresh();
        return context;
    }

    protected String generateDisplayName(String name) {
        return this.getClass().getSimpleName() + "-" + name;
    }
    
	/**
	 	获取指定服务的某个配置类实例
	*/
    public <T> T getInstance(String name, Class<T> type) {
        AnnotationConfigApplicationContext context = getContext(name);
        if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context,
                                                                type).length > 0) {
            return context.getBean(type);
        }
        return null;
    }


    public <T> Map<String, T> getInstances(String name, Class<T> type) {
        AnnotationConfigApplicationContext context = getContext(name);
        if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context,
                                                                type).length > 0) {
            return BeanFactoryUtils.beansOfTypeIncludingAncestors(context, type);
        }
        return null;
    }


}
```

### 1.3 FeignClientsConfiguration

作为spring为我们提供的feign默认配置类，它内部提供了如下组件

- **Encoder**：feign的编码器，**默认为SpringEncoder**。负责将参数编码为http请求需要的数据

- **Decoder**：feign的解码器，默认为包装后的SpringDecoder，内部使用spring的HttpMessageConverters。负责将http响应数据解码。

- **Contract**：feign接口的方法解析器，默认为SpringMvcContract。负责对feign接口方法（包括类、方法和方法参数的注解）上的注解进行解析。

- **Retryer**：feign的重试器，**默认为feign.Retryer#NEVER_RETRY**，表示不使用feign提供的重试机制，因为spring有自己配置的重试。当请求失败后，负责进行请求的重试，内部定义了重试次数和重试间隔时间等。

- **FeignLoggerFactory**：日志工厂，默认为DefaultFeignLoggerFactory，内部默认使用Slf4j

- **Feign.Builder**：feign实体的构造器，内部定义了构造feign实例需要的全部组件，内容和作用如下。

  ```java
  // feign请求的拦截器
  private final List<RequestInterceptor> requestInterceptors =
      new ArrayList<RequestInterceptor>();
  // feign调用的日志等级配置
  private Logger.Level logLevel = Logger.Level.NONE;
  private Contract contract = new Contract.Default();
  // feign的客户端，负责真正的执行http请求
  private Client client = new Client.Default(null, null);
  private Retryer retryer = new Retryer.Default();
  private Logger logger = new NoOpLogger();
  private Encoder encoder = new Encoder.Default();
  private Decoder decoder = new Decoder.Default();
  private QueryMapEncoder queryMapEncoder = new QueryMapEncoder.Default();
  private ErrorDecoder errorDecoder = new ErrorDecoder.Default();
  // socket参数（连接超时时间和读超时时间等）
  private Options options = new Options();
  // feign实例使用了jdk代理创建
  // jdk代理的InvocationHandler工厂类（内部负责创建FeignInvocationHandler）
  private InvocationHandlerFactory invocationHandlerFactory =
      new InvocationHandlerFactory.Default();
  // 是否解析404，默认false
  private boolean decode404;
  // http响应数据解析完毕后是否关闭socket，默认为true
  private boolean closeAfterDecode = true;
  // 异常策略
  private ExceptionPropagationPolicy propagationPolicy = NONE;
  ```

### 1.4 FeignClientFactoryBean的实例化

​		每一个@FeignClient的接口都对应一个FeignClientFactoryBean，在容器refresh的最后阶段开始实例化，FeignClientFactoryBean是FactoryBean，实例化内部对象使用getObject方法

#### FeignClientFactoryBean的getObject方法

```java
/**
	获取feign实例
*/
@Override
public Object getObject() throws Exception {
    return getTarget();
}
<T> T getTarget() {
    // 获取当前容器中的FeignContext(这个是被FeignAutoConfiguration自动导入的)
    FeignContext context = this.applicationContext.getBean(FeignContext.class);
	// 用容器中配置的feign组件构建feign实例化的Builder
    // 包括：SpringEncoder、SpringDecoder、SpringMvcContract、RequestInterceptor等等
    Feign.Builder builder = feign(context);

    if (!StringUtils.hasText(this.url)) { // url不存在，就是用负载均衡选择服务来构建feign实例
        if (!this.name.startsWith("http")) { // http协议请求
            this.url = "http://" + this.name;
        }
        else {
            this.url = this.name;
        }
        this.url += cleanPath(); // 添加path
        return (T) loadBalance(builder, context,
                               new HardCodedTarget<>(this.type, this.name, this.url));
    }
    // url存在，直接拿到真实的Client，构建不使用负载均衡选择服务的feign实例
    if (StringUtils.hasText(this.url) && !this.url.startsWith("http")) {
        this.url = "http://" + this.url;
    }
    String url = this.url + cleanPath();
    Client client = getOptional(context, Client.class);
    if (client != null) {
        if (client instanceof LoadBalancerFeignClient) {
            // not load balancing because we have a url,
            // but ribbon is on the classpath, so unwrap
            client = ((LoadBalancerFeignClient) client).getDelegate();
        }
        builder.client(client);
    }
    Targeter targeter = get(context, Targeter.class);
    return (T) targeter.target(this, builder, context,
                               new HardCodedTarget<>(this.type, this.name, url));
}
/**
	获取服务指定的Client
	
	默认为LoadBalancerFeignClient，被FeignRibbonClientAutoConfiguration所导入，这是
	一种没有使用apache http client或okhttp的默认策略。LoadBalancerFeignClient内部
	包装了一个feign.Client.Default，用来真正执行http请求（内部使用了HttpURLConnection）
*/
protected <T> T loadBalance(Feign.Builder builder, FeignContext context,
                            HardCodedTarget<T> target) {
    Client client = getOptional(context, Client.class);
    if (client != null) {
        builder.client(client);
        // 在不使用hystrix情况下，为DefaultTargeter
        Targeter targeter = get(context, Targeter.class);
        return targeter.target(this, builder, context, target);
    }

    throw new IllegalStateException(
        "No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-netflix-ribbon?");
}

/**
	从指定服务（contextId）的配置容器中拿指定type的bean（如果没有还会搜索父容器）
*/
protected <T> T getOptional(FeignContext context, Class<T> type) {
    return context.getInstance(this.contextId, type);
}
/**
	构建当前服务的Feign.Builder
*/
protected Feign.Builder feign(FeignContext context) {
    FeignLoggerFactory loggerFactory = get(context, FeignLoggerFactory.class);
    Logger logger = loggerFactory.create(this.type);

    // @formatter:off
    Feign.Builder builder = get(context, Feign.Builder.class)
        // required values
        .logger(logger)
        .encoder(get(context, Encoder.class)) // 默认SpringEncoder
        .decoder(get(context, Decoder.class)) // 默认SpringDecoder
        .contract(get(context, Contract.class)); // 默认SpringMvcContract
    // @formatter:on
	// 配置容器中自定义的feign组件，包括RequestInterceptor等等
    configureFeign(context, builder);

    return builder;
}
```

#### DefaultTargeter（不使用hystrix的情况）

```java
@Override
public <T> T target(FeignClientFactoryBean factory, Feign.Builder feign,
      FeignContext context, Target.HardCodedTarget<T> target) {
   return feign.target(target);
}
public <T> T target(Target<T> target) {
    return build().newInstance(target);
}

public Feign build() {
    // feign接口方法对应的代理方法工厂
    SynchronousMethodHandler.Factory synchronousMethodHandlerFactory =
        new SynchronousMethodHandler.Factory(client, retryer, requestInterceptors,logger,logLevel, decode404, closeAfterDecode, propagationPolicy);
    
    ParseHandlersByName handlersByName =
        new ParseHandlersByName(contract, options, encoder, decoder, queryMapEncoder,errorDecoder, synchronousMethodHandlerFactory);
    
    return new ReflectiveFeign(handlersByName, invocationHandlerFactory, queryMapEncoder);
}

/**
	创建feign实例
*/
public <T> T newInstance(Target<T> target) {
    // 解析feign接口，并对feign方法解析出对应的feign实例方法执行处理器
    Map<String, MethodHandler> nameToHandler = targetToHandlersByName.apply(target);
    Map<Method, MethodHandler> methodToHandler = new LinkedHashMap<Method, MethodHandler>();
    List<DefaultMethodHandler> defaultMethodHandlers = new LinkedList<DefaultMethodHandler>();

    for (Method method : target.type().getMethods()) {
        if (method.getDeclaringClass() == Object.class) { // Object上的方法
            continue;
        } else if (Util.isDefault(method)) { // jdk8以上接口的default方法
            DefaultMethodHandler handler = new DefaultMethodHandler(method);
            defaultMethodHandlers.add(handler);
            methodToHandler.put(method, handler);
        } else { // 普通接口方法
            methodToHandler.put(method, nameToHandler.get(Feign.configKey(target.type(), method)));
        }
    }
    // 利用解析出来的MethodHandler创建jdk代理的调用类
    InvocationHandler handler = factory.create(target, methodToHandler);
    // jdk代理生成feign实例
    T proxy = (T) Proxy.newProxyInstance(target.type().getClassLoader(),
                                         new Class<?>[] {target.type()}, handler);

    for (DefaultMethodHandler defaultMethodHandler : defaultMethodHandlers) {
        defaultMethodHandler.bindTo(proxy);
    }
    return proxy;
}
```

**ParseHandlersByName#apply方法解析**

```java

public Map<String, MethodHandler> apply(Target key) {
    // 利用Contract（这里是SpringMvcContract）解析feign接口方法
    List<MethodMetadata> metadata = contract.parseAndValidatateMetadata(key.type());
    Map<String, MethodHandler> result = new LinkedHashMap<String, MethodHandler>();
    for (MethodMetadata md : metadata) {
        BuildTemplateByResolvingArgs buildTemplate;
        if (!md.formParams().isEmpty() && md.template().bodyTemplate() == null) {
            // 方法是form类型的
            buildTemplate = new BuildFormEncodedTemplateFromArgs(md, encoder, queryMapEncoder);
        } else if (md.bodyIndex() != null) { // 参数会使用http请求体体
            buildTemplate = new BuildEncodedTemplateFromArgs(md, encoder, queryMapEncoder);
        } else {
            buildTemplate = new BuildTemplateByResolvingArgs(md, queryMapEncoder);
        }
        result.put(md.configKey(),
                   factory.create(key, md, buildTemplate, options, decoder, errorDecoder));
    }
    return result;
}
/**
	feign.SynchronousMethodHandler.Factory#create方法
	创建feign接口方法对应的代理执行方法
*/
public MethodHandler create(Target<?> target,
                            MethodMetadata md,
                            RequestTemplate.Factory buildTemplateFromArgs,
                            Options options,
                            Decoder decoder,
                            ErrorDecoder errorDecoder) {
    return new SynchronousMethodHandler(target, client, retryer, requestInterceptors, logger,logLevel, md, buildTemplateFromArgs, options, decoder,errorDecoder, decode404, closeAfterDecode, propagationPolicy);
}
```

#### BaseContract

​		BaseContract是feign提供解析接口相关参数的抽象类，由子类去实现它以解析feign接口、方法、参数的注解。在和spring整合里，实现类为SpringMvcContract

```java
/**
	feign.Contract.BaseContract#parseAndValidateMetadata方法源码
*/
protected MethodMetadata parseAndValidateMetadata(Class<?> targetType, Method method) {
    MethodMetadata data = new MethodMetadata();
    // 解析返回值类型
    data.returnType(Types.resolve(targetType, targetType, method.getGenericReturnType()));
    data.configKey(Feign.configKey(targetType, method));

    // 解析Class上的注解，交给子类（SpringMvcContract）去实现
    if (targetType.getInterfaces().length == 1) {
        processAnnotationOnClass(data, targetType.getInterfaces()[0]);
    }
    processAnnotationOnClass(data, targetType);

	// 解析方法上的注解
    for (Annotation methodAnnotation : method.getAnnotations()) {
        processAnnotationOnMethod(data, methodAnnotation, method);
    }
    checkState(data.template().method() != null,
               "Method %s not annotated with HTTP method type (ex. GET, POST)",
               method.getName());
    Class<?>[] parameterTypes = method.getParameterTypes();
    Type[] genericParameterTypes = method.getGenericParameterTypes();
	// 获取方法上的注解，准备开始解析
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    int count = parameterAnnotations.length;
    for (int i = 0; i < count; i++) {
        boolean isHttpAnnotation = false;
        if (parameterAnnotations[i] != null) { // 参数注解不为空才解析
            // 让子类自己判断注解是否是http相关（不是http相关注解将会设为请求体参数）
            isHttpAnnotation = processAnnotationsOnParameter(data, parameterAnnotations[i], i);
        }
        if (parameterTypes[i] == URI.class) { // 支持URI参数转为url
            data.urlIndex(i);
        } else if (!isHttpAnnotation) { // 参数上没有注解或不是http注解
            // 校验body参数的唯一性
            checkState(data.formParams().isEmpty(),
                       "Body parameters cannot be used with form parameters.");
            checkState(data.bodyIndex() == null, "Method has too many Body parameters: %s", method);
            // 设置http 请求体参数
            data.bodyIndex(i);
            data.bodyType(Types.resolve(targetType, targetType, genericParameterTypes[i]));
        }
    }
	// headerMap不为空，校验该参数必须为Map，且key为String类型
    if (data.headerMapIndex() != null) {
        checkMapString("HeaderMap", parameterTypes[data.headerMapIndex()],
                       genericParameterTypes[data.headerMapIndex()]);
    }
	// queryMap不为空，且如果为Map类型，校验Map的key为String类型
    if (data.queryMapIndex() != null) {
        if (Map.class.isAssignableFrom(parameterTypes[data.queryMapIndex()])) {
            checkMapKeys("QueryMap", genericParameterTypes[data.queryMapIndex()]);
        }
    }

    return data;
}
```

##### SpringMvcContract

​		是spring提供的解析spring mvc相关注解的解析器，继承了feign.Contract.BaseContract，重写了BaseContract类的三个解析接口方法（非别对应类、方法、和方法参数的注解解析）。**将注解解析后的结果填充到MethodMetadata里和内部的RequestTemplate里**

- processAnnotationOnClass：解析feign接口方法对应的Class，支持@RequestMapping注解
- processAnnotationOnMethod：解析feign接口方法上的注解，也支持@RequestMapping注解
- processAnnotationsOnParameter：解析feign接口方法的参数相关注解，内部使用AnnotatedParameterProcessor解析器来解析具体的方法参数注姐，spring提供的默认AnnotatedParameterProcessor如下
  - PathVariableParameterProcessor：解析@PathVariable注解
  - RequestParamParameterProcessor：解析@RequestParam注解。拥有该注解的方法参数支持Map格式
  - RequestHeaderParameterProcessor：解析@RequestHeader注解。拥有该注解的方法参数也支持Map格式（key只能为String）
  - QueryMapParameterProcessor：解析@SpringQueryMap注解

没有被上面4个AnnotatedParameterProcessor解析到的方法参数就是非http注解相关参数，会将该参数设置为http请求体参数，同时会校验唯一性

###  1.5 feign.MethodMetadata

MethodMetadata是feign提供的feign接口方法解析的结果，每个feign接口对应一个MethodMetadata

MethodMetadata字段说明

```java
public final class MethodMetadata implements Serializable {

    private static final long serialVersionUID = 1L;
    // 该feign接口方法的唯一标识符（如果有重载，会组合上参数）
    private String configKey;
    // feign接口方法的返回类型
    private transient Type returnType;
    // 当方法中参数有java.net.URI时，代表该参数的索引位置。会用来组合到请求url上
    private Integer urlIndex;
    // 请求体参数的索引值(非http注解标记的参数)
    private Integer bodyIndex;
    // 使用SpringMvcContract解析时，有@RequestHeader注解，且对应的参数为Map格式的索引
    private Integer headerMapIndex;
    // 使用SpringMvcContract解析时，@RequestParam或@SpringQueryMap注解标记的参数索引
    // 但由于只能存在一个，所以会覆盖掉前一个
    private Integer queryMapIndex;
    // 使用SpringMvcContract解析时，@SpringQueryMap的encoded字段值
    private boolean queryMapEncoded;
    // 上面bodyIndex对应参数的类型
    private transient Type bodyType;
    // 请求模板信息
    // 包括：请求头、url信息、请求方法等等
    private RequestTemplate template = new RequestTemplate();
    private List<String> formParams = new ArrayList<String>();
    private Map<Integer, Collection<String>> indexToName =
        new LinkedHashMap<Integer, Collection<String>>();
    private Map<Integer, Class<? extends Expander>> indexToExpanderClass =
        new LinkedHashMap<Integer, Class<? extends Expander>>();
    private Map<Integer, Boolean> indexToEncoded = new LinkedHashMap<Integer, Boolean>();
    private transient Map<Integer, Expander> indexToExpander;
}
```

### 1.6 feign代理的总结

​		通过使用@EnableFeignClients默认扫描使用@EnableFeignClients类包下的所有@FeignClient接口，并将每个feign接口封装为FeignClientFactoryBean放入容器中，等待后续实例化。

​		FeignClientFactoryBean实例化时，会获取各自服务的配置容器中的feign实例组件，如果没有则会使用spring默认提供的FeignClientsConfiguration，这些组件包括Encoder、Decoder、Contract、Retryer等等。

​		feign的实例化是使用jdk动态代理。feign.ReflectiveFeign.ParseHandlersByName内负责解析feign接口，在内部通过Contract解析每个feign方法，每个feign方法被解析成feign.MethodMetadata模板。feign.SynchronousMethodHandler.Factory再将feign需要的组件和上一步生成的MethodMetadata构造为SynchronousMethodHandler，表示一个feign方法的处理器。当调用feign接口方法时，实际上就是在调用这个方法对应的SynchronousMethodHandler

# Ribbon(2.1.1)



# Feign + Ribbon 的调用

