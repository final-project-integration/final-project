import java.util.ArrayList;
import java.util.List;

/**
 * ScenarioSimulator class for the Prediction Team module.
 * Provides functionality to create, modify, and compare multiple financial scenarios.
 * Scenarios represent "what-if" versions of a budget based on income and expense data.
 * This class manages multiple independent scenarios that can be modified and analyzed
 * to explore different financial outcomes and decision impacts.
 * @author Tanzina Sumona
 */
public class ScenarioSimulator {
    private final ArrayList<Scenario> scenarios = new ArrayList<>();
    private final ArrayList<String> scenarioNames = new ArrayList<>();
    private DataReader baseData;

    /**
     * Constructs a default ScenarioSimulator with no base data.
     * Creates an empty simulator. Must provide base data via constructor
     * or scenarios will not be created properly.
     * @author Tanzina Sumona
     */
    public ScenarioSimulator() {
        // Default constructor
    }

    /**
     * Constructs a ScenarioSimulator initialized with base financial data.
     * Initializes the simulator with data from a DataReader. This base data will be
     * used as the foundation for creating new scenarios.
     *
     * @param reader the DataReader containing loaded financial data from CSV
     * @author Tanzina Sumona
     */
    public ScenarioSimulator(DataReader reader) {
        this.baseData = reader;
    }
    /**
     * Creates a new financial scenario with the specified name.
     * Initializes a new scenario with data from the base DataReader. The scenario
     * is an independent copy and can be modified without affecting others.
     *
     * @param scenarioName the unique name for the new scenario
     * @return true if the scenario was created successfully; false if base data is null
     *         or a scenario with the same name already exists
     * @author Tanzina Sumona
     */
    public boolean createScenario(String scenarioName) {
        // Ensure base data is available
        if (baseData == null) {
            System.out.println("Error: No base data loaded for ScenarioSimulator.");
        return false;
        }
        // Check if scenario with the same name already exists
        if (scenarioExists(scenarioName)) {
            return false;
        }
        // Build new Scenario from base data
        Scenario newScenario = buildScenarioFromBaseData();
        addScenario(scenarioName, newScenario);

        return true;
    }
    /**
     * Checks if a scenario with the specified name already exists.
     *
     * @param scenarioName the name to check
     * @return true if a scenario with this name exists; false otherwise
     */
    private boolean scenarioExists(String scenarioName) {
        for (String name : scenarioNames) {
            if (name.equals(scenarioName)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Builds a new Scenario object using the base DataReader data.
     * Creates a Scenario by extracting income and expense data from the base DataReader,
     * classifying each entry, and storing in appropriate lists.
     *
     * @return a new Scenario initialized with base data
     */
    private Scenario buildScenarioFromBaseData() {

    // Get raw data from DataReader
    List<String> allCategories = baseData.getCategories();
    List<Integer> allAmounts = baseData.getAmounts();

    // Prepare separate lists for scenario
    ArrayList<String> incomeCategories = new ArrayList<>();
    ArrayList<String> expenseCategories = new ArrayList<>();
    ArrayList<Double> incomeValues = new ArrayList<>();
    ArrayList<Double> expenseValues = new ArrayList<>();

    // Split categories into income and expense
    for (int i = 0; i < allCategories.size(); i++) {
        String category = allCategories.get(i);
        double amount = allAmounts.get(i);

        if (DataReader.isIncomeCategory(category)) {
            incomeCategories.add(category);
            incomeValues.add(amount);
        } else if (DataReader.isExpenseCategory(category)) {
            expenseCategories.add(category);
            expenseValues.add(amount);
        }
    }
        // Build and return the Scenario
        return new Scenario(
                incomeCategories,
                expenseCategories,
                incomeValues,
                expenseValues
        );
    }
    /**
     * Adds a scenario and its name to the internal storage lists.
     *
     * @param scenarioName the name of the scenario to add
     * @param scenario the Scenario object to store
     */
    private void addScenario(String scenarioName, Scenario scenario) {
        scenarioNames.add(scenarioName);
        scenarios.add(scenario);
    }
    /**
     * Finds the index of a scenario by its name.
     *
     * @param scenarioName the name of the scenario to find
     * @return the index of the scenario if found; -1 if not found
     */
    private int findScenarioIndex(String scenarioName) {
        for (int i = 0; i < scenarioNames.size(); i++) {
            if (scenarioNames.get(i).equals(scenarioName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retrieves a Scenario object by its name.
     *
     * @param scenarioName the name of the scenario to retrieve
     * @return the Scenario object if found; null if not found
     */
    private Scenario getScenario(String scenarioName) {
        int idx = findScenarioIndex(scenarioName);
        if (idx == -1) 
            return null;
        return scenarios.get(idx);
    }
    /**
     * Applies a change to an expense category in the specified scenario.
     * Modifies the expense amount for a specific category in the given scenario.
     *
     * @param scenarioName the name of the scenario to modify
     * @param category the expense category to change
     * @param newAmount the new expense amount
     * @return true if the change was applied successfully; false if scenario or category not found
     * @author Tanzina Sumona
     */
    public boolean applyExpenseChange(String scenarioName, String category, double newAmount) {
        Scenario s = getScenario(scenarioName);
        if (s == null) 
            return false;

        ArrayList<String> expCats = s.getExpenseCategories();
        ArrayList<Double> expVals = s.getExpenseValues();

        for (int i = 0; i < expCats.size(); i++) {
            if (expCats.get(i).equals(category)) {
                expVals.set(i, newAmount);
                return true;
            }
        }
        return false; // category not found
    }
    /**
     * Applies a change to an income category in the specified scenario.
     * Modifies the income amount for a specific source in the given scenario.
     *      
     * @param scenarioName the name of the scenario to modify
     * @param category the income category to change
     * @param newAmount the new income amount
     * @return true if the change was applied successfully; false if scenario or category not found
     * @author Tanzina Sumona
     */
    public boolean applyIncomeChange(String scenarioName, String category, double newAmount) {
        Scenario s = getScenario(scenarioName);
        if (s == null) 
            return false;

        ArrayList<String> incCats = s.getIncomeCategories();
        ArrayList<Double> incVals = s.getIncomeValues();

        for (int i = 0; i < incCats.size(); i++) {
            if (incCats.get(i).equals(category)) {
                incVals.set(i, newAmount);
                return true;
            }
        }
        return false;
    }
    /**
     * Resets a scenario to its original state using base data.
     * Rebuilds the specified scenario from the base DataReader data, undoing any modifications.
     *
     * @param scenarioName the name of the scenario to reset
     * @return true if the scenario was successfully reset; false if scenario not found
     * @author Tanzina Sumona
     */
    public boolean resetScenarioToBase(String scenarioName) {
        Scenario baseScenario = buildScenarioFromBaseData();
        int idx = findScenarioIndex(scenarioName);
        if (idx == -1) return false;
            scenarios.set(idx, baseScenario);
        return true;
    }
    /**
     * Compares two financial scenarios and displays the results.
     * Calculates and displays income, expenses, and net totals for both scenarios,
     * showing the financial comparison between them.
     *
     * @param scenarioA the name of the first scenario to compare
     * @param scenarioB the name of the second scenario to compare
     * @author Tanzina Sumona
     */
    public void compareScenarios(String scenarioA, String scenarioB) {
        Scenario a = getScenario(scenarioA);
        Scenario b = getScenario(scenarioB);
        if (a == null || b == null) {
            System.out.println("One or both scenarios not found.");
            return;
        }

        double incomeA = a.getTotalIncome();
        double expenseA = a.getTotalExpenses();
        double netA = incomeA - expenseA;

        double incomeB = b.getTotalIncome();
        double expenseB = b.getTotalExpenses();
        double netB = incomeB - expenseB;

        System.out.println("--- Comparing '" + scenarioA + "' and '" + scenarioB + "' ---");
        System.out.println("Scenario " + scenarioA + ": income=" + incomeA +
                        ", expenses=" + expenseA + ", net=" + netA);
        System.out.println("Scenario " + scenarioB + ": income=" + incomeB +
                        ", expenses=" + expenseB + ", net=" + netB);
    }
      
}