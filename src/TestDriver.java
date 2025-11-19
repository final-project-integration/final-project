public class TestDriver {
    public static void main(String[] args) {

        // Create DataReader
        DataReader dr = new DataReader();

        // Read CSV data
        dr.readData();

        System.out.println(dr.createSummaryReport());  // uses calculateTotalIncome/Expenses

      /*  // 2. Create SurplusOptimizer using the same data
        SurplusOptimizer so = new SurplusOptimizer(dr);

        // 3. Check initial surplus + totals
        System.out.println("\n=== Initial Surplus Tracker ===");
        System.out.println(so.surplusTracker());

        // 4. Get a suggestion on which category to decrease
        System.out.println("=== Surplus Suggestion ===");
        System.out.println(so.surplusSuggestion());

        // 5. Test decreasing a specific expense category (example: Entertainment)
        System.out.println("\n=== After decreasing Entertainment by 10% ===");
        so.decreaseExpense("Entertainment");
        System.out.println(so.surplusTracker());

        // 6. Check savings based on 'Investments' income
        System.out.println("=== Savings Amount (Investments) ===");
        System.out.println("Savings: $" + so.savingsAmount());

        // 7. Create DeficitSolver using the same DataReader data
        System.out.println("\n=== DeficitSolver Tests ===");
        DeficitSolver ds = new DeficitSolver(dr);

        // 8. Show initial deficit and summary
        double initialDeficit = ds.calculateDeficit();
        System.out.println("Initial deficit: $" + initialDeficit);

        System.out.println("\n--- Deficit Summary ---");
        System.out.println(ds.generateSummary());

        // 9. Apply adjustments to reduce the deficit
        boolean applied = ds.applyAdjustments();
        System.out.println("Applied adjustments? " + applied);

        double deficitAfterAdjust = ds.calculateDeficit();
        System.out.println("Deficit after applying adjustments: $" + deficitAfterAdjust);

        System.out.println("\n--- Deficit Summary After Adjustments ---");
        System.out.println(ds.generateSummary());

        // 10. Undo the last set of adjustments
        boolean undone = ds.undoAdjustment();
        System.out.println("Undo adjustments? " + undone);

        double deficitAfterUndo = ds.calculateDeficit();
        System.out.println("Deficit after undoing adjustments: $" + deficitAfterUndo);*/
                // 11. ScenarioSimulator Tests
        System.out.println("\n=== ScenarioSimulator Tests ===");

        // Create ScenarioSimulator using the same base data
        ScenarioSimulator sim = new ScenarioSimulator(dr);

        // 11.1 Create two scenarios based on the same base data
        boolean s1 = sim.createScenario("BaseScenario");
        boolean s2 = sim.createScenario("CutEntertainment");

        System.out.println("Created BaseScenario? " + s1);
        System.out.println("Created CutEntertainment? " + s2);

        // 11.2 Apply an expense change in one scenario (example: Entertainment)
        // NOTE: Make sure "Entertainment" exists in your CSV as an expense category.
        boolean changedExp = sim.applyExpenseChange("CutEntertainment", "Entertainment", 50.0);
        System.out.println("Changed Entertainment in CutEntertainment? " + changedExp);

        // 11.3 (Optional) Apply an income change in one scenario
        // Replace "Job" with an actual income category name from your CSV.
        boolean changedInc = sim.applyIncomeChange("CutEntertainment", "Compensation", 2500.0);
        System.out.println("Changed Compensation income in CutEntertainment? " + changedInc);

        // 11.4 Compare the two scenarios
        System.out.println("\n--- Comparing BaseScenario and CutEntertainment ---");
        sim.compareScenarios("BaseScenario", "CutEntertainment");

    }
}

