package model.exceptions;

/**
 * Exception implementation
 * Thrown when a newly added student has conflicts with already present memebers
 * or
 * One of the already present members has conflicts with the newly added member
 */

public class StudentConflictException extends Exception {

    public StudentConflictException(String message){
        super(message);
        System.err.println(message);
    }
}
