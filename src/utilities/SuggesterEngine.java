package utilities;

import model.entities.Student;
import model.entities.Team;
import model.exceptions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Class implementing suggestions logic for project manager
 * Strategies :
 *          Skill Shortfall,
 *          Skill Competency
 */

public class SuggesterEngine extends Thread{

    // Copy of original teams
    private ArrayList<Team> teamsCopy = new ArrayList<Team>();

    // Suggestions by current run
    private ArrayList<String> suggestions = new ArrayList<String>();

    // Previous deviations values
    private double prevStdPref = 0.0;
    private double prevStdSkillComp = 0.0;
    private double prevStdSkillShortfall = 0.0;

    // Deviations values after suggestion implementation
    private double currStdPref = 0.0;
    private double currStdSkillComp = 0.0;
    private double currStdSkillShortfall = 0.0;

    private String currentStrategy = "skillcomp";

    public SuggesterEngine(HashMap<String, Team> clonedTeams) throws CloneNotSupportedException {

        ArrayList<Team> passedCopy = new ArrayList<>();
        for(Team t : clonedTeams.values()){
            Team ct = (Team) t.clone();

            passedCopy.add( ct );
        }

        this.makeCopy(passedCopy);
        this.suggestions = new ArrayList<String>();
    }

    public void makeCopy(ArrayList<Team> passedCopy){
        this.teamsCopy = (ArrayList<Team>) passedCopy.clone();
        this.storePrevValues();
    }


    // Stores the previous values of the standard dadivations
    public void storePrevValues(){

        for(int i = 0; i < this.teamsCopy.size(); i++){
            prevStdPref += teamsCopy.get(i).getPrctStudentReceivedPreference();
            prevStdSkillComp += teamsCopy.get(i).getAvgProjSkillComp();
            prevStdSkillShortfall += teamsCopy.get(i).getTotalSkillShortage();
        }
    }

    // Stores the current values of the standard deviations
    public void storeCurrValues(){

        for(int i = 0; i < this.teamsCopy.size(); i++){

            teamsCopy.get(i).updateStatistics();

            currStdPref += teamsCopy.get(i).getPrctStudentReceivedPreference();
            currStdSkillComp += teamsCopy.get(i).getAvgProjSkillComp();
            currStdSkillShortfall += teamsCopy.get(i).getTotalSkillShortage();
        }
    }

    /**
     *
     */
    public void run(){
        this.makeSuggestion();
    }


    /**
     * Actual implementation that makes the suggestions
     */
    public void makeSuggestion() {

        System.out.println("Making suggestions");
        calStudentGradeAvg();

        for(int i = 0; i < teamsCopy.size(); i++){

            System.out.println(currentStrategy);

            strategyDecision();

            Team team1 = this.teamsCopy.get(i);
            Team team2 = this.teamsCopy.get(this.teamsCopy.size() - 1);

            System.out.println("Best Team : "+team1.getTeamId()+" | Worst Team : "+team2.getTeamId());

            // If same team have been chosen, change the strategy
            if(team1 == team2){
                continue;
            }

            int maxGrades = -1;
            int minGrades = 9999;
            Student s1 = null;
            Student s2 = null;

            // Find Best Student from best team
            for(Student s : team1.getMembers().values()){
                if(s.getSumGrades() > maxGrades){
                    maxGrades = s.getSumGrades();
                    s1 = s;
                }
            }

            // Find Below Average Student from wrost team
            for(Student s : team2.getMembers().values()){
                if(s.getSumGrades() < minGrades){
                    minGrades = s.getSumGrades();
                    s2 = s;
                }
            }

            System.out.println("Best Student : "+s1.getId()+" Worst Student : "+s2.getId());

            // If one of them have Personality type A, then don't swap as it would lead to no leadership exception
            if(s1.getPersoanlity() == "A" || s2.getPersoanlity() == "A"){
                System.out.println("Student 1 : "+s1.getPersoanlity()+" Student 2 : "+s2.getPersoanlity());
                continue;
            }

            // Removing members
            team1.removeMember(team1.getProjectRef(), s1);
            team2.removeMember(team2.getProjectRef(), s2);

            try{
                // End of Swap
                team1.addMember(team1.getProjectRef(), s2);
                team2.addMember(team2.getProjectRef(), s1);

                team1.updateStatistics();
                team2.updateStatistics();

                // One possible Suggestion
                suggestions.add(s1.getId() + " swap with "+s2.getId());

                // Reverse the state so that next suggestion can still operate on same copy of team
                team1.removeMember(team1.getProjectRef(), s2);
                team2.removeMember(team2.getProjectRef(), s1);
                team1.addMember(team1.getProjectRef(), s1);
                team2.addMember(team2.getProjectRef(), s2);
                // Correction ends here

            }catch (PersonalityImbalanceException | InvalidMemberException | RepeatedMemberException | StudentConflictException | NoLeaderException | ExcessMemberException e) {

                team1.removeMember(team1.getProjectRef(), s2);
                team2.removeMember(team2.getProjectRef(), s1);
                try{
                    team1.addMember(team1.getProjectRef(), s1);
                    team2.addMember(team2.getProjectRef(), s2);
                }catch (PersonalityImbalanceException | InvalidMemberException | RepeatedMemberException | StudentConflictException | NoLeaderException | ExcessMemberException n) {
                    System.out.println("This line should never be printed");
                }
            }

            this.storeCurrValues();

            // If reached at the middle, change the strategy
            if( i > this.teamsCopy.size()/2){
                if(this.currentStrategy != "skillshortfall"){
                    this.suggestions.add("");
                    this.currentStrategy = "skillshortfall";
                    i = 0;
                    continue;
                }else{
                    break;
                }
            }
        }
    }


    /**
     * Calculates the students grade sum, based on this sum, system picks the brightest and poor students.
     */
    public void calStudentGradeAvg(){

        for(Team t : this.teamsCopy){
            for(Student s : t.getMembers().values()){
                int sumGrades = s.getGrades().values().stream().mapToInt(x -> x).sum();
                s.setSumGrades(sumGrades);
            }
        }

    }


    public void strategyDecision(){

        // Purposely kept here. to be able to change the strategy
        if(currentStrategy == "skillcomp"){
            this.teamsCopy.sort(Comparator.comparing( (t) -> t.getAvgProjSkillComp()));
        }else if(currentStrategy == "skillshortfall"){
            this.teamsCopy.sort(Comparator.comparing( (t) -> t.getTotalSkillShortage()));
        }
    }

    // Getter Setters Starts Here

    public ArrayList<Team> getTeamsCopy() {
        return teamsCopy;
    }

    public void setTeamsCopy(ArrayList<Team> teamsCopy) {
        this.teamsCopy = teamsCopy;
    }

    public ArrayList<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(ArrayList<String> suggestions) {
        this.suggestions = suggestions;
    }

    public double getPrevStdPref() {
        return prevStdPref;
    }

    public void setPrevStdPref(double prevStdPref) {
        this.prevStdPref = prevStdPref;
    }

    public double getPrevStdSkillComp() {
        return prevStdSkillComp;
    }

    public void setPrevStdSkillComp(double prevStdSkillComp) {
        this.prevStdSkillComp = prevStdSkillComp;
    }

    public double getPrevStdSkillShortfall() {
        return prevStdSkillShortfall;
    }

    public void setPrevStdSkillShortfall(double prevStdSkillShortfall) {
        this.prevStdSkillShortfall = prevStdSkillShortfall;
    }

    public double getCurrStdPref() {
        return currStdPref;
    }

    public void setCurrStdPref(double currStdPref) {
        this.currStdPref = currStdPref;
    }

    public double getCurrStdSkillComp() {
        return currStdSkillComp;
    }

    public void setCurrStdSkillComp(double currStdSkillComp) {
        this.currStdSkillComp = currStdSkillComp;
    }

    public double getCurrStdSkillShortfall() {
        return currStdSkillShortfall;
    }

    public void setCurrStdSkillShortfall(double currStdSkillShortfall) {
        this.currStdSkillShortfall = currStdSkillShortfall;
    }

    public String getCurrentStrategy() {
        return currentStrategy;
    }

    public void setCurrentStrategy(String currentStrategy) {
        this.currentStrategy = currentStrategy;
    }


    // Getter Setter Ends Here
}
