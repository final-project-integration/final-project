import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * This class allows the user to navigate to all of the different "pages" of the program.
 * Handles all menu navigation and user routing throughout the Personal Finance Manager application.
 *
 * @author Shazadul Islam
 */
public class MainMenu {

    /**
     * Default constructor for MainMenu.
     */
    public MainMenu() {}

    /**
     * Prints the Main menu, so users can see where they can navigate to within the program.
     *
     * @author Shazadul Islam
     */
    public static void displayMenu() {
        // Print main menu options
    	// Print main menu options
    	System.out.println("Hamilton Heights:");
    	System.out.println("Personal Finance Manager");
    	System.out.println();
    	System.out.println("Main Menu");
    	System.out.println("  1. Table of Contents");
    	System.out.println("  2. Settings");
    	System.out.println("  3. Quit to Desktop");
    	System.out.print("Please enter the number associated with you desired option: ");
    	
    	getUserChoice(3);
    }

    /**
     * Gets the user's choice of where they want to navigate to and sends them there.
     *
     * @param numChoice the user's selected menu option
     * @return true if a valid choice was made, otherwise false
     * @author Shazadul Islam
     */
    public static int getUserChoice(int numChoices) {
    	// Validate and route based on user choice
        Scanner sc = new Scanner(System.in);
        int num = 0;
        
        while(true){
            try {
                num = sc.nextInt();
            
                if (num >= 1 && num <=numChoices){
                    return num;
                }
                else{
                    System.out.print("Please enter a valid number associated with an option displayed in the main menu (1-"+ numChoices+"): ");
                }
                
            } catch(InputMismatchException e) {
                System.out.print("Please enter a valid number associated with an option displayed in the main menu (1-"+ numChoices+"):");
                sc.nextLine();
            }
        }
    }

    /**
     * Returns the user to the main menu.
     * If they are within a table of contents or a module and would like to go to a different "page",
     * then they can return to the main menu and select where they would like to go next.
     *
     * @author Shazadul Islam
     */
    public void returnToMenu() {
        // Return to main menu
    	System.out.println("Main Menu");
    	System.out.println("  1. Table of Contents");
    	System.out.println("  2. Settings");
    	System.out.println("  3. Quit to Desktop");
    	System.out.print("Please enter the number associated with you desired option: ");
    	
    	getUserChoice(3);
    }

    /**
     * Return the user to a table of contents of modules.
     * If they are within a module and would like to go to a different module, then
     * they can return to the list of modules and select the next module that they would like to view.
     *
     * @author Shazadul Islam
     */
    public void returnToModules() {
        // Return to modules list
    }

    /**
     * Lets the user exit the program to desktop from anywhere within the code.
     *
     * @author Shazadul Islam
     */
    public void exitProgram() {
        // Exit program
    	System.out.println("Goodbye!");
        System.exit(0);
    }

    /**
     * Prints the settings, so users can do things like change username and password and security questions.
     *
     * @author Shazadul Islam
     */
    public void displayAccountSettings() {
        // Display account settings menu
    }
    
    public static void main(String[] args) {
        displayMenu();
    }
}
