import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.Collections;

/**
 * Manages loading data and orchestrating the generation and presentation of
 * report artifacts Responsibilities include: Loading income/expense data for a
 * given year Producing monthly, category, and yearly summaries Computing
 * overall balance figures Exporting summaries to CSV and displaying reports on
 * screen
 * 
 * @author Chukwuemeka Okwuka
 * @author Omar Piron
 */
public class ReportManager {

	private ArrayList<FinancialRecord> loadedRecords;
	private ArrayList<Integer> availableYears;
	private ArrayList<String> loadedFilePaths;

	/**
	 * Creates a new ReportManager with empty records, years, and file path lists.
	 */
	public ReportManager() {
		this.loadedRecords = new ArrayList<>();
		this.availableYears = new ArrayList<Integer>();
		this.loadedFilePaths = new ArrayList<>();
	}

	/**
	 * Loads financial records from a CSV file into memory. CSV format expected:
	 * amount,category,month,year,isIncome
	 * 
	 * @param csvFile the CSV file to load
	 */
	public void loadCSV(File csvFile) {
		try {
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(csvFile));
			String line;

			// Skip header row
			reader.readLine();

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");

				String amount = parts[0].trim();
				String category = parts[1].trim();
				int month = Integer.parseInt(parts[2].trim());
				int year = Integer.parseInt(parts[3].trim());
				boolean isIncome = Boolean.parseBoolean(parts[4].trim());

				loadedRecords.add(new FinancialRecord(amount, category, month, year, isIncome));

				if (!availableYears.contains(year)) {
					availableYears.add(year);
				}
			}

			reader.close();
			loadedFilePaths.add(csvFile.getAbsolutePath());

		} catch (IOException e) {
			System.err.println("Error loading CSV: " + e.getMessage());
		}
	}

	/**
	 * Saves the list of loaded CSV file paths to a text file. This allows the app
	 * to remember which files were loaded between sessions.
	 */
	public void saveSession() {
		try {
			FileWriter writer = new FileWriter("session_data.txt");

			for (int i = 0; i < loadedFilePaths.size(); i++) {
				writer.write(loadedFilePaths.get(i) + "\n");
			}

			writer.close();
			System.out.println("Session saved successfully.");

		} catch (IOException e) {
			System.err.println("Error saving session: " + e.getMessage());
		}
	}

	/**
	 * Loads the previous session by reading saved file paths and reloading CSVs.
	 * Call this when the app starts.
	 */
	public void loadPreviousSession() {
		File sessionFile = new File("session_data.txt");

		if (!sessionFile.exists()) {
			System.out.println("No previous session found.");
			return;
		}

		try {
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(sessionFile));
			String filePath;

			while ((filePath = reader.readLine()) != null) {
				File csvFile = new File(filePath.trim());
				if (csvFile.exists()) {
					loadCSV(csvFile);
				} else {
					System.out.println("File not found: " + filePath);
				}
			}

			reader.close();
			System.out.println("Previous session loaded. Available years: " + availableYears);

		} catch (IOException e) {
			System.err.println("Error loading session: " + e.getMessage());
		}
	}

	/**
	 * Returns the list of distinct years for which records have been loaded
	 * 
	 * @return availableYears - an ArrayList of Integer values representing the
	 *         years that have data
	 */
	public ArrayList<Integer> getAvailableYears() {
		return availableYears;
	}

	/**
	 * Clears all loaded records, years, and file paths from this manager.
	 */
	public void clearAllData() {
		loadedRecords.clear();
		availableYears.clear();
		loadedFilePaths.clear();
	}

	/**
	 * Setter method to inject financial records (for integration with other
	 * modules) This method replaces all currently loaded records and rebuilds the
	 * list of available years based on the provided records.
	 * 
	 * @param records an ArrayList of FinancialRecord objects to load into the
	 *                manager
	 */
	public void setFinancialRecords(ArrayList<FinancialRecord> records) {
		this.loadedRecords = records;

		availableYears.clear();
		for (int i = 0; i < records.size(); i++) {
			int year = records.get(i).getYear();
			if (!availableYears.contains(year)) {
				availableYears.add(year);
			}
		}
	}

	/**
	 * Aggregates totals by month for the previously loaded year. Creates a list of
	 * formatted summary strings showing income, expenses, and balance for each of
	 * the 12 months.
	 *
	 * @param year the four-digit year corresponding to the loaded dataset
	 * @return an ArrayList of 12 formatted strings, one for each month
	 */
	public ArrayList<String> generateMonthlySummary(int year) {
		ArrayList<String> summary = new ArrayList<>();

		if (loadedRecords.size() == 0) {
			return summary;
		}

		// Arrays to store for each months Income and Expenses (index 0 is January,
		// index 11 is December)
		double[] monthlyIncome = new double[12];
		double[] monthlyExpenses = new double[12];

		// Aggregate data by month
		for (int i = 0; i < loadedRecords.size(); i++) {
			FinancialRecord record = loadedRecords.get(i);

			if (record.getYear() != year) {
				continue;
			}

			int month = record.getMonth();
			double amount = Double.parseDouble(record.getAmount());

			if (record.isIncome()) {
				monthlyIncome[month] += amount;
			} else {
				monthlyExpenses[month] += amount;
			}
		}

		// Format results
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };

		for (int month = 0; month <= 11; month++) {
			double income = monthlyIncome[month];
			double expenses = monthlyExpenses[month];
			double balance = income + expenses;

			String line = String.format("%s: Income=$%.2f, Expenses=$%.2f, Balance=$%.2f", monthNames[month], income,
					expenses, balance);
			summary.add(line);
		}

		return summary;
	}

	/**
	 * Aggregates totals by category label, (e.g. "Rent", "Groceries"). Creates a
	 * list of formatted summary strings showing the total amount for each category
	 *
	 * @param year the four-digit year corresponding to the loaded dataset
	 * @return an ArrayList of formatted strings in "Category: $Amount" format
	 */
	public ArrayList<String> generateCategorySummary(int year) {
		ArrayList<String> summary = new ArrayList<>();

		if (loadedRecords.size() == 0) {
			return summary;
		}

		// Lists to track categories and their totals
		ArrayList<String> categories = new ArrayList<>();
		ArrayList<Double> totals = new ArrayList<>();

		// Aggregate by category
		for (int i = 0; i < loadedRecords.size(); i++) {
			FinancialRecord record = loadedRecords.get(i);

			// Only process record from specified year
			if (record.getYear() != year) {
				continue;
			}

			String category = record.getCategory();
			double amount = Double.parseDouble(record.getAmount());

			// Find if category already exists
			int index = -1;
			for (int j = 0; j < categories.size(); j++) {
				if (categories.get(j).equals(category)) {
					index = j;
					break;
				}
			}

			if (index == -1) {
				categories.add(category);
				totals.add(amount);
			} else {
				totals.set(index, totals.get(index) + amount);
			}
		}

		// Format results (no sorting for simplicity)
		for (int i = 0; i < categories.size(); i++) {
			String line = String.format("%s: $%.2f", categories.get(i), totals.get(i));
			summary.add(line);
		}

		return summary;
	}

	/**
	 * Produces year-wide totals for income, expenses, and net balance.
	 *
	 * @param year the four-digit year corresponding to the loaded dataset
	 * @return a YearlySummary struct containing key totals as strings
	 */
	public YearlySummary generateYearlySummary(int year) {
		if (loadedRecords.size() == 0) {
			return new YearlySummary("0.00", "0.00", "0.00");
		}

		double totalIncome = 0.0;
		double totalExpenses = 0.0;

		for (int i = 0; i < loadedRecords.size(); i++) {
			FinancialRecord record = loadedRecords.get(i);

			// Only process record from specified year
			if (record.getYear() != year) {
				continue;
			}

			double amount = Double.parseDouble(record.getAmount());

			if (record.isIncome()) {
				totalIncome += amount;
			} else {
				totalExpenses += amount;
			}
		}

		double netBalance = totalIncome + totalExpenses;

		return new YearlySummary(String.format("%.2f", totalIncome), String.format("%.2f", totalExpenses),
				String.format("%.2f", netBalance));
	}

	/**
	 * Calculates the overall balance for the specified year (totalIncome -
	 * totalExpenses).
	 * 
	 * @param year the four-digit year corresponding to the loaded dataset
	 * @return net balance for the year
	 */
	public String calculateBalance(int year) {
		YearlySummary summary = generateYearlySummary(year);
		return summary.getNetBalance();
	}

	/**
	 * Exports the active summaries to a CSV file at the provided path.
	 * 
	 * @param outputFile target path for the CSV file, parent directories should
	 *                   exist
	 * @param year       the four-digit year to generate and export the report
	 */
	public void exportReportToCSV(File outputFile, int year) {
		try {
			FileWriter writer = new FileWriter(outputFile);

			// Get all summaries
			YearlySummary yearly = generateYearlySummary(year);
			ArrayList<String> monthly = generateMonthlySummary(year);
			ArrayList<String> category = generateCategorySummary(year);

			// Write header
			writer.write("Financial Report for " + year + "\n\n");

			// Write Yearly Summary
			writer.write("YEARLY SUMMARY\n");
			writer.write("Total Income," + yearly.getTotalIncome() + "\n");
			writer.write("Total Expenses," + yearly.getTotalExpenses() + "\n");
			writer.write("Net Balance," + yearly.getNetBalance() + "\n\n");

			// Write Monthly Summary
			writer.write("MONTHLY SUMMARY\n");
			writer.write("Month,Income,Expenses,Balance\n");
			for (int i = 0; i < monthly.size(); i++) {
				String line = monthly.get(i);
				String csvLine = line.replace(": Income=$", ",").replace(", Expenses=$", ",").replace(", Balance=$",
						",");
				writer.write(csvLine + "\n");
			}
			writer.write("\n");

			// Write Category Summary
			writer.write("CATEGORY SUMMARY\n");
			writer.write("Category,Total\n");
			for (int i = 0; i < category.size(); i++) {
				String line = category.get(i);
				String csvLine = line.replace(": $", ",");
				writer.write(csvLine + "\n");
			}

			writer.close();
			System.out.println("Report exported to: " + outputFile.getAbsolutePath());

		} catch (IOException e) {
			System.err.println("Error exporting report: " + e.getMessage());
		}
	}

	/**
	 * Prints a formatted financial report to the console for the given year.
	 * Includes yearly summary, monthly breakdown, and category totals.
	 * 
	 * @param year the four-digit year to generate and display the report
	 */
	public void displayReportOnScreen(int year) {
		// Get all summaries
		YearlySummary yearly = generateYearlySummary(year);
		ArrayList<String> monthly = generateMonthlySummary(year);
		ArrayList<String> category = generateCategorySummary(year);

		System.out.println("\n============================================================");
		System.out.println("                    FINANCIAL REPORT");
		System.out.println("============================================================");

		// Display Yearly Summary
		System.out.println("\nYEARLY SUMMARY");
		System.out.println("Total Income:    $" + yearly.getTotalIncome());
		System.out.println("Total Expenses:  $" + yearly.getTotalExpenses());
		System.out.println("Net Balance:     $" + yearly.getNetBalance());

		// Display Monthly Summary
		System.out.println("\nMONTHLY SUMMARY");
		for (int i = 0; i < monthly.size(); i++) {
			System.out.println(monthly.get(i));
		}

		// Display Category Summary
		System.out.println("\nCATEGORY SUMMARY");
		for (int i = 0; i < category.size(); i++) {
			System.out.println(category.get(i));
		}

		System.out.println("\n============================================================\n");
	}

	/**
	 * Immutable value object representing yearly financial totals. Once created,
	 * the values in this object cannot be modified.
	 *
	 */
	public static final class YearlySummary {
		private final String totalIncome;
		private final String totalExpenses;
		private final String netBalance;

		/**
		 * Constructs a new YearlySummary with the specified totals.
		 *
		 * @param totalIncome   sum of all income entries for the year as a formatted
		 *                      string
		 * @param totalExpenses sum of all expense entries for the year as a formatted
		 *                      string
		 * @param netBalance    the net balance (totalIncome - totalExpenses) as a
		 *                      formatted string
		 */
		public YearlySummary(String totalIncome, String totalExpenses, String netBalance) {
			this.totalIncome = totalIncome;
			this.totalExpenses = totalExpenses;
			this.netBalance = netBalance;
		}

		/**
		 * Returns the total income for the year.
		 * 
		 * @return the sum of all income entries for the year as a formatted string
		 */
		public String getTotalIncome() {
			return totalIncome;
		}

		/**
		 * Returns the total expenses for the year.
		 * 
		 * @return the sum of all expense entries for the year as a formatted string
		 */
		public String getTotalExpenses() {
			return totalExpenses;
		}

		/**
		 * Returns the net balance for the year.
		 * 
		 * @return the net balance calculated as (total income - total expenses) as a
		 *         formatted string
		 */
		public String getNetBalance() {
			return netBalance;
		}
	}

	/**
	 * Inner class representing a financial record. This should match the structure
	 * from Income/Expense modules.
	 */
	public static class FinancialRecord {
		private final String amount;
		private final String category;
		private final int month;
		private final int year;
		private final boolean isIncome;

		/**
		 * Constructs a new FinancialRecord with the specified details.
		 * 
		 * @param amount   - the transaction amount as a string
		 * @param category - the category label for this transaction (e.g. "Groceries",
		 *                 "Salary")
		 * @param month    - the month of the transaction
		 * @param year     - the the four-digit year of the transaction
		 * @param isIncome - true if this is an income transaction, false if it is an
		 *                 expense
		 */
		public FinancialRecord(String amount, String category, int month, int year, boolean isIncome) {
			this.amount = amount;
			this.category = category;
			this.month = month;
			this.year = year;
			this.isIncome = isIncome;
		}

		/**
		 * Returns the transaction amount as a string
		 * 
		 * @return the amount in string format
		 */
		public String getAmount() {
			return amount;
		}

		/**
		 * Returns the category of this transaction
		 * 
		 * @return the category label
		 */
		public String getCategory() {
			return category;
		}

		/**
		 * Returns the month of this transaction
		 * 
		 * @return the month as an integer
		 */
		public int getMonth() {
			return month;
		}

		/**
		 * Returns the year of this transaction.
		 * 
		 * @return the four-digit year
		 */
		public int getYear() {
			return year;
		}

		/**
		 * Indicates whether this transaction is income or expense.
		 * 
		 * @return true if this is an income transaction, false if it is an expense
		 */
		public boolean isIncome() {
			return isIncome;
		}
	}
}
