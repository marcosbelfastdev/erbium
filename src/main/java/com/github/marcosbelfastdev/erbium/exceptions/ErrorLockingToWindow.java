package com.github.marcosbelfastdev.erbium.exceptions;

public class ErrorLockingToWindow extends Throwable {

    public ErrorLockingToWindow() {
        super(
                "An error occurred attempting to assign a home window to an element."
        );
    }

    public ErrorLockingToWindow(String message) {
        super(message);
    }
}
