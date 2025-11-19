
/** 
 * Accounts manages user data management, including the creation, modification, and deletion
 * of stored users. It interacts with the Authentication class to securely handle user 
 * information and verification.
 * 
 * 
 * @author  Zhengjun Xie
 * @author  Andony Ariza
 * @author  Jessica Ramirez
 * @author  Guarav Banepali
 * @since   2025-11-19
 */

public class Accounts {

    private String username;
    private boolean signedIn = false;
    private Authentication authenticator;
    private Storage storage;

    /**
    * Creates a new Accounts object linked to both Authentication and Storage.
    * 
    * @param auth the Authentication instance used when validating credentials and hashing. 
    * @param storage the Storage instance used to save and retrieve user authentication records. 
    * @author Jessica Ramirez
    */
    
   public Accounts(Authentication auth, Storage storage) { 
       this.authenticator = auth;
       this.storage = storage;
   }

    /**
     * Registers a new user account.
     *
     * @param username the new user's username
     * @param password the chosen password
     * @param secretQuestion the secret question used for password recovery
     * @param secretAnswer the answer to the secret question
     * @return true if registration is successful, or false otherwise
     * @author Zhengjun Xie
     */
   public boolean registerAccount(String username, String password, String secretQuestion, String secretAnswer) {

       //Username already exists
       if (authenticator.checkUsername(username))
           return false;

       //Validate fields
       if (!authenticator.validateUserInfo(username, password, secretQuestion, secretAnswer))
           return false;

       //Hash sensitive data
       String hashedPassword = authenticator.hashPassword(password);
       String hashedAnswer = authenticator.hashPassword(secretAnswer);

       //Create new AuthRecord
       Authentication.AuthRecord record =
               new Authentication.AuthRecord(hashedPassword, secretQuestion, hashedAnswer);

       //Save
       storage.addAuthRecord(username, record);
       return true;
   }
   
     /**
     * Attempts to sign in a user by validating the provided credentials. If successful,
     * the user becomes marked as signed in.
     *
     * @param username the username of the account
     * @param password the password entered by the user
     * @return true if sign-in is successful, or false otherwise
     * @author Zhengjun Xie
     */
   
   public boolean signIn(String username, String password) {

       if (!authenticator.validateCredentials(username, password))
           return false;

       this.username = username;
       this.signedIn = true;
       return true;
   }

    /**
     * Sign out the currently active user and clears their session data.
     * @return true if sign-out was successful,or false otherwise
     * @author Zhengjun Xie
     */
   
   public boolean signOut() {
       if (!signedIn) return false;

       authenticator.clearSession();
       signedIn = false;
       username = null;
       return true;
   }

    /**
     * Change the user’s password after: verifying the old password, checking
     * if the user is signed in, ensuring the username matches the logged-in user, 
     * and the old password is correct. 
     * 
     * @param username the username of the account
     * @param oldPassword the current password
     * @param newPassword the new password to set
     * @return true if the password is successfully reset, false otherwise
     * @author Zhengjun Xie
     * 
     */
   
   public boolean changePassword(String username, String oldPassword, String newPassword) {

       if (!signedIn || !this.username.equals(username))
           return false;

       if (!authenticator.validateCredentials(username, oldPassword))
           return false;

       Authentication.AuthRecord rec = storage.getAuthInfo(username);
       rec.setHashedPassword(authenticator.hashPassword(newPassword));

       storage.addAuthRecord(username, rec);
       return true;
   }
   
    /**
     * Resets a user's password without requiring the old password.
     * Used during password recovery after verifying the secret answer.
     *
     * @param username  the username of the account
     * @param newPassword the new password to set
     * @return true if the password is successfully reset, false otherwise
     * @author Zhengjun Xie
     */
   public boolean resetPassword(String username, String newPassword) {
       Authentication.AuthRecord rec = storage.getAuthInfo(username);
       if (rec == null) return false;

       rec.setHashedPassword(authenticator.hashPassword(newPassword));
       storage.addAuthRecord(username, rec);
       return true;
   }

    /**
     * Updates the user's secret question and secret answer.
     * Only the signed-in user may change their own recovery information.
     *
     * @param username the username of the account
     * @param question the selected secret question
     * @param answer the secret answer for the question selected
     * @return true if both the question and answer were saved successfully, or false otherwise.
     * @author Jessica Ramirez
     */
   public boolean setSecretQuestionAndAnswer(String username, String question, String answer) {

       if (!signedIn || !this.username.equals(username))
           return false;

       Authentication.AuthRecord rec = storage.getAuthInfo(username);
       if (rec == null) return false;

       rec.setSecretQuestion(question);
       rec.setHashedSecretAnswer(authenticator.hashPassword(answer));

       storage.addAuthRecord(username, rec);
       return true;
   }

     /**
     * Retrieves the stored secret question for the given username during password recovery.
     *
     * @param username the username of the account
     * @return the stored secret question, or null if that user DNE.
     * @author Jessica Ramirez
     */
   public String getSecretQuestion(String username) {
       Authentication.AuthRecord rec = storage.getAuthInfo(username);
       if (rec == null) {
           return null;
       }
       return rec.getSecretQuestion();
   }

    /**
     * Verifies whether a provided secret answer matches the stored hashed answer.
     *
     * @param username the username of the account
     * @param answer the plain-text answer entered by the user
     * @return true if the answer matches; false otherwise
     * @author Zhengjun Xie
     */
   public boolean verifySecretAnswer(String username, String answer) {
       return authenticator.checkSecretAnswer(username, answer);
   }
   
    /**
     * Removes a user account and all associated records from the system. 
     *
     * @param username the username of the account
     * @return true if the deletion is successful, or false otherwise
     * @author Jessica Ramirez
     */

	public boolean deleteUser(String username) {
		if (!signedIn || !this.username.equals(username))
			return false;

		storage.removeAccount(username);
		signOut();
		return true;
		}
	}


