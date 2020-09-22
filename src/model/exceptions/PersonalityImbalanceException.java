package model.exceptions;

/**
 * Exception implementation
 * Thrown when a formed team does not contain balance personality types
 * @author Yogeshwar G. Chaudhari
 */
public class PersonalityImbalanceException extends Exception {

	public PersonalityImbalanceException(String msg) {
		super("Personalities in team are imbalanced. Your last addition will be reverted.");
		System.err.println(msg);
	}
}
