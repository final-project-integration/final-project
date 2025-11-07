/**
 * DataReader class for the Prediction Team Module Reads, validates, calculates
 * income and expenses, compares calculations, and summarizes report.
 * 
 * @author Jessica Barrera Saguay
 */

public class DataReader {

	/**
	 * Reads stored budget data from the file provided. This method does not return
	 * a value but prepares the data for further processing.
	 * 
	 * @author Jessica Barrera Saguay
	 */
	public void readData() {
	}

	/**
	 * Validates that the data in the file is correct and has no missing
	 * information.
	 * 
	 * @return {@code true} if the data is valid, {@code false} otherwise
	 * @author Jessica Barrera Saguay
	 */
	public boolean isDataValid() {
		return true;
	}

	/**
	 * Calculates the total income by adding up all sources of income such as
	 * salary, wages, bonuses, tips, and other money received (e.g., alimony, child
	 * support, or government benefits like unemployment or Social Security).
	 * 
	 * @return the sum of all income sources
	 * @author Jessica Barrera Saguay
	 */
	public double calculateTotalIncome() {

		double salary = 0, wages = 0, bonuses = 0, tips = 0, other = 0;
		double totalIncome = salary + wages + bonuses + tips + other;
		return totalIncome;
	}

	/**
	 * Calculates the total expenses by summing fixed, variable, and other expenses.
	 * 
	 * @return the total amount of all expenses combined
	 * @author Jessica Barrera Saguay
	 */
	public double calculateTotalExpenses() {

		double fixedExpenses = 0;
		double variableExpenses = 0;
		double otherExpenses = 0;
		double totalExpenses = fixedExpenses + variableExpenses + otherExpenses;
		return totalExpenses;
	}

	/**
	 * Compares the total income and total expenses to determine if there is a
	 * surplus.
	 * 
	 * @param totalIncome   the total income calculated from all sources
	 * @param totalExpenses the total amount of all expenses
	 * @return {@code true} if total income is greater than total expenses; {code@
	 *         false} otherwise
	 * @author Jessica Barrera Saguay
	 */
	public boolean compareIncomeVsExpenses(double totalIncome, double totalExpenses) {
		return totalIncome > totalExpenses;
	}

	/**
	 * Creates and displays a summary report using the calculated income and expense
	 * data. The report may include totals and indicate whether there is a surplus
	 * or deficit.
	 * 
	 * @author Jessica Barrera Saguay
	 */
	public void createSummaryReport() {
	}
}
