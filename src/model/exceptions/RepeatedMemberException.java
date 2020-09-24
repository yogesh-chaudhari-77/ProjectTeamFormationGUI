package model.exceptions;

/**
 * Exception implementation
 * Thrown when an attempt is made to add a memeber who is already part of the team.
 */
public class RepeatedMemberException extends Exception {

    public RepeatedMemberException(String msg){
        super(msg);
        System.err.println(msg);
    }

    public RepeatedMemberException(){
        super("Repeated Member");
        System.err.println("Repeated member exception occured");
    }
}
