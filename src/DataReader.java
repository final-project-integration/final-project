//Prediction Team Module

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataReader class for the Prediction Team Module reads, validates, calculates
 * income and expenses, compares calculations, and summarizes report. This class
 * provides functionality to:
 * <ul>
 * <li>Read budget data from a CSV file or Budget object</li>
 * <li>Validate date, category, and amount fields</li>
 * <li>Store categories and amounts in internal lists</li>
 * <li>Compute totals for income, expenses, and individual categories</li>
 * <li>Provide defensive copies of stored data for external use</li>
 * </ul>
 * The class maintains separate lists for categories and amounts, and enforces
 * consistency rules such as:
 * <ul>
 * <li>All dates must be in MM/DD/YYYY format and must be from the same year
 * </li>
 * <li>All transactions must belong to a recognized income or expense category
 * </li>
 * <li>Income amounts must be positive and expense amounts must be negative</li>
 * </ul>
 * The valid income categories list is: "compensation", "allowance", "investments", "other"
 * The valid expense categories list is: "home", "rent", "utilities", "food", "appearance", "work", "education", "transportation", "entertainment". "professional services"
 * The category "other" is only for income and cannot be used for expenses.
 * @author Jessica Barrera Saguay
 */


public class DataReader {

	/**
	 * Default constructor for the DataReader class.
	 * Initializes an empty reader with no loaded budget or CSV data.
	 * Provides a clean starting point before calling readData() or readFromBudget().
	 * @author Jessica Barrera Saguay
	 */
	public DataReader() {
	    // fields start empty.
	}

	/**
	 * Stores the list of category names extracted from the data and can correspond
	 * to either an income or expense category.
	 */
	final private List<String> categoriesList = new ArrayList<>();
	/**
	 * Stores the list of amounts extracted from the data. Amounts are positive for
	 * income categories and negative for expense categories.
	 */
	final private List<Integer> amountsList = new ArrayList<>();
	/**
	 * Tracks the first year encountered in the data set and is used to ensure that
	 * all dates have the same year.
	 */
	private Integer firstYear = null; // Used for isDataValid method

	/**
	 * List of valid income categories. Used for validation and classification of
	 * input data.
	 */
	private static final List<String> incomeCategories = Arrays.asList("compensation", "allowance", "investments",
			"other"); // List that stores the valid income categories
	/**
	 * List of valid expense categories. Used for validation and classification of
	 * input data.
	 */
	private static final List<String> expenseCategories = Arrays.asList("home", "rent", "utilities", "food",
			"appearance", "work", "education", "transportation", "entertainment", "professional services"); // List that stores the valid expense categories

	/**
	 * Tracks totals for the "other" category separately
	 * @bug 68362783: The "other" category is not correctly classified as income or expense. 
	 * Fix by classifying "other" as income or expense based on the amount sign.
	 * @author Jessica Barrera Saguay
	 */
	 
	private int otherIncome = 0;
	private int otherExpense = 0;
	
	/**
	 * Returns the total income from the "other" category.
	 *
	 * @return the sum of all "other" income amounts
	 * @author Jessica Barrera Saguay
	 */


	public int getOtherIncome() {
		return otherIncome;
	}

	/**
	 * Returns the total expense from the "other" category.
	 *
	 * @return the sum of all "other" expense amounts
	 * @author Jessica Barrera Saguay
	 */
	public int getOtherExpense() {
		return otherExpense;
	}

	/**
	 * Returns a defensive copy of all category names recorded from the user's data.
	 *
	 * @return a new list containing all stored categories
	 * @author Jessica Barrera Saguay
	 */
	public List<String> getCategories() {
		return new ArrayList<>(categoriesList); // defensive copy
	}

	/**
	 * Returns a defensive copy of all recorded amounts.
	 *
	 * @return a new list containing all stored amounts
	 * @author Jessica Barrera Saguay
	 */
	public List<Integer> getAmounts() {
		return new ArrayList<>(amountsList); // defensive copy
	}

	/**
	 * Determines whether the given category is an income category.
	 * 
	 * @param category the category name to evaluate
	 * @return {@code true} if the category is listed as an income category;
	 *         {@code false} otherwise
	 * @author Jessica Barrera Saguay
	 */
	public static boolean isIncomeCategory(String category) {
		return incomeCategories.contains(category);
	}

	/**
	 * Determines whether the given category is an expense category.
	 * 
	 * @param category the category name to evaluate
	 * @return {@code true} if the category is an expense category; {@code false}
	 *         otherwise
	 * @author Jessica Barrera Saguay
	 */
	public static boolean isExpenseCategory(String category) {
		return expenseCategories.contains(category);
	}



	/**
	 * Reads and processes stored budget data from the CSV file provided.
	 * 
	 * This method validates the file header, reads each data row, checks
	 * formatting, and logical errors such as invalid dates, categories, or amounts,
	 * and stores valid entries into internal lists for later analysis. Invalid rows
	 * are skipped and error messages are printed.
	 * 
	 * @param fileName the path to the CSV file to be read. The file must contain
	 *                 exactly three columns labeled: Date, Category, and Amount
	 * @author Jessica Barrera Saguay
	 */
	public void readData(String fileName) {


		File file = new File(fileName);
		// Tracks totals for the "other" category separately

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String headerLine = br.readLine(); // Read the first line
			if (headerLine == null) {
				System.err.println("Error: File is empty.");
				return;
			}
			// checks if the header is valid
			String[] headerFields = headerLine.split(",");
			String[] expectedHeader = { "Date", "Category", "Amount" }; // header that is expected from a correctly
																		// formatted CSV file

			// check if header length is less than the expected header length
			if (headerFields.length < expectedHeader.length) {
				System.err.println("Error: Header has missing columns.");
				return;
			}

			for (int i = 0; i < expectedHeader.length; i++) {
				if (!headerFields[i].trim().equalsIgnoreCase(expectedHeader[i])) { // header is not the same as expected
																					// header length
					System.err.println("Error: Expected " + expectedHeader[i] + " but found " + headerFields[i]);
					return;
				}
			}

			// read through the rest of the file
			String line;
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");

				if (columns.length != 3) { // should check if it is greater than, or change to != 3
					System.err.println("Error: row does not have exactly 3 columns: " + line);
					continue;
				}
				String date = columns[0].trim();
				String category = columns[1].trim().toLowerCase(); //turns categories from file to lower case
				String amount = columns[2].trim();

				// If row is not valid then it will continue (or skip it)
				if (!isDataValid(date, category, amount)) {
					System.err.println("Error validating the row: " + line);
					continue;
				}
				int amountInt = Integer.parseInt(amount);

				// NEW CODE Dec 9 classify "other" as income or expense
				if (category.equals("other")) { // if the category is "other"
					if (amountInt > 0) { // if amount is positive
						otherIncome += amountInt; // makes it positive
					} else { // if amount is negative
						otherExpense += amountInt; // stays negative
					}
				}
				// Store the data normally
				categoriesList.add(category);
				amountsList.add(amountInt);
			}
		} catch (FileNotFoundException e) { // If there is no file
			System.err.println("Error: File not found.");
		} catch (IOException e) { // If something went wrong
			System.err.println("Error reading the file: " + e.getMessage());
		}

	}

	/**
	 * Loads categories and amounts from a Budget object into the internal lists.
	 * Existing data is cleared before loading. Income amounts are stored as
	 * positive values and expense amounts are stored as negative values.
	 * @bug 68362783: The "other" category is not correctly classified as income or expense. 
	 * Fix by classifying "other" as income or expense based on the amount sign.
	 * 
	 * @param budget the budget object containing transactions to read
	 * @author Jessica Barrera Saguay
	 */
	public void readFromBudget(Budget budget) {
		// Clear lists
		categoriesList.clear();
		amountsList.clear();

		// Check if budget is null or empty
		if (budget == null) {
			System.err.println("Error: Budget is null.");
			return;
		}
		// Check if budget has no transactions
		if (budget.getAllTransactions() == null || budget.getAllTransactions().isEmpty()) {
			System.err.println("Error: Budget contains no transactions.");
			return;
		}

    	// Loop through Transaction objects
		for (Transaction t : budget.getAllTransactions()) {
			// Extract category and amount
			String category = t.getCategory();
			if (category == null) {
				System.err.println("Error: Transaction has null category.");
				continue;
			}
			category = category.trim().toLowerCase(); //ensures that category matches the valid categories from lists by turning it to lower case
			int amount = (int) t.getAmount();

			// NEW CODE Dec 9 classify "other" as income or expense
			// classify "other"
			if (category.equals("other")) { // if the category is "other"
				if (amount > 0) { // if amount is positive
					otherIncome += amount; // makes it positive
					categoriesList.add(category); // add category to list
					amountsList.add(amount); // positive
				} else {
					otherExpense += amount; // negative
					categoriesList.add(category); // add category to list
					amountsList.add(amount); // already negative
				}
			}
			// for all other categories
			else if (incomeCategories.contains(category)) { // if the category is in incomeCategories list
				categoriesList.add(category); // add category to list
				amountsList.add(Math.abs(amount)); // positive
			} 
			else if (expenseCategories.contains(category)) { // if the category is in expenseCategories list
				categoriesList.add(category); // add category to list
				amountsList.add(-Math.abs(amount)); // negative
			}
			else {
				System.err.println("Error: Unrecognized category " + category);
			}
		}
	}

	/**
	 * Validates a single row of data by checking the date, category, and amount
	 * fields for correctness and consistency.
	 *
	 * This method performs the following validations:
	 * <ul>
	 * <li>Ensures the date is in MM/DD/YYYY format and represents an actual
	 * calendar date.</li>
	 * <li>Ensures all dates belong to the same year on the first read.</li>
	 * <li>Ensures the category is not empty, contains no digits, "Other" is income-only and cannot be used for expenses, and matches one of
	 * the predefined income or expense categories.</li>
	 * <li>Ensures the amount is an integer and has the correct sign(positive for
	 * income, negative for expenses).</li>
	 * </ul>
	 * 
	 * @param date     the date string to validate
	 * @param category the category name to validate
	 * @param amount   the string representing the amount
	 * @return {@code true} if all the data is valid, {@code false} otherwise
	 * @author Jessica Barrera Saguay
	 */
	public boolean isDataValid(String date, String category, String amount) {
    boolean isValid = true;
    // check if date is not missing
    if (date == null || date.trim().isEmpty()) { 
        System.err.println("Error: Date is missing." + date); // debug line
        isValid = false; // date is missing
    } else {
        // check to see if date is in valid date format and must be from the same
        // year(example: 2024)
        if (!date.matches("^(0[1-9]|1[0-2])\\/(0[1-9]|[12]\\d|3[01])\\/(\\d{4})$")) {
            isValid = false;
            System.err.println("Error: Date must be in MM/DD/YYYY format. Invalid date: " + date);
        } else {
            try { // Check if year is a valid year
                LocalDate.parse(date.trim(),
                        DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT)); // checks if date is valid
                int year = Integer.parseInt(date.substring(date.length() - 4));
                if (firstYear == null) {
                    firstYear = year; // set the first year encountered
                } else if (year != firstYear) {
                    isValid = false; // all years must be the same
                    System.err.println("Error: All the years in the date column should be the same year " + date);
                }
            } catch (DateTimeParseException e) {
                isValid = false; // date is invalid
                System.err.println("Error: Invalid date: " + date + " (" + e.getMessage() + ")"); 
            }
        }
    }

    if (category == null || category.trim().isEmpty()) { // check if category is not missing
		System.err.println("Error: Category is missing." + category); // debug line
        isValid = false;
    } else {
        category = category.trim().toLowerCase(); // make sure category has no whitespace and is lower case

        // check if category is a valid category from lists
        if (!incomeCategories.contains(category) && !expenseCategories.contains(category)) { // category is not recognized
            System.err.println("Error: Category " + category + " is not recognized.");
            isValid = false;
        }

        // checks if category is an integer
        if (category.matches(".*\\d.*")) { // category contains numbers
            System.err.println("Error: Category contains numbers: " + category);
            isValid = false;
        }
    }

    if (amount == null || amount.trim().isEmpty()) { // check if amount is not missing
		System.err.println("Error: Amount is missing." + amount); // debug line
        isValid = false;
    } else {
        // check to see if amount is an integer
        if (!amount.matches("^-?\\d+$")) { // positive or negative integer
            isValid = false;
            System.err.println("Error: Amount is not an integer." + amount);
        } else {
            int amountInt = Integer.parseInt(amount); // convert amount to integer

            // income must be positive
            if (incomeCategories.contains(category) && amountInt < 0) { // income category with negative amount
                isValid = false; // income must be positive
                System.err.println(
                        "Error: Income category " + category + " should have a positive amount " + amountInt); // debug line
            }
            // expense must be negative
            else if (expenseCategories.contains(category) && amountInt > 0) { // expense category with positive amount
                isValid = false; // expense must be negative
                System.err.println(
                        "Error: Expense category " + category + " should have a negative amount " + amountInt); // debug line
            }
        }
    }
    return isValid; // if data is valid
}


	/**
	 * Calculates the total income by adding up all sources of income such as
	 * compensation, allowance, investments, and other money received (e.g.,
	 * alimony, child support, or government benefits like unemployment or Social
	 * Security). The result should be positive since income amounts are positive.
	 *
	 * @return the sum of all income sources
	 * @author Jessica Barrera Saguay
	 */
	public int calculateTotalIncome() {
	    int totalIncome = 0; 
	    for (int i = 0; i < categoriesList.size(); i++) {
	        String category = categoriesList.get(i);
	        int amount = amountsList.get(i);
	
	        if ("other".equals(category)) {
	            // "other" → only count positive amounts as income
	            if (amount > 0) {
	                totalIncome += amount;
	            }
	        } else if (incomeCategories.contains(category)) {
	            totalIncome += amount;
	        }
	    }
	    return totalIncome;

	}

	/**
	 * Returns the total income computed from all the income category entries. The
	 * value should be positive since income is stored as positive amounts.
	 *
	 * @return the sum of all income amounts
	 * @author Jessica Barrera Saguay
	 */
	public int getTotalIncome() {
		return calculateTotalIncome();
	}

	/**
	 * Calculates the total expenses by adding up all expenses such as home,
	 * utilities, food, appearance, work, education, transportation, entertainment,
	 * professional services, and other expenses. The result should be negative
	 * since expense amounts are negative.
	 *
	 * @return the total amount of all expenses combined
	 * @author Jessica Barrera Saguay
	 */
	public int calculateTotalExpenses() {
	    int totalExpenses = 0;
	    for (int i = 0; i < categoriesList.size(); i++) {
	        String category = categoriesList.get(i);
	        int amount = amountsList.get(i);
	
	        if ("other".equals(category)) {
	            // "other" → only count negative amounts as expenses
	            if (amount < 0) {
	                totalExpenses += amount; // stays negative
	            }
	        } else if (expenseCategories.contains(category)) {
	            totalExpenses += amount;
	        }
	    }
	    return totalExpenses;
	}


	/**
	 * Returns the total expenses computed from all the expense category entries.
	 * The value should be negative since expenses are stored as negative amounts.
	 * 
	 * @return the sum of all expense amounts
	 * @author Jessica Barrera Saguay
	 */
	public int getTotalExpenses() {
		return calculateTotalExpenses();
	}

	/**
	 * Compares the total income and total expenses to determine if there is a
	 * surplus.
	 *
	 * @param totalIncome   the total income calculated from all sources
	 * @param totalExpenses the total amount of all expenses
	 * @return {@code true} if total income is greater than total expenses;
	 *         {@code false} otherwise
	 * @author Jessica Barrera Saguay
	 */
	public boolean compareIncomeVsExpenses(int totalIncome, int totalExpenses) {
		return totalIncome > totalExpenses;
	}

	/**
	 * Creates a summary report using the calculated income and expense data. The
	 * report may include totals and indicate whether there is a surplus or deficit.
	 *
	 * @return the summary report
	 * @author Jessica Barrera Saguay
	 */
	public String createSummaryReport() {
		StringBuilder report = new StringBuilder(); // used to return the summary report

		int totalIncome = calculateTotalIncome();
		int totalExpenses = calculateTotalExpenses();
		int finalTotal = totalIncome + (-totalExpenses); // calculates the final total

//        report.append("===== Summary Report =====\n\n");
//        report.append("Total Income: $").append(String.format("%d", totalIncome)).append("\n");
//        report.append("Total Expenses: $").append(String.format("%d", totalExpenses)).append("\n");
//        report.append("Final Total: $").append(String.format("%d", (finalTotal))).append("\n");

		return report.toString(); // used to return the summary report
	}

	/**
	 * Returns the internal list of categories exactly as stored.
	 *
	 * @return the internal categories list
	 * @author Jessica Barrera Saguay
	 */
	public List<String> getCategoriesList() {
		return categoriesList;
	}

	/**
	 * Builds and returns a list containing all expense categories that actually
	 * appear in the recorded expenses, with no duplicates. A category is included
	 * only if it exists in expenseCategories and appears at least once in
	 * categoriesList.
	 *
	 * @return a list of distinct expense categories
	 * @author Jessica Barrera Saguay
	 */
	public List<String> getUniqueExpenseCategories() {
		List<String> unique = new ArrayList<>();
		for (String category : categoriesList) {
			if (expenseCategories.contains(category) && !unique.contains(category)) {
				unique.add(category);
			}
		}
		return unique;
	}

	/**
	 * Computes the total amount spent for a specific expense category.
	 * 
	 * @param category the category whose total amount should be calculated
	 * @return the total amount spent in the given category
	 * @author Jessica Barrera Saguay
	 */
	public int getTotalForCategory(String category) {
		int total = 0;
		for (int i = 0; i < categoriesList.size(); i++) {
			if (categoriesList.get(i).equals(category)) {
				total += amountsList.get(i);
			}
		}
		return total;
	}

	/**
	 * Calculates the total expense amount for each valid expense category. Only
	 * categories that are in expenseCategories are included.
	 * 
	 * @return a map where each key is a valid expense category and each value is
	 *         the total dollar amount recorded for that category
	 * @author Jessica Barrera Saguay
	 */
	public Map<String, Integer> getExpenseTotalsByCategory() {
		Map<String, Integer> totals = new HashMap<>();
		for (int i = 0; i < categoriesList.size(); i++) {
			if (expenseCategories.contains(categoriesList.get(i))) {
				totals.put(categoriesList.get(i), totals.getOrDefault(categoriesList.get(i), 0) + amountsList.get(i));
			}
		}
		return totals;
	}

	/**
	 * Computes the percentage share of total expenses represented by each category.
	 * Only categories that are in categoriesList are included. If
	 * calculateTotalExpenses is positive then the method will print an error and
	 * return an empty map since totalExpenses is meant to return a negative total.
	 * If calculateTotalExpenses is negative then it will calculate the percentage
	 * as normal by dividing categoryTotal/totalExpenses. If calculateTotalExpenses
	 * is zero then the method will print an error and return an empty map.
	 *
	 * @return a map where each key is an expense category and each value is its
	 *         percentage of the total expenses percentages
	 * @author Jessica Barrera Saguay
	 */
	public Map<String, Double> getExpensePercentages() {
		Map<String, Integer> totals = getExpenseTotalsByCategory(); // stores the totals calculated by the
																	// getExpenseTotalsByCategory method earlier
		Map<String, Double> percentages = new HashMap<>(); // stores the percentages
		if (calculateTotalExpenses() == 0) { // can't divide by zero
			System.err.println("Error: Total expenses is 0 and can't be evaluated as percentages.");
			return Collections.emptyMap(); // return an empty map if total expenses is 0
		}
		if (calculateTotalExpenses() > 0) { // total expenses should be negative
			System.err.println("Error: Total expenses should be negative not positive.");
			return Collections.emptyMap(); // return an empty map if total expenses is positive.
		}
		for (Map.Entry<String, Integer> entry : totals.entrySet()) {
			double percent = (double) entry.getValue() / calculateTotalExpenses(); // calculate the percentage
																					// categoryTotal/totalExpenses
			percentages.put(entry.getKey(), percent); // add the percentage to the map
		}
		return percentages;
	}

}
