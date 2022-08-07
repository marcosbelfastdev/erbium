package exceptions;

public class EnabledTimeoutTooSmall extends Throwable {

    public EnabledTimeoutTooSmall() {
        super(
                "The enabled timeout must be greater than 200 ms. "+
                        "This setting can be disabled instead."
        );
    }

    public EnabledTimeoutTooSmall(String message) {
        super(message);
    }
}
