package model.exceptions;

public class NullProjectException extends Exception {
	public NullProjectException() {
		super("Null project referenced in not allowed. Please try again.");
		System.out.println("Null project referenced in not allowed.");
	}
}
