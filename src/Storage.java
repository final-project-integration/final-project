import java.util.HashMap;
import java.util.Map;

/**
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

    
    /**
     * Constructs a new Storage object.
     * Initializes an empty authentication record database.
     *
     * @author Andony Ariza
     */
    
    public Storage() {}

    /**
     * Adds or updates the authentication record for a specific username.
     *
     * @param username the username associated with the record
     * @param rec the AuthRecord to store or overwrite
     * @author Zhengjun Xie
     */
    public void addAuthRecord(String username, Authentication.AuthRecord rec) {
        authData.put(username, rec);
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
}



