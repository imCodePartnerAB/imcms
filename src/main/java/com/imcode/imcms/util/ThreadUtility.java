package com.imcode.imcms.util;

public class ThreadUtility {

    public static Thread spawnDaemon(Runnable runBody) {
        final Thread thread = new Thread(runBody);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

}
