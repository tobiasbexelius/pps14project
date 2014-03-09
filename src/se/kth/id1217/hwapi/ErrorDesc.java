package se.kth.id1217.hwapi;

/**
 * Details about the error event.
 */
public class ErrorDesc {

    private final String message;

    public ErrorDesc(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
