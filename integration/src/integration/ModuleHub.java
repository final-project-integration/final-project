package integration;

/**
 * ModuleHub is the traffic controller, it directs requests to the right team's module.
 * Instead of calling other teams directly, we go through this hub to keep things organized.
 * Module hub is the middleman that talks to Accounts, Storage, Reports, Prediction, and Validation teams.
 * ModuleHub flow:
 *  1. Takes the request
 *  2. Forwards it to the right team's code
 *  3. Gets the response
 *  4. Sends it back to whoever asked
 *
 * @author Denisa Cakoni
 *
 */
public class ModuleHub {

    /**
     * Calls the Storage team to load, save, or delete budget data.
     *
     *
     * @param action what we want Storage to do: "load", "save", or "delete"
     * @param username which user's data we're working with
     * @param year which year's budget data (like 2024, 2025, etc.)
     * @return true if it worked, false if something went wrong
     * @author Denisa Cakoni
     */
    public boolean callStorage(String action, String username, int year) {
        // send the request to Storage team's code
        //
        return false;
    }

    /**
     * Calls the Reports team to generate a financial summary report.
     *
     *
     * @param reportType "monthly", "annual"...
     * @param username whose budget we're analyzing
     * @param year: which year to make the report for
     * @author Denisa Cakoni
     */
    public String callReports(String reportType, String username, int year) {
        // ask Reports team to crunch the numbers
        return null;
    }

    /**
     * Calls the Prediction team to run "what-if" scenarios.
     *
     *
     * @param scenarioType what to calculate
     * @param username whose budget to analyze
     * @param year which year's data to use
     * @author Denisa Cakoni
     */
    public String callPrediction(String scenarioType, String username, int year) {
        // send to Prediction team for what-if calculations
        return null;
    }

    /**
     * Calls the Validation team to check if data is valid.
     *
     *
     * @param validationType what to check: "csv", "date", "amount", or "category"
     * @param dataToValidate the actual data that needs checking
     * @return true if data is good, false if there's a problem
     * @author Denisa Cakoni
     */
    public boolean callValidation(String validationType, String dataToValidate) {
        // let Validation team check if this data is legit

        return false;
    }

    /**
     * Calls the Accounts team for login, logout, password...
     *
     *
     * @param action "login", "logout", "changePassword", "deleteAccount"
     * @param username the account username
     * @return true if the operation worked, false if it failed
     * @author Denisa Cakoni
     */
    public boolean callAccounts(String action, String username) {
        // route to Accounts team for user account stuff
        return false;
    }

    /**
     * Asks Reports team to export a report to a CSV file.
     *
     * @param reportContent the report text to save
     * @param filename what to name the file
     * @return true if file was created successfully, false if it failed
     * @author Denisa Cakoni
     */
    public boolean exportReportToCSV(String reportContent, String filename) {

        return false;
    }

    /**
     * Gets the list of years that a user has budget data for.
     * Like if they uploaded 2023.csv and 2024.csv, this returns {2023, 2024}.
     *
     * @param username whose data to check
     * @return array of years that have data, or empty array if no data exists
     * @author Denisa Cakoni
     */
    public int[] getStoredYears(String username) {
        // TODO: Ask Storage team what years this user has saved
        return new int[0];
    }
}