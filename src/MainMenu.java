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
final class MainMenu {
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
    private MainMenu() {
    	moduleHub = new ModuleHub();
    }

    /**
     * Only creates a new line when the program is being run in the eclipse console
     * Clears the screen when the program is being run in the terminal
     * Provides a clean display for new menu or information screens
     * 
     * @author Aaron Madou
     */
    private void clearConsole() {
    	System.out.println();
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Provides a buffer for when text needs to be displayed on the console or terminal 
     * before moving on to the next part of the program
     * 
     * @author Shazadul Islam
     */
    private void moveOn() {
    	System.out.print("Press enter when you are ready to move on...");
    	scanner.nextLine();
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
    private int getUserChoice(int numChoices) {
        // Validate and route based on user choice
        while (true) {
            try {
                int numChoice = Integer.parseInt(scanner.nextLine());
                if (numChoice < 1 || numChoice > numChoices) {
                	System.out.print("Please enter a valid number associated with an option displayed in the menu (1-" + numChoices + ") and then press enter: ");
                	continue;
                }
                return numChoice;

            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number associated with an option displayed in the menu (1-" + numChoices + ") and then press enter: ");
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
  	   				System.out.print("Please enter a valid year and then press enter: ");
  					continue;
  				}
  				return year;
   			} catch (NumberFormatException e) {
   				System.out.print("Please enter a valid year and then press enter: ");
   			}
   		}
    }
    
    enum AccountRecoverState{
    	USERNAME_DNE,
    	INCORRECT_ANSWER,
    	SUCCESSFUL_PASSWORD_CHANGE,
    	RETURN_TO_MENU
    }
    
    private AccountRecoverState accountRecover(String usernameRecovering) {
    	//If the account does not exist...
    	String secretQuestion = moduleHub.getUserSecretQuestion(usernameRecovering);
    	if (secretQuestion == null) {
    		clearConsole();
    		//Return the user to the login menu
    		System.out.println("No account with the username, " + usernameRecovering + ", exists.");
    		return AccountRecoverState.USERNAME_DNE;
    	}

    	//If the account exists...
    	clearConsole();
    	//Print out the user's security question
    	System.out.println("To recover your account, please answer the security question for the account, " + usernameRecovering + ", below and then press enter.");
    	System.out.println(secretQuestion);
    	//Get the user's answer to their security question
    	System.out.print("Answer: ");
    	String loginSecretAnswer = scanner.nextLine();

    	//If the user's answer to the their account's security question is incorrect
    	if (!moduleHub.verifyUserSecretAnswer(usernameRecovering,loginSecretAnswer)) {
    		clearConsole();
    		System.out.println("Your answer to the security question was incorrect.");
    		return AccountRecoverState.INCORRECT_ANSWER;
    	}

    	//If the user's answer to their account's security question is correct
    	while (true) {
    		clearConsole();
    		//Let them enter a new password
    		System.out.println("Please enter a new password for the account, " + usernameRecovering + ", below and then press enter.");
    		System.out.print("  New Password: ");
    		String loginSecretPassword = scanner.nextLine();
    		System.out.print("  Confirm your password: ");
    		String confirmPassword = scanner.nextLine();
    		
    		if (!confirmPassword.equals(loginSecretPassword)) {
    		    System.out.println("Passwords do not match. Try again.");
    		    continue;
    		}
    		
    		//Attempt to reset
    		//returns true if password passes requirements and was saved
    		//returns false if password failed requirements and was not saved
    		boolean passwordAccepted = moduleHub.resetUserPassword(usernameRecovering, loginSecretAnswer, loginSecretPassword);

    		//If their new password is valid...
    		if (passwordAccepted) {
    			clearConsole();
    			System.out.println("The password for the account, " + usernameRecovering + ", has been successfully changed.");
    			return AccountRecoverState.SUCCESSFUL_PASSWORD_CHANGE;
    		}

    		//If their new password is invalid..
    		clearConsole();
    		System.out.println("Your new password did not meet the requirements.");
    		System.out.println("What would you like to do?");
    		System.out.println("  1. Try creating a new password for your account, " + usernameRecovering + ", again");
    		System.out.println("  2. Return to the login menu");
    		System.out.println("  3. Exit the application");
    		System.out.print("Please enter the number associated with your desired option and then press enter: ");
    		int recoverUserChoice = getUserChoice(3);

    		//Try creating a new password for your account again
    		if (recoverUserChoice == 1) {
    			continue;
    		}
    		//Return to login menu
    		else if (recoverUserChoice == 2) {
    			return AccountRecoverState.RETURN_TO_MENU;
    		}
    		//Exit the application
    		else if (recoverUserChoice == 3) {
    			exitApplication();
    		}
    	}
    }
    
    /**
     * Handles the log in of the user
     * 
     * @return loginUsername - If a valid login event happens, then the username of that valid login attempt is returned
     * 		   null - If anything other than the desired valid login attempt happens, then null is returned
     * 
     * @author Shazadul Islam
     */
    private String handleLogIn() {
    	while (true) {
    		clearConsole();
    		//Go through login process
    		System.out.println("Enter your username below and then press enter. ");
    		System.out.print("  Username: ");
    		String loginUsername = scanner.nextLine();
    		System.out.println("Enter your password below and then press enter. ");
    		System.out.print("  Password: ");
    		String loginPassword = scanner.nextLine();

    		//Check if the login is valid
    		boolean validLogin = moduleHub.loginUser(loginUsername, loginPassword);

    		//If the login is valid, then return with the username
    		if (validLogin) {
    			return (loginUsername);
    		} 
    		
    		//If the login is invalid, then print invalid login menu
    		clearConsole();
    		//Print invalid login menu
    		System.out.println("The username or password you entered was incorrect.");
    		System.out.println("Would you like to do? ");
    		System.out.println("  1. Try logging in again");
    		System.out.println("  2. Forgot your password?");
    		System.out.println("  3. Return to the login menu");
    		System.out.println("  4. Exit application");
    		System.out.print("Please enter the number associated with your desired option and then press enter: ");
    		int loginRetryChoice = getUserChoice(4);

    		//If the user wants to try logging in again, jump back to the start of the loop, so that they can try logging in again
    		switch(loginRetryChoice) {
    		case 1:
    			continue;

    		//If the user forgot their password, go through the account recovery process
    		case 2: 
    			//Run account recovery process with username of their choice
    			clearConsole();
    			System.out.println("Is " + loginUsername + " the account you are trying to recover?");
    			System.out.println("  1. Yes");
        		System.out.println("  2. No");
        		System.out.print("Please enter the number associated with your desired option and then press enter: ");
        		int correctUsername = getUserChoice(2);
        		
        		String loginSecretUsername = null;
        		if (correctUsername == 1) {
        			loginSecretUsername = loginUsername;
        		}
        		else {
        			clearConsole(); 
	    			System.out.println("Please enter the username of the account you are trying to recover and then press enter."); 
	    			System.out.print("  Username: "); 
	    			loginSecretUsername = scanner.nextLine();
        		}
        		
        		boolean isNotDoneRecovering = true;
        		int retries = 0;
        		while (isNotDoneRecovering) {
        			AccountRecoverState forgotPasswordReturn = accountRecover(loginSecretUsername);

        			switch (forgotPasswordReturn) {
        			case USERNAME_DNE:
        				System.out.println("What would you like to do?");
        				System.out.println("  1. Try recovering a different account");
        				System.out.println("  2. Return to the login menu");
        				System.out.println("  3. Exit the application");
        				System.out.print("Please enter the number associated with your desired option and then press enter: ");
        				int recoverAgainChoice = getUserChoice(3);
        				
        				if (recoverAgainChoice == 1) {
        					clearConsole(); 
        					System.out.println("Please enter the username of the account you are trying to recover and then press enter."); 
        					System.out.print("  Username: "); 
        					loginSecretUsername = scanner.nextLine();
        					continue;
        				}
        				 else if (recoverAgainChoice == 2) {
        					return null; 
        				}
        				else {
        			        exitApplication();
        			    }
        				break;

        			case INCORRECT_ANSWER:
        				if (retries < 3) {
	        				System.out.println("What would you like to do?");
	        			    System.out.println("  1. Try answering again");
	        			    System.out.println("  2. Return to the login menu");
	        			    System.out.println("  3. Exit the application");
	        			    System.out.print("Please enter the number associated with your desired option and then press enter: ");
	        			    int incorrectChoice = getUserChoice(3);
	
	        			    if (incorrectChoice == 1) {
	        			    	retries++;
	        			        continue;
	        			    } 
	        			    else if (incorrectChoice == 2) {
	        			        return null;  
	        			    } 
	        			    else {
	        			        exitApplication();
	        			    }
        				}
        				
        				else {
        					System.out.println();
        					System.out.println("Too many account recovery attempts.");
        					System.out.println("You can either return to the login menu or exit the application.");
        					System.out.println("What would you like to do?");
	        			    System.out.println("  1. Return to the login menu");
	        			    System.out.println("  2. Exit the application");
	        			    System.out.print("Please enter the number associated with your desired option and then press enter: ");
	        			    
	        			    int exitChoice = getUserChoice(2);
	        			    if (exitChoice == 2) {
	        			    	exitApplication();
	        			    } 
	        			    else {
	        			    	return null; 
	        			    } 
        				}
        				
        			    break;

        			case SUCCESSFUL_PASSWORD_CHANGE:
        				System.out.print("Press enter when you are ready to return to the login menu and try logging in with your updated password...");
        				scanner.nextLine();
        				return null; //returns to login menu.

        			case RETURN_TO_MENU:
        				return null; //returns to login menu.
        			}
        		}
    		case 3: 
    			return null; //returns to login menu.
    		case 4: 
    			exitApplication();
    		} 
    	}
    }
    
    private void handleAccountCreation() {
    	while (true) {
    		clearConsole();
    		System.out.println("Username Rules: ");
    		System.out.println("• Cannot be empty or only whitespace");
    		System.out.println("• Cannot begin or end with a space");
    		System.out.println("• Must only include letter or numbers");
    		System.out.println("• Must be at least 3 characters and at most 20 characters");
    		System.out.println();
    		
    		System.out.println("Enter your username below and then press enter. ");
    		System.out.print("What would you like your username to be? ");
    		String registerUsername = scanner.nextLine();
    		
    		String registerPassword = null;
    		while(true) {
    			clearConsole();
    			System.out.println("Password Rules: ");
    			System.out.println("• Cannot be empty or only whitespace");
    			System.out.println("• Cannot begin or end with a space");
    			System.out.println("• Must be at least 5 characters and at most 30 characters");
    			System.out.println();

    			System.out.println("Enter your password below and then press enter. ");
    			System.out.print("What would you like your password to be? ");
    			registerPassword = scanner.nextLine();
    			System.out.println();

    			System.out.println("Re-enter your password below to confirm it and then press enter. ");
    			System.out.print("Confirm your password: ");
    			String confirmPassword = scanner.nextLine();

    			if (confirmPassword.equals(registerPassword)) {
    				break;
    			}
    			else {
    				clearConsole();
    				System.out.println("Passwords do not match.");
    				System.out.println("What would you like to do?");
    				System.out.println("  1. Try creating a password for your new account again");
    				System.out.println("  2. Return to the login menu");
    				System.out.println("  3. Exit the application");
    				System.out.print("Please enter the number associated with your desired option and then press enter: ");
    				int passwordRetryChoice = getUserChoice(3);

    				if (passwordRetryChoice == 1) {
    					continue;
    				}
    				else if (passwordRetryChoice == 2) {
    					return;
    				}
    				else if (passwordRetryChoice == 3) {
    					exitApplication();
    				}
    			}
    		}
    		
    		clearConsole();
    		System.out.println("Pick one of the security questions below and then press enter. ");
    		System.out.println("  1. What was your childhood nickname?");
    		System.out.println("  2. What is the name of your favorite childhood friend?");
    		System.out.println("  3. What street did you live on in third grade?");
    		System.out.println("  4. What is the middle name of your oldest child?");
    		System.out.println("  5. What school did you attend for sixth grade?");
    		System.out.println("  6. What was the name of your first stuffed animal?");
    		System.out.println("  7. What was the name of the first school you remember attending?");
    		System.out.println("  8. What was the destination of your most memorable school field trip?");
    		System.out.println("  9. What was your maths teacher’s surname in your 8th year of school?");
    		System.out.println("  10. What was the name of your first stuffed toy?");
    		System.out.println("  11. What was your driving instructor’s first name?");
    		System.out.println();
    		
    		System.out.println("Please enter the number associated with the question that you want to be your security question and then press enter: ");
    		System.out.print("What would you like your security question to be? ");
    		int secretQuestionChoice = getUserChoice(11);
    		
    		String registerSecretQuestion = null;
    		switch(secretQuestionChoice) {
    	    case 1:
    	        registerSecretQuestion = "What was your childhood nickname?";
    	        break;
    	    case 2:
    	        registerSecretQuestion = "What is the name of your favorite childhood friend?";
    	        break;
    	    case 3:
    	        registerSecretQuestion = "What street did you live on in third grade?";
    	        break;
    	    case 4:
    	        registerSecretQuestion = "What is the middle name of your oldest child?";
    	        break;
    	    case 5:
    	        registerSecretQuestion = "What school did you attend for sixth grade?";
    	        break;
    	    case 6:
    	        registerSecretQuestion = "What was the name of your first stuffed animal?";
    	        break;
    	    case 7:
    	        registerSecretQuestion = "What was the name of the first school you remember attending?";
    	        break;
    	    case 8:
    	        registerSecretQuestion = "What was the destination of your most memorable school field trip?";
    	        break;
    	    case 9:
    	        registerSecretQuestion = "What was your maths teacher’s surname in your 8th year of school?";
    	        break;
    	    case 10:
    	        registerSecretQuestion = "What was the name of your first stuffed toy?";
    	        break;
    	    case 11:
    	        registerSecretQuestion = "What was your driving instructor’s first name?";
    	        break;
    		}
    		
    		clearConsole();
    		System.out.println("The security question you chose was: " + registerSecretQuestion);
    		System.out.print("What would you like your answer to your chosen security question to be? ");
    		String registerSecretAnswer = scanner.nextLine();
    		
    		clearConsole();
    		System.out.println("Please confirm that these are the credentials that you want for your account.");
    		System.out.println("Username: " + registerUsername);
    		System.out.println("Password: " + "*".repeat(registerPassword.length()));
    		System.out.println("Security Question: " + registerSecretQuestion);
    		System.out.println("Security Question Answer: " + "*".repeat(registerSecretAnswer.length()));
    		System.out.println("Are you sure these are the credentials that you want for your account? ");
    		System.out.println(" 1. Yes");
    		System.out.println(" 2. No");
    		System.out.print("Please enter the number associated with your desired option and then press enter: ");
    		int surety = getUserChoice(2);

    		if (surety == 1) {
    			// User has not made any errors, so proceed with registration
    			boolean isValidAccount = moduleHub.registerUser(registerUsername, registerPassword, registerSecretQuestion, registerSecretAnswer, true);

    			if (isValidAccount) {
    				clearConsole();
    				System.out.println("Your account has been successfully created.");
    				System.out.print("Press enter when you are ready to return to the login menu...");
    				scanner.nextLine();
    				return;
    			} 
    			
    			else {
    				clearConsole();
    				System.out.println("The account credentials you entered were invalid or the username is already in use.");
    				System.out.println("What would you like to do?");
    				System.out.println("  1. Try creating an account again");
    				System.out.println("  2. Return to the login menu");
    				System.out.println("  3. Exit the application");
    				System.out.print("Please enter the number associated with your desired option and then press enter: ");
    				int sureRetryChoice = getUserChoice(3);

    				if (sureRetryChoice == 1) {
    					continue;
    				}
    				else if (sureRetryChoice == 2) {
    					return;
    				}
    				else if (sureRetryChoice == 3) {
    					exitApplication();
    				}
    			}

    		} else {
    			clearConsole();
    			System.out.println("What would you like to do?");
    			System.out.println("  1. Try creating an account again");
    			System.out.println("  2. Return to the login menu");
    			System.out.println("  3. Exit the application");
    			System.out.print("Please enter the number associated with your desired option and then press enter: ");
    			int unsureRetryChoice = getUserChoice(3);

    			if (unsureRetryChoice == 1) {
    				continue;
    			}
    			else if (unsureRetryChoice == 2) {
    				return;
    			}
    			else if (unsureRetryChoice == 3) {
    				exitApplication();
    			}
    		}
    	}
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
    private String enter() {
    	while (true) {
    		clearConsole(); 
    		// Print the login menu
    		System.out.println("Login menu: ");
    		System.out.println("  1. Log In");
    		System.out.println("  2. Create a new account");
    		System.out.println("  3. Exit application");
    		System.out.print("Please enter the number associated with your desired option and then press enter: ");
    		int userChoice = getUserChoice(3);

    		switch (userChoice) {
    		case 1:
    			String loggedInUsername = handleLogIn();
    			 if (loggedInUsername != null) {
    				 return loggedInUsername;
    			 }
                 break;
    		case 2:
    			handleAccountCreation();
    			break;
    		case 3:
    			exitApplication();
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
    private void financesMenu(String currentUser) {
    	while(true) {
    		 clearConsole();
    		 System.out.println("Finances Menu:");
    	     System.out.println("  1. Upload CSV");
    	     System.out.println("  2. Reports");
    	     System.out.println("  3. Predictions");
    	     System.out.println("  4. Data Management");
    	     System.out.println("  5. Return to Main Menu");
    	     System.out.print("Please enter the number associated with your desired option and then press enter: ");
    	     int userChoice = getUserChoice(5);
  	        
    	     switch (userChoice) {
    	     	case 1:
   	        		boolean uploadingFile = true;
   	        		while (uploadingFile) {
   	        			clearConsole();
	   	        		System.out.println("---CSV Loader---");
	   	        		System.out.println("Please enter the name of the CSV file you want to upload below and then press enter.");
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
	   	        			System.out.print("Please enter the number associated with your desired option and then press enter: ");
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
    private boolean reportsMenu(String currentUser) {
    	while(true) {
    		clearConsole();
    		moduleHub.callStorage("listyears", currentUser, 0);
   	    	System.out.print("Please enter the year you would like reports about below and then press enter: ");
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
	    		System.out.print("Please enter the number associated with your desired option and then press enter: ");
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
    private boolean predictionMenu(String currentUser) {
    	while(true) {
    		clearConsole();
    		moduleHub.callStorage("listyears", currentUser, 0);
   	    	System.out.print("Please enter the year you would like predictions about below and then press enter: ");
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
	    		System.out.print("Please enter the number associated with your desired option and then press enter: ");
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
    private boolean dataManagementMenu(String currentUser) {
        while(true) {
        	clearConsole();
            System.out.println("Data Management Menu: ");
            System.out.println("  1. Delete a CSV file");
            System.out.println("  2. Return to Finances Menu");
            System.out.println("  3. Return to Main Menu");
            System.out.print("Please enter the number associated with your desired option and then press enter: ");
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
	                    System.out.print("Please enter the number associated with your desired option and then press enter: ");
	                    userChoice = getUserChoice(2);
	                    
	                    if (userChoice == 1) {
	                    	continue;
	                    }
	                    else {
	                    	clearConsole();
	                    	System.out.println("Would you like to return to the Finances Menu or the Main Menu?");
		                    System.out.println("  1. Return to Finance Menu");
		                    System.out.println("  2. Return to Main Menu");
		                    System.out.print("Please enter the number associated with your desired option and then press enter: ");
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
     * @return Determines whether or not the user has decided to delete their account
     * 		   If the function returns true, then the user has not deleted their account and they can return to the Main Menu
     * 		   If the function returns false, then the user has deleted their account and are forcibly moved to the login page
     * 
     * @author Aaron Madou
     * @author Shazadul Islam
     */
    private boolean accountSettingsMenu(String currentUser) {
    	clearConsole();
        System.out.println(currentUser + " Account Settings:");
        System.out.println("  1. Change Password");
        System.out.println("  2. Delete Account");
        System.out.println("  3. Return to Main Menu");
        System.out.print("Please enter the number associated with your desired option and then press enter: ");
        int settingsChoice = getUserChoice(3);
        
        boolean inSettings = true;
        while (inSettings){
            switch (settingsChoice){
                case 1:
                	clearConsole();
                    System.out.println("Before you can change your password, please enter your current password below and then press enter. ");
                    System.out.print("Password: ");
                    String currentPass = scanner.nextLine();
                    
                case 2:
                    clearConsole();
                    System.out.println("Are you sure you want to delete this account: " + currentUser + "? ");
                    System.out.println("  1. Yes");
                    System.out.println("  2. No");
                    System.out.print("Please enter the number associated with your desired option and then press enter: ");
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
     * @param args - allows the program to receive and process command-line arguments when it is executed in terminal (we have no use for this)
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
                System.out.print("Please enter the number associated with your desired option and then press enter: ");
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
    private void exitApplication() {
        scanner.close();
        clearConsole();
        System.out.println("Thank you for using Hamilton Heights Personal Finance Manager!");
        System.exit(0);
    }
}
