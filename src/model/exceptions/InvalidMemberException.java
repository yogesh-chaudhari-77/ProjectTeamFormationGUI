package model.exceptions;
import model.entities.Student;

public class InvalidMemberException extends Exception {
	
	public InvalidMemberException(Student s) {
		System.out.println(s.getId()+" is currently associted with "+s.getCurrProjAssoc());
	}
}
