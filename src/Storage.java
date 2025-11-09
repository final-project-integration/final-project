
import java.util.Map;

/**
 * Storage handles the saving, loading, and retrieving of stored user data
 * between sessions of use. The Accounts class relies on Storage to ensure that
 * user information is consistent.
 * 
 * 
 * @author Zhengjun Xie
 * @author Andony Ariza
 * @author Jessica Ramirez
 * @author Guarav Banepali
 * @author Steven Farell
 * @since  2025-11-06
 */

public class Storage {

	/**
	* Constructs a new Storage object. 
	* This default constructor initializes Storage class (no parameters).
	*
	* @author Andony Ariza
	*/
	public Storage() { //Default Constructor
	}
	
	/**
	 * Saves the user's account data.
	 *
	 * @param users a map with user Account objects
	 * @author Andony Ariza
	 */
	public void saveUserInfo(Map<String, Accounts> users) {

	}

	/**
	 * Loads a map of all the stored users.
	 * @return a map of usernames and their corresponding Accounts objects
	 * @author Andony Ariza
	 */
	public Map<String, Accounts> loadUserInfo() {
		return null;
	}

	/**
	 * Retrieves the stored user information of a specific username.
	 *
	 * @param username the username of the account to return
	 * @return the Account object for the given username
	 * @author Andony Ariza
	 */
	public Accounts getUserInfo(String username) {
		return null;
	}


}


