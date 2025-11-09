
/**
 * The Transaction class represents a single Transaction entry.
 * It stores the date, categorical sorting, and amount spent in each transaction,
 * and allows for obtaining the data through various getter fields.
 *
 * This class is part of the Storage module responsible for creating an object representation
 * for any and all transactions.
 *
 * @author Karol Kopciuch
 * @version 11/5/2025
 */
public class Transaction {

    // Private fields to enforce encapsulation
    private String date; 
    private String category;
    private double amount;

    /**
     * Constructs a new transaction object.
     * Initializes all three instance fields with default empty values (null for Strings, 0.0 for amount).
     */
    public Transaction() {
        this.date = null;
        this.category = null;
        this.amount = 0.0;
    }
    
    /**
     * Constructs a new transaction object by initializing all three instance fields.
     * @param date the date of the transaction in MM/DD/YYYY format
     * @param category the category of the transaction (e.g., Food, Utilities, Compensation)
     * @param amount the dollar amount of the transaction (positive for income, negative for expense)
     * @author Karol Kopciuch
     */
    public Transaction(String date, String category, double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    /**
     * Retrieves the date that was entered in a transaction.
     * @return a String representing the date in MM/DD/YYYY format
     * @author Karol Kopciuch
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Retrieves the category the transaction was filed under.
     * @return a String representing the category of transaction
     * @author Karol Kopciuch
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Retrieves the amount of money that changed in the transaction.
     * @return a double representing the dollar amount of the transaction
     * @author Karol Kopciuch
     */
    public double getAmount() {
        return this.amount;
    }

}

