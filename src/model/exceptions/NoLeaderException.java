package model.exceptions;

/**
 * Exception implementation
 * thrown when a formed team does not contain atleast one memeber with leadership qualities.
 * @author Yogeshwar G. Chaudhari
 */
public class NoLeaderException extends Exception {
	
	public NoLeaderException(String msg) {
		super(msg);
		System.err.println(msg);
	}
}
