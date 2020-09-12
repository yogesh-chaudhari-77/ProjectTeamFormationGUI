/**
 * References :
 * [1] Releases · xerial/sqlite-jdbc
 * Releases · xerial/sqlite-jdbc (2020). Available at: https://github.com/xerial/sqlite-jdbc/releases (Accessed: 12 September 2020).
 */
package controller;

import globals.Globals;
import model.entities.*;
import utilities.DatabaseHelper;
import utilities.FileHandlingHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

/*
 * Java class that implements methods for reading and writting data from/to files
 * 
 */

public class DataSaverRetrieval {

	// Singleton reference of the fileHandling
	private static final FileHandlingHelper fileHandler = FileHandlingHelper.init();
	private static final DatabaseHelper dbHelper = DatabaseHelper.getInstance();
	
	// Reads the students file, creates the student objects, and return studentsList hashmap
	public static HashMap<String, Student> readStudentsFile() {

		System.out.println("Start : Reading Students File");
		HashMap<String, Student> studentsList = new HashMap<String, Student>();
		Scanner input = null;

		try {
			input = new Scanner(new File(Globals.STUDENTS_TXT));

			// Reading Next Lines
			while(input.hasNextLine()) {

				String studentRecord = input.nextLine();
				StringTokenizer tokener = new StringTokenizer(studentRecord);
				HashMap<String, Integer> grades = new HashMap<String, Integer>();

				String studentId = tokener.nextToken();			// Token 1 - StudentId

				try {
					// Store the grades in each subject
					grades.put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));
					grades.put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));
					grades.put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));
					grades.put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));

				}catch(NumberFormatException nfe) {
					nfe.printStackTrace();
				}

				studentsList.put(studentId, new Student(studentId, grades, new HashMap<String, Integer>()));
			}

		} catch (FileNotFoundException e) {
			System.err.println("Unable to locate required resource. Please check the file path.\n");
		} catch (Exception e) {
			System.err.println("readStudentsFile : Problem occured at our end. Please try again.");
		}

		if(input != null) {
			input.close();
		}

		System.out.println("End : Reading Students File");
		System.out.println("Total number of students in system : "+studentsList.size());
		return studentsList;

	}


	// Takes the current/updated copy of the studentHashmap, and writes to students.txt file
	public static void writeStudentsFile(HashMap<String, Student> studentsList) {

		System.err.println("Start : Writing student list.");

		if(studentsList != null && studentsList.size() > 0) {

			// Clear the existing contents of the students.txt file
			fileHandler.clearFileContents(Globals.STUDENTS_TXT);

			// Prepare same file file for writting with append mode
			fileHandler.setFile(Globals.STUDENTS_TXT, "append");

			// Iterate through list and write the fomatted string to file.
			ArrayList<String> studentKeys = new ArrayList<String>(studentsList.keySet());
			Collections.sort(studentKeys);

			for(String studentId : studentKeys) {

				Student studentRef = studentsList.get(studentId);
				System.out.println(fileHandler.writeOp(studentRef.getWriteFormattedRecord()));
			}

			fileHandler.destroy();
			
		}else {
			System.err.println("Student list is empty.");
		}

		System.err.println("End : Writting student list.");
	}

	// Read company file, create a company objects and returns companyList hashmap
	public static HashMap<String, Company> readComapniesFile()
	{
		System.out.println("Start : Reading companies file");
		HashMap<String, Company> companiesList = new HashMap<String, Company>();
		Scanner input = null;

		try {
			input = new Scanner(new File(Globals.COMPANIES_TXT));

			// Check if more data is there
			while ( input.hasNextLine() ) {

				// Read lines as per template
				String id = input.nextLine();
				String companyName = input.nextLine();
				String companyABN = input.nextLine();
				String webURL = input.nextLine();
				String address = input.nextLine();

				// Create company object and store that
				companiesList.put(id, new Company(id, companyName, companyABN, webURL, address));

				// Advance empty line which acts as separator
				if(input.hasNextLine()) {
					input.nextLine();
				}
			}

		} catch (FileNotFoundException e) {
			System.err.println("Unable to locate required resource. Please check the file path.\n");
		} catch (Exception e) {
			System.err.println("readComapniesFile : Problem occured at our end. Please try again.");
			e.printStackTrace();
		}

		if(input != null) {
			input.close();
		}

		System.out.println("End : Reading companies file");
		System.out.println("Total Companies in system "+companiesList.size());
		return companiesList;
	}


	// Writes back in-memory companies list to file for future reference.
	public static void writeCompaniesFile(HashMap<String, Company> companiesList) {

		System.out.println("Start : Writting companies file");
		if(companiesList != null && companiesList.size() > 0) {

			// Clear the existing contents of the file
			fileHandler.clearFileContents(Globals.COMPANIES_TXT);

			// Prepare same file for appending new records
			fileHandler.setFile(Globals.COMPANIES_TXT, "append");

			// Sort the collction keys based on company id
			ArrayList<String> compKeys = new ArrayList<String>(companiesList.keySet());
			Collections.sort(compKeys);

			// Iterate through list and write the fomatted string to file.
			for(String compId : compKeys) {

				Company tempComp = companiesList.get(compId);
				System.out.println(fileHandler.writeOp(tempComp.getWriteFormattedRecord()));
			}

			fileHandler.destroy();
		}else {
			System.err.println("Company list is empty.");
		}

		System.out.println("End : Writing companies file");
	}

	
	// Read projects from file, create a new project objects and stores that in the companiesList
	public static HashMap<String, Project> readProjectsFile() {

		System.out.println("Reading projects file . . . ");
		HashMap<String, Project> projectList = new HashMap<String, Project>();
		Scanner input = null;
		Project tempProject = null;

		try {
			input = new Scanner(new File(Globals.PROJECTS_TXT));

			// Check if more data is there
			while ( input.hasNextLine() ) {

				// Read lines as per template
				String title = input.nextLine();
				String projId = input.nextLine();
				String projDesc = input.nextLine();
				String projOwnerId = input.nextLine();
				String SkillsSought = input.nextLine();

				tempProject = new Project(projId, title, projDesc, projOwnerId);

				// format P 1 N 2 A 3 W 4
				StringTokenizer tokener = new StringTokenizer(SkillsSought);

				// There should be always be 8 token given that there are always 4 subjects are always sought after
				if(tokener.countTokens() == 8) {

					// If there is problem in any any subject preference then store nothing.
					try {
						// Read the soughtSkills preferences.
						tempProject.getSoughtSkills().put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));
						tempProject.getSoughtSkills().put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));
						tempProject.getSoughtSkills().put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));
						tempProject.getSoughtSkills().put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));

					}catch(NumberFormatException nfe) {
						nfe.printStackTrace();
					}

				}else {
					// Report the error and continue reading next project details;
					if(input.hasNextLine()) {
						input.nextLine();
					}
					continue;
				}

				// Create company object and store that
				projectList.put(projId, tempProject);

				// Advance empty line which acts as separator between 2 project records
				if(input.hasNextLine()) {
					input.nextLine();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Unable to locate required resource. Please check the file path.\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("readComapniesFile : Problem occured at our end. Please try again.");
		}

		if(input != null) {
			input.close();
		}
		
		return projectList;
	}
	
	
	// Write the in-memory project list into the projects.txt file - Usually to be called at quite event or save event.
	public static void writeProjectsFile(HashMap<String, Project> projectsList) {

		if(projectsList != null && projectsList.size() > 0) {

			// Clear the existing contents of the file
			
			fileHandler.clearFileContents(Globals.PROJECTS_TXT);			

			// Prepare same file file for appending new records
			fileHandler.setFile(Globals.PROJECTS_TXT, "append");

			// Sort projects based on project IDs for outputting purpose. 
			ArrayList<String> projKeys = new ArrayList<String>(projectsList.keySet());
			Collections.sort(projKeys);

			// Iterate through list and write the fomatted string to file.
			for(String projId : projKeys) {
				Project tempProj = projectsList.get(projId);
				System.out.println(fileHandler.writeOp(tempProj.getWriteFormattedRecord()));
			}

			fileHandler.destroy();
			
		}else {
			System.err.println("Project list is empty.");
		}
	}


	// Generates the file studentinfo.txt for the project owners with the associated personalities  
	public static void writeStudentInfoFile(HashMap<String, Student> studentsList) {

		fileHandler.clearFileContents(Globals.STUDENTINFO_TXT);
		fileHandler.setFile(Globals.STUDENTINFO_TXT, "append");

		/* Not required for milestone 1
		ProjectOwner actingProjOwner = this.getProjectOwnersList().get("Own1");
		HashMap<String, String> stuAssocs = actingProjOwner.getStudentsPersonalityAssoc();
		 */

		// Sort collection keys based on the student ids
		ArrayList<String> sKeys = new ArrayList<String>(studentsList.keySet());
		Collections.sort(sKeys);

		// Iterate through only those people whom are being interviewd by the project owner
		for(String stuentId : sKeys) {

			StringBuilder stuRecordStr = new StringBuilder();
			Student student = studentsList.get(stuentId);
			
			// Append Student Id
			stuRecordStr.append(student.getId());

			// Append Student Scores
			for( String subId : student.getGrades().keySet())
				stuRecordStr.append(" "+subId+" "+student.getGrades().get(subId));

			// Append Associated Personality
			stuRecordStr.append(" "+student.getPersoanlity());

			// Append can't work with preferences
			for( String cantworkWithId : student.getCantWorkWith() )
				stuRecordStr.append(" "+cantworkWithId);

			fileHandler.writeOp(stuRecordStr.toString()+"\n");
		}

		fileHandler.destroy();
	}

	
	
	public static HashMap<String, Student> readStudentInfoFile(HashMap<String, Student> studentsList) {

		System.out.println("Read studentinfo file . . .");
		Scanner input = null;

		try {
			input = new Scanner(new File(Globals.STUDENTINFO_TXT));

			// Check if more data is there
			while ( input.hasNextLine() ) {

				// Read lines as per template
				String studentRecord = input.nextLine();
				StringTokenizer tokener = new StringTokenizer(studentRecord);
				
				String studentId = tokener.nextToken();
				System.out.print(studentId+" ");
				String personalityType = "";
				Student studenRef = studentsList.get(studentId);

				// Advance 8 tokens has we already have that from students.txt file
				for(int i = 0; i < 8; i++) {
					System.out.print(tokener.nextToken()+" ");
				}

				try {
					// Read personality
					if(tokener.hasMoreTokens()) {
						personalityType = tokener.nextToken();
						System.out.println( personalityType +" ");
						studenRef.setPersoanlity( personalityType );
					}

					// Read Cant Work With
					for (int i = 0; i < Globals.CANT_WORK_WITH_ENTRY_COUNT; i++) {
						if(tokener.hasMoreTokens()) {
							studenRef.getCantWorkWith().add( tokener.nextToken() );
						}
					}

				}catch(NoSuchElementException nfe) {
					nfe.printStackTrace();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Unable to locate required resource. Please check the file path.\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("readComapniesFile : Problem occured at our end. Please try again.");
		}

		if(input != null) {
			input.close();
		}
		
		return studentsList;
	}


	// Read student ore
	public static HashMap<String, Student> readStudentPreferencesFile(HashMap<String, Student> studentsList) {
		
		System.out.println("Reading student preferences file . . .");
		Scanner input = null;

		try {
			input = new Scanner(new File(Globals.PREFERENCES_TXT));

			// Check if more data is there
			while ( input.hasNextLine() ) {

				// Read lines as per template
				String studentId = input.nextLine();
				String projectPreferences = input.nextLine();

				Student studenRef = studentsList.get(studentId);

				StringTokenizer tokener = new StringTokenizer(projectPreferences);

				// There should be always be 8 token given that there are always 4 subjects are always sought after
				if(tokener.countTokens() == 8) {

					// If there is problem in any any project preference then store nothing.
					try {
						// Read Student Project Preferences
						for (int i = 0; i < Globals.MAX_PROJ_PREF_ALLOWED; i++) {
							studenRef.getProjPreferences().put(tokener.nextToken(), Integer.parseInt(tokener.nextToken()));
						}

					}catch(NumberFormatException nfe) {
						nfe.printStackTrace();
					}

				}

				// Advance empty line which acts as separator
				if(input.hasNextLine()) {
					input.nextLine();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Unable to locate required resource. Please check the file path.\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("readStudentPreferencesFile : Problem occured at our end. Please try again.");
		}

		input.close();
		return studentsList;
	}


	// Writes back students projects preferences
	public static void writeStudentPreferences(HashMap<String, Student> studentsList) {

		fileHandler.clearFileContents(Globals.PREFERENCES_TXT);
		fileHandler.setFile(Globals.PREFERENCES_TXT, "append");

		ArrayList<String> sKeys = new ArrayList<String>(studentsList.keySet());
		Collections.sort(sKeys);

		// Iterate through only those people whom are being interviewd by the project owner
		for(String stuentId : sKeys) {

			StringBuilder stuRecordStr = new StringBuilder();
			Student student = studentsList.get(stuentId);
			
			// Append Student Id
			stuRecordStr.append(student.getId()+"\n");

			// Append Projects Preferences Scores
			for( String subId : student.getProjPreferences().keySet())
				stuRecordStr.append(subId+" "+student.getProjPreferences().get(subId)+" ");

			fileHandler.writeOp(stuRecordStr.toString()+"\n\n");
		}

		fileHandler.destroy();

	}
	
	
	// Write the in-memory project list into the projects.txt file - Usually to be called at quite event or save event.
	public static void writeShortListedProjectsFile(LinkedHashMap<String, Project> shortListedProjects) {

		if(shortListedProjects != null && shortListedProjects.size() > 0) {

			// Clear the existing contents of the file
			
			fileHandler.clearFileContents(Globals.POPULAR_PROJECTS_TXT);			

			// Prepare same file file for appending new records
			fileHandler.setFile(Globals.POPULAR_PROJECTS_TXT, "append");
			

			// Iterate through list and write the fomatted string to file.
			for(Project project : shortListedProjects.values()) {
				System.out.println(fileHandler.writeOp(project.getWriteFormattedRecord()));
			}

			fileHandler.destroy();
			
		}else {
			System.err.println("Project list is empty.");
		}
	}

	
	/*
	 * Store teams list as a serialised content
	 */
	public static void writeTeamsFile(HashMap<String, Team> teamsList) {
		
		
		try {
			
			fileHandler.setBinaryFile(Globals.TEAMS_TXT, "write");
			fileHandler.serializeObj(teamsList);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fileHandler.destroy();
	}

	
	/*
	 * Read the serialised data and return the Hashmap containing previously formed teams
	 */
	public static HashMap<String, Team> readTeamsFile(){

		HashMap<String, Team> teamsList = new HashMap<String, Team>();
		if(fileHandler.setBinaryFile(Globals.TEAMS_TXT, "read")) {

			try {

				teamsList = ((HashMap<String, Team>) fileHandler.readSerializedObj());

			} catch (ClassNotFoundException | IOException e ) {
				e.printStackTrace();
			}

			System.out.println(teamsList.size());
			for (Team t : teamsList.values()) {
				System.out.println(t.toString());
			}
		}

		System.out.println("Read teams list :"+teamsList.size());
		return teamsList;
	}


	/**
	 * Inserts companies records to the company table in SQL lite
	 */
	public static void writeCompaniesToDatabase(HashMap<String, Company> companiesList){

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = dbHelper.getDBConnection();

			// Clear existing records
			String sql = "DELETE FROM company";
			Statement deleteStmt = conn.createStatement();
			deleteStmt.executeUpdate(sql);

			for(Company company : companiesList.values()){
				sql = "INSERT INTO COMPANY (id,name,abn,webURL,address) " +
						"VALUES (?,?,?,?,?);";

				stmt = conn.prepareStatement(sql);
				stmt.setString(1, company.getId());
				stmt.setString(2, company.getName());
				stmt.setString(3, company.getAbn());
				stmt.setString(4, company.getWebURL());
				stmt.setString(5, company.getAddress());

				stmt.executeUpdate();
			}
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}


	/**
	 * Inserts project owners records to the project_owner table in SQL lite
	 */
	public static void writeProjectOwnerToDatabase(HashMap<String, ProjectOwner> projectOwners){

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = dbHelper.getDBConnection();

			// Clear existing records
			String sql = "DELETE FROM project_owner";
			Statement deleteStmt = conn.createStatement();
			deleteStmt.executeUpdate(sql);

			for(ProjectOwner projectOwner : projectOwners.values()){
				sql = "INSERT INTO project_owner (id, firstname, surname, role, email, companyId) " +
						"VALUES (?,?,?,?,?,?);";

				stmt = conn.prepareStatement(sql);
				stmt.setString(1, projectOwner.getProjOwnerId());
				stmt.setString(2, projectOwner.getFirstName());
				stmt.setString(3, projectOwner.getSurname());
				stmt.setString(4, projectOwner.getRole());
				stmt.setString(5, projectOwner.getEmail());
				stmt.setString(6, projectOwner.getCompanyId());

				stmt.executeUpdate();
			}
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	/**
	 * Insert teams into the database
	 */
	public static void writeTeamsToDatabase(HashMap<String, Team> teams){

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = dbHelper.getDBConnection();

			// Clear existing records
			String sql = "DELETE FROM team";
			Statement deleteStmt = conn.createStatement();
			deleteStmt.executeUpdate(sql);

			for(Team team : teams.values()){

				// Write the project entry
				sql = "INSERT INTO team (id, allocatedProjectId, prctStudentReceivedPreference, avgProjSkillComp, totalSkillShortage) " +
						"VALUES (?,?,?,?,?);";

				stmt = conn.prepareStatement(sql);
				stmt.setString(1, team.getTeamId());
				stmt.setString(2, team.getProjectRef().getId());
				stmt.setDouble(3, team.getPrctStudentReceivedPreference());
				stmt.setDouble(4, team.getAvgProjSkillComp());
				stmt.setDouble(5, team.getTotalSkillShortage());

				stmt.executeUpdate();
			}
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	/**
	 * Inserts projects, project sought skills records to the company table in SQL lite
	 */
	public static void writeProjectsToDatabase(HashMap<String, Project> projects){

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = dbHelper.getDBConnection();

			// Clear existing records
			String deleteProjects = "DELETE FROM projects";
			String deleteProjectSoughtSkills = "DELETE from project_soughtskills";
			String deleteProjectTeamsRel = "DELETE from projects_teams_rel";
			Statement deleteStmt = conn.createStatement();

			deleteStmt.executeUpdate(deleteProjects);
			deleteStmt.executeUpdate(deleteProjectSoughtSkills);
			deleteStmt.executeUpdate(deleteProjectTeamsRel);

			for(Project project : projects.values()){

				// Write the project entry
				String sql = "INSERT INTO projects (id, title, description, projectOwnerId, prefSum) " +
						"VALUES (?,?,?,?,?);";

				stmt = conn.prepareStatement(sql);
				stmt.setString(1, project.getId());
				stmt.setString(2, project.getTitle());
				stmt.setString(3, project.getDescription());
				stmt.setString(4, project.getProjectOwnerId());
				stmt.setInt(5, project.getProjectPrefSum());

				stmt.executeUpdate();

				// Writing project and team relation
				sql = "INSERT INTO projects_teams_rel VALUES(?,?)";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, project.getId());
				stmt.setString(2, (project.getTeamRef() == null) ? "null" : project.getTeamRef().getTeamId());


				// Write project requirements entry
				for(Map.Entry<String, Integer> entry : project.getSoughtSkills().entrySet()){
					stmt = conn.prepareStatement("INSERT INTO project_soughtskills VALUES (?,?,?)");

					stmt.setString(1, project.getId());
					stmt.setString(2, entry.getKey());
					stmt.setInt(3, entry.getValue());

					stmt.executeUpdate();
				}
			}
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}


	/**
	 * Insert teams into the database
	 */
	public static void writeStudentsToDatabase(HashMap<String, Student> studentsList){

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = dbHelper.getDBConnection();

			// Clear existing records
			String sql = "DELETE FROM student";
			Statement deleteStmt = conn.createStatement();
			deleteStmt.executeUpdate(sql);

			// For each student
			for(Student student : studentsList.values()){

				// Write the student entry
				sql = "INSERT INTO student (id, personality, projectId, teamId) " +
						"VALUES (?,?,?,?);";

				stmt = conn.prepareStatement(sql);
				stmt.setString(1, student.getId());
				stmt.setString(2, student.getPersoanlity());
				stmt.setString(3, student.getCurrProjAssoc());
				stmt.setString(4, (student.getCurrTeamAssoc() == null) ? null : student.getCurrTeamAssoc().getTeamId());

				stmt.executeUpdate();

				// Writting cant work with preferences
				for(String cantWorkWithId : student.getCantWorkWith()){
					stmt = conn.prepareStatement("INSERT INTO student_conflicts_rel VALUES (?, ?)");
					stmt.setString(1, student.getId());
					stmt.setString(2, cantWorkWithId);

					stmt.executeUpdate();
				}

				// Write student grades entries
				for(Map.Entry<String, Integer> entry : student.getGrades().entrySet()){
					stmt = conn.prepareStatement("INSERT INTO student_grades_rel VALUES (?,?)");

					stmt.setString(1, student.getId());
					stmt.setString(2, entry.getKey());
					stmt.setInt(3, entry.getValue());

					stmt.executeUpdate();
				}

				// Write student project preferences
				for(Map.Entry<String, Integer> entry : student.getProjPreferences().entrySet()){
					stmt = conn.prepareStatement("INSERT INTO student_grades_rel VALUES (?,?)");

					stmt.setString(1, student.getId());
					stmt.setString(2, entry.getKey());
					stmt.setInt(3, entry.getValue());

					stmt.executeUpdate();
				}
			}
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

}