import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    /** Default constructor for ReportAnalyzer. */
    public ReportAnalyzer() {}

    /**
     * Returns the category with the largest (absolute) expense total.
     *
     * @param categoryTotals mapping of category name to total amount
     *                       (expenses represented as positive magnitudes)
     * @return the category label with the highest expense, or {@code null} if none
     */
    public String calculateHighestExpenseCategory(Map<String, BigDecimal> categoryTotals) {
        return null;
    }

    /**
     * Computes the average monthly total given a map of month totals.
     *
     * @param monthlyTotals mapping of month index (1–12) to total amount
     * @return the arithmetic mean of the provided month totals
     */
    public BigDecimal calculateMonthlyAverage(Map<Integer, BigDecimal> monthlyTotals) {
        return BigDecimal.ZERO;
    }

    /**
     * Derives a per-month savings series defined as {@code income - expenses} for each month.
     *
     * @param monthlyIncome   mapping of month index (1–12) to total income
     * @param monthlyExpenses mapping of month index (1–12) to total expenses
     * @return an ordered list, Jan..Dec, of savings values; missing months assumed zero
     */
    public List<BigDecimal> calculateSavingsTrend(Map<Integer, BigDecimal> monthlyIncome,
                                                  Map<Integer, BigDecimal> monthlyExpenses) {
        return java.util.Collections.emptyList();
    }

    /**
     * Compares two periods (e.g., period A vs period B) for the same set of categories.
     *
     * @param periodA mapping of category name to total amount for period A
     * @param periodB mapping of category name to total amount for period B
     * @return a mapping of category name to the difference {@code (periodB - periodA)}
     */
    public Map<String, BigDecimal> generateComparisonReport(Map<String, BigDecimal> periodA,
                                                            Map<String, BigDecimal> periodB) {
        return java.util.Collections.emptyMap();
    }
}
