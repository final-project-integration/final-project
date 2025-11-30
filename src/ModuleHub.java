import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ModuleHub is the integration layer and traffic controller for the application.
 * It routes requests between Accounts, Storage, Reports, Prediction, and Validation modules.
 *
 * ModuleHub responsibilities:
 * Coordinate calls between team modules without re-implementing their logic
 * Convert data formats when passing information between modules
 * Provide a single integration entry point that MainMenu and the rest of the application can use
 * Handle CSV upload and the workflow of persisting that data through StorageManager
 *
 * flow:
 * 1. Accept a request from MainMenu
 * 2. Forward the request and data to the correct team module
 * 3. Collect the response or result
 * 4. Return a simple result or status message back to the caller
 *
 * All integration responsibilities are handled here so other modules can focus
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
    /** Accounts module responsible for user account operations. */
    private final Accounts accountsModule;


    /** StorageManager used for saving and loading user budgets by year. */
    private final StorageManager storageModule;


    /** ReportManager responsible for computing summaries and balances. */
    private final ReportManager reportsModule;
    /** ReportAnalyzer responsible for additional analysis on financial records. */
    private final ReportAnalyzer reportAnalyzer;
    /** ReportFormatter responsible for formatting analysis output. */
    private final ReportFormatter reportFormatter;


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

        // Prediction
        predictionData   = new DataReader();
        predictionModule = new ScenarioSimulator(predictionData);

        // Validation
        validationModule    = new ValidationEngine();
        crossFieldValidator = new CrossFieldValidator();

        // Error handling
        errorHandler = new ErrorHandler();
    }


    // Storage Integration

    /**
     * Routes a storage-related request to the StorageManager module.
     * This method only forwards the request and handles any thrown exceptions.
     *
     * @param action   the storage action to perform ("load", "delete", "listyears")
     * @param username the username whose data is being accessed
     * @param year     the year associated with the data
     * @return true if the requested action completes without throwing an exception false otherwise
     *
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
     * Updates Budget for a given user and year through StorageManager.
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


    // Csv upload (Separate from viewing reports)


    /**
     * Uploads a CSV file for a specific user and year and stores it as a Budget.
     * This method is used to take raw CSV input, transform it into a Budget,
     * and delegate persistence to the StorageManager.
     *
     * @param username    the user who is uploading the CSV data
     * @param csvFilePath the path to the CSV file on disk
     * @param year        the year represented by the CSV data
     * @return true if the upload succeeds and data is saved, false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean uploadCSVData(String username, String csvFilePath, int year) {
        if (csvFilePath == null || csvFilePath.trim().isEmpty()) {
            System.out.println("[ModuleHub] CSV file path cannot be empty.");
            return false;
        }

        File csvFile = new File(csvFilePath);
        if (!csvFile.exists()) {
            System.out.println("[ModuleHub] CSV file not found: " + csvFilePath);
            return false;
        }

        try {
            System.out.println("[ModuleHub] Reading CSV file: " + csvFilePath);

            ArrayList<ReportManager.FinancialRecord> records = parseCSVFile(csvFilePath);

            if (records.isEmpty()) {
                System.out.println("[ModuleHub] No valid records found in CSV.");
                return false;
            }

            Budget budget = convertRecordsToBudget(records, year);

            storageModule.saveUserData(username, year, budget);

            System.out.println("[ModuleHub] ✓ Successfully uploaded data for year " + year);
            System.out.println("[ModuleHub] ✓ " + records.size() + " transactions stored");

            return true;

        } catch (Exception e) {
            errorHandler.handleModuleError("CSV Upload", e);
            return false;
        }
    }

    /**
     * Parses a CSV file containing financial data into a list of FinancialRecord objects.
     * This helper translates external CSV data into an internal representation understood by the Reports module.
     * @param csvPath the path to the CSV file that should be parsed
     * @return a list of FinancialRecord objects representing valid rows in the CSV
     *
     * @author Denisa Cakoni
     */
    private ArrayList<ReportManager.FinancialRecord> parseCSVFile(String csvPath) {
        ArrayList<ReportManager.FinancialRecord> list = new ArrayList<>();

        try (Scanner sc = new Scanner(new File(csvPath))) {
            if (!sc.hasNextLine()) {
                return list;
            }
            sc.nextLine(); // skip header line

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 3) {
                    System.err.println("[ModuleHub] Skipping invalid line: " + line);
                    continue;
                }

                String date      = parts[0].trim(); // MM/DD/YYYY
                String category  = parts[1].trim();
                String amountStr = parts[2].trim();

                String[] dateParts = date.split("/");
                if (dateParts.length != 3) {
                    System.err.println("[ModuleHub] Invalid date format: " + date);
                    continue;
                }

                int month = Integer.parseInt(dateParts[0]) - 1; // 0-based month index
                int year  = Integer.parseInt(dateParts[2]);

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
            System.err.println("[ModuleHub] Error reading CSV: " + e.getMessage());
        }

        return list;
    }

    /**
     * Converts a list of FinancialRecord objects into a Budget instance.
     * This helper is responsible for data format conversion between the reporting representation and the storage
     * representation.
     *
     * For simplicity, all dates are set to the 15th of each month for the given year.
     *
     * @param records the list of FinancialRecord objects that should be converted
     * @param year    the year that should be applied to the generated dates
     * @return a Budget instance populated with transactions from the records list
     *
     * @author Denisa Cakoni
     */
    private Budget convertRecordsToBudget(
            ArrayList<ReportManager.FinancialRecord> records, int year) {

        Budget budget = new Budget();

        for (ReportManager.FinancialRecord rec : records) {
            String date = String.format("%02d/15/%d", rec.getMonth() + 1, year);

            String category = rec.getCategory();
            double amount = Double.parseDouble(rec.getAmount());

            budget.addTransaction(date, category, amount);
        }

        return budget;
    }


    // Reports view from stored data (no CSV prompting)

    /**
     * Generates and prints a report for a given user and year from previously stored data.
     * This method:
     * loads the Budget for the user and year from StorageManager
     * converts the Budget to a list of FinancialRecord objects
     * delegates summary calculations to ReportManager
     * delegates additional analysis to ReportAnalyzer
     *formats and prints the report sections to the console
     *
     * Report types:
     * yearly:   yearly totals and analysis
     * monthly:  monthly breakdown only
     * category: category breakdown only
     * full:     yearly, monthly, category, and analysis
     *
     * @param username   the user requesting the report
     * @param year       the year to generate the report for
     * @param reportType the type of report to generate ("yearly", "monthly", "category", "full")
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
                System.out.println("┌────────────────────────────────────────────┐");
                System.out.println("│           NO DATA AVAILABLE                │");
                System.out.println("└────────────────────────────────────────────┘");
                System.out.println();
                System.out.println("  No financial data found for year " + year);
                System.out.println("  Please upload a CSV file for this year first.");
                System.out.println();
                System.out.println("  Go to: Main Menu → Upload CSV Data");
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
                    printYearlySection(year, yearly);
                    printAnalysisSection(highestMonth, topSpendingCategory,
                            netBalancePretty, negativeBalanceMonths, true);
                    break;

                case "monthly":
                    printMonthlySection(year, monthly);
                    break;

                case "category":
                    printCategorySection(year, categorySummaries);
                    break;

                case "full":
                default:
                    printFullReport(year, yearly, monthly, categorySummaries);
                    printAnalysisSection(highestMonth, topSpendingCategory,
                            netBalancePretty, negativeBalanceMonths, true);
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
            String date = t.getDate(); // MM/DD/YYYY
            String[] parts = date.split("/");

            if (parts.length != 3) {
                System.err.println("[ModuleHub] Invalid date in transaction: " + date);
                continue;
            }

            int month = Integer.parseInt(parts[0]) - 1; // Convert to 0-based index

            String category = t.getCategory();
            String amount = String.valueOf(t.getAmount());
            boolean isIncome = t.getAmount() > 0;

            ReportManager.FinancialRecord rec = new ReportManager.FinancialRecord(
                    amount, category, month, year, isIncome
            );
            records.add(rec);
        }

        return records;
    }

    /**
     * Prints the yearly summary section of a financial report.
     * This method is responsible only for console formatting and display.
     *
     * @param year   the year being reported
     * @param yearly the YearlySummary object containing totals for the year
     *
     * @author Denisa Cakoni
     */
    private void printYearlySection(int year, ReportManager.YearlySummary yearly) {
        System.out.println("┌───────────────────────────────────────────────┐");
        System.out.println("│                FINANCIAL REPORT               │");
        System.out.println("└───────────────────────────────────────────────┘");
        System.out.println(" Year: " + year);
        System.out.println();
        System.out.println(" ───────────────── YEARLY SUMMARY ─────────────────");
        System.out.printf("   Total Income:    $%s%n", yearly.getTotalIncome());
        System.out.printf("   Total Expenses:  $%s%n", yearly.getTotalExpenses());
        System.out.printf("   Net Balance:     $%s%n", yearly.getNetBalance());
        System.out.println("────────────────────────────────────────────────────\n");
    }

    /**
     * Prints the monthly breakdown section of a financial report.
     * Each line represents income, expenses, and balance for a specific month.
     *
     * @param year    the year being reported
     * @param monthly the list of monthly summary lines generated by ReportManager
     *
     * @author Denisa Cakoni
     */
    private void printMonthlySection(int year, ArrayList<String> monthly) {
        System.out.println("┌───────────────────────────────────────────────┐");
        System.out.println("│              MONTHLY BREAKDOWN                │");
        System.out.println("└───────────────────────────────────────────────┘");
        System.out.println(" Year: " + year);
        System.out.println();
        System.out.println(" ───────────────── MONTHLY SUMMARY ────────────────");
        for (String line : monthly) {
            System.out.println("   • " + line);
        }
        System.out.println("────────────────────────────────────────────────────\n");
    }

    /**
     * Prints the category summary section of a financial report.
     * Each line represents a spending or income category and its total amount.
     *
     * @param year              the year being reported
     * @param categorySummaries the list of category summary lines
     *
     * @author Denisa Cakoni
     */
    private void printCategorySection(int year, ArrayList<String> categorySummaries) {
        System.out.println("┌───────────────────────────────────────────────┐");
        System.out.println("│              CATEGORY SUMMARY                 │");
        System.out.println("└───────────────────────────────────────────────┘");
        System.out.println(" Year: " + year);
        System.out.println();
        System.out.println(" ──────────────── CATEGORY SUMMARY ────────────────");
        for (String line : categorySummaries) {
            System.out.println("   • " + line);
        }
        System.out.println("────────────────────────────────────────────────────\n");
    }

    /**
     * Prints the full financial report including yearly, monthly, and category sections.
     * This is used when the user requests a complete view of their finances for a year.
     *
     * @param year              the year being reported
     * @param yearly            the yearly summary object
     * @param monthly           the monthly summary lines
     * @param categorySummaries the category summary lines
     *
     * @author Denisa Cakoni
     */
    private void printFullReport(int year,
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
     * Prints the additional analysis section of the report:
     * highest spending month
     * top spending category
     * overall net balance with formatted currency using ReportFormatter
     * months with a negative balance, if any
     *
     * @param highestMonth           the month with the highest total expenses
     * @param topSpendingCategory    the category with the highest total expenses
     * @param netBalancePretty       the formatted net balance string
     * @param negativeBalanceMonths  a list of months where the balance was negative
     * @param includeBanner          true to print a header and footer, false to skip them
     *
     * @author Denisa Cakoni
     */
    private void printAnalysisSection(String highestMonth,
                                      String topSpendingCategory,
                                      String netBalancePretty,
                                      ArrayList<String> negativeBalanceMonths,
                                      boolean includeBanner) {

        if (includeBanner) {
            String analysisBanner = reportFormatter.printHeaderFooter(
                    "Additional Analysis",
                    "End of analysis"
            );
            System.out.println(analysisBanner);
        }

        System.out.println("Highest spending month: " + highestMonth);
        System.out.println("Top spending category: " + topSpendingCategory);
        System.out.println("Overall net balance: " + netBalancePretty);

        if (negativeBalanceMonths == null || negativeBalanceMonths.isEmpty()) {
            System.out.println("No months with negative balance.");
        } else {
            System.out.println("Months with negative balance:");
            for (String month : negativeBalanceMonths) {
                System.out.println(" - " + month);
            }
        }
    }


    // Predictions (runs from stored data)


    /**
     * Runs a prediction scenario on previously uploaded and stored data:
     * loads the Budget for a user and year from StorageManager
     * converts the Budget into a temporary CSV file for the Prediction module
     * uses DataReader to read the CSV
     * runs a specified prediction scenario
     *
     * supported scenario types:
     * summary: prints a summary report using Prediction DataReader
     * compare-demo: creates two scenarios and compares them
     * deficit: runs the DeficitSolver to analyze overspending
     * surplus: runs the SurplusOptimizer to analyze surplus usage
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
                System.out.println("┌────────────────────────────────────────────┐");
                System.out.println("│           NO DATA AVAILABLE                │");
                System.out.println("└────────────────────────────────────────────┘");
                System.out.println();
                System.out.println("  No financial data found for year " + year);
                System.out.println("  Please upload a CSV file for this year first.");
                System.out.println();
                return "[ModuleHub] No data available for year " + year;
            }

            String tempCsvPath = createTemporaryCSVForPrediction(budget, username, year);

            predictionData.readData(tempCsvPath);

            switch (scenarioType.toLowerCase()) {

                case "summary": {
                    String summary = predictionData.createSummaryReport();
                    System.out.println("\n--- Prediction Summary ---");
                    System.out.println(summary);
                    return "Prediction summary generated.";
                }

                case "compare-demo": {
                    predictionModule.createScenario("BaseScenario");
                    predictionModule.createScenario("AdjustedScenario");

                    predictionModule.applyExpenseChange(
                            "AdjustedScenario", "Entertainment", 50.0);

                    predictionModule.compareScenarios(
                            "BaseScenario", "AdjustedScenario");

                    return "Scenario comparison complete.";
                }

                case "deficit": {
                    DeficitSolver ds = new DeficitSolver(predictionData);
                    System.out.println("\n--- Deficit Prediction ---");
                    System.out.println(ds.generateSummary());
                    return "Deficit prediction generated.";
                }

                case "surplus": {
                    SurplusOptimizer so = new SurplusOptimizer(predictionData);
                    System.out.println("\n--- Surplus Prediction ---");
                    System.out.println(so.surplusTracker());
                    System.out.println(so.surplusSuggestion());
                    return "Surplus prediction generated.";
                }

                default:
                    return "[ModuleHub] Unknown prediction scenarioType: " + scenarioType;
            }

        } catch (Exception e) {
            errorHandler.handleModuleError("Prediction", e);
            return "[ModuleHub] Failed to run prediction.";
        }
    }

    /**
     * Creates a temporary CSV file that mirrors the Budget data so that the
     * Prediction module can read it with its DataReader.
     * amounts are cast to integers to match the Prediction team's expectations.
     *
     * @param budget   the budget whose transactions should be exported
     * @param username the username (used for naming the temporary file)
     * @param year     the year (also used for naming the file)
     * @return the path to the newly created temporary CSV file
     *
     * @author Denisa Cakoni
     */
    private String createTemporaryCSVForPrediction(Budget budget, String username, int year) {
        String tempFileName = "temp_prediction_" + username + "_" + year + ".csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFileName))) {
            writer.println("Date,Category,Amount");

            for (Transaction t : budget.getAllTransactions()) {
                writer.println(
                        t.getDate() + "," +
                                t.getCategory() + "," +
                                (int) t.getAmount()
                );
            }

        } catch (Exception e) {
            System.err.println("[ModuleHub] Error creating temp CSV: " + e.getMessage());
        }

        return tempFileName;
    }


    // Validation

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


    // Accounts


    /**
     * Routes account action to the Accounts module.
     * Supported actions:
     * logout: signs out the currently logged-in user
     * deleteaccount: deletes the specified user account
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
     * Registers a new user account using the Accounts module.
     * This method forwards all data and reports any failure back to the console.
     *
     * @param username       the desired username
     * @param password       the chosen password
     * @param secretQuestion the selected secret question for account recovery
     * @param secretAnswer   the answer to the secret question
     * @return true if registration succeeds, false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean registerUser(String username,
                                String password,
                                String secretQuestion,
                                String secretAnswer) {
        try {
            boolean ok = accountsModule.registerAccount(
                    username, password, secretQuestion, secretAnswer);
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
     * This operation may require a matching signed-in session depending on the
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
     * Any failure is reported through the console.
     *
     * @param username    the username whose password should be reset
     * @param newPassword the new password to set
     * @return true if the password reset succeeds, false otherwise
     *
     * @author Denisa Cakoni
     */
    public boolean resetUserPassword(String username, String newPassword) {
        try {
            boolean ok = accountsModule.resetPassword(username, newPassword);
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
 * Logs errors, displays user-friendly error messages, and recovers from errors gracefully
 * to prevent application crashes.
 *
 * @author Kapil Tamang
 */
class ErrorHandler {

    public ErrorHandler() {}

    public void handleModuleError(String moduleName, Exception error) {
        displayError("An error has occured in the " + moduleName + " module.");
        logError("Error in " + moduleName, error);
        recoverToMenu();
    }

    public void recoverToMenu() {
        System.out.println("Restoring main menu.");
    }

    public void logError(String errorMessage, Exception error) {
        System.err.println("ERROR");
        System.err.println("Time: " + java.time.LocalDateTime.now());
        System.err.println("Message: " + errorMessage);
        System.err.println("Exception Type: " + error.getClass().getName());
        System.err.println("Stack Trace:");
        error.printStackTrace(System.err);
    }

    public void displayError(String message) {
        System.out.println("ERROR");
        System.out.println(message);
        System.out.println("Please try again.");
    }
}