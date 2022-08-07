package exceptions;

public class SearchScrollTimeoutTooSmall extends Throwable {

    public SearchScrollTimeoutTooSmall() {
        super(
                "The searchscroll timeout cannot be lesser than 1 second."
        );
    }

    public SearchScrollTimeoutTooSmall(String message) {
        super(message);
    }
}
