package model.exceptions;

public class StudentConflictException extends Exception {
    public StudentConflictException(String message){
        super(message);
        System.out.println(message);
    }
}
