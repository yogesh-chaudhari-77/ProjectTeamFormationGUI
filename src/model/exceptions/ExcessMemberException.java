package model.exceptions;

/**
 * Exception implementation
 * Thrown if an attempt to add excess member to team.
 * @author Yogeshwar G. Chaudhari
 */
public class ExcessMemberException extends Exception{

    public ExcessMemberException(String message){
        super(message);
        System.err.println(message);
    }

    public ExcessMemberException() {
        super("Team is already full. Please remove one student from the list and try again.");
        System.err.println("Team is already full. Please remove one student from the list and try again.");
    }
}
