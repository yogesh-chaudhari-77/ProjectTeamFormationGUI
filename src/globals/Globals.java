package globals;

public class Globals {
	
	// Prefixes for entities IDs starts here
	public static final String projectIdPrefix = "Pr";
	public static final String projectOwnerPrefix = "Own";
	public static final String companyPrefix = "C";
	// Prefixes for entities IDs ends here
	
	// default values starts here
	public static final int defaultSoughtSkill = -1;
	// default values ends here
	
	
	// Menu Options Starts Here
	public static final String OPT_ADD_COMPANY 					= "A";
	public static final String OPT_ADD_PROJ_OWNER 				= "B";
	public static final String OPT_ADD_PROJ 					= "C";
	public static final String OPT_CAPTURE_STUD_PERSONALITIES 	= "D";
	public static final String OPT_ADD_STUD_PREFERENCES 	  	= "E";
	public static final String OPT_SHORTLIST_PROJ 				= "F";
	public static final String FORM_TEAM		 				= "G";
	public static final String DISPLAY_TEAM_FITNESS_METRICS		= "H";
	public static final String CAL_SD							= "I";
	public static final String OPT_QUIT							= "Q";
	// Menu Options Ends Here
	
	
	// Subject Codes
	public static final String PROG_SOFT_ENGG = "P";
	public static final String NETWORK_SECURITY	= "N";
	public static final String ANALYTICS_BIG_DATA = "A";
	public static final String WEB_MOBILE_APP = "W";
	
	public static final String [] SUBJECTS = {"(P) Programming & Software Engineering", "(N) Networking and Security", "(A) Analytics and Big Data", "(W) Web & Mobile applications"};
	
	public static final String LINE_SEPERATOR = "------------------------------------------------------------";
	
	
	// Pattern indentifiers for standard input validations starts here
	public static final String EMAIL_PATT_IDENT = "email";
	public static final String PHONE_PATT_IDENT = "phone";
	public static final String COMP_NAME_PATT_IDENT = "name";
	public static final String ABN_PATT_IDENT = "abn";
	public static final String URL_PATT_IDENT = "weburl";
	public static final String ADDR_PATT_IDENT = "address";
	public static final String COMPANY_ID_IDENT = "companyid";
	public static final String PROJECT_ID_IDENT = "projectid";
	public static final String OWNER_ID_IDENT = "ownerid";
	// Pattern indentifiers for standard input validations ends here
	
	
	// Student constants
	
	// Defines how many conflict persons can be specified by an student
	public static final int CANT_WORK_WITH_ENTRY_COUNT = 2;
	
	// How many project preferences can be specified the student. Latest <number> projects are kept
	public static final int MAX_PROJ_PREF_ALLOWED = 4;
	
	public static final int NUM_PROJ_TOBE_DISCARDED = 5;
	
	// File names for data saving and retrival
	// Maintains the list of students
	public static final String STUDENTS_TXT = "D://datafiles/students.txt";
	
	// Saves the list of companies
	public static final String COMPANIES_TXT = "D://datafiles/companies.txt";
	
	// Keeps the list of all projects
	public static final String PROJECTS_TXT = "D://datafiles/projects.txt";
	
	// Maintains the student Info such as StudentID, Grades, Conflicts, Student Personality etc
	public static final String STUDENTINFO_TXT = "D://datafiles/studentInfo.txt";
	
	// Maintains the list of project preferences user is interested in
	public static final String PREFERENCES_TXT = "D://datafiles/preferences.txt";
	
	// Keep the most popular projects, eliminates the least 5 popular projects
	public static final String POPULAR_PROJECTS_TXT = "D://datafiles/popular_projects.txt";

	// Maintains the list of teams
	public static final String TEAMS_TXT = "D://datafiles/teamsBinary.txt";
}
