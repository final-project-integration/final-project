package validation;

import java.util.List;
import java.util.Collections;

/**
 * Higher-level rules that depend on multiple fields.
 * 
 * @author Ataik
 */
public class CrossFieldValidator {

    /**
     * Ensure startDate {@literal <=} endDate and both valid (format + chronology).
     * 
     * @param startDate the starting date to validate (string/DTO placeholder)
     * @param endDate the ending date to validate (string/DTO placeholder)
     * @return validation result with error if range invalid
     */
    public ValidationResult validateDateRange(String startDate, String endDate) {
        return null;
    }

    /**
     * Checks that the sum of category planned amounts equals the budget total (within tolerance).
     * 
     * @param budget object representing a budget (DTO placeholder)
     * @return validation result
     */
    public ValidationResult validateBudgetBalance(Object budget) {
        return null;
    }

    /**
     * Verifies income categories are not negative and expenses are not positive.
     * 
     * @param transaction object representing a transaction (DTO placeholder)
     * @return validation result
     */
    public ValidationResult validateIncomeVsExpense(Object transaction) {
        return null;
    }

    /**
     * Detects duplicate transactions (same date, amount, merchant, category).
     * 
     * @param transactions list (DTO placeholder)
     * @return validation result with duplicate findings
     */
    public ValidationResult detectDuplicateTransactions(List<Object> transactions) {
        return null;
    }

    /**
     * Validates that a category exists and respects hierarchy (e.g., "Food:Groceries").
     * 
     * @param category category object/string (DTO placeholder)
     * @return validation result
     */
    public ValidationResult validateCategoryHierarchy(Object category) {
        return null;
    }
}
