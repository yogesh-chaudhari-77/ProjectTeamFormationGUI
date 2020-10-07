package model.entities;

import globals.Globals;

import java.io.Serializable;
import java.util.HashMap;

public class Project implements Serializable, Cloneable {
	
	
	private static final long serialVersionUID = -9024218799680537993L;
	
	private String id;
	private String title;
	private String description;
	private String projectOwnerId;
	private int projectPrefSum;
	private HashMap<String, Integer> soughtSkills;
	
	private Team teamRef = null;


	// Constructors
	public Project(String id, String title, String description, String projectOwnerId) {
		
		this.id = id;
		this.title = title;
		this.description = description;
		this.projectOwnerId = projectOwnerId;
		
		// Hashmap for storing the skill requirements as per subjects codes
		this.soughtSkills = new HashMap<String, Integer>();
		this.soughtSkills.put(Globals.PROG_SOFT_ENGG, Globals.defaultSoughtSkill);
		this.soughtSkills.put(Globals.NETWORK_SECURITY, Globals.defaultSoughtSkill);
		this.soughtSkills.put(Globals.ANALYTICS_BIG_DATA, Globals.defaultSoughtSkill);
		this.soughtSkills.put(Globals.WEB_MOBILE_APP, Globals.defaultSoughtSkill);
	}
	// Constructors Ends Here
	
	
	// It returns the formatted string required for storing companies data in the file.
	public String getWriteFormattedRecord() {
		
		StringBuilder projectStr = new StringBuilder();
		projectStr.append(this.getTitle()			+"\n");
		projectStr.append(this.getId()				+"\n");
		projectStr.append(this.getDescription()		+"\n");
		projectStr.append(this.getProjectOwnerId()	+"\n");
		
		projectStr.append("W "+this.soughtSkills.get("W")+" P "+this.soughtSkills.get("P")+" N "+this.soughtSkills.get("N")+" A "+this.soughtSkills.get("A")+"\n");
		
		// Append an empty line as separation between the 2 project records
		projectStr.append("\n");				
		
		return projectStr.toString();
	}


	@Override
	public String toString() {
		return "Project [id=" + id + ", title=" + title + ", description=" + description + ", projectOwnerId="
				+ projectOwnerId + ", projectPrefSum=" + projectPrefSum + ", soughtSkills=" + soughtSkills + "]";
	}


	// Getters-Setters Starts Here
	public String getTitle() {
		return title;
	}
	
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public String getId() {
		return id;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public String getProjectOwnerId() {
		return projectOwnerId;
	}
	
	
	public void setProjectOwnerId(String projectOwnerId) {
		this.projectOwnerId = projectOwnerId;
	}
	
	
	public HashMap<String, Integer> getSoughtSkills() {
		return soughtSkills;
	}
	
	
	public void setSoughtSkills(HashMap<String, Integer> soughtSkills) {
		this.soughtSkills = soughtSkills;
	}


	public int getProjectPrefSum() {
		return projectPrefSum;
	}


	public void setProjectPrefSum(int projectPrefSum) {
		this.projectPrefSum = projectPrefSum;
	}
	
	
	public Team getTeamRef() {
		return teamRef;
	}


	public void setTeamRef(Team teamRef) {
		this.teamRef = teamRef;
	}
	
	// Getters-Setters Ends Here

	// [6] Returns the copy of project - for cloaning implemented in team class
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
