import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ModuleHub is the traffic controller: it routes requests to the proper team module.
 * It acts as the middleman that talks to Accounts, Storage, Reports, Prediction, and Validation teams.
 *
 * Flow:
 *  1) Accept a request
 *  2) Forward to the correct team
 *  3) Gather the response
 *  4) Return the response to the caller
 *
 * @author Denisa Cakoni
 */
public class ModuleHub {

    //  Authentication + Accounts stack
    private final Storage authStorage;
    private final Authentication authModule;
    private final Accounts accountsModule;

    // Budget storage (CSV / Budget)
    private final StorageManager storageModule;

    // Prediction (DataReader + ScenarioSimulator)
    private final DataReader predictionData;
    private final ScenarioSimulator predictionModule;
    // Prediction data loaded flag to avoid double-reading CSV
    private boolean predictionDataLoaded = false;
    // Reports
    private final ReportManager reportsModule;

    //  Validation
    private final ValidationEngine validationModule;

    // Error handling
    private final ErrorHandler errorHandler;


    // Beta skeleton: remember what file/year we last used for reports
    // So later Prediction & Storage can reuse these.

    private String lastReportFileName = null; // TODO (Beta): expose to other modules if needed
    private int    lastReportYear     = -1;   // TODO (Beta): use when choosing among uploaded years

    /**
     * Default constructor for ModuleHub. Wires all modules together.
     */
    public ModuleHub() {
        // Auth + Accounts
        authStorage    = new Storage();
        authModule     = new Authentication(authStorage);
        accountsModule = new Accounts(authModule, authStorage);

        // Budget storage (CSV files under /data)
        storageModule = new StorageManager();

        // Prediction: read Data.csv once and share with ScenarioSimulator
        predictionData = new DataReader();
        // predictionData.readData();   // TODO: enable after Prediction team finalizes file
        predictionModule = new ScenarioSimulator(predictionData);

        //  Reports
        reportsModule = new ReportManager();

        // Validation
        validationModule = new ValidationEngine();

        // Error handling
        errorHandler = new ErrorHandler();
    }

    /**
     * Calls the Storage team to load, delete, or list budget data.
     * For alpha, only actions that don't require a Budget object are supported.
     *
     * @param action   "load", "delete", or "listyears"
     * @param username user whose data we want
     * @param year     the year to load/delete/list
     * @return true if successful, false otherwise
     */
    public boolean callStorage(String action, String username, int year) {
        if (action == null) {
            System.out.println("[ModuleHub] storage action cannot be null.");
            return false;
        }

        try {
            switch (action.toLowerCase()) {
                case "load":
                    storageModule.loadUserData(username, year);
                    return true;
                case "delete":
                    storageModule.deleteUserData(username, year);
                    return true;
                case "listyears":
                    storageModule.listAvailableYears(username);
                    return true;
                default:
                    System.out.println("[ModuleHub] Unknown storage action: " + action);
                    return false;
            }
        } catch (Exception e) {
            errorHandler.handleModuleError("Storage", e);
            return false;
        }
    }

    /**
     * Generates a financial report by loading the CSV data, creating summaries,
     * and printing a clean formatted report to the console.
     *
     * For alpha, the user is prompted for a CSV file name. If  blank,
     * the default "data.csv" is used.
     *
     * @param reportType the type of report requested (example "monthly", "annual")
     * @param username   the username requesting the report (for display only)
     * @param in         shared Scanner from MainMenu for console input
     * @return a status message indicating success or failure
     */
    public String callReports(String reportType, String username, Scanner in) {
        if (reportType == null) {
            return "[ModuleHub] reportType cannot be null.";
        }

        try {
            // Ask user which CSV file to use
            System.out.println("---CSV Loader ---");
            System.out.println("Please enter the name of the CSV file to load.");
            System.out.println("• If the CSV is in the same folder as the JAR, just type:   Data.csv");
            System.out.println("• If it’s somewhere else, provide the full path.");
            System.out.print("CSV filename (press Enter to use default: Data.csv): ");

            String fileName = in.nextLine().trim();
            if (fileName.isEmpty()) {
                fileName = "data.csv";
            }
            // missing file
            File f = new File(fileName);
            if (!f.exists()) {
                System.out.println("[Reports] File not found: " + fileName);
                return "[ModuleHub] CSV file not found.";
            }

            System.out.println("\n[Reports] Loading file: " + fileName);

            // Load records from the chosen CSV
            ArrayList<ReportManager.FinancialRecord> records = loadReportCsv(fileName);

            if (records.isEmpty()) {
                System.out.println("[Reports] No records were loaded from " + fileName + ".");
                return "[ModuleHub] No data to report.";
            }

            // Use the year from the first record (assuming all rows are same year)
            int year = records.get(0).getYear();

            //  Beta skeleton: remember last used file + year ---
            lastReportFileName = fileName; // TODO (Beta): let Storage/Prediction reuse this
            lastReportYear     = year;     // TODO (Beta): use when user chooses among uploaded years

            // Pass records to the Reports module
            reportsModule.setFinancialRecords(records);

            // Generate summaries
            ReportManager.YearlySummary yearly  = reportsModule.generateYearlySummary(year);
            ArrayList<String> monthly           = reportsModule.generateMonthlySummary(year);
            ArrayList<String> categorySummaries = reportsModule.generateCategorySummary(year);

            // Print the formatted report block
            printFormattedReport(year, yearly, monthly, categorySummaries);

            return "Report generated for " + username
                    + " (" + year + "), type: " + reportType;

        } catch (Exception e) {
            errorHandler.handleModuleError("Reports", e);
            return "[ModuleHub] Failed to generate report.";
        }
    }

    /**
     * Prints a nicely formatted financial report section to the console.
     * Keeps formatting separate from logic so the main report method stays clean.
     */
    private void printFormattedReport(int year,
                                      ReportManager.YearlySummary yearly,
                                      ArrayList<String> monthly,
                                      ArrayList<String> categorySummaries) {

        System.out.println("┌───────────────────────────────────────────────┐");
        System.out.println("│                FINANCIAL REPORT               │");
        System.out.println("└───────────────────────────────────────────────┘");

        System.out.println(" Year: " + year);
        System.out.println();
        System.out.println(" ───────────────── YEARLY SUMMARY ─────────────────");
        System.out.printf("   Total Income:    $%s%n", yearly.getTotalIncome());
        System.out.printf("   Total Expenses:  $%s%n", yearly.getTotalExpenses());
        System.out.printf("   Net Balance:     $%s%n", yearly.getNetBalance());

        System.out.println("\n ───────────────── MONTHLY SUMMARY ────────────────");
        for (String line : monthly) {
            System.out.println("   • " + line);
        }

        System.out.println("\n ──────────────── CATEGORY SUMMARY ────────────────");
        for (String line : categorySummaries) {
            System.out.println("   • " + line);
        }

        System.out.println("────────────────────────────────────────────────────\n");
    }

    /**
     * Loads report data from a CSV file and converts each row into a
     * ReportManager.FinancialRecord.
     *
     * Date is parsed as MM/DD/YYYY and converted to:
     *   month = 0-based index (0 = January, 11 = December)
     *   year  = four-digit year (for example 2024)
     *
     * Amount is parsed as double, and isIncome is true if amount > 0.
     *
     * @param path path to the CSV file ("data.csv")
     * @return list of FinancialRecord objects
     */
    private ArrayList<ReportManager.FinancialRecord> loadReportCsv(String path) {
        ArrayList<ReportManager.FinancialRecord> list = new ArrayList<>();

        try (Scanner sc = new Scanner(new File(path))) {
            // Skip header if present
            if (!sc.hasNextLine()) {
                return list;
            }
            sc.nextLine(); // "Date,Category,Amount"

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 3) {
                    continue; // skip bad lines
                }

                String date      = parts[0].trim(); // mm/dd/yyyy
                String category  = parts[1].trim();
                String amountStr = parts[2].trim();

                // Parse date
                String[] d = date.split("/");
                if (d.length != 3) {
                    continue;
                }
                int month = Integer.parseInt(d[0]) - 1; // ReportManager uses 0-based month
                int year  = Integer.parseInt(d[2]);

                double amount = Double.parseDouble(amountStr);
                boolean isIncome = amount > 0;

                ReportManager.FinancialRecord rec =
                        new ReportManager.FinancialRecord(
                                amountStr,
                                category,
                                month,
                                year,
                                isIncome
                        );

                list.add(rec);
            }

        } catch (Exception e) {
            System.out.println("[ModuleHub] Could not read reports CSV: " + e.getMessage());
        }

        return list;
    }

    /**
     * Calls the Prediction module to run what-if scenarios.
     *
     * Alpha supports:
     *   summary      :summary report from DataReader
     *   compare-demo :demo comparison between two scenarios
     *
     * @param scenarioType type of prediction ("summary", "compare-demo")
     * @param username     user (not used in alpha)
     * @param year         year (not used in alpha)
     * @return prediction result text
     */
    public String callPrediction(String scenarioType, String username, int year) {
        if (scenarioType == null) {
            return "[ModuleHub] scenarioType cannot be null.";
        }

        try {
            // Alpha: DataReader always reads "Data.csv" internally.
            // TODO (Beta): once Prediction supports readData(String fileName),
            //              we'll call it here using lastReportFileName / year.
            if (!predictionDataLoaded) {
            	/**@bug:drpredicationData has no filename argument, which cause a syntax error if left as is*/
            	//drpredictionData.readData();  // current API: no filename parameter
                predictionDataLoaded = true;
            }

            if ("summary".equalsIgnoreCase(scenarioType)) {
                String report = predictionData.createSummaryReport();
                System.out.println("\n--- Prediction Summary (from DataReader) ---");
                System.out.println(report);
                return "Prediction summary generated (see console output).";

            } else if ("compare-demo".equalsIgnoreCase(scenarioType)) {
                predictionModule.createScenario("BaseScenario");
                predictionModule.createScenario("AdjustedScenario");
                predictionModule.applyExpenseChange("AdjustedScenario", "Entertainment", 50.0);
                predictionModule.compareScenarios("BaseScenario", "AdjustedScenario");
                return "Prediction scenario comparison complete (see console).";
            }

            return "[ModuleHub] Unknown prediction scenarioType: " + scenarioType;

        } catch (Exception e) {
            errorHandler.handleModuleError("Prediction", e);
            return "[ModuleHub] Failed to run prediction.";
        }
    }

    /**
     * Calls the Validation team to check input validity.
     *
     * @param validationType type of validation ("userinput", "text")
     * @param dataToValidate input data
     * @return true if input is valid
     */
    public boolean callValidation(String validationType, String dataToValidate) {
        if (validationType == null) {
            System.out.println("[ModuleHub] validationType cannot be null.");
            return false;
        }

        try {
            switch (validationType.toLowerCase()) {
                case "userinput":
                case "text":
                    ValidationResult result =
                            validationModule.validateUserInput("input", dataToValidate);

                    if (result.hasErrors()) {
                        for (String msg : result.getMessages()) {
                            System.out.println(msg);
                        }
                        return false;
                    }
                    return true;

                default:
                    System.out.println("[ModuleHub] Unknown validation type: " + validationType);
                    return false;
            }

        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            return false;
        }
    }

    /**
     * Calls the Accounts module for login/logout/deletion.
     *
     *
     * @param action   "logout" or "deleteAccount"
     * @param username the account username
     * @return true if successful
     */
    public boolean callAccounts(String action, String username)  {
        if (action == null) {
            System.out.println("[ModuleHub] accounts action cannot be null.");
            return false;
        }

        try {
            switch (action.toLowerCase()) {
    case "logout":
        return accountsModule.signOut();
    case "deleteaccount":
        return accountsModule.deleteUser(username);
    default:
        System.out.println("[ModuleHub] Unknown accounts action: " + action);
        return false;
}

        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }

    /**
     * Attempts to log a user in using the Accounts module.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return true if login succeeds, false otherwise
     */
    public boolean loginUser(String username, String password) {
        if (username == null || password == null) {
            System.out.println("[ModuleHub] Username and password cannot be null.");
            return false;
        }

        try {
            boolean ok = accountsModule.signIn(username, password);
            if (!ok) {
                System.out.println("[ModuleHub] Login failed: invalid username or password.");
            }
            return ok;
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }

   /**
     * Registers a new user account
     * @param username       desired username
     * @param password       desired password
     * @param secretQuestion recovery question
     * @param secretAnswer   recovery answer
     * @return true if registration succeeds, false otherwise
     */
    public boolean registerUser(String username,
                                String password,
                                String secretQuestion,
                                String secretAnswer) {
        try {
            boolean ok = accountsModule.registerAccount(
                    username, password, secretQuestion, secretAnswer
            );
            if (!ok) {
                System.out.println("[ModuleHub] Registration failed: invalid data or username already exists.");
            }
            return ok;
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }

    /**
     * Logs the current user out.
     *
     * @return true if logout succeeded, false otherwise
     */
    public boolean logoutUser() {
        try {
            boolean ok = accountsModule.signOut();
            if (!ok) {
                System.out.println("[ModuleHub] Logout failed: no user is currently signed in.");
            }
            return ok;
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }

    /**
     * Deletes the currently signed-in user account.
     *
     * @param username the username to delete (should match signed-in user)
     * @return true if deletion succeeded, false otherwise
     */
    public boolean deleteUserAccount(String username) {
        try {
            boolean ok = accountsModule.deleteUser(username);
            if (!ok) {
                System.out.println("[ModuleHub] Account deletion failed: either not signed in, or username mismatch.");
            }
            return ok;
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }
    // These getters are for the Beta build so Prediction/Storage
    // can access whichever file the user last loaded.
    public String getLastReportFileName() {
        return lastReportFileName;
    }

    public int getLastReportYear() {
        return lastReportYear;
    }

}
