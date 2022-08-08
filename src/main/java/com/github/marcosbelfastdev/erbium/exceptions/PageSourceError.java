package com.github.marcosbelfastdev.erbium.exceptions;

public class PageSourceError extends Throwable {

    public PageSourceError() {
        super(
                "It was not possible to get the page source."
        );
    }

    public PageSourceError(String message) {
        super(message);
    }
}
