import java.util.Scanner;
import java.io.File;
import java.util.List;

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
		BeautifulDisplay.printContinuePrompt();
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

	enum AccountChangeState{
		USERNAME_DNE,
		INCORRECT_ANSWER,
		SUCCESSFUL_PASSWORD_CHANGE,
		RETURN_TO_MENU
	}

	/**
	 * Handles recovery of the user's account
	 * 
	 * @param usernameRecovering - the account that the user is trying to recover
	 * @return AccountRecoverState - an enum that tells us what happened when the user tried recovering their account
	 * 
	 * @author Shazadul Islam
	 */
	private AccountChangeState accountRecover(String usernameRecovering) {
		//If the account does not exist...
		clearConsole();
		BeautifulDisplay.printGradientHeader("RECOVER PASSWORD", 70);
		System.out.println();
		
		String secureQuestion = moduleHub.getUserSecretQuestion(usernameRecovering);
		if (secureQuestion == null) {
			clearConsole();
			// Return the user to the login menu
			BeautifulDisplay.printGradientHeader("RECOVER PASSWORD", 70);
			System.out.println();

			System.out.println(BeautifulDisplay.RED + "Error: " + BeautifulDisplay.RESET
					+ "No account with the username, " + BeautifulDisplay.BOLD + BeautifulDisplay.CYAN
					+ usernameRecovering + BeautifulDisplay.RESET + ", exists.");
			return AccountChangeState.USERNAME_DNE;
		}

		//If the account exists...
		//Print out the user's security question
		System.out.println("To recover your account, please answer the security question for the \naccount, "
				+ BeautifulDisplay.BOLD + BeautifulDisplay.CYAN + usernameRecovering + BeautifulDisplay.RESET
				+ ", below and then press enter.\n");
		System.out.println(BeautifulDisplay.BOLD + secureQuestion + BeautifulDisplay.RESET + "\n");
		//Get the user's answer to their security question
		System.out.print("Answer: ");
		String secureAnswer = scanner.nextLine();

		//If the user's answer to the their account's security question is incorrect
		if (!moduleHub.verifyUserSecretAnswer(usernameRecovering, secureAnswer)) {
			clearConsole();
			BeautifulDisplay.printGradientHeader("RECOVER PASSWORD", 70);
			System.out.println();

			System.out.println("Your answer to the security question was incorrect.");
			return AccountChangeState.INCORRECT_ANSWER;
		}

		//If the user's answer to their account's security question is correct
		return recoveryChangePassword(usernameRecovering, secureQuestion, secureAnswer  );
	}
	
	private AccountChangeState recoveryChangePassword(String usernameChanging, String verifiedSecQuestion, String verifiedSecAnswer) {
		//If the user's answer to their account's security question is correct
		while (true) {
			clearConsole();
			//Let them enter a new password
			BeautifulDisplay.printGradientHeader("RECOVER PASSWORD", 70);
			System.out.println();

			System.out.println(BeautifulDisplay.BOLD + "\nPassword Rules: " + BeautifulDisplay.RESET);
			System.out.println("   • Cannot be empty or only whitespace");
			System.out.println("   • Cannot begin or end with a space");
			System.out.println("   • Must be at least 5 characters and at most 30 characters");
			BeautifulDisplay.printGradientDivider(70);
			System.out.println();
			System.out.println("Enter a new password for the account, " + BeautifulDisplay.CYAN + usernameChanging
					+ BeautifulDisplay.RESET + ", below and then press enter.");
			System.out.print(BeautifulDisplay.BOLD + "  New Password:" + BeautifulDisplay.RESET + " ");
			String recoverNewPassword = scanner.nextLine();

			if (!(moduleHub.followsPasswordRules(recoverNewPassword))) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("RECOVER PASSWORD", 70);
				System.out.println();

				BeautifulDisplay.printError("Password does not follow the required format.");
				System.out.println("\nWhat would you like to do?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
						+ " Try creating a different password for your account, " + BeautifulDisplay.BOLD
						+ BeautifulDisplay.CYAN + usernameChanging + BeautifulDisplay.RESET + ", again");
				System.out.println(
						BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to the login menu");
				System.out.println(
						BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
				BeautifulDisplay.printGradientDivider(70);
				System.out.println();
				System.out.print("Please enter the number associated with your desired option: ");
				
				int recoverUserChoice = getUserChoice(3);
				if (recoverUserChoice == 1) {
					continue;
				}
				else if (recoverUserChoice == 2) {
					return AccountChangeState.RETURN_TO_MENU;
				}
				else if (recoverUserChoice == 3) {
					exitApplication();
				}
			}

			System.out.println();
			System.out.println("Re-enter your password below to confirm it and then press enter. ");
			System.out.print(BeautifulDisplay.BOLD + "  Confirm your password:" + BeautifulDisplay.RESET + " ");
			String recoverConfirmPassword = scanner.nextLine();

			if (!recoverConfirmPassword.equals(recoverNewPassword)) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("RECOVER PASSWORD", 70);
				System.out.println();

				BeautifulDisplay.printError("Passwords do not match.");
				System.out.println("\nWhat would you like to do?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
						+ " Try creating a password for your account, " + usernameChanging + ",again");
				System.out.println(
						BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to the login menu");
				System.out.println(
						BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
				BeautifulDisplay.printGradientDivider(70);
				System.out.println();
				System.out.print("Please enter the number associated with your desired option and then press enter: ");

				int secretComparePassRetryChoice = getUserChoice(3);
				if (secretComparePassRetryChoice == 1) {
					continue;
				}
				else if (secretComparePassRetryChoice == 2) {
					return AccountChangeState.RETURN_TO_MENU;
				}
				else if (secretComparePassRetryChoice == 3) {
					exitApplication();
				}
			}

			//Attempt to reset
			//returns true if password passes requirements and was saved
			//returns false if password failed requirements and was not saved
			boolean changedPasswordAccepted = moduleHub.resetUserPassword(usernameChanging, verifiedSecAnswer, recoverNewPassword);

			//If their new password is valid...
			if (changedPasswordAccepted) {
				clearConsole();
				System.out.println("The password for the account, " + usernameChanging + ", has been successfully changed.");
				return AccountChangeState.SUCCESSFUL_PASSWORD_CHANGE;
			}

			//If their new password is invalid..(just for safety)
			clearConsole();
			BeautifulDisplay.printGradientHeader("RECOVER PASSWORD", 70);
			System.out.println();

			BeautifulDisplay.printError("Your new password did not meet the requirements.");
			System.out.println("\nWhat would you like to do?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
					+ " Try creating a new password for your account, " + BeautifulDisplay.BOLD + BeautifulDisplay.CYAN
					+ usernameChanging + BeautifulDisplay.RESET + ", again");
			System.out.println(
					BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to the login menu");
			System.out.println(
					BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
			BeautifulDisplay.printGradientDivider(70);
			System.out.println();

			System.out.print("Please enter the number associated with your desired option and then press enter: ");
			
			int recoverUserChoice = getUserChoice(3);

			//Try creating a new password for your account again
			if (recoverUserChoice == 1) {
				continue;
			}
			//Return to login menu
			else if (recoverUserChoice == 2) {
				return AccountChangeState.RETURN_TO_MENU;
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
			BeautifulDisplay.printGradientHeader("Sign In", 70);
			System.out.println();
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "   1. " + BeautifulDisplay.RESET
					+ "Enter your username below and then press enter. ");
			System.out.print(BeautifulDisplay.BOLD + "         Username:" + BeautifulDisplay.RESET + " ");
			String loginUsername = scanner.nextLine();
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "   2. " + BeautifulDisplay.RESET
					+ "Enter your password below and then press enter. ");
			System.out.print(BeautifulDisplay.BOLD + "         Password:" + BeautifulDisplay.RESET + " ");
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
			BeautifulDisplay.printGradientHeader("Sign In", 70);
			System.out.println();
			System.out.println("The username or password you entered was " + BeautifulDisplay.RED + "incorrect."
					+ BeautifulDisplay.RESET);
			System.out.println();

			BeautifulDisplay.printFullWidthSectionHeader("WHAT WOULD YOU LIKE TO DO?", BeautifulDisplay.BRIGHT_GREEN,
					70);
			System.out.println();
			System.out.println(
					BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Try logging in again");
			System.out.println(
					BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Forgot your password?");
			System.out.println(
					BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to the login menu");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  4." + BeautifulDisplay.RESET + " Exit application");

			System.out.println();
			BeautifulDisplay.printGradientDivider(70);
			System.out.print("Please enter the number associated with your desired option: ");
			
			int loginRetryChoice = getUserChoice(4);

			//If the user wants to try logging in again, jump back to the start of the loop, so that they can try logging in again
			switch(loginRetryChoice) {
			case 1:
				continue;
				//If the user forgot their password, go through the account recovery process
			case 2: 
				//Run account recovery process with username of their choice
				clearConsole();
				BeautifulDisplay.printGradientHeader("Sign In", 70);
				System.out.println();
				BeautifulDisplay.printFullWidthSectionHeader("FORGOT PASSWORD", BeautifulDisplay.BRIGHT_GREEN, 70);
				System.out.println();
				System.out.println("Is " + BeautifulDisplay.BOLD + BeautifulDisplay.CYAN + loginUsername
						+ BeautifulDisplay.RESET + " the account you are trying to recover?\n");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Yes");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " No");

				System.out.println();
				BeautifulDisplay.printGradientDivider(70);

				System.out.print("Please enter the number associated with your desired option: ");
				int correctUsername = getUserChoice(2);

				String loginSecretUsername = null;
				if (correctUsername == 1) {
					loginSecretUsername = loginUsername;
				}
				else {
					BeautifulDisplay.printGradientHeader("Sign In", 70);
					System.out.println();
					BeautifulDisplay.printFullWidthSectionHeader("FORGOT PASSWORD", BeautifulDisplay.BRIGHT_GREEN, 70);
					System.out.println();
					System.out.println("Please enter the username of the account you are trying to recover.");
					System.out.print(BeautifulDisplay.BOLD + "     Username:" + BeautifulDisplay.RESET + " ");				
					loginSecretUsername = scanner.nextLine();
				}

				int retries = 0;
				while (true) {
					AccountChangeState forgotPasswordReturn = accountRecover(loginSecretUsername);

					switch (forgotPasswordReturn) {
					case USERNAME_DNE:
						System.out.println("\nWhat would you like to do?\n");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
								+ " Try recovering a different account");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
								+ " Return to the login menu");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET
								+ " Exit the application");
						System.out.println();
						BeautifulDisplay.printGradientDivider(70);
						System.out.print("Please enter the number associated with your desired option: ");
						
						int recoverAgainChoice = getUserChoice(3);

						if (recoverAgainChoice == 1) {
							clearConsole(); 
							
							BeautifulDisplay.printGradientHeader("ACCOUNT RECOVERY", 70);
							System.out.println();
							System.out.println(
									"Please enter the username of the account you are trying to recover and \nthen press enter.");
							System.out.print(BeautifulDisplay.BOLD + "  Username:" + BeautifulDisplay.RESET + " ");
							
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
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
									+ " Try answering your security question again");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
									+ " Return to the login menu");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET
									+ " Exit the application");
							System.out.println();
							BeautifulDisplay.printGradientDivider(70);
							System.out.print("Please enter the number associated with your desired option: ");
						
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
							BeautifulDisplay.printError("Too many account recovery attempts.");
							System.out.println("\nYou can either return to the login menu or exit the application.");
							System.out.println("What would you like to do?\n");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
									+ " Return to the login menu");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
									+ " Exit the application");
							System.out.println();
							BeautifulDisplay.printGradientDivider(70);
							System.out.print("Please enter the number associated with your desired option: ");

							int exitChoice = getUserChoice(2);
							if (exitChoice == 1) {
								return null;
							} 
							else {
								exitApplication(); 
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

	/**
	 * Handles account creation for a user
	 * 
	 * @author Shazadul Islam
	 */
	private void handleAccountCreation() {
		while (true) {
			String registerUsername = null;
			while (true) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
				System.out.println();
				System.out.println(BeautifulDisplay.BOLD + "Username Rules: " + BeautifulDisplay.RESET);
				System.out.println("• Cannot be empty or only whitespace");
				System.out.println("• Cannot begin or end with a space");
				System.out.println("• Must only include letters or numbers");
				System.out.println("• Must be at least 3 characters and at most 20 characters");
				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print(BeautifulDisplay.BOLD + "Enter your username here:" + BeautifulDisplay.RESET + " ");
				
				registerUsername = scanner.nextLine();

				if (moduleHub.followsUsernameRules(registerUsername)) {
					break;
				}
				else {
					clearConsole();
					BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
					BeautifulDisplay.printError("Username does not follow the required format.");
					System.out.println("\nWhat would you like to do?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
							+ " Try creating a different username for your new account");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
							+ " Return to the login menu");
					System.out.println(
							BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
					System.out.println();
					BeautifulDisplay.printGradientDivider(70);
					System.out.print("Please enter the number associated with your desired option: ");
					
					int usernameRetryChoice = getUserChoice(3);
					if (usernameRetryChoice == 1) {
						continue;
					}
					else if (usernameRetryChoice == 2) {
						return;
					}
					else if (usernameRetryChoice == 3) {
						exitApplication();
					}
				}
			}

			String registerPassword = null;
			while(true) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
				System.out.println();
				System.out.println(BeautifulDisplay.BOLD + "Password Rules: " + BeautifulDisplay.RESET);
				System.out.println("   • Cannot be empty or only whitespace");
				System.out.println("   • Cannot begin or end with a space");
				System.out.println("   • Must be at least 5 characters and at most 30 characters");
				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print(BeautifulDisplay.BOLD + "Enter your password here:" + BeautifulDisplay.RESET + " ");
				
				registerPassword = scanner.nextLine();

				if (!(moduleHub.followsPasswordRules(registerPassword))) {
					clearConsole();
					
					BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
					BeautifulDisplay.printError("Password does not follow the required format.");
					System.out.println();
					System.out.println("What would you like to do?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
							+ " Try creating a different password for your new account");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
							+ " Return to the login menu");
					System.out.println(
							BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
					System.out.println();
					BeautifulDisplay.printGradientDivider(70);
					System.out.print("Please enter the number associated with your desired option: ");

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

				System.out.println();
				System.out.println("Re-enter your password below to confirm it and then press enter. ");
				System.out.print(BeautifulDisplay.BOLD + "Confirm your password:" + BeautifulDisplay.RESET + " ");
	
				String confirmPassword = scanner.nextLine();

				if (confirmPassword.equals(registerPassword)) {
					break;
				}
				else {
					clearConsole();
					BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
					BeautifulDisplay.printError("Passwords do not match.");
					System.out.println("\nWhat would you like to do?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
							+ " Try creating a password for your new account again");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
							+ " Return to the login menu");
					System.out.println(
							BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
					System.out.println();
					BeautifulDisplay.printGradientDivider(70);
					System.out.print("Please enter the number associated with your desired option: ");
					
					int comparePassRetryChoice = getUserChoice(3);
					if (comparePassRetryChoice == 1) {
						continue;
					}
					else if (comparePassRetryChoice == 2) {
						return;
					}
					else if (comparePassRetryChoice == 3) {
						exitApplication();
					}
				}
			}

			clearConsole();
			BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
			System.out.println();
			System.out.println("Pick one of the security questions below and then press enter. ");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
					+ " What was your childhood nickname?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
					+ " What is the name of your favorite childhood friend?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET
					+ " What was the name of your first stuffed animal?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  4." + BeautifulDisplay.RESET
					+ " What was the name of the first school you remember attending?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  5." + BeautifulDisplay.RESET
					+ " What was the destination of your most memorable school field trip?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  6." + BeautifulDisplay.RESET
					+ " What was your driving instructor’s first name?");
			System.out.println();
			BeautifulDisplay.printGradientDivider(70);
			System.out.print(
					"Please enter the number associated with the question that you want to \nbe your security question and then press enter: ");
			
			int secretQuestionChoice = getUserChoice(6);

			String registerSecretQuestion = null;
			switch(secretQuestionChoice) {
			case 1:
				registerSecretQuestion = "What was your childhood nickname?";
				break;
			case 2:
				registerSecretQuestion = "What is the name of your favorite childhood friend?";
				break;
			case 3:
				registerSecretQuestion = "What was the name of your first stuffed animal?";
				break;
			case 4:
				registerSecretQuestion = "What was the name of the first school you remember attending?";
				break;
			case 5:
				registerSecretQuestion = "What was the destination of your most memorable school field trip?";
				break;
			case 6:
				registerSecretQuestion = "What was your driving instructor’s first name?";
				break;
			}

			clearConsole();
			BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
			System.out.println();

			System.out.println("The security question you chose was: \n\n" + BeautifulDisplay.BOLD
					+ registerSecretQuestion + BeautifulDisplay.RESET);
			System.out.println("\nWhat would you like your answer to your chosen security question to be? ");
			System.out.print(BeautifulDisplay.BOLD + "   ▶" + BeautifulDisplay.RESET + " ");
			String registerSecretAnswer = scanner.nextLine();

			clearConsole();
			BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
			System.out.println();

			System.out.println("Please confirm that these are the credentials that you want for your account.");
			System.out.println("Username: " + BeautifulDisplay.BOLD + registerUsername + BeautifulDisplay.RESET);
			System.out.println("Password: " + BeautifulDisplay.BOLD + "*".repeat(registerPassword.length())
					+ BeautifulDisplay.RESET);
			System.out.println(
					"Security Question: " + BeautifulDisplay.BOLD + registerSecretQuestion + BeautifulDisplay.RESET);
			System.out.println("Security Question Answer: " + BeautifulDisplay.BOLD
					+ "*".repeat(registerSecretAnswer.length()) + BeautifulDisplay.RESET);
			System.out.println("\nAre you sure these are the credentials that you want for your account?\n");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + " 1." + BeautifulDisplay.RESET + " Yes");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + " 2." + BeautifulDisplay.RESET + " No");

			System.out.println();
			BeautifulDisplay.printGradientDivider(70);
			System.out.print("Please enter the number associated with your desired option: ");

			
			int surety = getUserChoice(2);

			if (surety == 1) {
				// User has not made any errors, so proceed with registration
				boolean isValidAccount = moduleHub.registerUser(registerUsername, registerPassword, registerSecretQuestion, registerSecretAnswer, true);

				if (isValidAccount) {
					clearConsole();
					BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
					System.out.println();

					BeautifulDisplay.printSuccess("Your account has been successfully created!");
					System.out.print("Press enter when you are ready to return to the login menu...");
					scanner.nextLine();
					return;
				} 

				else {
					clearConsole();
					BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
					System.out.println();

					System.out.println(
							"The account credentials you entered were invalid or the username is already in use.");
					System.out.println("What would you like to do?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
							+ " Try creating an account again");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
							+ " Return to the login menu");
					System.out.println(
							BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");

					System.out.println();
					BeautifulDisplay.printGradientDivider(70);
					System.out.print("Please enter the number associated with your desired option: ");

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
				BeautifulDisplay.printGradientHeader("ACCOUNT CREATION", 70);
				System.out.println();

				System.out.println("What would you like to do next?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
						+ " Try creating an account again");
				System.out.println(
						BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to the login menu");
				System.out.println(
						BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");

				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option: ");
				
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
	 * Handles the user's entry into the actual software
	 *
	 * @return loginUsername - the username of the successfully signed-in user
	 * 
	 * @author Shazadul Islam
	 */
	private String enter() {
		while (true) {
			clearConsole();

			// pretty START screen
			BeautifulDisplay.printStartMenu();

			int entryChoice = getUserChoice(3);

			switch (entryChoice) {
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
     * Displays and manages the Finances Menu for the currently signed-in user.
     *
     * This method acts as the main navigation hub for all financial features,
     * including:
     *  Uploading CSV files with full validation handling
     *  Viewing financial reports
     *  Running financial predictions
     *  Managing stored financial data
     *
     * During CSV uploads, this method:
     *  Extracts the year from the CSV filename
     *  Prevents invalid or duplicate-year uploads
     *  Displays validation errors and warnings in a user-friendly way
     *  Allows users to skip bad rows while still importing valid data
     *  Detects duplicate transactions and prompts the user to continue or cancel
     *  Finalizes the CSV import only after user confirmation
     *
	 * @param currentUser - username of the currently signed-in user
	 * 
	 * @author Aaron Madou
	 * @author Shazadul Islam
	 * @author Denisa Cakoni
	 */ 
	private void financesMenu(String currentUser) {
		while(true) {
			clearConsole();

			BeautifulDisplay.printFinancesMenu();

			int userChoice = getUserChoice(5);
			switch (userChoice) {
                case 1:
                    boolean uploadingFile = true;
                    while (uploadingFile) {
                        clearConsole();
                        BeautifulDisplay.printGradientHeader("CSV Loader", 70);
    					System.out.println(
    							"\n   Please enter the name of the CSV file you want to upload below and\n   then press enter.");
    					System.out.println(
    							"        • If the CSV is in the same folder as the JAR, just type the\n        file name (Ex: 2024_data.csv)");
    					System.out.println("        • Otherwise, paste the full file path.\n");
    					BeautifulDisplay.printGradientDivider(70);
    					System.out.print(BeautifulDisplay.BOLD + "\n   File name:" + BeautifulDisplay.RESET + " ");
    					
    					String csvFilePath = scanner.nextLine().trim();

                        File file = new File(csvFilePath);
                        String fileName = file.getName();

                        // fixes StringIndexOutOfBoundsException for short names
                        // fixes StringIndexOutOfBoundsException: Range [0, 4) out of bounds for length
                        // Extract year from filename
                        Integer year = null;
                        if (fileName != null && fileName.length() >= 4) {
                            String firstFour = fileName.substring(0, 4);
                            try {
                                year = Integer.parseInt(firstFour);
                            } catch (NumberFormatException ignored) { }
                        }

                        // If no valid year in range, show a friendly message instead of crashing
                        if (year == null || year < 1900 || year > 2100) {
                            clearConsole();
                            BeautifulDisplay.printGradientHeader("CSV Loader", 70);
    						System.out.println(BeautifulDisplay.BOLD + "\nERROR: " + BeautifulDisplay.RESET
    								+ "Your CSV file must begin with a valid year.");
    						System.out.println(BeautifulDisplay.BOLD + "Example: 2024_data.csv\n" + BeautifulDisplay.RESET);
    						
    						BeautifulDisplay.printFullWidthSectionHeader("WHAT WOULD YOU LIKE TO DO?",
    								BeautifulDisplay.BRIGHT_GREEN, 70);
    						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "\n  1." + BeautifulDisplay.RESET
    								+ " Try uploading a CSV file again");
    						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
    								+ " Return to Finances Menu");

    						System.out.println();
    						BeautifulDisplay.printGradientDivider(70);
    						System.out.print("Please enter the number associated with your desired option: ");
    						
                            int retryFileChoice = getUserChoice(2);

                            if (retryFileChoice == 1) {
                                continue;   // ask for another filename
                            }
                            uploadingFile = false;
                            break;          // back to Finances menu
                        }

                        // If that user already has data for that year, ask what to do
                        if (moduleHub.hasDataForYear(currentUser, year)) {
                            clearConsole();
                            BeautifulDisplay.printGradientHeader("CSV Loader", 70);
    						System.out.println(BeautifulDisplay.BOLD + "Data already exists for " + year + ".\n" + BeautifulDisplay.RESET);
    						
    						System.out.println("What would you like to do?");
    						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
    								+ " Overwrite existing data");
    						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
    								+ " Choose a different CSV");
    						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET
    								+ " Return to Finances Menu");

    						System.out.println();
    						BeautifulDisplay.printGradientDivider(70);
    						System.out.print("Please enter the number associated with your desired option: ");
                            
                            int overwriteChoice = getUserChoice(3);

                            if (overwriteChoice == 2) {
                                continue; // choose a different file
                            }
                            if (overwriteChoice == 3) {
                                uploadingFile = false;
                                break;
                            }
                            // overwrite existing data and continue
                        }

                        //  Run upload + validation (including duplicate detection in ModuleHub)
                        ValidationResult result = moduleHub.uploadCSVData(currentUser, csvFilePath, year);

                        clearConsole();

                        //  HARD ERRORS do not proceed to import
                        if (result.hasErrors()) {
                            boolean viewingErrors = true;

                            while (viewingErrors) {
                                clearConsole();
                                BeautifulDisplay.printGradientHeader("CSV Loader", 70);
    							BeautifulDisplay.printError("CSV Upload Failed:");

    							System.out.println();
    							System.out.println("We found " + result.getErrorMessages().size() + BeautifulDisplay.RED
    									+ " error(s) " + BeautifulDisplay.RESET + "in your CSV.");
    							if (!result.getWarningMessages().isEmpty()) {
    								System.out.println(
    										"There were also " + result.getWarningMessages().size() + " warning(s).");
    							}
    							System.out.println("What would you like to do?");
    							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
    									+ " Try a different CSV");
    							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
    									+ " View error details");
    							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET
    									+ " Return to Finances Menu");
    							System.out.println();
    							BeautifulDisplay.printGradientDivider(70);
    							System.out.print("Please enter the number associated with your desired option: ");

                                int afterErrorChoice = getUserChoice(3);

                                if (afterErrorChoice == 1) {
                                    viewingErrors = false;
                                    // go back to ask for another file
                                }
                                else if (afterErrorChoice == 2) {
                                    clearConsole();
                                    BeautifulDisplay.printGradientHeader("CSV Loader", 70);
    								BeautifulDisplay.printError("CSV Error Details:");
                                    
                                    int shown = 0;
                                    for (String msg : result.getErrorMessages()) {
                                        System.out.println(" • " + msg);
                                        shown++;
                                        if (shown >= 20) {
                                            System.out.println();
                                            System.out.println("  (Showing first 20 errors only...)");
                                            break;
                                        }
                                    }
                                    System.out.println();
                                    moveOn(); // Press enter to return to summary
                                }
                                else {
                                    uploadingFile = false;
                                    viewingErrors = false;
                                }
                            }

                            // If user chose "Try a different CSV", loop outer while again
                            continue;
                        }

                        //  NO HARD ERRORS handle warnings, especially duplicates
                        //  NO HARD ERRORS handle warnings, especially duplicates
                        boolean hasDuplicateWarning = false;

                        List<String> warnings = result.getWarningMessages();

                        if (warnings != null && !warnings.isEmpty()) {
                            // Short summary instead of 5 miles of text
                            BeautifulDisplay.printWarning("Upload validation completed with "
                                    + warnings.size() + " warning(s).");
                            System.out.println("Some bad rows were automatically skipped, but your valid data was saved.");
                            System.out.println();

                            // Quietly check if duplicates are mentioned anywhere
                            for (String raw : warnings) {
                                if (raw != null && raw.toLowerCase().contains("duplicate transactions detected")) {
                                    hasDuplicateWarning = true;
                                    break;
                                }
                            }

                            // Let the user opt-in to details
                            System.out.print("Type 'details' to see a few example issues, or press Enter to continue: ");
                            String choice = scanner.nextLine().trim();

                            if (choice.equalsIgnoreCase("details")) {
                                System.out.println();
                                int limit = Math.min(warnings.size(), 5);
                                for (int i = 0; i < limit; i++) {
                                    String cleaned = simplifyValidationMessage(warnings.get(i));
                                    System.out.println("  • " + cleaned);
                                }
                                if (warnings.size() > limit) {
                                    System.out.println("  (+ " + (warnings.size() - limit) + " more warning(s) not shown)");
                                }
                                System.out.println();
                                moveOn();  // your usual "press Enter to continue"
                            }
                        }
                        //  If duplicates exist, give the user the required choice:
                        // "Continue import with duplicates" vs "Cancel and clean CSV."
                        if (hasDuplicateWarning) {
                        	System.out.println(BeautifulDisplay.BOLD + "Duplicate transactions were found in this CSV." + BeautifulDisplay.RESET);
    						System.out.println("What would you like to do?");
    						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Continue import with duplicates");
    						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Cancel and clean CSV");
    						
    						System.out.println();
    						BeautifulDisplay.printGradientDivider(70);
    						System.out.print("Please enter the number associated with your desired option: ");
    						
                        	int dupChoice = getUserChoice(2);

                            if (dupChoice == 2) {
                                clearConsole();
                                System.out.println("Import cancelled. Please clean your CSV file to remove duplicates and try again.");
                                moveOn();
                                uploadingFile = false;
                                break;  // back to Finances menu WITHOUT importing
                            }
                        }

                        // If we reach here:
                        // there were no duplicates, or user chose to continue import with duplicates
                        boolean importOk = moduleHub.finalizeCsvUpload(currentUser, year, csvFilePath);

                        clearConsole();
                        if (importOk) {
                            if (!result.getWarningMessages().isEmpty()) {
                                BeautifulDisplay.printWarning("CSV data for " + year + " imported with "
                                        + result.getWarningMessages().size() + " warning(s).");
                                System.out.println("See validation details above for specific rows that were skipped.");
                            } else {
                                BeautifulDisplay.printSuccess("CSV data for " + year + " imported successfully.");
                            }
                        } else {
                            BeautifulDisplay.printError("Failed to import CSV into your saved data. Please try again.");
                        }

                        System.out.println();
                        moveOn();
                        uploadingFile = false; // After one successful (or attempted) import, go back to Finances menu
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
     * Cleans up raw validation messages by removing internal system prefixes
     * such as timestamps and severity labels.
     *
     * This method ensures that the user only sees the meaningful, human-readable
     * portion of the message (for example: "Line 3: Invalid date format").
     *
     * It safely handles null values and trims unnecessary formatting so
     * validation output remains clean and consistent in the UI.
     *
     * @param raw the original validation message produced by the validation system
     * @return a simplified, user-friendly version of the validation message
     *
     * @author Denisa Cakoni
     */

    private String simplifyValidationMessage(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();

        // Many messages look like: [time][WARNING] [time] Line X: ...
        // Peel off up to two leading "[...]" blocks.
        for (int i = 0; i < 2; i++) {
            if (s.startsWith("[")) {
                int idx = s.indexOf("] ");
                if (idx > 0 && idx + 2 < s.length()) {
                    s = s.substring(idx + 2).trim();
                }
            }
        }

        return s;
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
		boolean plzEnterYearInRangeRep = false;
		boolean plzEnterValidYearRep = false;
		while(true) {
			clearConsole();
			BeautifulDisplay.printGradientHeader("REPORTS MENU", 70);
			System.out.println();
			moduleHub.callStorage("listyears", currentUser, 0);
			System.out.println();
			System.out.println("Please enter the year you would like reports about below and then press enter. ");
			if (plzEnterYearInRangeRep) {
				System.out.println("Please make sure that the year you enter is between 1900 and 2100.");
				plzEnterYearInRangeRep = false;
			} else if (plzEnterValidYearRep) {
				System.out.println("Please make sure that the year you enter is digits only(e.g., 1999 or 2024).");
				plzEnterValidYearRep = false;
			}
			System.out.println(
					"If you have changed your mind about viewing the reports of a year, just \npress enter without any input.");
			System.out.print(BeautifulDisplay.BOLD + "\nYear that you would like reports about:" + BeautifulDisplay.RESET + " ");

			String reportsInput = scanner.nextLine();

			if (reportsInput.trim().isEmpty()) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("REPORTS MENU", 70);
				System.out.println();
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " View the reports of a different year");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Finance Menu");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to Main Menu");
				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option: ");
				int exitChoice = getUserChoice(3);

				if (exitChoice == 1) {
					continue;
				} else if (exitChoice == 2) {
					return false;
				} else {
					return true;
				}
			}
			
			int year;
			try {
				year = Integer.parseInt(reportsInput);
				if ((year < 1900) || (year > 2100)) {
					clearConsole();
					if (year < 1900) {
						System.out.println(
								"The year you entered is too far into the past. The earliest allowed year is 1900.");
					} else if (year > 2100) {
						System.out.println(
								"The year you entered is too far into the future. The latest allowed year is 2100.");
					}
					System.out.println("What would you like to do?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " View the reports of a different year");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Finance Menu");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to Main Menu");
					System.out.println();
					BeautifulDisplay.printGradientDivider(70);
					System.out.print("Please enter the number associated with your desired option: ");
					
					int rangeYearChoice = getUserChoice(3);

					if (rangeYearChoice == 1) {
						plzEnterYearInRangeRep = true;
						continue;
					} else if (rangeYearChoice == 2) {
						return false;
					} else {
						return true;
					}
				}

			} catch (NumberFormatException e) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("REPORTS MENU", 70);
				BeautifulDisplay.printError("Your input, " + reportsInput + " , is not a valid year.");
				System.out.println("\nWhat would you like to do?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " View the reports of a different year");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Finance Menu");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to Main Menu");
				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option: ");
				
				int invalidYearChoice = getUserChoice(3);

				if (invalidYearChoice == 1) {
					plzEnterValidYearRep = true;
					continue;
				} else if (invalidYearChoice == 2) {
					return false;
				} else {
					return true;
				}
			}

			if (!moduleHub.hasDataForYear(currentUser, year)) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("REPORTS MENU", 70);
				BeautifulDisplay.printError("There is no data for " + year + ".");
				System.out.println("\nWhat would you like to do?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " View the reports of a different year");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Finance Menu");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to Main Menu");
				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option: ");

				int userChoice = getUserChoice(3);

				if (userChoice == 1) {
					continue;
				}
				else if (userChoice == 2) {
					return false;
				}
				else {
					return true;
				}
			}

			boolean isWorkingWithYear = true;
			while (isWorkingWithYear) {
				clearConsole();
				BeautifulDisplay.printReportsMenu(year);

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
		boolean plzEnterYearInRangePred = false;
		boolean plzEnterValidYearPred = false;
		while(true) {
			clearConsole();
			BeautifulDisplay.printGradientHeader("PREDICTIONS MENU", 70);
			System.out.println();
			moduleHub.callStorage("listyears", currentUser, 0);
			System.out.println();
			System.out.println("Please enter the year you would like predictions about below and then press enter.");
			if (plzEnterYearInRangePred) {
				System.out.println("Please make sure that the year you enter is between 1900 and 2100.");
				plzEnterYearInRangePred = false;
			} else if (plzEnterValidYearPred) {
				System.out.println("Please make sure that the year you enter is digits only(e.g., 1999 or 2024).");
				plzEnterValidYearPred = false;
			}
			System.out.println(
					"If you have changed your mind about viewing the predictions of a year, \njust press enter without any input.");
			System.out.print(BeautifulDisplay.BOLD + "\nYear that you would like predictions about:" + BeautifulDisplay.RESET + " ");
			String predictionInput = scanner.nextLine();

			if (predictionInput.trim().isEmpty()) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("PREDICTIONS MENU", 70);
				System.out.println();
				System.out.println("What would you like to do?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." +  BeautifulDisplay.RESET + " View the predictions of a different year");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." +  BeautifulDisplay.RESET + " Return to Finance Menu");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." +  BeautifulDisplay.RESET + " Return to Main Menu");
				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option and then press enter: ");
				int exitChoice = getUserChoice(3);

				if (exitChoice == 1) {
					continue;
				} else if (exitChoice == 2) {
					return false;
				} else {
					return true;
				}
			}

			int year;
			try {
				year = Integer.parseInt(predictionInput);
				if ((year < 1900) || (year > 2100)) {
					clearConsole();
					if (year < 1900) {
						System.out.println(
								"The year you entered is too far into the past. The earliest allowed year is 1900.");
					} else if (year > 2100) {
						System.out.println(
								"The year you entered is too far into the future. The latest allowed year is 2100.");
					}
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." +  BeautifulDisplay.RESET + " View the predictions of a different year");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." +  BeautifulDisplay.RESET + " Return to Finance Menu");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." +  BeautifulDisplay.RESET + " Return to Main Menu");
					System.out.println();
					BeautifulDisplay.printGradientDivider(70);
					System.out.print(
							"Please enter the number associated with your desired option and then press enter: ");
					int rangeYearChoice = getUserChoice(3);

					if (rangeYearChoice == 1) {
						plzEnterYearInRangePred = true;
						continue;
					} else if (rangeYearChoice == 2) {
						return false;
					} else {
						return true;
					}
				}

			} catch (NumberFormatException e) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("PREDICTIONS MENU", 70);
				BeautifulDisplay.printError("Your input, " + predictionInput + " , is not a valid year.");
				System.out.println("\nWhat would you like to do?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." +  BeautifulDisplay.RESET + " View the predictions of a different year");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." +  BeautifulDisplay.RESET + " Return to Finance Menu");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." +  BeautifulDisplay.RESET + " Return to Main Menu");
				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option and then press enter: ");
				int invalidYearChoice = getUserChoice(3);

				if (invalidYearChoice == 1) {
					plzEnterValidYearPred = true;
					continue;
				} else if (invalidYearChoice == 2) {
					return false;
				} else {
					return true;
				}
			}

			if (!moduleHub.hasDataForYear(currentUser, year)) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("PREDICTIONS MENU", 70);
				BeautifulDisplay.printError("There is no data for " + year);
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." +  BeautifulDisplay.RESET + " View the predictions of a different year");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." +  BeautifulDisplay.RESET + " Return to Finance Menu");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." +  BeautifulDisplay.RESET + " Return to Main Menu");
				System.out.println();
				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option and then press enter: ");
				int userChoice = getUserChoice(3);

				if (userChoice == 1) {
					continue;
				}
				else if (userChoice == 2) {
					return false;
				}
				else {
					return true;
				}
			}

			boolean isWorkingWithYear = true;
			while (isWorkingWithYear) {
				clearConsole();
				// Pretty predictions screen
				BeautifulDisplay.printPredictionsMenu(year);

				int workingYearChoice = getUserChoice(8);

				switch (workingYearChoice) {
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
				
				case 4: {clearConsole();
                        List<String> categories=moduleHub.getDeficitAdjustableCategories(currentUser,year);
                        if (categories==null || categories.isEmpty()) {
                            System.out.println("No adjustable expense found for this year ");
                            System.out.println("Your deficit might be caused by fixed costs or insufficient income.");
                            moveOn();
                            break;
                        }
                        System.out.println("Choose an expense category to see how reducing it affects your deficit:\n");
                        for (int i=0; i<categories.size(); i++) {
                            System.out.println(" " + (i+1) + ". " + categories.get(i));
                        }
                        System.out.println();
                        System.out.println("Please enter the number associated with your desired option and then press enter:");
                        int catChoice = getUserChoice(categories.size());
                        String chosenCategory = categories.get(catChoice-1);
                        clearConsole();
                        String explanation= moduleHub.buildDeficitWhatifSummary(currentUser, year, chosenCategory);
                        System.out.println(explanation);
                        moveOn();
                        break;
                }
				case 5: {
                    clearConsole();
                    List<String> categories = moduleHub.getDeficitAdjustableCategories(currentUser, year);
                    if (categories == null || categories.isEmpty()) {
                        System.out.println("No expense categories were found to increase for this year ");
                        System.out.println("Upload CSV or check your data before running a surplus What-if.");
                        moveOn();
                        break;
                    }
                    System.out.println("Choose a category to see the maximum you can safely spend there without going into deficit:\n");
                    for (int i = 0; i < categories.size(); i++) {
                        System.out.println(" " + (i + 1) + ". " + categories.get(i));
                    }
                    System.out.println();
                    System.out.println("Please enter the number associated with your desired option and then press enter:");
                    int catChoice = getUserChoice(categories.size());
                    String chosenCategory = categories.get(catChoice - 1);
                    clearConsole();
                    String explanation = moduleHub.buildSurplusWhatifSummary(currentUser, year, chosenCategory);
                    System.out.println(explanation);
                    moveOn();
                    break;
                }

				case 6:
					isWorkingWithYear = false;
                    break;
                    case 7:
                        return false;
                    case 8:
                        return true;
				}
			}
		}
	}

	/**
	 * Data Management Menu
	 * 
	 * @return Determines if user wants to go to the back to the Finances Menu or the Main Menu
	 * @param currentUser - username of the currently signed-in user
	 * 
	 * @author Shazadul Islam
	 */
	private boolean dataManagementMenu(String currentUser) {
		while(true) {
			clearConsole();
			BeautifulDisplay.printDataManagementMenu();
			int dataManageChoice = getUserChoice(3);

			switch (dataManageChoice) {
			case 1:
				boolean plzEnterYearInRangeDel = false;
				boolean plzEnterValidYearDel = false;
				while (true) {
					clearConsole();
					BeautifulDisplay.printGradientHeader("DATA MANGAEMENT", 70);
					System.out.println();
					moduleHub.callStorage("listyears", currentUser, 0);
					System.out.println();
					System.out.println("Please enter the year of the CSV file you would like to delete below and then press enter. \n");
					if (plzEnterYearInRangeDel) {
						System.out.println("Please make sure that the year you enter is between 1900 and 2100.");
						plzEnterYearInRangeDel = false;
					}
					else if (plzEnterValidYearDel) {
						System.out.println("Please make sure that the year you enter is digits only(e.g., 1999 or 2024).");
						plzEnterValidYearDel = false;
					}
					System.out.println("If you have decided not to delete a CSV file anymore, just press enter without any input.");
					System.out.print("Year of the CSV file you would like to delete or exit file deletion: ");
					String csvDelInput = scanner.nextLine();
					
					if (csvDelInput.trim().isEmpty()) {
						clearConsole();
						BeautifulDisplay.printGradientHeader("DATA MANGAEMENT", 70);
						System.out.println();
						System.out.println("What would you like to do next?");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Delete the data of a different year");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Finance Menu");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to Main Menu");
						System.out.println();
						BeautifulDisplay.printGradientDivider(70);
						System.out.print("Please enter the number associated with your desired option and then press enter: ");
						int noDataChoice = getUserChoice(3);

						if (noDataChoice == 1) {
							continue;
						}
						else if (noDataChoice == 2) {
							return false;
						}
						else {
							return true;
						}
					}
					
					int year;
					try {
						year = Integer.parseInt(csvDelInput);
						if ((year < 1900) || (year > 2100)) {
							clearConsole();
							if (year < 1900) {
							    System.out.println("The year you entered is too far into the past. The earliest allowed year is 1900.");
							} 
							else if (year > 2100) {
							    System.out.println("The year you entered is too far into the future. The latest allowed year is 2100.");
							}
							System.out.println("What would you like to do next?");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Delete the data of a different year");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Finance Menu");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to Main Menu");
							System.out.println();
							BeautifulDisplay.printGradientDivider(70);
							System.out.print("Please enter the number associated with your desired option and then press enter: ");
							
							int rangeYearChoice = getUserChoice(3);

							if (rangeYearChoice == 1) {
								plzEnterYearInRangeDel = true;
								continue;
							}
							else if (rangeYearChoice == 2) {
								return false;
							}
							else {
								return true;
							}
						}
						
					} catch (NumberFormatException e) {
						clearConsole();
						BeautifulDisplay.printGradientHeader("DATA MANGAEMENT", 70);
						BeautifulDisplay.printError("Your input, "+ csvDelInput + " , is not a valid year.");
						System.out.println("\nWhat would you like to do next?");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Delete the data of a different year");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Finance Menu");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to Main Menu");
						System.out.println();
						BeautifulDisplay.printGradientDivider(70);
						System.out.print("Please enter the number associated with your desired option and then press enter: ");
						int invalidYearChoice = getUserChoice(3);

						if (invalidYearChoice == 1) {
							plzEnterValidYearDel = true;
							continue;
						}
						else if (invalidYearChoice == 2) {
							return false;
						}
						else {
							return true;
						}
					}
					
					if (!moduleHub.hasDataForYear(currentUser, year)) {
						clearConsole();
						BeautifulDisplay.printGradientHeader("DATA MANGAEMENT", 70);
						BeautifulDisplay.printError("There is no data for " + year);
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Delete the data of a different year");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Finance Menu");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to Main Menu");
						System.out.println();
						BeautifulDisplay.printGradientDivider(70);
						System.out.print("Please enter the number associated with your desired option and then press enter: ");
						int noDataChoice = getUserChoice(3);

						if (noDataChoice == 1) {
							continue;
						}
						else if (noDataChoice == 2) {
							return false;
						}
						else {
							return true;
						}
					}

					moduleHub.callStorage("delete", currentUser, year);

					clearConsole();
					BeautifulDisplay.printGradientHeader("DATA MANGAEMENT", 70);
					System.out.println("\nWould you like to delete another CSV file?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Yes");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " No");
					System.out.println();
					BeautifulDisplay.printGradientDivider(70);
					System.out.print("Please enter the number associated with your desired option and then press enter: ");
					int deleteAnotherChoice = getUserChoice(2);

					if (deleteAnotherChoice == 1) {
						continue;
					}
					else {
						clearConsole();
						BeautifulDisplay.printGradientHeader("DATA MANGAEMENT", 70);
						System.out.println("\nWould you like to return to the Finance Menu or the Main Menu?");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Return to Finance Menu");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to Main Menu");
						System.out.println();
						BeautifulDisplay.printGradientDivider(70);
						System.out.print("Please enter the number associated with your desired option and then press enter: ");
						int returnChoice = getUserChoice(2);

						if (returnChoice == 1) {
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
	 * Handles the creation of a new password when trying to change your password
	 * 
	 * @param usernameChanging - the username of the account you want to change the password of
	 * @param oldCurrentPassword - the old password of the account you want to change the password of
	 * @return - return the result of what happened when trying to change your password
	 */
	private AccountChangeState decideChangePassword(String usernameChanging, String oldCurrentPassword) {
		//If the user's answer to their account's security question is correct
		while (true) {
			clearConsole();
			//Let them enter a new password
			BeautifulDisplay.printGradientHeader("CHANGE PASSWORD", 70);
			System.out.println();
			System.out.println(BeautifulDisplay.BOLD + "Password Rules: " + BeautifulDisplay.RESET);
			System.out.println("   • Cannot be empty or only whitespace");
			System.out.println("   • Cannot begin or end with a space");
			System.out.println("   • Must be at least 5 characters and at most 30 characters");
			System.out.println();
			BeautifulDisplay.printGradientDivider(70);

			System.out.println("Enter a new password for the account, " + BeautifulDisplay.BOLD + BeautifulDisplay.CYAN
					+ usernameChanging + BeautifulDisplay.RESET + ", below and then press enter.");
			System.out.print(BeautifulDisplay.BOLD + "  New Password:" + BeautifulDisplay.RESET + " ");
			
			String decideNewPassword = scanner.nextLine();

			if (!(moduleHub.followsPasswordRules(decideNewPassword))) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("CHANGE PASSWORD", 70);
				System.out.println();
				BeautifulDisplay.printError("Password does not follow the required format.");
				System.out.println("What would you like to do?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
						+ " Try creating a different password for your account, " + BeautifulDisplay.BOLD
						+ BeautifulDisplay.CYAN + usernameChanging + BeautifulDisplay.RESET + ", again");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
						+ " Return to the account settings menu");
				System.out.println(
						BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");

				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option and then press enter: ");
				
				int decideUserChoice = getUserChoice(3);
				if (decideUserChoice == 1) {
					continue;
				}
				else if (decideUserChoice == 2) {
					return AccountChangeState.RETURN_TO_MENU;
				}
				else if (decideUserChoice == 3) {
					exitApplication();
				}
			}

			System.out.println();
			System.out.println("Re-enter your password below to confirm it and then press enter. ");
			System.out.print(BeautifulDisplay.BOLD + "  Confirm your password:" + BeautifulDisplay.RESET + " ");
			
			String recoverConfirmPassword = scanner.nextLine();

			if (!recoverConfirmPassword.equals(decideNewPassword)) {
				clearConsole();
				BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);
				System.out.println("\nPasswords do not match.");
				System.out.println("What would you like to do?");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
						+ " Try creating a password for your account, " + usernameChanging + ",again");
				System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
						+ " Return to the account settings menu");
				System.out.println(
						BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
				BeautifulDisplay.printGradientDivider(70);
				System.out.print("Please enter the number associated with your desired option and then press enter: ");

				int decideComparePassRetryChoice = getUserChoice(3);
				if (decideComparePassRetryChoice == 1) {
					continue;
				}
				else if (decideComparePassRetryChoice == 2) {
					return AccountChangeState.RETURN_TO_MENU;
				}
				else if (decideComparePassRetryChoice == 3) {
					exitApplication();
				}
			}

			//Attempt to changed password with old password
			//returns true if password passes requirements and was saved
			//returns false if password failed requirements and was not saved

			boolean changedPasswordAccepted = moduleHub.changePassword(usernameChanging, oldCurrentPassword, decideNewPassword);

			//If their new password is valid...
			if (changedPasswordAccepted) {
				clearConsole();
				System.out.println("The password for the account, " + usernameChanging + ", has been successfully changed.");
				return AccountChangeState.SUCCESSFUL_PASSWORD_CHANGE;
			}

			//If their new password is invalid..(just for safety)
			clearConsole();
			BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);
			System.out.println("\nYour new password did not meet the requirements.");
			System.out.println("What would you like to do?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
					+ " Try creating a new password for your account, " + usernameChanging + ", again");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
					+ " Return to the account settings menu");
			System.out.println(
					BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
			System.out.print("Please enter the number associated with your desired option and then press enter: ");
			
			int decideUserChoice = getUserChoice(3);

			//Try creating a new password for your account again
			if (decideUserChoice == 1) {
				continue;
			}
			//Return to login menu
			else if (decideUserChoice == 2) {
				return AccountChangeState.RETURN_TO_MENU;
			}
			//Exit the application
			else if (decideUserChoice == 3) {
				exitApplication();
			}
		}
	}

	/**
	 * Handles the changing of the user's password
	 * 
	 * @param currentUsername - User's username
	 * 
	 * @author Shazadul Islam
	 */
	private void handleChangePassword(String currentUsername) {
		while (true) {
			clearConsole();
			
			BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);
			System.out.println();
			
			System.out.println("Please enter your current password below and then press enter.");
			System.out.print("  Current Password: ");
			String currentPassword = scanner.nextLine();

			//Check if the password we just received is valid
			boolean validPass = moduleHub.verifyPassword(currentUsername, currentPassword);

			//If the current password is valid, then change Password
			if (validPass) {
				AccountChangeState currentPassChange = decideChangePassword(currentUsername, currentPassword);
				switch (currentPassChange) {
				case SUCCESSFUL_PASSWORD_CHANGE:
					System.out.print("Press enter when you are ready to return to the account settings menu...");
					scanner.nextLine();
					return; //returns to login menu.
				case RETURN_TO_MENU:
					return;
				default:
					return;
				}
			}

			//If the password is invalid, then print invalid login menu
			clearConsole();
			//Print invalid login menu
			BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);
			System.out.println();
			// Print invalid login menu
			System.out.println(BeautifulDisplay.RED + "ERROR: " + BeautifulDisplay.RESET
					+ "The password you entered was " + BeautifulDisplay.RED + "incorrect." + BeautifulDisplay.RESET);
			System.out.println("What you like to do? ");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
					+ " Try verifying your password again");
			System.out.println(
					BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Forgot your password?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET
					+ " Return to the account settings menu");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  4." + BeautifulDisplay.RESET + " Exit application");
			BeautifulDisplay.printGradientDivider(70);
			System.out.print("Please enter the number associated with your desired option and then press enter: ");
			int loginRetryChoice = getUserChoice(4);

			//If the user wants to try logging in again, jump back to the start of the loop, so that they can try logging in again
			switch(loginRetryChoice) {
			case 1:
				continue;
				//If the user forgot their password, go through the account recovery process
			case 2: 
				//Run account recovery process with the username their logged in as
				String loginSecretUsername = currentUsername;

				int retries = 0;
				while (true) {
					AccountChangeState forgotPasswordReturn = accountRecover(loginSecretUsername);

					switch (forgotPasswordReturn) {
					case INCORRECT_ANSWER:
						if (retries < 3) {
							System.out.println("What would you like to do?");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
									+ " Try answering your security question again");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
									+ " Return to the account settings menu");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET
									+ " Exit the application");
							System.out.print(
									"Please enter the number associated with your desired option and then press enter: ");
							int incorrectChoice = getUserChoice(3);

							if (incorrectChoice == 1) {
								retries++;
								continue;
							} 
							else if (incorrectChoice == 2) {
								return;  
							} 
							else {
								exitApplication();
							}
						}

						else {
							System.out.println();
							System.out.println("Too many account recovery attempts.");
							System.out.println(
									"You can either return to the account settings menu or exit the application.");
							System.out.println("What would you like to do?");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
									+ " Return to the account settings menu");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
									+ " Exit the application");
							System.out.print(
									"Please enter the number associated with your desired option and then press enter: ");

							int exitChoice = getUserChoice(2);
							if (exitChoice == 1) {
								return;
							} 
							else {
								exitApplication(); 
							} 
						}

						break;

					case SUCCESSFUL_PASSWORD_CHANGE:
						System.out.print("Press enter when you are ready to return to the account settings menu...");
						scanner.nextLine();
						return;

					case RETURN_TO_MENU:
						return;

					default:
						return;
					}
				}
			case 3: 
				return;
			case 4: 
				exitApplication();
			} 
		}
	}

	/**
	 * Handles the changing of the user's security question and security question answer
	 * 
	 * @param currentUsername - User's username
	 * 
	 * @author Shazadul Islam
	 */
	private void handleResetSecurity(String currentUsername) {
		while (true) {
			clearConsole();
			BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);
			System.out.println("\nPlease enter your current password below and then press enter.");
			System.out.print("  Current Password: ");
			
			String currentPassword = scanner.nextLine();

			//Check if the password we just received is valid
			boolean validPass = moduleHub.verifyPassword(currentUsername, currentPassword);

			//If the current password is valid, then change Password
			if (validPass) {
				while(true) {
					clearConsole();
					BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);
					System.out.println();

					System.out.println("Pick one of the security questions below and then press enter. ");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET
							+ " What was your childhood nickname?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET
							+ " What is the name of your favorite childhood friend?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET
							+ " What was the name of your first stuffed animal?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  4." + BeautifulDisplay.RESET
							+ " What was the name of the first school you remember attending?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  5." + BeautifulDisplay.RESET
							+ " What was the destination of your most memorable school field trip?");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  6." + BeautifulDisplay.RESET
							+ " What was your driving instructor’s first name?");
					System.out.println();
					System.out.print(
							"Please enter the number associated with the question that you want to \nbe your security question and then press enter: ");
					
					int newSecretQuestionChoice = getUserChoice(6);

					String newSecretQuestion = null;
					switch(newSecretQuestionChoice) {
					case 1:
						newSecretQuestion = "What was your childhood nickname?";
						break;
					case 2:
						newSecretQuestion = "What is the name of your favorite childhood friend?";
						break;
					case 3:
						newSecretQuestion = "What was the name of your first stuffed animal?";
						break;
					case 4:
						newSecretQuestion = "What was the name of the first school you remember attending?";
						break;
					case 5:
						newSecretQuestion = "What was the destination of your most memorable school field trip?";
						break;
					case 6:
						newSecretQuestion = "What was your driving instructor’s first name?";
						break;
					}

					clearConsole();
					BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);
					System.out.println();

					System.out.println("The security question you chose was: \n\n" + BeautifulDisplay.BOLD
							+ newSecretQuestion + BeautifulDisplay.RESET);
					System.out.println("\nWhat would you like your answer to your chosen security question to be? ");
					System.out.print(BeautifulDisplay.BOLD + "\n   ▶" + BeautifulDisplay.RESET + " ");
					
					String newSecretAnswer = scanner.nextLine();
					boolean updatedSecurity = moduleHub.updateUserSecretQuestionAndAnswer(currentUsername, newSecretQuestion, newSecretAnswer);

					if (updatedSecurity) {
						clearConsole();BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);
						System.out.println();

						BeautifulDisplay.printSuccess(
								"Your security question and security question answer have been\n changed successfully!");
						System.out.print("Press enter when you are ready to return to the account settings menu...");
						scanner.nextLine();
						return;
					}
					else {
						System.out.println("Failed to update you security question and security question answer.");
						System.out.println("What would you like to do?");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Try changing your security question again");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to the account settings menu");
						System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
						System.out.println();
						BeautifulDisplay.printGradientDivider(70);
						System.out.print("Please enter the number associated with your desired option and then press enter: ");
						int incorrectChoice = getUserChoice(3);

						if (incorrectChoice == 1) {
							continue;
						} 
						else if (incorrectChoice == 2) {
							return;  
						} 
						else {
							exitApplication();
						}
					}
				}
			}

			//If the login is invalid, then print invalid login menu
			clearConsole();
			//Print invalid login menu
			BeautifulDisplay.printGradientHeader("ACCOUNT SETTINGS", 70);		
			BeautifulDisplay.printError("The password you entered was incorrect.");
			System.out.println("\nWhat you like to do? ");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Try verifying your password again");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Forgot your password?");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Return to the account settings menu");
			System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  4." + BeautifulDisplay.RESET + " Exit application");
			System.out.println();
			BeautifulDisplay.printGradientDivider(70);
			System.out.print("Please enter the number associated with your desired option and then press enter: ");
			
			int loginRetryChoice = getUserChoice(4);

			//If the user wants to try logging in again, jump back to the start of the loop, so that they can try logging in again
			switch(loginRetryChoice) {
			case 1:
				continue;
				//If the user forgot their password, go through the account recovery process
			case 2: 
				//Run account recovery process with the username their logged in as
				clearConsole();

				String loginSecretUsername = currentUsername;

				int retries = 0;
				while (true) {
					AccountChangeState forgotPasswordReturn = accountRecover(loginSecretUsername);

					switch (forgotPasswordReturn) {
					case INCORRECT_ANSWER:
						if (retries < 3) {
							System.out.println("What would you like to do?");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Try answering your security question again");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Return to the account settings menu");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  3." + BeautifulDisplay.RESET + " Exit the application");
							System.out.println();
							BeautifulDisplay.printGradientDivider(70);
							System.out.print("Please enter the number associated with your desired option and then press enter: ");
							int incorrectChoice = getUserChoice(3);

							if (incorrectChoice == 1) {
								retries++;
								continue;
							} 
							else if (incorrectChoice == 2) {
								return;  
							} 
							else {
								exitApplication();
							}
						}

						else {
							System.out.println();
							BeautifulDisplay.printError("Too many account recovery attempts.");
							System.out.println("\nYou can either return to the account settings menu or exit the application.");
							System.out.println("What would you like to do?");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Return to the account settings menu");
							System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " Exit the application");
							System.out.println();
							BeautifulDisplay.printGradientDivider(70);
							System.out.print("Please enter the number associated with your desired option and then press enter: ");

							int exitChoice = getUserChoice(2);
							if (exitChoice == 1) {
								return;
							} 
							else {
								exitApplication(); 
							} 
						}

						break;

					case SUCCESSFUL_PASSWORD_CHANGE:
						System.out.print("Press enter when you are ready to return to the account settings menu...");
						scanner.nextLine();
						return;

					case RETURN_TO_MENU:
						return;

					default:
						return;
					}
				}
			case 3: 
				return;
			case 4: 
				exitApplication();

			}
		}
	}

	/**
	 * Account Settings Menu
	 *
	 * @param currentUsername - username of the currently signed-in user
	 * @return Determines whether or not the user has decided to delete their account
	 * 		   If the function returns true, then the user has not deleted their account and they can return to the Main Menu
	 * 		   If the function returns false, then the user has deleted their account and are forcibly moved to the login page
	 * 
	 * @author Shazadul Islam
	 */
	private boolean accountSettingsMenu(String currentUsername) {
		boolean inSettings = true;
		while (inSettings) {
			clearConsole();
			// ✨ Pretty ACCOUNT SETTINGS menu
			BeautifulDisplay.printAccountSettingsMenu(currentUsername);
			int settingsChoice = getUserChoice(4);

			switch (settingsChoice){
			case 1:
				handleChangePassword(currentUsername);
				continue;
			case 2:
				handleResetSecurity(currentUsername);
				continue;
			case 3:
				clearConsole();
				BeautifulDisplay.printGradientHeader("DELETE ACCOUNT", 70);
				System.out.println("\nPlease enter your current password below and then press enter.");
				System.out.print("  Current Password: ");
				
				String currentPassword = scanner.nextLine();

				//Check if the password we just received is valid
				boolean validPass = moduleHub.verifyPassword(currentUsername, currentPassword);
				
				if (validPass) {
					clearConsole();
					BeautifulDisplay.printGradientHeader("DELETE ACCOUNT", 70);	
					System.out.println();
					System.out.println("Are you sure you want to delete this account: " + BeautifulDisplay.BOLD + BeautifulDisplay.CYAN + currentUsername + BeautifulDisplay.RESET + "? ");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  1." + BeautifulDisplay.RESET + " Yes");
					System.out.println(BeautifulDisplay.BRIGHT_YELLOW + "  2." + BeautifulDisplay.RESET + " No");
					System.out.println();
					BeautifulDisplay.printGradientDivider(70);
					System.out.print("Please enter the number associated with your desired option and then press enter: ");
					int sureDelAccount = getUserChoice(2);

					if (sureDelAccount == 1) {
						// Ask ModuleHub / Accounts to delete the user
						boolean deleted = moduleHub.callAccounts("deleteaccount", currentUsername);

						if (deleted) {
							clearConsole();
							BeautifulDisplay.printGradientHeader("DELETE ACCOUNT", 70);	
							System.out.println();
							BeautifulDisplay.printSuccess("Your account has been deleted.");
							System.out.print("Press enter when you are ready to return to the account settings menu...");
							scanner.nextLine();
							// Tell main() that the user is NO LONGER logged in
							return false;
						} else {
							clearConsole();
							BeautifulDisplay.printGradientHeader("DELETE ACCOUNT", 70);	
							System.out.println();
							BeautifulDisplay.printError("Exiting account deletion. Your account has not been removed.");
							System.out.print("Press enter when you are ready to return to the account settings menu...");
							scanner.nextLine();
							break;  // stay logged in / in settings
						}
						
					} else {
						clearConsole();
						BeautifulDisplay.printGradientHeader("DELETE ACCOUNT", 70);	
						System.out.println();
						System.out.print("Press enter when you are ready to return to the account settings menu...");
						scanner.nextLine();
						break;
					}
				}
				else {
					clearConsole();
					BeautifulDisplay.printGradientHeader("DELETE ACCOUNT", 70);	
					System.out.println();
					BeautifulDisplay.printError("The password you entered is incorrect. Account deletion failed.");
					System.out.print("Press enter when you are ready to return to the account settings menu...");
					scanner.nextLine();
					break;  // stay logged in / in settings
				}

			case 4:
				inSettings = false;
				break;
			}
		}
		return true;
	}
	
	/**
	 * Prints out the starting animation of the software
	 */
	public void intro() {
		String dash_line = "==========";

		String Hamilton_Heights[] = {
				"\t  ■■  ■■   ■■■■   ■■■     ■■■  ■■■■  ■■    ■■■■■■  ■■■■■   ■■    ■■     ■■  ■■  ■■■■■  ■■■■    ■■■■■   ■■  ■■   ■■■■■■   ■■■■■  ", 
				"\t\t  ■■  ■■  ■■  ■■  ■■■■   ■■■■   ■■   ■■      ■■   ■■   ■■  ■■■■  ■■     ■■  ■■  ■■      ■■    ■■       ■■  ■■     ■■    ■■      ",
				"\t" + dash_line +	"■■■■■■  ■■■■■■  ■■ ■■ ■■ ■■   ■■   ■■      ■■   ■■   ■■  ■■ ■■ ■■     ■■■■■■  ■■■■■   ■■    ■■  ■■   ■■■■■■     ■■     ■■■■ " + dash_line,
				"\t\t  ■■  ■■  ■■  ■■  ■■  ■■■  ■■   ■■   ■■      ■■   ■■   ■■  ■■  ■■■■     ■■  ■■  ■■      ■■    ■■   ■■  ■■  ■■     ■■        ■■  ",
				"\t\t  ■■  ■■  ■■  ■■  ■■  ■■■  ■■  ■■■■  ■■■■■■  ■■    ■■■■■   ■■   ■■■     ■■  ■■  ■■■■■  ■■■■    ■■■■■   ■■  ■■     ■■    ■■■■■   " 				
		};

		for(int i = 0; i < 5; i++) {
			Hamilton_Heights[i] = Hamilton_Heights[i].replaceAll("■", "█");
		}

		String personalFinanceManager[] = {
				"  ■■■■■■  ■■■■■■  ■■■■■■    ■■■■■   ■■■■■   ■■    ■■   ■■■■   ■■       ■■■■■  ■■■■   ■■    ■■   ■■■■   ■■    ■■   ■■■■■  ■■■■■■    ■■■     ■■■   ■■■■   ■■    ■■   ■■■■    ■■■■■   ■■■■■■   ■■■■■     ",
				"  ■■  ■■  ■■      ■■   ■■  ■■      ■■   ■■  ■■■■  ■■  ■■  ■■  ■■       ■■      ■■    ■■■■  ■■  ■■  ■■  ■■■■  ■■  ■■■     ■■        ■■■■   ■■■■  ■■  ■■  ■■■■  ■■  ■■  ■■  ■■       ■■       ■■   ■■   ",
				"  ■■■■■■  ■■■■■■  ■■■■■■    ■■■■   ■■   ■■  ■■ ■■ ■■  ■■■■■■  ■■       ■■■■■   ■■    ■■ ■■ ■■  ■■■■■■  ■■ ■■ ■■  ■■      ■■■■■■    ■■ ■■ ■■ ■■  ■■■■■■  ■■ ■■ ■■  ■■■■■■  ■■  ■■   ■■■■■■   ■■■■■■    ",
				"  ■■      ■■      ■■   ■■      ■■  ■■   ■■  ■■  ■■■■  ■■  ■■  ■■       ■■      ■■    ■■  ■■■■  ■■  ■■  ■■  ■■■■  ■■■     ■■        ■■  ■■■  ■■  ■■  ■■  ■■  ■■■■  ■■  ■■  ■■   ■■  ■■       ■■   ■■   ",
				"  ■■      ■■■■■■  ■■   ■■  ■■■■■    ■■■■■   ■■   ■■■  ■■  ■■  ■■■■     ■■     ■■■■   ■■   ■■■  ■■  ■■  ■■   ■■■   ■■■■■  ■■■■■■    ■■  ■■■  ■■  ■■  ■■  ■■   ■■■  ■■  ■■   ■■■■■   ■■■■■■   ■■   ■■   ",

		};
		for(int i = 0; i < 5; i++) {
			personalFinanceManager[i] = personalFinanceManager[i].replaceAll("■", "█");
		}
		System.out.println("\n\n");
		int size = personalFinanceManager[1].length();
		int index = 0;
		int time = 0;
		while(time < 100) {
			for(String line : Hamilton_Heights) {
				System.out.println(BeautifulDisplay.BOLD + line + BeautifulDisplay.RESET);
			};
			System.out.println();
			for(String str : personalFinanceManager) {
				int frame = index;
				System.out.print("\t\t\t");
				for(int i = 0; i < 100; i++) {				
					System.out.print(BeautifulDisplay.BRIGHT_GREEN + str.charAt(frame) + BeautifulDisplay.RESET);
					frame = ((frame+1)%size);
				}
				System.out.println();
			}
			index = (index+1)%size;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
			}
			time++;
			clearConsole();
		}
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
		MainMenu applicationInterface = new MainMenu();
		
		applicationInterface.intro();
		
		applicationInterface.clearConsole();
		boolean running = true;
		while (running) {
			String currentUser = applicationInterface.enter();

			boolean loggedIn = false;
			if (currentUser != null) {
				loggedIn = true;
			}

			while (running && loggedIn) {
				applicationInterface.clearConsole();
				BeautifulDisplay.printMainMenuScreen(currentUser);

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
