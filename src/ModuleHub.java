import java.util.ArrayList;
import java.util.List;

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
 *
 */
public class ModuleHub {


    //  Team module fields


    private final Accounts          accountsModule;
    private final Authentication    authModule;
    private final StorageManager    storageModule;
    private final ScenarioSimulator predictionModule;
    private final ReportManager     reportsModule;
    private final ValidationEngine  validationModule;

    // shared data for storage/report/prediction
    private Budget      currentBudget;
    private final DataReader baseDataReader;

    /**
     * Default constructor for ModuleHub.
     * Creates one instance of each team module that the hub will talk to.
     */
    public ModuleHub() {
        // Accounts + Authentication
        this.accountsModule     = new Accounts();
        this.authModule         = new Authentication();

        // Storage
        this.storageModule      = new StorageManager();

        // Prediction: use shared DataReader
        this.baseDataReader     = new DataReader();
        this.baseDataReader.readData();
        this.predictionModule   = new ScenarioSimulator(baseDataReader);

        // Reports
        this.reportsModule      = new ReportManager();

        // Validation
        this.validationModule   = new ValidationEngine();

        // No budget yet – will be supplied later by UI
        this.currentBudget      = null;
    }

    /**
     * Allows MainMenu or other code to tell the hub what the "current" Budget is.
     *
     * @param budget active Budget object for the logged-in user/year
     */
    public void setCurrentBudget(Budget budget) {
        this.currentBudget = budget;
    }


    // Storage


    /**
     * Calls the Storage team to load, save, or delete budget data.
     *
     * @param action   what we want Storage to do ("load", "save", "delete", "listyears")
     * @param username which user's data we're working with
     * @param year     which year's budget data
     * @return true if the call was dispatched successfully, false otherwise
     */
    public boolean callStorage(String action, String username, int year) {
        if (action == null) {
            System.out.println("[ModuleHub] Storage action cannot be null.");
            return false;
        }

        switch (action.toLowerCase()) {
            case "load":
                storageModule.loadUserData(username, year);
                return true;

            case "save":
                if (currentBudget == null) {
                    System.out.println("[ModuleHub] No current budget set – cannot save.");
                    return false;
                }
                storageModule.saveUserData(username, year, currentBudget);
                return true;

            case "delete":
                storageModule.deleteUserData(username, year);
                return true;

            case "listyears":
                storageModule.listAvailableYears(username);
                return true;

            default:
                System.out.println("[ModuleHub] Invalid storage action: " + action);
                return false;
        }
    }


    // Reports


    /**
     * Calls the Reports team to generate a financial report.
     *
     * @param reportType "monthly", "category", or "yearly"
     * @param username   whose report to generate (reserved for future use)
     * @param year       which year to report on
     * @return formatted report text
     */
    public String callReports(String reportType, String username, int year) {
        if (reportType == null) {
            return "[ModuleHub] reportType cannot be null.";
        }

        // In a real integration, ReportManager would receive real FinancialRecord data
        reportsModule.loadYearlyData(year); // currently just a stub print

        if (reportType.equalsIgnoreCase("monthly")) {
            ArrayList<String> lines = reportsModule.generateMonthlySummary(year);
            return String.join("\n", lines);
        } else if (reportType.equalsIgnoreCase("category")) {
            ArrayList<String> lines = reportsModule.generateCategorySummary(year);
            return String.join("\n", lines);
        } else if (reportType.equalsIgnoreCase("yearly")) {
            ReportManager.YearlySummary summary = reportsModule.generateYearlySummary(year);
            return "Yearly Summary for " + year + "\n"
                    + "Income:  $" + summary.getTotalIncome() + "\n"
                    + "Expenses:$" + summary.getTotalExpenses() + "\n"
                    + "Net:     $" + summary.getNetBalance();
        } else {
            return "[ModuleHub] Unknown report type: " + reportType;
        }
    }


    //  Prediction


    /**
     * Calls the Prediction module to run "what-if" scenarios.
     *
     * @param scenarioType what to calculate ("compare", etc.)
     * @param username     whose data to use (reserved for future multi-user support)
     * @param year         which year's data to use
     * @return prediction result text
     */
    public String callPrediction(String scenarioType, String username, int year) {
        if (scenarioType == null) {
            return "[ModuleHub] scenarioType cannot be null.";
        }

        if (scenarioType.equalsIgnoreCase("compare")) {
            boolean s1 = predictionModule.createScenario("Base");
            boolean s2 = predictionModule.createScenario("WhatIf");

            if (!s1 || !s2) {
                return "[ModuleHub] Could not create prediction scenarios.";
            }

            predictionModule.applyExpenseChange("WhatIf", "Entertainment", 50.0);
            predictionModule.compareScenarios("Base", "WhatIf");
            return "[ModuleHub] Compared scenarios 'Base' and 'WhatIf' (see console output).";
        }

        return "[ModuleHub] Unknown prediction scenario: " + scenarioType;
    }


    //  Validation


    /**
     * Calls the Validation team to check if data is valid.
     *
     * @param validationType type of validation to perform ("userinput", "transaction", etc.)
     * @param dataToValidate actual input data (or DTO placeholder)
     * @return true if valid, false otherwise
     */
    public boolean callValidation(String validationType, String dataToValidate) {
        if (validationType == null) {
            System.out.println("[ModuleHub] validationType cannot be null.");
            return false;
        }

        ValidationResult result;

        switch (validationType.toLowerCase()) {
            case "userinput":
                result = validationModule.validateUserInput("userInput", dataToValidate);
                break;

            case "transaction":
                // For now we just pass the raw string as a placeholder DTO
                result = validationModule.validateTransaction(dataToValidate);
                break;

            default:
                System.out.println("[ModuleHub] Unknown validation type: " + validationType);
                return false;
        }

        if (result.hasErrors()) {
            List<String> messages = result.getMessages();
            for (String msg : messages) {
                System.out.println(msg);
            }
            return false;
        }

        return true;
    }


    //  Accounts+authentication


    /**
     * Calls the Accounts module for user account actions such as login/logout/password.
     *
     * NOTE: This simplified version only supports "logout" and "deleteAccount"
     * because the Accounts API uses username+password, which MainMenu will supply later.
     *
     * @param action   "logout", "deleteAccount", etc.
     * @param username the account username
     * @return true if successful, false otherwise
     */
    public boolean callAccounts(String action, String username) {
        if (action == null) {
            System.out.println("[ModuleHub] accounts action cannot be null.");
            return false;
        }

        switch (action.toLowerCase()) {
            case "logout":
                return accountsModule.signOut();

            case "deleteaccount":
                return accountsModule.deleteUser(username);

            default:
                System.out.println("[ModuleHub] Unknown accounts action: " + action);
                return false;
        }
    }
}
