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
 * - Metric related skill Tests -
 * 1) test_totalSkillShortage
 * 2) test_prctStudentReceivedPreference
 * 3) test_projSkillCompetency
 */

/**
 * @author Yogeshwar Chaudhari
 */
public class TestTeam {

	ProjectTeamFormationMain pjT = null;

	private LinkedHashMap<String, Project> shortListedProjectsList = new LinkedHashMap<>();
	private HashMap<String, Student> studentsList = new HashMap<>();
	
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

		pjT = new ProjectTeamFormationMain();
		loadTestData();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	
	/*
	 *  Positive test case 
	 *  Team will be formed as expected 
	 *  No exception thrown
	 */
	@Test
	public void testAddMembers_Positive() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
				
		Project projRef = this.getShortListedProjectsList().get("Pr3");
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s4")};
		
		Team tempTeam = new Team();
		
		// Initial members size - This is before the members has been added to the team
		Assert.assertEquals("Members size should be 0", 0, tempTeam.getMembers().size());

		tempTeam.addMembers(projRef, selectedStudents);

		// Testing if the new team has been added to the list
		Assert.assertEquals("Members size should be 4", 4, tempTeam.getMembers().size());
	}
	
	
	/*
	 * Positive test case
	 * Calculates the total skill shortage for the project for the team
	 */
	@Test
	public void test_totalSkillShortage() throws NoLeaderException, ProjectAlreadyAssignedException, InvalidMemberException, RepeatedMemberException, NullProjectException, StudentConflictException, ExcessMemberException, StudentNotFoundException {
		
		// Forming new team T3 for the project Pr3 with s1, s2,s3, s4 students
		Team tempTeam = new Team("T3", this.getShortListedProjectsList().get("Pr3"));
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s4")};
		tempTeam.addMembers(this.getShortListedProjectsList().get("Pr3"), selectedStudents);

		tempTeam.computeAvgSkillPerCategory();		// Prerequisites
		tempTeam.computeCategorySkillShortage();	// Required skill shortage - getAvgCategorySkillComp for every skill
		tempTeam.computeOverallSkillShortage();  	// Summation of all negative values - that indicates the skill shortage
		
		// Returns absolute value
		Assert.assertEquals("This value indicates the summation of shortfall of the skills in all areas", 2.5, tempTeam.getTotalSkillShortage(), 0.1);
	}
	
	
	/*
	 * Positive test case
	 * Calculates how many students received their first and second preferences in percentage amount
	 * 2 Students put Pr3 as 1 and 2 preference. Hence preference percentage is 50% for this team
	 */
	@Test
	public void test_prctStudentReceivedPreference() throws NoLeaderException, ProjectAlreadyAssignedException, InvalidMemberException, RepeatedMemberException, NullProjectException, StudentConflictException, ExcessMemberException, StudentNotFoundException {

		Team tempTeam = new Team("T3", this.getShortListedProjectsList().get("Pr3"));
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s4")};
		tempTeam.addMembers(this.getShortListedProjectsList().get("Pr3"), selectedStudents);

		tempTeam.updateStatistics();
		
		Assert.assertEquals("This value indicates the percentage of students who got first and second preferences", 50.0, tempTeam.getPrctStudentReceivedPreference(), 0.0001);
	}

	@Test
	public void test_projSkillCompetency() throws NoLeaderException, ProjectAlreadyAssignedException, InvalidMemberException, RepeatedMemberException, NullProjectException, StudentConflictException, ExcessMemberException, StudentNotFoundException {

		Team tempTeam = new Team("T3", this.getShortListedProjectsList().get("Pr3"));
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s4")};
		tempTeam.addMembers(this.getShortListedProjectsList().get("Pr3"), selectedStudents);

		tempTeam.updateStatistics();

		Assert.assertEquals("This value indicates the percentage of students who got first and second preferences", 2.5, tempTeam.getAvgProjSkillComp(), 0.0001);
	}
	
	/*
	 * Negative Test Case
	 * If student has been allocated, then throw an exception if an attempt is made to add that student to another project without clearing previous project allocation
	 */
	@Test (expected = InvalidMemberException.class)
	public void testAddMembers_StudentAlreadyAssigned() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
				
		// Allocating s1 to Pr3
		Project projRef = this.getShortListedProjectsList().get("Pr3");
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s4")};
		
		Team tempTeam = new Team();
		tempTeam.addMembers(projRef, selectedStudents);
		// Team formed successfully.
		
		// Allocating members for Pr4
		projRef = this.getShortListedProjectsList().get("Pr1");
		Team tempTeam2 = new Team();
		
		tempTeam2.addMembers(projRef, selectedStudents);
	}


	/*
	 * Negative test case
	 * The provided students have conflicts amoung them
	 * Expects studentConflictException while iteration
	 * Case 1 - Existing member has conflict with new member.
	 * s1 has conflicts with s6 but s6 does not having conflicts with anyone.
	 */
	@Test (expected = StudentConflictException.class)
	public void testAddMembers_StudentConflict() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
				
		Project projRef = this.getShortListedProjectsList().get("Pr3");
		//
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s6"), this.getStudentsList().get("s3"), this.getStudentsList().get("s5")};

		Team tempTeam = new Team();
		
		tempTeam.addMembers(projRef, selectedStudents);
	}


	/**
	 * Case 2 - New member has conflict with existing memeber
	 */
	@Test (expected = StudentConflictException.class)
	public void testAddMembers_StudentConflict2() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {

		Project projRef = this.getShortListedProjectsList().get("Pr3");

		// s1 is existing memeber, s7 has conflicts with s1
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s7"), this.getStudentsList().get("s3"), this.getStudentsList().get("s5")};

		Team tempTeam = new Team();

		tempTeam.addMembers(projRef, selectedStudents);
	}
	
	/*
	 *  Negative test case 
	 *  Null project reference is passed. NullProjectException will be thrown
	 */
	@Test (expected = NullProjectException.class)
	public void testAddMembers_NullProject() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
				
		// Null project is passed
		Project projRef = null;
		
		// List of unique students, no conflicts, no other requirements voilation
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s4")};
		
		Team tempTeam = new Team();	

		// NullProjectException is thrown is projRef is null
		tempTeam.addMembers(projRef, selectedStudents);
	}
	
	/*
	 *  Negative test case 
	 *  Student ID is not valid and hence the StudentNotFoundException will be thrown
	 */
	@Test (expected = StudentNotFoundException.class)
	public void testAddMembers_StudentIdWrong() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
				
		Project projRef = this.getShortListedProjectsList().get("Pr3");
		Student [] selectedStudents = { this.getStudentsList().get("s40"), this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s40")};
		
		Team tempTeam = new Team();		
		tempTeam.addMembers(projRef, selectedStudents);
	}
	
	
	/*
	 * Negative test case
	 * A personality type should not be present for less than 3 times
	 * Allowed : A B C C (1 Repetation is allowed)
	 * Not allowed : A C C C or A B B B
	 */
	@Test (expected = PersonalityImbalanceException.class)
	public void testAddMembers_PersonalityImbalance() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, PersonalityImbalanceException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
		
		Project projRef = this.getShortListedProjectsList().get("Pr3");

		// A, C, C, C
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s3"), this.getStudentsList().get("s9"), this.getStudentsList().get("s10")};

		Team tempTeam = new Team();
		
		tempTeam.addMembers(projRef, selectedStudents);
		
		tempTeam.checkIfPersonalityImbalance();
	}
	
	
	/*
	 * Negative test case
	 * Repeated member is being added to the same team. 
	 */
	@Test (expected = RepeatedMemberException.class)
	public void testAddMembers_RepeatedMember() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, PersonalityImbalanceException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
		
		Project projRef = this.getShortListedProjectsList().get("Pr3");

		// S1 is being 2 times
		Student [] selectedStudents = { this.getStudentsList().get("s1"), this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s1")};

		Team tempTeam = new Team();
		
		// Adding members to the team - Exception expected
		tempTeam.addMembers(projRef, selectedStudents);
	}
	
	
	/*
	 * Negative test case
	 * If no shortlisted student have leadership quality, exception will be thrown
	 */
	@Test (expected = NoLeaderException.class)
	public void testAddMembers_NoLeader() throws NoLeaderException, ProjectAlreadyAssignedException, InvalidMemberException, RepeatedMemberException, NullProjectException, StudentConflictException, ExcessMemberException, StudentNotFoundException {

		Project projRef = this.getShortListedProjectsList().get("Pr3");
		// B, C, D, B personality types, no leader
		Student [] selectedStudents = { this.getStudentsList().get("s2"), this.getStudentsList().get("s3"), this.getStudentsList().get("s4"), this.getStudentsList().get("s6")};

		Team tempTeam = new Team();
		tempTeam.addMembers(projRef, selectedStudents);

	}
	
	
	/*
	 * Positive test case - Manual calculation comes to 0.593
	 * Average Project Skill Competency is sum( skills of all students in all subjects ) / (number of students * number of subjects)
	 */
	@Test
	public void test_avgProjSkillComp() throws NoLeaderException {
		
		// Forming new team T3 for the project Pr3 with s1, s2,s3, s4 students 
		Team tempTeam = new Team("T3", this.getShortListedProjectsList().get("Pr3"));

		LinkedHashMap<String, Student> hash = new LinkedHashMap<String, Student>();
		hash.put("s1", this.getStudentsList().get("s1"));
		hash.put("s2", this.getStudentsList().get("s2"));
		hash.put("s3", this.getStudentsList().get("s3"));
		hash.put("s4", this.getStudentsList().get("s4"));
		
		tempTeam.setMembers(hash);
		
		tempTeam.computeAvgSkillPerCategory();		// Prerequisite 
		tempTeam.computeAvgSkillForProject();		// Sum(Per category skill avg) / 4
		
		Assert.assertEquals("Calculates the average skills for a project by all members", 2.5, tempTeam.getAvgProjSkillComp(), 0.1);
	}

	// Private getter setters for this class only - testing data
	private LinkedHashMap<String, Project> getShortListedProjectsList(){
		return this.shortListedProjectsList;
	}

	private HashMap<String, Student> getStudentsList(){
		return this.studentsList;
	}

	/**
	 * Prepares the dummy data that is required for performing this test.
	 */
	public void loadTestData(){
		shortListedProjectsList.put("Pr1", new Project("Pr1", "Test Project 1", "Pr1 - Sample Project Description", "Own1", 4, 3, 2, 1, 10));
		shortListedProjectsList.put("Pr2", new Project("Pr2", "Test Project 3", "Pr2 - Sample Project Description", "Own2", 3, 4, 2, 1, 9));
		shortListedProjectsList.put("Pr3", new Project("Pr3", "Test Project 3", "Pr3 - Sample Project Description", "Own3", 4, 2, 1, 3, 8));

		studentsList.put("s1", new Student("s1", 4, 3, 2, 1, "Pr1", "Pr2", "Pr3", "Pr4", "A", "s6", "s11"));
		studentsList.put("s2", new Student("s2", 4, 3, 2, 1, "Pr1", "Pr2", "Pr3", "Pr3", "B", "s12", "s13"));
		studentsList.put("s3", new Student("s3", 3, 4, 1, 2, "Pr4", "Pr3", "Pr1", "Pr2", "C", "s13", "s14"));
		studentsList.put("s4", new Student("s4", 2, 4, 3, 1, "Pr1", "Pr2", "Pr6", "Pr5", "D", "s13", "s14"));
		studentsList.put("s5", new Student("s5", 3, 2, 1, 4, "Pr4", "Pr3", "Pr6", "Pr7", "A", "", ""));
		studentsList.put("s6", new Student("s6", 1, 2, 3, 4, "Pr4", "Pr3", "Pr6", "Pr7", "B", "", ""));
		studentsList.put("s7", new Student("s7", 2, 1, 4, 3, "Pr4", "Pr3", "Pr6", "Pr7", "C", "s1", ""));
		studentsList.put("s8", new Student("s8", 1, 3, 4, 2, "Pr4", "Pr3", "Pr6", "Pr7", "D", "s20", ""));
		studentsList.put("s9", new Student("s9", 3, 4, 1, 2, "Pr4", "Pr3", "Pr6", "Pr7", "C", "s14", ""));
		studentsList.put("s10", new Student("s10", 3, 4, 1, 2, "Pr4", "Pr3", "Pr6", "Pr7", "C", "s11", ""));
	}
}
