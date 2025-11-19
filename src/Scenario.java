import java.util.ArrayList;

/**
 * Scenario class represents a financial scenario with income and expense data.
 * 
 * A Scenario stores income categories, expense categories, and their corresponding values.
 * It provides methods to calculate total income and total expenses.
 *
 * @author Tanzina Sumona
 */
public class Scenario {
    private final ArrayList<String> incomeCategories;
    private final ArrayList<String> expenseCategories;
    private final ArrayList<Double> incomeValues;
    private final ArrayList<Double> expenseValues;

    /**
     * Constructs a Scenario with the provided income and expense data.
     * 
     * Creates copies of the provided ArrayLists to maintain data independence.
     *
     * @param incomeCategories the list of income category names
     * @param expenseCategories the list of expense category names
     * @param incomeValues the list of income amounts
     * @param expenseValues the list of expense amounts
     * @author Tanzina Sumona
     */
    public Scenario(ArrayList<String> incomeCategories,
                    ArrayList<String> expenseCategories,
                    ArrayList<Double> incomeValues,
                    ArrayList<Double> expenseValues) {

        this.incomeCategories = new ArrayList<>(incomeCategories);
        this.expenseCategories = new ArrayList<>(expenseCategories);
        this.incomeValues = new ArrayList<>(incomeValues);
        this.expenseValues = new ArrayList<>(expenseValues);
    }

    /**
     * Returns the list of income categories.
     * @return the ArrayList of income category names
     */
    public ArrayList<String> getIncomeCategories() { return incomeCategories; }

    /**
     * Returns the list of expense categories.
     * @return the ArrayList of expense category names
     */
    public ArrayList<String> getExpenseCategories() { return expenseCategories; }

    /**
     * Returns the list of income values.
     * @return the ArrayList of income amounts
     */
    public ArrayList<Double> getIncomeValues() { return incomeValues; }

    /**
     * Returns the list of expense values.

     * @return the ArrayList of expense amounts
     */
    public ArrayList<Double> getExpenseValues() { return expenseValues; }

    /**
     * Calculates and returns the total income.
     * Sums all values in the income values list.
     * @return the total income amount
     * @author Tanzina Sumona
     */
    public double getTotalIncome() {
        double sum = 0;
        for (double v : incomeValues) sum += v;
        return sum;
    }

    /**
     * Calculates and returns the total expenses.
     * Sums all values in the expense values list.
     * @return the total expense amount
     * @author Tanzina Sumona
     */
    public double getTotalExpenses() {
        double sum = 0;
        for (double v : expenseValues) sum += v;
        return sum;
    }
}
