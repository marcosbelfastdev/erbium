package com.github.marcosbelfastdev.erbium.exceptions;

public class IncompatibleOptionTypes extends Throwable {

    public IncompatibleOptionTypes() {
        super(
                "An attempt was made to store an object of a different type " +
                        "than the original object stored in playback options."
        );
    }

    public IncompatibleOptionTypes(String message) {
        super(message);
    }
}
