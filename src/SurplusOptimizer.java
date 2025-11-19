import java.util.ArrayList;
import java.util.List;

/**
 * SurplusOptimizer class for the Prediction Team module.
 * Declares methods used to examine surplus budgets and determines how to manage
 * funds.
 * 
 * @author Daniel Moore
 */

public final class SurplusOptimizer {
    private double surplus;
    private double savings;
    private StringBuilder surplusLog;
    private ArrayList<String> incCategory;
    private ArrayList<String> expCategory;
    private ArrayList<Double> expenses;
    private ArrayList<Double> income;

    /**
     * Default constructor for the SurplusOptimizer class.
     * Initializes the object without any parameters.
     * 
     * @author Daniel Moore
     */
    public SurplusOptimizer() {
        this.surplus = 0.0;
        this.savings = 0.0;
        this.surplusLog = new StringBuilder();
        this.incCategory = new ArrayList<>();
        this.expCategory = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.income = new ArrayList<>();
    }

    /**
     * Constructor for the SurplusOptimizer class.
     * Initializes the object with the provided parameters.
     * 
     * @param surplus    the total amount of the surplus
     * @param savings    the current total amount of savings
     * @param surplusLog the log of surplus changes
     * @author Daniel Moore
     */
    public SurplusOptimizer(double surplus, double savings, StringBuilder surplusLog) {
        this.surplus = surplus;
        this.savings = savings;
        this.surplusLog = surplusLog;
        this.incCategory = new ArrayList<>();
        this.expCategory = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.income = new ArrayList<>();
    }

    /**
     * Constructor for the SurplusOptimizer class that uses DataReader.
     * Populates income and expense lists from the CSV data.
     *
     * @param reader the DataReader that has already loaded data.csv
     * @author Daniel Moore
     */
    public SurplusOptimizer(DataReader reader) {
        // Reuse the default constructor to initialize fields and lists
        this();

        List<String> categories = reader.getCategories();
        List<Integer> amounts = reader.getAmounts();

        for (int i = 0; i < categories.size(); i++) {
            String cat = categories.get(i);
            int amount = amounts.get(i);

            // Classify using the same rules as DataReader
            if (DataReader.isIncomeCategory(cat)) {
                incCategory.add(cat);
                income.add((double) amount);
            } else if (DataReader.isExpenseCategory(cat)) {
                expCategory.add(cat);
                expenses.add((double) amount);
            }
        }

        // Compute initial surplus based on the loaded data
        surplusCreator(income, expenses);
    }


    /**
     * Decreases expense in the specified category by 10%, excluding Rent.
     * 
     * @param category the financial category to decrease expense
     * @author Daniel Moore
     */
    public void decreaseExpense(String category) {
        if (category.equalsIgnoreCase("Rent")) {
                return;
            }
        for (int i = 0; i < expCategory.size(); i++) {
            if (expCategory.get(i).equalsIgnoreCase(category)) {
                double reduction = expenses.get(i) * (0.10); // 10% reduction byt will make this adjustable later
                expenses.set(i, expenses.get(i) - reduction);
                this.surplus += reduction;
                
                surplusLog.append("Decreased expense in category: ")    
                    .append(category)
                    .append(" by $")
                    .append(reduction)
                    .append("\n");
                return;
            }
        }
        surplusLog.append("Category ").append(category)     //tell the user if the category didn't exist
            .append(" not found. No changes applied.\n");
    }

    /**
     * Calculates the surplus based on income and expenses.
     * 
     * @param income   the list of income amounts
     * @param expenses the list of expense amounts
     * @return returns total amount of surplus
     * @author Daniel Moore
     */
    public double surplusCreator(ArrayList<Double> income, ArrayList<Double> expenses) {
        // Guard against null parameters
        if (income == null) {
            income = new ArrayList<>();
        }
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        this.income = income;
        this.expenses = expenses;
        this.surplus = 0.0;

        for (double inc : income) {
            this.surplus += inc;
        }

        for (double exp : expenses) {
            this.surplus -= exp;
        }

        if (this.surplus <= 0) {
            this.surplus = 0;
            System.out.println("No surplus available after expenses.");
        }
        return this.surplus;
    }

    /**
     * Returns the current amount of savings.
     * 
     * @return returns the current total amount of savings
     * @author Daniel Moore
     */
    public double savingsAmount() {
        
        this.savings = 0;

        for (int i = 0; i < incCategory.size(); i++) {
            if (incCategory.get(i).equalsIgnoreCase("Investments")) {
                this.savings = income.get(i);
            }
        }
        return this.savings;
    }

    /**
     * Tracks surplus changes over time.
     * 
     * @return provides a concise list of changes from the first surplus to the
     *         current surplus
     * @author Daniel Moore
     */
    public String surplusTracker() {
        double total = 0;
        surplusLog.setLength(0);
        surplusLog.append("Surplus changed to: $")
            .append(this.surplus)
            .append("\n");

        for (double inc : income)
            total += inc;
        surplusLog.append("Total income: $")
            .append(total)
            .append("\n");
        
        total = 0;
        for (double exp : expenses)
            total += exp;
        surplusLog.append("Total expenses: $")
            .append(total)
            .append("\n");

        return surplusLog.toString();
    }

    /**
     * Suggests which expense category to decrease based on the highest expense,
     * excluding Rent.
     * 
     * @return suggests which expense category to decrease
     * @author Daniel Moore
     */
    public String surplusSuggestion() {
        if (expenses.isEmpty()) {
        return "No expenses to suggest reductions.";
        }

        double maxExpense = 0.0;
        int maxIndex = -1;
        for (int i = 0; i < expenses.size(); i++) {
            String category = expCategory.get(i);
            if (category.equalsIgnoreCase("Rent")) {
                continue;
            }
        //double value = expenses.get(i);  --> commented out unused variable --Tanzina

            if (expenses.get(i) > maxExpense) {
                maxExpense = expenses.get(i);
                maxIndex = i;
            }
        }

        if (maxIndex == -1 || maxExpense <= 0) {
            return "No expenses to suggest reductions.";
        }
        return "Consider decreasing expenses in category: " + expCategory.get(maxIndex);
    }
}