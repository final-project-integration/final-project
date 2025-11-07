package storage;

import java.util.ArrayList;

/**
 * The Budget class represents a user's financial data for a single calendar year.
 * It stores income and expense transactions, allows modification of these records,
 * and provides analytical summaries such as monthly totals and annual performance.
 *
 * This class is part of the Storage module responsible for maintaining budget objects
 * between program sessions.
 *
 * @author Emmanuel Cano
 * @version 11/4/2025
 */

public class Budget {
	
    /**
     * Constructs a new Budget object for the current year.
     * Initializes internal data structures for tracking income and expenses.
     */
    public Budget() {
        // No initialization yet
    }
    
    /**
     * Adds a new transaction to the user's budget for the year.
     *
     * @param date the date of the transaction in MM/DD/YYYY format
     * @param category the category of the transaction (e.g., Food, Utilities, Compensation)
     * @param amount the dollar amount of the transaction (positive for income, negative for expense)
     */
    public void addTransaction(String date, String category, double amount) {
        // Implementation pending
    }

    /**
     * Removes a transaction from the user's budget.
     *
     * @param transactionIndex the index number of the transaction to remove
     */
    public void removeTransaction(int transactionIndex) {
        // Implementation pending
    }

    /**
     * Updates an existing transaction with new information.
     *
     * @param transactionIndex the index number of the transaction to update
     * @param newDate the updated date of the transaction in MM/DD/YYYY format
     * @param newCategory the updated category of the transaction
     * @param newAmount the updated amount of the transaction
     */
    public void updateTransaction(int transactionIndex, String newDate, String newCategory, double newAmount) {
        // Implementation pending
    }

    /**
     * Retrieves all transactions grouped by month.
     *
     * @return an ArrayList of transactions sorted or grouped by month
     */
    public ArrayList<Object> getTransactionsByMonth() {
        // Implementation pending
        return null;
    }

    /**
     * Retrieves all transactions grouped by category.
     *
     * @return an ArrayList of transactions sorted or grouped by category
     */
    public ArrayList<Object> getTransactionsByCategory() {
        // Implementation pending
        return null;
    }

    /**
     * Calculates the total income and expenses for each month in the year.
     *
     * @return an ArrayList of monthly totals (income minus expenses)
     */
    public ArrayList<Double> calculateMonthlyTotals() {
        // Implementation pending
        return null;
    }

    /**
     * Generates a summary of the user's annual financial performance.
     * This includes total income, total expenses, and overall net balance.
     *
     * @return an ArrayList containing summary values for income, expenses, and net balance
     */
    public ArrayList<Double> calculateAnnualSummary() {
        // Implementation pending
        return null;
    }

}
