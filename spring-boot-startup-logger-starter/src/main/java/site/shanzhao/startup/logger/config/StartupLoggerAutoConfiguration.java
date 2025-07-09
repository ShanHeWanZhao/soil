package site.shanzhao.startup.logger.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import site.shanzhao.startup.logger.entity.StartupCostTimeRecorder;
import site.shanzhao.startup.logger.properties.StartupLoggerProperties;


@AutoConfiguration
@AutoConfigureAfter(name = "org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration")
@EnableConfigurationProperties(StartupLoggerProperties.class)
@ConditionalOnClass(ApplicationReadyEvent.class)
@ConditionalOnProperty(prefix = "startup.logger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StartupLoggerAutoConfiguration {


    @Bean
    public ApplicationListener<ApplicationReadyEvent> startupLoggerListener(StartupLoggerProperties props) {
        return event -> {
            long cost = StartupCostTimeRecorder.cost();
            String timeStr = props.getTimeUnit().equals("s") ? (cost / 1000.0 + "s") : cost + "ms";
            String appName = event.getApplicationContext().getEnvironment().getProperty("spring.application.name", "Application");

            Logger log = LoggerFactory.getLogger("StartupLogger");
            log.info("========================================================");
            log.info("========================================================");
            log.info("=======   {} startup successful, cost time：{}   =======", appName, timeStr);
            log.info("========================================================");
            log.info("========================================================");
        };
    }
}
