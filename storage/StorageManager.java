package storage;

/**
 * The StorageManager Class handles reading and writing
 * user budget information to and from saved files.
 * * @author Farhan Chowdhury
 * @version 11/4/2025
 */

public class StorageManager{
	
	
	/**
	 * Default Constructor
	 */
	public StorageManager() {
	}

	/**
	 * Loads the user data from saved file.
	 * @param username the name of the user whose data is being loaded
	 * @param year the year the data is being loaded for
	 * @author Farhan Chowdhury
	*/	
	public void loadUserData(String username, int year){
			
	}
	
	/**
	 * Saves the current user data to a file.
	 * @param username the name of the user whose data is being saved
	 * @param year the year the data is being saved for
	 * @author Farhan Chowdhury
	*/	
	public void saveUserData( String username, int year){
			
	}
	
	/**
	 * Deletes the user Data from file.
	 * @param username the name of the user whose data is being deleted
	 * @param year the year the data is being deleted
	 * @author Farhan Chowdhury
	*/
	public void deleteUserData(String username, int year ){
			
	}
	
	/**
	 * Lists all the years for which the data is available for a user.
	 * @param username the name of the user whose data is being accessed
	 * @author Farhan Chowdhury
	*/		
	public void listAvailableYears(String username){
			
	}
	
	/**
	 * Updates the users budget for a given year.
	 * @param username the name of the user whose data is being accessed
	 * @param year the year the data is being updated for
	 * @param budgetData the updated budget information
	 * @author Farhan Chowdhury
	*/	
	public void updateUserBudget( String username, int year, Object budgetData){
			
	}
	
	/**
	 * Get the budget for a given user on a given year.
	 * @param username the name of the user whose data is being accessed
	 * @param year the year the data is being accessed
	 * @author Farhan Chowdhury
	 * @return The {@link Budget} object with all the budget information for the specified year
	*/	
	public Budget getUserBudget(String username, int year ){
		return null;
	}

}
