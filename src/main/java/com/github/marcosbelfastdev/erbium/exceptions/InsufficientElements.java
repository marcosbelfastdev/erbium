package com.github.marcosbelfastdev.erbium.exceptions;

public class InsufficientElements extends Throwable {

    public InsufficientElements() {
        super(
                "No or not all elements expected were found."
        );
    }

    public InsufficientElements(String message) {
        super(message);
    }
}
