import java.util.ArrayList;

/**
 * The Budget class represents all financial activity for a single calendar year.
 * It stores a list of income and expense transactions, allows modifying these records,
 * and provides summary calculations such as totals by month, totals by category,
 * and an overall annual summary.
 * 
 * This class is part of the Storage module and is responsible for maintaining
 * the user's financial data.
 *
 * @author Emmanuel
 * @version 12/4/2025
 */
public class Budget {

    /** A list of all income and expense transactions for this budget year. */
    private ArrayList<Transaction> transactions;

    /**
     * Constructs a new empty Budget object.
     * Initializes the internal list that stores all transactions.
     */
    public Budget() {
        transactions = new ArrayList<>();
    }

    /**
     * Adds a new transaction to the user's budget for the year.
     *
     * @param date      the date of the transaction in MM/DD/YYYY format
     * @param category  the transaction category (e.g., Food, Utilities, Compensation)
     * @param amount    the dollar amount of the transaction
     *                  (positive for income, negative for expenses)
     */
    public void addTransaction(String date, String category, double amount) {
        transactions.add(new Transaction(date, category, amount));
    }

    /**
     * Removes an existing transaction at the specified index. If the index is invalid, no transaction is removed and the budget
     * remains unchanged.
     * 
     *
     * @param transactionIndex  the index of the transaction to remove
     *                          (must be between 0 and size−1)
     */
    public void removeTransaction(int transactionIndex) {
        if (transactionIndex >= 0 && transactionIndex < transactions.size()) {
            transactions.remove(transactionIndex);
        } else {
            System.out.println("Invalid transaction index.");
        }
    }

    /**
     * Updates an existing transaction with new values. If the index is invalid, no 
     * transaction is updated and the budget remains unchanged.
     *
     * @param transactionIndex  the index of the transaction to update
     * @param newDate           the updated date in MM/DD/YYYY format
     * @param newCategory       the updated category name
     * @param newAmount         the updated amount (positive for income,
     *                          negative for expense)
     */
    public void updateTransaction(int transactionIndex,
                                  String newDate,
                                  String newCategory,
                                  double newAmount) {
        if (transactionIndex >= 0 && transactionIndex < transactions.size()) {
            transactions.get(transactionIndex).update(newDate, newCategory, newAmount);
        } else {
            System.out.println("Invalid transaction index.");
        }
    }

    /**
     * Groups all transactions by their calendar month based on the date string.
     * Index 0 corresponds to January, index 1 to February, ..., index 11 to December.
     *
     * @return an ArrayList<ArrayList<Transaction>> of 12 lists, where each
     *         inner list contains all transactions that occurred in that month
     */
    public ArrayList<ArrayList<Transaction>> getTransactionsByMonth() {

        ArrayList<ArrayList<Transaction>> monthly = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            monthly.add(new ArrayList<>());
        }

        for (Transaction t : transactions) {
            int month = extractMonth(t.getDate());
            if (month >= 1 && month <= 12) {
                monthly.get(month - 1).add(t);
            }
        }

        return monthly;
    }

    /**
     * Extracts the month number from a date in the format MM/DD/YYYY.
     *
     * @param date  the date string to parse
     * @return an integer month value from 1 (January) to 12 (December)
     */
    private int extractMonth(String date) {
        return Integer.parseInt(date.substring(0, 2));
    }

    /**
     * Groups all transactions by their category. Categories are ordered by their first 
     * appearance in the transaction list.
     *
     * @return an ArrayList<Object> containing:
     *         <ul>
     *             <li>Index 0: ArrayList<String> of unique category names</li>
     *             <li>Index 1: ArrayList<ArrayList<Transaction>> where each inner list
     *                 contains transactions for the corresponding category</li>
     *         </ul>
     */
    public ArrayList<Object> getTransactionsByCategory() {

        ArrayList<String> categories = new ArrayList<>();
        ArrayList<ArrayList<Transaction>> grouped = new ArrayList<>();

        for (Transaction t : transactions) {
            String cat = t.getCategory();
            int index = categories.indexOf(cat);

            if (index == -1) {
                categories.add(cat);
                grouped.add(new ArrayList<>());
                index = categories.size() - 1;
            }

            grouped.get(index).add(t);
        }

        ArrayList<Object> result = new ArrayList<>();
        result.add(categories);
        result.add(grouped);

        return result;
    }

    /**
     * Calculates the total income and expenses for each month of the year.
     *
     * @return an ArrayList<Double> of 12 values, where each element represents
     *         the total amount for that month (income minus expenses)
     */
    public ArrayList<Double> calculateMonthlyTotals() {

        ArrayList<Double> totals = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            totals.add(0.0);
        }

        for (Transaction t : transactions) {
            int month = extractMonth(t.getDate());
            if (month >= 1 && month <= 12) {
                double newTotal = totals.get(month - 1) + t.getAmount();
                totals.set(month - 1, newTotal);
            }
        }

        return totals;
    }

    /**
     * Computes an annual summary of all financial activity.
     *
     * @return an ArrayList<Double> containing:
     *         <ul>
     *             <li>Total income</li>
     *             <li>Total expenses</li>
     *             <li>Net balance</li>
     *         </ul>
     */
    public ArrayList<Double> calculateAnnualSummary() {
        double income = 0;
        double expenses = 0;

        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                income += t.getAmount();
            } else {
                expenses += t.getAmount();
            }
        }

        double net = income + expenses;

        ArrayList<Double> summary = new ArrayList<>();
        summary.add(income);
        summary.add(expenses);
        summary.add(net);

        return summary;
    }

    /**
     * Prints all stored transactions to the console.
     * This method is primarily used for debugging.
     */
    public void printAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
        } else {
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println(i + ": " + transactions.get(i));
            }
        }
    }

    /**
     * Returns a copy of all transactions stored in this budget.
     * Modifying the returned list will not affect the internal
     * state of the Budget object.
     *
     * @return an ArrayList<Transaction> containing copies of references
     *         to all transaction objects
     */
    public ArrayList<Transaction> getAllTransactions() {
        return new ArrayList<>(this.transactions);
    }
}
        transactions.add(new Transaction(date, category, amount));
    }

    /**
     * Removes an existing transaction at the specified index.
     * 
     * If the index is invalid, no transaction is removed and the budget
     * remains unchanged.
     *
     * @param transactionIndex  the index of the transaction to remove
     *                          (must be between 0 and size−1)
     */
    public void removeTransaction(int transactionIndex) {
        if (transactionIndex >= 0 && transactionIndex < transactions.size()) {
            transactions.remove(transactionIndex);
        } else {
            System.out.println("Invalid transaction index.");
        }
    }

    /**
     * Updates an existing transaction with new values.
     *
     * If the index is invalid, no transaction is updated and the budget
     * remains unchanged.
     *
     * @param transactionIndex  the index of the transaction to update
     * @param newDate           the updated date in MM/DD/YYYY format
     * @param newCategory       the updated category name
     * @param newAmount         the updated amount (positive for income,
     *                          negative for expense)
     */
    public void updateTransaction(int transactionIndex,
                                  String newDate,
                                  String newCategory,
                                  double newAmount) {
        if (transactionIndex >= 0 && transactionIndex < transactions.size()) {
            transactions.get(transactionIndex).update(newDate, newCategory, newAmount);
        } else {
            System.out.println("Invalid transaction index.");
        }
    }

    /**
     * Groups all transactions by their calendar month based on the date string.
     * Index 0 corresponds to January, index 1 to February, ..., index 11 to December.
     *
     * @return an ArrayList<ArrayList<Transaction>> of 12 lists, where each
     *         inner list contains all transactions that occurred in that month
     */
    public ArrayList<ArrayList<Transaction>> getTransactionsByMonth() {

        ArrayList<ArrayList<Transaction>> monthly = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            monthly.add(new ArrayList<>());
        }

        for (Transaction t : transactions) {
            int month = extractMonth(t.getDate());
            if (month >= 1 && month <= 12) {
                monthly.get(month - 1).add(t);
            }
        }

        return monthly;
    }

    /**
     * Extracts the month number from a date in the format MM/DD/YYYY.
     *
     * @param date  the date string to parse
     * @return an integer month value from 1 (January) to 12 (December)
     */
    private int extractMonth(String date) {
        return Integer.parseInt(date.substring(0, 2));
    }

    /**
     * Groups all transactions by their category.
     * 
     * Categories are ordered by their first appearance in the transaction list.
     *
     * @return an ArrayList<Object> containing:
     *         <ul>
     *             <li>Index 0: ArrayList<String> of unique category names</li>
     *             <li>Index 1: ArrayList<ArrayList<Transaction>> where each inner list
     *                 contains transactions for the corresponding category</li>
     *         </ul>
     */
    public ArrayList<Object> getTransactionsByCategory() {

        ArrayList<String> categories = new ArrayList<>();
        ArrayList<ArrayList<Transaction>> grouped = new ArrayList<>();

        for (Transaction t : transactions) {
            String cat = t.getCategory();
            int index = categories.indexOf(cat);

            if (index == -1) {
                categories.add(cat);
                grouped.add(new ArrayList<>());
                index = categories.size() - 1;
            }

            grouped.get(index).add(t);
        }

        ArrayList<Object> result = new ArrayList<>();
        result.add(categories);
        result.add(grouped);

        return result;
    }

    /**
     * Calculates the total income and expenses for each month of the year.
     *
     * @return an ArrayList<Double> of 12 values, where each element represents
     *         the total amount for that month (income minus expenses)
     */
    public ArrayList<Double> calculateMonthlyTotals() {

        ArrayList<Double> totals = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            totals.add(0.0);
        }

        for (Transaction t : transactions) {
            int month = extractMonth(t.getDate());
            if (month >= 1 && month <= 12) {
                double newTotal = totals.get(month - 1) + t.getAmount();
                totals.set(month - 1, newTotal);
            }
        }

        return totals;
    }

    /**
     * Computes an annual summary of all financial activity.
     *
     * @return an ArrayList<Double> containing:
     *         <ul>
     *             <li>Total income</li>
     *             <li>Total expenses</li>
     *             <li>Net balance</li>
     *         </ul>
     */
    public ArrayList<Double> calculateAnnualSummary() {
        double income = 0;
        double expenses = 0;

        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                income += t.getAmount();
            } else {
                expenses += t.getAmount();
            }
        }

        double net = income + expenses;

        ArrayList<Double> summary = new ArrayList<>();
        summary.add(income);
        summary.add(expenses);
        summary.add(net);

        return summary;
    }

    /**
     * Prints all stored transactions to the console.
     * This method is primarily used for debugging.
     */
    public void printAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
        } else {
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println(i + ": " + transactions.get(i));
            }
        }
    }

    /**
     * Returns a copy of all transactions stored in this budget.
     * 
     * Modifying the returned list will not affect the internal
     * state of the Budget object.
     *
     * @return an ArrayList<Transaction> containing copies of references
     *         to all transaction objects
     */
    public ArrayList<Transaction> getAllTransactions() {
        return new ArrayList<>(this.transactions);
    }
}

