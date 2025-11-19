import java.security.MessageDigest;

/**The Authentication class handles all security-related functionality for user accounts,
 * including verifying credentials, hashing passwords and secret answers, validating user input,
 * and retrieving authentication records from Storage. It ensures that no sensitive information
 * such as passwords or secret answers are ever stored in plain text.
 * Authentication uses SHA-256 hashing to secure all sensitive fields.
 
 * @author  Zhengjun Xie
 * @author  Andony Ariza
 * @author  Jessica Ramirez
 * @author  Guarav Banepali
 * @since   2025-11-19
 *  **/


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
     * Validates user credentials by comparing the stored hashed password
     * with a hashed version of the provided plain-text password.
     *
     * @param username the username being authenticated
     * @param password the plain-text password entered by the user
     * @return true if the credentials match or false otherwise
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
     * for the given username.
     *
     * @param username the username whose password is being checked
     * @param password the plain-text password entered by the user
     * @return true if the password matches the stored hash or false otherwise
     * @author Zhengjun Xie
     */

    public boolean checkPassword(String username, String password) {
        AuthRecord rec = storage.getAuthInfo(username);
        if (rec == null) return false;

        return rec.getHashedPassword().equals(hashPassword(password));
    }

    /**
     * Checks whether the given username exists in Storage.
     *
     * @param username the username to look up
     * @return true if the username exists, or false otherwise
     * @author Zhengjun Xie
     */

    
    public boolean checkUsername(String username) {
        return storage.getAuthInfo(username) != null;
    }

    /**
     * Validates a user's secret answer by hashing the user's answer and comparing
     * it with the stored hashed secret answer.
     *
     * @param username the username of the account
     * @param answer   the plain-text secret answer entered by the user
     * @return true if the hashed answers match; false otherwise
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
     * @param username the username whose secret question is requested
     * @return the stored secret question, or null if the user DNE
     * @author Zhengjun Xie
     */

    
    public String getSecretQuestion(String username) {
        AuthRecord rec = storage.getAuthInfo(username);
        if (rec == null) return null;
        return rec.getSecretQuestion();

    }

    /**
     * Validates registration input by ensuring no field is null or blank.
     *
     * @param username the username of the account
     * @param password the password to validate
     * @param secretQuestion the recovery question to validate
     * @param secretAnswer the recovery answer to validate
     * @return true if all fields are valid, or false otherwise
     * @author Zhengjun Xie
     */

    public boolean validateUserInfo(String username, String password, String secretQuestion, String secretAnswer) {
    	
        return !(isBlank(username) || isBlank(password) || isBlank(secretQuestion) || isBlank(secretAnswer));
    }

    /**
     * Determines whether a given text value is null, empty, or with whitespace.
     *
     * @param text the input string to check
     * @return true if the text is blank, or false otherwise
     * @author Zhengjun Xie
     */

    private boolean isBlank(String text) {
        return (text == null || text.isBlank());
    }

    /**
     * Resets a user's password after verification, hashing the new password
     * before saving it to Storage.
     *
     * @param username the username whose password will be reset
     * @param newPassword the new plain-text password to hash and store
     * @return true if the password was successfully reset, or false otherwise
     * @author Zhengjun Xie
     */

    
    public boolean recoverPassword(String username, String newPassword) {
        AuthRecord rec = storage.getAuthInfo(username);
        if (rec == null) return false;

        rec.setHashedPassword(hashPassword(newPassword));
        storage.addAuthRecord(username, rec);
        return true;
    }

    /**
     * Clears session related authentication data.
     * Future Editing
     *
     * @author Zhengjun Xie
     */

    public void clearSession() {
        // Future use
    }

    /**
     * Hashes a plain-text string (password or secret answer) using SHA-256.
     *
     * @param plain the plain-text string to be hashed
     * @return the hashed output in hexadecimal format
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
     * Stores authentication related data for a single user, including the hashed
     * password, the secret question, and the hashed secret answer.
     * No sensitive information is kept in plain text.
     *
     * @author Zhengjun Xie
     */

    
    public static class AuthRecord {
        private String hashedPassword;
        private String secretQuestion;
        private String hashedSecretAnswer;

        public AuthRecord(String hp, String sq, String hsa) {
            this.hashedPassword = hp;
            this.secretQuestion = sq;
            this.hashedSecretAnswer = hsa;
        }

        public String getHashedPassword() { 
            return hashedPassword; 
        }
        public String getSecretQuestion() { 
            return secretQuestion; 
        }
        public String getHashedSecretAnswer() { 
            return hashedSecretAnswer; 
        }

        public void setHashedPassword(String hashed) {
            this.hashedPassword = hashed;
        }

        public void setSecretQuestion(String question) {
            this.secretQuestion = question;
        }

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
     * @return true if credentials are invalid; false otherwise
     * @author Jessica Ramirez
     */


    public boolean checkInvalidCredentials(String username, String password) {
    return !validateCredentials(username, password);
    }
    
    /**
     * Checks whether a username already exists in the system.
     *
     * @param username the username of the account
     * @return true if the username already exists, or false otherwise
     * @author Jessica Ramirez
     */


    public boolean isDuplicateUsername(String username) {
        return checkUsername(username);
    }
    
    /**
     * Detects edge-case inputs such as invalid characters, surrounding whitespace,
     * or improper formatting.
     *
     * @param username the username of the account
     * @param password the password entered
     * @return true if any edge-case condition is detected, or false otherwise
     * @author Jessica Ramirez
     */


    public boolean checkEdgeCases(String username, String password) {
        if (username == null || password == null) 
            return true;

        if (username.trim().isEmpty() || password.trim().isEmpty()) 
            return true;

        if (!username.equals(username.trim()) || !password.equals(password.trim())) 
            return true;

        if (!username.matches("[A-Za-z0-9]+"))
            return true;

        if (username.length() < 3 || password.length() < 5) 
            return true;

        return false;
    }
    
    /**
     * Checks whether required login or registration fields are empty.
     *
     * @param username the username of the account
     * @param password the password entered
     * @return true if any required field is missing, or false otherwise
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




