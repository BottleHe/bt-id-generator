package work.bottle.plugin.utils;


public class TimeUtils {
    public static final long EPOCH = 1658939782000L;

    public static long getTimeMilliSeconds() {
        return System.currentTimeMillis() - EPOCH;
    }

    public static long getTimeSeconds() {
        return (System.currentTimeMillis() - EPOCH) / 1000;
    }
}
