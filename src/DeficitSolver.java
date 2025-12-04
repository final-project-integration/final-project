//Prediction Team Module

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DeficitSolver class for the Prediction Team module. Declares methods used to
 * analyze a financial deficit and determine adjustments/reductions to achieve a
 * balanced budget.
 *
 * @author Sahadat Amzad
 */
public class DeficitSolver {

    private final double income;
    private final double overallDeficit;

    // corresponding lists for categories and expense amounts
    private final ArrayList<String> categories;
    private final ArrayList<Double> expenses;
    // stores the last round of adjustments, used to undo
    private final ArrayList<Double> lastAdjustments;

    /**
     * Default constructor for the DeficitSolver class. Initializes the object
     * without any parameters.
     *
     * @param reader DataReader object to fetch income and expenses
     * @author Sahadat Amzad
     */
    public DeficitSolver(DataReader reader) {
        this.income = reader.getTotalIncome();
        this.categories = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.lastAdjustments = new ArrayList<>();

        // NEW: compute overall deficit from ALL expenses (fixed + adjustable)
        int totalIncomeAll   = reader.getTotalIncome();
        int totalExpensesNeg = reader.getTotalExpenses(); // negative
        int net              = totalIncomeAll + totalExpensesNeg;
        this.overallDeficit  = (net < 0) ? -net : 0.0;

        List<String> allCategories = reader.getCategories();
        List<Integer> allAmounts   = reader.getAmounts();

        // (keep your existing adjustable-category aggregation logic)
        Map<String, Double> expenseTotals = new HashMap<>();
        for (int i = 0; i < allCategories.size(); i++) {
            String cat = allCategories.get(i);
            int amount = allAmounts.get(i);

            if (amount < 0 && DataReader.isExpenseCategory(cat)) {
                if (cat.equalsIgnoreCase("Rent")
                        || cat.equalsIgnoreCase("Home")
                        || cat.equalsIgnoreCase("Utilities")
                        || cat.equalsIgnoreCase("Work")) {
                    continue; // skip fixed
                }
                double positiveExpense = -1.0 * amount;
                expenseTotals.merge(cat, positiveExpense, Double::sum);
            }
        }

        for (Map.Entry<String, Double> entry : expenseTotals.entrySet()) {
            categories.add(entry.getKey());
            expenses.add(entry.getValue());
        }
    }

    /**
     * Calculates how much deficit must be covered by adjustable categories.
     *
     * This uses the overallDeficit (from all income and expenses) and limits it
     * to what we actually spend in the adjustable categories tracked in this solver.
     *
     * @return the deficit amount to be covered by adjustable categories; 0 if none
     */
    public double calculateDeficit() {
        double totalAdjustable = 0.0;
        for (double cost : expenses) {
            totalAdjustable += Math.abs(cost); // expenses are already positive, but abs is safe
        }

        if (overallDeficit <= 0.0 || totalAdjustable <= 0.0) {
            return 0.0;
        }

        // We can’t cut more than we spend in adjustable categories.
        return Math.min(overallDeficit, totalAdjustable);
    }


    /**
     * Identifies possible adjustments to reduce expenses and eliminate the
     * deficit using a simple fixed reduction approach (non-rent 10%).
     *
     * @return a list of recommended expense reductions for each category
     * @author Sahadat Amzad
     */
    public ArrayList<Double> identifyAdjustments() {
        double deficit = calculateDeficit();
        ArrayList<Double> reductions = new ArrayList<>();

        if (deficit == 0) {
            for (int i = 0; i < categories.size(); i++) {
                reductions.add(0.0);
            }
            return reductions;
        }

        for (int i = 0; i < categories.size(); i++) {
            String name = categories.get(i);
            double cost = expenses.get(i);

            if (name.equalsIgnoreCase("Rent")) {
                reductions.add(0.0);
            } else {
                reductions.add(cost * 0.10);
            }
        }

        return reductions;
    }

    /**
     * Generates a summary of the user's financial deficit and proposed
     * adjustments (simple 10% reduction for non-rent categories).
     *
     * @return string detailing a summary of the deficit and adjustment plan
     * @author Sahadat Amzad
     */
    // public String generateSummary() {
    //     double deficit = calculateDeficit();
    //     StringBuilder sb = new StringBuilder();

    //     sb.append("=== Financial Deficit Summary ===\n");
    //     sb.append("Income: $").append(income).append("\n");

    //     double total = 0;
    //     for (double e : expenses) {
    //         total += e;
    //     }
    //     sb.append("Total Expenses: $").append(total).append("\n");
    //     sb.append("Deficit: $").append(deficit).append("\n\n");

    //     ArrayList<Double> recs = identifyAdjustments();
    //     sb.append("Recommended Adjustments:\n");

    //     for (int i = 0; i < recs.size(); i++) {
    //         if (recs.get(i) > 0) {
    //             sb.append(" - ").append(categories.get(i)).append(": reduce by $").append(recs.get(i)).append("\n");
    //         }
    //     }

    //     return sb.toString();
    // }

    /**
     * Applies adjustments to the user's expense data. By default, uses simple
     * 10% non-rent reductions.
     *
     * @return true if adjustments were applied, false otherwise
     * @author Sahadat Amzad
     */
    // public boolean applyAdjustments() {
    //     return applyAdjustments(false); // default = simple 10% reduction
    // }

    /**
     * Applies adjustments to the user's expense data.
     *
     * @param useProportional if true, applies proportional reductions based on
     * deficit; if false, uses simple 10% reduction for non-rent
     * @return true if adjustments were applied, false otherwise
     * @author Sahadat Amzad
     */
    // public boolean applyAdjustments(boolean useProportional) {
    //     ArrayList<Double> adjustments = useProportional ? proportionalReductions() : identifyAdjustments();

    //     boolean hasAdjustments = false;
    //     for (double a : adjustments) {
    //         if (a > 0) {
    //             hasAdjustments = true;
    //             break;
    //         }
    //     }

    //     if (!hasAdjustments) {
    //         return false;
    //     }

    //     lastAdjustments.clear();

    //     for (int i = 0; i < expenses.size(); i++) {
    //         lastAdjustments.add(adjustments.get(i));
    //         expenses.set(i, expenses.get(i) - adjustments.get(i));
    //     }

    //     return true;
    // }

    /**
     * Reverts previously applied adjustments to restore the original expense
     * data.
     *
     * @return true if the adjustments are successfully undone; false otherwise
     * @author Sahadat Amzad
     */
    // public boolean undoAdjustment() {
    //     if (lastAdjustments.isEmpty()) {
    //         return false;
    //     }

    //     for (int i = 0; i < expenses.size(); i++) {
    //         expenses.set(i, expenses.get(i) + lastAdjustments.get(i));
    //     }

    //     lastAdjustments.clear();
    //     return true;
    // }

    /**
     * Returns the total expense amount for a given category.
     *
     * @param category the category name
     * @return total expense amount; 0 if category not found
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
     * Calculates how much to reduce a specific category to eliminate deficit.
     *
     * @param category the chosen category
     * @return a double array: [amountToReduce, remainingDeficit]
     * @author Sahadat Amzad
     */
    public double[] whatIfReduceCategory(String category) {
        double deficit = calculateDeficit();
        double catTotal = getTotalForCategory(category);

        if (catTotal >= deficit) {
            return new double[]{deficit, 0.0}; // enough to cover deficit
        } else {
            return new double[]{catTotal, deficit - catTotal}; // reduce all, remaining deficit
        }
    }

    /**
     * Calculates proportional reductions across all categories to eliminate
     * deficit. Reductions are distributed based on each category's share of
     * total non-rent expenses.
     *
     * @return list of reductions (aligned with categories list)
     * @author Sahadat Amzad
     */
    public ArrayList<Double> proportionalReductions() {
        ArrayList<Double> reductions = new ArrayList<>();

        // If there is no overall deficit, or no adjustable spending,
        // then there is nothing to cut.
        double totalAdjustable = 0.0;
        for (double e : expenses) {
            totalAdjustable += e;
        }

        if (overallDeficit <= 0.0 || totalAdjustable <= 0.0) {
            for (int i = 0; i < categories.size(); i++) {
                reductions.add(0.0);
            }
            return reductions;
        }

        // We cannot cut more than we spend in adjustable categories.
        double amountToCut = Math.min(overallDeficit, totalAdjustable);

        // Distribute amountToCut proportional to each category's share
        for (int i = 0; i < categories.size(); i++) {
            double share = expenses.get(i) / totalAdjustable;
            reductions.add(share * amountToCut);
        }

        return reductions;
    }


    /**
     * Generates a human-readable What-If scenario for reducing a single
     * category to break even.
     *
     * @param category the chosen category
     * @return string summarizing the reduction and remaining deficit if any
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
            sb.append("Even reducing ").append(category).append(" to zero only covers $")
                    .append(reduced).append(" of the deficit.\n");
            sb.append("Remaining deficit: $").append(remaining).append("\n");
        }

        return sb.toString();
    }

    /**
     * Returns a copy of the list of categories.
     *
     * @return list of category names
     * @author Sahadat Amzad
     */
    public ArrayList<String> getCategories() {
        return new ArrayList<>(categories);
    }

    /**
     * Returns a copy of the current expense list.
     *
     * @return list of expense amounts
     * @author Sahadat Amzad
     */
    public ArrayList<Double> getExpenses() {
        return new ArrayList<>(expenses);
    }
}
