import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Manages loading data and orchestrating the generation and presentation of
 * report artifacts Responsibilities include: Loading income/expense data for a
 * given year Producing monthly, category, and yearly summaries Computing
 * overall balance figures Exporting summaries to CSV and displaying reports on
 * screen
 * 
 * @author Angelo Samir Alvarez
 * @author Furkan Bilgi
 * @author Chukwuemeka Okwuka
 * @author Omar Piron
 */
public class ReportManager {
    
    private ArrayList<FinancialRecord> loadedRecords;
    
    public ReportManager() {
        this.loadedRecords = new ArrayList<>();
    }

    /**
     * Loads all income and expense records for the specified year into memory
     * so that summaries can be produced without additional I/O.
     *
     * @param year the four-digit year to load (e.g., 2025)
     */
    public void loadYearlyData(int year) {
        this.loadedRecords.clear();
        
        //Replace with actual data loading from Income/Expense modules
        System.out.println("Loaded data for year: " + year);
    }
    
    /**
     * Setter method to inject financial records (for integration with other modules)
     */
    public void setFinancialRecords(ArrayList<FinancialRecord> records) {
        this.loadedRecords = records;
    }

    /**
     * Aggregates totals by month for the previously loaded year.
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return an immutable list of strings
     */
    public ArrayList<String> generateMonthlySummary(int year) {
        ArrayList<String> summary = new ArrayList<>();
        
        if (loadedRecords.size() == 0) {
            return summary;
        }
        
        // Arrays to store for each months Income and Expenses (index 0 is January, index 11 is December)
        
        double[] monthlyIncome = new double[12];
        double[] monthlyExpenses = new double[12];
        
        // Aggregate data by month
        for (int i = 0; i < loadedRecords.size(); i++) {
            FinancialRecord record = loadedRecords.get(i);
            
            if(record.getYear() != year) {
            	continue;
            }
            
            int month = record.getMonth();
            double amount = Double.parseDouble(record.getAmount());
            
            if (record.isIncome()) {
            	monthlyIncome[month] += amount;
            } else {
            	monthlyExpenses[month] += amount;
            }
        }
        
        // Format results
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                               "July", "August", "September", "October", "November", "December"};
        
        for (int month = 0; month <= 11; month++) {
            double income = monthlyIncome[month];
            double expenses = monthlyExpenses[month];
            double balance = income - expenses;
            
            String line = String.format("%s: Income=$%.2f, Expenses=$%.2f, Balance=$%.2f",
                    monthNames[month], income, expenses, balance);
            summary.add(line);
        }
        
        return summary;
    }

    /**
     * Aggregates totals by category label, e.g. "Rent", "Groceries".
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return an immutable list of strings
     */
    public ArrayList<String> generateCategorySummary(int year) {
        ArrayList<String> summary = new ArrayList<>();
        
        if (loadedRecords.size() == 0) {
            return summary;
        }
        
        // Lists to track categories and their totals
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<Double> totals = new ArrayList<>();
        
        // Aggregate by category
        for (int i = 0; i < loadedRecords.size(); i++) {
            FinancialRecord record = loadedRecords.get(i);
           
            //Only process record from specified year
            if (record.getYear() != year) {
                continue;
            }
            
            
            String category = record.getCategory();
            double amount = Double.parseDouble(record.getAmount());
            
            // Find if category already exists
            int index = -1;
            for (int j = 0; j < categories.size(); j++) {
                if (categories.get(j).equals(category)) {
                    index = j;
                    break;
                }
            }
            
            if (index == -1) {
                categories.add(category);
                totals.add(amount);
            } else {
                totals.set(index, totals.get(index) + amount);
            }
        }
        
        // Format results (no sorting for simplicity)
        for (int i = 0; i < categories.size(); i++) {
            String line = String.format("%s: $%.2f", categories.get(i), totals.get(i));
            summary.add(line);
        }
        
        return summary;
    }

    /**
     * Produces year-wide totals for income, expenses, and net balance.
     *
     * @param year the four-digit year corresponding to the loaded dataset
     * @return a YearlySummary struct containing key totals as strings
     */
    public YearlySummary generateYearlySummary(int year) {
        if (loadedRecords.size() == 0) {
            return new YearlySummary("0.00", "0.00", "0.00");
        }
        
        double totalIncome = 0.0;
        double totalExpenses = 0.0;
        
        for (int i = 0; i < loadedRecords.size(); i++) {
            FinancialRecord record = loadedRecords.get(i);
            
          //Only process record from specified year
            if (record.getYear() != year) {
                continue;
            }
            
            
            double amount = Double.parseDouble(record.getAmount());
            
            if (record.isIncome()) {
                totalIncome += amount;
            } else {
                totalExpenses += amount;
            }
        }
        
        double netBalance = totalIncome - totalExpenses;
        
        return new YearlySummary(
                String.format("%.2f", totalIncome),
                String.format("%.2f", totalExpenses),
                String.format("%.2f", netBalance)
        );
    }

    /**
     * Calculates the overall balance for the specified year (totalIncome - totalExpenses).
     * @param year the four-digit year corresponding to the loaded dataset
     * @return net balance for the year
     */
    public String calculateBalance(int year) {
        YearlySummary summary = generateYearlySummary(year);
        return summary.getNetBalance();
    }

    /**
     * Exports the active summaries to a CSV file at the provided path.
     * @param outputFile target path for the CSV file, parent directories should exist
     */
    public void exportReportToCSV(File outputFile) {
        try {
            FileWriter writer = new FileWriter(outputFile);
            
            // Get all summaries
            int year = 2025; // You may want to pass this or track it
            YearlySummary yearly = generateYearlySummary(year);
            ArrayList<String> monthly = generateMonthlySummary(year);
            ArrayList<String> category = generateCategorySummary(year);
            
            // Write header
            writer.write("Financial Report\n\n");
            
            // Write Yearly Summary
            writer.write("YEARLY SUMMARY\n");
            writer.write("Total Income," + yearly.getTotalIncome() + "\n");
            writer.write("Total Expenses," + yearly.getTotalExpenses() + "\n");
            writer.write("Net Balance," + yearly.getNetBalance() + "\n\n");
            
            // Write Monthly Summary
            writer.write("MONTHLY SUMMARY\n");
            writer.write("Month,Income,Expenses,Balance\n");
            for (int i = 0; i < monthly.size(); i++) {
                String line = monthly.get(i);
                String csvLine = line.replace(": Income=$", ",")
                                    .replace(", Expenses=$", ",")
                                    .replace(", Balance=$", ",");
                writer.write(csvLine + "\n");
            }
            writer.write("\n");
            
            // Write Category Summary
            writer.write("CATEGORY SUMMARY\n");
            writer.write("Category,Total\n");
            for (int i = 0; i < category.size(); i++) {
                String line = category.get(i);
                String csvLine = line.replace(": $", ",");
                writer.write(csvLine + "\n");
            }
            
            writer.close();
            System.out.println("Report exported to: " + outputFile.getAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
        }
    }

    /**
     * Renders the most recent summaries to the console.
     * Generate summaries prior to calling.
     */
    public void displayReportOnScreen() {
        // Get all summaries
        int year = 2025; // You may want to pass this or track it
        YearlySummary yearly = generateYearlySummary(year);
        ArrayList<String> monthly = generateMonthlySummary(year);
        ArrayList<String> category = generateCategorySummary(year);
        
        System.out.println("\n============================================================");
        System.out.println("                    FINANCIAL REPORT");
        System.out.println("============================================================");
        
        // Display Yearly Summary
        System.out.println("\nYEARLY SUMMARY");
        System.out.println("Total Income:    $" + yearly.getTotalIncome());
        System.out.println("Total Expenses:  $" + yearly.getTotalExpenses());
        System.out.println("Net Balance:     $" + yearly.getNetBalance());
        
        // Display Monthly Summary
        System.out.println("\nMONTHLY SUMMARY");
        for (int i = 0; i < monthly.size(); i++) {
            System.out.println(monthly.get(i));
        }
        
        // Display Category Summary
        System.out.println("\nCATEGORY SUMMARY");
        for (int i = 0; i < category.size(); i++) {
            System.out.println(category.get(i));
        }
        
        System.out.println("\n============================================================\n");
    }

    /**
     * Immutable value object representing yearly totals.
     *
     * @param totalIncome  sum of all income entries for the year
     * @param totalExpenses sum of all expense entries for the year
     * @param netBalance   totalIncome - totalExpenses
     */
    public static final class YearlySummary {
        private final String totalIncome;
        private final String totalExpenses;
        private final String netBalance;

        public YearlySummary(String totalIncome, String totalExpenses, String netBalance) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.netBalance = netBalance;
        }
        
        public String getTotalIncome() {
            return totalIncome;
        }

        public String getTotalExpenses() {
            return totalExpenses;
        }

        public String getNetBalance() {
            return netBalance;
        }
    }
    
    /**
     * Inner class representing a financial record.
     * This should match the structure from Income/Expense modules.
     */
    public static class FinancialRecord {
        private final String amount;
        private final String category;
        private final int month;
        private final int year;
        private final boolean isIncome;
        
        public FinancialRecord(String amount, String category, int month, int year, boolean isIncome) {
            this.amount = amount;
            this.category = category;
            this.month = month;
            this.year = year;
            this.isIncome = isIncome;
        }
        
        public String getAmount() { return amount; }
        public String getCategory() { return category; }
        public int getMonth() { return month; }
        public int getYear() {return year;}
        public boolean isIncome() { return isIncome; }
    }
}
