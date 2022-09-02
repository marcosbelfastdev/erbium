package com.github.marcosbelfastdev.erbium.exceptions;

public class SetTextError extends Throwable {

    public SetTextError() {
        super(
                "There was an error trying to set text."
        );
    }

    public SetTextError(String message) {
        super(message);
    }
}
