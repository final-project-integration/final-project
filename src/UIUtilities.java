import java.util.Scanner;

/**
 * UIUtilities provides utility methods for user interface operations.
 * Handles console I/O, message display, user prompts, and screen clearing.
 * Ensures consistent formatting and user interaction across the application.
 *
 *
 * @author Aaron Madou
 *
 */
public class UIUtilities {

    /**
     * Default constructor for UIUtilities.
     */
    public UIUtilities() {}

    /**
     * Clears all text from the console screen.
     * Provides a clean display for new menu or information screens.
     *
	 * NOTE: ONLY WORKS IN THE TERMINAL CONSOLE, NOT ECLIPSE. 
	 *
     * @author Aaron Madou
     */
    public void clearConsole() {
        // Clear console screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Outputs a message to the console.
     * Used for displaying information, prompts, and results to the user.
     *
     * @param message the message to be displayed to the user
     * @author Aaron Madou
     */
    public void outputMessage(String message) {
        // Print message to console
        System.out.println(message);
    }

    /**
     * Asks the user a question by printing to the console.
     * Displays a formatted query and prepares to receive user input.
     *
     * @return coupled with the user's response with a string.
     * @param query a string containing the question for the user
     * @author Aaron Madou
     */
    public String queryUser(String query) {
        // Display query and await input
		System.out.println(query);
		return grabResponse();
    }

    /**
     * Reads and returns the user's response from the console.
     * Captures user input after a query has been displayed.
     *
     * @return a string containing the user's response
     * @author Aaron Madou
     */
    public String grabResponse() {
        // Read and return user input
		return new Scanner(System.in).nextLine();
    }
}
