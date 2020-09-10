package utilities;

import globals.Globals;

import java.util.InputMismatchException;
import java.util.Scanner;

/*
 *  Singleton class returns only one reference of scannerUtil which is being used everywhere for accepting user input.
 *  This class implements methods for handling input
 */

public class ScannerUtil {

	private static ScannerUtil scannerInstance = null;
	private Scanner consoleIn = null;
	
	private ScannerUtil() {
	}

	
	/* Its there as part of standard implementation.
	 * Kept is static for code reduction 
	 */
	
	public static ScannerUtil createInstance() {
		if(scannerInstance == null) {
			scannerInstance = new ScannerUtil();
		}

		return scannerInstance;
	}


	// Initialize Scanner with system in but returns reference of ScannerUtil for method chaining 
	public static ScannerUtil consoleReader(){

		if (ScannerUtil.createInstance() != null) {
			if(ScannerUtil.scannerInstance.consoleIn == null) {
				scannerInstance.consoleIn = new Scanner(System.in);
			}

		}

		return ScannerUtil.scannerInstance;
	}
	
	
	/*
	 * Reads integer data until integer data is not supplied
	 * Re-prints original instruction message on failure
	 */
	
	public int readInt(String inputStr) {
		
		boolean success = false;
		int readVal = 0;
		
		do {
			try {
				System.out.print(inputStr);
				readVal = ScannerUtil.scannerInstance.consoleIn.nextInt();
				success = true;
			} catch (InputMismatchException | IllegalArgumentException ime) {
				System.err.println("Please enter numeric value.");
			}
			
			ScannerUtil.consoleReader().clearReader();
		}while(!success);
		
		
		return readVal;
	}
	
	// Reads double data and does not accept wrong data
	public double readDouble(String inputInstr) {
		
		boolean success = false;
		double readVal = 0;
		
		do {
			
			try {
				 System.out.print(inputInstr);
				 readVal = ScannerUtil.scannerInstance.consoleIn.nextDouble();
				 success = true;
			} catch (InputMismatchException ime) {
				System.err.println("Please enter numeric value. ");
			}
			
			ScannerUtil.consoleReader().clearReader();
			
		} while(!success);

		return readVal;
	}
	
	
	// Reads any String data but empty values are not allowed
	public String readString(String instrString) {
		
		String userInput = null;
		do {
			System.out.print(instrString);
			userInput = ScannerUtil.scannerInstance.consoleIn.nextLine();
			
			if(userInput == null || userInput == "" || userInput.isBlank() || userInput.isEmpty()) {
				System.err.print("Empty value is not allowed\nEnter Again : ");
			}
			
		}while(userInput == null || userInput == "" || userInput.isBlank() || userInput.isEmpty());
		
		return userInput.strip();
	}
	
	
	/*
	 * Reads any String and validates against given pattern
	 * User can supply either predefined patterns (Globals) or provide an custom in place pattern
	 * re-prints the original instruction message after failure
	 * 
	 * @param errMsg : error message that will be shown on error
	 */
	
	public String readString(String pattern, String inputInstr, String errMsg) {
	
		// Check if the developer is trying to use the predefined patterns
		String derivedPattern = predefinedPatterns(pattern);
		if(derivedPattern != null) {
			pattern = derivedPattern;
		}
		
		String userInput;
		boolean matched = false;
		do {
			System.out.print(inputInstr);
			userInput = ScannerUtil.scannerInstance.consoleIn.nextLine();
			matched = userInput.matches(pattern);
			if(!matched) {
				if(errMsg == null || errMsg.isBlank() || errMsg.isEmpty()) {
					System.err.println("Invalid Input");
				}else
					System.err.println(errMsg);
			}
		}while(!matched);
		
		return userInput;
	}
	
	
	/*
	 * List of common validation regex for name, email, phone, abn
	 */
	
	public String predefinedPatterns(String pattern) {
		
		String derivedPatt = null;
		switch(pattern) {
			case Globals.EMAIL_PATT_IDENT:
				derivedPatt = "^(.+)@(.+)$"; 
			break;
			
			case Globals.PHONE_PATT_IDENT:
				derivedPatt = "^\\d{10}$";
			break;
			
			case Globals.ABN_PATT_IDENT :
				derivedPatt = "^\\d{11}$";
			break;
			
			case Globals.URL_PATT_IDENT:
				derivedPatt = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
			break;
			
			case Globals.COMP_NAME_PATT_IDENT:
				derivedPatt = "(?i)(^[a-z])((?![ .,'-]$)[a-z .,'-]){0,24}$";
			break;
			
			case Globals.ADDR_PATT_IDENT:
				derivedPatt = "[A-Za-z0-9'//.//-//s//,]";
			break;
			
			default:
				derivedPatt = null;
		}
		
		return derivedPatt;
	}
		
		
	// Special method of reading only yes no type answers.
	public String readYesNo(String inputMessage) {
		String typedInput = "";
		boolean success = false;
		
		
		
		do {
			System.out.print(inputMessage);
			typedInput = ScannerUtil.scannerInstance.consoleIn.nextLine();
			
			if(typedInput.trim().equalsIgnoreCase("yes") || typedInput.trim().equalsIgnoreCase("y")) {
				success = true;
				typedInput = "Y";
			} else if (typedInput.trim().equalsIgnoreCase("no") || typedInput.trim().equalsIgnoreCase("n")){
				success = true;
				typedInput = "N";
			}else {
				System.out.println("-- Invalid Input --");
				success = false;
			}
			
		}while(!success);
		 
		return typedInput;
	}
	
	// Removes the newline character from the input stream
	public void clearReader() {
		ScannerUtil.scannerInstance.consoleIn.nextLine();
	}

	
	// Close connection after use. Now to be used with every request.
	public void closeReader() {

		if(this.consoleIn != null) {
			this.consoleIn.close();
		}
	}
	

}

