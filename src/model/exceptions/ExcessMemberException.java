package model.exceptions;

public class ExcessMemberException extends Exception{
    public ExcessMemberException(String message){
        System.out.println(message);
    }

    public ExcessMemberException(){
        System.out.println("Team is already full. Please remove one student from the list and try again.");
    }
}
