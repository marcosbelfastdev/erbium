package com.github.marcosbelfastdev.erbium.exceptions;

public class SyncedFindElementsError extends Throwable {

    public SyncedFindElementsError() {
        super(
                "Insufficient number of elements found."
        );
    }

    public SyncedFindElementsError(String message) {
        super(message);
    }
}
