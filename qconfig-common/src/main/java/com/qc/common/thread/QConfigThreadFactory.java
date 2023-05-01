package com.qc.common.thread;

import junit.framework.Test;

import java.util.concurrent.ThreadFactory;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/29/11:44
 */
public class QConfigThreadFactory implements ThreadFactory {

    private final ThreadGroup threadGroup;
    private final String namePrefix;
    private final boolean daemon;
    private int threadNumber = 0;

    public QConfigThreadFactory(String namePrefix, boolean daemon) {
        this.threadGroup = Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }


    public static ThreadFactory create(String namePrefix, boolean daemon) {
        return new QConfigThreadFactory(namePrefix, daemon);
    }

    @Override
    public Thread newThread(Runnable r) {
        QConfigThread configThread = new QConfigThread(threadGroup, r,//
                threadGroup.getName() + "-" + namePrefix + "-" + threadNumber++);
        configThread.setDaemon(daemon);
        return configThread;
    }

    public static class QConfigThread extends Thread {
        public QConfigThread(ThreadGroup group, Runnable target, String name) {
            super(group, target, name);
        }
    }

}
