import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cross-field and higher-level validation rules for PFM.
 *
 * Uses reflection to avoid hard dependency on specific DTO classes.
 * Returns ValidationResult with static helpers.
 *
 * @author Atai
 */
public class CrossFieldValidator {

    private static final double TOLERANCE = 0.01;

    @SuppressWarnings("unchecked")
    private <T> T get(Object obj, String methodName, Class<T> returnType) {
        try {
            return (T) obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("DTO missing method: " + methodName, e);
        }
    }

    /**
     * Validate that startDate <= endDate.
     */
    public ValidationResult validateDateRange(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            return ValidationResult.error("Start date or end date is missing.");
        }

        try {
            // assumes ISO format, e.g., "2025-11-18"
            LocalDate s = LocalDate.parse(startDate);
            LocalDate e = LocalDate.parse(endDate);

            if (s.isAfter(e)) {
                return ValidationResult.error("Start date cannot be after end date.");
            }

            return ValidationResult.ok();

        } catch (DateTimeParseException ex) {
            return ValidationResult.error("Invalid date format: " + ex.getMessage());
        }
    }

    /**
     * Validate that the sum of category planned amounts matches the budget total.
     */
    @SuppressWarnings("unchecked")
    public ValidationResult validateBudgetBalance(Object budget) {
        if (budget == null) {
            return ValidationResult.error("Budget object is null.");
        }

        double total = get(budget, "getTotal", Double.class);
        List<Object> categories = get(budget, "getCategories", List.class);

        double sum = 0.0;
        for (Object cat : categories) {
            Double planned = get(cat, "getPlannedAmount", Double.class);
            sum += planned;
        }

        if (Math.abs(sum - total) > TOLERANCE) {
            return ValidationResult.error(
                    "Budget total (" + total + ") does not match sum of categories (" + sum + ").");
        }

        return ValidationResult.ok();
    }

    /**
     * Validate sign of amount based on transaction type.
     */
    public ValidationResult validateIncomeVsExpense(Object transaction) {
        if (transaction == null) {
            return ValidationResult.error("Transaction object is null.");
        }

        String type = get(transaction, "getType", String.class); // e.g. "INCOME" / "EXPENSE"
        Double amount = get(transaction, "getAmount", Double.class);

        if ("INCOME".equalsIgnoreCase(type) && amount < 0) {
            return ValidationResult.error("Income amount cannot be negative.");
        }

        if ("EXPENSE".equalsIgnoreCase(type) && amount > 0) {
            return ValidationResult.error("Expense amount cannot be positive.");
        }

        return ValidationResult.ok();
    }

    /**
     * Detect duplicate transactions based on (date, amount, merchant, category).
     */
    public ValidationResult detectDuplicateTransactions(List<Object> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return ValidationResult.ok();
        }

        Map<String, List<Object>> groups = new HashMap<>();

        for (Object tx : transactions) {
            String key = get(tx, "getDate", String.class)
                    + "|" + get(tx, "getAmount", Double.class)
                    + "|" + get(tx, "getMerchant", String.class)
                    + "|" + get(tx, "getCategory", String.class);

            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(tx);
        }

        List<String> duplicates = new ArrayList<>();
        for (Map.Entry<String, List<Object>> entry : groups.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicates.add(entry.getKey());
            }
        }

        if (!duplicates.isEmpty()) {
            return ValidationResult.withDetails(
                    false,
                    "Duplicate transactions detected.",
                    duplicates);
        }

        return ValidationResult.ok();
    }

    /**
     * Validate categories like "Food:Groceries" or "Transport:Subway".
     */
    public ValidationResult validateCategoryHierarchy(Object categoryObj) {
        if (categoryObj == null) {
            return ValidationResult.error("Category is null.");
        }

        String category = (categoryObj instanceof String)
                ? (String) categoryObj
                : get(categoryObj, "getName", String.class);

        if (!category.contains(":")) {
            return ValidationResult.ok(); // single-level category is fine
        }

        String[] parts = category.split(":");
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            return ValidationResult.error("Invalid category hierarchy: " + category);
        }

        return ValidationResult.ok();
    }
}