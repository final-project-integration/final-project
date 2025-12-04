//Integration team

/**
 * BeautifulDisplay provides colorful, nicely formatted console output helpers
 * using ANSI escape codes. It is purely for display and UI, not business logic.
 *
 * All methods are static, so they can be called from anywhere in the project.
 *
 * @author Denisa Cakoni
 */
public class BeautifulDisplay {

    // ANSI COLOR CODES
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    /**
     * Private constructor to prevent instantiation.
     *
     * @author Denisa Cakoni
     */
    private BeautifulDisplay() { }

    
    // BASIC UTILITIES


    /**
     * Repeats a string a certain number of times.
     *
     * @param s     the string to repeat
     * @param count how many times to repeat it
     * @return the repeated string
     *
     * @author Denisa Cakoni
     */
    private static String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Removes ANSI color codes so the visible length can be measured correctly.
     *
     * @param text text that may contain color codes
     * @return the text with ANSI escape codes removed
     *
     * @author Denisa Cakoni
     */
    private static String stripAnsi(String text) {
        if (text == null) return "";
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    /**
     * Returns the number of visible characters in a string.
     *
     * @param text the string to measure
     * @return number of visible characters (ignoring color codes)
     *
     * @author Denisa Cakoni
     */
    private static int visibleLength(String text) {
        return stripAnsi(text).length();
    }

    /**
     * Pads text on the right so its visible length is at least the given width.
     *
     * @param text  the text to pad
     * @param width desired visible width
     * @return padded text
     *
     * @author Denisa Cakoni
     */
    private static String padVisible(String text, int width) {
        if (text == null) text = "";
        int visible = visibleLength(text);
        int needed = width - visible;

        if (needed <= 0) return text;

        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < needed; i++) sb.append(' ');
        return sb.toString();
    }

    /**
     * Pads text on the left to reach the desired visible width.
     *
     * @param text  the text to pad
     * @param width desired visible width
     * @return left-padded text
     *
     * @author Denisa Cakoni
     */
    private static String padVisibleLeft(String text, int width) {
        if (text == null) text = "";
        int visible = visibleLength(text);
        int needed = width - visible;

        if (needed <= 0) return text;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < needed; i++) sb.append(' ');
        sb.append(text);
        return sb.toString();
    }

    /**
     * Centers text within a given visible width.
     *
     * @param text  the text to center
     * @param width total space to center within
     * @return centered text
     *
     * @author Denisa Cakoni
     */
    private static String center(String text, int width) {
        int visible = visibleLength(text);
        if (visible >= width) return text;

        int padding = width - visible;
        int left = padding / 2;
        int right = padding - left;
        return repeat(" ", left) + text + repeat(" ", right);
    }


    // HEADERS & DIVIDERS


    /**
     * Prints a large decorative header box.
     *
     * @param title text to show inside the header
     * @param width width of the inner box
     *
     * @author Denisa Cakoni
     */
    public static void printGradientHeader(String title, int width) {
        if (width < visibleLength(title) + 4) {
            width = visibleLength(title) + 4;
        }
        String line = repeat("═", width);

        System.out.println(BRIGHT_BLUE + "┌" + line + "┐" + RESET);
        System.out.println(BRIGHT_BLUE + "│" +
                RESET + center(BOLD + BRIGHT_CYAN + title + RESET, width) +
                BRIGHT_BLUE + "│" + RESET);
        System.out.println(BRIGHT_BLUE + "└" + line + "┘" + RESET);
    }

    /**
     * Prints a smaller colored section header.
     *
     * @param title text to show
     * @param color ANSI color to use
     *
     * @author Denisa Cakoni
     */
    public static void printSectionHeader(String title, String color) {
        String line = repeat("─", visibleLength(title) + 6);
        System.out.println(color + line + RESET);
        System.out.println(color + "▶ " + BOLD + title + RESET);
        System.out.println(color + line + RESET);
    }

    /**
     * Prints a horizontal divider line.
     *
     * @param width divider length
     *
     * @author Denisa Cakoni
     */
    public static void printGradientDivider(int width) {
        if (width < 10) width = 10;
        System.out.println(DIM + repeat("─", width) + RESET);
    }


    // BOXES & LISTS


    /**
     * Prints a colored key/value table inside a box.
     *
     * @param title       the box title
     * @param rows        each row contains a key and value
     * @param borderColor color for the box border
     *
     * @author Denisa Cakoni
     */
    public static void printKeyValueBox(String title, String[][] rows, String borderColor) {
        int keyWidth = visibleLength(title);
        int valueWidth = 0;

        for (String[] row : rows) {
            if (visibleLength(row[0]) > keyWidth) keyWidth = visibleLength(row[0]);
            if (visibleLength(row[1]) > valueWidth) valueWidth = visibleLength(row[1]);
        }

        int innerWidth = keyWidth + valueWidth + 5;
        String topBottom = repeat("═", innerWidth);

        System.out.println(borderColor + "┌" + topBottom + "┐" + RESET);
        System.out.println(borderColor + "│" + RESET +
                center(BOLD + " " + title + " " + RESET, innerWidth) +
                borderColor + "│" + RESET);
        System.out.println(borderColor + "├" + repeat("─", innerWidth) + "┤" + RESET);

        for (String[] row : rows) {
            String key = padVisible(row[0], keyWidth);
            String value = padVisible(row[1], valueWidth);
            System.out.println(borderColor + "│" + RESET +
                    " " + key + " : " + value + " " +
                    borderColor + "│" + RESET);
        }

        System.out.println(borderColor + "└" + topBottom + "┘" + RESET);
    }

    /**
     * Prints an ordered list with colored index numbers.
     *
     * @param items       list of items
     * @param bulletColor color for the numbering
     *
     * @author Denisa Cakoni
     */
    public static void printColorfulList(String[] items, String bulletColor) {
        int index = 1;
        for (String item : items) {
            System.out.println(bulletColor + "  " + index + ". " + RESET + item);
            index++;
        }
    }


    // STATUS & MESSAGES

    /**
     * Prints a green success message.
     *
     * @param message text to print
     *
     * @author Denisa Cakoni
     */
    public static void printSuccess(String message) {
        System.out.println(BRIGHT_GREEN + "✔ " + message + RESET);
    }

    /**
     * Prints a yellow warning message.
     *
     * @param message text to print
     *
     * @author Denisa Cakoni
     */
    public static void printWarning(String message) {
        System.out.println(BRIGHT_YELLOW + "⚠ " + message + RESET);
    }

    /**
     * Prints a red error message.
     *
     * @param message text to print
     *
     * @author Denisa Cakoni
     */
    public static void printError(String message) {
        System.out.println(BRIGHT_RED + "✖ " + message + RESET);
    }

    /**
     * Prints an informational message.
     *
     * @param message text to print
     *
     * @author Denisa Cakoni
     */
    public static void printInfo(String message) {
        System.out.println(BRIGHT_CYAN + "ℹ " + message + RESET);
    }

    /**
     * Prints a loading animation using dots.
     *
     * @param message message to display before the dots
     * @param delayMs how long the animation lasts (milliseconds)
     *
     * @author Denisa Cakoni
     */
    public static void printLoading(String message, int delayMs) {
        int steps = 3;
        int stepDelay = delayMs / steps;

        System.out.print(DIM + message + RESET);
        for (int i = 0; i < steps; i++) {
            try {
                Thread.sleep(stepDelay);
            } catch (InterruptedException ignored) { }
            System.out.print(DIM + "." + RESET);
        }
        System.out.println();
    }

    /**
     * Formats a currency value as whole dollars and adds a color:
     * green for positive, red for negative, yellow for zero.
     *
     * @param value the number to format
     * @return a colored dollar string
     *
     * @author Denisa Cakoni
     */
    public static String formatCurrency(double value) {
        String formatted = String.format("$%.0f", value);

        if (value > 0) return GREEN + formatted + RESET;
        if (value < 0) return RED + formatted + RESET;
        return YELLOW + formatted + RESET;
    }
}
