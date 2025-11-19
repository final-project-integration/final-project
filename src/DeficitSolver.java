import java.util.ArrayList;
import java.util.List;


/**
 * DeficitSolver class for the Prediction Team module. Declares methods used
 * to analyze a financial deficit and determine adjustments/reductions to
 * achieve a balanced budget.
 *
 * @author Sahadat Amzad
 */
public class DeficitSolver {

    private final double income;

    // corresponding lists for categories and expense amounts
    private final ArrayList<String> categories;
    private final ArrayList<Double> expenses;

    // stores the last round of adjustments, used to undo
    private final ArrayList<Double> lastAdjustments;

    /**
     * Default constructor for the DeficitSolver class.
     * Initializes the object without any parameters.
     * @author Sahadat Amzad
     */
    public DeficitSolver(DataReader reader) {
        this.income = reader.getTotalIncome();
        this.categories = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.lastAdjustments = new ArrayList<>();
        
        List<String> allCategories = reader.getCategories();
        List<Integer> allAmounts = reader.getAmounts();
        
        for (int i = 0; i < allCategories.size(); i++) {
            String cat = allCategories.get(i);
            int amount = allAmounts.get(i);

            // income rows are already included in totalIncome,
            // but we only want to track expenses here:
            if (amount > 0) { // or better: check if it's an expense category
                categories.add(cat);
                expenses.add((double) amount);
            }
        }
    }
        /*income = 3000.0;

        categories = new ArrayList<>();
        expenses = new ArrayList<>();
        lastAdjustments = new ArrayList<>();

        //added this to test 
        categories.add("Rent");         
        expenses.add(1600.0);
        categories.add("Food");         
        expenses.add(500.0);
        categories.add("Transport");    
        expenses.add(250.0);
        categories.add("Entertainment");
        expenses.add(200.0);
        categories.add("Misc");         
        expenses.add(300.0);*/
    

    /**
     * Calculates the user's total financial deficit.
     *
     * @return the total deficit amount; returns 0 if there is no deficit
     * @author Sahadat Amzad
     */
    public double calculateDeficit() {
        double total = 0;

        for (double cost : expenses) {
            total += cost;
        }

        double deficit = total - income;
        return Math.max(deficit, 0);
    }

    /**
     * Identifies possible adjustments to reduce expenses and eliminate the
     * deficit.
     *
     * @return a list of recommended expense reductions for each category
     * @author Sahadat Amzad
     */
    public ArrayList<Double> identifyAdjustments() {
        double deficit = calculateDeficit();
        ArrayList<Double> reductions = new ArrayList<>();

        // if there is no deficit return 0's for the reductions ArrayList
        if (deficit == 0) {
            for (int i = 0; i < categories.size(); i++) {
                reductions.add(0.0);
            }
            return reductions;
        }

        // reduces everything except rent by 10% to reduce expenses 
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
     * adjustments.
     *
     * @return string detailing a summary of the deficit and adjustment plan
     * @author Sahadat Amzad
     */
    public String generateSummary() {
        double deficit = calculateDeficit();
        StringBuilder sb = new StringBuilder();

        sb.append("=== Financial Deficit Summary ===\n");
        sb.append("Income: $").append(income).append("\n");

        double total = 0;
        for (double e : expenses) total += e;
        sb.append("Total Expenses: $").append(total).append("\n");
        sb.append("Deficit: $").append(deficit).append("\n\n");

        ArrayList<Double> recs = identifyAdjustments();
        sb.append("Recommended Adjustments:\n");

        for (int i = 0; i < recs.size(); i++) {
            if (recs.get(i) > 0) {
                sb.append(" - ").append(categories.get(i)).append(": reduce by $").append(recs.get(i)).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Applies the identified adjustments to the user's expense data.
     *
     * @return true if the adjustments are successful, false otherwise
     * @author Sahadat Amzad
     */
    public boolean applyAdjustments() {
        ArrayList<Double> adjustments = identifyAdjustments();

        boolean hasAdjustments = false;
        for (double a : adjustments) {
            if (a > 0) {
                hasAdjustments = true;
                break;
            }
        }

        if (!hasAdjustments) return false;

        lastAdjustments.clear();

        for (int i = 0; i < expenses.size(); i++) {
            lastAdjustments.add(adjustments.get(i));
            expenses.set(i, expenses.get(i) - adjustments.get(i));
        }

        return true;
    }

    /**
     * Reverts previously applied adjustments to restore the original expense
     * data.
     *
     * @return true if the adjustments are successfully undone; false otherwise
     * @author Sahadat Amzad
     */
    public boolean undoAdjustment() {
        if (lastAdjustments.isEmpty()) return false;

        for (int i = 0; i < expenses.size(); i++) {
            expenses.set(i, expenses.get(i) + lastAdjustments.get(i));
        }

        lastAdjustments.clear();
        return true;
    }
}
