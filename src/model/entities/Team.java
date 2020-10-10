package model.entities;

import model.exceptions.*;
import utilities.Action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;


public class Team implements Serializable, Cloneable, Action {
	
	private static final long serialVersionUID = -5606452120005550219L;

	private final static int defaultTeamSize = 4;
	private String teamId;
	private Project projectRef;
	
	// Stores the students ref who are member of this team
	private LinkedHashMap<String, Student> members = new LinkedHashMap<String, Student>();
	
	// Avg skill competency per category
	private HashMap<String, Double> avgCategorySkillComp = new HashMap<String, Double>();
	
	// Avg category shortage per category (Required Skill Competency - (Avg skill competency in that category))
	private HashMap<String, Double> avgCategorySkillShortage = new HashMap<String, Double>();
	
	// How many students wanted this project - in percentage
	private double prctStudentReceivedPreference = 0.0;
	
	// Avg of skill competency for the project = sum(All skills) / (number of students * number of subjects)
	private double avgProjSkillComp = 0.0;
	
	// Sum of all shortages
	private double totalSkillShortage = 0.0;
	
	
	public Team() {
		this.teamId = "";
		this.projectRef = null;
		this.members = new LinkedHashMap<String, Student>();
		this.prctStudentReceivedPreference = 0;
		this.avgCategorySkillComp = new HashMap<String, Double>();
		this.avgCategorySkillShortage = new HashMap<String, Double>();
	}
	
	
	public Team(String teamId, Project projectRef) {
		this.teamId = teamId;
		this.projectRef = projectRef;
		this.members = new LinkedHashMap<String, Student>();
		this.prctStudentReceivedPreference = 0;
		this.avgCategorySkillComp = new HashMap<String, Double>();
		this.avgCategorySkillShortage = new HashMap<String, Double>();
	}
	
	
	/*
	 * Adds members to existing teams or newly created team
	 */
	public void addMembers(Project projRef, Student [] studentRefs) throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
		
		this.projectRef = projRef;
		
		// Basic null checking
		if(projRef == null) {
			throw new NullProjectException();
		}
		
		if(projRef.getTeamRef() != null) {
			throw new ProjectAlreadyAssignedException();
		}
		
		// Iterating over each student and adding to the team
		for(Student sRef : studentRefs) {
			if (sRef == null) {
				throw new StudentNotFoundException("Student not found");
			}else {
				this.addMember(sRef);
			}
		}
		
		// After team formation, check for leader
		this.checkIfLeaderExists();
		
		for(Student sRef : studentRefs) {
			sRef.setCurrProjAssoc(projRef.getId());
			sRef.setCurrTeamAssoc(this);
		}
	}
	
	
	/*
	 * Add individual member to the team
	 * Note : private method
	 */
	private void addMember(Student s) throws InvalidMemberException, RepeatedMemberException, StudentConflictException, ExcessMemberException {

		if(this.getMembers().size() >= defaultTeamSize){
			throw new ExcessMemberException();
		}
		// Student already assigned to another team
		if(s.getCurrProjAssoc() != "")
			throw new InvalidMemberException( s );
	
		// Check if any of the team member have any conflicts with one another
		this.checkConflicts(s);

		// Duplicate student Ids
		if ( this.getMembers().containsKey(s.getId()) )
			throw new RepeatedMemberException();
		
		this.members.put(s.getId(), s);
	}


	/**
	 * 09-09-2020 - addMember for GUI component
	 * @param s
	 * @throws StudentConflictException
	 */
	public void addMember(Project projRef, Student s) throws InvalidMemberException, RepeatedMemberException, StudentConflictException, ExcessMemberException, NoLeaderException, PersonalityImbalanceException {

		// Add member to the team
		this.addMember(s);

		this.checkIfPersonalityImbalance();

		// On success, assign current projectId to that student
		s.setCurrProjAssoc(projRef.getId());
		s.setCurrTeamAssoc(this);

		// Check leader and personality imbalance exceptions only after team of 4 is formed
		if(this.getMembers().size() == defaultTeamSize){
			this.checkIfLeaderExists();
		}

		// Updating the statistics
		this.updateStatistics();
	}

	/**
	 * 13-09-2020 - A team member can be removed from the team.
	 * This function is required to perform roll back of different references
	 * @param s
	 * @throws StudentConflictException
	 */

	public void removeMember(Project projRef, Student s){
		this.members.remove(s.getId());
		s.setCurrProjAssoc("");
		s.setCurrTeamAssoc(null);

		this.updateStatistics();
	}

	/*
	 * Checks the conflict with existing team members or the other way around
	 */
	public void checkConflicts(Student s) throws StudentConflictException {
		
		// Check for each member of the team
		for (Student member : this.getMembers().values()) {
		
			// Check if the new member has conflicts with exisiting team members
			if(s.getCantWorkWith().contains( member.getId() ))
				throw new StudentConflictException(s.getId()+" has conflicts with "+member.getId()+". Your last addition ("+s.getId()+") will be reverted.");
			
			// Check if existing member has any conflicts with new member
			if(member.getCantWorkWith().contains( s.getId() ))
				throw new StudentConflictException(member.getId()+" has conflicts with "+s.getId()+". Your last addition ("+s.getId()+") will be reverted.");
		}
	}
	
	
	/*
	 * Checks if the team contains leader or not
	 */
	public boolean checkIfLeaderExists() throws NoLeaderException {
		
		boolean leaderFound = false;
		
		// Iterate through the each member
		for(Student s : this.getMembers().values() ) {
			if (s.getPersoanlity().contentEquals("A")) {
				leaderFound = true;
				break;
			}
		}
		
		if(leaderFound != true) {
			throw new NoLeaderException("No leader in the team");
		}
		
		return leaderFound;
		
	}
	
	/*
	 * There should be atleast 3 different personalities
	 */
	public boolean checkIfPersonalityImbalance() throws PersonalityImbalanceException {
		
		// Being set, it will not hold duplicate values, Checking can be done just by refering at size
		Set<String> personalityTypes = new HashSet<String>();
		
		for(Student sRef : this.members.values()) {
			personalityTypes.add(sRef.getPersoanlity());
		}

		// 3 members have already been added
		if(this.members.size() == 3){
			// Of same personality type say b,b,b
			if(personalityTypes.size() == 1) {

				throw new PersonalityImbalanceException("Imbalanced personalities");
			}
		}else if( this.members.size() == 4 ) {

			// When complete team has been formed
			if(personalityTypes.size() < 3) {
				throw new PersonalityImbalanceException("Imbalanced personalities");
			}
		}
		
		return true;
	}
	
	
	/*
	 * Computes the average skill competancy per category based on current allocated students
	 */
	public void computeAvgSkillPerCategory() {
		
		double [] perCategorySum = { 0, 0, 0, 0 };
		
		// For each category sum the grades obtained by each student
		int studentCount = this.getMembers().size();
		for (Student studentRef : this.members.values()) {
			
			perCategorySum[0] += studentRef.getGrades().get("P") == null ? 0 : studentRef.getGrades().get("P");
			perCategorySum[1] += studentRef.getGrades().get("N") == null ? 0 : studentRef.getGrades().get("N");
			perCategorySum[2] += studentRef.getGrades().get("W") == null ? 0 : studentRef.getGrades().get("W");
			perCategorySum[3] += studentRef.getGrades().get("A") == null ? 0 : studentRef.getGrades().get("A");
		}
		
		
		// Store that in teams
		avgCategorySkillComp.put("P", perCategorySum[0] / studentCount);
		avgCategorySkillComp.put("N", perCategorySum[1] / studentCount);
		avgCategorySkillComp.put("W", perCategorySum[2] / studentCount);
		avgCategorySkillComp.put("A", perCategorySum[3] / studentCount);
	}
	
	
	/*
	 * Computes the average skills of the entire team
	 * Avg = sum (All marks for all subjects for all team members ) / (number of students * number of subjects)
	 */
	public void computeAvgSkillForProject() {
		
		int studentCount = this.getMembers().size();
		// int totalCount = studentCount * this.getAvgCategorySkillComp().size();		// Divident
		int totalCount = studentCount;													// Divident -- already having avg category marks
		double totalSum = 0;
		
		for(Double avgVal : this.getAvgCategorySkillComp().values()) {
			totalSum += avgVal;
		}
		
		this.avgProjSkillComp = (totalSum / totalCount);
	}
	
	
	
	/*
	 * Computes the skill shortage per category as required by the project
	 * Negative value indicates the skill shortage
	 */
	public void computeCategorySkillShortage() {
		
		for(String subId : this.getAvgCategorySkillComp().keySet()) {
			
			this.avgCategorySkillShortage.put(subId, (this.getAvgCategorySkillComp().get(subId) - this.projectRef.getSoughtSkills().get(subId) ) );
		}
		
		System.out.println(this.toString());
	}
	
	
	
	/*
	 * Calculates the total skill shortage. 
	 * Negative value indicates the skill shortage
	 * Positive value indicates the skill are in excess
	 * Reference : https://www.baeldung.com/java-stream-sum
	 */
	public void computeOverallSkillShortage() {
		
		// Run through all values, get negative values as it indicates the shortfall, convert negative to positive for returning purpose, and finally sum
		Double totalSkillShortage =  this.avgCategorySkillShortage.values().stream()
											.filter(x -> x < 0)
											.map(x -> Math.abs(x))
											.mapToDouble(Double::valueOf)
											.sum();
		
		this.totalSkillShortage = totalSkillShortage;
	}
	
	
	/*
	 * Stores the percentage of studetns who got their first and second preference.
	 */
	public void computePreferenceAllocPct() {
		
		double count = 0;
		
		// Run through each student
		for(Student studentRef : this.getMembers().values()) {
			
			// Get their project preferences
			HashMap<String, Integer> projPreference = studentRef.getProjPreferences();
			
			// Run through their project preferences
			for (String preferenceId : projPreference.keySet()) {
				
				if(preferenceId.contentEquals(this.projectRef.getId())) {
					
					// If this students preference is 1 or 2 then consider this student as success
					if(( projPreference.get(preferenceId) == 3 || projPreference.get(preferenceId) == 4 )) {
						count += 1;
						break;
					}
				}
			}
			
		}
		
		this.setPrctStudentReceivedPreference( ( count / this.getMembers().size() ) * 100 );
	}


	public void updateStatistics(){
		// Updating the statistics
		this.computeAvgSkillPerCategory();
		this.computeAvgSkillForProject();
		this.computeCategorySkillShortage();
		this.computeOverallSkillShortage();
		this.computePreferenceAllocPct();
	}
	
	@Override
	public String toString() {
		return "Team [teamId=" + teamId + ", projectRef=" + projectRef + ", members=" + members
				+ ", avgCategorySkillComp=" + avgCategorySkillComp + ", avgCategorySkillShortage="
				+ avgCategorySkillShortage + ", prctStudentReceivedPreference=" + prctStudentReceivedPreference
				+ ", avgProjSkillComp=" + avgProjSkillComp + ", totalSkillShortage=" + totalSkillShortage + "]";
	}


	// Getter setters starts here	
	
	public LinkedHashMap<String, Student> getMembers() {
		return members;
	}
	
	
	public void setMembers(LinkedHashMap<String, Student> members) {
		this.members = members;
	}


	public String getTeamId() {
		return teamId;
	}


	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}


	public double getPrctStudentReceivedPreference() {
		return prctStudentReceivedPreference;
	}
	

	public HashMap<String, Double> getAvgCategorySkillComp() {
		return avgCategorySkillComp;
	}


	public void setAvgCategorySkillComp(HashMap<String, Double> avgCategorySkillComp) {
		this.avgCategorySkillComp = avgCategorySkillComp;
	}


	public Project getProjectRef() {
		return projectRef;
	}


	public void setProjectRef(Project projectRef) {
		this.projectRef = projectRef;
	}


	public HashMap<String, Double> getAvgCategorySkillShortage() {
		return avgCategorySkillShortage;
	}


	public void setAvgCategorySkillShortage(HashMap<String, Double> avgCategorySkillShortage) {
		this.avgCategorySkillShortage = avgCategorySkillShortage;
	}


	public double getAvgProjSkillComp() {
		return avgProjSkillComp;
	}


	public void setAvgProjSkillComp(double avgProjSkillComp) {
		this.avgProjSkillComp = avgProjSkillComp;
	}


	public double getTotalSkillShortage() {
		return totalSkillShortage;
	}


	public void setTotalSkillShortage(double totalSkillShortage) {
		this.totalSkillShortage = totalSkillShortage;
	}


	public void setPrctStudentReceivedPreference(double prctStudentReceivedPreference) {
		this.prctStudentReceivedPreference = prctStudentReceivedPreference;
	}
	
	// Getter setter ends here

	// [6] Deep cloning Team object
	@Override
	public Object clone() throws CloneNotSupportedException {

		// Cloning Team
		Team clonedTeam = (Team)super.clone();

		// Associated Project Reference
		clonedTeam.setProjectRef((Project)clonedTeam.getProjectRef().clone());

		// Cloning members
		LinkedHashMap<String, Student> clonedMembers = new LinkedHashMap<>();
		for(Student s : clonedTeam.getMembers().values()){
			clonedMembers.put(s.getId(), (Student)s.clone());
		}

		clonedTeam.setMembers(clonedMembers);

		return clonedTeam;
	}

	@Override
	public void execute() {
		System.out.println(this.getTeamId()+" added into stack");
	}

	@Override
	public void undo() {
		System.out.println("Before undo");
		System.out.println(this.toString());
		System.out.println(this.members.keySet());

		this.removeMember(this.projectRef, this.getLastMemberInTeam());
		this.updateStatistics();

		System.out.println("After undo");
		System.out.println(this.toString());
		System.out.println(this.members.keySet());
	}

	@Override
	public void redo(){
		// We are not doing the redo for add operation
	}

	/**
	 * Since we are using linked hasmap, we need to remove the last member from the members
	 */
	public Student getLastMemberInTeam(){

		Student lastStudent = null;
		for(Student s : this.members.values()){
			lastStudent = s;
		}

		return lastStudent;
	}
}
