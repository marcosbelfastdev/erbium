package exceptions;

public class EnabledTimeoutTooBig extends Throwable {

    public EnabledTimeoutTooBig() {
        super(
                "The enabled timeout must not exceed the resolve timeout."
        );
    }

    public EnabledTimeoutTooBig(String message) {
        super(message);
    }
}
