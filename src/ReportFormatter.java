import java.util.List;

/**
 * Formatter produces user-friendly string representations of report data,
 * including currency values and text-based tables for console or log output.
 * 
 * @author Angelo Samir Alvarez
 * @author Furkan Bilgi
 * @author Chukwuemeka Okwuka
 * @author Omar Piron
 */
public class ReportFormatter {

    /**
     * Attaches a currency symbol to the amount string.
     *
     * @param amountString    a numeric value represented as a string
     * @param currencySymbol  something like "$", "€", etc.
     * @return a formatted currency string
     */
    public String formatCurrency(String amountString, String currencySymbol)  {
        return currencySymbol + amountString;
    }

    /**
     * Builds a monospace table string from headers and rows.
     *
     * @param headers list of column headers
     * @param rows    list of row data, where each row is a List<String>
     * @return a printable table string
     */
    public String formatTable(List<String> headers, List<List<String>> rows) {
        return "";
    }

    /**
     * Creates a readable bullet list for category totals.
     *
     * @param categoryLines a list of strings in the format "Category:Amount"
     * @param currencySymbol a symbol like "$"
     * @return a multi-line string with each category on its own line
     */
    public String formatCategoryTotals(List<String> categoryLines, String currencySymbol) {
        return "";
    }

    /**
     * Produces a header/footer block with the main content printed elsewhere.
     *
     * @param title  the title text to display at the top
     * @param footer the footer text to display at the bottom
     * @return a string that callers can concatenate with report bodies
     */
    public String printHeaderFooter(String title, String footer) {
        return "";
    }
}
