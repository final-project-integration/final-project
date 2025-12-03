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
 * @author Aaron Madou
 */
public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);
    
    /**
    Single integration point to the rest of the system
    */
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
     */
    public void clearConsole() {
        // Clear console screen when running in terminal and handle whitespace between "pages" when running in console
    	System.out.println();
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Gets the user's choice of where they want to navigate go
     *
     * @param numChoices - the number of valid number options the user has (1..numChoices)
     * @return numChoice - the menu option chosen by the user
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
    
    public void moveOn() {
    	System.out.print("Press enter when you are ready to move on...");
    	scanner.nextLine();    
    	}
    
    /**
     * Handles the initial entry flow: - Ask whether the user wants to sign in or
     * create an account - Loop until we have a successful sign-in
     *
     * @return loginUsername - the username of the successfully signed-in user
     */
    public String enter() {
        while (true) {
        	clearConsole();
            System.out.println("Would you like to sign in or create a new account? ");
            System.out.println(" 1. Sign In");
            System.out.println(" 2. Create a new account");
            System.out.print("Please enter the number associated with your desired option: ");
            int userChoice = getUserChoice(2);

            switch (userChoice) {
                case 1:
                    boolean isNotLoggedIn = true;
                    while (isNotLoggedIn) {
                    	clearConsole();
                        System.out.println("Enter your username and password: ");
                        System.out.print(" Username: ");
                        String loginUsername = scanner.nextLine();
                        System.out.print(" Password: ");
                        String loginPassword = scanner.nextLine();

                        boolean validLogin = moduleHub.loginUser(loginUsername, loginPassword);
                        if (validLogin) {
                            return (loginUsername);
                        } 
                        
                        else {
                        	clearConsole();
                            System.out.println("Your username or password was incorrect or that account does not exist.");
                            System.out.println("Would you like to try logging in again or create a new account? ");
                            System.out.println(" 1. Try Logging in again");
                            System.out.println(" 2. Create a New Account");
                            System.out.print("Please enter the number associated with your desired option: ");
                            int retryChoice = getUserChoice(2);
                            if (retryChoice == 2) {
                                // this code breaks out of isNotLoggedIn while loop and
                                // since case 1 doesn't end in a break,
                                // the user can fall through to case 2 and
                                // begin registering a new account
                                break;
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
                        System.out.println("Confirm account creation with this username, password, recovery question, and recovery question answer? ");
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
                                System.out.println("Your account has been succesffuly created.");
                                System.out.print("Press enter when you are ready to sign in with your new credentials...");
                                scanner.nextLine();
                                createdAccount = true;
                            }

                        } else {
                            // User said "No"
                            clearConsole();
                            System.out.println("Let's try creating an account again, shall we?");
                            System.out.print("Press enter when you are ready to try again...");
                            scanner.nextLine();
                        }

                    }
                    continue;// After successful registration, loop back and show the Sign In / Create menu again
            }
        }
    }
    
    /**
     * Used for validating a date from user input.
     * Currently used in financialMenu and ReportsMenu
     * 
     * @return the year or month
     * @param the type of date yow need. (e.g. year or month)
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
     * Handles the Financial Reports menu
     * 
     * @param currentUser - username of the currently signed-in user
     */ 
    public void financesMenu(String currentUser) {
    	while(true) {
    		 clearConsole();
    		 System.out.println("Finances Menu:");
    	     System.out.println("  1. Upload CSV");
    	     System.out.println("  2. Reports");
    	     System.out.println("  3. Predictions");
    	     System.out.println("  4. Data Management");
    	     System.out.println("  5. Back to Main Menu");
    	     System.out.print("Please enter the number associated with your desired option: ");
  	        int userChoice = getUserChoice(5);
  	        
   	        switch (userChoice) {
   	        	case 1:
   	        		clearConsole();
   	        		System.out.println("---CSV Loader---");
   	        		System.out.print("Please enter the year this CSV file is for: ");
   	        		int year = getUserYear();
   	        		System.out.println();
   	        		System.out.println("Please enter the name of the CSV file you want to upload.");
   	        		System.out.println("• If the CSV you want to upload is in the same folder as the JAR, just type: data.csv");
   	        		System.out.println("• However, if it’s somewhere else, please provide the full path.");
   	        		System.out.print("File name: ");
   	        		String csvPath = scanner.nextLine();
   	        		System.out.println();
   	        		
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
     * Handles the Reports Menu
     * 
     * @return Determines if user wants to go to the back to Financial Data or to the Main Menu
     * @param currentUser - username of the currently signed-in user
     */ 
    public boolean reportsMenu(String currentUser) {
    	while(true) {
    		clearConsole();
    		moduleHub.callStorage("listyears", currentUser, 0);
   	    	System.out.print("Please enter the year you would like reports about: ");
   			int year = getUserYear();
   			
   			boolean isWorkingWithYear = true;
   			clearConsole();
   			System.out.println("What kind of information would you like about data from " + year + "?");
   			System.out.println("Available Reports for " + year +":");
    		System.out.println("  1. Yearly Summary");
    		System.out.println("  2. Month Breakdown");
    		System.out.println("  3. Category Analysis");
    		System.out.println("  4. Full Report");
    		System.out.println("  5. Enter a different year");
    		System.out.println("  6. Back to Finances Menu");
   	    	System.out.println("  7. Return to Main Menu");
   	    	System.out.print("Please enter the number associated with your desired option: ");
   	    	int userChoice = getUserChoice(7);
   			
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
	        		continue;
	        	case 6:
	        		return false;
	        	case 7:
	        		return true;
   	    	}

   	    	clearConsole();
 			System.out.println("What would you like to do next?");
   			System.out.println("  1. View Reports for another year");
   			System.out.println("  2. Back to Finances Menu");
   			System.out.println("  3. Back to Main Menu");
   			System.out.print("Please enter the number associated with your desired option: ");
   			userChoice = getUserChoice(3);
   			
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
     */ 
    public boolean predictionMenu(String currentUser) {
    	while(true) {
    		clearConsole();
    		moduleHub.callStorage("listyears", currentUser, 0);
   	    	System.out.print("Please enter the year you would like predictions about: ");
   			int year = getUserYear();
   			
    		clearConsole();
   			System.out.println("What kind of Predictions would you like about the data from " + year + "?");
   			System.out.println("Available Predictions for " + year + ":");
    		System.out.println("  1. Summary Report");
    		System.out.println("  2. Deficit Analysis");
    		System.out.println("  3. Surplus Analysis");
    		System.out.println("  4. Enter a different year");
    		System.out.println("  5. Back to Finances Menu");
   	    	System.out.println("  6. Return to Main Menu");
   	    	System.out.print("Please enter the number associated with your desired option: ");
   	    	int userChoice = getUserChoice(6);
   			
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
   	    			continue;
   	    		case 5:
   	    			return false;
   	    		case 6:
   	    			return true;
   	    	}
   	    	
   	    	clearConsole();
 			System.out.println("What would you like to do next?");
   			System.out.println("  1. View Predictions for another year");
   			System.out.println("  2. Back to Finances Menu");
   			System.out.println("  3. Back to Main Menu");
   			System.out.print("Please enter the number associated with your desired option: ");
   			userChoice = getUserChoice(3);
   	    	
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
    Handles the Data Management Menu
    @return Determines if user wants to go to the back to Financial Data or to the Main Menu
    @param currentUser - username of the currently signed-in user
    */
    public boolean dataManagementMenu(String currentUser) {
        while(true) {
        	clearConsole();
            System.out.println("Data Management Menu: ");
            System.out.println("  1. Delete CSV file");
            System.out.println("  2. Back to Finances Menu");
            System.out.println("  3. Back to Main Menu");
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
     * Prints the settings page. For alpha, this just shows a placeholder and who is
     * currently signed in.
     *
     * @param currentUser - username of the currently signed-in user
     */
    public boolean accountSettingsMenu(String currentUser) {
        // Display account settings menu
    	clearConsole();
        System.out.println("Currently signed in as: " + currentUser);
        System.out.println();
        System.out.println("Settings:");
        System.out.println("  1. Change Password");
        System.out.println("  2. Reset Password");
        System.out.println("  3. Reset Secret Question and Answer (unfinished)");
        System.out.println("  4. Delete Account");
        System.out.println("  5. Return to Main Menu");
        System.out.println("Please enter the number associated with your desired option: ");
        int settingsChoice = getUserChoice(5);
        
        boolean inSettings = true;
        while (inSettings){
            switch (settingsChoice){
                case 1:
                	clearConsole();
                	System.out.println("Currently signed in as: " + currentUser);
                    System.out.print("Before changing your password, please enter your current password: ");
                    String currentPass = scanner.nextLine();

                    boolean retry1 = true;
                    while(true) {
                    	clearConsole();
                    	if(retry1) {
                            if(moduleHub.loginUser(currentUser, currentPass)) {
                            	 System.out.print("Please enter your new password: ");
                                 String newPass = scanner.nextLine();

                                 //necessary for accounts team keep
                                 System.out.println("For security, please answer your recovery question:");
                                 System.out.println(moduleHub.getUserSecretQuestion(currentUser));
                                 String secretAnswer = scanner.nextLine();

                                 moduleHub.resetUserPassword(currentUser, secretAnswer, newPass);

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
                            //necessary for accounts team keep
                                if (moduleHub.verifyUserSecretAnswer(currentUser, answer)) {
                                    System.out.print("Your password has been reset. What would you like to set your new password to be? \n Password: ");
                                    String newPass = scanner.nextLine();

                                    moduleHub.resetUserPassword(currentUser, answer, newPass);
                                    System.out.println("Password successfully changed!");
                                    clearConsole();
                                    break;
                                }else {
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
                             	moduleHub.updateUserSecretQuestionAndAnswer(currentUser, registerSecretQuestion,registerSecretAnswer);

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
                    clearConsole();
                    System.out.println("Are you sure you want to delete this account: " + currentUser + "? ");
                    System.out.println("  1. Yes");
                    System.out.println("  2. No");
                    System.out.print("Please enter the number associated with your desired option: ");
                    int sureDelAccount = getUserChoice(2);

                    clearConsole();
                    if (sureDelAccount == 1) {
                        // Ask ModuleHub / Accounts to delete the user
                        boolean deleted = moduleHub.callAccounts("deleteaccount", currentUser);

                        if (deleted) {
                            // Also log out this user so the session is gone
                            moduleHub.logoutUser();

                            System.out.println("Your account has been terminated.");
                            System.out.print("Press enter when you are ready to return to the login page...");
                            scanner.nextLine();
                            // Tell main() that the user is NO LONGER logged in
                            return false;
                        } else {
                            System.out.println("Account deletion failed. Your account was not removed.");
                            System.out.print("Press enter when you are to ready return to user settings...");
                            scanner.nextLine();
                            break;  // stay logged in / in settings
                        }
                    } else {
                        System.out.print("Press enter when you are ready to return to user settings...");
                        scanner.nextLine();
                        break;
                    }

                case 5:
                    inSettings = false;
                    break;
            }
        }
        return true;
    }

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
        		System.out.println("Main Menu: ");
                System.out.println("  1. Finances");
                System.out.println("  2. Settings");
                System.out.println("  3. Sign Out");
                System.out.println("  4. Quit Program");
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
                	loggedIn = false;
                	running = false;
                	break;
        		}
        	}
        }
        applicationInterface.exitProgram();
    }
    
    /**
     * Lets the user exit the program to desktop from anywhere within the code.
     */
    public void exitProgram() {
        scanner.close();
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
