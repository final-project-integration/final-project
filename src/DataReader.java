
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DataReader class for the Prediction Team Module Reads, validates, calculates
 * income and expenses, compares calculations, and summarizes report.
 *
 * @author Jessica Barrera Saguay
 */
public class DataReader {

    final private List<String> categoriesList = new ArrayList<>();
    final private List<Integer> amountsList = new ArrayList<>();
    
    

    private static final List<String> incomeCategories = Arrays.asList(
		"Compensation", 
		"Allowance", 
		"Investments",
        "Other"); // List that stores the valid income categories

    private static final List<String> expenseCategories = Arrays.asList(
		"Home", 
		"Rent", 
		"Utilities", 
		"Food", 
		"Appearance",
        "Work", 
		"Education", 
		"Transportation", 
		"Entertainment", 
		"Professional Services", 
		"Other"); // List that stores the valid expense categories

	public List<String> getCategories() {
		return new ArrayList<>(categoriesList); // defensive copy
	}
	public List<Integer> getAmounts() {
		return new ArrayList<>(amountsList); // defensive copy
	}

	public static boolean isIncomeCategory(String category) {
        return incomeCategories.contains(category);
    }

    public static boolean isExpenseCategory(String category) {
        return expenseCategories.contains(category);
    }
    /**
     * Reads stored budget data from the file provided. This method does not
     * return a value but prepares the data for further processing.
     *  @param fileName the path to the CSV file to load
     *  @bug Fixed: added ability for user to use different file names instead of hardcoding it
     * @author Jessica Barrera Saguay
     */
    public void readData(String fileName) {

        File file = new File(fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine(); // Read the first line
            if (headerLine == null) {
                System.err.println("Error: File is empty.");
                return;
            }
            // checks if the header is valid
            String[] headerFields = headerLine.split(",");
            String[] expectedHeader = {"Date", "Category", "Amount"};
			
			if (headerFields.length < expectedHeader.length) {
				System.err.println("Error: Header has missing columns.");
				return;
			}

            for (int i = 0; i < expectedHeader.length; i++) {
                if (!headerFields[i].trim().equalsIgnoreCase(expectedHeader[i])) {
                    System.err.println("Error: Expected " + expectedHeader[i] + " but found " + headerFields[i]);
                    return;
                }
            }

            String line;
            while ((line = br.readLine()) != null) { 
                String[] columns = line.split(",");

                if (columns.length != 3) {  // should check if it is greater than, or change to != 3
    				System.err.println("Error: row does not have exactly 3 columns: " + line);
					continue;
				}
                String date = columns[0].trim();
                String category = columns[1].trim();
                String amount = columns[2].trim();

                // If row is not valid then it will continue (or skip it)
                if (!isDataValid(date, category, amount)) {
                    System.err.println("Error validating the row: " + line);
                    continue;
                }
                categoriesList.add(category);
                amountsList.add(Integer.valueOf(amount)); // amountsList.add(Integer.parseInt(amount)); --> I think both work but just to be safe added valueOf so the complier doesn't complain -- Tanzina

            }
        } catch (FileNotFoundException e) { // If there is no file
            System.err.println("Error: File not found.");
        } catch (IOException e) { // If something went wrong
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    /**
     * Validates that the data in the file is correct and has no missing
     * information.
     *
     * @return {@code true} if the data is valid, {@code false} otherwise
     * @author Jessica Barrera Saguay
     */
    public boolean isDataValid(String date, String category, String amount) {
        boolean isValid = true;

        if (date == null || date.trim().isEmpty()) {
            System.err.println("Error: Date is missing.");
            isValid = false;
        } else {
            // check to see if date is in valid date format and must be from the same
            // year(example: 2024)
            if (!date.matches("^(0[1-9]|1[0-2])\\/(0[1-9]|[12]\\d|3[01])\\/2024$")) {
                isValid = false;
                System.err.println("Error: Date must be in MM/DD/YYYY format. Invalid date: " + date);
            }
        }

        if (category == null || category.trim().isEmpty()) {
            isValid = false;
        } else {
            // check if category is a valid category from lists
            if (!incomeCategories.contains(category) && !expenseCategories.contains(category)) {
                System.err.println("Error: Category " + category + " is not recognized.");
                isValid = false;
            }

            if (category.matches(".*\\d.*")) {
                System.err.println("Error: Category contains numbers: " + category);
                isValid = false;

            }
        }
        // check to see if amount is an integer
        if (amount == null || amount.trim().isEmpty()) {
            isValid = false;
        } else {

            if (!amount.matches("^-?\\d+$")) { // positive or negative integer
                isValid = false;
                System.err.println("Error: Amount is not an integer.");
            }
        }
        return isValid;
    }

    /**
     * Calculates the total income by adding up all sources of income such as
     * compensation, allowance, investments, and other money received (e.g.,
     * alimony, child support, or government benefits like unemployment or
     * Social Security).
     *
     * @return the sum of all income sources
     * @author Jessica Barrera Saguay
     */
    public int calculateTotalIncome() {
        int totalIncome = 0;
        for (int i = 0; i < categoriesList.size(); i++) {
            if (incomeCategories.contains(categoriesList.get(i))) {
                totalIncome += amountsList.get(i);
            }
        }
        return totalIncome;
    }

	public int getTotalIncome() {
		return calculateTotalIncome();
	}

    /**
     * Calculates the total expenses by adding up all expenses such as home,
     * utilities, food, appearance, work, education, transportation,
     * entertainment, professional services, and other expenses.
     *
     * @return the total amount of all expenses combined
     * @author Jessica Barrera Saguay
     */
    public int calculateTotalExpenses() {
        int totalExpenses = 0;
        for (int i = 0; i < categoriesList.size(); i++) {
            if (expenseCategories.contains(categoriesList.get(i))) {
                totalExpenses += amountsList.get(i);
            }
        }
        return totalExpenses;
    }
	public int getTotalExpenses() {
		return calculateTotalExpenses();
	}

    /**
     * Compares the total income and total expenses to determine if there is a
     * surplus.
     *
     * @param totalIncome the total income calculated from all sources
     * @param totalExpenses the total amount of all expenses
     * @return {@code true} if total income is greater than total expenses;
     * {@code false} otherwise
     * @author Jessica Barrera Saguay
     */
    public boolean compareIncomeVsExpenses(int totalIncome, int totalExpenses) {
        return totalIncome > totalExpenses;
    }

    /**
     * Creates and displays a summary report using the calculated income and
     * expense data. The report may include totals and indicate whether there is
     * a surplus or deficit.
     *
     * @return the summary report
     * @author Jessica Barrera Saguay
     */
    public String createSummaryReport() {
        StringBuilder report = new StringBuilder();

        int totalIncome = calculateTotalIncome();
        int totalExpenses = calculateTotalExpenses();
        int netTotal = totalIncome - totalExpenses;

        report.append("===== Summary Report =====\n\n");
        report.append("Total Income: $").append(String.format("%d", totalIncome)).append("\n");
        report.append("Total Expenses: $").append(String.format("%d", totalExpenses)).append("\n");
        report.append("Net Total: $").append(String.format("%d", (netTotal))).append("\n");

        return report.toString();
    }

    public List<String> getCategoriesList() {
        return categoriesList;
    }
}
