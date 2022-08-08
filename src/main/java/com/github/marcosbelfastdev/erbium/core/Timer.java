package com.github.marcosbelfastdev.erbium.core;

import static java.util.Objects.isNull;

public class Timer {
    Long endTime;
    Long timeout;

    public Timer(long timeout) {
        this.timeout = timeout;
    }

    public Timer start() {
        if (isNull(endTime))
            endTime = System.currentTimeMillis() + timeout;
        return this;
    }

    public boolean timedOut() {
        start();
        return endTime - System.currentTimeMillis() <= 0;
    }

    public void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}