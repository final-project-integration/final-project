// Prediction Team Module

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeficitSolver class for the Prediction Team module.
 * Analyzes a financial deficit and determines proportional
 * adjustments to eliminate or reduce a budget shortfall.
 *
 * Stores adjustable expense categories only (non-negotiable
 * categories excluded), calculates proportional reductions,
 * detects impossible deficits, and generates detailed summaries.
 *
 * @author Sahadat Amzad
 */
public class DeficitSolver {

    /** Total annual income. */
    private final double income;

    /** Total deficit (positive value) or 0 if none. */
    private final double overallDeficit;

    /** Names of adjustable categories. */
    private final ArrayList<String> categories;

    /** Expense amount for each adjustable category. */
    private final ArrayList<Double> expenses;

    /** Last applied adjustment list (for undo operations). */
    /**
     * #UnusedField
     * The name suggests an intended “undo last adjustment” feature, 
     * but no such capability exists in the current implementation.
     */
    private final ArrayList<Double> lastAdjustments;

    /**
     * Constructs a DeficitSolver by reading income and expense data
     * from a DataReader instance. Essential categories are excluded
     * from adjustment.
     *
     * @param reader DataReader supplying income and categorized amounts
     * @author Sahadat Amzad
     */
    public DeficitSolver(DataReader reader) {
        this.income = reader.getTotalIncome();
        this.categories = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.lastAdjustments = new ArrayList<>();

        int totalIncomeAll = reader.getTotalIncome();
        int totalExpensesNeg = reader.getTotalExpenses(); // negative
        int net = totalIncomeAll + totalExpensesNeg;

        // cleaner deficit calculation
        this.overallDeficit = Math.max(0, -net);

        List<String> allCategories = reader.getCategories();
        List<Integer> allAmounts = reader.getAmounts();

        // aggregate adjustable expenses
        Map<String, Double> expenseTotals = new HashMap<>();
        for (int i = 0; i < allCategories.size(); i++) {
            String cat = allCategories.get(i);
            int amount = allAmounts.get(i);

            if (amount < 0 && DataReader.isExpenseCategory(cat)) {
                if (isNonNegotiable(cat)) continue;
                double positiveExpense = -amount;
                expenseTotals.merge(cat, positiveExpense, Double::sum);
            }
        }

        for (Map.Entry<String, Double> entry : expenseTotals.entrySet()) {
            categories.add(entry.getKey());
            expenses.add(entry.getValue());
        }
    }

    /**
     * Determines whether a category is essential and cannot be reduced.
     *
     * @param cat name of category
     * @return true if the category is non-negotiable
     * @author Sahadat Amzad
     */
    private boolean isNonNegotiable(String cat) {
        return (cat.equalsIgnoreCase("Rent")
                || cat.equalsIgnoreCase("Home")
                || cat.equalsIgnoreCase("Utilities")
                || cat.equalsIgnoreCase("Work"));
    }

    /**
     * Calculates how much deficit must be covered by adjustable categories.
     *
     * @return deficit limited to adjustable total, or 0 if no deficit exists
     * @author Sahadat Amzad
     */
    public double calculateDeficit() {
        double totalAdjustable = 0.0;
        for (double cost : expenses) totalAdjustable += cost;

        if (overallDeficit <= 0.0 || totalAdjustable <= 0.0)
            return 0.0;

        return Math.min(overallDeficit, totalAdjustable);
    }

    /**
     * Determines whether non-negotiable expenses alone exceed income.
     * If true, no solution exists by only reducing adjustable spending.
     *
     * @param reader DataReader providing categorized expenses
     * @return true if essential expenses exceed income
     * @author Sahadat Amzad
     */
    public boolean essentialExpensesCauseDeficit(DataReader reader) {
        double nonNegotiableTotal = 0.0;

        List<String> cats = reader.getCategories();
        List<Integer> amts = reader.getAmounts();

        for (int i = 0; i < cats.size(); i++) {
            if (isNonNegotiable(cats.get(i)) && amts.get(i) < 0) {
                nonNegotiableTotal += -amts.get(i);
            }
        }
        return nonNegotiableTotal > income;
    }

    /**
     * Identifies simple recommended adjustments of 10% per category.
     *
     * @return list of recommended reductions per category (10% each)  
     *         or 0 for all if no deficit exists.
     * @author Sahadat Amzad
     */
    public ArrayList<Double> identifyAdjustments() {
        double deficit = calculateDeficit();
        ArrayList<Double> reductions = new ArrayList<>();

        if (deficit == 0) {
            for (int i = 0; i < categories.size(); i++)
                reductions.add(0.0);
            return reductions;
        }

        for (double expense : expenses)
            reductions.add(expense * 0.10);

        return reductions;
    }

    /**
     * Computes proportional reductions across all adjustable categories
     * to eliminate the deficit. Includes zero-capping and redistribution
     * to ensure mathematical correctness.
     *
     * @return list of proportional reductions for each category
     * @author Sahadat Amzad
     */
    public ArrayList<Double> proportionalReductions() {
        ArrayList<Double> reductions = new ArrayList<>();

        double totalAdjustable = 0.0;
        for (double e : expenses) totalAdjustable += e;

        if (overallDeficit <= 0.0 || totalAdjustable <= 0.0) {
            for (int i = 0; i < categories.size(); i++)
                reductions.add(0.0);
            return reductions;
        }

        double amountToCut = Math.min(overallDeficit, totalAdjustable);

        // initial proportion
        for (double expense : expenses) {
            double share = expense / totalAdjustable;
            reductions.add(share * amountToCut);
        }

        redistributeAfterZeroCap(reductions);
        return reductions;
    }

    /**
     * Ensures reductions never exceed category expenses and redistributes
     * overflow reductions proportionally.
     *
     * @param reductions preliminary list of reductions
     * @author Sahadat Amzad
     */
    private void redistributeAfterZeroCap(ArrayList<Double> reductions) {
        double remaining = 0.0;

        // Cap reductions at category max
        for (int i = 0; i < reductions.size(); i++) {
            double maxAllowed = expenses.get(i);
            if (reductions.get(i) > maxAllowed) {
                remaining += (reductions.get(i) - maxAllowed);
                reductions.set(i, maxAllowed);
            }
        }

        // redistribute remaining amount
        while (remaining > 0.01) {
            double pool = 0.0;

            for (int i = 0; i < reductions.size(); i++) {
                double room = expenses.get(i) - reductions.get(i);
                if (room > 0) pool += room;
            }

            if (pool <= 0) break;

            for (int i = 0; i < reductions.size(); i++) {
                double room = expenses.get(i) - reductions.get(i);
                if (room > 0) {
                    double share = room / pool;
                    double give = Math.min(room, remaining * share);
                    reductions.set(i, reductions.get(i) + give);
                    remaining -= give;
                }
            }
        }
    }

    /**
     * Generates a full explanation of the deficit and recommended reductions,
     * including annual and monthly values, proportional math explanation,
     * and special warnings for impossible scenarios.
     *
     * @param reader DataReader for checking essential expenses
     * @return formatted multi-line human-readable summary
     * @author Sahadat Amzad
     */
    public String generateDetailedSummary(DataReader reader) {

        if (essentialExpensesCauseDeficit(reader)) {
            return """
                Your essential expenses (such as rent, utilities, and required transportation)
                exceed your total income. As a result you cannot reduce your deficit.
                """;
        }

        double deficitAnnual = overallDeficit;
        double deficitMonthly = deficitAnnual / 12.0;

        ArrayList<Double> recs = proportionalReductions();

        StringBuilder sb = new StringBuilder();
        sb.append("=== Annual Budget Deficit Summary ===\n");
        sb.append("Annual deficit: $").append(deficitAnnual).append("\n");
        sb.append("Monthly equivalent: $")
          .append(String.format("%.2f", deficitMonthly)).append("\n\n");

        sb.append("Reductions are proportional — categories where you spend more receive\n");
        sb.append("larger reductions to ensure fairness.\n\n");

        sb.append("=== Recommended Annual Reductions ===\n");
        for (int i = 0; i < categories.size(); i++) {
            double annual = recs.get(i);
            double monthly = annual / 12.0;

            sb.append(" - ").append(categories.get(i)).append(": reduce $")
              .append(String.format("%.2f", annual)).append(" annually (≈ $")
              .append(String.format("%.2f", monthly)).append("/month)\n");
        }

        return sb.toString();
    }

    /**
     * Returns the total adjustable spending for a specific category.
     *
     * @param category category name
     * @return total spending in that category
     * @author Sahadat Amzad
     */
    public double getTotalForCategory(String category) {
        double total = 0.0;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).equalsIgnoreCase(category)) {
                total += expenses.get(i);
            }
        }
        return total;
    }

    /**
     * Runs a what-if scenario where one category is reduced first.
     *
     * @param category category to reduce
     * @return array: [amount reduced, deficit remaining]
     * @author Sahadat Amzad
     */
    public double[] whatIfReduceCategory(String category) {
        double deficit = calculateDeficit();
        double catTotal = getTotalForCategory(category);

        if (catTotal >= deficit) {
            return new double[]{deficit, 0.0};
        } else {
            return new double[]{catTotal, deficit - catTotal};
        }
    }

    /**
     * Generates a formatted summary of a what-if scenario.
     *
     * @param category category being reduced
     * @return explanation of impact on deficit
     * @author Sahadat Amzad
     */
    public String generateWhatIfSummary(String category) {
        double[] result = whatIfReduceCategory(category);
        double reduced = result[0];
        double remaining = result[1];

        StringBuilder sb = new StringBuilder();
        sb.append("=== What-If Scenario: Reduce ").append(category).append(" ===\n");
        sb.append("Amount reduced: $").append(reduced).append("\n");

        if (remaining == 0.0) {
            sb.append("This eliminates your deficit of $").append(calculateDeficit()).append("\n");
        } else {
            sb.append("Reducing ").append(category)
              .append(" to zero covers $").append(reduced).append(" of the deficit.\n");
            sb.append("Remaining deficit: $").append(remaining).append("\n");
        }

        return sb.toString();
    }

    /**
     * Returns a copy of the adjustable category list.
     *
     * @return list of category names
     * @author Sahadat Amzad
     */
    public ArrayList<String> getCategories() {
        return new ArrayList<>(categories);
    }

    /**
     * Returns a copy of the adjustable expense list.
     *
     * @return list of expenses per category
     * @author Sahadat Amzad
     */
    public ArrayList<Double> getExpenses() {
        return new ArrayList<>(expenses);
    }
}
