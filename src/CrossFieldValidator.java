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

    /**
     * Acceptable tolerance for floating-point comparisons (0.01).
     */
    private static final double TOLERANCE = 0.01;

    /**
     * Internal reflection helper to invoke a getter method on an arbitrary object.
     *
     * @param obj        the object to inspect
     * @param methodName the name of the method to invoke (e.g., "getAmount")
     * @param returnType the expected class of the return value
     * @param <T>        the type to cast the result to
     * @return the value returned by the method invocation
     * @throws IllegalArgumentException if the method does not exist or cannot be
     *                                  invoked (wraps ReflectiveOperationException)
     */
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
     *
     * @param startDate the start date string (Expected format: YYYY-MM-DD)
     * @param endDate   the end date string (Expected format: YYYY-MM-DD)
     * @return ValidationResult.ok() if valid, or error if invalid
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
     * Validate that the sum of category planned amounts matches the budget total
     * within a specific tolerance.
     * <p>
     * Requires the following methods on the input objects:
     * <ul>
     * <li>Budget Object: {@code double getTotal()},
     * {@code List<Object> getCategories()}</li>
     * <li>Category Object: {@code Double getPlannedAmount()}</li>
     * </ul>
     *
     * @param budget the budget object to validate
     * @return ValidationResult indicating if the totals match
     * @throws IllegalArgumentException if the required methods are missing
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
     * <p>
     * Requires the following methods on the transaction object:
     * <ul>
     * <li>{@code String getType()} (e.g., "INCOME" or "EXPENSE")</li>
     * <li>{@code Double getAmount()}</li>
     * </ul>
     *
     * @param transaction the transaction object
     * @return ValidationResult.ok() if signs are correct
     * @throws IllegalArgumentException if the required methods are missing
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
     * <p>
     * Requires the following methods on the transaction objects:
     * <ul>
     * <li>{@code String getDate()}</li>
     * <li>{@code Double getAmount()}</li>
     * <li>{@code String getMerchant()}</li>
     * <li>{@code String getCategory()}</li>
     * </ul>
     *
     * @param transactions list of transaction objects
     * @return ValidationResult with warning details if duplicates are found
     * @throws IllegalArgumentException if the required methods are missing
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
     * <p>
     * Accepts either:
     * <ul>
     * <li>A {@code String} (the category name directly)</li>
     * <li>An {@code Object} containing the method {@code String getName()}</li>
     * </ul>
     *
     * @param categoryObj the category string or object to validate
     * @return ValidationResult indicating if hierarchy format is valid
     * @throws IllegalArgumentException if object is not a String and lacks
     *                                  getName()
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

    /**
     * Cross-check a transaction's year against a known file year.
     * Intended for post-CSV DTO validation.
     * <p>
     * Requires {@code String getDate()} on the transaction object.
     *
     * @param transaction the transaction object
     * @param fileYear    the year expected from the file
     * @return ValidationResult indicating if the years match
     * @throws IllegalArgumentException if the required method is missing
     */
    public ValidationResult validateTransactionYear(Object transaction, int fileYear) {
        if (transaction == null) {
            return ValidationResult.error("Transaction object is null.");
        }

        String dateStr = get(transaction, "getDate", String.class); // e.g. "12/30/2024"
        DataTypeValidator dt = new DataTypeValidator();

        if (!dt.isValidDate(dateStr)) {
            return ValidationResult.error("Invalid transaction date: " + dateStr);
        }

        int year = dt.extractYear(dateStr);
        if (year != fileYear) {
            return ValidationResult.error(
                    "Transaction year " + year + " does not match file year " + fileYear + ".");
        }

        return ValidationResult.ok();
    }
}