//Integration team

/**
 * BeautifulDisplay provides colorful, nicely formatted console output helpers
 * using ANSI escape codes. It is purely for display / UI, not business logic.
 *
 * All methods are static, so we can call BeautifulDisplay.methodName()
 * from anywhere in the default package without imports.
 *
 * @author Denisa Cakoni
 */
public class BeautifulDisplay {

    //ANSI COLOR CODES 
    public static final String RESET          = "\u001B[0m";
    public static final String BOLD           = "\u001B[1m";
    public static final String DIM            = "\u001B[2m";

    public static final String RED            = "\u001B[31m";
    public static final String GREEN          = "\u001B[32m";
    public static final String YELLOW         = "\u001B[33m";
    public static final String BLUE           = "\u001B[34m";
    public static final String MAGENTA        = "\u001B[35m";
    public static final String CYAN           = "\u001B[36m";
    public static final String WHITE          = "\u001B[37m";

    public static final String BRIGHT_RED     = "\u001B[91m";
    public static final String BRIGHT_GREEN   = "\u001B[92m";
    public static final String BRIGHT_YELLOW  = "\u001B[93m";
    public static final String BRIGHT_BLUE    = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN    = "\u001B[96m";
    public static final String BRIGHT_WHITE   = "\u001B[97m";

    private BeautifulDisplay() { }

    //BASIC UTILITIES

    private static String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Removes ANSI color codes so we can measure visible text width.
     */
    private static String stripAnsi(String text) {
        if (text == null) {
            return "";
        }
        // Matches ESC[ ... m
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    /**
     * Returns the visible length of a string.
     */
    private static int visibleLength(String text) {
        return stripAnsi(text).length();
    }

    /**
     * Pads a string with spaces on the right so that its visible length
     * is at least width characters.
     */
    private static String padVisible(String text, int width) {
        if (text == null) {
            text = "";
        }
        int visible = visibleLength(text);
        int needed = width - visible;
        if (needed <= 0) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < needed; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static String center(String text, int width) {
        int visible = visibleLength(text);
        if (visible >= width) {
            return text;
        }
        int padding = width - visible;
        int left = padding / 2;
        int right = padding - left;
        return repeat(" ", left) + text + repeat(" ", right);
    }

    //HEADERS/DIVIDERS 

    /**
     * Prints a fancy top-level header with a box and accent colors.
     */
    public static void printGradientHeader(String title, int width) {
        if (width < visibleLength(title) + 4) {
            width = visibleLength(title) + 4;
        }
        String line = repeat("═", width);
        System.out.println(BRIGHT_BLUE + "┌" + line + "┐" + RESET);
        System.out.println(BRIGHT_BLUE + "│" +
                RESET +
                center(BOLD + BRIGHT_CYAN + title + RESET, width) +
                BRIGHT_BLUE + "│" + RESET);
        System.out.println(BRIGHT_BLUE + "└" + line + "┘" + RESET);
    }

    /**
     * Prints a smaller section header with a colored left border.
     */
    public static void printSectionHeader(String title, String color) {
        String line = repeat("─", visibleLength(title) + 6);
        System.out.println(color + line + RESET);
        System.out.println(color + "▶ " + BOLD + title + RESET);
        System.out.println(color + line + RESET);
    }

    /**
     * Prints a horizontal divider.
     */
    public static void printGradientDivider(int width) {
        if (width < 10) width = 10;
        System.out.println(DIM + repeat("─", width) + RESET);
    }

    //BOXES/LISTS 

    /**
     * Prints a key/value table inside a colored box.
     *
     * This version accounts for ANSI color codes inside keys/values so that
     * all rows line up visually, and the colored borders look straight.
     */
    public static void printKeyValueBox(String title, String[][] rows, String borderColor) {
        int keyWidth = visibleLength(title);
        int valueWidth = 0;

        // Determine visible widths
        for (String[] row : rows) {
            int keyLen = visibleLength(row[0]);
            int valLen = visibleLength(row[1]);
            if (keyLen > keyWidth) keyWidth = keyLen;
            if (valLen > valueWidth) valueWidth = valLen;
        }

        int innerWidth = keyWidth + valueWidth + 5; // " key : value "

        String topBottom = repeat("═", innerWidth);
        System.out.println(borderColor + "┌" + topBottom + "┐" + RESET);

        String headerLine = " " + title + " ";
        System.out.println(borderColor + "│" + RESET +
                center(BOLD + headerLine + RESET, innerWidth) +
                borderColor + "│" + RESET);

        System.out.println(borderColor + "├" + repeat("─", innerWidth) + "┤" + RESET);

        // Print each key/value row with visible length padding
        for (String[] row : rows) {
            String key = padVisible(row[0], keyWidth);
            String value = padVisible(row[1], valueWidth);
            String line = " " + key + " : " + value + " ";
            System.out.println(borderColor + "│" + RESET + line + borderColor + "│" + RESET);
        }

        System.out.println(borderColor + "└" + topBottom + "┘" + RESET);
    }

    /**
     * Prints an ordered list with colored bullets.
     */
    public static void printColorfulList(String[] items, String bulletColor) {
        int index = 1;
        for (String item : items) {
            System.out.println(bulletColor + "  " + index + ". " + RESET + item);
            index++;
        }
    }

    //STATUS/MESSAGES

    public static void printSuccess(String message) {
        System.out.println(BRIGHT_GREEN + "✔ " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(BRIGHT_YELLOW + "⚠ " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(BRIGHT_RED + "✖ " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(BRIGHT_CYAN + "ℹ " + message + RESET);
    }

    /**
     * Simple "loading" animation with dots.
     * delayMs is the total time; split into a few steps.
     */
    public static void printLoading(String message, int delayMs) {
        int steps = 3;
        int stepDelay = delayMs / steps;

        System.out.print(DIM + message + RESET);
        for (int i = 0; i < steps; i++) {
            try {
                Thread.sleep(stepDelay);
            } catch (InterruptedException ignored) {}
            System.out.print(DIM + "." + RESET);
        }
        System.out.println();
    }

    /**
     * Formats a currency number with a $ and two decimals.
     */
    public static String formatCurrency(double value) {
        String formatted = String.format("$%.2f", value);
        if (value > 0) {
            return GREEN + formatted + RESET;
        } else if (value < 0) {
            return RED + formatted + RESET;
        } else {
            return YELLOW + formatted + RESET;
        }
    }
}
