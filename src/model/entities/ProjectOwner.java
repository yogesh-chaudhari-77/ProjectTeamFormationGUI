package model.entities;

import java.util.HashMap;

public class ProjectOwner {

	private String projOwnerId;
	private String firstName;
	private String surname;
	private String role;
	private String email;
	private String companyId;
	
	// Currently not in use.
	private HashMap<String, String> studentsPersonalityAssoc;
	
	
	// Constructors starts here, usually used when reading projects.txt file
	public ProjectOwner(String id) {
		this.projOwnerId = id;
		studentsPersonalityAssoc = new HashMap<String, String>();
	}
	
	
	public ProjectOwner(String id, String firstName, String surname, String role, String email, String companyId) {
		
		this.projOwnerId = id;
		this.firstName = firstName;
		this.surname = surname;
		this.role = role;
		this.email = email;
		this.companyId = companyId;
		studentsPersonalityAssoc = new HashMap<String, String>();
	}
	// Constructors Ends here
	
	
	// Assign the personality type to the students the interviewer interviewed - This seems to be not required in the milestone 1
	public void assignStudentPersonalities(String studentId, String personalityType) {
		this.studentsPersonalityAssoc.put(studentId, personalityType);
	}
	
	// Getters - Setters Starts here
	public String getProjOwnerId() {
		return projOwnerId;
	}
	
	
	public void setProjOwnerId(String projOwnerId) {
		this.projOwnerId = projOwnerId;
	}
	
	
	public String getFirstName() {
		return firstName;
	}
	
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	
	public String getSurname() {
		return surname;
	}
	
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	
	public String getRole() {
		return role;
	}
	
	
	public void setRole(String role) {
		this.role = role;
	}
	
	
	public String getEmail() {
		return email;
	}
	
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public String getCompanyId() {
		return companyId;
	}
	
	
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}


	public HashMap<String, String> getStudentsPersonalityAssoc() {
		return studentsPersonalityAssoc;
	}


	public void setStudentsPersonalityAssoc(HashMap<String, String> studentsPersonalityAssoc) {
		this.studentsPersonalityAssoc = studentsPersonalityAssoc;
	}	

	// Getters - Setters Ends here
}
