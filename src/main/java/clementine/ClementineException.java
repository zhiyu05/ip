package clementine;

/**
 * Custom exception class for the Clementine task management application.
 * This exception is thrown when application-specific errors occur, such as
 * invalid user input, task operation failures, or data processing errors.
 * Provides user-friendly error messages with the application's characteristic tone.
 *
 * @author zhiyu
 */
public class ClementineException extends Exception {
    /**
     * Constructs a new ClementineException with the specified error message.
     * @param message the descriptive error message explaining what went wrong
     */
    public ClementineException(String message) {
        super(message);
    }
}
