import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The StorageManager Class handles reading and writing user budget information
 * to and from saved files.
 * 
 * @author Farhan Chowdhury
 * @version 11/4/2025
 */
public class StorageManager {

    private CSVHandler csvHandler;

    /**
     * Initializes the StorageManager and creates the internal CSVHandler
     * used for reading and writing CSV files.
     */
    public StorageManager() {
        this.csvHandler = new CSVHandler();
    }

    /**
     * Builds a file path in the format: data/{username}_{year}.csv
     */
    private String getFilePath(String username, int year) {
        return "data/" + username + "_" + year + ".csv";
    }

    /**
     * Loads the user data from saved file.
     * 
     * @param username the name of the user whose data is being loaded
     * @param year the year the data is being loaded for
     * @author Farhan Chowdhury
     */
    public void loadUserData(String username, int year) {
        String filePath = getFilePath(username, year);
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("No saved data found for this user/year.");
            return;
        }

        try {
            Budget budget = loadBudgetFromFile(filePath);
            System.out.println("Data successfully loaded for " + username + " (" + year + ").");
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    /**
     * Saves the current user data to a file.
     * 
     * @param username the name of the user whose data is being saved
     * @param year the year the data is being saved for
     * @param budget the Budget object containing all user transactions for the year
     * @author Farhan Chowdhury
     */
    public void saveUserData(String username, int year, Budget budget) {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdirs(); // create data folder if it doesn't exist
        }

        String filePath = getFilePath(username, year);
        try {
            csvHandler.writeCSV(filePath, budget.getAllTransactions());
            System.out.println("Data successfully saved for " + username + " (" + year + ").");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Deletes the user Data from file.
     * 
     * @param username the name of the user whose data is being deleted
     * @param year     the year the data is being deleted
     * @author Farhan Chowdhury
     */
    public void deleteUserData(String username, int year) {
        String filePath = getFilePath(username, year);
        File file = new File(filePath);

        if (file.exists() && file.delete()) {
            System.out.println("Data deleted for " + username + " (" + year + ").");
        } else {
            System.out.println("No file found to delete.");
        }
    }

    /**
     * Lists all the years for which the data is available for a user.
     * 
     * @param username the name of the user whose data is being accessed
     * @author Farhan Chowdhury
     */
    public void listAvailableYears(String username) {
        File folder = new File("data");
        File[] files = folder.listFiles();

        if (files == null) {
            System.out.println("No data directory found.");
            return;
        }

        System.out.println("Available years for " + username + ":");

        for (File f : files) {
            String name = f.getName(); // e.g., "john_2023.csv"
            if (name.startsWith(username + "_") && name.endsWith(".csv")) {
                String yearStr = name.substring(name.indexOf("_") + 1, name.indexOf("."));
                System.out.println(yearStr);
            }
        }
    }

    /**
     * Updates the users budget for a given year.
     * 
     * @param username the name of the user whose data is being accessed
     * @param year     the year the data is being updated for
     * @param budget   the updated budget information
     * @author Farhan Chowdhury
     */
    public void updateUserBudget(String username, int year, Budget budget) {
        saveUserData(username, year, budget);
    }

    /**
     * Get the budget for a given user on a given year.
     *
     * @param username the name of the user whose data is being accessed
     * @param year     the year the data is being accessed
     * @return The {@link Budget} object with all the budget information for the
     *     		specified year, or {@code null} if no saved file exists or if an
     *         	error occurs while reading the file.
     * @author Farhan Chowdhury
     */
    public Budget getUserBudget(String username, int year) {
        String filePath = getFilePath(username, year);
        File file = new File(filePath);

        if (!file.exists())
            return null;

        try {
            return loadBudgetFromFile(filePath);
        } catch (IOException e) {
            System.out.println("Error reading user budget: " + e.getMessage());
            return null;
        }
    }

    /**
     * Private helper method to load a budget from a CSV file.
	 * 
     * @param filePath the path to the CSV file containing transaction data
     * @author Farhan Chowdhury
     * @return a {@link Budget} object populated with all transactions from the file
     * @throws IOException if an error occurs while reading the file
     */
    private Budget loadBudgetFromFile(String filePath) throws IOException {
        ArrayList<Transaction> list = csvHandler.readCSV(filePath);
        Budget budget = new Budget();

        for (Transaction t : list) {
            budget.addTransaction(t.getDate(), t.getCategory(), t.getAmount());
        }

        return budget;
    }

    /**
     * Imports a CSV file from an external location, converts it into a Budget,
     * saves it into the user's storage directory, and returns the Budget.
     *
     * @param username the user who owns the budget
     * @param year     the year the imported CSV represents
     * @param csvFilePath the full path to the external CSV file to import
     * @return a fully populated Budget object
     * @throws IOException if the CSV cannot be read
     * @author Farhan Chowdhury
     */
    public Budget importCSV(String username, int year, String csvFilePath) throws IOException {
        ArrayList<Transaction> transactions = csvHandler.readCSV(csvFilePath);
        Budget budget = new Budget();

        for (Transaction t : transactions) {
            budget.addTransaction(t.getDate(), t.getCategory(), t.getAmount());
        }

        saveUserData(username, year, budget);

        return budget;
    }
}
