import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Coordinates top-level validations and result aggregation with per-row
 * tracking.
 * 
 * - ERRORS: Structural issues that prevent CSVHandler from processing (bad
 * header, empty file)
 * - WARNINGS: Row-level issues that CSVHandler can skip during import (bad data
 * in rows)
 *
 * @author Mayesha Fahmida (Enhanced for per-row validation tracking)
 */
public class ValidationEngine {

    private final DataTypeValidator dataValidator;

    /**
     * UPDATED: Complete list matching the official Line Items specification.
     * Includes all Income and Expense categories from the project documentation.
     */
    private static final List<String> ALLOWED_CATEGORIES = List.of(
            // Income categories
            "Compensation",
            "Allowance",
            "Investments",
            // Expense categories
            "Home",
            "Utilities",
            "Food",
            "Appearance",
            "Work",
            "Education",
            "Transportation",
            "Entertainment",
            "Professional Services",
            // Flexible category (can be income or expense)
            "Other");

    /**
     * UPDATED: Categories that represent income (amounts should be non-negative).
     * "Other" is intentionally NOT included here because it's a flexible category.
     */
    private static final List<String> INCOME_CATEGORIES = List.of(
            "Compensation",
            "Allowance",
            "Investments");

    /**
     * Categories that are flexible - can be either income or expense.
     * Sign validation is skipped for these categories.
     */
    private static final List<String> FLEXIBLE_CATEGORIES = List.of(
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
        // Reuse existing rules with per-row tracking
        ValidationResult result = validateCsvLines(lines);

        // Only attempt filename-vs-CSV year cross-check if there are valid rows
        if (result.hasErrors() || result.getValidRowCount() == 0) {
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
            result.addWarning("File name year (" + fileYear
                    + ") does not match CSV date year (" + csvYear + ").");
        }

        return result;
    }

    /**
     * ENHANCED: Validate a CSV file with per-row tracking.
     * 
     * SEVERITY POLICY:
     * - ERRORS (block upload): Bad header, empty file, no valid rows
     * - WARNINGS (allow upload, skip rows): Invalid data in individual rows
     * 
     * This aligns with CSVHandler behavior which skips bad rows during import.
     *
     * @param lines all lines of the CSV file (including header)
     * @return ValidationResult with per-row validation status
     */
    public ValidationResult validateCsvLines(List<String> lines) {
        ValidationResult result = new ValidationResult();

        // ----- STRUCTURAL VALIDATION (ERRORS) -----

        if (lines == null || lines.isEmpty()) {
            result.addError("CSV file is empty.");
            return result;
        }

        // Header validation - CRITICAL ERROR
        String header = lines.get(0).trim();
        String expectedHeader = "Date,Category,Amount";

        if (!header.equals(expectedHeader)) {
            result.addError("Invalid CSV header. Expected exactly: '" + expectedHeader
                    + "' but found: '" + header + "'. "
                    + "Please ensure the header has exactly these three columns with correct spelling and no extra columns.");
            // Header error is critical - can't proceed with row validation
            return result;
        }

        // Keep track of year consistency
        Integer fileYear = null;
        boolean hasAnyValidRows = false;

        // ----- ROW-BY-ROW VALIDATION (WARNINGS) -----

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1; // human-readable (header is line 1)

            // Ignore completely blank lines - don't track them
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            // Track this row - assume valid until proven otherwise
            boolean rowIsValid = true;

            String[] fields = line.split(",", -1);

            // Column count validation - WARNING (CSVHandler can skip this row)
            if (fields.length != 3) {
                String columnIssue = fields.length > 3 ? "extra columns" : "missing columns";
                result.addWarning("Line " + lineNumber
                        + ": Expected exactly 3 columns (Date,Category,Amount) but found "
                        + fields.length + " columns (" + columnIssue + "). "
                        + "This row will be skipped during import.",
                        lineNumber);
                rowIsValid = false;
                continue; // Can't validate further without correct column count
            }

            String dateField = fields[0].trim();
            String categoryField = fields[1].trim();
            String amountField = fields[2].trim();

            // ----- Missing required fields - WARNING -----
            boolean missing = false;

            if (!dataValidator.isNonEmpty(dateField)) {
                result.addWarning("Line " + lineNumber + ": Date field is required and cannot be empty. "
                        + "This row will be skipped during import.",
                        lineNumber);
                missing = true;
                rowIsValid = false;
            }
            if (!dataValidator.isNonEmpty(categoryField)) {
                result.addWarning("Line " + lineNumber + ": Category field is required and cannot be empty. "
                        + "This row will be skipped during import.",
                        lineNumber);
                missing = true;
                rowIsValid = false;
            }
            if (!dataValidator.isNonEmpty(amountField)) {
                result.addWarning("Line " + lineNumber + ": Amount field is required and cannot be empty. "
                        + "This row will be skipped during import.",
                        lineNumber);
                missing = true;
                rowIsValid = false;
            }

            if (missing) {
                continue; // Can't validate further without required fields
            }

            // ----- Date validity + year consistency - WARNING -----
            if (!dataValidator.isValidDate(dateField)) {
                result.addWarning("Line " + lineNumber + ": Invalid date '" + dateField
                        + "'. Date must be in MM/DD/YYYY format with valid month (01-12), "
                        + "day (01-31 depending on month), and year. "
                        + "This row will be skipped during import.",
                        lineNumber);
                rowIsValid = false;
            } else {
                int year = dataValidator.extractYear(dateField);
                if (fileYear == null) {
                    fileYear = year;
                } else if (year != fileYear) {
                    result.addWarning("Line " + lineNumber
                            + ": Year " + year + " does not match file year " + fileYear + ". "
                            + "This row will be skipped during import.",
                            lineNumber);
                    rowIsValid = false;
                }
            }

            // ----- Category validity - WARNING -----
            boolean categoryOk = false;
            for (String allowed : ALLOWED_CATEGORIES) {
                if (allowed.equalsIgnoreCase(categoryField)) {
                    categoryOk = true;
                    break;
                }
            }
            if (!categoryOk) {
                result.addWarning("Line " + lineNumber
                        + ": Invalid category '" + categoryField
                        + "'. Category must be one of the approved categories: "
                        + String.join(", ", ALLOWED_CATEGORIES) + ". "
                        + "This row will be skipped during import.",
                        lineNumber);
                rowIsValid = false;
            }

            // ----- Amount basic validation + sign consistency - WARNING -----
            if (!dataValidator.isNumeric(amountField)) {
                result.addWarning("Line " + lineNumber
                        + ": Amount '" + amountField + "' is not a valid integer number. "
                        + "This row will be skipped during import.",
                        lineNumber);
                rowIsValid = false;
            } else {
                try {
                    int amount = Integer.parseInt(amountField.trim());

                    // FIXED: Check if this is a flexible category
                    boolean isFlexible = FLEXIBLE_CATEGORIES.stream()
                            .anyMatch(cat -> cat.equalsIgnoreCase(categoryField));

                    // Skip sign validation for flexible categories like "Other"
                    if (!isFlexible) {
                        boolean isIncome = INCOME_CATEGORIES.stream()
                                .anyMatch(cat -> cat.equalsIgnoreCase(categoryField));

                        // Sign convention: income >= 0, expenses <= 0
                        if (isIncome && amount < 0) {
                            result.addWarning("Line " + lineNumber + ": Income category '" + categoryField
                                    + "' must not have a negative amount (" + amount + "). "
                                    + "This row will be skipped during import.",
                                    lineNumber);
                            rowIsValid = false;
                        } else if (!isIncome && amount > 0) {
                            result.addWarning("Line " + lineNumber + ": Expense category '" + categoryField
                                    + "' should have a negative amount, but found " + amount + ". "
                                    + "This row will be skipped during import.",
                                    lineNumber);
                            rowIsValid = false;
                        }
                    }
                    // For flexible categories (e.g., "Other"), any sign is valid

                } catch (NumberFormatException e) {
                    // Should be caught by isNumeric check, but safe guard here
                    result.addWarning("Line " + lineNumber + ": Amount format error. "
                            + "This row will be skipped during import.", lineNumber);
                    rowIsValid = false;
                }
            }

            // Mark the row's final validation status
            if (rowIsValid) {
                result.markRowValid(lineNumber);
                hasAnyValidRows = true;
            }
        }

        // ----- POST-VALIDATION CHECK: At least one valid row required (ERROR) -----
        if (!hasAnyValidRows) {
            result.addError("CSV contains no valid rows to import. "
                    + "Please fix the issues and try again.");
        }

        return result;
    }

    /**
     * NEW: Get only the valid data rows from the CSV (excluding header and invalid
     * rows).
     * This allows Storage to import only valid transactions.
     *
     * @param lines            all lines of the CSV file (including header)
     * @param validationResult the result from validateCsvLines()
     * @return list of valid data lines (without header, only valid rows)
     */
    public List<String> filterValidRows(List<String> lines, ValidationResult validationResult) {
        if (lines == null || lines.isEmpty() || validationResult == null) {
            return List.of();
        }

        List<String> validLines = new ArrayList<>();
        List<Integer> validRowNumbers = validationResult.getValidRowNumbers();

        for (Integer rowNum : validRowNumbers) {
            // rowNum is 1-based and includes header, so data is at index rowNum-1
            int index = rowNum - 1;
            if (index > 0 && index < lines.size()) {
                validLines.add(lines.get(index));
            }
        }

        return validLines;
    }

    /**
     * NEW: Generate a user-friendly summary of validation results for display.
     * This helps users understand what went wrong and make an informed decision.
     *
     * @param validationResult the validation result to summarize
     * @return formatted summary string
     */
    public String generateValidationSummary(ValidationResult validationResult) {
        if (validationResult == null) {
            return "No validation results available.";
        }

        StringBuilder summary = new StringBuilder();

        // Check for blocking errors first
        if (validationResult.hasErrors()) {
            summary.append("❌ Critical errors found - upload blocked:\n\n");
            for (String error : validationResult.getErrorMessages()) {
                summary.append("  ").append(error).append("\n");
            }
            return summary.toString();
        }

        // No blocking errors - check for warnings
        if (validationResult.getWarningMessages().isEmpty()) {
            summary.append("✓ All rows are valid! Ready to import.\n");
            summary.append("Total valid rows: ").append(validationResult.getValidRowCount());
            return summary.toString();
        }

        // Has warnings - provide detailed breakdown
        summary.append("⚠ Validation found issues with some rows:\n");
        summary.append("- Valid rows: ").append(validationResult.getValidRowCount()).append("\n");
        summary.append("- Invalid rows: ").append(validationResult.getInvalidRowCount())
                .append(" (will be skipped)\n\n");

        List<Integer> invalidRows = validationResult.getInvalidRowNumbers();
        if (!invalidRows.isEmpty()) {
            summary.append("Invalid row numbers: ");
            if (invalidRows.size() <= 10) {
                summary.append(invalidRows.toString());
            } else {
                summary.append(invalidRows.subList(0, 10)).append("... and ")
                        .append(invalidRows.size() - 10).append(" more");
            }
            summary.append("\n\n");
        }

        // Show first few warnings as examples
        List<String> warningMessages = validationResult.getWarningMessages();
        if (!warningMessages.isEmpty()) {
            summary.append("Example issues:\n");
            int maxWarnings = Math.min(5, warningMessages.size());
            for (int i = 0; i < maxWarnings; i++) {
                summary.append("  ").append(warningMessages.get(i)).append("\n");
            }
            if (warningMessages.size() > maxWarnings) {
                summary.append("  ... and ").append(warningMessages.size() - maxWarnings)
                        .append(" more issues\n");
            }
        }

        return summary.toString();
    }
}