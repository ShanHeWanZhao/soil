package site.shanzhao.soil.spring.config;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class NestedOrConditionDemo {

    static class OrCondition extends AnyNestedCondition {

        public OrCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }
        @ConditionalOnProperty(value = "condition.or.enable", havingValue = "true")
        static class PropertyOrEnable{}

        @ConditionalOnProperty(value = "condition.or.use", havingValue = "yes")
        static class PropertyOrUse{}

    }

    public static class C{
        public void init(){
            System.out.println("NestedOrConditionDemo$C initialized");
        }
    }

    @Bean(initMethod = "init")
    @Conditional(OrCondition.class)
    public C buildC(){
        return new C();
    }

}
