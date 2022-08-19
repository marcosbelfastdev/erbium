package com.github.marcosbelfastdev.erbium.core;

import static java.util.Objects.isNull;

public class Timer {
    Long startTime;
    Long endTime;
    Long timeout;

    public Timer(long timeout) {
        this.timeout = timeout;
    }

    public Timer start() {
        if (isNull(startTime))
            startTime = System.currentTimeMillis();
        if (isNull(endTime))
            endTime = System.currentTimeMillis() + timeout;
        return this;
    }

    public boolean timedOut() {
        start();
        return endTime - System.currentTimeMillis() <= 0;
    }

    public static void sleep(long sleep) {
        if (sleep <= 0)
            return;
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public long elapsedTime() {
        start();
        return System.currentTimeMillis() - startTime;
    }

}