package com.github.marcosbelfastdev.erbium.core;

import static java.util.Objects.isNull;

public class StopWatch {
    Long startTime;


    public StopWatch() {
        this.startTime = System.currentTimeMillis();
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
        return System.currentTimeMillis() - startTime;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
    }

}