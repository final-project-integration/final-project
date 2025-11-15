import java.util.Collections;
import java.util.List;

/**
 * Analyzer provides analytical computations over already-aggregated report data,
 * such as identifying top categories and computing trends.
 * 
 * @author Angelo Samir Alvarez
 * @author Furkan Bilgi
 * @author Chukwuemeka Okwuka
 * @author Omar Piron
 */
public class ReportAnalyzer {

    /**
     * Returns the category with the largest (absolute) expense total.
     *
     * @param categoryLines list of strings formatted as "CategoryName:Amount"
     * @return the category name with the highest amount, or null if the list is empty
     */
    public String calculateHighestExpenseCategory(List<String> categoryLines) {
        return null;
    }

    /**
     * Computes the average monthly total given a list of month totals.
     *
     * @param monthlyLines list of strings representing monthly totals
     * @return the arithmetic mean of the provided month totals
     */
    public String calculateMonthlyAverage(List<String> monthlyLines) {
        return "0.00";
    }

    /**
     * Derives a per-month savings series defined as 'income - expenses' for each month.
     *
     * @param monthlyIncomeLines  list of income lines
     * @param monthlyExpenseLines list of expense lines
     * @return an ordered list of savings lines
     */
    public List<String> calculateSavingsTrend(List<String> monthlyIncomeLines,
                                              List<String> monthlyExpenseLines) {
        return Collections.emptyList();
    }

    /**
     * Compares two periods (e.g., year A vs year B) for the same set of categories.
     *
     * @param periodALines list of category totals for period A
     * @param periodBLines list of category totals for period B
     * @return a list of difference lines
     */
    public List<String> generateComparisonReport(List<String> periodALines,
                                                 List<String> periodBLines) {
        return Collections.emptyList();
    }
}
