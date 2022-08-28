package com.github.marcosbelfastdev.erbium.exceptions;

public class EnabledTimeoutTooBig extends RuntimeException {

    public EnabledTimeoutTooBig() {
        super(
                "The enabled timeout must not exceed the resolve timeout."
        );
    }

    public EnabledTimeoutTooBig(String message) {
        super(message);
    }
}
