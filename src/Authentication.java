import java.security.MessageDigest;

/**
 * The Authentication class handles all security-related functionality for user accounts including:
 * - Hashing passwords and secret answers using SHA-256
 * - Validating username and password formats
 * - Checking secret answers for account recovery.
 * - Accessing stored AuthRecord objects through Storage.
 * 
 * Authentication makes sure that no plain-text passwords or secret answers are
 * stored.
 * 
 * The class does not store users itself. It depends on Storage for persistence and on
 * Accounts for session management.
 * 
 * Required Storage interactions:
 * - getAuthInfo() for authentication and memory.
 * - addAuthRecord() indirectly (used by Accounts for password changes).
 *  
 * @author  Zhengjun Xie
 * @author  Andony Ariza
 * @author  Jessica Ramirez
 * @author  Guarav Banepali
 * @since   2025-11-19
 * 
 */



public class Authentication {

    private Storage storage;
    
    /**
     * Creates a new Authentication object linked to a Storage instance.
     *
     * @param storage the Storage instance containing all authentication records
     * @author Jessica Ramirez
     */

    public Authentication(Storage storage) {
        this.storage = storage;
    }

    /**
     * Validates login attempts by hashing the provided plain text password and 
     * comparing it with the stored hashed password retrieved from Storage.getAuthInfo().
     *
     * @param username the username of the account
     * @param password the plain text password entered by the user
     * @return true if the stored and computed hashes match, or false otherwise
     * @author Zhengjun Xie
     */

    public boolean validateCredentials(String username, String password) {
        AuthRecord rec = storage.getAuthInfo(username);
        if (rec == null) return false;

        String hashedInput = hashPassword(password);
        return rec.getHashedPassword().equals(hashedInput);

    }


    /**
     * Checks whether the provided password matches the stored hashed password
     * for the given username. Uses Storage.getAuthInfo() to retrieve the record.
     * 
     * Behavior:
     * - Retrieves the user's AuthRecord using Storage.getAuthInfo().
     * - If no record exists (rec == null), the method returns false immediately.
     * - Otherwise, the provided password is hashed and compared to the stored hash.
     * 
     * This method is used by Accounts.changePassword() when verifying a user's identity
     * through their old password. 
     *
     * @param username the username whose password is being checked
     * @param password the plain-text password entered by the user
     * @return true if the user exists and the hashed password matches, or false otherwise
     * @author Zhengjun Xie
     */

    public boolean checkPassword(String username, String password) {
        AuthRecord rec = storage.getAuthInfo(username);
        if (rec == null) return false;

        return rec.getHashedPassword().equals(hashPassword(password));
    }

    /**
     * Checks whether the given username exists in persistent Storage.
     *
     * Behavior:
     * - Calls Storage.getAuthInfo(username) to look up the user's AuthRecord.
     * - If getAuthInfo() returns null, the username does not exist. 
     * - Otherwise, the username is already registered. 
     * 
     * This method supports early validation during account creation.
     *  
     * @param username the username to look up
     * @return true if the username exists in storage, or false otherwise
     * @author Zhengjun Xie
     */

    
    public boolean checkUsername(String username) {
        return storage.getAuthInfo(username) != null;
    }

    /**
     * Validates a user's secret answer by hashing the user's answer and comparing
     * it with the stored hashed secret answer.
     * 
     * Behavior:
     * - Retrieves the user's AuthRecord using Storage.getAuthInfo().
     * - If no record exists (rec == null), the method returns false immediately.
     * - Otherwise, the provided answer is hashed and compared with the stored hash. 
     * 
     * Used by Accounts.changePassword() for password recovery when user is not signed in.
     *
     * @param username the username of the account
     * @param answer   the plain-text secret answer entered by the user
     * @return true if the user exists in storage and hashed answers match, or false otherwise
     * @author Zhengjun Xie
     */

    public boolean checkSecretAnswer(String username, String answer) {
        AuthRecord rec = storage.getAuthInfo(username);
        if (rec == null) return false;

        return rec.getHashedSecretAnswer().equals(hashPassword(answer));
    }

    /**
     * Retrieves a user's stored secret question for password recovery.
     *
     * Behavior:
     * - Looks up the user's AuthRecord using Storage.getAuthInfo().
     * - If no record exists (rec == null), the method returns null immediately.
     * - Otherwise, it returns the plain-text secret question stored in the record. 
     * @param username the username whose secret question is requested
     * @return the stored secret question, or null if the user does not exist
     * @author Zhengjun Xie
     */

    
    public String getSecretQuestion(String username) {
        AuthRecord rec = storage.getAuthInfo(username);
        if (rec == null) return null;
        return rec.getSecretQuestion();

    }

    /**
     * Checks if a field is blank (null, empty, or only whitespace).
     * This supports early validation so the user is immediately notified
     * when attempting to submit incomplete data.
     *
     * @param field the text to check
     * @return true if the field is blank, or false otherwise
     * @author Jessica Ramirez
     */

    public boolean isBlankField(String field) {
        return field == null || field.isBlank();
    }
        
    /**
     * Hashes a plain-text string (password or secret answer) using SHA-256.
     * 
     * Behavior: 
     * - Converts the input string into a SHA-256 hash.
     * - Returns the hexadecimal string representation of the hash.
     * - If any exception occurs during hashing, the method returns an 
     * empty string ("") as a fallback value. 
     *
     * @param plain the plain-text string to be hashed
     * @return the SHA-256 hash as a hex string, or "" if hashing fails
     * @author Zhengjun Xie
     */

    public String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plain.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));

            return sb.toString();

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * A data container storing authentication information for a single user.
     * 
     * Fields:
     * - hashedPassword (SHA-256 hashed password).
     * - secretQuestion (stored as plain text).
     * - hashedSecretAnswer (SHA-256 hashed version of the recovery answer).
     * 
     * No sensitive fields (password or secret answer) are ever stored in plain text.
     * The secret question is stored normally because it does not reveal sensitive data.
     * 
     * Stored and retrieved through Storage using addAuthRecord(), getAuthInfo(), and removeAccount().
 
     * @author Zhengjun Xie
     */

    public static class AuthRecord {
        private String hashedPassword;
        private String secretQuestion;
        private String hashedSecretAnswer;
        
        /**
         * Creates a new AuthRecord storing hashed password, secret question,
         * and hashed secret answer.
         *
         * @param hp the SHA-256 hashed password
         * @param sq the plain-text secret question
         * @param hsa the SHA-256 hashed secret answer
         */


        public AuthRecord(String hp, String sq, String hsa) {
            this.hashedPassword = hp;
            this.secretQuestion = sq;
            this.hashedSecretAnswer = hsa;
        }
        
        /**
         * Retrieves the stored hashed password.
         * @return the SHA-256 hashed password string
         */
        
        public String getHashedPassword() { 
            return hashedPassword; 
        }
        
        /**
         * Retrieves the stored secret recovery question. 
         * @return the secret question in plain text
         */
        public String getSecretQuestion() { 
            return secretQuestion; 
        }
        
        /** 
         * Retrieves the stored hashed secret answer.
         * @return the SHA-256 hashed secret answer
         */
        public String getHashedSecretAnswer() { 
            return hashedSecretAnswer; 
        }
        
        /**
         * Updates the stored hashed password.
         * @param hashed the new hashed password value
         */

        public void setHashedPassword(String hashed) {
            this.hashedPassword = hashed;
        }
        
        /**
         * Updates the user's secret recovery question.
         * @param question the new secret question in plain text
         */

        public void setSecretQuestion(String question) {
            this.secretQuestion = question;
        }
        
        /**
         * Updates the stored hashed secret answer.
         *
         * @param answer the new hashed secret answer
         */

        public void setHashedSecretAnswer(String answer) {
            this.hashedSecretAnswer = answer;
        }
    }

    /**
     * Returns true if the credentials are invalid.
     * Used for detecting failed login attempts.
     *
     * @param username the username of the account
     * @param password the password entered
     * @return true if username does not exist or password hash does not match, or false otherwise
     * @author Jessica Ramirez
     */


    public boolean checkInvalidCredentials(String username, String password) {
    return !validateCredentials(username, password);
    }
    
    /**
     * Checks whether a username already exists in the system.
     * Supports early validation so the user is notified 
     * immediately before entering additional account fields.
     *
     * @param username the username of the account
     * @return true if the username already exists, or false otherwise
     * @author Jessica Ramirez
     */


    public boolean isDuplicateUsername(String username) {
        return checkUsername(username);
    }
    
    /**
     * Checks whether a username has invalid formatting.
     * A valid username must: be non-null, contain only alphanumeric characters
     * (A-Z, a-z, 0-9), not contain leading/trailing spaces, and be at least 3 to 20 characters long.
     *
     * @param username the username of the account. 
     * @return true if the username violates any rule, false if it is valid
     * @author Jessica Ramirez
     */
    
    public boolean isInvalidUsernameFormat(String username) {
        if (username == null) return true;

        // Cannot be empty or only whitespace
        if (username.trim().isEmpty()) return true;

        // No leading/trailing spaces
        if (!username.equals(username.trim())) return true;

        // Must be alphanumeric only
        if (!username.matches("[A-Za-z0-9]+")) return true;

        // Minimum length
        if (username.length() < 3) return true;
        
       // Max length check
        if (username.length() > 20) return true;

        return false;
    }
    
    /**
     * Checks whether a password has invalid formatting.
     * A valid password must be non-null, not be empty or whitespace-only,
     * not contain leading or trailing spaces, and have 5 to 30 characters. 
     * It may include special characters.
     *
     * @param password the password to check
     * @return true if the password violates any formatting rule, or false if it is valid
     * @author Jessica Ramirez
     */
    
    public boolean isInvalidPasswordFormat(String password) {
        if (password == null) return true;

        // Cannot be empty or whitespace
        if (password.trim().isEmpty()) return true;

        // Cannot have spaces at the beginning or end
        if (!password.equals(password.trim())) return true;

        // Minimum length 
        if (password.length() < 5) return true;
        
        // Max length check (allow long passwords)
        if (password.length() > 30) return true;

        return false;
    }
    
    /**
     * Checks whether required login or registration fields are empty.
     *
     * @param username the username of the account
     * @param password the password entered
     * @return true if any required field is null, empty, or whitespace only; or false 
     * if both fields are valid
     * @author Jessica Ramirez
     */


    public boolean checkIncompleteForm(String username, String password) {
        if (username == null || username.trim().isEmpty()) 
            return true;
        if (password == null || password.trim().isEmpty()) 
            return true;
        return false;
    }
}
