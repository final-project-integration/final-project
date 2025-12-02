//Integration team

/**
 * BeautifulDisplay provides colorful, nicely formatted console output helpers
 * using ANSI escape codes. It is purely for display / UI, not business logic.
 *
 * All methods are static so you can call BeautifulDisplay.methodName(...)
 * from anywhere in the default package without imports.
 *
 * @author Denisa Cakoni
 */
public class BeautifulDisplay {

    // ===== ANSI COLOR CODES =====
    public static final String RESET         = "\u001B[0m";
    public static final String BOLD          = "\u001B[1m";
    public static final String DIM           = "\u001B[2m";

    public static final String RED           = "\u001B[31m";
    public static final String GREEN         = "\u001B[32m";
    public static final String YELLOW        = "\u001B[33m";
    public static final String BLUE          = "\u001B[34m";
    public static final String MAGENTA       = "\u001B[35m";
    public static final String CYAN          = "\u001B[36m";
    public static final String WHITE         = "\u001B[37m";

    public static final String BRIGHT_RED    = "\u001B[91m";
    public static final String BRIGHT_GREEN  = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE   = "\u001B[94m";
    public static final String BRIGHT_MAGENTA= "\u001B[95m";
    public static final String BRIGHT_CYAN   = "\u001B[96m";
    public static final String BRIGHT_WHITE  = "\u001B[97m";

    private BeautifulDisplay() { }

    // BASIC UTILITIES

    private static String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    private static String center(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = width - text.length();
        int left = padding / 2;
        int right = padding - left;
        return repeat(" ", left) + text + repeat(" ", right);
    }

    // HEADERS / DIVIDERS

    /**
     * Prints a fancy top-level header with a box and accent colors.
     */
    public static void printGradientHeader(String title, int width) {
        if (width < title.length() + 4) {
            width = title.length() + 4;
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
        String line = repeat("─", title.length() + 6);
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

    //  BOXES / LISTS

    /**
     * Prints a key/value table inside a colored box.
     */
    public static void printKeyValueBox(String title, String[][] rows, String borderColor) {
        int keyWidth = title.length();
        int valueWidth = 0;
        for (String[] row : rows) {
            if (row[0].length() > keyWidth) keyWidth = row[0].length();
            if (row[1].length() > valueWidth) valueWidth = row[1].length();
        }
        int innerWidth = keyWidth + valueWidth + 5; // "key : value"

        String topBottom = repeat("═", innerWidth);
        System.out.println(borderColor + "┌" + topBottom + "┐" + RESET);
        String headerLine = " " + title + " ";
        System.out.println(borderColor + "│" + RESET +
                center(BOLD + headerLine + RESET, innerWidth) +
                borderColor + "│" + RESET);
        System.out.println(borderColor + "├" + repeat("─", innerWidth) + "┤" + RESET);

        for (String[] row : rows) {
            String key = row[0];
            String value = row[1];
            String line = String.format(" %-" + keyWidth + "s : %-" + valueWidth + "s ", key, value);
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

    // STATUS / MESSAGES

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
     * delayMs is total time; we just split into a few steps.
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