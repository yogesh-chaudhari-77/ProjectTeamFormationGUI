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
 *          Preference Allocation
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
    private double sumPrevStds = 0.0;

    // Deviations values after suggestion implementation
    private double currStdPref = 0.0;
    private double currStdSkillComp = 0.0;
    private double currStdSkillShortfall = 0.0;
    private double sumCurrStds = 0.0;

    // Initial strategy
    private String currentStrategy = "skillcomp";

    // Applied strategies are stored here to avoid looping
    ArrayList<String> strategies = new ArrayList<>();


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
        this.teamsCopy = (ArrayList<Team>) passedCopy;  // I used to clone here, no required
        this.storePrevValues();
    }


    // Stores the previous values of the standard dadivations
    public void storePrevValues(){

        this.prevStdSkillComp = this.sDInSkillCompetency();
        this.prevStdPref = this.sDInProjPrefAllocPrct();
        this.prevStdSkillShortfall = this.sDInShortFallAcrossTeam();
        sumPrevStds = prevStdSkillComp + prevStdPref + prevStdSkillShortfall;
    }

    // Stores the current values of the standard deviations
    public void storeCurrValues(){

        this.currStdSkillComp = this.sDInSkillCompetency();
        this.currStdPref = this.sDInProjPrefAllocPrct();
        this.currStdSkillShortfall = this.sDInShortFallAcrossTeam();
        sumCurrStds = prevStdSkillComp + prevStdPref + prevStdSkillShortfall;
    }

    /**
     *
     */
    public void run(){
        this.makeSuggestion();
    }


    /**
     * Decides the current strategy
     * skillComp -> skill shortfall -> pref_alloc
     */
    public void strategyDecision()
    {
        if(strategies.size() == 0) {
            strategies.add("skillcomp");
        }else if(strategies.size() == 1){
            strategies.add("skillshortfall");
        }else{
            strategies.add("preference_alloc");
        }

        switch (strategies.get( strategies.size() - 1 )){
            case "skillcomp" :{
                currentStrategy = "skillcomp";
                this.teamsCopy.sort(Comparator.comparing( (t) -> t.getAvgProjSkillComp()));
            }
            break;

            case "skillshortfall" :{
                currentStrategy = "skillshortfall";
                this.teamsCopy.sort(Comparator.comparing( (t) -> t.getTotalSkillShortage()));
            }
            break;

            case "preference_alloc" :{
                currentStrategy = "preference_alloc";
                this.teamsCopy.sort(Comparator.comparing( (t) -> t.getPrctStudentReceivedPreference()));
            }
            break;

        }
    }

    /**
     * Actual implementation that makes the suggestions
     */
    public void makeSuggestion() {

        System.out.println("Making suggestions");
        calStudentGradeAvg();

        for(int i = 0; i < teamsCopy.size(); i++){

            strategyDecision();

            System.out.println(currentStrategy);

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

                this.storeCurrValues();

                System.out.println("Previous Stds Sum : "+sumPrevStds);
                System.out.println("Current Stds Sum : "+sumCurrStds);

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

            if(this.strategies.size() == 3){
                break;
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



    public double sDInSkillCompetency() {

        // Sum the skill competency of all projects for calculating mean
        double sumSkillCompetency = 0;

        // Number of teams
        int numOfTeams = this.teamsCopy.size();

        // Sum / numberOfTeams - mean of skill competency
        double meanSkillCompetency = 0;

        // Iterate over each team member to get the sum of skill competency of all projects
        for (Team team : this.teamsCopy) {
            sumSkillCompetency += team.getAvgProjSkillComp();
        }

        meanSkillCompetency = sumSkillCompetency / numOfTeams;

        // Calculating the SD here
        double temp = 0;
        for (Team team : this.teamsCopy) {
            temp += Math.pow((team.getAvgProjSkillComp() - meanSkillCompetency), 2);
        }

        double sd = Math.sqrt((temp/numOfTeams));
        return sd;
    }


	/*
	 * 24-08-2020 - Standard deviation for percentage of project members getting first and second project preferences across
					projects
	 */

    public double sDInProjPrefAllocPrct() {

        // Sum the skill competency of all projects for calculating mean
        double sumPrefPrct = 0;

        // Number of teams
        int numOfTeams = this.teamsCopy.size();

        // Sum / numberOfTeams - mean of skill competency
        double meanPrefPrct = 0;

        // Iterate over each team member to get the sum of preference allocation in all projects
        for (Team team : this.teamsCopy) {
            sumPrefPrct += team.getPrctStudentReceivedPreference();
        }

        meanPrefPrct = sumPrefPrct / numOfTeams;

        // Calculating the SD here
        double temp = 0;
        for (Team team : this.teamsCopy) {
            temp += Math.pow((team.getPrctStudentReceivedPreference() - meanPrefPrct), 2);
        }


        double sd = Math.sqrt((temp/numOfTeams));
        return sd;
    }


    /*
     * 24-08-2020 - Standard deviation of shortfall across teams
     */

    public double sDInShortFallAcrossTeam() {

        // Sum the skill competency of all projects for calculating mean
        double sumShortFall = 0;

        // Number of teams
        int numOfTeams = this.teamsCopy.size();

        // Sum / numberOfTeams - mean of skill competency
        double meanShortFall = 0;

        // Iterate over each team member to get the sum of skill competency of all projects
        for (Team team : this.teamsCopy) {
            sumShortFall += team.getTotalSkillShortage();
        }

        meanShortFall = sumShortFall / numOfTeams;

        // Calculating the SD here
        double temp = 0;
        for (Team team : this.teamsCopy) {
            temp += Math.pow(( team.getTotalSkillShortage() - meanShortFall), 2);
        }

        double sd = Math.sqrt((temp/numOfTeams));

        return sd;
    }

}
