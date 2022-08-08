package com.github.marcosbelfastdev.erbium.exceptions;

public class OptionValueTooSmall extends Throwable {

    public OptionValueTooSmall() {
        super(
                "An option value cannot be negative."
        );
    }

    public OptionValueTooSmall(String message) {
        super(message);
    }
}
