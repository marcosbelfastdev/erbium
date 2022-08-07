package exceptions;

public class VisibleTimeoutTooBig extends Throwable {

    public VisibleTimeoutTooBig() {
        super(
                "The visible timeout must not exceed the resolve timeout."
        );
    }

    public VisibleTimeoutTooBig(String message) {
        super(message);
    }
}
