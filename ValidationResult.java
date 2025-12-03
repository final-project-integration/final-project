import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores and manages validation results!
 * 
 * @author Aung Latt
 */
public class ValidationResult {

    /**
     * Severity levels for messages.
     */
    public enum Severity {
        ERROR,
        WARNING
    }

    /**
     * Internal message container.
     */
    private static class ValidationMessage {
        private final Severity severity;
        private final String message;
        private final LocalDateTime timestamp;

        public ValidationMessage(Severity severity, String message) {
            this.severity = severity;
            this.message = message;
            this.timestamp = LocalDateTime.now();
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
     * Tracks error count separately for quick hasErrors().
     */
    private int errorCount;

    /**
     * Used for internal debugging.
     */
    private boolean debugMode = false;

    // Constructor
    public ValidationResult() {
        this.allMessages = new ArrayList<>();
        this.errorMessages = new ArrayList<>();
        this.warningMessages = new ArrayList<>();
        this.errorCount = 0;
    }

    // Static helpers (for CrossFieldValidator, etc.)

    /**
     * Create a ValidationResult that represents "no issues found".
     */
    public static ValidationResult ok() {
        return new ValidationResult();
    }

    /**
     * Create a ValidationResult that contains a single error.
     */
    public static ValidationResult error(String message) {
        ValidationResult vr = new ValidationResult();
        vr.addError(message);
        return vr;
    }

    /**
     * Create a ValidationResult with a summary message and optional details.
     * If ok == false, the summary is treated as an error; otherwise as a warning.
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
        if (message == null) {
            message = "(null error)";
        }
        ValidationMessage msg = new ValidationMessage(Severity.ERROR, message);
        allMessages.add(msg);
        errorMessages.add(msg);
        errorCount++;

        if (debugMode) {
            System.out.println("Debug: Added ERROR: " + message);
        }
    }

    /**
     * Add a warning message.
     *
     * @param message text describing the warning
     */
    public void addWarning(String message) {
        if (message == null) {
            message = "(null warning)";
        }
        ValidationMessage msg = new ValidationMessage(Severity.WARNING, message);
        allMessages.add(msg);
        warningMessages.add(msg);

        if (debugMode) {
            System.out.println("Debug: Added WARNING: " + message);
        }
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
        errorCount = 0;
    }

    /**
     * Get messages by severity.
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
     * Quick summary for integration debugging.
     */
    public String summary() {
        return "ValidationResult Summary: " +
                "errors=" + errorCount +
                ", warnings=" + warningMessages.size();
    }

    @Override
    public String toString() {
        return "ValidationResult{ allMessages=" + allMessages + " }";
    }
}