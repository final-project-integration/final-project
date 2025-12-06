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

        incomeMonthly  = stripCents(incomeMonthly);
        expenseMonthly = stripCents(expenseMonthly);
        balanceMonthly = stripCents(balanceMonthly);

        System.out.printf("│ %-12s │ %12s │ %13s │ %13s │%n",
                monthName, incomeMonthly, expenseMonthly, balanceMonthly);
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

            amount = stripCents(amount);

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
        for (String a : amounts) {
            if (a.length() > amountWidth) {
                amountWidth = a.length();
            }
        }

        String header = String.format("│ %-3s │ %-" + nameWidth + "s │ %-" + amountWidth + "s │",
                "#", "Category", "Amount");
        String border = "─".repeat(header.length() - 2);

        String borderColor = BeautifulDisplay.BRIGHT_WHITE;

        System.out.println(borderColor + "┌" + border + "┐" + BeautifulDisplay.RESET);
        System.out.println(borderColor + header + BeautifulDisplay.RESET);
        System.out.println(borderColor + "├" + border + "┤" + BeautifulDisplay.RESET);

        for (int i = 0; i < names.size(); i++) {
            String row = String.format("│ %3d │ %-" + nameWidth + "s │ %" + amountWidth + "s │",
                    (i + 1), names.get(i), amounts.get(i));
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
     * @param highestMonth          the month with highest expenses
     * @param topSpendingCategory   the category with highest expenses
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

        String[] insights = {
                "📈 Highest spending month: " +
                        BeautifulDisplay.BOLD + BeautifulDisplay.BRIGHT_YELLOW +
                        highestMonth + BeautifulDisplay.RESET,
                "🏆 Top spending category: " +
                        BeautifulDisplay.BOLD + BeautifulDisplay.BRIGHT_MAGENTA +
                        topSpendingCategory + BeautifulDisplay.RESET,
                "💰 Overall net balance: " + BeautifulDisplay.BOLD + netBalancePretty +
                        BeautifulDisplay.RESET
        };

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
}
