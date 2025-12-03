import java.util.ArrayList;

/**
 * Performs additional analysis on financial records that have already been
 * loaded by the Reports module.
 *
 * This class does not do any file I/O. It works only with the in-memory
 * records provided by other modules (for example, {@link ReportManager}).
 *
 * All results are returned as simple {@code String} or {@code ArrayList<String>}
 * so that they can be printed to the console or formatted by
 * {@link ReportFormatter}.
 *
 * IMPORTANT: In this project, income amounts are POSITIVE and expense amounts
 * are NEGATIVE (see ReportManager). All calculations below respect that
 * convention.
 *
 * @author Angelo Samir Alvarez
 * @author Furkan Bilgi
 * @author Chukwuemeka Okwuka
 * @author Omar Piron
 */

public class ReportAnalyzer {

    /** Records to be analyzed. These should typically come from ReportManager. */
    private ArrayList<ReportManager.FinancialRecord> records;

    /**
     * Creates an analyzer with an empty record list.
     * Records should be supplied later via {@link #setRecords(ArrayList)}.
     */
    public ReportAnalyzer() {
        this.records = new ArrayList<ReportManager.FinancialRecord>();
    }

    /**
     * Creates an analyzer with an initial list of financial records.
     *
     * @param records list of records to analyze; a defensive copy is created
     */
    public ReportAnalyzer(ArrayList<ReportManager.FinancialRecord> records) {
        setRecords(records);
    }

    /**
     * Replaces the records used for analysis.
     *
     * @param records list of records; if {@code null}, an empty list is used
     */
    public void setRecords(ArrayList<ReportManager.FinancialRecord> records) {
        if (records == null) {
            this.records = new ArrayList<ReportManager.FinancialRecord>();
        } else {
            this.records = new ArrayList<ReportManager.FinancialRecord>(records);
        }
    }

    /**
     * Returns the month that has the highest total expenses for the given year.
     * Only records marked as expenses (where {@code isIncome() == false})
     * are considered.
     *
     * NOTE: Expenses are stored as NEGATIVE numbers. "Highest spending"
     * therefore means the month with the MOST NEGATIVE total.
     *
     * @param year the four-digit year to inspect (for example, 2025)
     * @return a string like {@code "March ($-542.30)"} or a message saying that
     *         no expense data is available for that year
     */

    public String findHighestSpendingMonth(int year) {
        if (records.isEmpty()) {
            return "No data loaded.";
        }

        double[] monthlyExpenses = new double[12];

        // accumulate expenses (negative amounts) per month

        for (int i = 0; i < records.size(); i++) {
            ReportManager.FinancialRecord rec = records.get(i);

            if (rec.getYear() != year) {
                continue;
            }
            if (rec.isIncome()) {
                // we only care about expenses here
                continue;
            }

            int month = rec.getMonth();
            if (month < 0 || month > 11) {
                continue; // ignore invalid months defensively
            }

            double amount = parseAmount(rec.getAmount());  // negative for expenses
            monthlyExpenses[month] += amount;
        }

        int bestMonthIndex = -1;
        double bestValue = 0.0;

        // find the MOST NEGATIVE total (largest absolute spending)

        for (int m = 0; m < 12; m++) {
            double total = monthlyExpenses[m];
            if (total < 0.0 && (bestMonthIndex == -1 || total < bestValue)) {
                bestValue = total;
                bestMonthIndex = m;
            }
        }

        if (bestMonthIndex == -1) {
            return "No expense data for year " + year + ".";
        }

        String monthName = monthNames[bestMonthIndex];
        // keep the sign (e.g. $-6000.00) so it matches the rest of the reports module
        return monthName + " ($" + String.format("%.2f", bestValue) + ")";
    }

    /**
     * Returns the category with the highest total spending (expenses only)
     * for the specified year.
     *
     * NOTE: Expenses are NEGATIVE. "Top spending category" means the category
     * whose total is the MOST NEGATIVE.
     *
     * @param year the four-digit year to inspect
     * @return a string like {@code "Groceries ($-1234.50)"} or a message saying
     *         that there is no expense data
     */
    public String findTopSpendingCategory(int year) {
        if (records.isEmpty()) {
            return "No data loaded.";
        }

        ArrayList<String> categories = new ArrayList<String>();
        ArrayList<Double> totals = new ArrayList<Double>();

        // accumulate negative expenses per category
        for (int i = 0; i < records.size(); i++) {
            ReportManager.FinancialRecord rec = records.get(i);

            if (rec.getYear() != year) {
                continue;
            }
            if (rec.isIncome()) {
                continue; // only expenses
            }

            String category = rec.getCategory();
            double amount = parseAmount(rec.getAmount()); // negative

            int index = indexOfCategory(categories, category);
            if (index == -1) {
                categories.add(category);
                totals.add(Double.valueOf(amount));
            } else {
                double newTotal = totals.get(index).doubleValue() + amount;
                totals.set(index, Double.valueOf(newTotal));
            }
        }

        if (categories.isEmpty()) {
            return "No expense data by category for year " + year + ".";
        }

        // find the MOST NEGATIVE total (largest absolute spending)
        int bestIndex = -1;
        double bestValue = 0.0;

        for (int i = 0; i < totals.size(); i++) {
            double value = totals.get(i).doubleValue();
            if (value < 0.0 && (bestIndex == -1 || value < bestValue)) {
                bestValue = value;
                bestIndex = i;
            }
        }

        if (bestIndex == -1) {
            // no negative totals means no expenses
            return "No expense data by category for year " + year + ".";
        }

        String topCategory = categories.get(bestIndex);
        return topCategory + " ($" + String.format("%.2f", bestValue) + ")";
    }

    /**
     * Returns a list of months where the balance is negative
     * (income + expenses &lt; 0) for the specified year.
     *
     * NOTE: Since expenses are negative, true balance is income + expenses,
     * which matches ReportManager.generateMonthlySummary.
     *
     * @param year the four-digit year to inspect
     * @return an {@code ArrayList<String>} where each entry looks like
     *         {@code "April: -$45.67"}. If there are no negative months,
     *         the list will be empty.
     */

    public ArrayList<String> listNegativeBalanceMonths(int year) {
        ArrayList<String> result = new ArrayList<String>();

        if (records.isEmpty()) {
            return result;
        }

        double[] monthlyIncome = new double[12];
        double[] monthlyExpenses = new double[12];

        // aggregate income (positive) and expenses (negative)

        for (int i = 0; i < records.size(); i++) {
            ReportManager.FinancialRecord rec = records.get(i);

            if (rec.getYear() != year) {
                continue;
            }

            int month = rec.getMonth();
            if (month < 0 || month > 11) {
                continue;
            }

            double amount = parseAmount(rec.getAmount());

            if (rec.isIncome()) {
                monthlyIncome[month] += amount;      // positive
            } else {
                monthlyExpenses[month] += amount;     // negative
            }
        }

        // balance = income + expenses (because expenses are negative)

        for (int m = 0; m < 12; m++) {
            double balance = monthlyIncome[m] + monthlyExpenses[m];
            if (balance < 0.0) {
                String monthName = monthNames[m];
                String line = monthName + ": -$" + String.format("%.2f", Math.abs(balance));
                result.add(line);
            }
        }

        return result;
    }

    // ---------------------------------------------------------------------
    // Helper methods and data
    // ---------------------------------------------------------------------

    /** Month names reused by several analysis methods. */
    private static final String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    /**
     * Safely parses a string amount to a double. If parsing fails,
     * zero is returned.
     *
     * @param amount string representation of a number
     * @return parsed double value, or 0.0 if parsing fails
     */

    private double parseAmount(String amount) {
        if (amount == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(amount);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    /**
     * Finds the index of a category in the given list.
     *
     * @param categories list of category names
     * @param category   category to search for
     * @return index of the category or -1 if not found
     */

    private int indexOfCategory(ArrayList<String> categories, String category) {
        for (int i = 0; i < categories.size(); i++) {
            String existing = categories.get(i);
            if (existing != null && existing.equals(category)) {
                return i;
            }
        }
        return -1;
    }
}
