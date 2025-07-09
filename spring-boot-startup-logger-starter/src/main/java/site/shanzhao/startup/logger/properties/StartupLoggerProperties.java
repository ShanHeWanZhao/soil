package site.shanzhao.startup.logger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("startup.logger")
public class StartupLoggerProperties {
    /**
     * 是否开启
     */
    private boolean enabled = true;

    /**
     * 时间格式：ms, s
     */
    private String timeUnit = "ms";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}