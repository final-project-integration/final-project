import java.math.BigDecimal;
import java.io.File;
import java.util.Map;

/**
 * Manages loading data and orchestrating the generation and presentation of report artifacts.
 * Responsibilities include:
 * <ul>
 *   <li>Loading income/expense data for a given year</li>
 *   <li>Producing monthly, category, and yearly summaries</li>
 *   <li>Computing overall balance figures</li>
 *   <li>Exporting summaries to CSV and displaying reports on screen</li>
 * </ul>
 *
 * @author Angelo Samir Alvarez
 * @author Furkan Bilgi
 * @author Chukwuemeka Okwuka
 * @author Omar Piron
 */
public class ReportManager {
    /** Default constructor for ReportManager. */
public ReportManager() {}


    /**
     * Loads all income and expense records for the specified year into memory
     * so that summaries can be produced without additional I/O.
     *
     * @param year the four-digit year to load (e.g., 2025)
     */
    public void loadYearlyData(int year) {
    }

    /**
     * Aggregates totals by month for the previously loaded year.
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return an immutable mapping of month index (1–12) to total amount
     */
    public Map<Integer, BigDecimal> generateMonthlySummary(int year) {
        return java.util.Collections.emptyMap();
    }

    /**
     * Aggregates totals by category label (e.g., "Rent", "Groceries").
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return an immutable mapping of category name to total amount
     */
    public Map<String, BigDecimal> generateCategorySummary(int year) {
        return java.util.Collections.emptyMap();
    }

    /**
     * Produces year-wide totals for income, expenses, and net balance.
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return a {@link YearlySummary} struct containing key totals
     */
    public YearlySummary generateYearlySummary(int year) {
        return new YearlySummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Calculates the overall balance for the specified year (totalIncome - totalExpenses).
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return net balance for the year
     */
    public BigDecimal calculateBalance(int year) {
        return BigDecimal.ZERO;
    }

    /**
     * Exports the active summaries to a CSV file at the provided path.
     *
     * @param outputfile target path for the CSV file; parent directories should exist
     */
    public void exportReportToCSV(File outputfile) {
    }

    /**
     * Renders the most recent summaries to the console.
     * Call the summary generation methods before invoking this.
     */
    public void displayReportOnScreen() {
    }

    /**
     * Immutable value object representing yearly totals.
     */
    public static final class YearlySummary {
        private final BigDecimal totalIncome;
        private final BigDecimal totalExpenses;
        private final BigDecimal netBalance;

        /**
         * Constructs a yearly summary with income, expenses, and net balance.
         *
         * @param totalIncome   sum of all income entries for the year
         * @param totalExpenses sum of all expense entries for the year
         * @param netBalance    {@code totalIncome - totalExpenses}
         */
        public YearlySummary(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netBalance) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.netBalance = netBalance;
        }

        /** Gets total income for the year.
         *  @return total income for the year
         */

        public BigDecimal getTotalIncome() { return totalIncome; }

        /** Gets total expenses for the year.
         *  @return total expenses for the year
         */
        public BigDecimal getTotalExpenses() { return totalExpenses; }

        /** Gets net balance for the year.
         *  @return net balance for the year
         */
        public BigDecimal getNetBalance() { return netBalance; }
    }
}
