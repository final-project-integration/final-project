import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The CSVHandler class provides the methods to perform the low-level file I/O operations
 * on the given CSV files, converting between CSV format and {@link Transaction} objects.
 * @author Eddie Zhu
 * @version 11/4/2025
 */

public class CSVHandler {
	
	/**
	 * Default constructor for CSVHandler.
	 */
	public CSVHandler() {}

	/**
	 * Reads all the transactions from the specified CSV file.
	 * @param file the name of the CSV file that we're reading into
	 * @return an array list of {@link Transaction} objects loaded from the CSV file
	 * @throws IOException if the file can't be read
	 * @author Eddie Zhu
	 */
	public ArrayList<Transaction> readCSV(String file) throws IOException {
		ArrayList<Transaction> transactions = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			
			while((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				
				if (parts.length != 3) {
					throw new IOException("Invalid line: " + line);
				}
				
				String date = parts[0];
				String category = parts[1];
				double amount = Double.parseDouble(parts[2]);
					
				transactions.add(new Transaction(date, category, amount));
			}
		}
		return transactions;
	}
	
	/**
	 * Writes a list of transactions to the specified CSV file.
	 * @param file the name of the CSV file that we're writing to
	 * @param transactions an array list of {@link Transaction} objects to save
	 * @throws IOException if the file can't be written to
	 * @author Eddie Zhu
	 */
	public void writeCSV(String file, ArrayList<Transaction> transactions) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			for (Transaction t : transactions) {
				bw.write(t.getDate() + "," + t.getCategory() + "," + t.getAmount());
				bw.newLine();
			}
		}
	}
	
	/**
	 * Displays the first couple of lines of the CSV file to allow the user to preview the data.
	 * @param file the name of the CSV file that we're previewing
	 * @param linesToPreview the number of lines that you would like to preview (excluding header)
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
	 * Overwrites the file given with a new, complete list of transactions.
	 * @param file the name of the CSV file that we're overwriting
	 * @param transactions an array list of {@link Transaction} objects to completely replace the file contents
	 * @throws IOException if the overwriting can't be done
	 * @author Eddie Zhu
	 */
	public void overwriteCSV(String file, ArrayList<Transaction> transactions) throws IOException {
		writeCSV(file, transactions);
	}
}