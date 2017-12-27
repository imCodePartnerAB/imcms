package com.imcode.imcms.util;

// translated from scala...
public class ThreadUtility {

    private static Thread mkThread(String name, Runnable runBody) {
        final Thread thread = new Thread(runBody);
        thread.setName(name);
        return thread;
    }

    public static Thread spawn(Runnable runBody) {
        return startThread(new Thread(runBody));
    }

    public static Thread spawn(String name, Runnable runBody) {
        return startThread(mkThread(name, runBody));
    }

    public static Thread spawnDaemon(Runnable runBody) {
        return startDaemon(new Thread(runBody));
    }

    public static Thread spawnDaemon(String name, Runnable runBody) {
        return startDaemon(mkThread(name, runBody));
    }

    private static Boolean isTerminated(Thread thread) {
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
