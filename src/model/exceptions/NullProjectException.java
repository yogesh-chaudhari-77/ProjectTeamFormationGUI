package model.exceptions;

public class NullProjectException extends Exception {
	public NullProjectException() {
		System.out.println("Null project referenced in not allowed.");
	}
}
