
import java.math.BigDecimal;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.nio.file.Path;
import java.time.Month;

/**
 * Manages loading data and orchestrating the generation and presentation of report artifacts
 * Responsibilities include:
 *  Loading income/expense data for a given year
 *  Producing monthly, category, and yearly summaries
 *  Computing overall balance figures
 *  Exporting summaries to CSV and displaying reports on screen
 * 
 * @author Angelo Samir Alvarez 
 * @author Furkan Bilgi
 * @author Chukwuemeka Okwuka
 * @author Omar Piron
 */
public class ReportManager {

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
     * @return an immutable mapping of Month → total amount (expenses can be negative)
     */
    public Map<Integer, BigDecimal> generateMonthlySummary(int year) {
        return java.util.Collections.emptyMap();
    }

    /**
     * Aggregates totals by category label, eg. "Rent", "Groceries".
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return an immutable mapping of Category → total amount
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
     * @param year the four-digit year corresponding to the loaded dataset
     * @return net balance for the year
     */
    public BigDecimal calculateBalance(int year) {
        return BigDecimal.ZERO;
    }

    /**
     * Exports the active summaries to a CSV file at the provided path.
     * @param outputFile target path for the CSV file, parent directories should exist
     */
    public void exportReportToCSV(File outputfile) {
    }

    /**
     * Renders the most recent summaries to the the console 
     * generate summaries prior to calling.
     *
     */
    public void displayReportOnScreen() {
    }


    /**
     * Immutable value object representing yearly totals.
     *
     * @param totalIncome  sum of all income entries for the year
     * @param totalExpenses sum of all expense entries for the year 
     * @param netBalance   {@code totalIncome - totalExpenses} 
     */
    public static final class YearlySummary {
        private final BigDecimal totalIncome;
        private final BigDecimal totalExpenses;
        private final BigDecimal netBalance;

        public YearlySummary(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netBalance) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.netBalance = netBalance;
        }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public BigDecimal getTotalExpenses() { return totalExpenses; }
        public BigDecimal getNetBalance() { return netBalance; }
    }
}

