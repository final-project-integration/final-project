import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Simple console test harness for ModuleHub.
 * Lets you exercise core flows without going through MainMenu.
 *
 * @author Denisa Cakoni
 */
public class ModuleHubTest {

    public static void main(String[] args) {
        ModuleHub hub = new ModuleHub();
        Scanner in = new Scanner(System.in);

        System.out.println("=== ModuleHub Test Harness ===");

        // 1) Quick Accounts / Auth test
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

        // 2) Storage basic calls (these will depend on your StorageManager internals)
        System.out.println("\n--- Storage test (no Budget object yet) ---");
        System.out.println("Listing years for user: " + username);
        boolean listYearsOk = hub.callStorage("listyears", username, 0);
        System.out.println("callStorage(listyears) -> " + listYearsOk);

        // 3) Validation basic test
        System.out.println("\n--- Validation test ---");
        ValidationResult val1 = hub.validateUserField("amount", "");
        System.out.println("validateUserField('amount', '') -> hasErrors=" + val1.hasErrors());
        System.out.println("Messages:");
        for (String msg : val1.getMessages()) {
            System.out.println("  " + msg);
        }

        ValidationResult val2 = hub.validateUserField("notes", "This is a valid note.");
        System.out.println("\nvalidateUserField('notes', 'This is a valid note.') -> hasErrors=" + val2.hasErrors());

        ValidationResult combined = hub.aggregateValidationResults(val1, val2);
        System.out.println("\naggregateValidationResults(val1, val2) -> " + combined.summary());

        // 4) Cross-field validation examples
        System.out.println("\n--- Cross-field Validation test ---");
        ValidationResult dateOk = hub.validateDateRange("2025-01-01", "2025-12-31");
        System.out.println("validateDateRange(2025-01-01, 2025-12-31) -> hasErrors=" + dateOk.hasErrors());

        ValidationResult dateBad = hub.validateDateRange("2025-12-31", "2025-01-01");
        System.out.println("validateDateRange(2025-12-31, 2025-01-01) -> hasErrors=" + dateBad.hasErrors());
        for (String msg : dateBad.getMessages()) {
            System.out.println("  " + msg);
        }

        // 5) Reports & Prediction (will ask for CSV name)
        System.out.println("\n--- Reports test ---");
        System.out.println("Make sure there is a data.csv (or your test CSV) available.");
        System.out.println("When prompted, press Enter to use default data.csv,");
        System.out.println("or type another file name if you have one.\n");

        String reportStatus = hub.callReports("annual", username, in);
        System.out.println("callReports -> " + reportStatus);

        System.out.println("\n--- Prediction test (summary) ---");
        String predStatus = hub.callPrediction("summary", username, 0);
        System.out.println("callPrediction('summary') -> " + predStatus);

        // 6) Accounts clean-up (optional)
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
