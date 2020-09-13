package model.exceptions;
import model.entities.Student;

public class InvalidMemberException extends Exception {
	
	public InvalidMemberException(Student s) {
		super(s.getId()+" is currently associted with "+s.getCurrProjAssoc());
		System.out.println(s.getId()+" is currently associted with "+s.getCurrProjAssoc());
	}
}
