
/** 
 * TEAM: ACCOUNTS
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
     * Registers a new user account with early validation and secure data handling.
     * This method performs validation before creating an account:
     * Rejects the username immediately if is already taken
     * Rejects the username if it contains any non-alphanumeric characters
     * Rejects the password, secret question, or secret answer if any are blank.
     * If all validation passes, the password and secret answer are hashed
     * using SHA-256 and stored inside a new AuthRecord object. The record is then
     * saved to storage.
     *
     * @param username the new user's username
     * @param password the chosen password
     * @param secretQuestion the secret question used for password recovery
     * @param secretAnswer the answer to the secret question
     * @param confirm adds confirmation step on whether the user approved final account creation
     * @return  true only if validation succeeds, the user confirms creation, and
     * the authentication record is successfully saved to persistent storage. Returns 
     * false if validation fails, the user cancels, or saving fails.
     * @author Zhengjun Xie, Jessica Ramirez
     */
   
   public boolean registerAccount(String username, String password, String secretQuestion, String secretAnswer, boolean confirm) {

	   // Username cannot be blank
       if (authenticator.isBlankUsername(username)) {
           return false;
       }

       // Username formatting must be valid (must: be non-null, 
       // contain only alphanumeric characters, no blank spaces, and at least 3 characters long
       if (authenticator.isInvalidUsernameFormat(username)) {
           return false;
       }

       // Username must not already exist
       if (authenticator.isDuplicateUsername(username)) {
           return false;
       }

       // Validate password formatting
       if (authenticator.isInvalidPasswordFormat(password)) {
           return false;
       }

       // Secret question and answer cannot be blank
       if (authenticator.isBlankField(secretQuestion) ||
           authenticator.isBlankField(secretAnswer)) 
       {return false; }

       // Confirm account creation
       if (!confirm) {return false;}


       //Hash sensitive data
       String hashedPassword = authenticator.hashPassword(password);
       String hashedAnswer = authenticator.hashPassword(secretAnswer);

       //Create new AuthRecord
       Authentication.AuthRecord record =
               new Authentication.AuthRecord(hashedPassword, secretQuestion, hashedAnswer);

       //Save
       boolean saved = storage.addAuthRecord(username, record);
       return saved;

   }
   
     /**
     * Attempts to sign in a user by first validating the input fields and then 
     * verifying the credentials entered. 
     *
     * Rejects blank usernames or passwords.
     * Rejects improperly formatted usernames (non-alphanumeric characters,
     * blank spaces, or fewer than 3 characters)
     * Rejects improperly formatted passwords (blanks, whitespace, or fewer 
     * than the minimum required length).
     * Validates the credentials by comparing the stored hashed password with 
     * the hash of the provided password.
     * If validation succeeds, the user's session is activated and the user 
     * becomes marked as signed in.
     * 
     * @param username the username of the account
     * @param password the password entered by the user
     * @return true if sign-in is successful, or false otherwise
     * @author Jessica Ramirez
     */
   
   public boolean signIn(String username, String password) {
	   
	   // Username and password must not be blank.
	   if (authenticator.isBlankField(username) || authenticator.isBlankField(password)) {
	        return false;
	    }
	   
	   // Username format must be valid (alphanumeric, more than 3 characters,
	   // no spaces)
	   if (authenticator.isInvalidUsernameFormat(username)) {
	        return false;
	    }
	   
	   // Password format must be valid
	   if (authenticator.isInvalidPasswordFormat(password)) {
	        return false;
	    }
	   
       // Credential check (Ensures username and password match)
       if (!authenticator.validateCredentials(username, password))
           return false;

       // Session state
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
       
       signedIn = false;
       username = null;
       return true;
   }

    /**
     * Change the user’s password after: verifying the old password, checking
     * if the user is signed in, ensuring the username matches the logged-in user, 
     * proper format entered, and the old password is correct. 
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

        // Validate old password without changing session state
       if (!authenticator.checkPassword(username, oldPassword)) {
           return false;
       }
     
       // Validate new password formatting
       if (authenticator.isInvalidPasswordFormat(newPassword)) {
           return false;
       }

       Authentication.AuthRecord rec = storage.getAuthInfo(username);
       rec.setHashedPassword(authenticator.hashPassword(newPassword));

       boolean saved = storage.addAuthRecord(username, rec);
       return saved;
   }
   
    /**
     * Resets a user's password using their secret answer for verification
     * Ensures new password follows proper formatting. 
     *
     * @param username  the username of the account
     * @param secretAnswer the plain-text recovery answer entered by the user
     * @param newPassword the new password to set
     * @return true if the password is successfully reset, or false otherwise
     * @author Zhengjun Xie
     */
   public boolean resetPassword (String username, String secretAnswer, String newPassword){
       Authentication.AuthRecord rec = storage.getAuthInfo(username);
       
       if (rec == null) {
    	   return false;
       }
       
       // Verify secret answer first
       if (!authenticator.checkSecretAnswer(username, secretAnswer)) {
           return false;
       }

       // Password format validation
       if (authenticator.isInvalidPasswordFormat(newPassword)) {
           return false;
       }
       
       // Apply password reset
       rec.setHashedPassword(authenticator.hashPassword(newPassword));
       return storage.addAuthRecord(username, rec);
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
       
        // Validate non-blank question/answer
       if (authenticator.isBlankField(question)) {
           return false;
       }


       if (authenticator.isBlankField(answer)) {
           return false;
       }

       Authentication.AuthRecord rec = storage.getAuthInfo(username);
       if (rec == null) return false;

       rec.setSecretQuestion(question);
       rec.setHashedSecretAnswer(authenticator.hashPassword(answer));

       boolean saved = storage.addAuthRecord(username, rec);
       return saved;

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
     * @return true if the answer matches, or false otherwise
     * @author Zhengjun Xie
     */
   public boolean verifySecretAnswer(String username, String answer) {
       return authenticator.checkSecretAnswer(username, answer);
   }
   
   /**
    * Deletes the currently signed in user’s account if it exists
    * If the deleted account is the one currently signed in, the user is signed out.
    * This method *requires* an active session to delete an account.
    *
    * @param username the username of the account 
    * @return true if the user was signed in and the account was deleted, or false otherwise
    * @author Jessica Ramirez, Zhengjun Xie
    */
   
   public boolean deleteUser(String username) {

	    // Must be signed in AND deleting own account
	    if (!signedIn || !username.equals(this.username)) {
	        return false;
	    }

	    if (storage.getAuthInfo(username) == null) {
	        return false;
	    }

	    storage.removeAccount(username);
	    signOut();
	    return true;
	}
}
