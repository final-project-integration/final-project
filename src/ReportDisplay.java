//Integration

import java.util.ArrayList;

/**
 * ReportDisplay handles all formatting and console output for financial reports.
 * This class separates presentation logic from business logic, making reports
 * easier to maintain and customize.
 *
 * @author Denisa Cakoni
 * @author Kapil Tamang
 */
public class ReportDisplay {

    /**
     * Prints a colored header with horizontal lines of a fixed width.
     *
     * @param title text to show in the center
     * @param color ANSI color from BeautifulDisplay
     * @param width total width of the header line
     *
     * @author Denisa Cakoni
     */
    public void printFixedWidthHeader(String title, String color, int width) {
        if (title == null) {
            title = "";
        }

        if (width < title.length() + 4) {
            width = title.length() + 4;
        }

        StringBuilder line = new StringBuilder();
        for (int i = 0; i < width; i++) {
            line.append('─');
        }

        int padding = width - title.length();
        int left = padding / 2;
        int right = padding - left;

        StringBuilder middle = new StringBuilder();
        for (int i = 0; i < left; i++) {
            middle.append(' ');
        }
        middle.append(title);
        for (int i = 0; i < right; i++) {
            middle.append(' ');
        }

        System.out.println(color + line.toString() + BeautifulDisplay.RESET);
        System.out.println(color + middle.toString() + BeautifulDisplay.RESET);
        System.out.println(color + line.toString() + BeautifulDisplay.RESET);
    }


    /**
     * Drops a trailing ".00" from a currency string.
     *
     * @param amount the original currency string
     * @return the same string without trailing .00
     *
     * @author Denisa Cakoni
     */
    public String stripCents(String amount) {
        if (amount == null) return "";
        amount = amount.trim();
        if (amount.endsWith(".00")) {
            return amount.substring(0, amount.length() - 3);
        }
        return amount;
    }

    /**
     * Prints the yearly summary section of a financial report.
     *
     * @param year   the year being reported
     * @param yearly the YearlySummary object containing totals
     *
     * @author Denisa Cakoni
     */
    public void printYearlySection(int year, ReportManager.YearlySummary yearly) {
        BeautifulDisplay.printGradientHeader("FINANCIAL REPORT - " + year, 70);

        String incomeRaw  = "$" + yearly.getTotalIncome();
        String expenseRaw = "$" + yearly.getTotalExpenses();

        expenseRaw = expenseRaw.replace("$-", "$");

        String incomeStr  = BeautifulDisplay.GREEN  + stripCents(incomeRaw)  + BeautifulDisplay.RESET;
        String expenseStr = BeautifulDisplay.RED    + stripCents(expenseRaw) + BeautifulDisplay.RESET;

        double netVal = 0.0;
        try {
            netVal = Double.parseDouble(yearly.getNetBalance());
        } catch (Exception ignored) { }

        String netColored = BeautifulDisplay.formatCurrency(netVal);

        String[][] summaryData = {
                {"Total Income",   incomeStr},
                {"Total Expenses", expenseStr},
                {"Net Balance",    netColored}
        };

        BeautifulDisplay.printKeyValueBox("YEARLY SUMMARY", summaryData, BeautifulDisplay.BRIGHT_CYAN);
        BeautifulDisplay.printGradientDivider(70);
    }

    /**
     * Prints the monthly breakdown section of a financial report.
     *
     * @param year    the year being reported
     * @param monthly the list of monthly summary lines
     *
     * @author Denisa Cakoni
     * @author Kapil Tamang
     */
    public void printMonthlySection(int year, ArrayList<String> monthly) {
        printFixedWidthHeader("MONTHLY BREAKDOWN - " + year,
                BeautifulDisplay.BRIGHT_MAGENTA, 70);

        System.out.println("┌──────────────┬──────────────┬───────────────┬───────────────┐");
        System.out.printf("│ %-12s │ %-12s │ %-13s │ %-13s │%n",
                "Month", "Income", "Expenses", "Balance");
        System.out.println("├──────────────┼──────────────┼───────────────┼───────────────┤");

        printMonthlyRow(monthly.size() > 0  ? monthly.get(0)  : null, "January");
        printMonthlyRow(monthly.size() > 1  ? monthly.get(1)  : null, "February");
        printMonthlyRow(monthly.size() > 2  ? monthly.get(2)  : null, "March");
        printMonthlyRow(monthly.size() > 3  ? monthly.get(3)  : null, "April");
        printMonthlyRow(monthly.size() > 4  ? monthly.get(4)  : null, "May");
        printMonthlyRow(monthly.size() > 5  ? monthly.get(5)  : null, "June");
        printMonthlyRow(monthly.size() > 6  ? monthly.get(6)  : null, "July");
        printMonthlyRow(monthly.size() > 7  ? monthly.get(7)  : null, "August");
        printMonthlyRow(monthly.size() > 8  ? monthly.get(8)  : null, "September");
        printMonthlyRow(monthly.size() > 9  ? monthly.get(9)  : null, "October");
        printMonthlyRow(monthly.size() > 10 ? monthly.get(10) : null, "November");
        printMonthlyRow(monthly.size() > 11 ? monthly.get(11) : null, "December");

        System.out.println("└──────────────┴──────────────┴───────────────┴───────────────┘");
        BeautifulDisplay.printGradientDivider(70);
    }

    /**
     * Prints a single row of the monthly table.
     *
     * @param line             Example from 'monthly':
     *                         "January: Income=$3700.00, Expenses=$-1740.00, Balance=$1960.00"
     *                         or null if no data for that month.
     * @param fallbackMonth    month name to use if the line is null or badly formatted.
     *
     * @author Kapil Tamang
     */
    private void printMonthlyRow(String line, String fallbackMonth) {
        String monthName      = fallbackMonth;
        String incomeMonthly  = "$0.00";
        String expenseMonthly = "$0.00";
        String balanceMonthly = "$0.00";

        if (line != null && !line.isBlank()) {
            int colonIndex = line.indexOf(':');

            if (colonIndex >= 0) {
                monthName = line.substring(0, colonIndex).trim();
                String rest = line.substring(colonIndex + 1).trim();

                String[] parts = rest.split(",");
                for (String part : parts) {
                    part = part.trim();
                    if (part.startsWith("Income=")) {
                        incomeMonthly = part.substring("Income=".length()).trim();
                    } else if (part.startsWith("Expenses=")) {
                        expenseMonthly = part.substring("Expenses=".length()).trim();
                    } else if (part.startsWith("Balance=")) {
                        balanceMonthly = part.substring("Balance=".length()).trim();
                    }
                }
            } else {
                monthName = line.trim();
            }
        }

        // strip .00
        incomeMonthly  = stripCents(incomeMonthly);
        // remove minus sign for display, but still conceptually “expense”
        expenseMonthly = stripCents(expenseMonthly).replace("-", "");
        balanceMonthly = stripCents(balanceMonthly);

        // align inside the column *then* wrap with color
        String incomeField = BeautifulDisplay.GREEN +
                String.format("%12s", incomeMonthly) + BeautifulDisplay.RESET;
        String expenseField = BeautifulDisplay.RED +
                String.format("%13s", expenseMonthly) + BeautifulDisplay.RESET;
        // balance stays white
        String balanceField = String.format("%13s", balanceMonthly);

        System.out.printf("│ %-12s │ %s │ %s │ %s │%n",
                monthName, incomeField, expenseField, balanceField);
    }

    /**
     * Prints the category summary section of a financial report.
     *
     * @param year              the year being reported
     * @param categorySummaries the list of category summary lines
     *
     * @author Denisa Cakoni
     */
    public void printCategorySection(int year, ArrayList<String> categorySummaries) {
        printFixedWidthHeader("CATEGORY SUMMARY - " + year,
                BeautifulDisplay.BRIGHT_YELLOW, 70);

        if (categorySummaries == null || categorySummaries.isEmpty()) {
            System.out.println("No category data available for this year.");
            BeautifulDisplay.printGradientDivider(70);
            return;
        }

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> amounts = new ArrayList<>();

        for (String line : categorySummaries) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String text = line.trim();
            String name = text;
            String amount = "";

            int colonIndex = text.indexOf(':');
            if (colonIndex >= 0) {
                name = text.substring(0, colonIndex).trim();
                amount = text.substring(colonIndex + 1).trim();
            }

            amount = stripCents(amount); // e.g. "$-500.00" -> "$-500"

            names.add(name);
            amounts.add(amount);
        }

        int nameWidth = "Category".length();
        int amountWidth = "Amount".length();

        for (String n : names) {
            if (n.length() > nameWidth) {
                nameWidth = n.length();
            }
        }
        // measure width WITHOUT the minus sign so the column fits cleaned amounts
        for (String a : amounts) {
            String display = a.replace("-", "");
            if (display.length() > amountWidth) {
                amountWidth = display.length();
            }
        }

        String header = String.format("│ %-3s │ %-" + nameWidth + "s │ %-" + amountWidth + "s │",
                "#", "Category", "Amount");
        String border = "─".repeat(header.length() - 2);

        String borderColor = BeautifulDisplay.RESET;

        System.out.println(borderColor + "┌" + border + "┐" + BeautifulDisplay.RESET);
        System.out.println(borderColor + header + BeautifulDisplay.RESET);
        System.out.println(borderColor + "├" + border + "┤" + BeautifulDisplay.RESET);

        for (int i = 0; i < names.size(); i++) {
            String name   = names.get(i);
            String amount = amounts.get(i);          // e.g. "$1200" or "$-500"

            double val = parseAmount(amount);        // <- uses your helper

            // display with NO minus sign
            String displayAmount = amount.replace("-", "");
            String paddedAmount  = String.format("%" + amountWidth + "s", displayAmount);

            String coloredAmount;
            if (val < 0) {
                // expenses -> red (no minus)
                coloredAmount = BeautifulDisplay.RED + paddedAmount + BeautifulDisplay.RESET;
            } else if (val > 0) {
                // income -> green
                coloredAmount = BeautifulDisplay.GREEN + paddedAmount + BeautifulDisplay.RESET;
            } else {
                // zero -> plain
                coloredAmount = paddedAmount;
            }

            String row = String.format("│ %3d │ %-" + nameWidth + "s │ %s │",
                    (i + 1), name, coloredAmount);
            System.out.println(borderColor + row + BeautifulDisplay.RESET);
        }

        System.out.println(borderColor + "└" + border + "┘" + BeautifulDisplay.RESET);
        BeautifulDisplay.printGradientDivider(70);
    }
    /**
     * Prints the full financial report.
     *
     * @param year              the year being reported
     * @param yearly            the yearly summary object
     * @param monthly           the monthly summary lines
     * @param categorySummaries the category summary lines
     *
     * @author Denisa Cakoni
     */
    public void printFullReport(int year,
                                ReportManager.YearlySummary yearly,
                                ArrayList<String> monthly,
                                ArrayList<String> categorySummaries) {
        printYearlySection(year, yearly);
        printMonthlySection(year, monthly);
        printCategorySection(year, categorySummaries);
    }

    /**
     * Prints the financial insights section.
     *
     * @param highestMonth          the month with the highest expenses
     * @param topSpendingCategory   the category with the highest expenses
     * @param netBalancePretty      the formatted net balance
     * @param negativeBalanceMonths list of months with negative balance
     * @param includeBanner         whether to print header/footer
     *
     * @author Denisa Cakoni
     */
    public void printAnalysisSection(String highestMonth,
                                     String topSpendingCategory,
                                     String netBalancePretty,
                                     ArrayList<String> negativeBalanceMonths,
                                     boolean includeBanner) {

        if (includeBanner) {
            printFixedWidthHeader("FINANCIAL INSIGHTS",
                    BeautifulDisplay.BRIGHT_GREEN, 70);
        }

        // Month label (e.g. "May $-2513.00") -> "May" in magenta, amount red, no minus
        String line1 = "📈 Highest spending month: " +
                colorLabelAndCurrency(highestMonth, BeautifulDisplay.BRIGHT_MAGENTA);

        // Category label (e.g. "Home $-6000.00") -> "Home" in blue, amount red, no minus
        String line2 = "🏆 Top spending category: " +
                colorLabelAndCurrency(topSpendingCategory, BeautifulDisplay.BRIGHT_BLUE);

        // Net balance string is already colored by ReportFormatter/formatCurrency
        String line3 = "💰 Overall net balance: " + netBalancePretty;

        String[] insights = { line1, line2, line3 };
        BeautifulDisplay.printColorfulList(insights, BeautifulDisplay.BRIGHT_CYAN);

        if (negativeBalanceMonths == null || negativeBalanceMonths.isEmpty()) {
            BeautifulDisplay.printSuccess(
                    "All months had a non-negative balance. Nice job managing your finances!");
        } else {
            BeautifulDisplay.printWarning(
                    negativeBalanceMonths.size() + " month(s) had a negative balance:");
            String[] neg = new String[negativeBalanceMonths.size()];
            for (int i = 0; i < negativeBalanceMonths.size(); i++) {
                neg[i] = BeautifulDisplay.RED + "⚠ " + negativeBalanceMonths.get(i)
                        + BeautifulDisplay.RESET;
            }
            BeautifulDisplay.printColorfulList(neg, BeautifulDisplay.RED);
        }

        BeautifulDisplay.printGradientDivider(70);
    }
    /**
     * Finds the first $-style amount in the text (e.g. "-$2153.00" or "$-2153.00"),
     * removes the minus sign for display, and colors the number:
     *  - RED for negative
     *  - GREEN for positive
     *  - YELLOW for zero
     *
     * @author Denisa Cakoni
     */
    private String colorCurrencyInline(String text) {
        if (text == null) return "";

        int dollar = text.indexOf('$');
        if (dollar == -1) {
            return text; // no money, nothing to color
        }

        int start = dollar;
        // include leading '-' if present BEFORE the '$'
        if (dollar > 0 && text.charAt(dollar - 1) == '-') {
            start = dollar - 1;
        }

        int end = dollar + 1;
        // allow optional '-' after '$', plus digits, dot, comma
        while (end < text.length()) {
            char c = text.charAt(end);
            if (Character.isDigit(c) || c == '.' || c == ',' || c == '-') {
                end++;
            } else {
                break;
            }
        }

        String money = text.substring(start, end); // e.g. "-$2153.00" or "$-2153.00"
        String cleaned = money.replace("$", "").replace(",", "").trim();

        double value;
        try {
            value = Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return text; // fallback if weird format
        }

        double absVal = Math.abs(value);
        String plain = String.format("$%.0f", absVal); // no minus in display

        String colored;
        if (value > 0) {
            colored = BeautifulDisplay.GREEN + plain + BeautifulDisplay.RESET;
        } else if (value < 0) {
            colored = BeautifulDisplay.RED + plain + BeautifulDisplay.RESET;
        } else {
            colored = BeautifulDisplay.YELLOW + plain + BeautifulDisplay.RESET;
        }

        // put the colored amount back in place of the original money substring
        return text.substring(0, start) + colored + text.substring(end);
    }

    /**
     * Colors the first word in a label (e.g., "May $-2513.00") with the given color,
     * then colors any currency part inline (using red/green/yellow, no minus sign).
     *
     * @author Denisa Cakoni
     */
    private String colorLabelAndCurrency(String label, String labelColor) {
        if (label == null) return "";

        // Remove parentheses like "May (-$2153.00)" -> "May -$2153.00"
        label = label.replace("(", "").replace(")", "").trim();

        String[] parts = label.split("\\s+", 2);
        String name = parts[0];                         // "May" or "Home"
        String rest = (parts.length > 1) ? " " + parts[1] : "";

        String coloredName = labelColor + name + BeautifulDisplay.RESET;

        // Now color the currency inside the whole string (no minus, red/green)
        return colorCurrencyInline(coloredName + rest);
    }
    /** Parses a currency-like string such as "$-1740" into a double. */
    private double parseAmount(String amount) {
        if (amount == null) return 0.0;
        String cleaned = amount.replace("$", "").replace(",", "").trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
