package storage;

import java.util.ArrayList;

/**
 * The CSVHandler class provides the methods to perform the low-level file I/O operations
 * on the given CSV files, converting between CSV format and {@link Transaction} objects.
 * * @author Eddie Zhu
 * @version 11/4/2025
 */

public class CSVHandler {
	
	/**
	 * Default constructor for CSVHandler.
	 */
	public CSVHandler() {
	}

	/**
	 * Reads all the transactions from the specified CSV file.
	 * * @param file the name of the CSV file that we're reading into
	 * @return an array list of {@link Transaction} objects loaded from the CSV file
	 * @author Eddie Zhu
	 */
	
	public ArrayList<Transaction> readCSV(String file) {
		return null;
	}
	
	/**
	 * Writes a list of transactions to the specified CSV file.
	 * * @param file the name of the CSV file that we're writing to
	 * @param transactions an array list of {@link Transaction} objects to save
	 * @author Eddie Zhu
	 */
	
	
	public void writeCSV(String file, ArrayList<Transaction> transactions) {
		
	}
	
	/**
	 * Displays the first couple of lines of the CSV file to allow the user to preview the data.
	 * * @param file the name of the CSV file that we're previewing
	 * @param linesToPreview the number of lines that you would like to preview (excluding header)
	 * @author Eddie Zhu
	 */
	
	public void previewCSV(String file, int linesToPreview) {
		
	}
	
	/**
	 * Overwrites the file given with a new, complete list of transactions.
	 * * @param file the name of the CSV file that we're overwriting
	 * @param transactions an array list of {@link Transaction} objects to completely replace the file contents
	 * @author Eddie Zhu
	 */
	
	public void overwriteCSV(String file, ArrayList<Transaction> transactions) {
		
	}
	
}
