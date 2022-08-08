package com.github.marcosbelfastdev.erbium.exceptions;

public class RetryIntervalTooBig extends Throwable {

    public RetryIntervalTooBig() {
        super(
                "The retry interval cannot be greater than a third of the resolve timeout or " +
                        "greater than the enabled or visible timeouts. " +
                        "To solve this, set a greater value for the resolve timeout or lower the retry interval " +
                        "up to its minimum accepted value of 20 ms."
        );
    }

    public RetryIntervalTooBig(String message) {
        super(message);
    }
}
