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

    // === Authentication + Accounts stack ===
    private final Storage authStorage;
    private final Authentication authModule;
    private final Accounts accountsModule;

    // === Budget storage (CSV / Budget) ===
    private final StorageManager storageModule;

    // === Prediction (DataReader + ScenarioSimulator) ===
    private final DataReader predictionData;
    private final ScenarioSimulator predictionModule;

    // === Reports ===
    private final ReportManager reportsModule;

    // === Validation ===
    private final ValidationEngine validationModule;

    // === Error handling ===
    private final ErrorHandler errorHandler;

    /**
     * Default constructor for ModuleHub. Wires all modules together.
     */
    public ModuleHub() {

        // ---- Auth + Accounts ----
        authStorage    = new Storage();
        authModule     = new Authentication(authStorage);
        accountsModule = new Accounts(authModule, authStorage);

        // ---- Budget storage (CSV files under /data) ----
        storageModule = new StorageManager();

        // ---- Prediction: read Data.csv once and share with ScenarioSimulator ----
        predictionData = new DataReader();
        predictionData.readData();       // prints errors if CSV missing or bad
        predictionModule = new ScenarioSimulator(predictionData);

        // ---- Reports ----
        reportsModule = new ReportManager();

        // ---- Validation ----
        validationModule = new ValidationEngine();

        // ---- Error handling ----
        errorHandler = new ErrorHandler();
    }

    /**
     * Calls the Storage team to load, delete, or list budget data.
     * For alpha, only actions that do NOT require a Budget object are supported.
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
     * Calls the Reports team to generate a financial report.
     * For alpha, always loads year 2025.
     *
     * @param reportType the type of report ("monthly", "annual", etc.)
     * @param username   whose report to generate (used for display only)
     * @return a status message
     */
    public String callReports(String reportType, String username) { //int year(removed from method args because year is fixed in alpha build)
        if (reportType == null) {
            return "[ModuleHub] reportType cannot be null.";
        }

        try {
            int year = 2025;  // Alpha fixed year
            reportsModule.loadYearlyData(year);
            reportsModule.displayReportOnScreen();
            return "Report generated for " + username + " (" + year + "), type: " + reportType;

        } catch (Exception e) {
            errorHandler.handleModuleError("Reports", e);
            return "[ModuleHub] Failed to generate report.";
        }
    }

    /**
     * Calls the Prediction module to run what-if scenarios.
     *
     * Alpha supports:
     *   "summary" → summary report from DataReader
     *   "compare-demo" → demo comparison between two scenarios
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
            if ("summary".equalsIgnoreCase(scenarioType)) {
                return predictionData.createSummaryReport();
            }

            else if ("compare-demo".equalsIgnoreCase(scenarioType)) {
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
     * Alpha version supports:
     *   - logout
     *   - deleteAccount
     *
     * @param action   "logout" or "deleteAccount"
     * @param username the account username
     * @return true if successful
     */
    public boolean callAccounts(String action, String username, String password) {
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
}