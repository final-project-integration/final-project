import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The CSVHandler class provides the methods to perform the low-level file I/O operations
 * on the given CSV files, converting between CSV format and {@link Transaction} objects.
 * 
 * @author Eddie Zhu
 * @version 2025-12-03
 */

public class CSVHandler {
	
	/**
	 * Creates a new CSVHandler instance, used for low-level CSV file operations.
	 */
	public CSVHandler() {}

	/**
	 * Reads all the transactions from the specified CSV file and converts them into {@link Transaction} objects.
	 * 
	 * This method will skip header rows if the first row
	 * contains the column names (Date, Category, Amount). 
	 * 
	 * @param file the name of the CSV file that we're reading into
	 * @return an array list of {@link Transaction} objects loaded from the CSV file
	 * @throws IOException if the file can't be read
	 * @throws NumberFormatException if the amount can't be parsed as a number
	 * @author Eddie Zhu
	 */
	public ArrayList<Transaction> readCSV(String file) throws IOException {
		ArrayList<Transaction> transactions = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			
			if (line != null && isHeaderLine(line)) {
				line = br.readLine();
			}
			
			while (line != null) {
				if (!line.trim().isEmpty()) {
					String[] parts = line.split(",");
				
					if (parts.length != 3) {
						System.out.println("Skipping invalid row: " + line);
						line = br.readLine();
						continue;
					}	
				
					String date = parts[0].trim();
					String category = parts[1].trim();
					String amountStr = parts[2].trim();
					
					try {
						int dollars = Integer.parseInt(amountStr);
					    double amount = dollars;  // Exact storage
					    transactions.add(new Transaction(date, category, amount));
					} catch (NumberFormatException e) {
						System.out.println("Skipping row with invalid amount: " + line);
					}
				}
				line = br.readLine();
			}
		}
		return transactions;
	}
	
	/**
	 * Writes a list of transactions to the specified CSV file.
	 * 
	 * Will overwrite any existing file and a header row
	 * (Date, Category, Amount) is always the first line
	 * 
	 * @param file the name of the CSV file that we're writing to
	 * @param transactions an array list of {@link Transaction} objects to save
	 * @throws IOException if the file can't be written to
	 * @author Eddie Zhu
	 */
	public void writeCSV(String file, ArrayList<Transaction> transactions) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write("Date,Category,Amount");
			bw.newLine();
			
			for (Transaction t : transactions) {
				long amountLong = (long) t.getAmount();
				bw.write(t.getDate() + "," + t.getCategory() + "," + amountLong);
				bw.newLine();  // ← ADDED MISSING NEWLINE
			}
		}
	}
	
	/**
	 * Displays the first couple of lines, including the header, of the specified CSV file.
	 * 
	 * @param file the name of the CSV file that we're previewing
	 * @param linesToPreview the number of lines that you would like to preview (including header); must be at least 1
	 * @return a list of the lines that want to be previewed
	 * @throws IOException if the file can't be read
	 * @author Eddie Zhu
	 */
	public ArrayList<String> previewCSV(String file, int linesToPreview) throws IOException {
		ArrayList<String> preview = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			int count = 0;
			
			while ((line = br.readLine()) != null && count < linesToPreview) {
				preview.add(line);
				count++;
			}
		}
		return preview;
	}
	
	/**
	 * Replaces the content of the specified CSV file with the given list of {@link Transaction} objects.
	 * 
	 * @param file the name of the CSV file that we're overwriting
	 * @param transactions an array list of {@link Transaction} objects to completely replace the file contents
	 * @throws IOException if the overwriting can't be done
	 * @author Eddie Zhu
	 */
	public void overwriteCSV(String file, ArrayList<Transaction> transactions) throws IOException {
		writeCSV(file, transactions);
	}
	
	/**
	 * Determines whether the given CSV line is a header row.
	 * 
	 * A line is considered a header row if it contains exactly 3 (case insensitive)
	 * values which are "date", "category", and "amount".
	 * 
	 * @param line the line of a CSV file to check
	 * @return true if the line being checked is a header and false otherwise
	 * @author Eddie Zhu
	 */
	private boolean isHeaderLine(String line) {
		if (line == null) {
			return false;
		}
		
		String[] parts = line.split(",");
		if (parts.length != 3) {
			return false;
		}
		
		String date = parts[0].trim().toLowerCase();
		String category = parts[1].trim().toLowerCase();
		String amount = parts[2].trim().toLowerCase();  // ← FIXED: DECLARED 'amount'
		
		return date.equals("date") && category.equals("category") && amount.equals("amount");
	}
}
