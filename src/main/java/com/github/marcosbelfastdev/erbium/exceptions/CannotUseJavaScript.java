package com.github.marcosbelfastdev.erbium.exceptions;

public class CannotUseJavaScript extends Throwable {

    public CannotUseJavaScript() {
        super(
                "It was not possible to use JavaScript Executor because it is disabled."
        );
    }

    public CannotUseJavaScript(String message) {
        super(message);
    }
}
