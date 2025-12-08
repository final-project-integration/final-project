// Integration team

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ModuleHub is the integration layer and traffic controller for the application.
 * It routes requests between Accounts, Storage, Reports, Prediction, and Validation modules.
 *
 * ModuleHub responsibilities:
 *  - Coordinate calls between team modules without re-implementing their logic
 *  - Convert data formats when passing information between modules
 *  - Provide a single integration entry point that MainMenu and the rest of
 *    the application can use
 *
 * Flow:
 *  1. Accept a request from MainMenu
 *  2. Forward the request and data to the correct team module
 *  3. Collect the response or result
 *  4. Return a simple result or status message back to the caller
 *
 * All integration responsibilities are handled here, so other modules can focus
 * only on their core logic.
 *
 * @author Denisa Cakoni
 * @author Kapil Tamang
 */
public class ModuleHub {

    /** Storage used by the authentication and accounts module. */
    private final Storage authStorage;
    /** Authentication module responsible for login, registration, and credentials. */
    private final Authentication authModule;
    /** Accounts module is responsible for user account operations. */
    private final Accounts accountsModule;

    /** StorageManager used for saving and loading user budgets by year. */
    private final StorageManager storageModule;

    /** ReportManager is responsible for computing summaries and balances. */
    private final ReportManager reportsModule;
    /** ReportAnalyzer is responsible for additional analysis on financial records. */
    private final ReportAnalyzer reportAnalyzer;
    /** ReportFormatter is responsible for formatting analysis output. */
    private final ReportFormatter reportFormatter;
    /** Handles printing all report sections to the console. */
    private final ReportDisplay reportDisplay;

    /** DataReader used by the Prediction module to read CSV budget data. */
    private final DataReader predictionData;
    /** ScenarioSimulator used to create and compare prediction scenarios. */
    private final ScenarioSimulator predictionModule;

    /** ValidationEngine used for validating user input and domain objects. */
    private final ValidationEngine validationModule;

    /** CrossFieldValidator used for cross-field and aggregate validations. */
    private final CrossFieldValidator crossFieldValidator;

    /** ErrorHandler used to log and handle module-level errors. */
    private final ErrorHandler errorHandler;

    /**
     * Reads all lines from the given file into a List&lt;String&gt;.
     *
     * @param file the file to read
     * @return list of lines read from the file
     * @throws IOException if an I/O error occurs while reading
     *
     * @author Denisa Cakoni
     */
    private List<String> readAllLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Default constructor for ModuleHub.
     * Wires together all dependent modules and prepares the integration layer.
     *
     * @author Denisa Cakoni
     */
    public ModuleHub() {
        // Auth + Accounts
        authStorage    = new Storage();
        authModule     = new Authentication(authStorage);
        accountsModule = new Accounts(authModule, authStorage);

        // Budget storage (CSV files)
        storageModule = new StorageManager();

        // Reports
        reportsModule   = new ReportManager();
        reportAnalyzer  = new ReportAnalyzer();
        reportFormatter = new ReportFormatter();
        reportDisplay   = new ReportDisplay();

        // Prediction
        predictionData   = new DataReader();
        predictionModule = new ScenarioSimulator(predictionData);

        // Validation
        validationModule    = new ValidationEngine();
        crossFieldValidator = new CrossFieldValidator();

        // Error handling
        errorHandler = new ErrorHandler();
    }

    // -----------------------Storage Integration----------------------------


    /**
     * Routes a storage-related request to the StorageManager module.
     * This method only forwards the request and handles any thrown exceptions.
     *
     * @param action   the storage action to perform ("load", "delete", "listyears")
     * @param username the username whose data is being accessed
     * @param year     the year associated with the data
     * @return true if the requested action completes without throwing an exception,
     *         false otherwise
     *
     * @author Denisa Cakoni
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
     * Gets the Budget for the specified user and year from StorageManager.
     *
     * @param username the username whose budget is requested
     * @param year     the year of the budget
     * @return the Budget for that user and year, or null if not found or on error
     *
     * @author Denisa Cakoni
     */
    public Budget getUserBudget(String username, int year) {
        try {
            return storageModule.getUserBudget(username, year);
        } catch (Exception e) {
            errorHandler.handleModuleError("Storage", e);
            return null;
        }
    }

    /**
     * Saves the provided Budget for a given user and year through StorageManager.
     * Any exceptions are handled by the ErrorHandler.
     *
     * @param username the username whose budget is being saved
     * @param year     the year of the budget
     * @param budget   the Budget object to store
     * @return true if the save operation completes successfully, false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean saveUserBudget(String username, int year, Budget budget) {
        try {
            storageModule.saveUserData(username, year, budget);
            return true;
        } catch (Exception e) {
            errorHandler.handleModuleError("Storage", e);
            return false;
        }
    }

    /**
     * Updates the Budget for a given user and year through StorageManager.
     * Any exceptions are logged and reported via ErrorHandler.
     *
     * @param username the username whose budget is being updated
     * @param year     the year of the budget
     * @param budget   the updated Budget object
     * @return true if the update operation completes successfully, false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean updateUserBudget(String username, int year, Budget budget) {
        try {
            storageModule.updateUserBudget(username, year, budget);
            return true;
        } catch (Exception e) {
            errorHandler.handleModuleError("Storage", e);
            return false;
        }
    }

    /**
     * Checks whether stored budget data exists for a given user and year.
     *
     * @param username the user whose data is being checked
     * @param year     the year to check for existing data
     * @return true if a saved file exists for that user and year, false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean hasDataForYear(String username, int year) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        try {
            Budget budget = storageModule.getUserBudget(username, year);
            return budget != null;
        } catch (Exception e) {
            errorHandler.handleModuleError("Storage", e);
            return false;
        }
    }

    /**
     * Uploads a CSV file for a specific user and year and stores it as a Budget.
     * This method:
     * resolves the CSV file path
     * validates the CSV content using ValidationEngine
     * if valid, imports it via StorageManager
     *
     * It does not print to the console; instead it returns a ValidationResult
     * so the caller (MainMenu) can decide how to display messages.
     *
     * @param username    the user who is uploading the CSV data
     * @param csvFilePath the path to the CSV file on disk (as typed by the user)
     * @param year        the year represented by the CSV data
     * @return ValidationResult describing any validation errors, or ok() on success
     *
     * @author Denisa Cakoni
     */
    public ValidationResult uploadCSVData(String username, String csvFilePath, int year) {
        //  sanity checks
        if (csvFilePath == null || csvFilePath.trim().isEmpty()) {
            return ValidationResult.error("CSV file path cannot be empty.");
        }

        // Resolve the file (as typed or inside ./data/)
        File csvFile = resolveCsvFile(csvFilePath);
        if (csvFile == null || !csvFile.exists()) {
            return ValidationResult.error(
                    "CSV file not found: '" + csvFilePath.trim()
                            + "'. Please check the name or full path and try again.");
        }

        try {
            // Read raw lines for Validation team
            List<String> lines = readAllLines(csvFile);

            // Use ValidationEngine to validate header, dates, categories, amounts, etc.
            ValidationResult csvValidation =
                    validationModule.validateCsvLines(csvFile.getName(), lines);

            if (csvValidation.hasErrors()) {
                return csvValidation;
            }

            storageModule.importCSV(username, year, csvFile.getPath());
            return ValidationResult.ok();

        } catch (Exception e) {
            // Log technical details, but return a friendly message to caller.
            errorHandler.handleModuleError("CSV Upload", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while uploading CSV. Please try again or contact support.");
            return vr;
        }
    }

    /**
     * Tries to resolve the CSV file path in a few common locations:
     *  As given (relative or absolute), after trimming whitespace
     *  Inside a data subfolder (data/filename.csv)
     *
     * @param csvFilePath the path or filename the user typed
     * @return a File that exists on disk, or null if not found
     *
     * @author Denisa Cakoni
     */
    private File resolveCsvFile(String csvFilePath) {
        if (csvFilePath == null) {
            return null;
        }

        String cleaned = csvFilePath.trim();   // kill trailing/leading spaces

        // As typed (absolute or relative)
        File direct = new File(cleaned);
        if (direct.exists()) {
            return direct;
        }

        // Try in ./data/
        File inDataFolder = new File("data", cleaned);
        if (inDataFolder.exists()) {
            return inDataFolder;
        }

        // Not found anywhere we expect
        return null;
    }

    // Reports view from stored data (no CSV prompting)


    /**
     * Generates and prints a report for a given user and year from previously stored data.
     * This method:
     *  - loads the Budget for the user and year from StorageManager
     *  - converts the Budget to a list of FinancialRecord objects
     *  - delegates summary calculations to ReportManager
     *  - delegates additional analysis to ReportAnalyzer
     *  - formats and prints the report sections to the console
     *
     * Report types:
     *  yearly:   yearly totals and analysis
     *  monthly:  monthly breakdown only
     *  category: category breakdown only
     *  full:     yearly, monthly, category, and analysis
     *
     * @param username   the user requesting the report
     * @param year       the year to generate the report for
     * @param reportType the type of report to generate ("yearly", "monthly",
     *                   "category", "full")
     * @return a status message describing the outcome
     *
     * @author Denisa Cakoni
     */
    public String viewReport(String username, int year, String reportType) {
        if (reportType == null) {
            return "[ModuleHub] reportType cannot be null.";
        }

        try {
            Budget budget = storageModule.getUserBudget(username, year);

            if (budget == null) {
                BeautifulDisplay.printError("No financial data found for year " + year);
                System.out.println();
                System.out.println("  " + BeautifulDisplay.BRIGHT_CYAN + "📤 To upload data:" +
                        BeautifulDisplay.RESET);
                System.out.println("     " + BeautifulDisplay.DIM +
                        "Main Menu → Financial Data → Upload CSV" +
                        BeautifulDisplay.RESET);
                System.out.println();
                return "[ModuleHub] No data available for year " + year;
            }

            ArrayList<ReportManager.FinancialRecord> records =
                    convertBudgetToRecords(budget, year);

            if (records.isEmpty()) {
                System.out.println("[ModuleHub] Budget exists but contains no transactions.");
                return "[ModuleHub] No transactions to report.";
            }

            reportsModule.setFinancialRecords(records);
            reportAnalyzer.setRecords(records);

            ReportManager.YearlySummary yearly  = reportsModule.generateYearlySummary(year);
            ArrayList<String> monthly           = reportsModule.generateMonthlySummary(year);
            ArrayList<String> categorySummaries = reportsModule.generateCategorySummary(year);

            String highestMonth        = reportAnalyzer.findHighestSpendingMonth(year);
            String topSpendingCategory = reportAnalyzer.findTopSpendingCategory(year);
            ArrayList<String> negativeBalanceMonths =
                    reportAnalyzer.listNegativeBalanceMonths(year);
            String netBalance       = reportsModule.calculateBalance(year);
            String netBalancePretty = reportFormatter.formatCurrency(netBalance, "$");

            switch (reportType.toLowerCase()) {
                case "yearly":
                    reportDisplay.printYearlySection(year, yearly);
                    reportDisplay.printAnalysisSection(
                            highestMonth,
                            topSpendingCategory,
                            netBalancePretty,
                            negativeBalanceMonths,
                            true
                    );
                    break;

                case "monthly":
                    reportDisplay.printMonthlySection(year, monthly);
                    break;

                case "category":
                    reportDisplay.printCategorySection(year, categorySummaries);
                    break;

                case "full":
                default:
                    reportDisplay.printFullReport(year, yearly, monthly, categorySummaries);
                    reportDisplay.printAnalysisSection(
                            highestMonth,
                            topSpendingCategory,
                            netBalancePretty,
                            negativeBalanceMonths,
                            true);
                    break;
            }

            return "Report generated for " + username + " (" + year + ")";

        } catch (Exception e) {
            errorHandler.handleModuleError("Reports", e);
            return "[ModuleHub] Failed to generate report.";
        }
    }

    /**
     * Converts a Budget instance into a list of FinancialRecord objects for reporting.
     * Each transaction in the Budget is translated into the structure required by
     * ReportManager and ReportAnalyzer.
     *
     * @param budget the Budget object returned from StorageManager
     * @param year   the year for which the report is being generated
     * @return a list of FinancialRecord objects derived from the budget's transactions
     *
     * @author Denisa Cakoni
     */
    private ArrayList<ReportManager.FinancialRecord> convertBudgetToRecords(
            Budget budget, int year) {

        ArrayList<ReportManager.FinancialRecord> records = new ArrayList<>();

        for (Transaction t : budget.getAllTransactions()) {
            String rawDate = t.getDate(); // MM/DD/YYYY (hopefully)

            if (rawDate == null) {
                System.err.println("[ModuleHub] Null date in transaction.");
                continue;
            }

            String date = rawDate.trim();
            String[] parts = date.split("/");

            if (parts.length != 3) {
                System.err.println("[ModuleHub] Invalid date in transaction: " + date);
                continue;
            }

            String monthPart = parts[0].trim();
            int month;
            try {
                month = Integer.parseInt(monthPart) - 1;  // 0-based month index
            } catch (NumberFormatException e) {
                System.err.println("[ModuleHub] Invalid month in date: " + date);
                continue;
            }

            String category   = t.getCategory();
            double amountValue = t.getAmount();
            String amount      = String.valueOf(amountValue);
            boolean isIncome   = amountValue > 0;

            ReportManager.FinancialRecord rec = new ReportManager.FinancialRecord(
                    amount, category, month, year, isIncome
            );
            records.add(rec);
        }

        return records;
    }

    //------------------------------- Predictions-------------------------------------

    /**
     * Runs a prediction scenario on previously uploaded and stored data:
     *  - loads the Budget for a user and year from StorageManager
     *  - passes the Budget directly into the Prediction DataReader
     *  - delegates the selected scenario to ScenarioSimulator
     *
     * Supported scenario types:
     *  summary – prints an overall income/expense/net summary
     *  deficit – checks for a deficit and prints a proportional reduction plan
     *  surplus – checks for a surplus and prints a proportional allocation plan
     *
     * @param username     the user requesting the prediction
     * @param year         the year whose data should be analyzed
     * @param scenarioType the type of prediction scenario to run
     * @return a status message describing the outcome
     *
     * @author Denisa Cakoni
     */
    public String runPrediction(String username, int year, String scenarioType) {
        if (scenarioType == null) {
            return "[ModuleHub] scenarioType cannot be null.";
        }

        try {
            Budget budget = storageModule.getUserBudget(username, year);

            if (budget == null) {
                BeautifulDisplay.printError("No financial data found for year " + year);
                System.out.println();
                System.out.println("  " + BeautifulDisplay.BRIGHT_CYAN + "📤 To upload data:" +
                        BeautifulDisplay.RESET);
                System.out.println("     " + BeautifulDisplay.DIM +
                        "Main Menu → Financial Data → Upload CSV" +
                        BeautifulDisplay.RESET);
                System.out.println();
                return "[ModuleHub] No data available for year " + year;
            }

            predictionData.readFromBudget(budget);

            switch (scenarioType.toLowerCase()) {

                case "summary": {
                    BeautifulDisplay.printGradientHeader("PREDICTION SUMMARY - " + year, 70);
                    String summary = predictionModule.buildFinancialSummary();
                    System.out.println(BeautifulDisplay.BRIGHT_WHITE + summary + BeautifulDisplay.RESET);
                    BeautifulDisplay.printGradientDivider(70);
                    return "Prediction summary completed for " + year + ".";
                }

                case "deficit": {
                    BeautifulDisplay.printGradientHeader("DEFICIT ANALYSIS - " + year, 70);

                    if (!predictionModule.hasDeficit()) {
                        BeautifulDisplay.printSuccess(
                                "You do not currently have a deficit. No cuts are required.");
                        BeautifulDisplay.printGradientDivider(70);
                        return "No deficit detected for " + year + ".";
                    }

                    BeautifulDisplay.printWarning(
                            "Budget deficit detected. Suggested proportional reduction plan:");
                    System.out.println();
                    String plan = predictionModule.buildDeficitProportionalPlan();

                    System.out.println(BeautifulDisplay.BRIGHT_WHITE + plan + BeautifulDisplay.RESET);
                    BeautifulDisplay.printGradientDivider(70);
                    return "Deficit analysis completed for " + year + ".";
                }

                case "surplus": {
                    BeautifulDisplay.printGradientHeader("SURPLUS ANALYSIS - " + year, 70);

                    if (!predictionModule.hasSurplus()) {
                        BeautifulDisplay.printWarning(
                                "You do not currently have a surplus for this year.");
                        BeautifulDisplay.printGradientDivider(70);
                        return "No surplus detected for " + year + ".";
                    }

                    BeautifulDisplay.printSuccess("Surplus detected. Suggested allocation plan:");
                    System.out.println();
                    String plan = predictionModule.buildSurplusProportionalPlan();
                    System.out.println(BeautifulDisplay.BRIGHT_WHITE + plan + BeautifulDisplay.RESET);
                    BeautifulDisplay.printGradientDivider(70);
                    return "Surplus analysis completed for " + year + ".";
                }

                default:
                    return "[ModuleHub] Unknown prediction scenarioType: " + scenarioType;
            }

        } catch (Exception e) {
            errorHandler.handleModuleError("Prediction", e);
            return "[ModuleHub] Failed to run prediction.";
        }
    }

    // --------------------------Validation---------------------------------------


    /**
     * Validates a single value using the ValidationEngine based on a given type.
     * If validation errors are found, they are printed to the console.
     *
     * @param type  the validation type or field name
     * @param value the value to be validated
     * @return true if the value passes validation, false if any errors occur
     *
     * @author Denisa Cakoni
     */
    public boolean callValidation(String type, String value) {
        try {
            ValidationResult result =
                    validationModule.validateUserInput(type, value);

            if (result.hasErrors()) {
                for (String msg : result.getMessages()) {
                    System.out.println(msg);
                }
                return false;
            }
            return true;

        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            return false;
        }
    }

    /**
     * Validates a single user field and returns the full ValidationResult.
     * This is useful when the caller needs to inspect all error messages.
     *
     * @param fieldName the name of the field being validated
     * @param value     the value of the field
     * @return a ValidationResult object containing any warnings or errors
     *
     * @author Denisa Cakoni
     */
    public ValidationResult validateUserField(String fieldName, String value) {
        try {
            return validationModule.validateUserInput(fieldName, value);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal validation error for field '" + fieldName + "'.");
            return vr;
        }
    }

    /**
     * Validates a transaction data transfer object using the ValidationEngine.
     *
     * @param transactionData an object representing the transaction to be validated
     * @return a ValidationResult containing any validation errors or warnings
     *
     * @author Denisa Cakoni
     */
    public ValidationResult validateTransaction(Object transactionData) {
        try {
            return validationModule.validateTransaction(transactionData);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while validating transaction.");
            return vr;
        }
    }

    /**
     * Validates a single budget line item using the ValidationEngine.
     * This is useful when a budget is represented as multiple line items.
     *
     * @param budgetItem the budget line item representation
     * @return a ValidationResult with errors or warnings for the given item
     *
     * @author Denisa Cakoni
     */
    public ValidationResult validateBudgetLineItem(Object budgetItem) {
        try {
            return validationModule.validateBudgetLineItem(budgetItem);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while validating budget line item.");
            return vr;
        }
    }

    /**
     * Validates report criteria using the ValidationEngine.
     * Report criteria might include date ranges, filters, or grouping options.
     *
     * @param reportCriteria an object describing the desired report criteria
     * @return a ValidationResult for the provided report criteria
     *
     * @author Denisa Cakoni
     */
    public ValidationResult validateReportCriteria(Object reportCriteria) {
        try {
            return validationModule.validateReportCriteria(reportCriteria);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while validating report criteria.");
            return vr;
        }
    }

    /**
     * Aggregates multiple ValidationResult objects into one using the ValidationEngine.
     *
     * @param results one or more ValidationResult instances
     * @return a single ValidationResult summarizing all input results
     *
     * @author Denisa Cakoni
     */
    public ValidationResult aggregateValidationResults(ValidationResult... results) {
        try {
            return validationModule.aggregateResults(results);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while aggregating validation results.");
            return vr;
        }
    }

    /**
     * Validates a date range using the CrossFieldValidator.
     *
     * @param startDate the starting date of a range, in string form
     * @param endDate   the ending date of a range, in string form
     * @return a ValidationResult describing any issues with the date range
     *
     * @author Denisa Cakoni
     */
    public ValidationResult validateDateRange(String startDate, String endDate) {
        try {
            return crossFieldValidator.validateDateRange(startDate, endDate);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while validating date range.");
            return vr;
        }
    }

    /**
     * Validates that a budget structure is balanced and logically consistent
     * using the CrossFieldValidator.
     *
     * @param budget an object representing the budget to validate
     * @return a ValidationResult with any detected balance issues
     *
     * @author Denisa Cakoni
     */
    public ValidationResult validateBudgetBalance(Object budget) {
        try {
            return crossFieldValidator.validateBudgetBalance(budget);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while validating budget balance.");
            return vr;
        }
    }

    /**
     * Validates that a transaction's income or expense classification is correct
     * using the CrossFieldValidator.
     *
     * @param transaction an object representing the transaction to check
     * @return a ValidationResult describing any income versus expense issues
     *
     * @author Denisa Cakoni
     */
    public ValidationResult validateIncomeVsExpense(Object transaction) {
        try {
            return crossFieldValidator.validateIncomeVsExpense(transaction);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while validating income vs expense.");
            return vr;
        }
    }

    /**
     * Detects duplicate transactions in a collection using the CrossFieldValidator.
     * Useful when importing or merging multiple sources of data.
     *
     * @param transactions a list of transaction representations to inspect
     * @return a ValidationResult that lists any detected duplicates
     *
     * @author Denisa Cakoni
     */
    public ValidationResult detectDuplicateTransactions(List<Object> transactions) {
        try {
            return crossFieldValidator.detectDuplicateTransactions(transactions);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while checking duplicate transactions.");
            return vr;
        }
    }

    /**
     * Validates a category object using the CrossFieldValidator.
     *
     * @param categoryData an object describing the category structure
     * @return a ValidationResult listing any structural or consistency issues
     *
     * @author Denisa Cakoni
     */
    public ValidationResult validateCategoryHierarchy(Object categoryData) {
        try {
            return crossFieldValidator.validateCategoryHierarchy(categoryData);
        } catch (Exception e) {
            errorHandler.handleModuleError("Validation", e);
            ValidationResult vr = new ValidationResult();
            vr.addError("Internal error while validating category hierarchy.");
            return vr;
        }
    }


    // -----------------Accounts / Authentication--------------------------------

    /**
     * Checks if a username follows all of the username rules.
     *
     * @param passedUsername the username that the function is checking
     * @return true if the passed username follows all of the username rules,
     *         false otherwise
     *
     * @author Shazadul Islam
     */
    public boolean followsUsernameRules(String passedUsername) {
        return !authModule.isInvalidUsernameFormat(passedUsername);
    }

    /**
     * Checks if a password follows all of the password rules.
     *
     * @param passedPassword the password that the function is checking
     * @return true if the passed password follows all of the password rules,
     *         false otherwise
     *
     * @author Shazadul Islam
     */
    public boolean followsPasswordRules(String passedPassword) {
        return !authModule.isInvalidPasswordFormat(passedPassword);
    }

    /**
     * Verifies whether the provided password matches the stored password
     * for the given username.
     *
     * @param passedUsername the username whose password is being checked
     * @param passedPassword the password to verify
     * @return true if the password matches the stored value, false otherwise
     *
     * @author Shazadul Islam
     */
    public boolean verifyPassword(String passedUsername, String passedPassword) {
        return authModule.checkPassword(passedUsername, passedPassword);
    }

    /**
     * Routes account action to the Accounts module.
     * Supported actions:
     *  "logout": signs out the currently logged-in user
     *  "deleteaccount": deletes the specified user account
     *
     * @param action   the account action to perform
     * @param username the username affected by the action
     * @return true if the requested action executes successfully, false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean callAccounts(String action, String username) {
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
     * Attempts to log in a user with the given credentials using the Accounts module.
     * Any errors or invalid credentials are reported through the console.
     *
     * @param username the username to authenticate
     * @param password the password for the given username
     * @return true if login succeeds, false if credentials are invalid or an error occurs
     *
     * @author Denisa Cakoni
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
     * Registers a new user account by delegating to the Accounts module.
     * This wrapper:
     *  forwards all user-centered fields,
     *  passes along the caller's confirmation flag,
     *  reports any failure back to the console.
     *
     * @param username       the desired username
     * @param password       the chosen password
     * @param secretQuestion the selected secret question for account recovery
     * @param secretAnswer   the answer to the secret question
     * @param confirm        true if registration is confirmed, false otherwise
     * @return true if the account was created and saved successfully; false if
     *         validation fails, the username is taken, the user cancels, or saving fails
     *
     * @author Denisa Cakoni
     */
    public boolean registerUser(String username,
                                String password,
                                String secretQuestion,
                                String secretAnswer,
                                boolean confirm) {
        try {
            boolean ok = accountsModule.registerAccount(
                    username, password, secretQuestion, secretAnswer
            );

            if (!ok) {
                System.out.println(
                        "[ModuleHub] Registration failed: invalid data, duplicate username, or user cancelled."
                );
            }

            return ok;

        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }

    /**
     * Logs out the currently signed-in user using the Accounts module.
     * Any issues are printed to the console.
     *
     * @return true if logout succeeds, false otherwise
     *
     * @author Denisa Cakoni
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
     * Deletes the specified user account using the Accounts module.
     * This operation may require a matching signed-in session, depending on the
     * Accounts team implementation.
     *
     * @param username the username of the account to delete
     * @return true if deletion succeeds, false otherwise
     *
     * @author Denisa Cakoni
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

    /**
     * Retrieves the secret question for a given username from the Accounts module.
     * This is typically used during password recovery.
     *
     * @param username the username whose secret question is requested
     * @return the stored secret question, or null if an error occurs
     *
     * @author Denisa Cakoni
     */
    public String getUserSecretQuestion(String username) {
        try {
            return accountsModule.getSecretQuestion(username);
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return null;
        }
    }

    /**
     * Verifies the secret answer for a given username using the Accounts module.
     * This is typically part of a password reset workflow.
     *
     * @param username     the username whose secret answer should be checked
     * @param secretAnswer the user-provided answer to verify
     * @return true if the answer is correct, false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean verifyUserSecretAnswer(String username, String secretAnswer) {
        try {
            boolean ok = accountsModule.verifySecretAnswer(username, secretAnswer);
            if (!ok) {
                System.out.println("[ModuleHub] Verify secret answer failed.");
            }
            return ok;
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }

    /**
     * Resets a user's password to a new value using the Accounts module.
     * This is the recovery flow, where the user proves identity using a
     * secret answer instead of the old password.
     *
     * @param username     the username of the account being recovered
     * @param secretAnswer the plain-text secret answer entered by the user
     * @param newPassword  the new password the user wishes to set
     * @return true if the secret answer is correct, the new password is valid,
     *         and the update is saved successfully; false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean resetUserPassword(String username,
                                     String secretAnswer,
                                     String newPassword) {
        try {
            boolean ok = accountsModule.changePassword(username, null, secretAnswer, newPassword);
            if (!ok) {
                System.out.println(
                        "[ModuleHub] Password reset failed: invalid secret answer, invalid password, or user may not exist."
                );
            }
            return ok;
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }

    /**
     * Changes a user's password using their existing (old) password for verification.
     *
     * Internally, this delegates to Accounts.changePassword(...) and passes
     * null for the secret answer, since verification is done via oldPassword.
     *
     * @param username    the username of the account whose password is being changed
     * @param oldPassword the current password entered by the user
     * @param newPassword the new password the user wishes to set
     * @return true if the old password is correct and the new password is saved;
     *         false if verification or validation fails
     *
     * @author Denisa Cakoni
     */
    public boolean changePassword(String username,
                                  String oldPassword,
                                  String newPassword) {
        try {
            boolean ok = accountsModule.changePassword(username, oldPassword, null, newPassword);
            if (!ok) {
                System.out.println(
                        "[ModuleHub] Password change failed: incorrect old password or invalid new password."
                );
            }
            return ok;
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }

    /**
     * Updates a user's secret question and answer using the Accounts module.
     * Any failure is reported through the console.
     *
     * @param username    the username whose password should be reset
     * @param newQuestion the new secret question to set
     * @param newAnswer   the new secret answer to set
     * @return true if the security question/answer reset succeeds, false otherwise
     *
     * @author Aaron Madou
     */
    public boolean updateUserSecretQuestionAndAnswer(String username,
                                                     String newQuestion,
                                                     String newAnswer) {
        try {
            boolean ok = accountsModule.setSecretQuestionAndAnswer(username, newQuestion, newAnswer);
            if (!ok) {
                System.out.println("[ModuleHub] Password reset failed (user may not exist).");
            }
            return ok;
        } catch (Exception e) {
            errorHandler.handleModuleError("Accounts", e);
            return false;
        }
    }
}

/**
 * ErrorHandler manages all error handling and recovery operations for the application.
 * It receives exceptions thrown by other modules, logs the details for debugging,
 * displays user-friendly messages, and safely recovers control back to the main menu.
 *
 * This prevents the application from crashing on unexpected errors.
 *
 * @author Kapil Tamang
 */
class ErrorHandler {

    /**
     * Creates a new ErrorHandler instance.
     * The handler provides a centralized way to display, log, and recover
     * from errors across all modules.
     *
     * @author Kapil Tamang
     */
    public ErrorHandler() { }

    /**
     * Handles an exception that was thrown by a module.
     *
     * This method does *not* catch exceptions itself.
     * Instead, exceptions are passed into this method after being caught
     * in ModuleHub or other components.
     *
     * @param moduleName the name of the module where the error occurred
     * @param error      the exception that was thrown
     *
     * @author Kapil Tamang
     */
    public void handleModuleError(String moduleName, Exception error) {
        displayError("An error has occurred in the " + moduleName + " module.");
        logError("Error in " + moduleName, error);
        recoverToMenu();
    }

    /**
     * Displays a message indicating the system is returning to the main menu.
     * This keeps the program usable after an error.
     *
     * @author Kapil Tamang
     */
    public void recoverToMenu() {
        System.out.println("Restoring main menu.");
    }

    /**
     * Logs detailed debugging information about an error.
     * This includes the timestamp, message, exception type, and full stack trace.
     *
     * @param errorMessage a brief description of the error context
     * @param error        the exception thrown
     *
     * @author Kapil Tamang
     */
    public void logError(String errorMessage, Exception error) {
        System.err.println("ERROR");
        System.err.println("Time: " + java.time.LocalDateTime.now());
        System.err.println("Message: " + errorMessage);
        System.err.println("Exception Type: " + error.getClass().getName());
        System.err.println("Stack Trace:");
        error.printStackTrace(System.err);
    }

    /**
     * Displays a user-friendly error message to the console.
     * This avoids exposing technical details to the user while still informing them
     * that something went wrong.
     *
     * @param message the error message to display
     *
     * @author Kapil Tamang
     */
    public void displayError(String message) {
        System.out.println("ERROR");
        System.out.println(message);
        System.out.println("Please try again.");
    }
}
