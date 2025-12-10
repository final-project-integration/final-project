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
            "compensation",
            "professional services",
            "food",
            "home",
            "utilities",
            "transportation",
            "entertainment",
            "appearance",
            "work",
            "education",
            "allowance",
            "investments",
            "other");

    /**
     * Categories treated as income (amounts should be non-negative).
     */
    private static final List<String> INCOME_CATEGORIES = List.of(
            "compensation",
            "investments",
            "allowance",
            "other");
    private static final List<String> FLEXIBLE_CATEGORIES = List.of(
            "other");

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
     * UPDATED: Now uses row-level tracking to support partial imports.
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
            // Header failure is usually fatal for the whole file, but we continue
            // to allow row processing if the user forces it, or we just return here.
            // Usually header errors block everything, but we'll let the loop run.
        }

        // Keep track of year consistency
        Integer fileYear = null;

        // ----- 2) Row-by-row validation -----
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1; // human-readable (header is line 1)

            // Ignore completely blank lines
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] fields = line.split(",", -1);

            // CHECK 1: Column Count
            if (fields.length != 3) {
                String columnIssue = fields.length > 3 ? "extra columns" : "missing columns";
                result.addError("Line " + lineNumber
                        + ": Expected exactly 3 columns (Date,Category,Amount) but found "
                        + fields.length + " columns (" + columnIssue + "). "
                        + "Each row must have exactly Date, Category, and Amount with no extra fields.",
                        lineNumber); // <--- Passed lineNumber

                // If structure is broken, we usually can't parse the rest of the row safely.
                // markRowValid will NOT run because addError sets the status to false.
                continue;
            }

            String dateField = fields[0].trim();
            String categoryField = fields[1].trim();
            String amountField = fields[2].trim();

            // ----- Missing required fields -----
            boolean missing = false;

            if (!dataValidator.isNonEmpty(dateField)) {
                result.addError("Line " + lineNumber + ": Date field is required and cannot be empty. "
                        + "Please provide a date in MM/DD/YYYY format.", lineNumber);
                missing = true;
            }
            if (!dataValidator.isNonEmpty(categoryField)) {
                result.addError("Line " + lineNumber + ": Category field is required and cannot be empty. "
                        + "Please provide a valid category from the approved list.", lineNumber);
                missing = true;
            }
            if (!dataValidator.isNonEmpty(amountField)) {
                result.addError("Line " + lineNumber + ": Amount field is required and cannot be empty. "
                        + "Please provide a numeric amount.", lineNumber);
                missing = true;
            }

            if (missing) {
                // Determine validity at end of loop
                continue;
            }

            // ----- Date validity + year consistency -----
            if (!dataValidator.isValidDate(dateField)) {
                result.addError("Line " + lineNumber + ": Invalid date '" + dateField
                        + "'. Date must be in MM/DD/YYYY format.", lineNumber);
            } else {
                int year = dataValidator.extractYear(dateField);
                if (fileYear == null) {
                    fileYear = year;
                } else if (year != fileYear) {
                    result.addError("Line " + lineNumber
                            + ": Year " + year + " does not match file year " + fileYear + ". "
                            + "All transactions in the CSV must be from the same year.", lineNumber);
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
                        + String.join(", ", ALLOWED_CATEGORIES) + ".", lineNumber);
            }

            // ----- Amount basic validation + sign consistency -----
            if (!dataValidator.isNumeric(amountField)) {
                result.addError("Line " + lineNumber
                        + ": Amount '" + amountField + "' is not a valid integer number. "
                        + "Please provide a whole number.", lineNumber);
            } else {
                try {
                    int amount = Integer.parseInt(amountField.trim());

                    boolean isIncome = INCOME_CATEGORIES.stream()
                            .anyMatch(cat -> cat.equalsIgnoreCase(categoryField));

                    boolean isFlexible = FLEXIBLE_CATEGORIES.stream()
                            .anyMatch(cat -> cat.equalsIgnoreCase(categoryField));

                    if (!isFlexible) {
                        if (isIncome && amount < 0) {
                            result.addError("Line " + lineNumber + ": Income category '" + categoryField
                                    + "' must not have a negative amount (" + amount + ").", lineNumber);
                        } else if (!isIncome && amount > 0) {
                            result.addError("Line " + lineNumber + ": Expense category '" + categoryField
                                    + "' should have a negative amount, but found " + amount + ".", lineNumber);
                        }
                    }

                } catch (NumberFormatException e) {
                    result.addError("Line " + lineNumber + ": Amount format error.", lineNumber);
                }
            }

            // FINAL STEP: Mark row as valid if no errors were added for this row.
            // If addError(..., lineNumber) was called above, the row is already marked
            // false (invalid).
            // This method simply sets it to true if it isn't already tracked.
            result.markRowValid(lineNumber);
        }

        return result;
    }
}