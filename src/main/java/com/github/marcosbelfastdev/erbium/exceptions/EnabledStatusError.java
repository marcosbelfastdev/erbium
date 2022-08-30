package com.github.marcosbelfastdev.erbium.exceptions;

public class EnabledStatusError extends Throwable {

    public EnabledStatusError() {
        super(
                "Element did not become enabled as required."
        );
    }

    public EnabledStatusError(String message) {
        super(message);
    }
}
