//Integration team

/**
 * BeautifulDisplay provides colorful, nicely formatted console output helpers
 * using ANSI escape codes. It is purely for display and UI, not business logic.
 *
 * This utility class contains static methods and color constants for creating
 * visually appealing terminal output. All methods and constants are static,
 * so they can be called from anywhere in the project without instantiation.
 *
 *
 *
 * COLOR CONSTANTS PURPOSE:
 * The color constants are intended to be used in the following ways:
 *  RESET: Always use after colored text to prevent color bleeding
 *  BOLD: Emphasize important text or headers
 *  DIM: De-emphasize secondary information
 *  GREEN: Success messages, positive values, income
 *  RED: Error messages, negative values, expenses
 *  YELLOW: Warnings, neutral values
 *  CYAN: Information messages, highlights
 *  BLUE: Headers, structural elements
 *  BRIGHT_* versions: More vibrant alternatives to basic colors
 *
 * DESIGN NOTES:
 *  This is a utility class with a private constructor to prevent instantiation
 *  All methods handle ANSI color codes properly to ensure correct visual alignment
 *
 * @author Denisa Cakoni
 */
public class BeautifulDisplay {


    // ANSI COLOR CODES


    /**
     * Resets all ANSI formatting (color, bold, dim) back to terminal default.
     * Important: Always use RESET after colored text to prevent color bleeding
     * into subsequent output.
     *
     * Example usage:
     * System.out.println(GREEN + "Success" + RESET + " - Operation complete");
     */
    public static final String RESET = "\u001B[0m";

    /**
     * Makes text bold/bright.
     * Use for emphasizing headers, titles, or important information.
     *
     * Example usage:
     * System.out.println(BOLD + "Total: " + RESET + "$1,500");
     */
    public static final String BOLD = "\u001B[1m";

    /**
     * Makes text dimmed/faint.
     * Use for de-emphasizing secondary information like timestamps or footnotes.
     *
     * Example usage:
     * System.out.println(DIM + "Last updated: 2025-12-04" + RESET);
     */
    public static final String DIM = "\u001B[2m";

    /**
     * Standard red color.
     * Commonly used for error messages, negative values, or expenses.
     */
    public static final String RED = "\u001B[31m";

    /**
     * Standard green color.
     * Commonly used for success messages, positive values, or income.
     */
    public static final String GREEN = "\u001B[32m";

    /**
     * Standard yellow color.
     * Commonly used for warning messages or neutral values.
     */
    public static final String YELLOW = "\u001B[33m";

    /**
     * Standard blue color.
     * Commonly used for headers, borders, or structural elements.
     */
    public static final String BLUE = "\u001B[34m";

    /**
     * Standard magenta color.
     * Commonly used for highlighting special categories or sections.
     */
    public static final String MAGENTA = "\u001B[35m";

    /**
     * Standard cyan color.
     * Commonly used for informational messages or balance amounts.
     */
    public static final String CYAN = "\u001B[36m";

    /**
     * Standard white color.
     * Commonly used for general text or backgrounds.
     */
    public static final String WHITE = "\u001B[37m";

    /**
     * Bright/vivid red color.
     * More vibrant than RED. Use for critical errors or important negative values.
     */
    public static final String BRIGHT_RED = "\u001B[91m";

    /**
     * Bright/vivid green color.
     * More vibrant than GREEN. Use for prominent success messages or important income.
     */
    public static final String BRIGHT_GREEN = "\u001B[92m";

    /**
     * Bright/vivid yellow color.
     * More vibrant than YELLOW. Use for important warnings.
     */
    public static final String BRIGHT_YELLOW = "\u001B[93m";

    /**
     * Bright/vivid blue color.
     * More vibrant than BLUE. Use for prominent headers or borders.
     */
    public static final String BRIGHT_BLUE = "\u001B[94m";

    /**
     * Bright/vivid magenta color.
     * More vibrant than MAGENTA. Use for special highlights.
     */
    public static final String BRIGHT_MAGENTA = "\u001B[95m";

    /**
     * Bright/vivid cyan color.
     * More vibrant than CYAN. Use for important information or titles.
     */
    public static final String BRIGHT_CYAN = "\u001B[96m";

    /**
     * Bright/vivid white color.
     * More vibrant than WHITE. Use for high-contrast text.
     */
    public static final String BRIGHT_WHITE = "\u001B[97m";

    // Global UI width so all boxes/headers line up nicely
    public static final int MAIN_WIDTH = 60;

    /**
     * Private constructor to prevent instantiation.
     * BeautifulDisplay is a utility class with only static methods and should
     * never be instantiated.
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
     * This is essential for proper text alignment when using colored strings,
     * as ANSI escape sequences don't consume visual space.
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
     * Ignores ANSI color codes when calculating length.
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
     * Accounts for ANSI color codes to ensure proper alignment.
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
     * Accounts for ANSI color codes to ensure proper alignment.
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
     * Accounts for ANSI color codes to ensure proper centering.
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
     * Creates a bordered box with the title centered inside, using bright blue
     * for borders and bright cyan for the title text.
     *
     * @param title text to show inside the header
     * @param width width of the inner box (minimum will be adjusted to fit title)
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
     * Creates a simple header with horizontal lines above and below,
     * with a triangular bullet point.
     *
     * @param title text to show
     * @param color ANSI color to use for the header elements
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
     * Useful for separating sections of output.
     *
     * @param width divider length (minimum 10 characters)
     *
     * @author Denisa Cakoni
     */
    public static void printGradientDivider(int width) {
        if (width < 10) width = 10;
        System.out.println(DIM + repeat("─", width) + RESET);
    }

    /**
     * Prints a full-width section header with centered text so it lines up
     * with the main gradient header.
     *
     * Example:
     *  ────────────────────────────────────────────────
     *                 ▶ START
     *  ────────────────────────────────────────────────
     *
     * @param title the section title text
     * @param color ANSI color to use for the bar and label
     */
    public static void printFullWidthSectionHeader(String title, String color, int width) {
        if (width < visibleLength(title) + 4) {
            width = visibleLength(title) + 4;
        }

        String line = repeat("─", width);
        String headerText = color + "▶ " + BOLD + title + RESET;

        // top line
        System.out.println(color + line + RESET);
        // centered label
        System.out.println(center(headerText, width));
        // bottom line
        System.out.println(color + line + RESET);
    }

    // BOXES & LISTS



    /**
     * Prints a colored key/value table inside a box.
     * Creates a bordered table with a title and key-value pairs.
     * Automatically calculates column widths and aligns content properly,
     * accounting for ANSI color codes in the values.
     *
     * @param title       the box title (displayed centered at top)
     * @param rows        each row contains [key, value] can include ANSI colors
     * @param borderColor ANSI color constant to use for the box border
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
     * Each item is prefixed with a colored number (1, 2, 3, etc.).
     *
     * @param items       array of items to display
     * @param bulletColor ANSI color constant to use for the numbering
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
     * Prints a green success message with a checkmark icon.
     * Use for confirming successful operations.
     *
     * @param message text to print
     *
     * @author Denisa Cakoni
     */
    public static void printSuccess(String message) {
        System.out.println(BRIGHT_GREEN + "✔ " + message + RESET);
    }

    /**
     * Prints a yellow warning message with a warning icon.
     * Use for alerting users to potential issues or required actions.
     *
     * @param message text to print
     *
     * @author Denisa Cakoni
     */
    public static void printWarning(String message) {
        System.out.println(BRIGHT_YELLOW + "⚠ " + message + RESET);
    }

    /**
     * Prints a red error message with an X icon.
     * Use for reporting errors or failed operations.
     *
     * @param message text to print
     *
     * @author Denisa Cakoni
     */
    public static void printError(String message) {
        System.out.println(BRIGHT_RED + "✖ " + message + RESET);
    }

    /**
     * Prints an informational message with an info icon.
     * Use for general information or helpful tips.
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
     * Displays a message followed by animated dots. Useful for indicating
     * that a long-running operation is in progress.
     *
     * @param message message to display before the dots
     * @param delayMs how long the animation lasts in milliseconds (divided into 3 steps)
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
     * Formats a currency value as whole dollars and adds color based on value.
     * Colors applied:
     *  GREEN for positive values (income, profit)
     *  RED for negative values (expenses, loss)
     *  YELLOW for zero values
     *
     * @param value the number to format
     * @return a colored dollar string with ANSI codes (e.g., "[GREEN]$1500[RESET]")
     *
     * @author Denisa Cakoni
     */
    public static String formatCurrency(double value) {
        String formatted = String.format("$%.0f", value);

        if (value > 0) return GREEN + formatted + RESET;
        if (value < 0) return RED + formatted + RESET;
        return YELLOW + formatted + RESET;
    }


    // HIGH-LEVEL SCREENS FOR MAIN MENU FLOW

    /**
     * Prints the START screen the user sees after the title,
     * with options to sign in or create an account.
     */
    public static void printStartMenu() {
        int width = MAIN_WIDTH;

        // Big blue box (already looks nice)
        printGradientHeader("WELCOME TO PERSONAL FINANCE MANAGER", width);
        System.out.println();

        // Full-width green START bar
        printFullWidthSectionHeader("START", BRIGHT_GREEN, width);
        System.out.println();

        // Menu options: yellow numbers, default text color
        System.out.println(BRIGHT_YELLOW + "  1. " + RESET + "Sign In");
        System.out.println(BRIGHT_YELLOW + "  2. " + RESET + "Create a New Account");
        System.out.println(BRIGHT_YELLOW + "  3. " + RESET + "Exit Application");

        System.out.println();
        printGradientDivider(width);

        // Prompt in default color (no bright white)
        System.out.print("Please enter the number associated with your desired option: ");
    }
    /**
     * Prints the MAIN MENU the user sees after logging in.
     */
    public static void printMainMenuScreen(String currentUser) {
        int width = MAIN_WIDTH;

        // Big MAIN MENU header box
        printGradientHeader("MAIN MENU", width);
        System.out.println();

        // "Signed in as" line (small & dim)
        System.out.println(
                DIM + "Signed in as: " + RESET +
                        BRIGHT_CYAN + currentUser + RESET
        );
        System.out.println();

        // Full-width green bar
        printFullWidthSectionHeader("WHAT WOULD YOU LIKE TO DO?", BRIGHT_GREEN, width);
        System.out.println();

        // Options with yellow numbers, default text color
        String[] items = new String[] {
                "Finances",
                "Settings",
                "Sign Out",
                "Exit Program"
        };
        printColorfulList(items, BRIGHT_YELLOW);

        System.out.println();
        printGradientDivider(width);

        // Prompt in default color
        System.out.print("Please enter the number associated with your desired option: ");
    }

    /**
     * Prints the FINANCES menu (Upload CSV, Reports, etc.).
     */
    public static void printFinancesMenu() {
        int width = MAIN_WIDTH;

        printGradientHeader("FINANCES", width);
        System.out.println();

        printFullWidthSectionHeader("FINANCES MENU", BRIGHT_GREEN, width);
        System.out.println();

        // Yellow numbers, default words
        System.out.println(BRIGHT_YELLOW + "  1. " + RESET + "Upload CSV");
        System.out.println(BRIGHT_YELLOW + "  2. " + RESET + "Reports");
        System.out.println(BRIGHT_YELLOW + "  3. " + RESET + "Predictions");
        System.out.println(BRIGHT_YELLOW + "  4. " + RESET + "Data Management");
        System.out.println(BRIGHT_YELLOW + "  5. " + RESET + "Return to Main Menu");

        System.out.println();
        printGradientDivider(width);

        // Prompt in default color
        System.out.print("Please enter the number associated with your desired option: ");
    }


    public static void printReportsMenu(int year) {
        int width = MAIN_WIDTH;

        // Big header box: REPORTS FOR 2023
        printGradientHeader("REPORTS FOR " + year, width);
        System.out.println();

        // Section: Available Reports
        printFullWidthSectionHeader("AVAILABLE REPORTS", BRIGHT_GREEN, width);
        System.out.println();

        // Plain text (safe on any background)
        System.out.println("What kind of information would you like about data from " + year + "?");
        System.out.println();

        // Options 1–4: yellow numbers, default text
        System.out.println(BRIGHT_YELLOW + "  1. " + RESET + "Yearly Summary");
        System.out.println(BRIGHT_YELLOW + "  2. " + RESET + "Month Breakdown");
        System.out.println(BRIGHT_YELLOW + "  3. " + RESET + "Category Analysis");
        System.out.println(BRIGHT_YELLOW + "  4. " + RESET + "Full Report");
        System.out.println();

        // Controls section with matching bar
        printFullWidthSectionHeader("CONTROLS", BRIGHT_GREEN, width);
        System.out.println();

        System.out.println(BRIGHT_YELLOW + "  5. " + RESET + "View the reports for another year");
        System.out.println(BRIGHT_YELLOW + "  6. " + RESET + "Return to Finances Menu");
        System.out.println(BRIGHT_YELLOW + "  7. " + RESET + "Return to Main Menu");
        System.out.println();

        // Bottom divider + prompt
        printGradientDivider(width);
        System.out.print("Please enter the number associated with your desired option: ");
    }


    public static void printPredictionsMenu(int year) {
        int width = MAIN_WIDTH;

        // Big header box: PREDICTIONS FOR 2023
        printGradientHeader("PREDICTIONS FOR " + year, width);
        System.out.println();

        // Section: Available Predictions
        printFullWidthSectionHeader("AVAILABLE PREDICTIONS", BRIGHT_GREEN, width);
        System.out.println();

        System.out.println("What kind of predictions would you like about the data from " + year + "?");
        System.out.println();

        // Options 1–3
        System.out.println(BRIGHT_YELLOW + "  1. " + RESET + "Summary Report");
        System.out.println(BRIGHT_YELLOW + "  2. " + RESET + "Deficit Analysis");
        System.out.println(BRIGHT_YELLOW + "  3. " + RESET + "Surplus Analysis");
        System.out.println();

        // Controls section with matching bar
        printFullWidthSectionHeader("CONTROLS", BRIGHT_GREEN, width);
        System.out.println();

        System.out.println(BRIGHT_YELLOW + "  4. " + RESET + "View the Predictions for another year");
        System.out.println(BRIGHT_YELLOW + "  5. " + RESET + "Return to Finances Menu");
        System.out.println(BRIGHT_YELLOW + "  6. " + RESET + "Return to Main Menu");
        System.out.println();

        // Bottom divider + prompt (plain color)
        printGradientDivider(width);
        System.out.print("Please enter the number associated with your desired option: ");
    }

    public static void printDataManagementMenu() {
        int width = MAIN_WIDTH;

        // Big header
        printGradientHeader("DATA MANAGEMENT", width);
        System.out.println();

        // Section header
        printFullWidthSectionHeader("DATA MANAGEMENT MENU", BRIGHT_GREEN, width);
        System.out.println();

        // Yellow numbers, normal text
        System.out.println(BRIGHT_YELLOW + "  1. " + RESET + "Delete a CSV file");
        System.out.println(BRIGHT_YELLOW + "  2. " + RESET + "Return to Finances Menu");
        System.out.println(BRIGHT_YELLOW + "  3. " + RESET + "Return to Main Menu");
        System.out.println();

        printGradientDivider(width);
        System.out.print("Please enter the number associated with your desired option: ");
    }

    public static void printAccountSettingsMenu(String currentUser) {
        int width = MAIN_WIDTH;

        // Big header
        printGradientHeader("ACCOUNT SETTINGS", width);
        System.out.println();

        // Section header with username
        printFullWidthSectionHeader(currentUser + " ACCOUNT SETTINGS", BRIGHT_GREEN, width);
        System.out.println();

        // Options
        System.out.println(BRIGHT_YELLOW + "  1. " + RESET + "Change Password");
        System.out.println(BRIGHT_YELLOW + "  2. " + RESET + "Change Security Question and Answer");
        System.out.println(BRIGHT_YELLOW + "  3. " + RESET + "Delete Account");
        System.out.println(BRIGHT_YELLOW + "  4. " + RESET + "Return to Main Menu");
        System.out.println();

        printGradientDivider(width);
        System.out.print("Please enter the number associated with your desired option: ");
    }

    /**
     * Prints a generic "press enter to continue" prompt using dim text.
     */
    public static void printContinuePrompt() {
        System.out.print(DIM + "Press enter when you are ready to move on..." + RESET);
    }

}
