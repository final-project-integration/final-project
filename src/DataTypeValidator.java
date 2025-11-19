// package validation;

/**
 * The DataTypeValidator class checks if user input is valid
 * by going through simple data checks.
 *
 * @author David Humala
 */
public class DataTypeValidator {

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
     * Checks if the given string is a positive integer.
     *
     * @param input number to test
     * @return true if the number is positive
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
     *
     * @param input the number input as text
     * @param min   the lower limit of the range
     * @param max   the upper limit of the range
     * @return true if the number is in the given range
     */
    public boolean isWithinRange(String input, double min, double max) {
        if (!isNumeric(input)) {
            return false;
        }

        double num = Double.parseDouble(input.trim());
        return num >= min && num <= max;
    }

    /**
     * Checks if the date is in the format "MM/DD/YYYY".
     *
     * @param input the text to check
     * @return true if the date follows the expected format
     */
    public boolean isValidDate(String input) {
        if (input == null) {
            return false;
        }

        input = input.trim();

        if (input.length() != 10) {
            return false;
        }

        if (input.charAt(2) != '/' || input.charAt(5) != '/') {
            return false;
        }

        String month = input.substring(0, 2);
        String day = input.substring(3, 5);
        String year = input.substring(6, 10);

        if (!isNumeric(month) || !isNumeric(day) || !isNumeric(year)) {
            return false;
        }

        int m = Integer.parseInt(month);
        int d = Integer.parseInt(day);
        int y = Integer.parseInt(year);

        if (m < 1 || m > 12 || d < 1 || d > 31 || y < 1) {
            return false;
        }

        return true;
    }

    /**
     * Simple CSV line validation demo.
     * For now, it prints results to the console.
     * Expected format: date,text,number
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

    public static void main(String[] args) {
        String exInput = "12/30/2004, Food, -200"; // test unit.
        DataTypeValidator example = new DataTypeValidator();
        example.validateCSVLine(exInput);
    }
}