package utilities.undoredo;

import model.entities.Student;
import model.entities.Team;
import model.exceptions.*;

public class SwapedOp implements Action
{
    Team team1 = null;
    String team1RemovedStudentId = "";
    Team team2 = null;
    String team2RemovedStudentId = "";

    public SwapedOp(Team team1, String team1RemovedStudentId, Team team2, String team2RemovedStudentId){
        this.team1 = team1;
        this.team1RemovedStudentId = team1RemovedStudentId;
        this.team2 = team2;
        this.team2RemovedStudentId = team2RemovedStudentId;
    }

    @Override
    public void execute() {

    }

    @Override
    public void undo() {

        System.out.println("Undoing Swapping");
        Student s1 = this.team1.getMembers().get( this.team2RemovedStudentId );
        Student s2 = this.team2.getMembers().get( this.team1RemovedStudentId );

        if(s1 == null || s2 == null){
            System.err.println("Cant perform undone since, one of the member has already been moved");
            return;
        }

        team1.removeMember(team1.getProjectRef(), s1);
        team2.removeMember(team2.getProjectRef(), s2);

        try {
            team1.addMember(team1.getProjectRef(), s2);
            team2.addMember(team2.getProjectRef(), s1);

        } catch (InvalidMemberException | RepeatedMemberException | StudentConflictException | ExcessMemberException | NoLeaderException | PersonalityImbalanceException e) {
            e.printStackTrace();
        }
    }

    /*
     * Above undo can be redone
     */
    public void redo(){

        System.out.println("Redoing Swapping");
        Student s1 = this.team1.getMembers().get( this.team1RemovedStudentId );
        Student s2 = this.team2.getMembers().get( this.team2RemovedStudentId );

        if(s1 == null || s2 == null){
            System.err.println("Cant perform undone since, one of the member has already been moved");
            return;
        }

        team1.removeMember(team1.getProjectRef(), s1);
        team2.removeMember(team2.getProjectRef(), s2);

        try {
            team1.addMember(team1.getProjectRef(), s2);
            team2.addMember(team2.getProjectRef(), s1);

        } catch (InvalidMemberException | RepeatedMemberException | StudentConflictException | ExcessMemberException | NoLeaderException | PersonalityImbalanceException e) {
            e.printStackTrace();
        }

    }
}
