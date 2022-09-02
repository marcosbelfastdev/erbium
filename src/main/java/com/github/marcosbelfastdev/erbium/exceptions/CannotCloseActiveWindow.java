package com.github.marcosbelfastdev.erbium.exceptions;

public class CannotCloseActiveWindow extends Throwable {

    public CannotCloseActiveWindow() {
        super(
                "It is not possible to close the element's active window."
        );
    }

    public CannotCloseActiveWindow(String message) {
        super(message);
    }
}
