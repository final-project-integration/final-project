
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
     * Default constructor for ModuleHub.
     */
    public ModuleHub() {}

    // fields for each team's modules | placeholders, not initialized 
    private Object accountsModule;
    private Object storageModule;
    private Object reportsModule;
    private Object predictionModule;
    private Object validationModule;

    /**
     * Calls the Storage team to load, save, or delete budget data.
     *
     *
     * @param action what we want Storage to do
     * @param username which user's data we're working with
     * @param year which year's budget data 
     *  @return true if successful, false otherwise
     * @author Denisa Cakoni
     */
    public boolean callStorage(String action, String username, int year) {
   
      
    }
    /**
     * calls the Reports team to generate a financial  report.
     *
     *
     * @param reportType "monthly", "annual" etc.
     * @param username whose report to generate
     * @return formatted report as a String
     * @author Denisa Cakoni
     */
    public String callReports(String reportType, String username) {
        
    }
    

    /**
     * calls the Prediction module to run "what-if" scenarios.
     *
     *
     * @param scenarioType what to calculate
     * @param username : whose data to use
     * @param year     : which year's data to use
     * @return prediction result text
     * @author Denisa Cakoni
     */
    public String callPrediction(String scenarioType, String username, int year) {
  
    }

    /**
     * Calls the Validation team to check if data is valid.
     *
     *
     * @param validationType : type of validation to perform
     * @param dataToValidate actual input data
     * @return true if valid, false otherwise
     * @author Denisa Cakoni
     */
    public boolean callValidation(String validationType, String dataToValidate) {
       
    }

    /**
     * Calls the Accounts module for user account actions such as login, logout, password
     *
     *
     * @param action "login", "logout", "changePassword", "deleteAccount"
     * @param username the account username
     *  @return true if successful, false otherwise
     * @author Denisa Cakoni
     */
    public boolean callAccounts(String action, String username) {
        
    }

    
}
