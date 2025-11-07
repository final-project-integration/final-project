package validation;

/**
 * Coordinates top-level validations and result aggregation.
 * 
 * @author Mayesha Fahmida
 */

public class ValidationEngine {

    /**
     * Validate generic user input (emptiness, length, allowed chars, etc.).
     * 
     * @param fieldName logical field name (e.g., "amount", "notes")
     * @param value     raw input string
     * @return result for this field
     */

    public ValidationResult validateUserInput(String fieldName, String value) {
        return null;
    }

    /**
     * Validate a transaction row including amount, date, category, and description.
     * 
     * @param transaction transaction DTO placeholder
     * @return aggregated result
     */

    public ValidationResult validateTransaction(Object transaction) {
        return null;
    }

    /**
     * Validate a budget line item (category, planned amount, time window).
     * 
     * @param budgetItem budget item DTO placeholder
     * @return result for the budget item
     */

    public ValidationResult validateBudgetLineItem(Object budgetItem) {
        return null;
    }

    /**
     * Validate report criteria (date range, category filters, totals vs details).
     * 
     * @param reportCriteria report criteria DTO placeholder
     * @return result for report criteria
     */

    public ValidationResult validateReportCriteria(Object reportCriteria) {
        return null;
    }

    /**
     * Combine multiple ValidationResult objects into one rollup.
     * 
     * @param results array of results to aggregate
     * @return aggregated result
     */

    public ValidationResult aggregateResults(ValidationResult... results) {
        return null;
    }
}
