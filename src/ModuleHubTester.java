/**
 * Simple tester for ModuleHub.
 * Bypasses MainMenu + calls the hub directly.
 *
 * @author Denisa Cakoni
 */
public class ModuleHubTester {
    public static void main(String[] args) {

        ModuleHub hub = new ModuleHub();


        // 1. Test storage

        System.out.println("\n=== Test Storage ===");
        hub.callStorage("listyears", "alice", 0);


        // 2. Test reports

        System.out.println("\n=== Test Reports ===");
        String monthly = hub.callReports("monthly", "alice", 2025);
        System.out.println(monthly);


        // 3. Test prediction

        System.out.println("\n=== Test Prediction ===");
        String prediction = hub.callPrediction("whatif-surplus", "alice", 2024);
        System.out.println(prediction);



        // 4. Test validation

        System.out.println("\n=== Test Validation ===");
        boolean ok1 = hub.callValidation("userinput", "12345");
        boolean ok2 = hub.callValidation("userinput", "");
        System.out.println("Validation #1: " + ok1);
        System.out.println("Validation #2: " + ok2);


        // 5. Test accountd

        System.out.println("\n=== Test Accounts ===");
        hub.callAccounts("deleteaccount", "alice");
    }
}
