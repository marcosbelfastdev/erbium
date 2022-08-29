package com.github.marcosbelfastdev.erbium.exceptions;

public class ClickError extends Throwable {

    public ClickError() {
        super(
                "An error occurred trying to click on an element."
        );
    }

    public ClickError(String message) {
        super(message);
    }
}
