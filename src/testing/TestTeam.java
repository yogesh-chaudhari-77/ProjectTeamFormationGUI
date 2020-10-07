package testing;

import main.ProjectTeamFormationMain;
import model.entities.Project;
import model.entities.Student;
import model.entities.Team;
import model.exceptions.*;
import org.junit.*;

import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * 
 */

/**
 * @author Yogeshwar Chaudhari
 */
public class TestTeam {


	// https://stackoverflow.com/questions/6415728/junit-testing-with-simulated-user-input
	InputStream sysInputBk = null;
	ProjectTeamFormationMain pjT = null;
	
	
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
		pjT.loadDataFromFiles();

		sysInputBk = System.in; // Backing up System.in so that it can be restored later
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
				
		Project projRef = this.pjT.getShortListedProjectsList().get("Pr3");
		Student [] selectedStudents = { this.pjT.getStudentsList().get("s1"), this.pjT.getStudentsList().get("s2"), this.pjT.getStudentsList().get("s3"), this.pjT.getStudentsList().get("s4")};
		
		Team tempTeam = new Team();
		
		// Initial size of the teams list - This is before the new team has been formed
		// int initialTeamSize = this.pjT.getTeamsList().size();

		tempTeam.addMembers(projRef, selectedStudents);
		
		// System.out.println(this.pjT.getTeamsList());
		
		// Testing if the new team has been added to the list
		// Assert.assertEquals("The size of the project should be increased by 1",  initialTeamSize+1, this.pjT.getTeamsList().size());
	}
	
	
	/*
	 * Positive test case
	 * Calculates the total skill shortage for the project for the team
	 */
	@Test
	public void test_totalSkillShortage() throws NoLeaderException {
		
		// Forming new team T3 for the project Pr3 with s1, s2,s3, s4 students
		Team tempTeam = new Team("T3", this.pjT.getShortListedProjectsList().get("Pr3"));

		LinkedHashMap<String, Student> hash = new LinkedHashMap<String, Student>();
		hash.put("s1", this.pjT.getStudentsList().get("s1"));
		hash.put("s2", this.pjT.getStudentsList().get("s2"));
		hash.put("s3", this.pjT.getStudentsList().get("s3"));
		hash.put("s4", this.pjT.getStudentsList().get("s4"));
		
		tempTeam.setMembers(hash);
		
		tempTeam.computeAvgSkillPerCategory();		// Prerequisites
		tempTeam.computeCategorySkillShortage();	// Required skill shortage - getAvgCategorySkillComp for every skill
		tempTeam.computeOverallSkillShortage();  	// Summation of all negative values - that indicates the skill shortage
		
		// Returns absolute value
		Assert.assertEquals("This value indicates the summation of shortfall of the skills in all areas", 2.0, tempTeam.getTotalSkillShortage(), 0.1);
	}
	
	
	/*
	 * Positive test case
	 * Calculates how many students received their first and second preferences in percentage amount
	 * 2 Students put Pr3 as 1 and 2 preference. Hence preference percentage is 50% for this team
	 */
	@Test
	public void test_prctStudentReceivedPreference() throws NoLeaderException {
		
		Team tempTeam = new Team("T1", this.pjT.getShortListedProjectsList().get("Pr3"));

		LinkedHashMap<String, Student> hash = new LinkedHashMap<String, Student>();
		hash.put("s1", this.pjT.getStudentsList().get("s1"));
		hash.put("s2", this.pjT.getStudentsList().get("s2"));
		hash.put("s3", this.pjT.getStudentsList().get("s3"));
		hash.put("s4", this.pjT.getStudentsList().get("s4"));
		
		tempTeam.setMembers(hash);
		
		tempTeam.computePreferenceAllocPct();
		
		Assert.assertEquals("This value indicates the percentage of students who got first and second preferences", 25.0, tempTeam.getPrctStudentReceivedPreference(), 0.0001);
	}
	
	
	/*
	 * Negative Test Case
	 * If student has been allocated, then throw an exception if an attempt is made to add that student to another project without clearing previous project allocation
	 */
	@Test (expected = InvalidMemberException.class)
	public void testAddMembers_StudentAlreadyAssigned() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
				
		// Allocating s1 to Pr3
		Project projRef = this.pjT.getShortListedProjectsList().get("Pr3");
		Student [] selectedStudents = { this.pjT.getStudentsList().get("s1"), this.pjT.getStudentsList().get("s2"), this.pjT.getStudentsList().get("s3"), this.pjT.getStudentsList().get("s4")};
		
		Team tempTeam = new Team();
		tempTeam.addMembers(projRef, selectedStudents);
		// Team formed successfully.
		
		// Allocating members for Pr4
		projRef = this.pjT.getShortListedProjectsList().get("Pr4");
		Team tempTeam2 = new Team();
		
		tempTeam2.addMembers(projRef, selectedStudents);
	}

	

	/*
	 * Negative test case
	 * The provided students have conflicts amoung them
	 * Expects studentConflictException while iteration
	 */
	@Test (expected = StudentConflictException.class)
	public void testAddMembers_StudentConflict() throws StudentNotFoundException, InvalidMemberException, RepeatedMemberException, StudentConflictException, NoLeaderException, NullProjectException, ProjectAlreadyAssignedException, ExcessMemberException {
				
		Project projRef = this.pjT.getShortListedProjectsList().get("Pr3");
		
		Student [] selectedStudents = { this.pjT.getStudentsList().get("s1"), this.pjT.getStudentsList().get("s6"), this.pjT.getStudentsList().get("s3"), this.pjT.getStudentsList().get("s5")};

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
		Student [] selectedStudents = { this.pjT.getStudentsList().get("s1"), this.pjT.getStudentsList().get("s2"), this.pjT.getStudentsList().get("s3"), this.pjT.getStudentsList().get("s4")};
		
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
				
		Project projRef = this.pjT.getShortListedProjectsList().get("Pr3");
		Student [] selectedStudents = { this.pjT.getStudentsList().get("s40"), this.pjT.getStudentsList().get("s2"), this.pjT.getStudentsList().get("s3"), this.pjT.getStudentsList().get("s40")};
		
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
		
		Project projRef = this.pjT.getShortListedProjectsList().get("Pr3");
		
		Student [] selectedStudents = { this.pjT.getStudentsList().get("s10"), this.pjT.getStudentsList().get("s14"), this.pjT.getStudentsList().get("s18"), this.pjT.getStudentsList().get("s5")};

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
		
		Project projRef = this.pjT.getShortListedProjectsList().get("Pr3");

		// S1 is being 2 times
		Student [] selectedStudents = { this.pjT.getStudentsList().get("s1"), this.pjT.getStudentsList().get("s2"), this.pjT.getStudentsList().get("s3"), this.pjT.getStudentsList().get("s1")};

		Team tempTeam = new Team();
		
		// Adding members to the team - Exception expected
		tempTeam.addMembers(projRef, selectedStudents);
	}
	
	
	/*
	 * Negative test case
	 * If no shortlisted student have leadership quality, exception will be thrown
	 */
	@Test (expected = NoLeaderException.class)
	public void testAddMembers_NoLeader() throws NoLeaderException {
		
		Team tempTeam = new Team();

		LinkedHashMap<String, Student> hash = new LinkedHashMap<String, Student>();
		hash.put("s7", this.pjT.getStudentsList().get("s14"));
		hash.put("s8", this.pjT.getStudentsList().get("s18"));
		hash.put("s9", this.pjT.getStudentsList().get("s8"));
		hash.put("s10", this.pjT.getStudentsList().get("s10"));
		
		tempTeam.setMembers(hash);
		
		// Throws NoLeaderException if no student posses the leadership quality
		tempTeam.checkIfLeaderExists();
	}
	
	
	/*
	 * Positive test case - Manual calculation comes to 0.593
	 * Average Project Skill Competency is sum( skills of all students in all subjects ) / (number of students * number of subjects)
	 */
	@Test
	public void test_avgProjSkillComp() throws NoLeaderException {
		
		// Forming new team T3 for the project Pr3 with s1, s2,s3, s4 students 
		Team tempTeam = new Team("T3", this.pjT.getShortListedProjectsList().get("Pr3"));

		LinkedHashMap<String, Student> hash = new LinkedHashMap<String, Student>();
		hash.put("s1", this.pjT.getStudentsList().get("s1"));
		hash.put("s2", this.pjT.getStudentsList().get("s2"));
		hash.put("s3", this.pjT.getStudentsList().get("s3"));
		hash.put("s4", this.pjT.getStudentsList().get("s4"));
		
		tempTeam.setMembers(hash);
		
		tempTeam.computeAvgSkillPerCategory();		// Prerequisite 
		tempTeam.computeAvgSkillForProject();		// Sum(Per category skill avg) / 4
		
		Assert.assertEquals("Calculates the average skills for a project by all members", 0.593, tempTeam.getAvgProjSkillComp(), 0.1);
	}
}
