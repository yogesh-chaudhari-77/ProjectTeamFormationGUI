package controller;

import globals.Globals;
import model.entities.Company;
import model.entities.Project;
import model.entities.Student;
import model.entities.Team;
import utilities.FileHandlingHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/*
 * Java class that implements methods for reading and writting data from/to files
 * 
 */

public class DataSaverRetrieval {

	// Singleton reference of the fileHandling
	private static FileHandlingHelper fileHandler = FileHandlingHelper.init();
	
	
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
}