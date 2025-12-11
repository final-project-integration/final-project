
//Prediction Team Module

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SurplusOptimizer class for the Prediction Team module.
 * Declares methods used to examine surplus budgets and determine how to manage
 * surplus funds.
 *
 * @author Daniel Moore
 */
public class SurplusOptimizer {

    // Core totals (derived from DataReader)
    private int surplus;
    private int totalIncome;
    private int totalExpenses;
    private int savings;

    // Logging for debugging / testing (not user-facing UI)
    private final StringBuilder surplusLog = new StringBuilder();
    private boolean logging = true;

    // Aggregated by category (positive amounts)
    private final HashMap<String, Integer> income = new HashMap<>();
    private final HashMap<String, Integer> expenses = new HashMap<>();

    // Categories temporarily excluded from tracking
    private final HashMap<String, Integer> blacklist = new HashMap<>();

    // Source of truth for budget data
    private final DataReader baseData;

    /**
     * Main constructor for the SurplusOptimizer class.
     * Initializes the object using a DataReader with loaded budget data.
     *
     * @param reader the DataReader instance providing base data
     * @author Daniel Moore
     */
    public SurplusOptimizer(DataReader reader) {
        this.baseData = reader;
        this.surplus = 0;
        this.totalIncome = 0;
        this.totalExpenses = 0;
        this.savings = 0;

        dataRetriever(); // auto-populate income/expenses maps
        currentTotals(); // compute totals from baseData
    }

    /**
     * Parameterized constructor for testing or advanced scenarios.
     *
     * @param surplus       initial surplus amount
     * @param totalIncome   initial total income
     * @param totalExpenses initial total expenses
     * @param savings       initial savings
     * @param reader        the DataReader object containing base data
     * @author Daniel Moore
     */
    public SurplusOptimizer(int surplus, int totalIncome, int totalExpenses, int savings,
            DataReader reader) {

        this(reader); // reuse main constructor to populate maps
        this.surplus = surplus;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.savings = savings;
    }

    /**
     * Retrieves data from the DataReader and populates income and expense maps.
     *
     * @return true if data retrieval is successful; false otherwise
     * @author Daniel Moore
     */
    public boolean dataRetriever() {
        if (baseData == null) {
            log("Error: No base data loaded for SurplusOptimizer.\n");
            return false;
        }

        income.clear();
        expenses.clear();
        // Clears both income and expenses maps to prepare for fresh data

        List<String> allCategories = baseData.getCategories();
        List<Integer> allAmounts = baseData.getAmounts();
        // assigns values from DataReader to local variables

        for (int i = 0; i < allCategories.size(); i++) {
            String category = allCategories.get(i);
            int amount = allAmounts.get(i);

            if (DataReader.isIncomeCategory(category)) {
                // incomes are positive in DataReader
                income.merge(category, amount, Integer::sum);
            } else if (DataReader.isExpenseCategory(category)) {
                // DataReader stores expenses as negative; keep them positive here
                expenses.merge(category, Math.abs(amount), Integer::sum);
            }

            log(String.format("Category: %s - Amount: $%d", category, amount));
        }
        return true;
    }

    /**
     * Updates the current total income and expenses based on the DataReader.
     * Uses DataReader's totals as the single source of truth.
     *
     * @author Daniel Moore
     */
    public void currentTotals() { // 12/1/2025 Renamed from currentValues() to currentTotals(), separated from
                                  // DisplayCurrentTotals()
        this.totalIncome = baseData.getTotalIncome(); // > 0
        int negativeExpenses = baseData.getTotalExpenses(); // < 0
        this.totalExpenses = Math.abs(negativeExpenses); // store as positive
        log(String.format("Current totals updated: Income = $%d, Expenses = $%d%n", this.totalIncome,
                this.totalExpenses));
    }

    /**
     * Displays the current total income and expenses.
     * @since New Code 12/1/2025 to display current totals, made to separate from
     * CurrentTotals() which just calculates.
     * Done to avoid flooding logs when pulling from DataReader.
     * @author Daniel Moore
     */

    public void displayCurrentTotals() {
        currentTotals();
        log(String.format("Total income: $%d%n", this.totalIncome));
        log(String.format("Total expenses: $%d%n", this.totalExpenses));
    }

    /**
     * Calculates the surplus available after expenses.
     *
     * @return the surplus amount (0 if there is no surplus or a deficit)
     * @author Daniel Moore
     */
    public int surplusCreator() {
        currentTotals();
        this.surplus = this.totalIncome - this.totalExpenses;

        if (this.surplus <= 0) {
            log("No surplus available after expenses.\n");
            return 0;
        }

        log(String.format("Surplus available: $%d%n", this.surplus));
        return this.surplus;
    }

    /**
     * Builds a proportional allocation plan for a surplus.
     * Each expense category gets a share of the surplus based on
     * its fraction of total expenses.
     *
     * @return a map from category name to suggested increase amount;
     *         empty map if there is no surplus
     * @author Daniel Moore
     */
    public Map<String, Integer> surplusAllocationPlan() {
        surplusCreator(); // ensure surplus and totals are up to date

        Map<String, Integer> plan = new HashMap<>();
        if (this.surplus <= 0 || this.totalExpenses <= 0) {
            return plan; // empty
        }

        for (Map.Entry<String, Integer> entry : expenses.entrySet()) {
            String category = entry.getKey();
            int categoryAmount = entry.getValue(); // positive
            double proportion = (double) categoryAmount / this.totalExpenses; // 0 - 1
            int increase = (int) Math.round(proportion * this.surplus); // dollars

            plan.put(category, increase);
        }

        int totalCheck = plan.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        // Check sum to ensure it matches surplus

        int difference = this.surplus - totalCheck; // adjust for rounding errors

        if (difference != 0) { // adjust largest category to fix rounding
            String maxKey = expenses.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();

            plan.put(maxKey, plan.get(maxKey) + difference);
        }

        // Logs allocation plan, returns a formatted table.
        int longestTitle = plan.keySet().stream() // finds longest title for formatting
                .mapToInt(String::length)
                .max()
                .orElse(10);
        int colWidth = Math.max(20, longestTitle + 2); // minimum width of 20 for aesthetics, could be adjusted
        String rowFormat = "%-" + colWidth + "s | $%8d %n"; // dynamic formatting based on title length

        System.out.println("Here is a category-by-category Annual Surplus Allocation Plan: ");
        System.out.println(
                "This plan shows categories receiving increases in proportion to their share of the total expenses. \n");
        System.out.println(String.format("%-" + colWidth + "s | %s", "| Category", "Suggested Increase"));
        System.out.println("-".repeat(colWidth + 22));
        log("Compact Surplus Allocation Plan View:");

        StringBuilder line = new StringBuilder(); // for debug logging
        int count = 0;

        for (Map.Entry<String, Integer> entry : plan.entrySet()) { // for debug logging
            line.append(String.format("%s: %d, ", entry.getKey(), entry.getValue()));
            count++;

            if (count % 3 == 0) { // break line every 3 entries
                log(line.toString());
                line.setLength(0); // reset StringBuilder
            }
        }

        if (line.length() > 0) { // log any remaining entries
            log(line.toString());
        }
        log("");

        for (Map.Entry<String, Integer> entry : plan.entrySet()) {
            String label = capitalize(entry.getKey());
            System.out.printf(rowFormat, label, entry.getValue());
        }

        System.out.println();
        return plan;
    }

    /**
     * Suggests an expense category to reduce based on the largest expense amount.
     *
     * @return a suggestion message, or a message indicating no expenses are
     *         available
     * @author Daniel Moore
     */
    public String surplusSuggestion() {
        if (expenses.isEmpty()) {
            String msg = "No expenses available to analyze.";
            log(msg + "\n");
            return msg;
        }
        // If the expenses hashmap is empty, it returns a message indicating no expenses

        String maxKey = null;
        // Used for finding the max expense category

        int maxValue = Integer.MIN_VALUE;
        // Used for tracking the max expense value

        for (Map.Entry<String, Integer> entry : expenses.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
        }
        // Finds the max expense category and value

        String msg = String.format(
                "Consider reducing expenses in category: %s. Current amount: $%d",
                maxKey, maxValue);

        log(msg + "\n");
        return msg;
    }

    /**
     * Decreases the expense in the specified category by a given percentage,
     * except for the "Rent" category which cannot be decreased.
     *
     * @param category the expense category to decrease
     * @param percent  the percentage to decrease the expense by (e.g., 10 = 10%)
     * @return true if the expense was decreased; false otherwise
     * @author Daniel Moore
     */
    public boolean decreaseExpense(String category, double percent) {
        if (percent <= 0) {
            return false;
        }
        // Validates that the percentage to decrease is positive

        String key = findCategory(category);
        if (key == null) {
            return false;
        }
        // Validates that the category is not null before decreasing

        if (key.equalsIgnoreCase("Rent")) {
            // Policy: do not reduce Rent
            log("Attempted to decrease Rent; operation skipped.\n");
            return false;
        }

        int currentExpense = expenses.get(key);
        int reduction = (int) Math.round(currentExpense * (percent / 100.0));
        int newExpense = currentExpense - reduction;

        expenses.put(key, newExpense);
        // Decreasing expenses increases available surplus

        this.surplus += reduction;
        // increase surplus by reduction amount

        log(String.format(
                "Decreased expense in category: %s by $%d (from $%d to $%d)%n",
                key, reduction, currentExpense, newExpense));

        // Do NOT call currentValues(), which re-pulls data from DataReader
        return true;
    }

    /**
     * Checks if spending a given amount from the surplus on a specified expense
     * category
     * would still keep the budget at or above break-even.
     *
     * This method does NOT modify expenses or surplus; it only logs and returns a
     * boolean.
     *
     * @param category the expense category to simulate spending on
     * @param amount   the amount to simulate spending
     * @return true if spending is possible without going below break-even; false
     *         otherwise
     * @author Daniel Moore
     */
    public boolean simulateSpending(String category, int amount) {
        if (amount <= 0) {
            log("simulateSpending called with non-positive amount.\n");
            return true;
        }
        // Checks if the amount to spend is less than or equal to zero and returns early
        // Logs reason for not spending if amount is non-positive

        int currentSurplus = surplusCreator(); // recompute from DataReader

        if (currentSurplus <= 0) {
            log("No surplus available to spend.\n");
            return false;
        }
        // If there is no surplus, it logs that no surplus is available and returns
        // false.

        if (amount > currentSurplus) {
            log(String.format(
                    "Not enough surplus to spend $%d on %s.%n", amount, category));
            return false;
        }
        // If the amount to spend exceeds the current surplus, it logs that there is not
        // enough surplus to cover the spending.

        log(String.format(
                "Spending $%d on %s is possible and keeps you at or above break-even.%n",
                amount, category));
        return true;
        // If spending is possible without going below break-even, it logs that the
        // spending is possible and returns true.
    }

    /**
     * Spends a specified amount from the surplus on a given expense category.
     * Updates the expense amount and surplus accordingly.
     *
     * @param category the expense category to spend on
     * @param amount   the amount to spend
     * @return true if the spend was applied; false otherwise
     * @author Daniel Moore
     */
    public boolean spendSurplus(String category, int amount) {
        if (amount <= 0) {
            return false;
        }
        // Checks if the amount to spend is less than or equal to zero and returns false

        String key = findCategory(category);
        if (key == null) {
            log(String.format(
                    "Category %s not found in expenses.%n", category));
            return false;
        }
        // If the category is not found in expenses, it logs that the category was not
        // found and returns false.

        int currentSurplus = surplusCreator();
        if (currentSurplus < amount) {
            log(String.format(
                    "Not enough surplus to spend $%d on %s.%n", amount, category));
            return false;
        }
        // If there is not enough surplus to cover the spending, it logs that there is
        // not enough surplus and returns false.

        int currentExpense = expenses.get(key);
        int newExpense = currentExpense + amount; // spend more → expense goes up

        expenses.put(key, newExpense);
        this.surplus = currentSurplus - amount; // reduce surplus by amount spent

        int monthly = this.surplus / 12; // calculate monthly surplus

        log(String.format(
                "Spent $%d on category %s, new expense: $%d, Your new Annual surplus: $%d%n, Your new Monthly surplus: $%d%n",
                amount, key, newExpense, this.surplus, monthly));

        return true;
    }

    /**
     * Finds a category in the given map, ignoring case sensitivity.
     *
     * @param map      the map to search in
     * @param category the category to find
     * @return the matched category key if found; null otherwise
     * @author Daniel Moore
     */
    private String findCategory(String category) {
        for (String key : expenses.keySet()) {
            if (key.equalsIgnoreCase(category))
                return key;
        }
        for (String key : income.keySet()) {
            if (key.equalsIgnoreCase(category))
                return key;
        }
        return null;
        // Searches through the keys of the provided map to find a match for the given
        // category,
        // ignoring case sensitivity. If a match is found, it returns the matched key.
    }

    /**
     * Retrieves the amount for a specific category from the given map.
     *
     * @param map      the map to search in
     * @param category the category to get the amount for
     * @return the amount if found; 0.0 otherwise
     * @author Daniel Moore
     */
    private int getCategoryAmount(String category) {
        String key = findCategory(category);
        if (key != null) {
            if (expenses.containsKey(key)) {
                log(String.format("Category %s found in expenses.%n", category));
                return expenses.get(key);
            }
            if (income.containsKey(key)) {
                log(String.format("Category %s found in income.%n", category));
                return income.get(key);
            }
        }
        log(String.format("Category %s not found in income or expenses.%n", category));
        System.out.println(String.format("Category %s not found in income or expenses.%n", category));
        return 0;
    }

    /**
     * Removes a category from tracking by adding it to the blacklist.
     *
     * @param category the category to blacklist
     * @author Daniel Moore
     */
    public void categoryBlacklist(String category) {
        String key = expenses.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(category))
                .findFirst().orElse(null); // find key ignoring case

        if (key != null) {
            blacklist.put(key, expenses.remove(key));
            log("Category " + category + " has been removed from tracking (expense).\n");
            return;
        }
        // If key is found in expenses, it is removed from it and added to blacklist.
        // and log that the category was removed from tracking.
        // If not, check income categories.

        key = income.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(category))
                .findFirst().orElse(null); // find key ignoring case

        if (key != null) {
            blacklist.put(key, income.remove(key));
            log("Category " + category + " has been removed from tracking (income).\n");
            return;
        }
        // Works the same way for income categories.

        log("Category " + category + " not found for blacklisting.\n");
        // If both categories are not found, it logs that the category was not found for
        // blacklisting
    }

    /**
     * Adds a category back to tracking from the blacklist.
     *
     * @param category the category to whitelist
     * @author Daniel Moore
     */
    public void categoryWhitelist(String category) {
        String key = blacklist.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(category))
                .findFirst().orElse(null); // find key ignoring case
        if (key != null) {
            int amount = blacklist.remove(key);
            // If the income/expense category in the blacklist exists, it is removed from it
            // the value is stored in amount

            if (DataReader.isIncomeCategory(key)) {
                income.put(key, amount);
            } else if (DataReader.isExpenseCategory(key)) {
                expenses.put(key, amount);
            }
            // If the category is found in DataReader, it and it's respective value is added
            // back to the appropriate map (income/expenses)

            log("Category " + category + " has been added back to tracking.\n");
            return;
        }
        log("Category " + category + " not found in blacklist.\n");
    }

    /**
     * Displays all categories along with their total amounts for both income and
     * expenses.
     * Intended primarily for debugging from a driver or test, not from the UI
     * layer.
     * @return sb.toString() a formatted string containing all categories and their amounts
     * @author Daniel Moore
     */
    public String printAllCategories() { 
                                       
        String header = "Income";
        int longestTitle = income.keySet().stream() // obtain the longest title for formatting
                .mapToInt(String::length) //
                .max() // max of element
                .orElse(10); // default width if no categories
        int totalWidth = longestTitle + 13;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-" + longestTitle + "s | %s%n", header, "Amount"));
        sb.append("-".repeat(totalWidth)).append("\n");
        // Displayed before printing categories and values

        for (String key : income.keySet()) {
            int total = income.get(key);
            sb.append(String.format("%-" + longestTitle + "s | $%8d%n", key, total));
            // Format for displaying all category names and their values
        }
        // Loop prints out the names of the expense categories and their values, and
        // formats accordingly
        // Expense categories follow

        sb.append("\n");

        header = "Expenses";
        longestTitle = expenses.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(10);
        totalWidth = longestTitle + 13;

        sb.append(String.format("%-" + longestTitle + "s | %s%n", header, "Amount"));
        sb.append("-".repeat(totalWidth)).append("\n");

        for (String key : expenses.keySet()) {
            int total = expenses.get(key);
            sb.append(String.format("%-" + longestTitle + "s | $%8d%n", key, total));
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Records savings from a specified income category.
     *
     * @param category the income category to treat as savings
     * @author Daniel Moore
     */
    public void addSavings(String category) {
        String key = findCategory(category);
        if (key != null) {
            this.savings += income.get(key);
        } else {
            System.out.println(String.format("Category %s not found in income.%n", category));
        }
        // Finds the specified income category and records its amount as savings.
        // If category not found, logs an appropriate message.
    }

    /**
     * Removes savings from a specified income category.
     * 
     * @param category the income category to remove from savings
     * @author Daniel Moore
     */
    public void removeSavings(String category) {
        String key = findCategory(category);
        if (key != null) {
            this.savings -= income.get(key);
        } else {
            System.out.println(String.format("Category %s not found in income.%n", category));
        }
        // Finds the specified income category and removes its amount from savings.
        // If category not found, logs an appropriate message.
    }

    /**
     * Prints the surplus log.
     * @author Daniel Moore
     */
    public void printSurplusLog() {
        if (!logging) {
            System.out.println("Logging is disabled.");
            return;
        }
        System.out.println("=== Surplus Log === \n");
        System.out.print(surplusLog.toString());
        System.out.println("===================");
    }

    /**
     * Returns the total income.
     *
     * @return total income value
     * @author Daniel Moore
     */
    public int getTotalIncomeValue() {
        return totalIncome; // return total income
    }

    /**
     * Returns the total expenses (positive number).
     *
     * @return total expenses value
     * @author Daniel Moore
     */
    public int getTotalExpensesValue() {
        return totalExpenses; // return total expenses
    }

    /**
     * Returns the current surplus.
     *
     * @return surplus value (may be 0 if no surplus)
     * @author Daniel Moore
     */
    public int getSurplusValue() {
        surplusCreator(); // ensure totals are up to date
        return this.surplus; // return surplus
    }

    /**
     * Prints the annual and monthly surplus.
     * @since  New Code 12/8/2025 to print annual and monthly surplus
     * @return the surplus message with annual and monthly surplus
     * @author Daniel Moore
     */
    public String printSurplus() {
        int annual = getSurplusValue();
        int monthly = annual / 12; // calculate monthly surplus
        return ("You have a Surplus of $" + annual + " for the year which is $"
                + monthly + " monthly.");
    }

    /**
     * Displays the current amount of savings.
     * 
     * @author Daniel Moore
     * @return the current savings
     */
    public int getSavings() {
        return this.savings; // return current savings
    }

    /**
     * Generates a summary of what-if spending analysis for a given category.
     *
     * @param category the expense category to analyze
     * @return a summary message indicating spending possibilities
     * @author Daniel Moore
     */
    public String generateWhatIfSummary(String category) {
        int possible = surplusCreator();
        if (simulateSpending(category, possible) == false) {
            return "Spending in " + category + " is not possible without going into deficit."; //
        }
        return "You can spend a maximum of $" + possible + " in " + category + " without going into deficit.";
    }

    /**
     * Enables or disables logging.
     *
     * @param change true to enable logging; false to disable
     * @author Daniel Moore
     */
    public void setLogging(boolean change) {
        this.logging = change;
    }

    /**
     * Capitalizes the first letter of a string.
     *
     * @param str the string to capitalize
     * @return the capitalized string
     * @author Daniel Moore
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Internal logging method to record messages with caller context.
     *
     * @param message the message to log
     * @return the SurplusOptimizer instance (for method chaining)
     * @author Daniel Moore
     */
    private SurplusOptimizer log(String message) {
        if (!logging || message == null)
            return this;

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String caller = "UnknownMethod";
        if (stack.length > 2)
            caller = stack[2].getMethodName();

        String logEntry = String.format("[%s] %s", caller, message);
        surplusLog.append(logEntry).append("\n"); // append to StringBuilder

        return this;
    }
}


