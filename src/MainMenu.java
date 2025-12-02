import java.util.Scanner;

/**
 * This class allows the user to navigate to all of the different "pages" of the
 * program. Handles all menu navigation and user routing throughout the Personal
 * Finance Manager application.
 *
 * MainMenu only talks to one dependency: ModuleHub. ModuleHub then forwards
 * requests to Accounts, Storage, Reports, Prediction, and Validation.
 *
 * @author Shazadul Islam
 */
public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);
;
    // Single integration point to the rest of the system
    private final ModuleHub moduleHub;

    /**
     * Default constructor for MainMenu. Wires in a new ModuleHub instance.
     */
    public MainMenu() {
        moduleHub = new ModuleHub();
    }

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
     * Gets the user's choice of where they want to navigate go
     *
     * @param numChoices - the number of valid number options the user has
     *                   (1..numChoices)
     * @return numChoice - the menu option chosen by the user
     */
    public int getUserChoice(int numChoices) {
        // Validate and route based on user choice
        while (true) {
            try {
                int numChoice = Integer.parseInt(scanner.nextLine());

                if (numChoice >= 1 && numChoice <= numChoices) {
                    return numChoice;
                }

                else {
                    System.out.print(
                            "Please enter a valid number associated with an option displayed in the main menu (1-"
                                    + numChoices + "): ");
                }

            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number associated with an option displayed in the main menu (1-"
                        + numChoices + "): ");
            }
        }
    }

    /**
     * Handles the initial entry flow: - Ask whether the user wants to sign in or
     * create an account - Loop until we have a successful sign-in
     *
     * @return loginUsername - the username of the successfully signed-in user
     */
    public String enter() {
        while (true) {
            System.out.println("Would you like to sign in or create a new account? ");
            System.out.println(" 1. Sign In");
            System.out.println(" 2. Create a new account");
            System.out.print("Please enter the number associated with your desired option: ");
            int userChoice = getUserChoice(2);
            //clearConsole();

            switch (userChoice) {
                case 1:
                    boolean isNotLoggedIn = true;
                    while (isNotLoggedIn) {
                        System.out.println("Enter your username and password: ");
                        System.out.print(" Username: ");
                        String loginUsername = scanner.nextLine();
                        System.out.print(" Password: ");
                        String loginPassword = scanner.nextLine();

                        boolean validLogin = moduleHub.loginUser(loginUsername, loginPassword);
                        if (validLogin) {
                            clearConsole();
                            return (loginUsername);
                        } else {
                            System.out.println("Your username or password was incorrect or that account does not exist.");
                            System.out.println();
                            System.out.println("Would you like to try logging in again or create a new account? ");
                            System.out.println(" 1. Try Logging in again");
                            System.out.println(" 2. Create a New Account");
                            System.out.print("Please enter the number associated with your desired option: ");
                            int retryChoice = getUserChoice(2);
                            clearConsole();
                            if (retryChoice == 2) {
                                // this code breaks out of sign-in while loop and
                                // since case 1 doesn't end in a break,
                                // the user can fall through to case 2 and
                                // begin registering a new account
                                break;
                            }
                        }
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

                        System.out.println(
                                "Are you sure about your username, password, recovery question, and recovery question answer "
                                        + "for your new account? ");
                        System.out.println(" 1. Yes");
                        System.out.println(" 2. No");
                        System.out.print("Please enter the number associated with your desired option: ");
                        int surety = getUserChoice(2);

                        boolean isValidAccount = moduleHub.registerUser(registerUsername, registerPassword,
                                registerSecretQuestion, registerSecretAnswer);

                        clearConsole();

                        if ((surety == 1) && isValidAccount) {
                            areYouSure = true;
                            System.out.println(
                                    "\nYour account has been created. Please sign in with your new credentials.\n");
                        } else if (!isValidAccount) {
                            System.out.println(
                                    "The account details you entered were invalid or the username already exists.");
                            System.out.println("Please follow the required format and try again.");
                            System.out.println();
                        } else {
                            clearConsole();
                        }
                    }
                    continue;// After successful registration, loop back and show the Sign In / Create menu again
            }
        }
    }

    /**
     * Returns the user to the main menu. If they are within a table of contents or
     * a module and would like to go to a different "page", then they can return to
     * the main menu and select where they would like to go next.
     *
     * @return mainMenuChoice - the user's main menu choice (1–3)
     */
    public int toMenu() {
        // Return to main menu
        System.out.println("Main Menu: ");
        System.out.println("  1. Financial Data");
        System.out.println("  2. Settings");
        System.out.println("  3. Sign Out");
        System.out.println("  4. Quit Program");
        System.out.print("Please enter the number associated with your desired option: ");

        int mainMenuChoice = getUserChoice(4);
        clearConsole();
        return mainMenuChoice;
    }

    /**
     *	NOTE: This method is deprecated. New module menu is handled by financialMenu
     * 
     * Return the user to a table of contents of modules. If they are within a
     * module and would like to go to a different module, then they can return to
     * the list of modules and select the next module that they would like to view.
     *
     * @return tableMenuChoice - the user's table of contents choice (1–3)
     */

    public int toModules() {
        // Return to modules list
        System.out.println("Financial Data:");
        System.out.println("  1. Reports");
        System.out.println("  2. Prediction");
        System.out.println("  3. Back to Main Menu");
        System.out.print("Please enter the number associated with your desired option: ");
        
        int tableChoice = getUserChoice(3);
        clearConsole();
        return tableChoice;
    }

    /**
     * Prints the settings page. For alpha, this just shows a placeholder and who is
     * currently signed in.
     *
     * @param currentUser - username of the currently signed-in user
     */
    public int displayAccountSettings(String currentUser) {
        // Display account settings menu
        System.out.println("Currently signed in as: " + currentUser);
        System.out.println();
        System.out.println("Settings:");
        System.out.println("  1. Change Password");
        System.out.println("  2. Reset Password");
        System.out.println("  3. Reset Secret Question and Answer (unfinished)");
        System.out.println("  4. Delete Account");
        System.out.println("  5. Return to Main Menu");
        System.out.print("Please enter the number associated with your desired option: ");
        int settingsChoice = getUserChoice(5);
        clearConsole();
        return settingsChoice;
    }

    /**
     * Runs the entire program
     */
    public void start() {
        //clearConsole();
        System.out.println("Hamilton Heights Presents");
        System.out.println("Personal Finance Manager");
        System.out.println();

        // Login / registration
        String currentUser = enter();

        boolean running = true;
        while (running) {
            int mainMenuChoice = toMenu();

            switch (mainMenuChoice) {
                case 1: // Table of contents → table of contents / Now being changed to Financial Menu
                	financialMenu(currentUser);
                	break;
                case 2:
                    // Settings stub; can later use moduleHub + Accounts for password / recovery changes
                    boolean inSettings = true;
                    while (inSettings){
                        int settingsChoice = displayAccountSettings(currentUser);
                        clearConsole();

                        switch (settingsChoice){
                            case 1:
                            	
                            	System.out.println("Currently signed in as: " + currentUser);
                            	System.out.println();
                                System.out.print("Before changing your password, please enter your current password: ");                              
                                
                                String currentPass = scanner.nextLine();
                                
                                boolean retry1 = true;
                                
                                while(true) {
                                	clearConsole();
                                	if(retry1) {
		                                if(moduleHub.loginUser(currentUser, currentPass)) {
		                                	
		                                    System.out.print("Please enter your new password: ");		                                    
		                                    String newPass = scanner.nextLine();
		                                    
		                                    moduleHub.resetUserPassword(currentUser, newPass);
		                                    
		                                    clearConsole();
		                                    System.out.println("Password successfully changed! \n");
		                                    
		                                	break;
		                                } else {
		                                      System.out.println("The password you entered is incorrect. Would you like to try again?");
		                                      System.out.println(" 1. Yes \n 2. No");
		                                      System.out.print("Please enter the number associated with your desired option: ");
		
		                                     int newChoice = getUserChoice(2);
		                                     clearConsole();
		                                     switch (newChoice) {
		                                     	case 1:
		                                     		System.out.print("Please enter your current password: ");  
		                                     		currentPass = scanner.nextLine();
		                                     		
		                                     		continue;                                 	
		                                     	case 2:
		                                     		retry1 = false;
		                                     		break;
		                                     }
		                                }
                                	} else { break; }
                                }
                                continue;
                                                            
                            case 2:
                            	System.out.println("To reset your password, please answer the following question:");                          	
                            	System.out.println(moduleHub.getUserSecretQuestion(currentUser));
                            	
                            	String answer = scanner.nextLine();
                            	boolean retry2 = true;
                            	
                            	while(true) { 
                            		clearConsole();
	                            	if(retry2) {	    
	                            		
	                            		if(moduleHub.verifyUserSecretAnswer(currentUser, answer)) {
	                            			System.out.print("Your password has been reset. What would you like to set your new password to be? \n Password: ");                            			
	                            			String newPass = scanner.nextLine();
		                                    
		                                    moduleHub.resetUserPassword(currentUser, newPass);
		                                    System.out.println("Password successfully changed!");		                                    
		                                    clearConsole();
		                                    break;
	                            		} else {
	                            			System.out.println("The answer you entered is incorrect. Would you like to try again?");
	                            			System.out.println(" 1. Yes \n 2. No");
	                            			System.out.print("Please enter the number associated with your desired option: ");
	                            			
	                            			int choice = getUserChoice(2);
	                            			clearConsole();
		                                    switch (choice) {
		                                    	case 1:
		                                    		System.out.println(moduleHub.getUserSecretQuestion(currentUser));  
		                                     		currentPass = scanner.nextLine();
		                                     		continue;                                 	
		                                    	case 2:
		                                    		retry2 = false;
		                                    		break;
		                                    }
	                            		}
	                            		
	                            	} else { break; }	                                                       	
                            	}
                            	
                            	continue;
                                
                            case 3:
                            	System.out.print("To reset your recovery question, please enter your password: ");    
                            	
                            	 String currentPassword = scanner.nextLine();
                                 
                                 boolean retry3 = true;
                                 
                                 while(true) {
                                 	clearConsole();
                                 	if(retry3) {
 		                                if(moduleHub.loginUser(currentUser, currentPassword)) {
 		                                	System.out.print("What would you like your recovery question to be? ");
 		                                	String registerSecretQuestion = scanner.nextLine();
 		                                	System.out.print("What would you like your recovery question answer to be? ");
 		                                	String registerSecretAnswer = scanner.nextLine();
 		                                    
 		                                	// TODO: Add a method in moduleHub to set the user's secret question and answer
 		                                    //moduleHub.setUserSecretQuestionAndAnswer(currentUser, registerSecretQuestion, registerSecretAnswer);
 		                                    
 		                                    clearConsole();
 		                                    System.out.println("Recovery question successfully changed! \n");
 		                                    
 		                                	break;
 		                                } else {
 		                                      System.out.println("The password you entered is incorrect. Would you like to try again?");
 		                                      System.out.println(" 1. Yes \n 2. No");
 		                                     System.out.print("Please enter the number associated with your desired option: ");
 		
 		                                     int newChoice = getUserChoice(2);
 		                                     clearConsole();
 		                                     switch (newChoice) {
 		                                     	case 1:
 		                                     		System.out.print("Please enter your current password: ");  
 		                                     		currentPassword = scanner.nextLine();
 		                                     		
 		                                     		continue;                                 	
 		                                     	case 2:
 		                                     		retry3 = false;
 		                                     		break;
 		                                     }
 		                                }
                                 	} else { break; }
                                 }
                                 continue;
                                
                            case 4:
                                System.out.println("Are you sure you want to delete this account: "+ currentUser + "? ");
                                System.out.println(" 1. Yes");
                                System.out.println(" 2. No");
                                System.out.print("Please enter the number associated with your desired option: ");
                                int sureDelAccount = getUserChoice(2);
                                clearConsole();

                                if (sureDelAccount == 1) {
                                    moduleHub.callAccounts("deleteaccount", currentUser);
                                    System.out.println("Your account has been terminated.");
                                    System.out.println("You are being returned to the login page(coming soon for beta).");
                                    System.out.println();
                                } else {
                                    System.out.println("Returning to user settings.");
                                    System.out.println();
                                    break;
                                }
                            case 5:
                                inSettings = false;
                                break;
                        }
                    }
                    break;
                case 3: 
                	moduleHub.logoutUser();
                	// Return to the while loop and restart the program
                	return;
                case 4:
                    running = false;
                    break;
            }
        }
        exitProgram();
    }
    
    /**
     * Used for validating a date from user input.
     * Currently used in financialMenu and ReportsMenu
     * 
     * @return the year or month
     * @param the type of date yow need. (e.g. year or month)
     * @author Aaron Madou
     */ 
    private int getUserDate(String type) {
    	int date;
  		while (true) {
   			try {
  				date = Integer.parseInt(scanner.nextLine());
  				return date;
   			} catch (NumberFormatException e) {
   				clearConsole();
   				System.out.print("Please enter a valid " + type + " : ");
   			}
   		}
    }
    
    /**
     * Handles the Financial Reports menu
     * 
     * @param currentUser - username of the currently signed-in user
     * @author Aaron Madou
     */ 
    public void financialMenu(String currentUser) {
    	while(true) {
    		 System.out.println("Financial Data: ");
    	     System.out.println("  1. Upload CSV");
    	     System.out.println("  2. Reports");
    	     System.out.println("  3. Predictions");
    	     System.out.println("  4. Manage Data (unfinished)");
    	     System.out.println("  5. Back");
    	     System.out.print("Please enter the number associated with your desired option: ");

  	        int userChoice = getUserChoice(5);
   	        clearConsole();
   	        
   	        switch (userChoice) {
   	        	case 1:
   	        		System.out.println("---CSV Loader ---");
   	             
   	        		System.out.print("Please enter the year this CSV file is for: ");
   	        		int year = getUserDate("year");
   	          
   	             System.out.println("\nPlease enter the name of the CSV file to load.");
   	             System.out.println("• If the CSV is in the same folder as the JAR, just type:   data.csv");
   	             System.out.println("• If it’s somewhere else, provide the full path.");
   	             System.out.print("File name: ");
   	             String csvPath = scanner.nextLine();
   	             
   	             clearConsole();
  	             moduleHub.uploadCSVData(currentUser, csvPath, year);	             
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
   	        		continue;
   	        	case 5:
   	        		return;
   	        }
    	}
    }
    
    /**
     * Handles the Reports Menu
     * 
     * @return Determines if user wants to go to the back to Financial Data or to the Main Menu
     * @param currentUser - username of the currently signed-in user
     * @author Aaron Madou
     */ 
    public boolean reportsMenu(String currentUser) {
    	while(true) {
    		System.out.println("Reports Menu: ");
    		System.out.println("Available years: (Unfinished)");
    		System.out.println("  1. Yearly Summary");
    		System.out.println("  2. Month Breakdown");
    		System.out.println("  3. Category Analysis");
    		System.out.println("  4. Full Report");
   	    	System.out.println("  5. Back");
   	    	System.out.print("Please enter the number associated with your desired option: ");

   	    	int userChoice = getUserChoice(5);
   	    	clearConsole();
   	    	if(userChoice == 5) { return false; }
   	    	
   	    	System.out.print("Please enter the year you would like to view: ");
   			int year = getUserDate("year");
   			clearConsole();
  	        
   	    	switch (userChoice) {
   	    		case 1:
   	    			moduleHub.viewReport(currentUser, year, "yearly");
   	    			break;
   	    			
   	    		case 2: 
   	    			moduleHub.viewReport(currentUser, year, "monthly");
   	    			break;
   	    		case 3: 
   	    			moduleHub.viewReport(currentUser, year, "category");
	        		break;
	        	case 4:
	        		moduleHub.viewReport(currentUser, year, "full");
	        		break;
   	    	}
   	    	
 			System.out.println("\nWhat would you like to do next?");
   			System.out.println(" 1. View another report \n 2. Back to Financial Data \n 3. Back to Main Menu");
   			System.out.print("Please enter the number associated with your desired option: ");
   			
   			userChoice = getUserChoice(3);
   	    	clearConsole();
   	    	switch (userChoice) {
   	    		case 1: 
   	    			continue;
   	    		case 2: 
   	    			return false; 
   	    		case 3:
   	    			return true;
   	    	}
    	}
    }
    
    /**
     * Handles the Prediction Menu
     * 
     * @return Determines if user wants to go to the back to Financial Data or to the Main Menu
     * @param currentUser - username of the currently signed-in user
     * @author Aaron Madou
     */ 
    public boolean predictionMenu(String currentUser) {
    	while(true) {
    		System.out.println("Prediction Menu: ");
    		System.out.println("  1. Summary Report");
    		System.out.println("  2. Deficit Analysis");
    		System.out.println("  3. Surplus Analysis");
   	    	System.out.println("  5. Back");
   	    	System.out.print("Please enter the number associated with your desired option: ");

   	    	int userChoice = getUserChoice(4);
   	    	clearConsole();
   	    	if(userChoice == 4) { return false; }
   	    	
   	    	System.out.print("Please enter the year you would like to view: ");
   			int year = getUserDate("year");
   			clearConsole();
  	        
   	    	switch (userChoice) {
   	    		case 1:
   	    			moduleHub.runPrediction(currentUser, year, "summary");
   	    			break;
   	    			
   	    		case 2: 
   	    			moduleHub.runPrediction(currentUser, year, "deficit");
   	    			break;
   	    		case 3: 
   	    			moduleHub.runPrediction(currentUser, year, "surplus");
	        		break;

   	    	}
   	    	
 			System.out.println("\nWhat would you like to do next?");
   			System.out.println(" 1. View another prediction (analysis?) \n 2. Back to Financial Data \n 3. Back to Main Menu");
   			System.out.print("Please enter the number associated with your desired option: ");
   			
   			userChoice = getUserChoice(3);
   	    	clearConsole();
   	    	switch (userChoice) {
   	    		case 1: 
   	    			continue;
   	    		case 2: 
   	    			return false; 
   	    		case 3:
   	    			return true;
   	    	}
    	}
    }
    
    /**
     * Lets the user exit the program to desktop from anywhere within the code.
     */
    public void exitProgram() {
        scanner.close();
        System.out.println("Goodbye!");
        System.exit(0);
    }

    public static void main(String[] args) {
        MainMenu programMenu = new MainMenu();
        while(true) {
        	programMenu.start();
        }
    }
}
