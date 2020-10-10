package model.entities;

import globals.Globals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Student implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1479813639469477032L;

	// Assigns the unique ID to the student - e.g s1
	private String id;
	
	// Assigns personality to the student - Any(A, B, C, D)
	private String persoanlity;
	
	// Stores the grades of individual subject
	private HashMap<String, Integer> grades;
	
	// Stores the project preferences - pr1 4 pr2 3 .. Limited to 4 projects only
	private HashMap<String, Integer> projPreferences;
	
	// Stores the IDs of additional students with this individual cant work - Limited to 2 persons only
	private ArrayList<String> cantWorkWith;
	
	private String currProjAssoc;

	private Team currTeamAssoc;

	private int sumGrades = 0;

	// Constructor one accepting all fields
	public Student(String id, String personality, HashMap<String, Integer> grades, HashMap<String, Integer> preferences) {
		
		this.id = id;
		this.persoanlity = personality;
		this.grades = grades;
		this.projPreferences = preferences;
		this.cantWorkWith = new ArrayList<String>();
		this.currProjAssoc = "";
		this.currTeamAssoc = null;
	}
	
	// Constructor 2, Without personality
	public Student(String id, HashMap<String, Integer> grades, HashMap<String, Integer> preferences) {
		
		this.id = id;
		this.grades = grades; 
		this.projPreferences = preferences;
		this.cantWorkWith = new ArrayList<String>();
		this.currProjAssoc = "";
		this.currTeamAssoc = null;
	}

	// Constructor 3, Only for testing purpose
	public Student(String id, int P_grades, int N_grades, int A_grades, int W_grades, String pref1, String pref2, String pref3, String pref4, String persoanlity, String conflict1, String conflict2) {

		this.id = id;

		// Setting grades
		this.grades = new HashMap<>();
		this.grades.put("P", P_grades);
		this.grades.put("N", N_grades);
		this.grades.put("A", A_grades);
		this.grades.put("W", W_grades);

		// Setting project preferences
		this.projPreferences = new HashMap<>();
		this.projPreferences.put(pref1, 1);
		this.projPreferences.put(pref2, 2);
		this.projPreferences.put(pref3, 3);
		this.projPreferences.put(pref4, 4);

		this.persoanlity = persoanlity;

		// Setting conflicts
		this.cantWorkWith = new ArrayList<>();
		if(conflict1 != "" && !conflict1.isEmpty() && !conflict1.isBlank()){
			this.addCantWorkWithStudent(conflict1);
		}
		if(conflict2 != "" && !conflict2.isEmpty() && !conflict2.isBlank()) {
			this.addCantWorkWithStudent(conflict2);
		}

		// Other references - this will be set on time
		this.currProjAssoc = "";
		this.currTeamAssoc = null;
	}
	
	// Student can specify 2 students he/she cant work with
	public boolean addCantWorkWithStudent(String studentId) {
		
		// Just add elements until allowed count
		if(this.cantWorkWith.size() < Globals.CANT_WORK_WITH_ENTRY_COUNT) {
			this.cantWorkWith.add(studentId);
			
		}else {
			// Once we reach to count, remove the first element and add the newly specified ID to the list
			this.cantWorkWith.remove(0);
			this.cantWorkWith.add(studentId);
		}
		
		return true;
	}
	
	
	@Override
	public String toString() {
		return "Student [id=" + id + ", persoanlity=" + persoanlity + ", grades=" + grades + ", projPreferences="
				+ projPreferences + ", cantWorkWith=" + cantWorkWith + "]";
	}
	
	/*
	 * Format of student Records - s1 P 4 W 3 N 2 A 1
	 */
	public String getWriteFormattedRecord() {
		
		StringBuilder studentStr = new StringBuilder();
		// Append ID
		studentStr.append(this.getId());
		
		// Append grades
		for( String subId : this.getGrades().keySet())
			studentStr.append(" "+subId+" "+this.getGrades().get(subId));
		
		studentStr.append("\n");			
		
		return studentStr.toString();
	}

	// Getters-Setters Starts Here
	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}
	

	public HashMap<String, Integer> getProjPreferences() {
		return projPreferences;
	}
	

	public void setProjPreferences(HashMap<String, Integer> projPreferences) {
		this.projPreferences = projPreferences;
	}


	public HashMap<String, Integer> getGrades() {
		return grades;
	}


	public void setGrades(HashMap<String, Integer> grades) {
		this.grades = grades;
	}


	public ArrayList<String> getCantWorkWith() {
		return cantWorkWith;
	}


	public void setCantWorkWith(ArrayList<String> cantWorkWith) {
		this.cantWorkWith = cantWorkWith;
	}

	
	public String getPersoanlity() {
		return persoanlity;
	}

	
	public void setPersoanlity(String persoanlity) {
		this.persoanlity = persoanlity;
	}

	
	public String getCurrProjAssoc() {
		return currProjAssoc;
	}

	
	public void setCurrProjAssoc(String currProjAssoc) {
		this.currProjAssoc = currProjAssoc;
	}

	public Team getCurrTeamAssoc() {
		return currTeamAssoc;
	}

	public void setCurrTeamAssoc(Team currTeamAssoc) {
		this.currTeamAssoc = currTeamAssoc;
	}

	public int getSumGrades() {
		return sumGrades;
	}

	public void setSumGrades(int sumGrades) {
		this.sumGrades = sumGrades;
	}

	// Getters-Setters Ends Here

	// [6] Clone method for deep cloning, since team contains student's reference
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
