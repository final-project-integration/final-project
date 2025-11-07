package validation;

/**
 * Stores and manages the results of validation results.
 * 
 * @author Aung Latt
 */

public class ValidationResult {

    /**
     * Aggregated result/messages for validations.
     * 
     * @author Aung Latt
     */

    public ValidationResult() {

    }

    /**
     * Record an error message.
     * 
     * @param message error text
     */

    public void addError(String message) {

    }

    /**
     * Record a warning message.
     * 
     * @param message warning text
     */

    public void addWarning(String message) {

    }

    /**
     * @return true if errors exist
     */

    public boolean hasErrors() {
        return false;
    }

    /**
     * @return read-only list of messages
     */

    public java.util.List<String> getMessages() {
        return java.util.Collections.emptyList();
    }

    /**
     * Clear messages and flags.
     */

    public void clear() {

    }
}
