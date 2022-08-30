package com.github.marcosbelfastdev.erbium.exceptions;

public class DisplayedStatusError extends Throwable {

    public DisplayedStatusError() {
        super(
                "Element did not become visible as required."
        );
    }

    public DisplayedStatusError(String message) {
        super(message);
    }
}
