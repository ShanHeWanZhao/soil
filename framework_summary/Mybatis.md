# 1. Mybatis（3.5.8）

​		org.apache.ibatis.session.Configuration是mybatis最重要的东西，几乎封装了所有mybatis需要的东西，mybatis在解析配置文件过程中，也是把解析出来的各自结果封装并放入到Configuration里

## 1.1 Mybatis解析过程

### 1.1.1 XMLConfigBuilder

**XMLConfigBuilder#parseConfiguration**是解析xml配置的入口

```java
private void parseConfiguration(XNode root) {
  try {

    propertiesElement(root.evalNode("properties"));
    // Configuration的全局设置
    // 解析settings节点，并将得到的值通过set方法赋值到Configuration类中（如果Configuration类中不存在该属性会直接抛异常）
    Properties settings = settingsAsProperties(root.evalNode("settings"));
    loadCustomVfs(settings);
    loadCustomLogImpl(settings);
    // 注册别名
    typeAliasesElement(root.evalNode("typeAliases"));
    // 注册插件
    pluginElement(root.evalNode("plugins"));
    objectFactoryElement(root.evalNode("objectFactory"));
    objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
    reflectorFactoryElement(root.evalNode("reflectorFactory"));
    settingsElement(settings);
    // read it after objectFactory and objectWrapperFactory issue #631
    // 解析environment（和数据源相关）
    environmentsElement(root.evalNode("environments"));
    databaseIdProviderElement(root.evalNode("databaseIdProvider"));
    // 解析typeHandlers节点（类型转换器，解释了java中的对象数据如何转为数据库中某个指定的数据）
    typeHandlerElement(root.evalNode("typeHandlers"));
    // 解析xml的mapper
    mapperElement(root.evalNode("mappers"));
  } catch (Exception e) {
    throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
  }
}
```



### 1.1.2 MetaClass和DefaultReflectorFactory

​		这两个类配合使用，利用反射用来解析一个class有哪些字段、set方法、get方法、无参构造器的，最终一个class的元信息被封装成Reflector对象，并将Reflector缓存在Configuration.reflectorFactory.reflectorMap字段里。**当sql语句执行后，需要将返回的信息封装到我们指定的对象里时，Reflector就会起作用**

​		DefaultReflectorFactory解析get方法和set方法还是很简单和原始的，就是将一个class的所有Method拿到，依次对每个Method进行过滤（比如get方法：只要方法无参且方法名为get开头且长度大于3等），再将过滤后的Method抽象成Invoker，提供一个公共的方法来执行，最后将其缓存就行

### 1.1.3 typeAliases

mybatis提供的别名和实际class的映射功能，在mapper.xml里用在parameterType和resultType标签里。

​		每一对别名都会通过Configuration#typeAliasRegistry进行缓存，查看Configuration和TypeAliasRegistry的构造方法可以发现，mybatis为我们默认配置了许多别名对：包括基本数据类型、基本类型的包装数据类型、基本数据类型的数组形式、mybatis内部提供的一些工具（缓存器、日志实现类、数据源工厂等）等等

### 1.1.3 typeHandlers

typeHandlers提供了jdbc数据类型和java数据类型互相转化的功能，handler都缓存在TypeHandlerRegistry里

TypeHandlerRegistry的构造里就为我们默认添加了很多类型转换器，所以很多数据类型可以直接转换

**TypeHandlerRegistry关键字段：**

```java

private final Map<JdbcType, TypeHandler<?>>  jdbcTypeHandlerMap = new EnumMap<>(JdbcType.class);
/**
 * key为java类型，value为一个map，jdbc类型和对应的TypeHandler<p/>
 * 因为一个java类型可能对于很多的jdbc类型（例如String），所以需要为每个jdbc类型创建对应的TypeHandler
 */
private final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = new ConcurrentHashMap<>();
private final TypeHandler<Object> unknownTypeHandler;
private final Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = new HashMap<>();

private static final Map<JdbcType, TypeHandler<?>> NULL_TYPE_HANDLER_MAP = Collections.emptyMap();
// 默认的枚举类型转换器
private Class<? extends TypeHandler> defaultEnumTypeHandler = EnumTypeHandler.class;
```

### 1.1.5 解析mapper.xml

重点说下insert、update、select、delete标签的解析：

> 方法：org.apache.ibatis.scripting.LanguageDriver#createSqlSource(org.apache.ibatis.session.Configuration, org.apache.ibatis.parsing.XNode, java.lang.Class<?>)
>
> 默认使用的实现类：org.apache.ibatis.scripting.xmltags.XMLLanguageDriver
>
> ​		这些标签因为里面也可以嵌套很多标签（比如where、if、foreach等）,在解析时将每种标签都封装成对应的SqlNode结构，最终再按顺序放入MixedSqlNode里，最终按是否动态sql封装成SqlSource结构（存在${}占位符或一些动态标签（如if、choose等）的就是动态sql，需要在运行时替换指定参数。动态sql对于的SqlSource是DynamicSqlSource，非动态就是RawSqlSource）
>
> ​		SqlSource就是sql转化的工具，方法getBoundSql用于解析sql并将参数设置到sql里，它返回的BoundSql就是sql语句的抽象，封装了这个sql需要执行的任何东西
>
> BoundSql字段：
>
> ```java
> // 一个完整的 SQL 语句，可能会包含问号 ? 占位符
> private final String sql;
> // 参数映射列表，SQL 中的每个 #{xxx} 占位符都会被解析成对应的 ParameterMapping 对象
> private final List<ParameterMapping> parameterMappings;
> // 运行时参数，即用户传入的参数
> private final Object parameterObject;
> // 附加参数集合，用于存储一些额外的信息，比如 datebaseId 等
> private final Map<String, Object> additionalParameters;
> // additionalParameters 的元信息对象
> private final MetaObject metaParameters;
> ```

### 1.1.5 ${} 和#{}占位符区别

​		**#{}**解析入口源码在**org.apache.ibatis.builder.SqlSourceBuilder#parse**方法里，占位符对应的处理器是**ParameterMappingTokenHandler**。

​		**${}**解析入口源码在**org.apache.ibatis.scripting.xmltags.TextSqlNode#apply**，占位符对应的处理器是**BindingTokenParser**

​		按sql顺序解析占位符，#{}会最终替换成 “?” 号，并且每一个#{}都会解析为一个ParameterMapping的数据结构，按sql中#{}的顺序放入List中，最终放在**org.apache.ibatis.mapping.BoundSql#parameterMappings**里。最终在ParameterHandler里发挥作用，在构建PreparedStatement时，利用ParameterMapping里对应的TypeHandler，进行对应的参数设置，可以防止sql注入

​		sql里${}占位符会直接被参数替换，不能防sql注入

## 1.2 一二级缓存

​		mybatis只支持二级缓存可选择是否打开，一级缓存是一定存在的

​		二级缓存是全局的，是根据mapper.xml配置的cache标签来实现的，Configuration在创建Executor是会根据cacheEnabled来决定是否使用CachingExecutor对原本的Executor进行包装。而一级缓存则是SqlSession级别的，一个SqlSession使用一个缓存，不会有多线程问题

缓存是一种key-value的形式，key是mybatis提供的CacheKey，而value就是返回值。

```txt
CacheKey：CacheKey受多种参数控制，包括：
    1. MappedStatement的命名空间（就是mapper.xml里的namesapce+ '.' + 语句的id）
    2. 解析后sql语句（包括 ? ）
    3. 运行时的参数
CacheKey通过这些参数计算自己的hashcode和checksum，在覆盖equals方法会这些参数进行校验，这样就能保证CacheKey唯一对应一种查询
```

### 1.2.1 二级缓存

​	二级缓存和语句的命名空间绑定，mapper.xml里每个select、update、insert、datele语句都会解析成MappedStatement对象（包括id，语句类型，缓存等），当CachingExecutor在执行查询时，首先会通过当前MappedStatement里的cache来查询当前查询语句是否存在缓存，如果缓存存在，而将其返回。如果不存在，则向下查询（一级缓存或数据库），再将结果缓存起来并返回

​	二级缓存由于时绑定在命名空间上的，所以完全可能出现不同的事务访问同一个缓存并更新的情况，mybatis提供的解决方法：

> ​		每个CachingExecutor里都有TransactionalCacheManager，用来解决不同事物可能造成的缓存脏读问题
>
> ​		在TransactionalCacheManager里面，每个原始Cache对应一个TransactionalCache，由TransactionalCache装饰原始缓存，为 Cache 增加事务功能，为了解决不同事务之间缓存导致的脏读。
>
> TransactionalCacheManager字段
>
> ```java
> // 真正的Cache，查询缓存都从这里获取
> private final Cache delegate;
> private boolean clearOnCommit;
> // 在事务被提交前，所有从数据库中查询的结果将缓存在此集合中
> private final Map<Object, Object> entriesToAddOnCommit;
> // 在事务被提交前，当缓存未命中时，CacheKey 将会被存储在此集合中
> private final Set<Object> entriesMissedInCache;
> ```
>
> ​		**当查询缓存时，还是都从delegate里获取。而当放入缓存时，都放在entriesToAddOnCommit里，等待事务提交后，才会把entriesToAddOnCommit里的数据放入真正的缓存delegate中，避免脏读的产生。而当事务回滚时，清除entriesToAddOnCommit和entriesMissedInCache就行**

### 1.2.2 一级缓存

​		一级缓存不用担心锁和事务的问题，因为时SqlSession范围的，实现在BaseExecutor.localCache里。

​		当进行查询时，通过CachaKey在BaseExecutor.localCache的查找是否存在缓存，存在则直接返回结果，不存在就向下查询数据库，再将查询结果缓存到一级缓存里

​		**事务的提交、回滚或当前语句时insert、update、delete语句时，都会清空一级缓存**

## 1.3 插件机制

​		**每一个自定义的插件都需要实现org.apache.ibatis.plugin.Interceptor接口，并在实现类上使用@org.apache.ibatis.plugin.Intercepts注解指定拦截的方法**

### 1.3.1 拦截Executor

​		在**org.apache.ibatis.session.Configuration#newExecutor**方法里，最后通过org.apache.ibatis.plugin.InterceptorChain#pluginAll方法对Executor应用上拦截器，这时拦截器的@Intercepts注解里的@Signature注解就需要指定Executor.class和对应的方法和参数，来拦截Executor方法

​		拦截器的应用是使用jdk的动态代理实现的，一个方法具有多个拦截器的话会进行插件的层层嵌套，如果当前方法能够被拦截，就直接运行Interceptor的方法，如果还想运行拦截方法，Invocation里的process可以直接调用。且Invocation里的target就是对应的Executor实现对象

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  try {
    // 从signatureMap中获取拦截方法的类的所有被拦截方法，存在就执行Interceptor逻辑，不存在就跳过
    Set<Method> methods = signatureMap.get(method.getDeclaringClass());
    if (methods != null && methods.contains(method)) {
      return interceptor.intercept(new Invocation(target, method, args));
    }
    return method.invoke(target, args);
  } catch (Exception e) {
    throw ExceptionUtil.unwrapThrowable(e);
  }
}
```

**使用案例**：mybatis-plus里的乐观锁拦截器

​	因为任何一个更新语句都会走org.apache.ibatis.executor.Executor#update这个方法，要实现乐观锁机制只需拦截这个方法，对update语句拼凑乐观锁字段就可以实现

### 1.3.2 拦截ParameterHandler

​	**ParameterHandler是参数处理器接口，用于获取运行时sql参数和将参数设置到PreparedStatement上。**

​	对ParameterHandler进行拦截是在org.apache.ibatis.session.Configuration#newParameterHandler方法里（也是创建ParameterHandler的方法，创建后马上拦截），实现如上

### 1.3.3 拦截ResultSetHandler

**ResultSetHandler是结果集处理器，用于将sql返回的ResultSet封装到我们指定的对象中**

对ResultSetHandler进行拦截是在org.apache.ibatis.session.Configuration#newResultSetHandler方法里

### 1.3.4 拦截StatementHandler

**StatementHandler用于处理Statement，包括设置查询的超时时间，查询行数，和应用KeyGenerator以返回自增主键等信息**

对StatementHandler进行拦截是在org.apache.ibatis.session.Configuration#newStatementHandler方法里

**使用案例：**mybatis-plus里的分页插件

​	通过拦截StatementHandler的prepare方法，在sql语句执行之前构建count查询的sql语句，然后执行这个count查询语句，再通过结果来判断是否需要组装原sql语句继续执行分页查询结果

## 1.4 sql执行流程

- 通过SqlSessionFactory创建一个DefaultSqlSession
- SqlSession获取Mapper对象：通过org.apache.ibatis.session.Configuration#getMapper方法创建一个由jdk动态代理实现的mapper对象，而InvocationHandler实现类则是MapperProxy
- 由于mapper对象是动态代理生成的，所以调用mapper里的任何接口都会走到**org.apache.ibatis.binding.MapperProxy#invoke**方法
- 对当前方法解析并封装成MapperMethod对象然后缓存起来，调用execute方法执行
- **select类型的方法都会走Executor#query方法**，Executor#query方法逻辑
  - 查询二级缓存，命中就返回缓存，没有则向下继续查询
  - 查询一级缓存，命中就返回缓存，没有则向下继续查询（走到**BaseExecutor#doQuery**方法）
  - 根据实现类查询，默认是SimpleExecutor的doQuery方法
  - 创建StatementHandler，利用TypeHandler#setParameter方法将参数设置到PreparedStatement里
  - 执行PreparedStatement
  - 通过ResultSetHandler处理ResultSet，应用TypeHandler#getResult方法来将返回值映射到javaBean里
  - 返回List
- **update、delete、insert类型的方法都会走Executor#update方法**，Executor#update方法逻辑
  - 清除所有一级缓存（因为并不知道这个update会更新哪些数据，所有直接清理所有一级缓存更保险点）
  - 创建StatementHandler，利用TypeHandler#setParameter方法将参数设置到PreparedStatement里
  - 执行PreparedStatement
  - 通过KeyGenerator#processAfter方法（如果设置了useGeneratedKeys那么默认实现类是Jdbc3KeyGenerator，如果没有设置useGeneratedKeys实现类则是NoKeyGenerator）设置插入生成的自增主键
  - 返回update操作影响的行数（可依此来判断操作是否成功）

到此则一条sql的执行过程就完全分析完了

# 2. Mybatis + Spring整合（2.0.7）

mybatis整合到spring后需要解决了什么问题：

- java的mapper注册问题：单mybatis获取mapper都是通过SqlSession获取，但spring管理后mapper怎么保持单例
- 事务问题：如何支持spring的事务注解和配置

## 2.1 mapper如何注册

### 2.1.1 @MapperScan注解注册mapper bean

> ​	@MapperScan会导入MapperScannerRegistrar，MapperScannerRegistrar又会注册MapperScannerConfigurer，并将@MapperSacn注解设置的属性应用进去
>
> ​	MapperScannerConfigurer实现了BeanDefinitionRegistryPostProcessor接口，就知道MapperScannerConfigurer也是用来注册BeanDefinition的
>
> ​	MapperScannerConfigurer的postProcessBeanDefinitionRegistry方法里又注册了ClassPathMapperScanner，ClassPathMapperScanner专门用来扫描包路径的class文件并按配置决定是否注册这个class的BeanDefinition到bean容器中，等待spring的实例化
>
> ​	ClassPathMapperScanner重写了doScan方法，将扫描出来的所有BeanDefinitionHolder进行进一步的处理（主要是设置**beanClass为MapperFactoryBean**，将@MapperScan里设置的sqlSessionTemplateRef或sqlSessionFactoryRef设置到MapperFactoryBean）
>
> ​	MapperFactoryBean又是个FactoryBean，查看其getObject方法便可知道mapper bean 和mybatis创建的mapper基本一样，不过SqlSession的实现必须要用SqlSessionTemplate

### 2.2.2 为什么要使用SqlSessionTemplate？

​		mapper归spring管理后就是单例模式了，无法再和以前一样每个请求来临，通过SqlSession创建Mapper，而一个SqlSession的有效期就是一次事务。通过查看SqlSessionTemplate内部，可以发现所有的实现都转发给了sqlSessionProxy，而sqlSessionProxy是一个SqlSession的动态代理，拦截器为SqlSessionInterceptor，拦截器内部还是保持了SqlSession的事务隔离性，一个事务使用一个DefaultSqlSession，同时，和数据库打交道的时mybatis，但数据源又是spring管理的，所以还需要从spring中获取数据源，来解决spring的事务管理

## 2.2 事务解决

​	SpringManagedTransactionFactory实现了TransactionFactory，在构建mybatis的Configuration时，默认就会将SpringManagedTransactionFactory设在org.apache.ibatis.mapping.Environment里，再将这个Environment设置在Configuration里

​	**spring提供了一个工具叫DataSourceUtils，专门用来获取给定的DatsSource里的事务Connection（如果当前存在spring事务，就获取事务的Connection，没有才会利用给的的DataSource新建）**。所以，在SpringManagedTransaction里获取的Connection都是这么拿的，就可以将spring的事务Connection用到mybatis里，从而保证mybatis实现了spring管理的事务

​	**如何保证当前SqlSession应用在一个事务里**：在拦截器SqlSessionInterceptor里每次创建DefaultSqlSession时，就会将其保存在spring提供的事务资源里（TransactionSynchronizationManager），如果事务开启了，就将其保存，所以每次获取SqlSession先检查事务是否开启，在检查事务资源是否存在SqlSession。

- 事务开启且还没有SqlSession：创建DefaultSqlSession并绑定到当前事务资源里，等待下次获取
- 事务开启且有SqlSession：直接重用这个SqlSession
- 事务没有开启：每次都新建DefaultSqlSession开执行方法

# 3. Mybatis-Plus（3.4.3）

## 3.1 mp是如何实现BaseMapper里的crud接口的

​		每一个mapper的java文件都有一个对应的MapperFactoryBean对象，用来实现mapper接口的动态代理。在MapperFactoryBean中覆盖的afterPropertiesSet方法里，会利用**com.baomidou.mybatisplus.core.MybatisConfiguration#mybatisMapperRegistry将当前mapper的java接口注册，并且用mp自定义的MybatisMapperAnnotationBuilder解析mapper接口里的方法，注册基本的crud就是在里面完成的**

​		在parse方法内部会先判断mapper的Java接口是否继承了BaseMapper接口（只有继承了这个接口才具有基础的crud功能），在调用DefaultSqlInjector#inspectInject方法进行表TableInfo的构建和基础crud的设置

```java
// DefaultSqlInjector的inspectInject方法，就是mp主要功能的实现入口
@Override
public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
    // 表对应的实体类
    Class<?> modelClass = ReflectionKit.getSuperClassGenericType(mapperClass, Mapper.class, 0);
    if (modelClass != null) {
        String className = mapperClass.toString();
        Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
        if (!mapperRegistryCache.contains(className)) {
            List<AbstractMethod> methodList = this.getMethodList(mapperClass);
            if (CollectionUtils.isNotEmpty(methodList)) {
                // 先构建实体类的TableInfo
                TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
                // 再循环注入mybatis-plus提供的BaseMapper里的crud方法
                methodList.forEach(m -> m.inject(builderAssistant, mapperClass, modelClass, tableInfo));
            } else {
                logger.debug(mapperClass.toString() + ", No effective injection method was found.");
            }
            mapperRegistryCache.add(className);
        }
    }
}
// BaseMapper里的接口方法对应的实现抽象
@Override
public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
    return Stream.of(
        new Insert(),
        new Delete(),
        new DeleteByMap(),
        new DeleteById(),
        new DeleteBatchByIds(),
        new Update(),
        new UpdateById(),
        new SelectById(),
        new SelectBatchByIds(),
        new SelectByMap(),
        new SelectOne(),
        new SelectCount(),
        new SelectMaps(),
        new SelectMapsPage(),
        new SelectObjs(),
        new SelectList(),
        new SelectPage()
    ).collect(toList());
}
```

​		**TableInfoHelper#initTableInfo方法用于构建表对应的实体类的信息，包括表名，表中所有的字段信息，各种mp支持的注解，和主键生成策略等等**

​		**AbstractMethod就是BaseMapper里每个接口封装的抽象信息，每一个BaseMapper里的接口都通过对应的AbstractMethod方法构造成一个MappedStatement放在Configuration里，所以基础的crud接口就实现了**

 		以BaseMapper里的selectOne方法为例，对应的实现AbstractMethod就是SelectOne，构建对应的MappedStatement时最重要的就是构建SqlSource信息，SelectOne方法对应的SqlSource里的sql脚本如下

```xml
<!-- 一个只有id,name,create_time三个字段的表所对应的sql结构 -->
<script>
    <choose>
        <!-- 实体类Wrapper参数存在，且设置了first参数，在AbstractWrapper -->
        <when test="ew != null and ew.sqlFirst != null"> 
            ${ew.sqlFirst}
        </when>
        <otherwise></otherwise>
    </choose> 
    SELECT 
    <choose>
        <!-- 自定义的查询字段，LambdaQueryWrapper里的sqlSelect -->
        <when test="ew != null and ew.sqlSelect != null">
            ${ew.sqlSelect}
        </when>
        <!-- 如果没有自定义查询字段，就默认查询全表字段（从TableInfo中获取表字段信息） -->
        <otherwise>ID,NAME,CREATE_TIME</otherwise> 
    </choose> FROM srm_category 
    <if test="ew != null">
        <where>
            <!-- 实体类Wrapper存在，且设置里内部的entity，就按照entity来匹配查询，AbstractWrapper#entity -->
            <if test="ew.entity != null"> 
                <if test="ew.entity.id != null">ID=#{ew.entity.id}</if>
                <if test="ew.entity['name'] != null"> AND NAME=#{ew.entity.name}</if>
                <if test="ew.entity['createTime'] != null"> AND CREATE_TIME=#{ew.entity.createTime}</if>
            </if>
             <!-- sql片段存在（通常都是lamda表达式查询增加的），且查询条件不为空。由AbstractWrapper#expression组装sql片段 -->
            <if test="ew.sqlSegment != null and ew.sqlSegment != '' and ew.nonEmptyOfWhere">
                <if test="ew.nonEmptyOfEntity and ew.nonEmptyOfNormal"> AND</if> ${ew.sqlSegment}
            </if>
        </where>
        <if test="ew.sqlSegment != null and ew.sqlSegment != '' and ew.emptyOfWhere">
            ${ew.sqlSegment}
        </if>
    </if>
    <choose>
        <when test="ew != null and ew.sqlComment != null">
            ${ew.sqlComment}
        </when>
        <otherwise></otherwise>
    </choose>
</script>
```

​		这个sql脚本就已经添加了很多功能了，比如**在sql头部增加查询，自定义的sql查询字段，根据实体类查询，sql片段查询条件**。**lamda查询或更新就是将每个条件组合成一个sql片段，放入Wrapper中，等待对应的crud接口使用动态sql拼凑出真正的sql，然后执行**。

## 3.2 mp插件

​		mp在这个版本（3.4.3）只定义了一个实现了org.apache.ibatis.plugin.Interceptor接口的MybatisPlusInterceptor用来做全局拦截器，再抽象出自定义的InnerInterceptor用来指向更具体的拦截方法和参数，基于InnerInterceptor提供的接口可以更加明确的实现拦截点

​	MybatisPlusInterceptor拦截了Executor的update和query方法，基于此可以实现乐观锁和分页器。同时也拦截了StatementHandler的prepare和getBoundSql方法

### 3.2.1 PaginationInnerInterceptor

​		PaginationInnerInterceptor用来实现mp提供的分页，流程先判断参数是否有mp提供的分页对象（com.baomidou.mybatisplus.core.metadata.IPage），如果有才会考虑是否需要分页。PaginationInnerInterceptor会根据当前查询sql构造出一条count sql，利用jsqlparser包解析原sql，在进行优化（比如原sql有order by，而count sql是完全不需要排序的，所以在构造count sql时可以去掉原SQL的排序），构建出一条基于原sql上的新count sql，然后直接调用Executor的query方法执行这条count sql，再根据返回值判断是否还需要执行原sql

​		在执行原sql之前又需要构造limit，PaginationInnerInterceptor的beforeQuery发挥作用，在将Ipage参数转化为offset和limit，按sql方言构造limit自己加入原sql，这样在原sql的基础上分页sql就实现了

### 3.2.2 OptimisticLockerInnerInterceptor

​		OptimisticLockerInnerInterceptor是mp提供的乐观锁插件，在更新时配合@Version字段的值使用，实现数据库的乐观锁机制

​		**当使用了BaseMapper里的updateById或update方法，且传入的实体类参数不为空，且这个实体类里面字段使用了有@Version注解，且这个使用了@Version注解的字段不为空时，乐观锁机制才会生效。**

​		**mp根据@Version字段的值构造新的值（对于数字来说，新值 = 原值 + 1；对于时间类型的乐观锁来说，新值 = 当前时间）。同时，利用Wrapper，增加set和where的sql片段（set部分sql片段就是乐观锁字段=新值，where部分sql片段就是乐观锁字段=旧值），从而实现自动的乐观锁机制**

```java
protected void doOptimisticLocker(Map<String, Object> map, String msId) {
    //updateById(et), update(et, wrapper);
    // 获取Wrapper里的实体类
    Object et = map.getOrDefault(Constants.ENTITY, null);
    if (et != null) { // 实体类存在，才考虑走乐观锁
        // entity
        String methodName = msId.substring(msId.lastIndexOf(StringPool.DOT) + 1);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(et.getClass());
        if (tableInfo == null || !tableInfo.isWithVersion()) { // 没有@Version字段直接返回
            return;
        }
        try {
            TableFieldInfo fieldInfo = tableInfo.getVersionFieldInfo();
            Field versionField = fieldInfo.getField();
            // 旧的 version 值
            Object originalVersionVal = versionField.get(et);
            if (originalVersionVal == null) { // @Version字段的值都为null，连原始值都不知道，就不走乐观锁了
                return;
            }
            // @Version注解对应的数据库字段
            String versionColumn = fieldInfo.getColumn();
            // 新的 version 值（对于数字类型，就是原值 + 1，如果是日期类型，就是当前时间）
            Object updatedVersionVal = this.getUpdatedVersionVal(fieldInfo.getPropertyType(), originalVersionVal);
            if (PARAM_UPDATE_METHOD_NAME.equals(methodName)) {
                AbstractWrapper<?, ?, ?> aw = (AbstractWrapper<?, ?, ?>) map.getOrDefault(Constants.WRAPPER, null);
                if (aw == null) {
                    UpdateWrapper<?> uw = new UpdateWrapper<>();
                    uw.eq(versionColumn, originalVersionVal);
                    map.put(Constants.WRAPPER, uw);
                } else {
                    // 添加
                    aw.apply(versionColumn + " = {0}", originalVersionVal);
                }
            } else {
                map.put(Constants.MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
            }
            versionField.set(et, updatedVersionVal);
        } catch (IllegalAccessException e) {
            throw ExceptionUtils.mpe(e);
        }
    }
}
```