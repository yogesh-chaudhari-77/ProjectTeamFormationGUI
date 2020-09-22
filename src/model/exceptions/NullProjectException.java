package model.exceptions;

/**
 * Exception implementation
 * Thrown when a invalid project id is supplied
 * In other words, could not find a project with supplied project id
 * @author Yogeshwar G. Chaudhari
 */
public class NullProjectException extends Exception {

	public NullProjectException(String msg) {
		super(msg);
		System.err.println(msg);
	}

	public NullProjectException() {
		super("Null project referenced in not allowed. Please try again.");
		System.err.println("Null project referenced in not allowed.");
	}
}
