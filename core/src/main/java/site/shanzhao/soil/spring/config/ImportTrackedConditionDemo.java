package site.shanzhao.soil.spring.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(D.class)
public class ImportTrackedConditionDemo {

}
@ConditionalOnMissingBean(A.class)
class A{}

@Import(A.class)
@ConditionalOnMissingBean(B.class)
class B{}

@Import(B.class)
@ConditionalOnBean(C.class)
class C{}

@Import(value = {C.class, A.class})
@ConditionalOnMissingBean(D.class)
class D{}
