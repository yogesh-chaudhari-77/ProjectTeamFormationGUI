package model.exceptions;

/**
 * Exception implementation
 * Thrown when student with specified ID cant be found.
 * In other words, there is no student with specified id returning null reference to student
 */
public class StudentNotFoundException extends Exception {
	
	public StudentNotFoundException(String errMsg) {
	    super(errMsg);
		System.err.println(errMsg);
    }
}
