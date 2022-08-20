package com.github.marcosbelfastdev.erbium.exceptions;

public class PageLoadTimeoutTooSmall extends Throwable {

    public PageLoadTimeoutTooSmall() {
        super(
                "The page load timeout cannot be lesser than the resolve timeout."
        );
    }

    public PageLoadTimeoutTooSmall(String message) {
        super(message);
    }
}
