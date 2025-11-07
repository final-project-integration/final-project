package storage;

import java.util.ArrayList;

/**
 * The CSVHandler class provides the methods to perform the file I/O operations on the given CSV files
 * 
 * @author Eddie Zhu
 * @version 11/4/2025
 */

public class CSVHandler {
	
	/**
	 * Reads all the transactions from the CSV file
	 * 
	 * @param file the name of the CSV file that we're reading into
	 * @return an array list of Transaction objects loaded from the CSV file
	 * @author Eddie Zhu
	 */
	
	public ArrayList<Transaction> readCSV(String file) {
		return null;
	}
	
	/**
	 * Writes a list of transactions to the CSV file
	 * 
	 * @param file the name of the CSV file that we're writing to
	 * @param transactions an array list of Transaction objects to save
	 * @author Eddie Zhu
	 */
	
	
	public void writeCSV(String file, ArrayList<Transaction> transactions) {
		
	}
	
	/**
	 * Displays the first couple of lines of the CSV file to preview
	 * 
	 * @param file the name of the CSV file that we're previewing
	 * @param linesToPreview the number of lines that you would like to preview
	 * @author Eddie Zhu
	 */
	
	public void previewCSV(String file, int linesToPreview) {
		
	}
	
	/**
	 * Overwrites the file given with a new list of transactions
	 * 
	 * @param file the name of the CSV file that we're overwriting
	 * @param transaction an array list of Transaction objects 
	 * @author Eddie Zhu
	 */
	
	public void overwriteCSV(String file, ArrayList<Transaction> transaction) {
		
	}
	
}

