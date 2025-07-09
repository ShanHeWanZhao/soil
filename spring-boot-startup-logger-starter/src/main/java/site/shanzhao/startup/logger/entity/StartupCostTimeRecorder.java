package site.shanzhao.startup.logger.entity;

public class StartupCostTimeRecorder {

    private static Long startTime = null;

    public static void recordStart() {
        startTime = System.currentTimeMillis();
    }

    public static long cost() {
        return System.currentTimeMillis() - startTime;
    }
}
