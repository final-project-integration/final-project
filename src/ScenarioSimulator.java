//Prediction Team Module

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ScenarioSimulator class for the Prediction Team module.
 *
 * This class does not handle any user input or printing.
 * It is intended to be called by ModuleHub that manages user interaction.
 *
 * ScenarioSimulator is responsible for:
 *   - orchestrating calls between DataReader, DeficitSolver, and SurplusOptimizer
 *   - packaging results as strings or simple data structures for the UI to display.
 *
 * @author Tanzina Sumona
 */
public class ScenarioSimulator {

    private final DataReader dataReader;

    /**
     * Prediction module is wired to share the same DataReader
     * that ModuleHub owns. DataReader is assumed to have already
     * read data via readData() or readFromBudget().
     *
     * @param reader a DataReader that has already loaded budget data
     */
    public ScenarioSimulator(DataReader reader) {
        this.dataReader = reader;
    }

    
    /**
     * All numbers come from DataReader, DeficitSolver, and SurplusOptimzer.
     *
     * @return formatted summary text for the UI to display
     * @author Tanzina Sumona
     */
    public String buildFinancialSummary() {
        int totalIncome  = dataReader.getTotalIncome();
        int totalExpenses = dataReader.getTotalExpenses(); // negative
        int net = totalIncome + totalExpenses;   // profit/loss

        StringBuilder sb = new StringBuilder();
        sb.append("===== Financial Summary =====\n\n");
        sb.append("Total Income:   $").append(totalIncome).append("\n");
        sb.append("Total Expense:  $").append(totalExpenses).append("\n");
        sb.append("Net Balance:    $").append(net).append("\n\n");

        if (net > 0) {
            sb.append("You have a Surplus of: $").append(net).append("\n");
            sb.append("A surplus means you are earning more than you are spending.\n This extra money can be used for savings, investments, paying off debt, or optional spending.\n");
        } else if (net < 0) {
            DeficitSolver solver = new DeficitSolver(dataReader);
            double deficit = solver.calculateDeficit();
            sb.append("You have a Deficit of: $").append(deficit).append("\n");
            sb.append("A deficit means you are spending more than you are earning.\n You may need to reduce your expenses or increase your income to balance your budget.\n");
        } else {
            sb.append("You are breaking even (no surplus or deficit).\n");
            sb.append("This means your income exactly matches your expenses.\n");
        }

        return sb.toString();
    }

    /**
     * Convenience helper so the UI can quickly check if
     * the current budget is in deficit.
     *
     * @return true if there is a deficit, false otherwise
     */
    public boolean hasDeficit() {
        DeficitSolver solver = new DeficitSolver(dataReader);
        return solver.calculateDeficit() > 0;
    }

    /**
     * Convenience helper so the UI can quickly check if
     * the current budget is in surplus.
     *
     * @return true if there is a surplus, false otherwise
     */
    public boolean hasSurplus() {
        int totalIncome   = dataReader.getTotalIncome();
        int totalExpenses = dataReader.getTotalExpenses(); // negative
        int net           = totalIncome + totalExpenses;
        return net > 0;
    }

    /**
     * Returns a list of expense categories the user is allowed
     * to adjust for deficit "What-If" scenarios.
     *
     * Uses DataReader's unique expense categories and optionally
     * filters out fixed categories such as "Rent".
     *
     * @return list of adjustable expense category names
     * @author Tanzina Sumona
     */
    public List<String> getAdjustableExpenseCategories() {
        List<String> base = new ArrayList<>(dataReader.getUniqueExpenseCategories());

        // Optionally remove fixed categories like Rent
        base.removeIf(c -> c.equalsIgnoreCase("Rent"));
        base.removeIf(c -> c.equalsIgnoreCase("Home"));
        base.removeIf(c -> c.equalsIgnoreCase("Utilities"));

        return base;
    }

    /**
     * Builds the "Deficit What-If" explanation for a single
     * chosen category. This method assumes the caller has
     * already checked that there is a deficit and that the
     * category name is valid.
     *
     * Internally, this delegates to DeficitSolver's
     * generateWhatIfSummary(...) method.
     *
     * @param category expense category to reduce (e.g. "Entertainment")
     * @return formatted explanation string for the UI to display
     * @author Tanzina Sumona
     */
    public String buildDeficitWhatIfSummary(String category) {
        DeficitSolver solver = new DeficitSolver(dataReader);
        double deficit = solver.calculateDeficit();

        if (deficit <= 0) {
            return """
                   ===== Deficit What-If =====
                    You do not currently have a deficit, which means your income covers
                    all of your expenses for this period.

                    There is no need to reduce any spending right now.
                    Instead, you can focus on:
                        • Building an emergency fund
                        • Increasing savings or investments
                        • Paying down any existing debt
                        • Planning for future large expenses

                    You're in a stable financial position. 
                   """;
        }

        // using human-readable explanation from DeficitSolver
        return solver.generateWhatIfSummary(category);
    }


    /**
     * Builds a proportional reduction plan across all expense
     * categories to eliminate the current deficit. This uses
     * DeficitSolver's proportionalReductions() method.
     *
     * @return formatted plan text for the UI to display
     * @author Tanzina Sumona
     */
    public String buildDeficitProportionalPlan() {
        DeficitSolver solver = new DeficitSolver(dataReader);
        double deficit = solver.calculateDeficit();

        if (deficit <= 0) {
            return """
                   ===== Proportional Deficit Plan =====
                    You do not currently have a deficit, which means your income covers
                    all of your expenses for this period.

                    There is no need to reduce any spending right now.
                    Instead, you can focus on:
                        • Building an emergency fund
                        • Increasing savings or investments
                        • Paying down any existing debt
                        • Planning for future large expenses

                    You're in a stable financial position.
                   """;
        }

        ArrayList<String> categories = solver.getCategories();
        ArrayList<Double> expenses   = solver.getExpenses();
        ArrayList<Double> reductions = solver.proportionalReductions();

        StringBuilder sb = new StringBuilder();
        sb.append("===== Proportional Deficit Plan =====\n");
        sb.append("You currently have a deficit of $").append(deficit).append(".\n");
        sb.append("Based on your current spending pattern, we suggest:\n\n");

        for (int i = 0; i < categories.size(); i++) {
            double r = reductions.get(i);
            if (r > 0) {
                sb.append(String.format("  %-15s reduce by $%.2f (current: $%.2f)%n",
                        categories.get(i), r, expenses.get(i)));
            }
        }

        sb.append("\nAfter following this plan, you should end at break-even.\n");
        return sb.toString();
    }

    /**
     * Returns a list of expense categories the user may choose
     * to increase when there is a surplus. For now this mirrors
     * getAdjustableExpenseCategories(), but a SurplusOptimizer
     * could impose different constraints.
     *
     * @return list of categories that may be increased
     * @author Tanzina Sumona
     */
    public List<String> getIncreaseableExpenseCategories() {
        // For now, reuse adjustable categories.
        return getAdjustableExpenseCategories();
    }

    /**
     * Builds the "Surplus What-If" explanation for increasing
     * a single category. This assumes a SurplusOptimizer class
     * exists that can calculate surplus and generate a summary
     * for how much extra can be safely spent in the given category.
     *
     * NOTE: This will need to be adjusted to match your
     * SurplusOptimizer's actual method names.
     *
     * @param category expense category to increase
     * @return formatted explanation string for UI
     * @author Tanzina Sumona
     */
    public String buildSurplusWhatIfSummary(String category) {
        int totalIncome  = dataReader.getTotalIncome();
        int totalExpenses = dataReader.getTotalExpenses(); // negative
        int net = totalIncome + totalExpenses;

        if (net <= 0) {
            return """
                   ===== Surplus What-If =====
                    You currently do not have a surplus. This means your income is only
                    covering your existing expenses with nothing left over.

                    Increasing any expense right now would push your budget into a deficit.

                    To create a surplus in the future, consider:
                        • Reducing optional or discretionary spending
                        • Tracking recurring expenses for potential savings
                        • Increasing your income where possible
                        • Setting a small monthly savings goal

                    Once you have extra income available, we can help you plan where it
                    can be safely allocated.
                   """;
        }

        // Hypothetical SurplusOptimizer usage – adjust to teammate's API
        SurplusOptimizer optimizer = new SurplusOptimizer(dataReader);
        // Example: assume SurplusOptimizer has a method like:
        //   String generateWhatIfSummary(String category)
        return optimizer.generateWhatIfSummary(category);
    }



    /**
     * Builds a proportional surplus allocation plan, assuming a
     * SurplusOptimizer can calculate how to spread a surplus across
     * categories based on current spending percentages.
     *
     * This is a placeholder that should be aligned with the actual
     * SurplusOptimizer API once finalized.
     *
     * @return formatted plan text for UI to display
     * @author Tanzina Sumona
     */
    public String buildSurplusProportionalPlan() {
        int totalIncome   = dataReader.getTotalIncome();
        int totalExpenses = dataReader.getTotalExpenses(); // negative
        int net           = totalIncome + totalExpenses;

        if (net <= 0) {
            return """
                   ===== Proportional Surplus Plan =====
                    You currently do not have a surplus. This means your income is only
                    covering your existing expenses with nothing left over.

                    Increasing any expense right now would push your budget into a deficit.

                    To create a surplus in the future, consider:
                        • Reducing optional or discretionary spending
                        • Tracking recurring expenses for potential savings
                        • Increasing your income where possible
                        • Setting a small monthly savings goal

                    Once you have extra income available, we can help you plan where it
                    can be safely allocated.
                   """;
        }

        SurplusOptimizer optimizer = new SurplusOptimizer(dataReader);

        optimizer.categoryBlacklist("Home");
        optimizer.categoryBlacklist("Rent");
        optimizer.categoryBlacklist("Utilities");
        optimizer.categoryBlacklist("Work");

        // Example assumption: SurplusOptimizer has a helper that returns
        // a Map<String, Double> of category → extraAmount.
        Map<String, Double> plan = optimizer.getProportionalPlan();

        StringBuilder sb = new StringBuilder();
        sb.append("===== Proportional Surplus Plan =====\n");
        sb.append("You have a surplus of $").append(net).append(".\n");
        sb.append("Based on your current spending pattern, we suggest:\n\n");

        for (Map.Entry<String, Double> entry : plan.entrySet()) {
            sb.append(String.format("  %-15s +$%.2f%n", entry.getKey(), entry.getValue()));
        }

        sb.append("\nAfter following this plan, you should end at break-even.\n");
        return sb.toString();
    }
}
