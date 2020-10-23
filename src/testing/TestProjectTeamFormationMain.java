package testing;

import main.ProjectTeamFormationMain;
import model.entities.Project;
import model.entities.Student;
import model.entities.Team;
import model.exceptions.*;
import org.junit.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 
 */

/**
 * @author Yogeshwar Chaudhari
 *
 */
public class TestProjectTeamFormationMain {

	private static ProjectTeamFormationMain projectTeamFormationMain = null;
	private LinkedHashMap<String, Project> shortListedProjectsList = new LinkedHashMap<>();
	private HashMap<String, Student> studentsList = new HashMap<>();
	LinkedHashMap<String, Team> teamsList = new LinkedHashMap<>();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		projectTeamFormationMain = new ProjectTeamFormationMain();

		// Prepare the testing data.
		loadTestDataForDeviations();

		// Set the required variables so that SD functions can work
		projectTeamFormationMain.setShortListedProjectsList(shortListedProjectsList);
		projectTeamFormationMain.setStudentsList(studentsList);
		projectTeamFormationMain.setTeamsList(teamsList);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/*
	 * Positive case
	 * SD In Skill Competency Across Projects
	 */
	@Test
	public void test_sDInSkillCompetency() {
		
		projectTeamFormationMain.sDInSkillCompetency();
		Assert.assertEquals("Standard Daviation in Skill Competency", 0.272, projectTeamFormationMain.getsDInSkillCompetencyAcrossProj(), 0.001);
	}
	
	
	/*
	 * Positive case
	 * 2 projects in every team received their preference projects, standard daviation calculates to 0 as per the formula
	 */
	@Test
	public void test_sDInProjPrefAllocPrct() {
		
		projectTeamFormationMain.sDInProjPrefAllocPrct();
		Assert.assertEquals("Project Preference Allocation SD : ", 20.0, projectTeamFormationMain.getsDInProjPrefAllocPrct(), 0.01);
	}

	/*
	 * SD in Skill Shortfall Across Team
	 */
	@Test
	public void sDInShortFallAcrossTeam() {
		
		projectTeamFormationMain.sDInShortFallAcrossTeam();
		Assert.assertEquals("Standard Daviation in Skill Shortfall across Teams", 0.48, projectTeamFormationMain.getsDInSkillShortfall(), 0.01);
		
	}


	/**
	 * Prepares the dummy data that is required for performing this test.
	 */
	public void loadTestDataForDeviations() throws ProjectAlreadyAssignedException, InvalidMemberException, RepeatedMemberException, NullProjectException, StudentConflictException, NoLeaderException, ExcessMemberException, StudentNotFoundException {

		shortListedProjectsList.put("Pr1", new Project("Pr1", "Test Project 1", "Pr1 - Sample Project Description", "Own1", 4, 3, 2, 1, 10));
		shortListedProjectsList.put("Pr2", new Project("Pr2", "Test Project 3", "Pr2 - Sample Project Description", "Own2", 3, 4, 2, 1, 9));
		shortListedProjectsList.put("Pr3", new Project("Pr3", "Test Project 3", "Pr3 - Sample Project Description", "Own3", 4, 2, 1, 3, 8));
		shortListedProjectsList.put("Pr4", new Project("Pr3", "Test Project 3", "Pr3 - Sample Project Description", "Own3", 3, 2, 1, 4, 7));
		shortListedProjectsList.put("Pr5", new Project("Pr3", "Test Project 3", "Pr3 - Sample Project Description", "Own3", 4, 3, 1, 2, 6));

		studentsList.put("s1", new Student("s1", 1, 3, 2, 1, "Pr1", "Pr2", "Pr3", "Pr4", "A", "", ""));
		studentsList.put("s2", new Student("s2", 1, 3, 2, 1, "Pr1", "Pr2", "Pr3", "Pr3", "B", "", ""));
		studentsList.put("s3", new Student("s3", 1, 4, 1, 2, "Pr4", "Pr3", "Pr1", "Pr2", "C", "", ""));
		studentsList.put("s4", new Student("s4", 2, 4, 3, 1, "Pr1", "Pr2", "Pr6", "Pr5", "D", "", ""));
		studentsList.put("s5", new Student("s5", 3, 2, 1, 4, "Pr4", "Pr3", "Pr6", "Pr7", "A", "", ""));
		studentsList.put("s6", new Student("s6", 4, 2, 3, 4, "Pr4", "Pr3", "Pr6", "Pr7", "B", "", ""));
		studentsList.put("s7", new Student("s7", 1, 1, 4, 3, "Pr4", "Pr3", "Pr6", "Pr7", "C", "", ""));
		studentsList.put("s8", new Student("s8", 4, 3, 4, 2, "Pr4", "Pr3", "Pr6", "Pr7", "D", "", ""));
		studentsList.put("s9", new Student("s9", 1, 4, 1, 2, "Pr4", "Pr3", "Pr6", "Pr7", "A", "", ""));
		studentsList.put("s10", new Student("s10", 2, 4, 1, 2, "Pr4", "Pr3", "Pr6", "Pr7", "B", "", ""));
		studentsList.put("s11", new Student("s11", 3, 3, 2, 1, "Pr1", "Pr2", "Pr3", "Pr4", "C", "", ""));
		studentsList.put("s12", new Student("s12", 4, 3, 2, 1, "Pr1", "Pr2", "Pr3", "Pr3", "D", "", ""));
		studentsList.put("s13", new Student("s13", 3, 4, 1, 2, "Pr4", "Pr3", "Pr1", "Pr2", "A", "", ""));
		studentsList.put("s14", new Student("s14", 2, 4, 3, 1, "Pr1", "Pr2", "Pr6", "Pr5", "B", "", ""));
		studentsList.put("s15", new Student("s15", 3, 2, 1, 4, "Pr4", "Pr3", "Pr6", "Pr7", "C", "", ""));
		studentsList.put("s16", new Student("s16", 1, 2, 3, 4, "Pr4", "Pr3", "Pr6", "Pr7", "D", "", ""));
		studentsList.put("s17", new Student("s17", 2, 1, 4, 3, "Pr4", "Pr3", "Pr6", "Pr7", "A", "", ""));
		studentsList.put("s18", new Student("s18", 1, 3, 4, 2, "Pr4", "Pr3", "Pr6", "Pr7", "B", "", ""));
		studentsList.put("s19", new Student("s19", 3, 4, 1, 2, "Pr4", "Pr3", "Pr6", "Pr7", "C", "", ""));
		studentsList.put("s20", new Student("s20", 3, 4, 1, 2, "Pr4", "Pr3", "Pr6", "Pr7", "D", "", ""));

		Team tempTeam1 = new Team("T1",shortListedProjectsList.get("Pr1"));
		tempTeam1.addMembers(shortListedProjectsList.get("Pr1"), new Student[] { studentsList.get("s1"), studentsList.get("s2"), studentsList.get("s3"), studentsList.get("s4") });
		teamsList.put( "T1", tempTeam1);

		Team tempTeam2 = new Team("T2",shortListedProjectsList.get("Pr2"));
		tempTeam2.addMembers(shortListedProjectsList.get("Pr2"), new Student[] { studentsList.get("s5"), studentsList.get("s6"), studentsList.get("s7"), studentsList.get("s8") });
		teamsList.put( "T2", tempTeam2);

		Team tempTeam3 = new Team("T3",shortListedProjectsList.get("Pr3"));
		tempTeam3.addMembers(shortListedProjectsList.get("Pr3"), new Student[] { studentsList.get("s12"), studentsList.get("s9"), studentsList.get("s10"), studentsList.get("s11") });
		teamsList.put( "T3", tempTeam3);

		Team tempTeam4 = new Team("T4",shortListedProjectsList.get("Pr4"));
		tempTeam4.addMembers(shortListedProjectsList.get("Pr4"), new Student[] { studentsList.get("s16"), studentsList.get("s13"), studentsList.get("s14"), studentsList.get("s15") });
		teamsList.put( "T4", tempTeam4);

		Team tempTeam5 = new Team("T5",shortListedProjectsList.get("Pr5"));
		tempTeam5.addMembers(shortListedProjectsList.get("Pr5"), new Student[] { studentsList.get("s20"), studentsList.get("s17"), studentsList.get("s18"), studentsList.get("s19") });
		teamsList.put( "T5", tempTeam5);
	}

}
