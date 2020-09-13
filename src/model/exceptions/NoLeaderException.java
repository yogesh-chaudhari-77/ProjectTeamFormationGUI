package model.exceptions;

public class NoLeaderException extends Exception {
	
	public NoLeaderException(String msg) {
		super(msg);
		System.err.println(msg);
	}
}
