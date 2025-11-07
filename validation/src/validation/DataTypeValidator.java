package validation;

import java.util.List;
import java.util.Collections;

/**
 * Low-level, field-centric checks (emptiness, numeric, ranges, dates).
 * 
 * @author David Humala
 */

public class DataTypeValidator {

    /**
     * @param value input string
     * @return true if non-null/non-blank after trim
     */

    public boolean isNonEmpty(String value) {
        return false;
    }

    /**
     * @param value numeric string
     * @return true if parses as number
     */

    public boolean isNumeric(String value) {
        return false;
    }

    /**
     * @param value numeric string
     * @return true if strictly greater than zero
     */

    public boolean isPositive(String value) {
        return false;
    }

    /**
     * @param value numeric string
     * @param min   inclusive min
     * @param max   inclusive max
     * @return true if within [min, max]
     */

    public boolean isWithinRange(String value, double min, double max) {
        return false;
    }

    /**
     * @param value date string
     * @return true if value parses to a valid date
     */

    public boolean isValidDate(String value) {
        return false;
    }
}
