package com.imcode.imcms.util;

// translated from scala...
public class ThreadUtility {

    public static Thread mkThread(Runnable runBody) {
        return new Thread(runBody);
    }

    public static Thread mkThread(String name, Runnable runBody) {
        final Thread thread = mkThread(runBody);
        thread.setName(name);
        return thread;
    }

    public static Thread spawn(Runnable runBody) {
        return startThread(mkThread(runBody));
    }

    public static Thread spawn(String name, Runnable runBody) {
        return startThread(mkThread(name, runBody));
    }

    public static Thread spawnDaemon(Runnable runBody) {
        return startDaemon(mkThread(runBody));
    }

    public static Thread spawnDaemon(String name, Runnable runBody) {
        return startDaemon(mkThread(name, runBody));
    }

    public static Boolean isTerminated(Thread thread) {
        return (thread == null) || (thread.getState() == Thread.State.TERMINATED);
    }

    public static Boolean notTerminated(Thread thread) {
        return !isTerminated(thread);
    }

    public static void interruptAndAwaitTermination(Thread thread) throws InterruptedException {
        if (thread != null) {
            thread.interrupt();
            thread.join();
        }
    }

    private static Thread startThread(Thread thread) {
        thread.start();
        return thread;
    }

    private static Thread startDaemon(Thread thread) {
        thread.setDaemon(true);
        return startThread(thread);
    }

}
