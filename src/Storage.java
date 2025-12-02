import java.util.HashMap;
import java.util.Map;
import java.io.*;

/**
 * TEAM: ACCOUNTS
 * The Storage class manages all authentication related records for users.
 * It stores, retrieves, updates, and removes AuthRecord objects, which contain
 * hashed passwords, secret questions, and hashed secret answers.
 *
 * This class is used by both Accounts and Authentication to make sure that
 * authentication data remains consistent
 *
 * @author  Zhengjun Xie
 * @author  Andony Ariza
 * @author  Jessica Ramirez
 * @author  Guarav Banepali
 * @since   2025-11-06
 */

public class Storage {

	/** 
     * A map storing all authentication records, where each key is a username 
     * and each value is the corresponding AuthRecord.
     */
    private Map<String, Authentication.AuthRecord> authData = new HashMap<>();
    private static final String FILE_NAME = "auth_data.csv";
    
    /**
     * Constructs a new Storage object.
     * Initializes an empty authentication record database.
     *
     * @author Andony Ariza
     */
    
    public Storage() {
    	loadFromFile();
    }

    /**
     * Adds or updates the authentication record for a specific username.
     *
     * @param username the username associated with the record
     * @param rec the AuthRecord to store or overwrite
     * @author Zhengjun Xie
     */
    public void addAuthRecord(String username, Authentication.AuthRecord rec) {
        authData.put(username, rec);
        saveToFile();
    }

    /**
     * Retrieves the stored authentication record for the username
     *
     * @param username the username whose authentication data is being requested
     * @return the AuthRecord associated with the username, or null if none exists
     * @author Zhengjun Xie
     */
    
    public Authentication.AuthRecord getAuthInfo(String username) {
        return authData.get(username);
    }

    /**
     * Removes all authentication data associated with a username
     *
     * @param username the username whose authentication record should be deleted
     * @author Zhengjun Xie
     */
    public void removeAccount(String username) {
        authData.remove(username);
        saveToFile();
    }

    /**
     * Returns a shallow copy of the authentication record map.
     *
     * @return a new Map containing all username to AuthRecord entries
     * @author Zhengjun Xie
     */

    public Map<String, Authentication.AuthRecord> loadAllAuthRecords() {
        return new HashMap<>(authData);
    }


/** Saves all authentication records to a CSV file
 * Has 4 columns: username, hashedPassword, secretQuestions, hashedSecretAnswer
 * @author Jessica Ramirez */
    private void saveToFile() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

        // CSV HEADER (optional)
        writer.write("username,hashedPassword,secretQuestion,hashedSecretAnswer");
        writer.newLine();

        for (String username : authData.keySet()) {
            Authentication.AuthRecord rec = authData.get(username);

            // Escape commas in secret question (if they exist)
            String question = rec.getSecretQuestion().replace(",", "\\,");
            
            writer.write(username + "," +
                         rec.getHashedPassword() + "," +
                         question + "," +
                         rec.getHashedSecretAnswer());
            writer.newLine();
        }

    } catch (Exception e) {
        System.out.println("Error saving auth data: " + e.getMessage());
    }
}

	/** Loads authentication records from the CSV file 
	 * Rebuilds all AuthRecord objects and restores them into the authData map
	 * @author Jessica Ramirez */
    
    private void loadFromFile() {
    File file = new File(FILE_NAME);
    if (!file.exists()) {
        return;  // No saved accounts yet
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

        String line = reader.readLine();  // skip header

        while ((line = reader.readLine()) != null) {

            // Split respecting commas that were escaped
            String[] parts = line.split("(?<!\\\\),");

            if (parts.length != 4) continue;

            String username = parts[0].trim();
            String hashedPassword = parts[1].trim();

            // Unescape commas
            String secretQuestion = parts[2].replace("\\,", ",").trim();

            String hashedSecretAnswer = parts[3].trim();

            // Rebuild AuthRecord
            Authentication.AuthRecord rec =
                    new Authentication.AuthRecord(hashedPassword, secretQuestion, hashedSecretAnswer);

            authData.put(username, rec);
        }

    } catch (Exception e) {
        System.out.println("Error loading auth data: " + e.getMessage());
    }
}
}


