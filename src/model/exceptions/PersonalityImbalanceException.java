package model.exceptions;

public class PersonalityImbalanceException extends Exception {

	public PersonalityImbalanceException(String msg) {
		System.err.println(msg);
	}
}
