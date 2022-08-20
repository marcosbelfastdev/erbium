package com.github.marcosbelfastdev.erbium.exceptions;

public class OptionValueTooSmall extends Throwable {

    public OptionValueTooSmall() {
        super(
                "An option value cannot be a negative number."
        );
    }

    public OptionValueTooSmall(String message) {
        super(message);
    }
}
