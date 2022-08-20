package com.github.marcosbelfastdev.erbium.exceptions;

public class NoElementFound extends Throwable {

    public NoElementFound() {
        super(
                "No element was found for the list of locators provided."
        );
    }

    public NoElementFound(String message) {
        super(message);
    }
}
