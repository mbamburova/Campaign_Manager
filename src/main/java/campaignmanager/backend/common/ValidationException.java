package campaignmanager.backend.common;

/**
 * This exception is thrown when validation of entity fails.
 * 
 * @author Petr Adámek
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs an instance of
     * <code>ValidationException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ValidationException(String msg) {
        super(msg);
    }
}
