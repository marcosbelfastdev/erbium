package com.github.marcosbelfastdev.erbium.exceptions;

public class RetryIntervalTooSmall extends Throwable {

    public RetryIntervalTooSmall() {
        super(
                "The resolve was set to 20 ms, which is the minimum accepted value."
        );
    }

    public RetryIntervalTooSmall(String message) {
        super(message);
    }
}
