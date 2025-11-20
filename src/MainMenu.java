import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * This class allows the user to navigate to all of the different "pages" of the program.
 * Handles all menu navigation and user routing throughout the Personal Finance Manager application.
 *
 * @author Shazadul Islam
 */
public class MainMenu {
	private Scanner scanner = new Scanner(System.in);

	// === Authentication + Accounts stack ===
    private final Storage authStorage;
    private final Authentication authModule;
    private final Accounts accountsModule;

    // === Budget storage (CSV / Budget) ===
    private final StorageManager storageModule;

    // === Prediction (DataReader + ScenarioSimulator) ===
    private final DataReader predictionData;
    private final ScenarioSimulator predictionModule;

    // === Reports ===
    private final ReportManager reportsModule;

    // === Validation ===
    private final ValidationEngine validationModule;

    // === Error handling ===
    private final ErrorHandler errorHandler;
    
    /**
     * Default constructor for MainMenu.
     */
    public MainMenu() {
    	// ---- Auth + Accounts ----
        authStorage    = new Storage();
        authModule     = new Authentication(authStorage);
        accountsModule = new Accounts(authModule, authStorage);

        // ---- Budget storage (CSV files under /data) ----
        storageModule = new StorageManager();

        // ---- Prediction: read Data.csv once and share with ScenarioSimulator ----
        predictionData = new DataReader();
        predictionData.readData();       // prints errors if CSV missing or bad
        predictionModule = new ScenarioSimulator(predictionData);

        // ---- Reports ----
        reportsModule = new ReportManager();

        // ---- Validation ----
        validationModule = new ValidationEngine();

        // ---- Error handling ----
        errorHandler = new ErrorHandler();
    }
    
    /**
     * Gets the user's choice of where they want to navigate to and sends them there.
     *
     * @param numChoice the user's selected menu option
     * @return true if a valid choice was made, otherwise false
     * @author Shazadul Islam
     */
    public int getUserChoice(int numChoices) {
    	// Validate and route based on user choice
        while(true){
            try {
                int numChoice = Integer.parseInt(scanner.nextLine());
            
                if (numChoice >= 1 && numChoice <=numChoices){
                    return numChoice;
                }
                else{
                    System.out.print("Please enter a valid number associated with an option displayed in the main menu (1-"+ numChoices+"): ");
                }
                
            } catch(NumberFormatException e) {
                System.out.print("Please enter a valid number associated with an option displayed in the main menu (1-"+ numChoices+"):");
                scanner.nextLine();
            }
        }
    }

    
    public String enter() {
    	while (true) {
    		System.out.println("Would you like to sign in or create a new account? ");
    		System.out.println(" 1. Sign In");
    		System.out.println(" 2. Create a new account");
    		System.out.print("Please enter the number associated with your desired option: ");
    		int userChoice = getUserChoice(2);
    		System.out.println();
    		
        	switch (userChoice) {
        	case 1:
        		boolean isNotLoggedIn = true;
        		while (isNotLoggedIn) {
            		System.out.println("Enter your username and password: ");
            		System.out.print(" Username: ");
                	String loginUsername = scanner.nextLine();
                	System.out.print(" Password: ");
                	String loginPassword = scanner.nextLine();
                	
                	boolean validLogin = accountsModule.signIn(loginUsername, loginPassword);
                	if (validLogin) {
                		System.out.println();
                		return(loginUsername);
                	}
                	else{
                		System.out.println("Your username or password was incorrect or that account does not exist.");
                		System.out.println();
                		System.out.println("Would you like to try logging in again or create a new account? ");
                		System.out.println(" 1. Try Logging in again");
                		System.out.println(" 2. Create a New Account");
                		System.out.print("Please enter the number associated with your desired option: ");
                		int retryChoice = getUserChoice(2);
                    	System.out.println();
                        if (retryChoice == 2) {
                            break; 
                        }

                	};
        		}
        	case 2:
        		boolean areYouSure = false;
        		while (!areYouSure) { 
        			System.out.print("What would you like your username to be? ");
            		String registerUsername = scanner.nextLine();
            		System.out.print("What would you like your password to be? ");
            		String registerPassword = scanner.nextLine();
            		System.out.print("What would you like your recovery question to be? ");
            		String registerSecretQuestion = scanner.nextLine();
            		System.out.print("What would you like your recovery question answer to be? ");
            		String registerSecretAnswer = scanner.nextLine();
            		System.out.println("Are you sure about your username, password, recovery question, and recovery question answer "
            				+ "for your new account? ");
            		System.out.println(" 1. Yes");
            		System.out.println(" 2. No");
            		System.out.print("Please enter the number associated with your desired option: ");
            		int surety = getUserChoice(2);
            		boolean isValidAccount = accountsModule.registerAccount(registerUsername, registerPassword, 
        					registerSecretQuestion, registerSecretAnswer);
            		if ((surety == 1) && isValidAccount) {
            			areYouSure = true;
            		}
            		else if(!isValidAccount) {
            			System.out.println("The account details you entered were invalid");
            			System.out.println("Please follow the required format: ");
            		}
            		System.out.println();
        		}
        		continue;
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
    public int toMenu() {
        // Return to main menu
    	System.out.println("Main Menu: ");
    	System.out.println("  1. Table of Contents");
    	System.out.println("  2. Settings");
    	System.out.println("  3. Quit Program");
    	System.out.print("Please enter the number associated with your desired option: ");
    	
    	int mainMenuChoice = getUserChoice(3);
    	
    	System.out.println();
    	
    	return mainMenuChoice;
    }
    
    /**
     * Return the user to a table of contents of modules.
     * If they are within a module and would like to go to a different module, then
     * they can return to the list of modules and select the next module that they would like to view.
     *
     * @author Shazadul Islam
     */
    public int toModules() {
    	// Return to modules list
    	System.out.println("Table of Modules");
    	System.out.println("  1. Reports");
    	System.out.println("  2. Prediction");
    	System.out.print("Please enter the number associated with your desired option: ");
    	
    	int tableChoice = getUserChoice(2);
    	
    	return tableChoice;
    }
    
    /**
     * Runs the entire program
     *
     * @author Shazadul Islam
     */
    public void start() {
    	System.out.println("Hamilton Heights Presents");
    	System.out.println("Personal Finance Manager");
    	System.out.println();
    	
    	enter();
    	
    	int mainMenuChoice = toMenu();
    	switch (mainMenuChoice) {
        case 1:
            toModules();
            break; 
        case 2:
            displayAccountSettings();
            break;
        case 3:
        	exitProgram();
        	break;
        default:
        	System.out.print("Something went wrong. You shouldn't be here!");
    	}
    	
    	int tableChoice = toModules();
    	switch (tableChoice) {
        case 1:
            //Make a call to reports that gets its files from storage
            break; 
        case 2:
            //Make a call to predictions 
            break;
        case 3:
        	exitProgram();
        	break;
        default:
        	System.out.print("Something went wrong. You shouldn't be here!");
    	}
    	
    	getUserChoice(3);
    }


    /**
     * Prints the settings, so users can do things like change username and password and security questions.
     *
     * @author Shazadul Islam
     */
    public void displayAccountSettings() {
        // Display account settings menu
    	System.out.println("");
    }
    
    /**
     * Lets the user exit the program to desktop from anywhere within the code.
     *
     * @author Shazadul Islam
     */
    public void exitProgram() {
        // Exit program
    	scanner.close();
    	System.out.println("Goodbye!");
        System.exit(0);
    }
    
    public static void main(String[] args) {
    	MainMenu programMenu = new MainMenu();
        programMenu.start();
    }
}
