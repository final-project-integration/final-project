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
    private double surplus;
    private double totalIncome;
    private double totalExpenses;
    private double savings;

    // Logging for debugging / testing (not user-facing UI)
    private final StringBuilder surplusLog = new StringBuilder();

    // Aggregated by category (positive amounts)
    private final HashMap<String, Double> income = new HashMap<>();
    private final HashMap<String, Double> expenses = new HashMap<>();

    // Categories temporarily excluded from tracking
    private final HashMap<String, Double> blacklist = new HashMap<>();

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
        this.surplus = 0.0;
        this.totalIncome = 0.0;
        this.totalExpenses = 0.0;
        this.savings = 0.0;

        dataRetriever();  // auto-populate income/expenses maps
        currentValues();  // compute totals from baseData
    }

    /**
     * Parameterized constructor for testing or advanced scenarios.
     *
     * @param surplus initial surplus amount
     * @param totalIncome initial total income
     * @param totalExpenses initial total expenses
     * @param savings initial savings
     * @param reader the DataReader object containing base data
     * @author Daniel Moore
     */
    public SurplusOptimizer(double surplus, double totalIncome, double totalExpenses, double savings, DataReader reader) {

        this(reader);  // reuse main constructor to populate maps
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
            surplusLog.append("Error: No base data loaded for SurplusOptimizer.\n");
            return false;
        }

        income.clear();
        expenses.clear();

        List<String> allCategories = baseData.getCategories();
        List<Integer> allAmounts = baseData.getAmounts();

        for (int i = 0; i < allCategories.size(); i++) {
            String category = allCategories.get(i);
            int amount = allAmounts.get(i);

            if (DataReader.isIncomeCategory(category)) {
                // incomes are positive in DataReader
                income.merge(category, (double) amount, Double::sum);
            } else if (DataReader.isExpenseCategory(category)) {
                // DataReader stores expenses as negative; keep them positive here
                expenses.merge(category, Math.abs((double) amount), Double::sum);
            }

            surplusLog.append(String.format("Category: %s, Amount: $%d%n", category, amount));
        }
        return true;
    }


    /**
     * Updates the current total income and expenses based on the DataReader.
     * Uses DataReader's totals as the single source of truth.
     *
     * @author Daniel Moore
     */
    public void currentValues() {
        this.totalIncome = baseData.getTotalIncome();       // > 0
        int negativeExpenses = baseData.getTotalExpenses(); // < 0
        this.totalExpenses = -negativeExpenses;             // store as positive

        surplusLog.append(String.format("Total income: $%.2f%n", this.totalIncome));
        surplusLog.append(String.format("Total expenses: $%.2f%n", this.totalExpenses));
    }

    /**
     * Calculates the surplus available after expenses.
     *
     * @return the surplus amount (0 if there is no surplus or a deficit)
     * @author Daniel Moore
     */
    public double surplusCreator() {
        currentValues();
        this.surplus = this.totalIncome - this.totalExpenses;

        if (this.surplus <= 0) {
            surplusLog.append("No surplus available after expenses.\n");
            return 0.0;
        }

        surplusLog.append(String.format("Surplus available: $%.2f%n", this.surplus));
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
    public Map<String, Double> getProportionalPlan() {
        surplusCreator();  // ensure surplus and totals are up to date

        Map<String, Double> plan = new HashMap<>();
        if (this.surplus <= 0 || this.totalExpenses <= 0) {
            return plan; // empty
        }

        for (Map.Entry<String, Double> entry : expenses.entrySet()) {
            String category = entry.getKey();
            double categoryAmount = entry.getValue();      // positive
            double proportion = categoryAmount / this.totalExpenses; // 0 - 1
            double increase = proportion * this.surplus;   // dollars

            plan.put(category, increase);
        }

        return plan;
    }



    /**
     * Suggests an expense category to reduce based on the lowest expense amount.
     * @author Daniel Moore
     */
    /**
     * Suggests an expense category to reduce based on the largest expense amount.
     *
     * @return a suggestion message, or a message indicating no expenses are available
     * @author Daniel Moore
     */
    public String surplusSuggestion() {
        if (expenses.isEmpty()) {
            String msg = "No expenses available to analyze.";
            surplusLog.append(msg).append('\n');
            return msg;
        }

        String maxKey = null;
        double maxValue = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Double> entry : expenses.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
        }

        String msg = String.format(
                "Consider reducing expenses in category: %s. Current amount: $%.2f",
                maxKey, maxValue);

        surplusLog.append(msg).append('\n');
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

        String key = findCategory(expenses, category);
        if (key == null) {
            return false;
        }

        if (key.equalsIgnoreCase("Rent")) {
            // Policy: do not reduce Rent
            surplusLog.append("Attempted to decrease Rent; operation skipped.\n");
            return false;
        }

        double currentExpense = expenses.get(key);
        double reduction = currentExpense * (percent / 100.0);
        double newExpense = currentExpense - reduction;

        expenses.put(key, newExpense);
        // Decreasing expenses increases available surplus
        this.surplus += reduction;

        surplusLog.append(String.format(
                "Decreased expense in category: %s by $%.2f (from $%.2f to $%.2f)%n",
                key, reduction, currentExpense, newExpense));

        // Do NOT call currentValues(), which re-pulls data from DataReader
        return true;
    }

    
        /**
     * Checks if spending a given amount from the surplus on a specified expense category
     * would still keep the budget at or above break-even.
     *
     * This method does NOT modify expenses or surplus; it only logs and returns a boolean.
     *
     * @param category the expense category to simulate spending on
     * @param amount   the amount to simulate spending
     * @return true if spending is possible without going below break-even; false otherwise
     * @author Daniel Moore
     */
    public boolean simulateSpending(String category, double amount) {
        if (amount <= 0) {
            surplusLog.append("simulateSpending called with non-positive amount.\n");
            return true;
        }

        double currentSurplus = surplusCreator(); // recompute from DataReader

        if (currentSurplus <= 0) {
            surplusLog.append("No surplus available to spend.\n");
            return false;
        }

        if (amount > currentSurplus) {
            surplusLog.append(String.format(
                    "Not enough surplus to spend $%.2f on %s.%n", amount, category));
            return false;
        }

        surplusLog.append(String.format(
                "Spending $%.2f on %s is possible and keeps you at or above break-even.%n",
                amount, category));
        return true;
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
    public boolean spendSurplus(String category, double amount) {
        if (amount <= 0) {
            return false;
        }

        String key = findCategory(expenses, category);
        if (key == null) {
            surplusLog.append(String.format(
                    "Category %s not found in expenses.%n", category));
            return false;
        }

        double currentSurplus = surplusCreator();
        if (currentSurplus < amount) {
            surplusLog.append(String.format(
                    "Not enough surplus to spend $%.2f on %s.%n", amount, category));
            return false;
        }

        double currentExpense = expenses.get(key);
        double newExpense = currentExpense + amount; // spend more → expense goes up

        expenses.put(key, newExpense);
        this.surplus = currentSurplus - amount;

        surplusLog.append(String.format(
                "Spent $%.2f on category %s, new expense: $%.2f, new surplus: $%.2f%n",
                amount, key, newExpense, this.surplus));

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
    private String findCategory(Map<String, Double> map, String category) {
        for (String key : map.keySet()) {
            if (key.equalsIgnoreCase(category)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Removes a category from tracking by adding it to the blacklist.
     *
     * @param category the category to blacklist
     * @author Daniel Moore
     */
    public void categoryBlacklist(String category) {
        String key = findCategory(expenses, category);
        if (key != null) {
            blacklist.put(key, expenses.remove(key));
            surplusLog.append("Category ").append(category)
                      .append(" has been removed from tracking (expense).")
                      .append('\n');
            return;
        }

        key = findCategory(income, category);
        if (key != null) {
            blacklist.put(key, income.remove(key));
            surplusLog.append("Category ").append(category)
                      .append(" has been removed from tracking (income).")
                      .append('\n');
            return;
        }

        surplusLog.append("Category ").append(category)
                  .append(" not found for blacklisting.\n");
    }

    /**
     * Adds a category back to tracking from the blacklist.
     *
     * @param category the category to whitelist
     * @author Daniel Moore
     */
    public void categoryWhitelist(String category) {
        String key = findCategory(blacklist, category);
        if (key != null) {
            Double amount = blacklist.remove(key);
            if (DataReader.isIncomeCategory(key)) {
                income.put(key, amount);
            } else if (DataReader.isExpenseCategory(key)) {
                expenses.put(key, amount);
            }
            surplusLog.append("Category ").append(category)
                      .append(" has been added back to tracking.\n");
            return;
        }
        surplusLog.append("Category ").append(category)
                  .append(" not found in blacklist.\n");
    }

    /**
     * Displays all categories along with their total amounts for both income and expenses.
     * Intended primarily for debugging from a driver or test, not from the UI layer.
     *
     * @author Daniel Moore
     */
    public void allCategories() {
        System.out.printf("%-21s | %s%n", "Category", "Total");
        System.out.println("--------------------------------------");
        for (String key : expenses.keySet()) {
            double total = expenses.get(key);
            System.out.printf("%-21s | $%.2f%n", key, total);
        }
        System.out.print("\n");
        System.out.println("Total income by category:");
        for (String key : income.keySet()) {
            double total = income.get(key);
            System.out.printf("%-21s | $%.2f%n", key, total);
        }
    }

    /**
     * Records savings from a specified income category.
     *
     * @param category the income category to treat as savings
     * @author Daniel Moore
     */
    public void addSaving(String category) {
        String key = findCategory(income, category);
        if (key != null) {
            savings = income.get(key);
            surplusLog.append(String.format(
                    "Current savings in %s: $%.2f%n", key, savings));
        } else {
            surplusLog.append("addSaving: category not found: ")
                      .append(category).append('\n');
        }
    }

    /**
     * Returns the total income.
     *
     * @return total income value
     * @author Daniel Moore
     */
    public double getTotalIncomeValue() {
        return totalIncome;
    }

    /**
     * Returns the total expenses (positive number).
     *
     * @return total expenses value
     * @author Daniel Moore
     */
    public double getTotalExpensesValue() {
        return totalExpenses;
    }

    /**
     * Returns the current surplus.
     *
     * @return surplus value (may be 0 if no surplus)
     * @author Daniel Moore
     */
    public double getSurplusValue() {
        return surplus;
    }

    public String generateWhatIfSummary(String category) {
    double possible = surplusCreator();
    return "You can spend $" + possible + " in " + category + " without going into deficit.";
}

}
