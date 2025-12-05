import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Coordinates top-level validations and result aggregation.
 *
 * @author Mayesha Fahmida
 * @version 11/18/2025
 */
public class ValidationEngine {

    private final DataTypeValidator dataValidator;

    /**
     * Example allowed categories list.
     */
    private static final List<String> ALLOWED_CATEGORIES = List.of(
            "Compensation",
            "Professional Services",
            "Food",
            "Home",
            "Transportation",
            "Entertainment",
            "Health",
            "Savings",
            "Other");

    /**
     * Categories treated as income (amounts should be non-negative).
     */
    private static final List<String> INCOME_CATEGORIES = List.of(
            "Compensation",
            "Savings");

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
        return result;
    }

    /**
     * Combine multiple ValidationResult objects into one rollup.
     * <p>
     * UPDATED: Now uses the native {@link ValidationResult#merge(ValidationResult)}
     * method instead of manual string parsing.
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
            combined.merge(current);
        }

        return combined;
    }

    /**
     * Extract a 4-digit year from the file name (e.g., "PFM_2024_data.csv").
     *
     * @param filename name of the CSV file
     * @return year if found (e.g., 2024), or null if not detected
     */
    private Integer extractYearFromFilename(String filename) {
        if (filename == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("20\\d{2}");
        Matcher matcher = pattern.matcher(filename);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return null;
    }

    /**
     * Validate a CSV file represented as a list of lines, also cross-checking the
     * year encoded in the file name (if any) against the dates in the file.
     *
     * @param fileName name of the CSV file (e.g., "PFM_2024_data.csv")
     * @param lines    all lines of the CSV file (including header)
     * @return ValidationResult describing all problems found
     */
    public ValidationResult validateCsvLines(String fileName, List<String> lines) {
        // Reuse existing rules
        ValidationResult result = validateCsvLines(lines);

        // Only attempt filename-vs-CSV year cross-check if the basic CSV is valid.
        if (result.hasErrors()) {
            return result;
        }

        // Infer year from first valid date in the data rows
        Integer csvYear = null;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] fields = line.split(",", -1);
            if (fields.length != 3) {
                continue;
            }
            String dateField = fields[0].trim();
            if (dataValidator.isValidDate(dateField)) {
                csvYear = dataValidator.extractYear(dateField);
                break;
            }
        }

        Integer fileYear = extractYearFromFilename(fileName);

        if (csvYear != null && fileYear != null && !csvYear.equals(fileYear)) {
            result.addError("File name year (" + fileYear
                    + ") does not match CSV date year (" + csvYear + ").");
        }

        return result;
    }

    /**
     * Validate a CSV file represented as a list of lines.
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

        // ----- 1) Header validation -----
        String header = lines.get(0).trim();
        String expectedHeader = "Date,Category,Amount";

        if (!header.equals(expectedHeader)) {
            result.addError("Invalid CSV header. Expected exactly: '" + expectedHeader
                    + "' but found: '" + header + "'. "
                    + "Please ensure the header has exactly these three columns with correct spelling and no extra columns.");
        }

        // Keep track of year consistency
        Integer fileYear = null;

        // ----- 2) Row-by-row validation -----
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            // Ignore completely blank lines
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] fields = line.split(",", -1);
            int lineNumber = i + 1; // human-readable (header is line 1)

            if (fields.length != 3) {
                String columnIssue = fields.length > 3 ? "extra columns" : "missing columns";
                result.addError("Line " + lineNumber
                        + ": Expected exactly 3 columns (Date,Category,Amount) but found "
                        + fields.length + " columns (" + columnIssue + "). "
                        + "Each row must have exactly Date, Category, and Amount with no extra fields.");
                continue;
            }

            String dateField = fields[0].trim();
            String categoryField = fields[1].trim();
            String amountField = fields[2].trim();

            // ----- Missing required fields -----
            boolean missing = false;

            if (!dataValidator.isNonEmpty(dateField)) {
                result.addError("Line " + lineNumber + ": Date field is required and cannot be empty. "
                        + "Please provide a date in MM/DD/YYYY format.");
                missing = true;
            }
            if (!dataValidator.isNonEmpty(categoryField)) {
                result.addError("Line " + lineNumber + ": Category field is required and cannot be empty. "
                        + "Please provide a valid category from the approved list.");
                missing = true;
            }
            if (!dataValidator.isNonEmpty(amountField)) {
                result.addError("Line " + lineNumber + ": Amount field is required and cannot be empty. "
                        + "Please provide a numeric amount.");
                missing = true;
            }

            if (missing) {
                continue;
            }

            // ----- Date validity + year consistency -----
            if (!dataValidator.isValidDate(dateField)) {
                result.addError("Line " + lineNumber + ": Invalid date '" + dateField
                        + "'. Date must be in MM/DD/YYYY format with valid month (01-12), "
                        + "day (01-31 depending on month), and year. "
                        + "Examples of invalid dates: 13/40/2024 (month 13 doesn't exist), "
                        + "02/30/2024 (Feb doesn't have 30 days), 2024/01/01 (wrong order).");
            } else {
                int year = dataValidator.extractYear(dateField);
                if (fileYear == null) {
                    fileYear = year;
                } else if (year != fileYear) {
                    result.addError("Line " + lineNumber
                            + ": Year " + year + " does not match file year " + fileYear + ". "
                            + "All transactions in the CSV must be from the same year.");
                }
            }

            // ----- Category validity -----
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
                        + "'. Category must be one of the approved categories: "
                        + String.join(", ", ALLOWED_CATEGORIES) + ".");
            }

            // ----- Amount basic validation + sign consistency -----
            // Note: Currently enforcing integers via DataTypeValidator.isNumeric
            if (!dataValidator.isNumeric(amountField)) {
                result.addError("Line " + lineNumber
                        + ": Amount '" + amountField + "' is not a valid integer number. "
                        + "Please provide a whole number (positive for income, negative for expenses).");
            } else {
                try {
                    int amount = Integer.parseInt(amountField.trim());

                    boolean isIncome = INCOME_CATEGORIES.stream()
                            .anyMatch(cat -> cat.equalsIgnoreCase(categoryField));

                    // Sign convention: income >= 0, expenses <= 0
                    if (isIncome && amount < 0) {
                        result.addError("Line " + lineNumber + ": Income category '" + categoryField
                                + "' must not have a negative amount (" + amount + "). "
                                + "Income amounts should be positive or zero.");
                    } else if (!isIncome && amount > 0) {
                        result.addError("Line " + lineNumber + ": Expense category '" + categoryField
                                + "' should have a negative amount, but found " + amount + ". "
                                + "Expense amounts should be negative (e.g., -50 for $50 spent).");
                    }
                } catch (NumberFormatException e) {
                    // Should be caught by isNumeric check, but safe guard here
                    result.addError("Line " + lineNumber + ": Amount format error.");
                }
            }
        }

        return result;
    }
}