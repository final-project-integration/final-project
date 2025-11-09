
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Month;

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
     * @param categoryTotals mapping of Category → total amount (expenses as positive magnitude)
     * @return the category label with the highest expense, or empty if none
     * @author Angelo Samir Alvarez 
     */
    public String calculateHighestExpenseCategory(Map<String, BigDecimal> categoryTotals) {
        return null;
    }

    /**
     * Computes the average monthly total given a map of month totals.
     *
     * @param monthlyTotals mapping of Month → total amount
     * @return the arithmetic mean of the provided month totals
     * @author Angelo Samir Alvarez 
     */
    public BigDecimal calculateMonthlyAverage(Map<Integer, BigDecimal> monthlyTotals) {
        return BigDecimal.ZERO;
    }

    /**
     * Derives a per-month savings series defined as 'income - expenses' for each month.
     *
     * @param monthlyIncome  mapping of Month → total income
     * @param monthlyExpenses mapping of Month → total expenses
     * @return an ordered list, Jan..Dec, of savings values; missing months are assumed zero
     * @author Angelo Samir Alvarez 
     */
    public List<BigDecimal> calculateSavingsTrend(Map<Integer, BigDecimal> monthlyIncome,
                                                  Map<Integer, BigDecimal> monthlyExpenses) {
         return java.util.Collections.emptyList();
    }

    /**
     * Compares two periods (e.g., year a vs year b) for the same set of categories.
     *
     * @param periodA mapping of Category → total amount for period a
     * @param periodB mapping of Category → total amount for period b
     * @return a mapping of Category : (periodB - periodA)
     * @author Angelo Samir Alvarez 
     */
    public Map<String, BigDecimal> generateComparisonReport(Map<String, BigDecimal> periodA,
                                                            Map<String, BigDecimal> periodB) {
        return java.util.Collections.emptyMap();
    }
}

