package utilities;

import java.io.*;

/*
 * Singleton class implementing all file reading, writting, appending  or clear content functionality as helper methods
 * Implements using BfferedWritter(FileWritter(File)));
 */
public class FileHandlingHelper {
	
	private static FileHandlingHelper fileHandler = null;
	
	File currentFile;
	FileReader fileReader;
	BufferedReader bufferReader;
	FileWriter fileAppender;
	BufferedWriter bufferAppender;
	
	// Support for writting serialised objects
	FileOutputStream fileOS; 
    ObjectOutputStream objectOS;
    FileInputStream fileIS;
    ObjectInputStream objectIS;
    
	// Private constructor that can be called by the public method.
	private FileHandlingHelper() {
		
	}

	
	// Initialised new FileHandlingHelper instance or returns exisiting one if present.
	public static FileHandlingHelper init() {
		if(fileHandler == null) {
			fileHandler = new FileHandlingHelper();
		}
		return fileHandler;
	}
	
	
	// Sets the file that needs to be used for performing read, write, append operations
	public boolean setFile(String fileName, String mode) {
		
		try {
			
			if(mode.contentEquals("read")) {
				
				fileReader = new FileReader(fileName);
				bufferReader = new BufferedReader(fileReader);
				
			}else if(mode.contentEquals("write")) {
				
				fileAppender = new FileWriter(fileName);
				bufferAppender = new BufferedWriter(fileAppender);
				
			}else if(mode.contentEquals("append")) {
				
				//currentFile = new File(fileName);
				fileAppender = new FileWriter(fileName, true);
				bufferAppender = new BufferedWriter(fileAppender);
			}
		
		}catch (FileNotFoundException fnf) {
			System.err.println("Could not locate "+fileName+". Please check the filename. Try providing absolute path of the file.");
			return false;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}catch (Exception e) {
			System.err.println("Unexpected error has occured. Please check system logs for more info");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	// Sets the file that needs to be used for performing read, write, append operations
	public boolean setBinaryFile(String fileName, String mode) {
		
		try {
			
			if(mode.contentEquals("read")) {
				
				fileIS = new FileInputStream(fileName);
				if (fileHandler.fileIS.available() > 0) {
					objectIS = new ObjectInputStream(fileIS);
				}else {
					return false;
				}
				
			}else if(mode.contentEquals("write")) {
				
				fileOS = new FileOutputStream(fileName);
				objectOS = new ObjectOutputStream(fileOS);
				
			}else if(mode.contentEquals("append")) {
				
				fileOS = new FileOutputStream(fileName, true);
				objectOS = new ObjectOutputStream(fileOS);
				
			}
		
		}catch (FileNotFoundException fnf) {
			System.err.println("Could not locate "+fileName+". Please check the filename. Try providing absolute path of the file.");
			return false;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
			
		}catch (Exception e) {
			
			System.err.println("Unexpected error has occured. Please check system logs for more info");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	// Writes / Appends the supplied content to end of the currently set file name
	public boolean writeOp(String content){
		
		try {
			
			bufferAppender.write(content);
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	// Reads file contents line by line
	public String readOp(){

		String line = null;
		try {
			line = this.bufferReader.readLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}		
		
		return line;
	}
	
	
	// Writting method for serialization
	public boolean writeOp(Object content){

		try {

			objectOS.writeObject(content);
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
		
	// Returns the currently set file for reading and writting
	public String getCurrentFilePath() {
		return this.currentFile.getAbsolutePath();
	}
	
	
	// Clears the entire content of supplied file.
	public boolean clearFileContents(String filePath) {

		// Clear the existing contents of the file
		try {
			fileHandler.setFile(filePath, "write");
			fileHandler.writeOp("");
			fileHandler.destroy();
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	// Closes all the IO resources
	public void destroy() {
		
		try {
			if(bufferAppender != null) {
				bufferAppender.close();
				
				if(fileAppender != null) {
					fileAppender.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    // Milestone 2
	    // Closing the fileOutputStream and Object Output Stream
		try {
			if(objectOS != null) {
				objectOS.close();
				
				if(fileOS != null) {
					fileOS.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		// Milestone 2
		// Closing the file input stream and object input stream object
		try {
			if(objectIS != null) {
				objectIS.close();

				if(fileIS != null) {
					fileIS.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// Writes the serialize objects
	public void serializeObj(Object obj) throws IOException {
		this.objectOS.writeObject(obj);
	}
	
	
	// Reads the serialised object and returns the parent Object type
	public Object readSerializedObj() throws ClassNotFoundException, IOException {
		return this.objectIS.readObject();
	}
	
} // End of class FileHandlingHelper
