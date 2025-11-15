import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Manages loading data and orchestrating the generation and presentation of report artifacts.
 * Responsibilities include:
 *  - Loading income/expense data for a given year
 *  - Producing monthly, category, and yearly summaries
 *  - Computing overall balance figures
 *  - Exporting summaries to CSV and displaying reports on screen
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
        // Implementation to load data for the given year goes here
    }

    /**
     * Aggregates totals by month for the previously loaded year.
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return an immutable list of strings
     */
    public List<String> generateMonthlySummary(int year) {
        return Collections.emptyList();
    }

    /**
     * Aggregates totals by category label, e.g. "Rent", "Groceries".
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return an immutable list of strings
     */
    public List<String> generateCategorySummary(int year) {
        return Collections.emptyList();
    }

    /**
     * Produces year-wide totals for income, expenses, and net balance.
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return a YearlySummary object containing key totals as strings
     */
    public YearlySummary generateYearlySummary(int year) {
        return new YearlySummary("0.00", "0.00", "0.00");
    }

    /**
     * Calculates the overall balance for the specified year (totalIncome - totalExpenses).
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return net balance for the year
     */
    public String calculateBalance(int year) {
        return "0.00";
    }

    /**
     * Exports the active summaries to a CSV file at the provided path.
     *
     * @param outputFile target path for the CSV file, parent directories should exist
     */
    public void exportReportToCSV(File outputFile) {
        // Implementation to export report data to CSV goes here
    }

    /**
     * Renders the most recent summaries to the console.
     * Call one of the generate*Summary methods prior to calling this.
     */
    public void displayReportOnScreen() {
        // Implementation to print report summaries to the console goes here
    }

    /**
     * Immutable value object representing yearly totals.
     */
    public static final class YearlySummary {
        private final String totalIncome;
        private final String totalExpenses;
        private final String netBalance;

        /**
         * @param totalIncome   sum of all income entries for the year
         * @param totalExpenses sum of all expense entries for the year
         * @param netBalance    totalIncome - totalExpenses
         */
        public YearlySummary(String totalIncome, String totalExpenses, String netBalance) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.netBalance = netBalance;
        }

        /**
         * @return total income for the year
         */
        public String getTotalIncome() {
            return totalIncome;
        }

        /**
         * @return total expenses for the year
         */
        public String getTotalExpenses() {
            return totalExpenses;
        }

        /**
         * @return net balance for the year
         */
        public String getNetBalance() {
            return netBalance;
        }
    }
}
