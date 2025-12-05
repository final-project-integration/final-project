import java.util.Scanner;
import java.io.File;

/**
 * This class allows the user to navigate to all of the different "pages" of the
 * program. Handles all menu navigation and user routing throughout the Personal
 * Finance Manager application.
 *
 * MainMenu only talks to one dependency: ModuleHub. ModuleHub then forwards
 * requests to Accounts, Storage, Reports, Prediction, and Validation.
 *
 * @author Shazadul Islam
 * @author Aaron Madou
 */
public class MainMenu {
	/**
	 * Creates a scanner that is used to handle all of the user input that is taken in and used by the program
	 */
    private final Scanner scanner = new Scanner(System.in);
    
    /**
     * Connects the MainMenu to the rest of the functions from the accounts, reports, prediction, validation, and storage teams
     */
    private final ModuleHub moduleHub;
    
    /**
     * Default constructor for MainMenu. Creates a new ModuleHub instance.
     */
    public MainMenu() {
        moduleHub = new ModuleHub();
    }

    /**
     * Only creates a new line when the program is being run in the eclipse console
     * Clears all everything when the program is being run in the terminal
     * Provides a clean display for new menu or information screens
     * 
     * @author Aaron Madou
     */
    public void clearConsole() {
    	System.out.println();
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Grabs the user's choice of where they want to navigate to when 
     * given a menu with numerical choices associated with choices within the menu,
     * validates that input to make sure its a valid choice for the menu being navigated,
     * and returns the users desired numerical choice of the menu option that they want to navigate too
     *
     * @param numChoices - the number of valid number options the user has (1..numChoices) that correlate to menu choices
     * @return numChoice - the numerical menu option chosen by the user
     * 
     * @author Shazadul Islam
     */
    public int getUserChoice(int numChoices) {
        // Validate and route based on user choice
        while (true) {
            try {
                int numChoice = Integer.parseInt(scanner.nextLine());
                if (numChoice < 1 || numChoice > numChoices) {
                	System.out.print("Please enter a valid number associated with an option displayed in the main menu (1-" + numChoices + "): ");
                	continue;
                }
                return numChoice;

            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number associated with an option displayed in the main menu (1-" + numChoices + "): ");
            }
        }
    }
    
    /**
     * Provides a buffer for when text needs to be displayed on the console or terminal 
     * before moving on to the next part of the program
     * 
     * @author Shazadul Islam
     */
    public void moveOn() {
    	System.out.print("Press enter when you are ready to move on...");
    	scanner.nextLine();
    }
    
    /**
     * Handles the accounts creation and entry:
     * Asks the user whether the user wants to sign in or create an account
     * Loop until we have a successful sign-in or the user decides to exit the program
     *
     * @return loginUsername - the username of the successfully signed-in user
     * 
     * @author Shazadul Islam
     */
    public String enter() {
        while (true) {
        	clearConsole();
            System.out.println("Would you like to sign in or create a new account? ");
            System.out.println("  1. Sign In");
            System.out.println("  2. Create a new account");
            System.out.println("  3. Exit application");
            System.out.print("Please enter the number associated with your desired option: ");
            int userChoice = getUserChoice(3);

            switch (userChoice) {
                case 1:
                    boolean isNotLoggedIn = true;
                    while (isNotLoggedIn) {
                    	clearConsole();
                        System.out.println("Enter your username and password: ");
                        System.out.print("  Username: ");
                        String loginUsername = scanner.nextLine();
                        System.out.print("  Password: ");
                        String loginPassword = scanner.nextLine();

                        boolean validLogin = moduleHub.loginUser(loginUsername, loginPassword);
                        if (validLogin) {
                            return (loginUsername);
                        } 
                        
                        else {
                        	clearConsole();
                            System.out.println("Your username or password was incorrect or that account does not exist.");
                            System.out.println("Would you like to try logging in again or create a new account? ");
                            System.out.println("  1. Try Logging in again");
                            System.out.println("  2. Create a New Account");
                            System.out.println("  3. Exit Application");
                            System.out.print("Please enter the number associated with your desired option: ");
                            int retryChoice = getUserChoice(3);
                            if (retryChoice == 2) {
                                // this code breaks out of isNotLoggedIn while loop and
                                // since case 1 doesn't end in a break,
                                // the user can fall through to case 2 and
                                // begin registering a new account
                                break;
                            }
                            else if (retryChoice == 3) {
                            	exitApplication();
                            }
                        }
                    }
                case 2:
                    boolean createdAccount = false;
                    while (!createdAccount) {
                    	clearConsole();
                        System.out.print("What would you like your username to be? ");
                        String registerUsername = scanner.nextLine();
                        System.out.print("What would you like your password to be? ");
                        String registerPassword = scanner.nextLine();
                        System.out.print("What would you like your recovery question to be? ");
                        String registerSecretQuestion = scanner.nextLine();
                        System.out.print("What would you like your recovery question answer to be? ");
                        String registerSecretAnswer = scanner.nextLine();
                        
                        clearConsole();
                        System.out.println("Please confirm that these are the credentials that you want for your account. ");
                        System.out.println("Username: "+ registerUsername);
                        System.out.println("Password: "+ registerPassword);
                        System.out.println("Account Recory Question: "+ registerSecretQuestion);
                        System.out.println("Account Recovery Answewr: "+ registerSecretAnswer);
                        System.out.println("Are these are the credentials that you want for your account? ");
                        System.out.println(" 1. Yes");
                        System.out.println(" 2. No");
                        System.out.print("Please enter the number associated with your desired option: ");
                        int surety = getUserChoice(2);

                        if (surety == 1) {
                            // User confirmed → proceed with registration
                            boolean isValidAccount = moduleHub.registerUser(
                                    registerUsername,
                                    registerPassword,
                                    registerSecretQuestion,
                                    registerSecretAnswer,
                                    true
                            );

                            if (!isValidAccount) {
                            	clearConsole();
                                System.out.println("The account details you entered were invalid or the username already exists.");
                                System.out.println("Please try entering different credentials and try again.");
                                System.out.print("Press enter when you are ready to try again...");
                                scanner.nextLine();
                            } else {
                                clearConsole();
                                System.out.println("Your account has been successfully created.");
                                System.out.print("Press enter when you are ready to sign in with your new credentials...");
                                scanner.nextLine();
                                createdAccount = true;
                            }

                        } else {
                            // User said "No"
                            clearConsole();
                            System.out.println("Let's try creating your account again, shall we?");
                            System.out.print("Press enter when you are ready to try again...");
                            scanner.nextLine();
                        }

                    }
                    continue;// After successful registration, loop back and show the Sign In / Create menu again
                case 3:
                	exitApplication();
            }
        }
    }
    
    /**
     * Scans user input for a year,
     * validates that input to make sure its a valid year,
     * and return the valid year from the user input
     * 
     * @return the year entered by the user
     * 
     * @author Shazadul Islam
     */ 
    private int getUserYear(){
  		while (true) {
   			try {
  				int year = Integer.parseInt(scanner.nextLine());
  				if ((year < 1900) || (year > 2100)) {
  	   				System.out.print("Please enter a valid year: ");
  					continue;
  				}
  				return year;
   			} catch (NumberFormatException e) {
   				System.out.print("Please enter a valid year: ");
   			}
   		}
    }
    
    /**
     * Finances Menu
     * 
     * @param currentUser - username of the currently signed-in user
     * 
     * @author Aaron Madou
     * @author Shazadul Islam
     */ 
    public void financesMenu(String currentUser) {
    	while(true) {
    		 clearConsole();
    		 System.out.println("Finances Menu:");
    	     System.out.println("  1. Upload CSV");
    	     System.out.println("  2. Reports");
    	     System.out.println("  3. Predictions");
    	     System.out.println("  4. Data Management");
    	     System.out.println("  5. Return to Main Menu");
    	     System.out.print("Please enter the number associated with your desired option: ");
    	     int userChoice = getUserChoice(5);
  	        
    	     switch (userChoice) {
    	     	case 1:
   	        		boolean uploadingFile = true;
   	        		while (uploadingFile) {
   	        			clearConsole();
	   	        		System.out.println("---CSV Loader---");
	   	        		System.out.println("Please enter the name of the CSV file you want to upload below.");
	   	        		System.out.println("• If the CSV you want to upload is in the same folder as the JAR, just type the file name(Ex: data.csv)");
	   	        		System.out.println("• However, if the file is somewhere else, please provide the full file path.");
	   	        		System.out.print("File name: ");
	   	        		String csvFilePath = scanner.nextLine();
	   	        		
	   	        		File file = new File(csvFilePath);
	   	        		String fileName = file.getName();
	   	        		try {
	   	        			int year = Integer.parseInt(fileName.substring(0, 4));
		   	        		System.out.println();
		   	        		if ((year < 1900) || (year > 2100)) {
		   	        			throw new NumberFormatException();
		   	  				}
	   	        			moduleHub.uploadCSVData(currentUser, csvFilePath, year);
	   	        		} catch(NumberFormatException e){
	   	        			clearConsole();
	   	        			System.out.println("Please make sure that the name of your CSV file begins with a valid year associated with that data.");
	   	        			System.out.println("Would you like to try uploading your CSV file again or return to the Finances Menu?");
	   	        			System.out.println("  1. Try uploading my CSV file again");
	   	        			System.out.println("  2. Return to Finances Menu");
	   	        			System.out.print("Please enter the number associated with your desired option: ");
	   	        			userChoice = getUserChoice(2);
	   	        			switch (userChoice){
	   	        				case 1:
	   	        					continue;
	   	        				case 2:
	   	        					uploadingFile = false;
	   	        			}	
	   	        		}
   	        		}
   	        		break;
   	        	case 2: 
   	        		if(reportsMenu(currentUser)) {
   	        			return;
   	        		} 
   	        		continue;
   	        	case 3: 
   	        		if(predictionMenu(currentUser)) {
   	        			return;
   	        		}
   	        		continue;
   	        	case 4:
   	        		if(dataManagementMenu(currentUser)) {
                        return;
                    }
                    continue;
   	        	case 5:
   	        		return;
   	        }
    	}
    }
    
    /**
     * Reports Menu
     * 
     * @return Determines if user wants to go to the back to Finances Menu or the Main Menu
     * @param currentUser - username of the currently signed-in user
     * 
     * @author Aaron Madou
     * @author Shazadul Islam
     */ 
    public boolean reportsMenu(String currentUser) {
    	while(true) {
    		clearConsole();
    		moduleHub.callStorage("listyears", currentUser, 0);
   	    	System.out.print("Please enter the year you would like reports about: ");
   			int year = getUserYear();
   			
   			boolean isWorkingWithYear = true;
   			int userChoice;
   			while (isWorkingWithYear) {
	   			clearConsole();
	   			System.out.println("What kind of information would you like about data from " + year + "?");
	   			System.out.println("Available Reports for " + year +":");
	    		System.out.println("  1. Yearly Summary");
	    		System.out.println("  2. Month Breakdown");
	    		System.out.println("  3. Category Analysis");
	    		System.out.println("  4. Full Report");
	    		System.out.println("Controls:");
	    		System.out.println("  5. View the Reports for another year");
	    		System.out.println("  6. Return to Finances Menu");
	    		System.out.println("  7. Return to Main Menu");
	   	    	System.out.print("Please enter the number associated with your desired option: ");
	   	    	userChoice = getUserChoice(7);
	   			
	   	    	switch (userChoice) {
	   	    		case 1:
	   	    			clearConsole();
	   	    			moduleHub.viewReport(currentUser, year, "yearly");
	   	    			moveOn();
	   	    			break;	
	   	    		case 2: 
	   	    			clearConsole();
	   	    			moduleHub.viewReport(currentUser, year, "monthly");
	   	    			moveOn();
	   	    			break;
	   	    		case 3:
	   	    			clearConsole();
	   	    			moduleHub.viewReport(currentUser, year, "category");
	   	    			moveOn();
		        		break;
		        	case 4:
		        		clearConsole();
		        		moduleHub.viewReport(currentUser, year, "full");
		        		moveOn();
		        		break;
		        	case 5:
		        		isWorkingWithYear = false;
		        		break;
		        	case 6: 
	   	    			return false; 
	   	    		case 7:
	   	    			return true;
   	    		}
   	    	}
    	}
    }
    
    /**
     * Predictions Menu
     * 
     * @return Determines if user wants to go to the back to the Finances Menu or the Main Menu
     * @param currentUser - username of the currently signed-in user
     * 
     * @author Aaron Madou
     * @author Shazadul Islam
     */ 
    public boolean predictionMenu(String currentUser) {
    	while(true) {
    		clearConsole();
    		moduleHub.callStorage("listyears", currentUser, 0);
   	    	System.out.print("Please enter the year you would like predictions about: ");
   			int year = getUserYear();
   			
   			boolean isWorkingWithYear = true;
   			int userChoice;
   			while (isWorkingWithYear) {
	    		clearConsole();
	   			System.out.println("What kind of Predictions would you like about the data from " + year + "?");
	   			System.out.println("Available Predictions for " + year + ":");
	    		System.out.println("  1. Summary Report");
	    		System.out.println("  2. Deficit Analysis");
	    		System.out.println("  3. Surplus Analysis");
	    		System.out.println("Controls:");
	    		System.out.println("  4. View the Predictions for another year");
	    		System.out.println("  5. Return to Finances Menu");
	    		System.out.println("  6. Return to Main Menu");
	   	    	System.out.print("Please enter the number associated with your desired option: ");
	   	    	userChoice = getUserChoice(6);
	   			
	   	    	switch (userChoice) {
	   	    		case 1:
	   	    			clearConsole();
	   	    			moduleHub.runPrediction(currentUser, year, "summary");
	   	    			moveOn();
	   	    			break;	
	   	    		case 2: 
	   	    			clearConsole();
	   	    			moduleHub.runPrediction(currentUser, year, "deficit");
	   	    			moveOn();
	   	    			break;
	   	    		case 3: 
	   	    			clearConsole();
	   	    			moduleHub.runPrediction(currentUser, year, "surplus");
	   	    			moveOn();
		        		break;
	   	    		case 4:
	   	    			isWorkingWithYear = false;
		        		break;
	   	    		case 5: 
	   	    			return false; 
	   	    		case 6:
	   	    			return true;
	   	    	}
   			}
    	}
    }
    
    /**
     * Data Management Menu
     * @return Determines if user wants to go to the back to the Finances Menu or the Main Menu
     * @param currentUser - username of the currently signed-in user
     * 
     * @author Shazadul Islam
    */
    public boolean dataManagementMenu(String currentUser) {
        while(true) {
        	clearConsole();
            System.out.println("Data Management Menu: ");
            System.out.println("  1. Delete a CSV file");
            System.out.println("  2. Return to Finances Menu");
            System.out.println("  3. Return to Main Menu");
            System.out.print("Please enter the number associated with your desired option: ");
            int userChoice = getUserChoice(3);
            
            switch (userChoice) {
                case 1:
                	while (true) {
	                	clearConsole();
	                    moduleHub.callStorage("listyears", currentUser, 0);
	                    System.out.print("Enter the year of the CSV file you would like to delete: ");
	                    int year = getUserYear();
	                    moduleHub.callStorage("delete", currentUser, year);
	                     
	                    clearConsole();
	                    System.out.println("Would you like to delete another CSV file?");
	                    System.out.println("  1. Yes");
	                    System.out.println("  2. No");
	                    System.out.print("Please enter the number associated with your desired option: ");
	                    userChoice = getUserChoice(2);
	                    
	                    if (userChoice == 1) {
	                    	continue;
	                    }
	                    else {
	                    	clearConsole();
	                    	System.out.println("Would you like to return to the Finances Menu or the Main Menu?");
		                    System.out.println("  1. Return to Finance Menu");
		                    System.out.println("  2. Return to Main Menu");
		                    System.out.print("Please enter the number associated with your desired option: ");
		                    userChoice = getUserChoice(2);
		                    
		                    if (userChoice == 1) {
		                    	return false;
		                    }
		                    else {
		                    	return true;
		                    }
	                    }
                	}
                case 2: 
                    return false;
                case 3:
                    return true;
                }
            }
        }

    /**
     * Account Settings Menu
     *
     * @param currentUser - username of the currently signed-in user
     * @return true if user remains logged in, false if account was deleted or logout requested
     *
     * @author Aaron Madou
     * @author Shazadul Islam
     */
    public boolean accountSettingsMenu(String currentUser) {
    	clearConsole();
        System.out.println(currentUser + " Account Settings:");
        System.out.println("  1. Change Password");
        System.out.println("  2. Delete Account");
        System.out.println("  3. Return to Main Menu");
        System.out.print("Please enter the number associated with your desired option: ");
        int settingsChoice = getUserChoice(3);
        
        boolean inSettings = true;
        while (inSettings){
            switch (settingsChoice){
                case 1:
                	clearConsole();
                    System.out.println("Before you can change your password, please enter your current password. ");
                    System.out.print("Password: ");
                    String currentPass = scanner.nextLine();
                    
                case 2:
                    clearConsole();
                    System.out.println("Are you sure you want to delete this account: " + currentUser + "? ");
                    System.out.println("  1. Yes");
                    System.out.println("  2. No");
                    System.out.print("Please enter the number associated with your desired option: ");
                    int sureDelAccount = getUserChoice(2);

                    if (sureDelAccount == 1) {
                        // Ask ModuleHub / Accounts to delete the user
                        boolean deleted = moduleHub.callAccounts("deleteaccount", currentUser);

                        if (deleted) {
                        	clearConsole();
                            System.out.println("Your account has been terminated.");
                            System.out.print("Press enter when you are ready to return to the login page...");
                            scanner.nextLine();
                            // Tell main() that the user is NO LONGER logged in
                            return false;
                        } else {
                        	clearConsole();
                            System.out.println("Account deletion failed. Your account was not removed.");
                            System.out.print("Press enter when you are to ready return to user settings...");
                            scanner.nextLine();
                            break;  // stay logged in / in settings
                        }
                    } else {
                    	clearConsole();
                        System.out.print("Press enter when you are ready to return to user settings...");
                        scanner.nextLine();
                        break;
                    }

                case 3:
                    inSettings = false;
                    break;
            }
        }
        return true;
    }

    /**
     * Runs the entire application with the MainMenu as the main interface for the user to interact with
     * Makes calls to other menus that handle other parts of the application
     * Also lets the user log out and quit the program
     * 
     * @param args command line arguments (not used)
     * 
     * @author Shazadul Islam
     */
    public static void main(String[] args) {
    	System.out.println("Hamilton Heights Presents");
        System.out.println("Personal Finance Manager");
        
        MainMenu applicationInterface = new MainMenu();
        
        boolean running = true;
        while (running) {
        	String currentUser = applicationInterface.enter();
        	
        	boolean loggedIn = false;
        	if (currentUser != null) {
        		loggedIn = true;
        	}
        	
        	while(running && loggedIn) {
        		applicationInterface.clearConsole();
        		System.out.println("Personal Finance Manager:");
        		System.out.println("Main Menu: ");
                System.out.println("  1. Finances");
                System.out.println("  2. Settings");
                System.out.println("  3. Sign Out");
                System.out.println("  4. Exit Applicaiton");
                System.out.print("Please enter the number associated with your desired option: ");
                int mainMenuChoice = applicationInterface.getUserChoice(4);
                
        		switch (mainMenuChoice) {
                case 1: 
                	applicationInterface.financesMenu(currentUser);
                	break;
                case 2:
                	loggedIn = applicationInterface.accountSettingsMenu(currentUser);
                	break;
                case 3:
                	applicationInterface.moduleHub.logoutUser();
                	loggedIn = false;
                	break;
                case 4:
                	applicationInterface.moduleHub.logoutUser();
                	loggedIn = false;
                	running = false;
                	break;
        		}
        	}
        }
        applicationInterface.exitApplication();
    }
    
    /**
     * When the user requests to exit the application,
     * this function is called to clean up the scanner, 
     * relay a message to the user that have exited the program,
     * and returns the user to the normal command line in terminal
     * 
     * @author Shazadul Islam
     */
    public void exitApplication() {
        scanner.close();
        clearConsole();
        System.out.println("Thank you for using Hamilton Heights Personal Finance Manager!");
        System.exit(0);
    }
}
