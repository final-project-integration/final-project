import java.util.List;

/**
 * Formatter produces user-friendly string representations of report data,
 * including currency values and text-based tables for console or log output.
 * 
 * @author Angelo Samir Alvarez
 */
public class ReportFormatter {

    public ReportFormatter(){
    }
    /**
     * Attaches a currency symbol to the amount string.
     *
     * @param amountString    a numeric value represented as a string
     * @param currencySymbol  something like "$", "€", etc.
     * @return a formatted currency string
     */
    public String formatCurrency(String amountString, String currencySymbol)  {
        if (amountString == null || currencySymbol == null) {
            throw new IllegalArgumentException("amountString and currencySymbol cannot be null");
        }
        return currencySymbol + amountString;
    }

    /**
     * Builds a monospace table string from headers and rows.
     *
     * @param headers list of column headers
     * @param rows    list of row data, where each row is a {@code List<String>}. 
     * @return a printable table string
     */
    public String formatTable(List<String> headers, List<List<String>> rows) {
        if (headers == null || rows == null) {
            throw new IllegalArgumentException("Headers and rows cannot be null");
        }

        int columns = headers.size();
        int[] widths = new int[columns];

        
        for (int i = 0; i < columns; i++) {
            widths[i] = headers.get(i).length();
        }

        for (List<String> row : rows) {
            if (row.size() != columns) {
                throw new IllegalArgumentException("Row column count does not match header count");
            }
            for (int i = 0; i < columns; i++) {
                String cell = row.get(i);
                if (cell != null && cell.length() > widths[i]) {
                    widths[i] = cell.length();
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < columns; i++) {
            sb.append(pad(headers.get(i), widths[i])).append("  ");
        }
        sb.append("\n");

        for (int w : widths) {
            sb.append(repeat("-", w)).append("  ");
        }
        sb.append("\n");
        
        for (List<String> row : rows) {
            for (int i = 0; i < columns; i++) {
                String cell = row.get(i) == null ? "" : row.get(i);
                sb.append(pad(cell, widths[i])).append("  ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Creates a readable bullet list for category totals.
     *
     * @param categoryLines a list of strings in the format "Category:Amount"
     * @param currencySymbol a symbol like "$"
     * @return a multi-line string with each category on its own line
     */
    public String formatCategoryTotals(List<String> categoryLines, String currencySymbol) {
        if (categoryLines == null || currencySymbol == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        StringBuilder sb = new StringBuilder();

        for (String line : categoryLines) {
            if (line == null) {
                continue;
            }
            int idx = line.indexOf(':');
            if (idx == -1) {
            
                sb.append("- ").append(line).append("\n");
                continue;
            }

            String category = line.substring(0, idx).trim();
            String amount = line.substring(idx + 1).trim();

            sb.append("- ")
              .append(category)
              .append(": ")
              .append(currencySymbol)
              .append(amount)
              .append("\n");
        }

        return sb.toString();
    }

    /**
     * Produces a header/footer block with the main content printed elsewhere.
     *
     * @param title  the title text to display at the top
     * @param footer the footer text to display at the bottom
     * @return a string that callers can concatenate with report bodies
     */
    public String printHeaderFooter(String title, String footer) {
        StringBuilder sb = new StringBuilder();

        sb.append("==== ").append(title).append(" ====").append("\n\n");
        sb.append("---- ").append(footer).append(" ----").append("\n");

        return sb.toString();
    }

    private String pad(String text, int width) {
        if (text == null) {
            text = "";
        }
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

}
