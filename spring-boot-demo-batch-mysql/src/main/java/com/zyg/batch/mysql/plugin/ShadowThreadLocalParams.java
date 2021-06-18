package com.zyg.batch.mysql.plugin;

/**
 * @author pengpeng
 */
public class ShadowThreadLocalParams {

    private static final ThreadLocal<String> SHADOW_THREAD = new ThreadLocal<>();

    public static String getShadowParams() {
        return SHADOW_THREAD.get();
    }

    public static void setKey(String key) {
        SHADOW_THREAD.set(key);
    }

    public static void remove() {
        SHADOW_THREAD.remove();
    }
}
