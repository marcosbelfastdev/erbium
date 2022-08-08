package com.github.marcosbelfastdev.erbium.exceptions;

public class ResolveTimeoutTooSmall extends Throwable {

    public ResolveTimeoutTooSmall() {
        super(
                "The resolve timeout cannot be lesser than 2000 ms. and it must be greater than 3 times " +
                        "the retry interval. To solve this, increase the resolve timeout or lower the retry interval " +
                        "up to its minimum of 20 ms."
        );
    }

    public ResolveTimeoutTooSmall(String message) {
        super(message);
    }
}
