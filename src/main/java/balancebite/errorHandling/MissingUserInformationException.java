package balancebite.errorHandling;

/**
 * Custom exception to handle cases where the user information necessary
 * for calculating the recommended daily intake is incomplete or missing.
 *
 * This exception is thrown when required user details such as weight, height,
 * age, gender, activity level, and goal are not provided.
 */
public class MissingUserInformationException extends RuntimeException {

    /**
     * Constructs a new MissingUserInformationException with the specified detail message.
     *
     * @param message The detail message explaining what information is missing or incomplete.
     *                This message is passed to the superclass {@code RuntimeException}.
     */
    public MissingUserInformationException(String message) {
        super(message);  // Pass the error message to the RuntimeException constructor
    }
}
