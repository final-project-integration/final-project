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
	 * Saves the user's account data
	 *
	 * @param users - a map with user Account objects
	 * @author
	 */
	public void saveUserInfo(Map<String, Accounts> users) {

	}

	/**
	 * Loads a map of all the stored users
	 *
	 * @author
	 */
	public Map<String, Accounts> loadUserInfo() {
		
	}

	/**
	 * Retrieves the stored user information of a specific username
	 *
	 * @param username - the username of the account to return
	 * @return the Account object for the given username
	 * @author
	 */
	public Accounts getUserInfo(String username) {
		
	}

}