package com.github.marcosbelfastdev.erbium.exceptions;

public class CannotSwitchBackToWindow extends Throwable {

    public CannotSwitchBackToWindow() {
        super(
                "It was not possible to switch back to the previous window."
        );
    }

    public CannotSwitchBackToWindow(String message) {
        super(message);
    }
}
