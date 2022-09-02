package com.github.marcosbelfastdev.erbium.exceptions;

public class CannotScrollAlert extends Throwable {

    public CannotScrollAlert() {
        super(
                "Could not scroll to element."
        );
    }

    public CannotScrollAlert(String message) {
        super(message);
    }
}
