package site.shanzhao.startup.logger.config;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import site.shanzhao.startup.logger.entity.StartupCostTimeRecorder;

public class StartupStartListener implements ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        StartupCostTimeRecorder.recordStart();
    }
}
