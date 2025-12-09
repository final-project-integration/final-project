//Prediction Team Module

import java.util.ArrayList;
import java.util.Map;


/**
 * ScenarioSimulator provides formatted summaries and explanations
 * for ModuleHub. It performs no calculations itself, all numbers
 * come directly from DataReader, SurplusOptimizer, or DeficitSolver.
 *
 * Tab 1: Financial Summary using DataReader ONLY.
 *
 * @author Tanzina Sumona
 */
public class ScenarioSimulator {

    private final DataReader dataReader; // source of all financial data

    public ScenarioSimulator(DataReader reader) { 
        this.dataReader = reader; // initialize with DataReader
    }

    /**
     * Builds a clean financial summary showing:
     *  - Total income
     *  - Total expenses
     *  - Net balance
     *  - Surplus/Deficit message
     *
     * No calculations except:
     *     net = income + expenses   (expenses are negative)
     *
     * @return formatted summary string
     * @author Tanzina Sumona
     */
    public String buildFinancialSummary() { 

        int totalIncome   = dataReader.getTotalIncome();     // positive
        int totalExpenses = dataReader.getTotalExpenses();   // negative
        int net           = totalIncome + totalExpenses;     // profit or loss

        StringBuilder sb = new StringBuilder(); // build the summary string

        sb.append("===== Financial Summary =====\n\n"); 
        sb.append(String.format("Total Income:    $%d%n", totalIncome));
        sb.append(String.format("Total Expenses:  $%d%n", Math.abs(totalExpenses)));
        sb.append(String.format("Net Balance:     $%d%n%n", net));

        // SURPLUS / DEFICIT / BREAK-EVEN MESSAGE
        if (net > 0) {
            sb.append(String.format("You have a Surplus of $%d.%n", net));
            sb.append("A surplus means your income is greater than your expenses.\n");
        }
        else if (net < 0) {
            sb.append(String.format("You have a Deficit of $%d.%n", -net));
            sb.append("A deficit means your spending exceeds your income.\n");
        }
        else {
            sb.append("You are breaking even.\n");
            sb.append("Your income exactly matches your expenses.\n");
        }

        return sb.toString(); // return the completed summary
    }

    /**
     * Tab 2: Deficit Analysis using DeficitSolver ONLY.
     */

    /**
     * Checks whether the current budget is in deficit.
     * ScenarioSimulator does not calculate anything — it simply
     * uses the totals provided by DataReader.
     *
     * @return true if expenses exceed income
     * @author Tanzina Sumona
     */
    public boolean hasDeficit() { // deficit if net < 0
        int totalIncome   = dataReader.getTotalIncome();
        int totalExpenses = dataReader.getTotalExpenses(); // negative
        int net = totalIncome + totalExpenses;

        return net < 0;
    }
    /**
     * Builds the What-If explanation for reducing a single category.
     * All deficit logic comes from DeficitSolver.
     *
     * @param category adjustable expense category to test
     * @return formatted message for the UI
     * @author Tanzina Sumona
     */
    public String buildDeficitWhatIfSummary(String category) { // deficit what-if explanation

        // If there is NO deficit, ScenarioSimulator returns a friendly explanation
        if (!hasDeficit()) { // no deficit
            return """
                ===== Deficit What-If =====
                You do not currently have a deficit.

                A What-If reduction is only meaningful when your expenses
                exceed your income.

                You're in a stable financial position.
                """;
        }

        // If there IS a deficit, then DeficitSolver will explain it
        DeficitSolver solver = new DeficitSolver(dataReader); // create solver instance
        double adjustableDeficit = solver.calculateDeficit(); // deficit from adjustable categories

        if (adjustableDeficit <= 0) { // no adjustable deficit
            return """
                ===== Deficit What-If =====
                You do have a deficit, but none of it comes from
                adjustable categories like Food or Entertainment.

                The deficit likely comes from fixed categories such as:
                    • Rent / Home
                    • Utilities
                    • Work-related required expenses

                Reducing flexible spending alone cannot fix this deficit.
                """;
        }

        // Delegate the explanation to DeficitSolver
        return solver.generateWhatIfSummary(category); // get explanation from solver
    }

    /**
     * Builds a proportional reduction plan across all adjustable 
     * expense categories. All math is done by DeficitSolver.
     *
     * @return formatted deficit-closing plan text
     * @author Tanzina Sumona
     */
    public String buildDeficitProportionalPlan() { // proportional deficit plan

        if (!hasDeficit()) { // no deficit
            return """
                ===== Proportional Deficit Plan =====
                No deficit detected — you are not spending more
                than you earn, so no reductions are necessary.
                """;
        }

        DeficitSolver solver = new DeficitSolver(dataReader); // create solver instance

        // Ask solver for adjustable categories, current expenses, and reductions
        ArrayList<String> categories = solver.getCategories(); // adjustable categories
        ArrayList<Double> expenses   = solver.getExpenses();   // current expenses
        ArrayList<Double> cuts       = solver.proportionalReductions(); // suggested cuts

        StringBuilder sb = new StringBuilder(); // build the plan string
        sb.append("===== Proportional Deficit Plan =====\n");

        double deficit = solver.calculateDeficit(); // total adjustable deficit
        sb.append(String.format("Your deficit that can be fixed through adjustable spending is $%.2f.%n%n",
                                deficit));

        boolean anyCut = false; // check if any cuts are suggested
        for (double cut : cuts) { // check each suggested cut
            if (cut > 0) { 
                anyCut = true; // at least one cut suggested
                break; }
        }

        if (!anyCut) { // no cuts suggested
            sb.append("""
                No adjustable categories were found to reduce.
                Your deficit likely comes from fixed categories (Rent, Utilities, Work).
                """);
            return sb.toString();
        }

        sb.append("Recommended reductions:\n\n");

        for (int i = 0; i < categories.size(); i++) { // list each category
            double cut = cuts.get(i);          // suggested cut
            double current = expenses.get(i); // current expense

            if (cut > 0) { // only show categories with suggested cuts
                sb.append(String.format("  %-15s reduce by $%.2f (current: $%.2f)%n",
                                        categories.get(i), cut, current));
            }
        }

        sb.append("""
                
                These reductions are proportional to how much you currently spend in each
                adjustable category and are designed to close your deficit. For example, if 
                you spend twice as much on Entertainment as on Appearance, the suggested cut
                for Entertainment will be roughly twice that of Appearance.
                """);

        return sb.toString();
    }


    /**
     * Tab 3: Surplus Analysis using SurplusOptimizer ONLY.
     */

    /**
     * Checks whether the current budget is in surplus.
     * Uses totals from DataReader only.
     *
     * @return true if income > expenses
     * @author Tanzina Sumona
     */
    public boolean hasSurplus() { // surplus if net > 0
        int totalIncome   = dataReader.getTotalIncome();
        int totalExpenses = dataReader.getTotalExpenses(); // negative
        int net           = totalIncome + totalExpenses;

        return net > 0;
    }


    /**
     * Builds the "Surplus What-If" explanation for increasing
     * a single expense category. All surplus logic is handled
     * by SurplusOptimizer.
     *
     * @param category expense category to increase
     * @return formatted explanation string for the UI
     * @author Tanzina Sumona
     */
    public String buildSurplusWhatIfSummary(String category) { // surplus what-if explanation

        if (!hasSurplus()) { // no surplus
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

        // Delegate to SurplusOptimizer
        SurplusOptimizer optimizer = new SurplusOptimizer(dataReader); // create optimizer instance
        return optimizer.generateWhatIfSummary(category); // get explanation from optimizer
    }

    /**
     * Builds a proportional surplus allocation plan. SurplusOptimizer
     * computes how to spread the surplus across expense categories
     * based on their share of total expenses.
     *
     * @return formatted surplus plan text for the UI
     * @author Tanzina Sumona
     */
    public String buildSurplusProportionalPlan() { // proportional surplus plan

        if (!hasSurplus()) { // no surplus
            return """
                ===== Proportional Surplus Plan =====
                You currently do not have a surplus. This means your income is only
                covering your existing expenses with nothing left over.

                To create a surplus in the future, consider:
                    • Reducing optional or discretionary spending
                    • Tracking recurring expenses for potential savings
                    • Increasing your income where possible
                    • Setting a small monthly savings goal
                """;
        }

        SurplusOptimizer optimizer = new SurplusOptimizer(dataReader); // create optimizer instance

        // Optionally exclude fixed categories from getting extra money
        optimizer.categoryBlacklist("Home");
        optimizer.categoryBlacklist("Rent");
        optimizer.categoryBlacklist("Utilities");
        optimizer.categoryBlacklist("Work");

        // Ask SurplusOptimizer for the allocation plan (annual amounts)
        Map<String, Integer> plan = optimizer.surplusAllocationPlan();
        int annualSurplus = optimizer.getSurplusValue();
        int monthlySurplus = annualSurplus / 12;

        StringBuilder sb = new StringBuilder(); // build the plan string
        sb.append("===== Proportional Surplus Plan =====\n");
        sb.append(String.format("You have an annual surplus of $%d (≈ $%d per month).%n%n",
                                annualSurplus, monthlySurplus));
        sb.append("This plan allocates your surplus in proportion to how much you\n");
        sb.append("currently spend in each category.\n\n");
        sb.append(String.format("%-15s  %-15s  %-15s%n",
                                "Category", "Annual Increase", "≈ Monthly"));
        sb.append("--------------------------------------------------------\n");

        for (Map.Entry<String, Integer> entry : plan.entrySet()) { // each category
            String cat = capitalize(entry.getKey()); // category name
            int annual = entry.getValue();          // annual increase
            int monthly = (int) Math.round(annual / 12.0); // monthly approx


            sb.append(String.format("%-15s  $%-14d  $%-14d%n",
                                    cat, annual, monthly)); // formatted line
        }

        sb.append("\nAfter following this plan, you should end at break-even.\n");
        return sb.toString();
    }
     /**
     * Capitalizes the first letter of a string.
     * Copied from SurplusOptimizer for consistency.
     * @param str the string to capitalize
     * @return the capitalized string
     * @author Daniel Moore
     */
    private String capitalize(String str) { // capitalize first letter
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}




