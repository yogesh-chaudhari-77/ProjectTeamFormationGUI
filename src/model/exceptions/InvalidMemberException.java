package model.exceptions;
import model.entities.Student;

/**
 * Exception implementation
 * Thrown when an attempt to add already assigned student is made
 * @author Yogeshwar G. Chaudhari
 */
public class InvalidMemberException extends Exception {

	// 22-09-2020
	public InvalidMemberException(String message){
		super(message);
		System.err.println(message);
	}

	public InvalidMemberException(Student s) {
		super(s.getId()+" is currently associted with "+s.getCurrProjAssoc());
		System.err.println(s.getId()+" is currently associted with "+s.getCurrProjAssoc());
	}
}
