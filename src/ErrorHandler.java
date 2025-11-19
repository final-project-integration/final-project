

/**
 * ErrorHandler manages all error handling and recovery operations for the application.
 * Logs errors, displays user-friendly error messages, and recovers from errors gracefully
 * to prevent application crashes.
 *
 *
 * @author Kapil Tamang
 */
public class ErrorHandler {

    /**
     * Default constructor for ErrorHandler.
     */
    public ErrorHandler() {}

    /**
     * Handles errors that occur when calling other team modules.
     * Catches exceptions, displays appropriate messages, and recovers gracefully.
     *
     * @param moduleName the name of the module where the error occurred
     * @param error the exception that was thrown
     * @author Kapil Tamang
     */
    public void handleModuleError(String moduleName, Exception error) {
        // Handle and log module errors
        displayError("An error has occured in the ")+ moduleName + " module.";
        logError("Error in " + moduleName, error);
        recoverToMenu();
    }

    /**
     * Recovers from an error state and returns the user to the main menu.
     * Ensures the application continues running after an error occurs.
     *
     * @author Kapil Tamang
     */
    public void recoverToMenu() {
        // Return to safe state
        System.out.println("Restoring main menu.");
    }

    /**
     * Logs error information for debugging and tracking purposes.
     * Records error details, timestamps, and stack traces for later review.
     *
     * @param errorMessage the error message to log
     * @param error the exception that occurred
     * @author Kapil Tamang
     */
    public void logError(String errorMessage, Exception error) {
        // Log error details
        System.err.println("ERROR")
        System.err.println("Time: " + java.time.LocalDateTime.now());
        System.err.println("Message: " + errorMessage);
        System.err.println("Exception Type: " + error.getClass().getName());
        System.err.println("Stack Trace:");
        error.printStackTrace(System.err);
    }

    /**
     * Displays a user-friendly error message to the console.
     * Formats error messages consistently across the application.
     *
     * @param message the error message to display to the user
     * @author Kapil Tamang
     */
    public void displayError(String message) {
        // Display formatted error message
        System.out.println("ERROR");
        System.out.println(message);
        System.out.println("Please try again.");
    }
}
