package com.pfm.reports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Formatter produces user-friendly string representations of report data,
 * including currency values and text-based tables for console or log output.
 * 
 * @Author Angelo Samir Alvarez 
 * @Author Furkan Bilgi
 * @Author Chukwuemeka Okwuka
 * @Author Omar Piron
 */
public class ReportFormatter {

    /**
     * Formats a monetary amount for display using the given {@link Locale}.
     *
     * @param amount the amount to format, could be negative for expenses
     * @param locale the locale that determines currency symbol and grouping
     * @return a localized currency string
     * @author Angelo Samir Alvarez 
     */
    public String formatCurrency(BigDecimal amount, Locale locale) {
        return "";
    }

    /**
     * Builds a simple monospace table string from headers and rows.
     * Column widths should fit the widest cell in each column.
     *
     * @param headers ordered list of column headers
     * @param rows list of rows, each row is an ordered list of column strings
     * @return a printable table string
     * @author Angelo Samir Alvarez 
     */
    public String formatTable(List<String> headers, List<List<String>> rows) {
        return "";
    }

    /**
     * Formats category totals into a bullet list for quick reading.
     *
     * @param categoryTotals mapping of Category → total amount
     * @param locale locale for currency formatting
     * @return a multi line string such as - "Groceries: $300.00"
     * @author Angelo Samir Alvarez 
     */
    public String formatCategoryTotals(Map<String, BigDecimal> categoryTotals, Locale locale) {
        return "";
    }

    /**
     * Produces a header/footer block that can wrap report content.
     *
     * @param title  the title text to display at the top
     * @param footer the footer text to display at the bottom
     * @return a string that callers can concatenate with report bodies
     */
    public String printHeaderFooter(String title, String footer) {
        return "";
    }
}
