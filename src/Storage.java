import java.util.HashMap;
import java.util.Map;
import java.io.*;

/**
 * The Storage class manages all authentication related records for users.
 * It stores, retrieves, updates, and removes AuthRecord objects, which contain
 * hashed passwords, secret questions, and hashed secret answers.
 *
 * Storage acts as the persistence layer for the Accounts/Authentication system.
 * This class is responsible for maintaining memory even after the program ends. 
 * It automatically loads existing records from disk at startup and saves updates whenever
 * a user account is created, modified, or deleted. 
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
     * Constructs a new Storage object  and loads any existing authentication
     * records from disk. If no file exists, the database begins empty. 
     * 
     * This ensures that user accounts persist across program sessions.
     *
     * @author Andony Ariza
     */
    
    public Storage() {
    	loadFromFile();
    }

    /**
     * Adds or updates the authentication record for the given username and saves
     * the updated authentication database to disk. This method attempts to persist
     * the new state by calling saveToFile(), and returns whether the save operation
     * worked.
     *
     * @param username the username associated with the record
     * @param rec the AuthRecord to store or overwrite
     * @return true if the record was stored and successfully saved to disk, 
     * or false if a file write error occurred
     * @author Jessica Ramirez
     */
    public boolean addAuthRecord(String username, Authentication.AuthRecord rec) {
        authData.put(username, rec);
        return saveToFile();
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
     * Removes all authentication data associated with a username and 
     * attempts to persist the updated state to disk.
     *
     * @param username the username whose authentication record should be deleted
     * @return true if the record was removed and the updated data was successfully saved to disk,
     * or false if a saving error occurred. 
     * @author Zhengjun Xie
     */
    public boolean removeAccount(String username) {
    	authData.remove(username);
        return saveToFile();
    }


    /**
     * Returns a shallow copy of all stored authentication records.
     *
     * @return a new Map containing all username to AuthRecord entries
     * @author Zhengjun Xie
     */

    public Map<String, Authentication.AuthRecord> loadAllAuthRecords() {
        return new HashMap<>(authData);
    }


    /** 
     * Saves all authentication records to a CSV file
     * This file has 4 columns: username, hashedPassword, secretQuestions, hashedSecretAnswer.
     * 
     * Secret questions may contain commas entered by users. To prevent these commas from breaking CSV formatting, 
     * they are replaced as "\," (escaped commas). 
     * 
     * This method overwrites the entire auth_data.csv file every time. 
     * If any file writing error occurs, the method prints an error and returns false. 
     * 
     * 
     * @return true if all records were successfully written to the file, or false if an error occurred 
     * during saving
     * @author Jessica Ramirez 
     */
    
    private boolean saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
        	
        	// Write CSV header.
            writer.write("username,hashedPassword,secretQuestion,hashedSecretAnswer");
            writer.newLine();

            // Loop through all user accounts stored in memory.
            for (String username:authData.keySet()) {
                Authentication.AuthRecord rec = authData.get(username);
                
                // Replace commas in secret questions so the CSV format remains valid.
                String question = rec.getSecretQuestion().replace(",", "\\,");

                //Write the user's data in CSV format.
                writer.write(username + "," + rec.getHashedPassword() + "," 
                + question + "," + rec.getHashedSecretAnswer());
                writer.newLine();
            }

            return true; // Was able to write all data to the disk.

        } catch (Exception e) {
            System.out.println("Error saving auth data: " + e.getMessage());
            return false; // File write failed. It prints an error message for debugging.
        }
    }
    
    /**
     * Splits a CSV line on commas that are not escaped.
     * 
     * In the CSV file, saveToFile() stores real commas as "\," to prevent them from being treated
     * as field separators. This method reverses that by:
     * 
     * - Treating "\," as a literal comma and adding to the current field.
     * - Treating a normal comma as a separator between fields. 
     *
     *@param line a single CSV line from auth_data.csv 
     *@return an array with 4 fields: [username, hashedPassword, secretQuestion (with commas restored), hashedSecretAnswer]
     *@author Jessica Ramirez 
     */
    
    private String[] splitCommas(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            // Case 1: Escaped comma "\,"
            if (c == '\\' && i + 1 < line.length() && line.charAt(i + 1) == ',') {
                current.append(','); //Add REAL comma to field
                i++; //Skip next character (the comma)
            }
            // Case 2: Normal unescaped comma. This is a field separator.
            else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0); // Prepare for next field.
            }
            // Case 3: Normal character.
            else {
                current.append(c);
            }
        }

        // Add final field.
        fields.add(current.toString());

        return fields.toArray(new String[0]);
    }



	/** 
	 * Loads authentication records from the CSV file into memory.
	 * 
	 * Behavior:
	 * - If the file does not exist, the method returns immediately and the in-memory
	 * authentication map remains empty.
	 * - Reads each line of the CSV (skips the header).
	 * - Uses splitCommas() to parse fields containing escaped commas. ("\," becomes ",").
	 * - Rebuilds all AuthRecord objects and loads them into the authData map.
	 * 
	 * This method makes sure that all previously saved user accounts are restored
	 * when the program starts.
	 * 
	 * @author Jessica Ramirez
	 */
    
    private void loadFromFile() {
    File file = new File(FILE_NAME);
    
    if (!file.exists()) {
        return;  // No saved accounts yet.
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

        String line = reader.readLine();// Skip the header line: username,hashedPassword,secretQuestion,hashedSecretAnswer

        //Go through each account record
        while ((line = reader.readLine()) != null) {

        	// Parse the line using a split that handles escaped commas
            String[] parts = splitCommas(line);

            // Must have exactly 4 fields to form a valid AuthRecord
            if (parts.length != 4) continue;

            String username = parts[0].trim();
            String hashedPassword = parts[1].trim();

            // Convert "\," into regular commas
            String secretQuestion = parts[2].replace("\\,", ",").trim();

            String hashedSecretAnswer = parts[3].trim();

            // Rebuild AuthRecord
            Authentication.AuthRecord rec =
                    new Authentication.AuthRecord(hashedPassword, secretQuestion, hashedSecretAnswer);
            
            // Normalize username before storing
            authData.put(username.toLowerCase(), rec);

            
        }

    } catch (Exception e) {
        System.out.println("Error loading auth data: " + e.getMessage());
        
    }
    
}
}
