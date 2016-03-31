package common;

public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs an instance of <code>common.EntityNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public EntityNotFoundException(String msg) {
        super(msg);
    }
}