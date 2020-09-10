package model.exceptions;

public class NoLeaderException extends Exception {
	
	public NoLeaderException(String msg) {
		System.err.println(msg);
	}
}
