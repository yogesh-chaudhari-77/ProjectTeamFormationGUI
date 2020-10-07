package main;

import controller.VisualSensitiveAnalysisController;
import globals.Globals;
import model.entities.*;
import model.exceptions.*;
import utilities.FileHandlingHelper;
import utilities.ScannerUtil;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Main class implementing method calling
 */

public class ProjectTeamFormationMain {

	private boolean quit = false;

	private ScannerUtil scannerUtil = ScannerUtil.createInstance().consoleReader();
	private FileHandlingHelper fileHandler = FileHandlingHelper.init();

	// ArrayLists that maintains each such entities
	private HashMap<String, Company> companiesList;
	private HashMap<String, ProjectOwner> projectOwnersList;
	private HashMap<String, Project> projectsList;

	// Stores the subset of projects list, favoured projects
	private LinkedHashMap<String, Project> shortListedProjectsList;
	private HashMap<String, Student> studentsList;

	// 18-08-2020 - MileStone 2
	private LinkedHashMap<String, Team> teamsList;

	// SD In Skill Competency Across Projects
	private double sDInSkillCompetencyAcrossProj = 0.0;

	// SD in Project Preferences Allocation Percentage 
	private double sDInProjPrefAllocPrct = 0.0;

	//SD in Skill Shortfall Across Team
	private double sDInSkillShortfall = 0.0;

	// Controller ref
	VisualSensitiveAnalysisController controllerRef = null;

	public ProjectTeamFormationMain() {

		companiesList = new HashMap<String, Company>();
		projectOwnersList = new HashMap<String, ProjectOwner>();
		projectsList = new HashMap<String, Project>();
		shortListedProjectsList = new LinkedHashMap<String, Project>();
		studentsList = new HashMap<String, Student>();
		teamsList = new LinkedHashMap<String, Team>();
	}


	// Mapping of operation with numbers using switch statement
	public void executeOperation(String userChoice) {

		switch (userChoice) {

		case Globals.OPT_ADD_COMPANY: 						// A
			Company tempComp = this.addCompany();
			if (tempComp != null) {

				// Add newly created company to companies list
				this.companiesList.put(tempComp.getId(), tempComp);
				System.out.println("Company has been added.");
				System.out.println(tempComp.toString());

				// Write newly added record to the companies file.
				this.fileHandler.setFile(Globals.COMPANIES_TXT, "append");
				System.out.println(fileHandler.writeOp(tempComp.getWriteFormattedRecord()));
				this.fileHandler.destroy();
			}
			break;

		case Globals.OPT_ADD_PROJ_OWNER: 					// B

			ProjectOwner retProjOwner = this.addProjectOwner();
			if (retProjOwner != null) {

				// Add newly created project owner to project owner's list
				this.projectOwnersList.put(retProjOwner.getProjOwnerId(), retProjOwner);
				System.out.println("Project Owner Has Been Added");
			}
			break;

		case Globals.OPT_ADD_PROJ: 							// C
			Project tempProj = this.addProject();
			if (tempProj != null) {

				// Add newly created project to the projects list
				this.projectsList.put(tempProj.getId(), tempProj);
				System.out.println("Project has been added to the system\n" + tempProj.toString());
				DataSaverRetrieval.writeProjectsFile(this.getProjectsList());
			}
			break;

		case Globals.OPT_CAPTURE_STUD_PERSONALITIES: 		// D

			// Captures the student personalities such as A, B, C, D
			this.captuerStudentPersonalities();

			// Capture the student cant work preferences
			this.captureCantWorkWith();

			// Save data into txt files
			DataSaverRetrieval.writeStudentInfoFile(this.getStudentsList());
			break;

		case Globals.OPT_ADD_STUD_PREFERENCES: 				// E

			// Capture student's project preferences
			this.captureStudentPreferences();
			DataSaverRetrieval.writeStudentPreferences(this.getStudentsList());
			break;

		case Globals.OPT_SHORTLIST_PROJ: 					// F

			// Score the individual projects popularity based on all student's preferences
			this.calProjectsPrefSum();

			// Discard (total num of students / 4) projects from projects list

			this.setShortListedProjectsList( this.discardLeastPopularProj() );

			// Export this shortlisted projects to the popular_projects file
			DataSaverRetrieval.writeShortListedProjectsFile(this.getShortListedProjectsList());

			break;

		case Globals.FORM_TEAM: 					// G
			try {
				this.formTeamManually();
			} catch (StudentNotFoundException e) {
				e.printStackTrace();
			} catch (NoLeaderException e) {
				e.printStackTrace();
			} catch (NullProjectException e) {
				e.printStackTrace();
			}
			break;

		case Globals.DISPLAY_TEAM_FITNESS_METRICS: 	// H
			// Input handling to be done in Menu class. 
			// Hence moved to Menu class
			break;

		case Globals.CAL_SD:						// I
			sDInSkillCompetency();
			System.out.println("SD In Skill Competency Across Projects : "+this.getsDInSkillCompetencyAcrossProj());
			sDInProjPrefAllocPrct();
			System.out.println("SD in Project Preferences Allocation Percentage : "+this.getsDInProjPrefAllocPrct());
			sDInShortFallAcrossTeam();
			System.out.println("SD in Skill Shortfall Across Team "+this.getsDInSkillShortfall());
			break;

		case Globals.OPT_QUIT: 						// Q

			this.saveDataToFiles();
			this.saveDataToDatabase();
			this.setQuit(true);
			break;

		default:
			System.err.println("Please select from above options");
		}
	}

	/*
	 * Creates a new company projects
	 */

	public Company addCompany() {
		Company tempComp = null;

		String id = scannerUtil.readString("Company ID : ");
		String companyName = scannerUtil.readString(Globals.COMP_NAME_PATT_IDENT, "Name : ", "");
		String companyABN = scannerUtil.readString(Globals.ABN_PATT_IDENT, "ABN : ", "");
		String companyURL = scannerUtil.readString(Globals.URL_PATT_IDENT, "URL : ", "");
		String companyAddress = scannerUtil.readString("Address : ");

		tempComp = new Company(id, companyName, companyABN, companyURL, companyAddress);

		return tempComp;
	}

	// Creates new project owner and returns it
	public ProjectOwner addProjectOwner() {

		ProjectOwner tempProjectOwner = null;

		String id = scannerUtil.readString("Project Owner ID : ");
		String firstName = scannerUtil.readString("First Name : ");
		String surname = scannerUtil.readString("Surname : ");
		String role = scannerUtil.readString("Role : ");
		String email = scannerUtil.readString("Email : ");

		// Print the list of all available companies
		System.out.println(companiesList.keySet());

		String companyId = scannerUtil.readString("Company ID : ");

		// validate the entered company ID
		while (!this.getCompaniesList().containsKey(companyId)) {
			System.err.println(companyId + " is unavailable. Please enter company ID from above list.");
			companyId = scannerUtil.readString("Company ID : ");
		}

		tempProjectOwner = new ProjectOwner(id, firstName, surname, role, email, companyId);

		return tempProjectOwner;
	}

	/*
	 * Creates new project and returns it
	 */

	public Project addProject() {

		Project tempProj = null;

		String id = scannerUtil.readString("Project ID : ");
		String title = scannerUtil.readString("Title : ");
		String description = scannerUtil.readString("Description : ");

		// Show exisiting projects
		System.out.println(this.getProjectOwnersList().keySet());

		String projectOwnerId = scannerUtil.readString("Project Owner ID : ");

		// Validate supplied project owner ID
		while (!this.getProjectOwnersList().containsKey(projectOwnerId)) {
			System.err.println(projectOwnerId + " is unavailable. Please enter Owner ID from above list.");
			projectOwnerId = scannerUtil.readString("Project Owner ID : ");
		}

		System.out.println(
				"You are required to submit the ranking of the skills sought after for this project in following technical specialization [1-4]");
		System.out.println(Arrays.asList(Globals.SUBJECTS));
		System.out.println(Globals.LINE_SEPERATOR);

		HashMap<String, Integer> soughtSkills = this.captureRequiredSkills();

		tempProj = new Project(id, title, description, projectOwnerId);
		tempProj.setSoughtSkills(soughtSkills);

		return tempProj;
	}

	/*
	 * Captures the skills sought by project. part of creating project function
	 * returns the captures skilled preferences
	 */
	private HashMap<String, Integer> captureRequiredSkills() {

		HashMap<String, Integer> soughtSkills = new HashMap<String, Integer>();
		boolean accepted = false;
		do {
			int pRank = scannerUtil.readInt("Programming & Software Engineering");
			soughtSkills.put(Globals.PROG_SOFT_ENGG, pRank);

			int nRank = scannerUtil.readInt("Networking and Security");
			soughtSkills.put(Globals.NETWORK_SECURITY, nRank);

			int aRank = scannerUtil.readInt("Analytics and Big Data");
			soughtSkills.put(Globals.ANALYTICS_BIG_DATA, aRank);

			int wRank = scannerUtil.readInt("Web & Mobile Applications");
			soughtSkills.put(Globals.WEB_MOBILE_APP, wRank);

			// Shortest way of checking if all elements are uniue or not -
			// https://www.geeksforgeeks.org/check-if-all-array-elements-are-distinct/
			Set<Integer> preference = new HashSet<Integer>(soughtSkills.values());

			// same size means all elements are unique
			if (preference.size() == soughtSkills.size()) {
				accepted = true;
			} else {
				System.out.println("Duplicate preference are not allowed");
			}

		} while (!accepted);

		return soughtSkills;
	}

	// Capture the student's project preferences
	public boolean captureStudentPreferences() {

		System.out.println(this.getStudentsList().keySet());
		String studentId = scannerUtil.readString("Please enter student ID");

		while (!this.getStudentsList().containsKey(studentId)) {
			System.err.println(studentId + " is not a valid student ID. Please enter student ID from above list.");
		}

		Student stuRef = this.studentsList.get(studentId);
		System.out.println("Capturing Project Preference For : " + studentId);

		if (stuRef != null) {

			HashMap<String, Integer> tempPreferences = new HashMap<String, Integer>();

			int i = 0;
			// Ideally we need to capture preferences of 4 projects
			while (i < 4) {

				String projId = scannerUtil.readString("[" + i + "/4] Enter Project ID : ");
				int rank = scannerUtil.readInt("Preference [1-4] : ");

				// If the projectID is already present, then update the preference of that
				// project.
				if (tempPreferences.containsKey(projId)) {
					tempPreferences.put(projId, rank);
					System.out.println("Preference Has Been Updated.");
					continue;
				} else {

					// Otherwise add new entry
					tempPreferences.put(projId, rank);
				}

				i += 1;
			}

			stuRef.setProjPreferences(tempPreferences);

		} else {
			System.err.println("Could not find any student with ID " + studentId);
			return false;
		}

		// Create studentsInfo file to save data - S1 P 4 N 3 A 2 W 1 B S3 S17
		DataSaverRetrieval.writeStudentInfoFile(this.getStudentsList());

		return true;
	}

	// Every project owner can assing the personalities to student
	public void captuerStudentPersonalities() {

		// ProjectOwner actingProjOwner = this.getProjectOwnersList().get("Own1");
		// System.out.println("Capturing details as "+actingProjOwner.getProjOwnerId());

		ArrayList<Integer> personalityQuota = calPersonalityQuotas();

		for (String studentId : this.getStudentsList().keySet()) {

			// Capture personality
			System.out.print("Capturing For " + studentId + " : ");
			String personalityType = scannerUtil.readString("Enter Personality Type [A-D]: ");

			// Only 4 personality types are allowed which are to be equally divided amoung
			// the students
			boolean status = checkQuota(personalityQuota, personalityType);

			while (!status) {
				personalityType = scannerUtil
						.readString("Capturing For " + studentId + " : Enter Personality Type [A-D]: ");
				status = checkQuota(personalityQuota, personalityType);
			}

			// Add a separate copy to project owner
			this.getStudentsList().get(studentId).setPersoanlity(personalityType);
		}

		// Create studentsInfo file to save data
		DataSaverRetrieval.writeStudentInfoFile(this.getStudentsList());
	}

	// Calculates the available for quota left for associations for each personality
	// type.
	// For 20 students it will be 5,5,5,5 for 40 students it will be 10 => A, 10 =>
	// B, 10 => C, 10 => D
	public ArrayList<Integer> calPersonalityQuotas() {
		int totalStudents = this.getStudentsList().keySet().size();

		ArrayList<Integer> allowedAssocs = new ArrayList<Integer>();

		for (int i = 0; i < 4; i++) {
			allowedAssocs.add(totalStudents / 4);
		}

		return allowedAssocs;
	}

	/*
	 * Check if the assigned personality has any quota left Since personality types
	 * are limited to 4, the quota for each personality is alwasy = num(students)/4
	 * It makes sure that user is not trying to overboard with any single
	 * personality type
	 */
	public boolean checkQuota(ArrayList<Integer> personalityQuota, String personalityType) {

		if (personalityType.contentEquals("A")) {
			if (personalityQuota.get(0) > 0) {
				personalityQuota.set(0, personalityQuota.get(0) - 1);
			} else {
				System.out.println("Out of Quota - Personality Type A");
				return false;
			}
		} else if (personalityType.contentEquals("B")) {
			if (personalityQuota.get(1) > 0) {
				personalityQuota.set(1, personalityQuota.get(1) - 1);
			} else {
				System.out.println("Out of Quota - Personality Type B");
				return false;
			}
		} else if (personalityType.contentEquals("C")) {
			if (personalityQuota.get(2) > 0) {
				personalityQuota.set(2, personalityQuota.get(2) - 1);
			} else {
				System.out.println("Out of Quota - Personality Type C");
				return false;
			}
		} else if (personalityType.contentEquals("D")) {
			if (personalityQuota.get(3) > 0) {
				personalityQuota.set(3, personalityQuota.get(3) - 1);
			} else {
				System.out.println("Out of Quota - Personality Type D");
				return false;
			}
		} else {
			System.out.println("Only A, B, C, D personality types are allowed");
			return false;
		}

		return true;
	}

	/*
	 * Student can specify 2 other students with whom he/she cant work with Student
	 * can't specify his/her own ID Student can specifity upto max 2 students
	 */
	public void captureCantWorkWith() {

		System.out.println("Cant work with details. \nType ! and Press enter to quit.");
		String captureForId = scannerUtil.readString("Capture for Student ID :");

		if (this.getStudentsList().containsKey(captureForId)) {
			Student studentRef = this.studentsList.get(captureForId);

			String studentIds = scannerUtil.readString("Enter Student IDs [space separated] : ");

			String[] studentIdsArr = studentIds.trim().split(" ");

			// Even if user enters 5 space seperated IDs, system will only accept last 2 as
			// implementation of addCantWorkWithStudent()
			for (int i = 0; i < studentIdsArr.length; i++) {

				// Find if the ID is valid and Self Id is not given
				if (this.getStudentsList().containsKey(studentIdsArr[i])
						&& !studentRef.getId().equalsIgnoreCase(studentIdsArr[i])) {

					// Find if the can't work with already contains that ID
					if (!studentRef.getCantWorkWith().contains(studentIdsArr[i])) {
						studentRef.addCantWorkWithStudent(studentIdsArr[i]);
					} else {
						System.err.println(studentIdsArr[i] + " is already present. Updated");
					}
				} else {
					System.err.println("Invalid Student ID : " + studentIdsArr[i]);
				}
			}

			if (studentIdsArr.length == 0) {
				System.err.println("You did not provide any preference. Assumed as skipped step.");
			}

		} else {
			System.err.println("Invalid Student ID");
		}

		// Provide option to do it for other students
		String yesNo = scannerUtil.readYesNo(" Try Again [Y/N] ?");
		if (yesNo.equalsIgnoreCase("Y")) {
			this.captureCantWorkWith();
		}

		// Create/Overwrite studentsInfo file to save data
		DataSaverRetrieval.writeStudentInfoFile(this.getStudentsList());

	}

	/*
	 * Sums the project preferences of each project by each student Assigns that
	 * score to project Important part of short listing project
	 */

	public void calProjectsPrefSum() {

		HashMap<String, Student> studentListRef = this.getStudentsList();
		HashMap<String, Project> projListRef = this.getProjectsList();

		// Clear the current score of each project
		projListRef.forEach((String, Project) -> Project.setProjectPrefSum(0));

		// Visit each students
		for (Student studentRef : studentListRef.values()) {

			// Visit each project rated by student
			for (String projId : studentRef.getProjPreferences().keySet()) {

				// Find that project in ProjectsList and update the score
				if (projListRef.containsKey(projId)) {

					int currProjScore = projListRef.get(projId).getProjectPrefSum();
					int studentPrefScore = studentRef.getProjPreferences().get(projId);

					projListRef.get(projId).setProjectPrefSum(currProjScore + studentPrefScore);

				} else {
					System.err.println("There is no project with ID :" + projId);
				}

			}

		}
	}

	/*
	 * Sorts the projects in descending order of projPopularity sum
	 */
	public LinkedHashMap<String, Project> discardLeastPopularProj() {

		// Before Filtering
		for(Project p : this.projectsList.values()) {
			System.out.println(p.getId() + " " + p.getProjectPrefSum());
		}
		
		// Do not reorder the original list
		HashMap<String, Project> copyOfProjs = (HashMap<String, Project>) this.getProjectsList().clone();

		// [9] - sort the projects based on score they received, then limit the projects to num(students)/4 as per project can have max of 4 students
		LinkedHashMap<String, Project> sortedProjs = copyOfProjs.entrySet().stream()

				.sorted((proj1, proj2) -> Integer.compare(proj2.getValue().getProjectPrefSum(),
						proj1.getValue().getProjectPrefSum()))

				.limit( this.getStudentsList().size() / 4 )

				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (proj1, proj2) -> proj1,
						LinkedHashMap::new));

		
		// Print each project
		System.out.println("After Filtering");
		sortedProjs.forEach((String, Project) -> System.out.println(Project.getId() + " " + Project.getProjectPrefSum()));

		return sortedProjs;
	}

	/*
	 * Reads the text files having data and load the appropriate lists
	 */
	public void loadDataFromFiles() {

		// Read Students Data
		this.setStudentsList(DataSaverRetrieval.readStudentsFile());
		DataSaverRetrieval.readStudentPreferencesFile(this.getStudentsList());
		DataSaverRetrieval.readStudentInfoFile(this.getStudentsList());

		// DataSaverRetrieval.readStudentInfoFromDatabase(this.getStudentsList());

		this.getStudentsList().values().forEach((student) -> System.out.println(student));

		// Read companies file, load the companies list, and print on console
		this.setCompaniesList(DataSaverRetrieval.readComapniesFile());
		this.getCompaniesList().values().forEach((company) -> System.out.println(company));

		// Read project file, load projects list, print it on console
		this.setProjectsList(DataSaverRetrieval.readProjectsFile());

		// Main separate hashmasp for project owners for quick reading
		this.setProjectOwnersList(new HashMap<String, ProjectOwner>());

		this.getProjectsList().values().forEach((project) -> {
			System.out.println(project);
			String ownerId = project.getProjectOwnerId();
			this.getProjectOwnersList().put(ownerId, new ProjectOwner(ownerId));
		});

		this.calProjectsPrefSum();
		
		// Get the shortlisted projects
		this.setShortListedProjectsList( this.discardLeastPopularProj() );

		// Milestone 2 - Read teams serialised files
		this.setTeamsList( main.DataSaverRetrieval.readTeamsFile() );

		// Milestone 4 - Updated for reading from database
		// this.setTeamsList( DataSaverRetrieval.readTeamsFromDatabase( this.getProjectsList(), this.getStudentsList()) );

		// For each team
		this.getTeamsList().forEach( (teamId, team) -> {

			// Updating the student current project allocation as well
			if(team.getProjectRef().getId() != null){
				this.getProjectsList().get( team.getProjectRef().getId() ).setTeamRef( team );

				team.getMembers().keySet().forEach((studentId)->{
					this.getStudentsList().get(studentId).setCurrProjAssoc( team.getProjectRef().getId());
					this.getStudentsList().get(studentId).setCurrTeamAssoc(team);
				});
			}
		});

		System.out.println(this.getTeamsList());
		System.out.println("Team Size : "+this.getTeamsList().size());
	}


	public void saveDataToFiles() {

		// Saving operations begin here

		this.controllerRef.statusbarProgress.setProgress(1/7);
		DataSaverRetrieval.writeCompaniesFile(this.getCompaniesList());

		this.controllerRef.statusbarProgress.setProgress(2/7);
		DataSaverRetrieval.writeProjectsFile(this.getProjectsList());

		this.controllerRef.statusbarProgress.setProgress(3/7);
		DataSaverRetrieval.writeStudentsFile(this.getStudentsList());

		this.controllerRef.statusbarProgress.setProgress(4/7);
		DataSaverRetrieval.writeStudentInfoFile(this.getStudentsList());

		this.controllerRef.statusbarProgress.setProgress(5/7);
		DataSaverRetrieval.writeStudentPreferences(this.getStudentsList());

		this.calProjectsPrefSum();
		this.setShortListedProjectsList(this.discardLeastPopularProj());

		this.controllerRef.statusbarProgress.setProgress(6/7);
		DataSaverRetrieval.writeShortListedProjectsFile(this.getShortListedProjectsList());

		this.controllerRef.statusbarProgress.setProgress(7/7);
		DataSaverRetrieval.writeTeamsFile(this.getTeamsList());

	}

	public void saveDataToDatabase(){

		// Saving data to database

		this.controllerRef.statusbarProgress.setProgress(1/7);
		DataSaverRetrieval.writeTeamsFile(this.getTeamsList());

		this.controllerRef.statusbarProgress.setProgress(2/7);
		DataSaverRetrieval.writeCompaniesToDatabase(this.getCompaniesList());

		this.controllerRef.statusbarProgress.setProgress(3/7);
		DataSaverRetrieval.writeProjectOwnerToDatabase(this.getProjectOwnersList());

		this.controllerRef.statusbarProgress.setProgress(4/7);
		DataSaverRetrieval.writeTeamsToDatabase(this.getTeamsList());

		this.controllerRef.statusbarProgress.setProgress(5/7);
		DataSaverRetrieval.writeProjectsToDatabase(this.getProjectsList());

		this.controllerRef.statusbarProgress.setProgress(6/7);
		DataSaverRetrieval.writeStudentsToDatabase(this.getStudentsList());

		this.controllerRef.statusbarProgress.setProgress(7/7);
		DataSaverRetrieval.writeTeamsMembersToDatabase(this.getTeamsList());
	}

	// 18-08-2020 - MileStone 2 - Forming team
	public void formTeamManually() throws StudentNotFoundException, NoLeaderException, NullProjectException {

		Team tempTeam = new Team();

		// Showing the projects IDs to choose from
		System.out.println(this.getShortListedProjectsList().keySet());

		String projId = scannerUtil.readString("Project ID: ");
		String studentIds = scannerUtil.readString("Student Ids [s1 s2 s3 s4] (Space seperated): ");

		Project projRef = this.getShortListedProjectsList().get(projId);

		String[] studentIdsArr = studentIds.split(" ");
		Student[] selectedStudents = new Student[studentIdsArr.length];

		for (int i = 0; i < studentIdsArr.length; i++) {

			Student studentRef = this.studentsList.get(studentIdsArr[i]);
			if (studentRef == null) {
				System.out.println(studentIdsArr[i]);
			}
			selectedStudents[i] = studentRef;
		}

		try {
			tempTeam.addMembers(projRef, selectedStudents);

			// Update the association of team with project
			projRef.setTeamRef(tempTeam);

			/*
			 * Calculating the metrics info
			 */
			tempTeam.computeAvgSkillPerCategory();
			tempTeam.computeAvgSkillForProject();
			tempTeam.computeCategorySkillShortage();
			tempTeam.computeOverallSkillShortage();
			tempTeam.computePreferenceAllocPct();

			// Add reference to the teams lists
			tempTeam.setTeamId(projId.replace("Pr", "T"));

			this.getTeamsList().put(tempTeam.getTeamId(), tempTeam);

			System.out.println(this.getTeamsList().keySet());


			// We need to freshly write the teams list back to file
			DataSaverRetrieval.writeTeamsFile(this.teamsList);

		} catch (InvalidMemberException | RepeatedMemberException | StudentConflictException | ProjectAlreadyAssignedException | ExcessMemberException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Text taken from assignment specification
	 * Team-Fitness Metrics A : Average student skill competency for each project
	 * team B : Percentage of students who got their first and second preferences in
	 * each of the teams. C : Skills shortfall for each project based on categories
	 * in which the average grade for a skill category Referenced from the
	 * assignment specification
	 */
	public void teamFitnessMetricOperations(String userChoice) {

		switch (userChoice) {

			// Average student skill competency for each project
			case "A": {

				for (Team teamRef : this.getTeamsList().values()) {
					System.out.println(teamRef.getTeamId() + " |-> " + teamRef.getProjectRef().getId() + " -> "
							+ teamRef.getAvgProjSkillComp());
				}

			}
			break;

			// Percentage of students who got their first and second (Numerically 3, 4) preferences in each of the teams
			case "B": {

				for (Team teamRef : this.getTeamsList().values()) {
					System.out.println(teamRef.getTeamId() + " |-> " + teamRef.getProjectRef().getId() + " -> "
							+ teamRef.getPrctStudentReceivedPreference());
				}

			}
			break;

			// Skills shortfall for each project based on categories in which the average grade for a skill category - Referenced from the assignment specification
			case "C": {

				for (Team teamRef : this.getTeamsList().values()) {
					System.out.println(teamRef.getTeamId() + " |-> " + teamRef.getProjectRef().getId());

					for (String subId : teamRef.getAvgCategorySkillShortage().keySet()) {
						if (teamRef.getAvgCategorySkillShortage().get(subId) < 0) {
							System.out.println(subId + " |-> " + teamRef.getAvgCategorySkillShortage().get(subId));
						}
					}
				}

			}
			break;

			case "Q" :{
				// Do nothing
			}
			break;

			default:
				System.err.println("Wrong option. Please enter option from the menu");
		}
	}

	/*
	 * 24-08-2020 - Standard deviation in skill competency
	 */

	public void sDInSkillCompetency() {

		// Sum the skill competency of all projects for calculating mean
		double sumSkillCompetency = 0;

		// Number of teams
		int numOfTeams = this.teamsList.size();

		// Sum / numberOfTeams - mean of skill competency
		double meanSkillCompetency = 0;

		// Iterate over each team member to get the sum of skill competency of all projects
		for (Team team : this.teamsList.values()) {
			sumSkillCompetency += team.getAvgProjSkillComp();
		}

		meanSkillCompetency = sumSkillCompetency / numOfTeams;

		// Calculating the SD here
		double temp = 0;
		for (Team team : this.teamsList.values()) {
			temp += Math.pow((team.getAvgProjSkillComp() - meanSkillCompetency), 2); 
		}

		double sd = Math.sqrt((temp/numOfTeams));

		this.setsDInSkillCompetencyAcrossProj(sd);

		//this.controllerRef.update();
	}


	/*
	 * 24-08-2020 - Standard deviation for percentage of project members getting first and second project preferences across
					projects
	 */

	public void sDInProjPrefAllocPrct() {

		// Sum the skill competency of all projects for calculating mean
		double sumPrefPrct = 0;

		// Number of teams
		int numOfTeams = this.teamsList.size();

		// Sum / numberOfTeams - mean of skill competency
		double meanPrefPrct = 0;

		// Iterate over each team member to get the sum of preference allocation in all projects
		for (Team team : this.teamsList.values()) {
			sumPrefPrct += team.getPrctStudentReceivedPreference();
		}

		meanPrefPrct = sumPrefPrct / numOfTeams;

		// Calculating the SD here
		double temp = 0;
		for (Team team : this.teamsList.values()) {
			temp += Math.pow((team.getPrctStudentReceivedPreference() - meanPrefPrct), 2); 
		}


		double sd = Math.sqrt((temp/numOfTeams));

		this.setsDInProjPrefAllocPrct(sd);
	}


	/*
	 * 24-08-2020 - Standard deviation of shortfall across teams
	 */

	public void sDInShortFallAcrossTeam() {

		// Sum the skill competency of all projects for calculating mean
		double sumShortFall = 0;

		// Number of teams
		int numOfTeams = this.teamsList.size();

		// Sum / numberOfTeams - mean of skill competency
		double meanShortFall = 0;

		// Iterate over each team member to get the sum of skill competency of all projects
		for (Team team : this.teamsList.values()) {
			sumShortFall += team.getTotalSkillShortage();
		}

		meanShortFall = sumShortFall / numOfTeams;

		// Calculating the SD here
		double temp = 0;
		for (Team team : this.teamsList.values()) {
			temp += Math.pow(( team.getTotalSkillShortage() - meanShortFall), 2); 
		}

		double sd = Math.sqrt((temp/numOfTeams));

		this.setsDInSkillShortfall(sd);
	}

	// Getter-Setter Starts Here

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	public ScannerUtil getScannerUtil() {
		return scannerUtil;
	}

	public void setScannerUtil(ScannerUtil scannerUtil) {
		this.scannerUtil = scannerUtil;
	}

	public FileHandlingHelper getFileHandler() {
		return fileHandler;
	}

	public void setFileHandler(FileHandlingHelper fileHandler) {
		this.fileHandler = fileHandler;
	}

	public HashMap<String, Company> getCompaniesList() {
		return companiesList;
	}

	public void setCompaniesList(HashMap<String, Company> companiesList) {
		this.companiesList = companiesList;
	}

	public HashMap<String, ProjectOwner> getProjectOwnersList() {
		return projectOwnersList;
	}

	public void setProjectOwnersList(HashMap<String, ProjectOwner> projectOwnersList) {
		this.projectOwnersList = projectOwnersList;
	}

	public HashMap<String, Project> getProjectsList() {
		return projectsList;
	}

	public void setProjectsList(HashMap<String, Project> projectsList) {
		this.projectsList = projectsList;
	}

	public HashMap<String, Student> getStudentsList() {
		return studentsList;
	}

	public void setStudentsList(HashMap<String, Student> studentsList) {
		this.studentsList = studentsList;
	}

	public HashMap<String, Team> getTeamsList() {
		return teamsList;
	}

	public void setTeamsList(LinkedHashMap<String, Team> teamsList) {
		this.teamsList = teamsList;
	}



	// Milestone 2 - 

	public LinkedHashMap<String, Project> getShortListedProjectsList() {
		return shortListedProjectsList;
	}

	public void setShortListedProjectsList(LinkedHashMap<String, Project> shortListedProjectsList) {
		this.shortListedProjectsList = shortListedProjectsList;
	}

	public double getsDInSkillCompetencyAcrossProj() {
		return sDInSkillCompetencyAcrossProj;
	}

	public void setsDInSkillCompetencyAcrossProj(double sDInSkillCompetencyAcrossProj) {
		this.sDInSkillCompetencyAcrossProj = sDInSkillCompetencyAcrossProj;
	}

	public double getsDInProjPrefAllocPrct() {
		return sDInProjPrefAllocPrct;
	}

	public void setsDInProjPrefAllocPrct(double sDInProjPrefAllocPrct) {
		this.sDInProjPrefAllocPrct = sDInProjPrefAllocPrct;
	}

	public double getsDInSkillShortfall() {
		return sDInSkillShortfall;
	}

	public void setsDInSkillShortfall(double sDInSkillShortfall) {
		this.sDInSkillShortfall = sDInSkillShortfall;
	}

	// Getter-Setters Ends Here

	/**
	 * Setting controller for this class
	 * Controller is : VisualAnalysisController
	 */

	public void setController(VisualSensitiveAnalysisController controllerRef){
		this.controllerRef = controllerRef;
	}
}