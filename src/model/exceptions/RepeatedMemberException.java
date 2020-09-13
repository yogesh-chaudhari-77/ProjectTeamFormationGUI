package model.exceptions;

public class RepeatedMemberException extends Exception {
    public RepeatedMemberException(){
        super("Repeated Member");
        System.out.println("Repeated member exception occured");
    }
}
