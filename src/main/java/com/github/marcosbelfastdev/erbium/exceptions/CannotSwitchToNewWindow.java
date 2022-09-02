package com.github.marcosbelfastdev.erbium.exceptions;

public class CannotSwitchToNewWindow extends Throwable {

    public CannotSwitchToNewWindow() {
        super(
                "It was not possible to switch to a new window opened."
        );
    }

    public CannotSwitchToNewWindow(String message) {
        super(message);
    }
}
