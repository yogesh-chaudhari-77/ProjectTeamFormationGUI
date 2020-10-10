package testing;

import main.ProjectTeamFormationMain;
import org.junit.*;

/**
 * 
 */

/**
 * @author Yogeshwar Chaudhari
 *
 */
public class TestProjectTeamFormationMain {

	private static ProjectTeamFormationMain projectTeamFormationMain = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		projectTeamFormationMain = new ProjectTeamFormationMain();
		projectTeamFormationMain.loadDataFromFiles();
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
		Assert.assertEquals("Standard Daviation in Skill Competency", 0.671, projectTeamFormationMain.getsDInSkillCompetencyAcrossProj(), 0.001);
	}
	
	
	/*
	 * Positive case
	 * 2 projects in every team received their preference projects, standard daviation calculates to 0 as per the formula
	 */
	@Test
	public void test_sDInProjPrefAllocPrct() {
		
		projectTeamFormationMain.sDInProjPrefAllocPrct();
		Assert.assertEquals("Project Preference Allocation SD : ", 22.360, projectTeamFormationMain.getsDInProjPrefAllocPrct(), 0.01);
	}

	/*
	 * SD in Skill Shortfall Across Team
	 */
	@Test
	public void sDInShortFallAcrossTeam() {
		
		projectTeamFormationMain.sDInShortFallAcrossTeam();
		Assert.assertEquals("Standard Daviation in Skill Shortfall across Teams", 1.065, projectTeamFormationMain.getsDInSkillShortfall(), 0.01);
		
	}

}
