import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Simple test file for ModuleHub.
 * This lets us quickly try out different parts of the system
 * (Accounts, Storage, Reports, Prediction, Validation)
 * without having to go through MainMenu every time.
 *
 * Basically this is just a sandbox to make sure all the modules
 * respond correctly and nothing crashes.
 * @author Denisa Cakoni
 */
public class ModuleHubTest {

    public static void main(String[] args) {
        ModuleHub hub = new ModuleHub();
        Scanner in = new Scanner(System.in);

        System.out.println("=== ModuleHub Test Harness ===");

        // Accounts / Authentication Test 
        // Register a user, log in, and check that everything works normally.
        // Good for confirming basic account flow before using the real MainMenu.
        System.out.println("\n--- Accounts / Authentication test ---");
        String username = "testuser";
        String password = "password123";

        System.out.println("Registering user: " + username);
        boolean regOk = hub.registerUser(
                username,
                password,
                "What is your favorite color?",
                "blue"
        );
        System.out.println("registerUser -> " + regOk);

        System.out.println("Logging in user: " + username);
        boolean loginOk = hub.loginUser(username, password);
        System.out.println("loginUser -> " + loginOk);

        // Storage Test 
        // not creating real Budget objects here yet, but we can at least
        // check that listing/loading/deleting years doesn't crash.
        System.out.println("\n--- Storage test (no Budget object yet) ---");
        System.out.println("Listing years for user: " + username);
        boolean listYearsOk = hub.callStorage("listyears", username, 0);
        System.out.println("callStorage(listyears) -> " + listYearsOk);

        // Validation (Single-Field) Test 
        // Try validating empty text vs normal text.
        // Should show an error on the empty one and pass the valid one.
        System.out.println("\n--- Validation test ---");
        ValidationResult val1 = hub.validateUserField("amount", "");
        System.out.println("validateUserField('amount', '') -> hasErrors=" + val1.hasErrors());
        System.out.println("Messages:");
        for (String msg : val1.getMessages()) {
            System.out.println("  " + msg);
        }

        ValidationResult val2 = hub.validateUserField("notes", "This is a valid note.");
        System.out.println("\nvalidateUserField('notes', 'This is a valid note.') -> hasErrors=" + val2.hasErrors());

        //  Validation Combine Test 
        // Make sure multiple ValidationResult objects can be merged together
        // and that the combined result reports the expected number of errors.
        ValidationResult combined = hub.aggregateValidationResults(val1, val2);
        System.out.println("\naggregateValidationResults(val1, val2) -> " + combined.summary());

        //  Cross-field validation examples
        System.out.println("\n--- Cross-field Validation test ---");
        ValidationResult dateOk = hub.validateDateRange("2025-01-01", "2025-12-31");
        System.out.println("validateDateRange(2025-01-01, 2025-12-31) -> hasErrors=" + dateOk.hasErrors());

        ValidationResult dateBad = hub.validateDateRange("2025-12-31", "2025-01-01");
        System.out.println("validateDateRange(2025-12-31, 2025-01-01) -> hasErrors=" + dateBad.hasErrors());
        for (String msg : dateBad.getMessages()) {
            System.out.println("  " + msg);
        }

        // Reports Test 
        // This part loads a CSV and generates a formatted report.
        // You’ll need a real data.csv file next to the project for this to work.
        System.out.println("\n--- Reports test ---");
        System.out.println("Make sure there is a data.csv (or your test CSV) available.");
        System.out.println("When prompted, press Enter to use default data.csv,");
        System.out.println("or type another file name if you have one.\n");

        String reportStatus = hub.callReports("annual", username, in);
        System.out.println("callReports -> " + reportStatus);

        // Prediction Test 
        // Runs a basic summary prediction to make sure the Prediction team’s
        // data reader and simulator connect correctly through ModuleHub.
        System.out.println("\n--- Prediction test (summary) ---");
        String predStatus = hub.callPrediction("summary", username, 0);
        System.out.println("callPrediction('summary') -> " + predStatus);

        //  Accounts Cleanup Test 
        // Just verifying secret question, answer check, password reset,
        // and deleting the account at the end.
        System.out.println("\n--- Accounts clean-up test ---");
        String secretQuestion = hub.getUserSecretQuestion(username);
        System.out.println("getUserSecretQuestion -> " + secretQuestion);

        boolean answerOk = hub.verifyUserSecretAnswer(username, "blue");
        System.out.println("verifyUserSecretAnswer('blue') -> " + answerOk);

        boolean resetOk = hub.resetUserPassword(username, "newPass123");
        System.out.println("resetUserPassword -> " + resetOk);

        boolean logoutOk = hub.logoutUser();
        System.out.println("logoutUser -> " + logoutOk);

        boolean deleteOk = hub.deleteUserAccount(username);
        System.out.println("deleteUserAccount -> " + deleteOk);

        System.out.println("\n=== ModuleHub Test Harness complete ===");
        in.close();
    }
}
