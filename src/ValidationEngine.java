package validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates top-level validations and result aggregation.
 * 
 * @author Mayesha Fahmida
 * @version 11/18/2025
 */
public class ValidationEngine {

    private final DataTypeValidator dataValidator;

    /**
     * Constructor initializes the data type validator.
     */
    public ValidationEngine() {
        this.dataValidator = new DataTypeValidator();
    }

    /**
     * Validate generic user input (emptiness, length, allowed chars, etc.).
     * 
     * @param fieldName logical field name (e.g., "amount", "notes")
     * @param value     raw input string
     * @return result for this field
     */
    public ValidationResult validateUserInput(String fieldName, String value) {
        ValidationResult result = new ValidationResult();

        if (fieldName == null) {
            result.addError("Field name cannot be null.");
            return result;
        }

        if (!dataValidator.isNonEmpty(value)) {
            result.addError(fieldName + " cannot be empty.");
            return result;
        }

        if (value.trim().length() > 500) {
            result.addWarning(fieldName + " is very long (over 500 characters).");
        }

        return result;
    }

    /**
     * Validate a transaction row including amount, date, category, and description.
     * Currently only checks for null.
     *
     * @param transaction transaction DTO placeholder
     * @return aggregated result
     */
    public ValidationResult validateTransaction(Object transaction) {
        ValidationResult result = new ValidationResult();

        if (transaction == null) {
            result.addError("Transaction cannot be null.");
            return result;
        }

        // Later, use reflection or DTO getters (amount, date, category, etc.)
        return result;
    }

    /**
     * Validate a budget line item (category, planned amount, time window).
     * 
     * @param budgetItem budget item DTO placeholder
     * @return result for the budget item
     */
    public ValidationResult validateBudgetLineItem(Object budgetItem) {
        ValidationResult result = new ValidationResult();

        if (budgetItem == null) {
            result.addError("Budget item cannot be null.");
            return result;
        }

        // Hook for future rules.
        return result;
    }

    /**
     * Validate report criteria (date range, category filters, totals vs details).
     * 
     * @param reportCriteria report criteria DTO placeholder
     * @return result for report criteria
     */
    public ValidationResult validateReportCriteria(Object reportCriteria) {
        ValidationResult result = new ValidationResult();

        if (reportCriteria == null) {
            result.addError("Report criteria cannot be null.");
            return result;
        }

        // Hook for future rules.
        return result;
    }

    /**
     * Combine multiple ValidationResult objects into one rollup.
     *
     * Looks at the string form of each message to guess severity.
     * (Because ValidationResult doesn't expose internal messages yet.)
     *
     * @param results array of results to aggregate
     * @return aggregated result
     */
    public ValidationResult aggregateResults(ValidationResult... results) {
        ValidationResult combined = new ValidationResult();

        if (results == null) {
            return combined;
        }

        for (ValidationResult current : results) {
            if (current == null) {
                continue;
            }
            List<String> messages = current.getMessages();
            for (String message : messages) {
                // Infer severity from the string form.
                if (message.contains("[ERROR]")) {
                    combined.addError(message);
                } else if (message.contains("[WARNING]")) {
                    combined.addWarning(message);
                } else if (current.hasErrors()) {
                    combined.addError(message);
                } else {
                    combined.addWarning(message);
                }
            }
        }

        return combined;
    }
}