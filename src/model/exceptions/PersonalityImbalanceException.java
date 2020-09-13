package model.exceptions;

public class PersonalityImbalanceException extends Exception {

	public PersonalityImbalanceException(String msg) {
		super("Personalities in team are imbalanced. Your last addition will be reverted.");
		System.err.println(msg);
	}
}
