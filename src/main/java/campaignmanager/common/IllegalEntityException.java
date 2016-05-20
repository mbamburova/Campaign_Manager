package campaignmanager.common;

/**
 * This exception is thrown when you try to use an entity that can not be 
 * used for the operation.
 * 
 * @author Petr Adámek
 */
public class IllegalEntityException extends RuntimeException {

    /**
     * Constructs an instance of
     * <code>IllegalEntityException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public IllegalEntityException(String msg) {
        super(msg);
    }
}
