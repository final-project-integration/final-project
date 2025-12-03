import java.util.List;

/**
 * Coordinates top-level validations and result aggregation.
 * 
 * @author
 *         Mayesha Fahmida
 * @version 11/18/2025
 */
public class ValidationEngine {

    private final DataTypeValidator dataValidator;

    /**
     * Example allowed categories list.
     */
    private static final List<String> ALLOWED_CATEGORIES = List.of(
            "Compensation",
            "Food",
            "Home",
            "Transportation",
            "Entertainment",
            "Health",
            "Savings",
            "Other");

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

    /**
     * Validate a CSV file represented as a list of lines.
     *
     * Rules:
     * - Header must be exactly: Date,Category,Amount
     * - No extra or missing columns.
     * - Every data row must have non-empty Date, Category, Amount.
     * - Date must be valid (MM/DD/YYYY, real date).
     * - All dates must be in the same year.
     * - Category must be in the allowed list.
     * - If ANY row fails, the entire file is rejected (hasErrors() == true).
     *
     * @param lines all lines of the CSV file (including header)
     * @return ValidationResult describing all problems found
     */
    public ValidationResult validateCsvLines(List<String> lines) {
        ValidationResult result = new ValidationResult();

        if (lines == null || lines.isEmpty()) {
            result.addError("CSV file is empty.");
            return result;
        }

        // ----- 1) Header validation (Issues #3 and #4) -----
        String header = lines.get(0).trim();
        String expectedHeader = "Date,Category,Amount";

        if (!header.equals(expectedHeader)) {
            result.addError("Invalid header. Expected exactly: " + expectedHeader
                    + " but found: " + header);
        }

        // Keep track of year consistency (Issue #6)
        Integer fileYear = null;

        // ----- 2) Row-by-row validation -----
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            // Optionally ignore completely blank lines
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] fields = line.split(",", -1); // -1 keeps empty trailing fields
            int lineNumber = i + 1; // human-readable (header is line 1)

            if (fields.length != 3) {
                result.addError("Line " + lineNumber
                        + ": Expected exactly 3 columns (Date,Category,Amount) but found "
                        + fields.length + ".");
                continue;
            }

            String dateField = fields[0].trim();
            String categoryField = fields[1].trim();
            String amountField = fields[2].trim();

            // ----- Missing required fields (Issue #2) -----
            boolean missing = false;

            if (!dataValidator.isNonEmpty(dateField)) {
                result.addError("Line " + lineNumber + ": Date is required and cannot be empty.");
                missing = true;
            }
            if (!dataValidator.isNonEmpty(categoryField)) {
                result.addError("Line " + lineNumber + ": Category is required and cannot be empty.");
                missing = true;
            }
            if (!dataValidator.isNonEmpty(amountField)) {
                result.addError("Line " + lineNumber + ": Amount is required and cannot be empty.");
                missing = true;
            }

            // If any required field on this line is empty, skip further checks on this
            // line.
            if (missing) {
                continue;
            }

            // ----- Date validity + year consistency (Issues #1 and #6) -----
            if (!dataValidator.isValidDate(dateField)) {
                result.addError("Line " + lineNumber + ": Invalid date '" + dateField
                        + "'. Expected a real date in MM/DD/YYYY format.");
            } else {
                int year = dataValidator.extractYear(dateField);
                if (fileYear == null) {
                    fileYear = year;
                } else if (year != fileYear) {
                    result.addError("Line " + lineNumber
                            + ": Year " + year + " does not match file year " + fileYear + ".");
                }
            }

            // ----- Category validity (Issue #5) -----
            boolean categoryOk = false;
            for (String allowed : ALLOWED_CATEGORIES) {
                if (allowed.equalsIgnoreCase(categoryField)) {
                    categoryOk = true;
                    break;
                }
            }
            if (!categoryOk) {
                result.addError("Line " + lineNumber
                        + ": Invalid category '" + categoryField
                        + "'. Category must be one of the approved line-item categories.");
            }

            // ----- Amount basic validation -----
            if (!dataValidator.isNumeric(amountField)) {
                result.addError("Line " + lineNumber
                        + ": Amount '" + amountField + "' is not a valid integer number.");
            }
        }

        return result;
    }
}