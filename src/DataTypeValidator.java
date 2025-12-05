import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * The DataTypeValidator class checks if user input is valid
 * by performing specific validations such as null checks, numeric parsing,
 * range validation, and date format verification.
 *
 * @author David Humala
 */
public class DataTypeValidator {

    /**
     * Default constructor.
     */
    public DataTypeValidator() {
        // Default constructor
    }

    /**
     * Checks if the input string is not null and contains at least one
     * non-whitespace character.
     *
     * @param input the string to test
     * @return true if the string contains text, false if null or whitespace only
     */
    public boolean isNonEmpty(String input) {
        return input != null && input.trim().length() > 0;
    }

    /**
     * Check if the text represents an integer number (optional leading '-').
     *
     * @param input the text to test
     * @return true if the text is a valid integer
     */
    public boolean isNumeric(String input) {
        if (input == null || input.trim().length() == 0) {
            return false;
        }

        input = input.trim();
        int start = 0;
        if (input.charAt(0) == '-') {
            start = 1;
        }
        for (int i = start; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the given string is a strictly positive integer.
     * <p>
     * Note: Zero (0) is not considered positive.
     *
     * @param input number to test
     * @return true if the number is strictly greater than 0
     */
    public boolean isPositive(String input) {
        if (!isNumeric(input)) {
            return false;
        }

        int num = Integer.parseInt(input.trim());
        return num > 0;
    }

    /**
     * Checks if the number is within the given range.
     * <p>
     * <strong>Parsing Rule:</strong> The input is parsed as a {@code double} for
     * comparison.
     * However, the input string must pass {@link #isNumeric(String)} first, which
     * currently
     * restricts input to integer formats (no decimal points allowed).
     *
     * @param input the number input as text
     * @param min   the lower limit of the range (inclusive)
     * @param max   the upper limit of the range (inclusive)
     * @return true if the number is valid and falls between min and max
     */
    public boolean isWithinRange(String input, double min, double max) {
        if (!isNumeric(input)) {
            return false;
        }

        double num = Double.parseDouble(input.trim());
        return num >= min && num <= max;
    }

    /**
     * Checks if the date is in the format "MM/DD/YYYY" and is a real calendar date.
     * * FIXED: Now uses STRICT resolver style to properly validate dates like
     * 13/40/2024
     * and properly rejects dates with wrong separators or wrong order like
     * YYYY/MM/DD.
     *
     * @param input the text to check
     * @return true if the date follows the expected format and is valid
     */
    public boolean isValidDate(String input) {
        if (input == null) {
            return false;
        }

        input = input.trim();

        // Strict pattern: exactly 2 digits, slash, 2 digits, slash, 4 digits
        if (!input.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        // Use STRICT resolver style to catch invalid dates like 13/40/2024, 02/30/2024,
        // etc.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            // parse will fail for invalid dates like 13/40/2024, 02/30/2024, etc.
            LocalDate.parse(input, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Extracts the year from a valid MM/DD/YYYY date string, or -1 if invalid.
     *
     * @param input date string in MM/DD/YYYY
     * @return the year, or -1 if the date is invalid
     */
    public int extractYear(String input) {
        if (!isValidDate(input)) {
            return -1;
        }
        // positions 6–9 are the 4-digit year in MM/DD/YYYY
        return Integer.parseInt(input.substring(6, 10));
    }

    /**
     * Simple CSV line validation demo.
     * For now, it prints results to the console.
     * Expected format: date,text,number
     *
     * @param line the raw CSV string to validate
     */
    public void validateCSVLine(String line) {

        System.out.println("Validation Results");
        if (!isNonEmpty(line)) {
            System.err.println("Error: Input is invalid.");
            return;
        }

        String[] fields = line.split(",");
        if (fields.length != 3) {
            System.err.println("Error: Input must have 3 fields.");
            return;
        }

        String dateField = fields[0].trim();
        String textField = fields[1].trim();
        String numberField = fields[2].trim();

        System.out.println("Text field:");
        if (isNonEmpty(textField)) {
            System.out.println("Input is valid (not empty).");
        } else {
            System.err.println("Error: Input is invalid (empty text).");
        }

        System.out.println("Number field:");
        if (isNumeric(numberField)) {
            System.out.println("Input is valid (numeric).");
        } else {
            System.err.println("Error: Input is invalid (not numeric).");
        }

        if (isPositive(numberField)) {
            System.out.println("Input is valid (positive).");
        } else {
            System.err.println("Error: Input is invalid (input is not positive).");
        }

        if (isWithinRange(numberField, 0, 1000)) { // temporary min and max.
            System.out.println("Input is valid (within range).");
        } else {
            System.err.println("Error: Input is invalid (out of range).");
        }

        System.out.println("Date field:");
        if (isValidDate(dateField)) {
            System.out.println("Input is valid (date format).");
        } else {
            System.err.println("Error: Input is invalid (date must be in MM/DD/YYYY).");
        }
    }

    /**
     * Main entry point for manual testing of the validator logic.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        // Demo only; real PFM CSVs may use negative amounts for expenses.
        String exInput = "12/30/2004, Food, 200";
        DataTypeValidator example = new DataTypeValidator();
        example.validateCSVLine(exInput);
    }
}