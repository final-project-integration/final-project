import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Stores and manages validation results with per-row tracking support.
 * 
 * @author Aung Latt (Enhanced for per-row validation tracking)
 */
public class ValidationResult {

    /**
     * Severity levels for messages.
     */
    public enum Severity {
        /**
         * Represents a critical failure that prevents processing.
         */
        ERROR,

        /**
         * Represents a potential issue that does not stop processing.
         */
        WARNING
    }

    /**
     * Internal message container.
     */
    private static class ValidationMessage {
        private final Severity severity;
        private final String message;
        private final LocalDateTime timestamp;
        private final Integer rowNumber; // NEW: Track which row this applies to

        @SuppressWarnings("unused")
        public ValidationMessage(Severity severity, String message) {
            this(severity, message, null);
        }

        public ValidationMessage(Severity severity, String message, Integer rowNumber) {
            this.severity = severity;
            this.message = message;
            this.timestamp = LocalDateTime.now();
            this.rowNumber = rowNumber;
        }

        public Severity getSeverity() {
            return severity;
        }

        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }

        @SuppressWarnings("unused")
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        @SuppressWarnings("unused")
        public Integer getRowNumber() {
            return rowNumber;
        }

        @Override
        public String toString() {
            return "[" + timestamp + "][" + severity + "] " + message;
        }
    }

    // Internal state
    private final List<ValidationMessage> allMessages;
    private final List<ValidationMessage> errorMessages;
    private final List<ValidationMessage> warningMessages;

    /**
     * NEW: Maps row numbers to their validation status.
     * Key: row number (1-based, matching line numbers in CSV)
     * Value: true if row is valid, false if row has errors
     */
    private final Map<Integer, Boolean> rowValidationStatus;

    /**
     * NEW: Maps row numbers to list of error messages for that row.
     */
    private final Map<Integer, List<String>> rowErrors;

    /**
     * Tracks error count separately for quick hasErrors().
     */
    private int errorCount;

    /**
     * Used for internal debugging.
     */
    private boolean debugMode = false;

    // Constructor

    /**
     * Initializes a new, empty ValidationResult instance.
     */
    public ValidationResult() {
        this.allMessages = new ArrayList<>();
        this.errorMessages = new ArrayList<>();
        this.warningMessages = new ArrayList<>();
        this.rowValidationStatus = new HashMap<>();
        this.rowErrors = new HashMap<>();
        this.errorCount = 0;
    }

    // Static helpers (for CrossFieldValidator, etc.)

    /**
     * Create a ValidationResult that represents "no issues found".
     *
     * @return a new empty ValidationResult instance
     */
    public static ValidationResult ok() {
        return new ValidationResult();
    }

    /**
     * Create a ValidationResult that contains a single error.
     *
     * @param message the error message to add
     * @return a new ValidationResult containing the specified error
     */
    public static ValidationResult error(String message) {
        ValidationResult vr = new ValidationResult();
        vr.addError(message);
        return vr;
    }

    /**
     * Create a ValidationResult with a summary message and optional details.
     * If ok == false, the summary is treated as an error; otherwise as a warning.
     *
     * @param ok      true to treat the summary as a warning, false to treat as an
     *                error
     * @param summary the main summary message
     * @param details a list of detailed messages to append (can be null)
     * @return a new ValidationResult populated with the summary and details
     */
    public static ValidationResult withDetails(boolean ok, String summary, List<String> details) {
        ValidationResult vr = new ValidationResult();
        if (ok) {
            vr.addWarning(summary);
        } else {
            vr.addError(summary);
        }
        if (details != null) {
            for (String d : details) {
                if (ok) {
                    vr.addWarning("Detail: " + d);
                } else {
                    vr.addError("Detail: " + d);
                }
            }
        }
        return vr;
    }

    // Public API

    /**
     * Add an error message.
     *
     * @param message text describing the error
     */
    public void addError(String message) {
        addError(message, null);
    }

    /**
     * NEW: Add an error message associated with a specific row.
     *
     * @param message   text describing the error
     * @param rowNumber the row number (1-based) this error applies to, or null for
     *                  global errors
     */
    public void addError(String message, Integer rowNumber) {
        if (message == null) {
            message = "(null error)";
        }
        ValidationMessage msg = new ValidationMessage(Severity.ERROR, message, rowNumber);
        allMessages.add(msg);
        errorMessages.add(msg);
        errorCount++;

        // Track row-specific errors
        if (rowNumber != null) {
            rowValidationStatus.put(rowNumber, false);
            rowErrors.computeIfAbsent(rowNumber, k -> new ArrayList<>()).add(message);
        }

        if (debugMode) {
            System.out.println("Debug: Added ERROR" +
                    (rowNumber != null ? " (row " + rowNumber + ")" : "") + ": " + message);
        }
    }

    /**
     * Add a warning message.
     *
     * @param message text describing the warning
     */
    public void addWarning(String message) {
        addWarning(message, null);
    }

    /**
     * NEW: Add a warning message associated with a specific row.
     *
     * @param message   text describing the warning
     * @param rowNumber the row number (1-based) this warning applies to, or null
     *                  for global warnings
     */
    public void addWarning(String message, Integer rowNumber) {
        if (message == null) {
            message = "(null warning)";
        }
        ValidationMessage msg = new ValidationMessage(Severity.WARNING, message, rowNumber);
        allMessages.add(msg);
        warningMessages.add(msg);

        if (debugMode) {
            System.out.println("Debug: Added WARNING" +
                    (rowNumber != null ? " (row " + rowNumber + ")" : "") + ": " + message);
        }
    }

    /**
     * NEW: Mark a row as valid (has no errors).
     * This is useful for tracking which rows passed validation.
     *
     * @param rowNumber the row number (1-based) to mark as valid
     */
    public void markRowValid(Integer rowNumber) {
        if (rowNumber != null && !rowValidationStatus.containsKey(rowNumber)) {
            rowValidationStatus.put(rowNumber, true);
        }
    }
    /**
     * Check if a specific row is valid (passed validation with no errors).
     *
     * @param rowNumber the row number (1-based) to check
     * @return true if the row is explicitly marked valid, false otherwise
     */
    public boolean isRowValid(Integer rowNumber) {
        Boolean status = rowValidationStatus.get(rowNumber);
        return status != null && status;
    }
    /**
     * Check if errors exist.
     *
     * @return true if there is at least one error
     */
    public boolean hasErrors() {
        return errorCount > 0;
    }

    /**
     * NEW: Check if a specific row has errors.
     *
     * @param rowNumber the row number (1-based) to check
     * @return true if the row has errors, false if valid or unknown
     */
    public boolean hasRowErrors(Integer rowNumber) {
        Boolean status = rowValidationStatus.get(rowNumber);
        return status != null && !status;
    }

    /**
     * NEW: Get all row numbers that have errors.
     *
     * @return list of row numbers with errors (1-based)
     */
    public List<Integer> getInvalidRowNumbers() {
        List<Integer> invalid = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : rowValidationStatus.entrySet()) {
            if (!entry.getValue()) {
                invalid.add(entry.getKey());
            }
        }
        Collections.sort(invalid);
        return invalid;
    }

    /**
     * NEW: Get all row numbers that passed validation.
     *
     * @return list of valid row numbers (1-based)
     */
    public List<Integer> getValidRowNumbers() {
        List<Integer> valid = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : rowValidationStatus.entrySet()) {
            if (entry.getValue()) {
                valid.add(entry.getKey());
            }
        }
        Collections.sort(valid);
        return valid;
    }

    /**
     * NEW: Get error messages for a specific row.
     *
     * @param rowNumber the row number (1-based)
     * @return list of error messages for that row, or empty list if none
     */
    public List<String> getRowErrors(Integer rowNumber) {
        List<String> errors = rowErrors.get(rowNumber);
        return errors != null ? Collections.unmodifiableList(errors) : Collections.emptyList();
    }

    /**
     * NEW: Get count of rows with errors.
     *
     * @return number of invalid rows
     */
    public int getInvalidRowCount() {
        return getInvalidRowNumbers().size();
    }

    /**
     * NEW: Get count of valid rows.
     *
     * @return number of valid rows
     */
    public int getValidRowCount() {
        return getValidRowNumbers().size();
    }

    /**
     * NEW: Check if any rows have been validated (whether valid or invalid).
     *
     * @return true if row-level validation has been performed
     */
    public boolean hasRowLevelValidation() {
        return !rowValidationStatus.isEmpty();
    }

    /**
     * Retrieve all messages in readonly format.
     *
     * @return read-only list of full string representations
     */
    public List<String> getMessages() {
        List<String> output = new ArrayList<>();
        for (ValidationMessage msg : allMessages) {
            output.add(msg.toString());
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * Returns only error messages, for detailed reporting.
     *
     * @return a read-only list of error string representations
     */
    public List<String> getErrorMessages() {
        List<String> output = new ArrayList<>();
        for (ValidationMessage msg : errorMessages) {
            output.add(msg.toString());
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * Returns only warnings.
     *
     * @return a read-only list of warning string representations
     */
    public List<String> getWarningMessages() {
        List<String> output = new ArrayList<>();
        for (ValidationMessage msg : warningMessages) {
            output.add(msg.toString());
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * Enable or disable debug mode.
     *
     * @param enabled true to enable debug output to stdout, false to disable
     */
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }

    /**
     * Clear all messages and reset state.
     */
    public void clear() {
        allMessages.clear();
        errorMessages.clear();
        warningMessages.clear();
        rowValidationStatus.clear();
        rowErrors.clear();
        errorCount = 0;
    }

    /**
     * Get messages by severity.
     *
     * @param severity the Severity level to filter by
     * @return a read-only list of messages matching the specified severity
     */
    public List<String> getMessagesBySeverity(Severity severity) {
        List<String> output = new ArrayList<>();
        for (ValidationMessage msg : allMessages) {
            if (msg.getSeverity() == severity) {
                output.add(msg.toString());
            }
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * Merge another ValidationResult into this one.
     * Severity is inferred from the string form (as in aggregateResults).
     *
     * @param other another ValidationResult (may be null)
     * @return this (for chaining)
     */
    public ValidationResult merge(ValidationResult other) {
        if (other == null) {
            return this;
        }
        for (String msg : other.getMessages()) {
            if (msg.contains("[ERROR]")) {
                this.addError(msg);
            } else if (msg.contains("[WARNING]")) {
                this.addWarning(msg);
            } else {
                // Default to warning if severity cannot be inferred
                this.addWarning(msg);
            }
        }

        // Merge row-level validation status
        for (Map.Entry<Integer, Boolean> entry : other.rowValidationStatus.entrySet()) {
            Integer rowNum = entry.getKey();
            Boolean isValid = entry.getValue();

            // If this row is already tracked as invalid, keep it invalid
            if (this.rowValidationStatus.containsKey(rowNum)) {
                if (!this.rowValidationStatus.get(rowNum)) {
                    continue; // Already invalid, don't overwrite
                }
            }
            this.rowValidationStatus.put(rowNum, isValid);
        }

        // Merge row errors
        for (Map.Entry<Integer, List<String>> entry : other.rowErrors.entrySet()) {
            this.rowErrors.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                    .addAll(entry.getValue());
        }

        return this;
    }

    /**
     * Quick summary for integration debugging.
     *
     * @return a string summary of the error and warning counts
     */
    public String summary() {
        StringBuilder sb = new StringBuilder("ValidationResult Summary: ");
        sb.append("errors=").append(errorCount);
        sb.append(", warnings=").append(warningMessages.size());

        if (hasRowLevelValidation()) {
            sb.append(", invalid rows=").append(getInvalidRowCount());
            sb.append(", valid rows=").append(getValidRowCount());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "ValidationResult{ allMessages=" + allMessages +
                ", rowValidation=" + rowValidationStatus + " }";
    }
}
