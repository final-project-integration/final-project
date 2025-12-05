/** 
 * The Accounts Class manages all account operations, including:
 * - Creating new user accounts.
 * - Managing authentication session state (signed in/signed out).
 * - Handling password changes using either old-password verification or secret answer recovery.
 * - Setting and updating secret questions and secret answers.
 * - Deleting existing user accounts.
 * 
 * It relies on the Authentication and Storage systems to perform the following
 * required operations.
 * 
 * Storage method Usage:
 *  - addAuthRecord() saves new users, updating passwords and secret questions.
 *  - getAuthInfo() retrieves stored AuthRecord objects during login or recovery.
 *  - removeAccount() deletes a user account from persistent storage.
 *  
 *  Authentication method usage:
 *  - validateCredentials() verifies username/password pairs during sign-in.
 *  - checkPassword(), checkSecretAnswer() are used during password changes to 
 *  verify old passwords or recovery answers.  
 *  - isInvalidUsernameFormat(), isInvalidPasswordFormat(), isBlankField() enforce username/
 *  password formatting rules and non-blank fields.
 *  - hashPassword() hashes passwords and secret answers before saving to Storage.
 * 
 * All sensitive data (passwords and secret answers) is hashed through Authentication before being saved.
 * 
 * 
 * @author  Zhengjun Xie
 * @author  Andony Ariza
 * @author  Jessica Ramirez
 * @author  Guarav Banepali
 * @since   2025-11-19
 */

public class Accounts {

	//The username of the currently signed-in user, or null if none is signed in
    private String username;
    
    //Tracks whether a user session is currently active. 
    private boolean signedIn = false;
    
    /** Authentication object used for hashing, validation, and credential checking 
     * during sign-in and password changes. */
    private Authentication authenticator;
    
    /** Storage object used for saving, loading, and deleting user authentication recor
     * from persistent storage. */
    
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
     * This method validates these conditions before creating an account:
     * - Rejects the username immediately if is already taken.
     * - Rejects the username if it contains any non-alphanumeric characters.
     * - Rejects blank passwords, secret questions, or secret answers.
     * 
     * If validation succeeds, the password and secret answer are hashed
     * using SHA-256. A new AuthRecord is created and stored using Storage.addAuthRecord().
     *
     * @param username the new user's username
     * @param password the chosen password
     * @param secretQuestion the secret question used for password recovery
     * @param secretAnswer the answer to the secret question
     * @return  true if validation succeeds and the record is saved, or false otherwise.
     * @author Zhengjun Xie, Jessica Ramirez
     */
   
   public boolean registerAccount(String username, String password, String secretQuestion, String secretAnswer) {

       // Username formatting must be valid (must: be non-null, 
       // contain only alphanumeric characters, no blank spaces, and 3 to 20 chars long). 
	  
       if (authenticator.isInvalidUsernameFormat(username)) {
           return false;
       }

       // Username must not already exist.
       if (authenticator.isDuplicateUsername(username)) {
           return false;
       }

       // Validate password formatting.
       if (authenticator.isInvalidPasswordFormat(password)) {
           return false;
       }

       // Secret question and answer cannot be blank.
       if (authenticator.isBlankField(secretQuestion) ||
           authenticator.isBlankField(secretAnswer)) 
       {return false; }


       //Hash sensitive data.
       String hashedPassword = authenticator.hashPassword(password);
       String hashedAnswer = authenticator.hashPassword(secretAnswer);

       //Create new AuthRecord.
       Authentication.AuthRecord record =
               new Authentication.AuthRecord(hashedPassword, secretQuestion, hashedAnswer);

       //Save.
       boolean saved = storage.addAuthRecord(username, record);
       return saved;

   }
   
     /**
     * Attempts to sign in a user by first validating the input fields and then 
     * verifying the credentials entered. 
     *
     * Rejects blank usernames or passwords.
     * Rejects improperly formatted usernames (non-alphanumeric characters,
     * blank spaces, or not between 3 to 20 characters).
     * Rejects improperly formatted passwords (null, whitespace, or fewer 
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
	   
	   // Trim username only for login (prevents accidental trailing spaces).
	   if (username != null) {
	        username = username.trim();
	    }
	   
	   // Username and password must not be blank.
	   if (authenticator.isBlankField(username) || authenticator.isBlankField(password)) {
	        return false;
	    }
	   
	   // Username format must be valid.
	   if (authenticator.isInvalidUsernameFormat(username)) {
	        return false;
	    }
	   
	   // Password format must be valid.
	   if (authenticator.isInvalidPasswordFormat(password)) {
	        return false;
	    }
	   
       // Credential check (Ensures username and password match).
       if (!authenticator.validateCredentials(username, password))
           return false;

       // Session state.
       this.username = username;
       this.signedIn = true;
       return true;
   }

    /**
     * Sign out the currently active user and clears their session data.
     * @return true if sign-out was successful, or false otherwise
     * @author Zhengjun Xie
     */
   
   public boolean signOut() {
       if (!signedIn) return false;
       
       signedIn = false;
       username = null;
       return true;
   }

    /**
     * Changes the user’s password after using one of two verification options: 
     * 
     * 1) Using the old password. (The user must be signed in, provided username must
     * match the signed-in user, and the old password must be correct).
     * 
     * 2) Using the secret answer. (No sign-in is required due to lost password, and the stored 
     * hash secret answer must match the provided answer).
     * 
     * After verification, the new password is validated and hashed. The updated AuthRecord
     * is then saved using Storage.addAuthRecord().
     * 
     * @param username the username of the account
     * @param oldPassword the current password
     * @param secretAnswer the answer to the secret question
     * @param newPassword the new password to set
     * @return true if the password is successfully reset, false otherwise
     * @author Zhengjun Xie
     * 
     */
   
   public boolean changePassword(String username, String oldPassword, String secretAnswer, String newPassword) {
	   Authentication.AuthRecord rec = storage.getAuthInfo(username);
	   if (rec == null) {
		   return false;
	   }
	   
	   boolean usingOldPassword = (oldPassword != null && !oldPassword.isEmpty());
	   boolean usingSecretAnswer = (secretAnswer != null && !secretAnswer.isEmpty());
	   
	   // Must supply a method of verification
	   if (!usingOldPassword && !usingSecretAnswer) {
		   return false;
	   }
	   
	   // If using old password, user must be signed in as themselves
	   if (usingOldPassword) {
		   if (!signedIn || !this.username.equals(username)) {
			   return false;
		   }
		   if (!authenticator.checkPassword(username, oldPassword)) {
			   return false;
		   }
	   }
	   
	   // If using secret answer, no sign-in required (account recovery option)
	   if (usingSecretAnswer) {
		   if (!authenticator.checkSecretAnswer(username, secretAnswer)) {
			   return false;
		   }
	   }
	   
	   // Validate new password format
	   if (authenticator.isInvalidPasswordFormat(newPassword)) {
		   return false;
	   }
	   
	   // Perform password reset
	   rec.setHashedPassword(authenticator.hashPassword(newPassword));
	   return storage.addAuthRecord(username, rec);
   }
   
    /**
     * Updates the user's secret question and secret answer.
     * Only the signed-in user may change their own recovery information.
     * 
     * Requirements:
     * - A user must be signed in.
     * - The provided username must match the signed-in user (this.username.equals(username)).
     * - Question and answer must be non-blank.
     * 
     * The new answer is hashed, and the updated record is saved using Storage.addAuthRecord().
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
    * Deletes the currently signed in user’s account.
    * 
    * Requirements:
    * - A user must be signed in.
    * - The provided username must match the signed-in user. 
    * 
    * Although the user is signed-in, the method still verifies that a stored 
    * record still exists in Storage before deletion. It is then removed using Storage.removeAccount().
    * After deletion, this method automatically calls signOut() to clear session state.
    * 
    * Note: A deleted account cannot be recovered once removed from persistent storage.
    * 
    * @param username the username of the account 
    * @return true if the user was signed in and the account was deleted, 
    * or false otherwise
    * @author Jessica Ramirez
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
