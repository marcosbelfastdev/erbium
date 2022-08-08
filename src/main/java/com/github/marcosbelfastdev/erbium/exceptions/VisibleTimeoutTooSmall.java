package com.github.marcosbelfastdev.erbium.exceptions;

public class VisibleTimeoutTooSmall extends Throwable {

    public VisibleTimeoutTooSmall() {
        super(
                "The visible timeout must be greater than 200 ms. "+
                        "This setting can be disabled instead."
        );
    }

    public VisibleTimeoutTooSmall(String message) {
        super(message);
    }
}
