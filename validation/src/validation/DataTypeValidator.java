
import java.util.List;
import java.util.Collections;

/**
 * Low-level, field-centric checks (emptiness, numeric, ranges, dates).
 * 
 * @author David Humala
 */
public class DataTypeValidator {

    /**
     * Default constructor for DataTypeValidator.
     */
    public DataTypeValidator() {}

    /**
     * Checks that the input is non-null and non-blank after trim.
     * 
     * @param value input string
     * @return true if non-null/non-blank after trim
     */
    public boolean isNonEmpty(String value) {
        return false;
    }

    /**
     * Checks whether the input parses as a number.
     * 
     * @param value numeric string
     * @return true if parses as number
     */
    public boolean isNumeric(String value) {
        return false;
    }

    /**
     * Checks whether the numeric value is strictly greater than zero.
     * 
     * @param value numeric string
     * @return true if strictly greater than zero
     */
    public boolean isPositive(String value) {
        return false;
    }

    /**
     * Checks whether the numeric value lies within the inclusive range.
     * 
     * @param value numeric string
     * @param min   inclusive min
     * @param max   inclusive max
     * @return true if within [min, max]
     */
    public boolean isWithinRange(String value, double min, double max) {
        return false;
    }

    /**
     * Checks whether the input parses to a valid date.
     * 
     * @param value date string
     * @return true if value parses to a valid date
     */
    public boolean isValidDate(String value) {
        return false;
    }
}
