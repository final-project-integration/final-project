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

	// Single integration point to the rest of the system
	private final ModuleHub moduleHub;

	/**
	 * Default constructor for MainMenu. Wires in a new ModuleHub instance.
	 */
	public MainMenu() {
		moduleHub = new ModuleHub();
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

					boolean validLogin = moduleHub.loginUser(loginUsername, loginPassword);
					if (validLogin) {
						System.out.println();
						return (loginUsername);
					} else {
						System.out.println("Your username or password was incorrect or that account does not exist.");
						System.out.println();
						System.out.println("Would you like to try logging in again or create a new account? ");
						System.out.println(" 1. Try Logging in again");
						System.out.println(" 2. Create a New Account");
						System.out.print("Please enter the number associated with your desired option: ");
						int retryChoice = getUserChoice(2);
						System.out.println();
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
						System.out.println();
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
		System.out.println("  1. Table of Contents");
		System.out.println("  2. Settings");
		System.out.println("  3. Quit Program");
		System.out.print("Please enter the number associated with your desired option: ");

		int mainMenuChoice = getUserChoice(3);
		System.out.println();
		return mainMenuChoice;
	}

	/**
	 * Return the user to a table of contents of modules. If they are within a
	 * module and would like to go to a different module, then they can return to
	 * the list of modules and select the next module that they would like to view.
	 *
	 * @return tableMenuChoice - the user's table of contents choice (1–3)
	 */

	public int toModules() {
		// Return to modules list
		System.out.println("Table of Contents:");
		System.out.println("  1. Reports");
		System.out.println("  2. Prediction");
		System.out.println("  3. Back to Main Menu");
		System.out.print("Please enter the number associated with your desired option: ");
		int tableChoice = getUserChoice(3);
		System.out.println();
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
		System.out.println("  3. Reset Secret Question and Answer");
		System.out.println("  4. Delete Account");
		System.out.println("  5. Return to Main Menu");
		System.out.print("Please enter the number associated with your desired option: ");
		int settingsChoice = getUserChoice(5);
		System.out.println();
		return settingsChoice;
	}

	/**
	 * Runs the entire program
	 */
	public void start() {
		System.out.println("Hamilton Heights Presents");
		System.out.println("Personal Finance Manager");
		System.out.println();

		// Login / registration
		String currentUser = enter();

		boolean running = true;
		while (running) {
			int mainMenuChoice = toMenu();

			switch (mainMenuChoice) {
			case 1: // Table of contents → table of contents
				boolean inModules = true;

				while (inModules) {
					int tableChoice = toModules();

					switch (tableChoice) {
					case 1:
						// Reports (alpha: single hard-coded report)
						moduleHub.callReports("monthly", currentUser, scanner);
						break;
					case 2:
						// Prediction (alpha: simple summary scenario)
						moduleHub.callPrediction("summary", currentUser, 2024);
						break;
					case 3:
						inModules = false;
						break;
					}
				}
				break;

			case 2:
				// Settings stub; can later use moduleHub + Accounts for password / recovery changes
				boolean inSettings = true;
				while (inSettings){
					int settingsChoice = displayAccountSettings(currentUser);
					switch (settingsChoice){
					case 1:
						System.out.println("Change Password(coming soon for beta).");
					case 2:
						System.out.println("Reset Password(coming soon for beta).");
					case 3:
						System.out.println("Reset Secret Question and Answer(coming soon for beta).");
					case 4:
						System.out.println("Are you sure you want to delete this account: "+ currentUser + "? ");
						System.out.println(" 1. Yes");
						System.out.println(" 2. No");
						System.out.print("Please enter the number associated with your desired option: ");
						int sureDelAccount = getUserChoice(2);
						
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
				running = false;
				break;
			}
		}
		exitProgram();
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
		programMenu.start();
	}
}
