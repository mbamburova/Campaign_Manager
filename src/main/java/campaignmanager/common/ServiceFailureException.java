package campaignmanager.common;

/**
 * This exception indicates service failure.
 *  
 * @author Petr Adamek
 */
public class ServiceFailureException extends RuntimeException {

    public ServiceFailureException(String msg) {
        super(msg);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
