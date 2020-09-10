import controller.ProjectTeamFormationMain;
import globals.Globals;
import utilities.ScannerUtil;

public class Console_Menu{

	private boolean quit = false;
	ProjectTeamFormationMain driver = null;
	private ScannerUtil scannerUtil = ScannerUtil.createInstance().consoleReader();

	public Console_Menu() {
		driver = new ProjectTeamFormationMain();
	}

	// Show menu -> Get user Choice -> Perform Operation -> repeate
	public void run() {

		// Prepares the system with provided test data
		this.driver.loadDataFromFiles();

		do {
			this.showMenu();
			String userChoice = this.getChoice();
			
			// Check if operation has any sub menu - Team Fitness Metric Handling
			if(userChoice.equalsIgnoreCase("H")) {
				
				do {
					this.showDisplayTeamFitnessSubMenu();
					userChoice = scannerUtil.readString("Enter Choice : ");
					this.driver.teamFitnessMetricOperations(userChoice);
					
				}while(userChoice.contentEquals("Q") == false);
				continue;
				
			}
			// Sub menu checking ends here
			
			this.driver.executeOperation(userChoice);
			
			if(userChoice.equalsIgnoreCase("Q")) {
				this.quit = true;
			}

		}while(quit != true);

		// Closing open resources
		this.scannerUtil.closeReader();
	}

	// Prints Menu
	public void showMenu() {
		System.out.println("\t\t\t Menu \t\t\t");
		System.out.println(Globals.LINE_SEPERATOR);
		System.out.println("A. Add Company						\n" + 
				"B. Add Project Owner				\n" + 
				"C. Add Project						\n" + 
				"D. Capture Student Personalities	\n" + 
				"E. Add Student Preferences			\n" + 
				"F. Shortlist Projects				\n" +
				"G. Form Team						\n" + 
				"H. Display Team Fitness Metrics	\n" +
				"I. Update Standard Deviations		\n" +
				"Q. Quit							"
				);
	}

	
	// Prints the sub menu for the team fitenss metric operations
	public void showDisplayTeamFitnessSubMenu() {
		
		System.out.println("\t\t\t Sub Menu \t\t\t");
		System.out.println(Globals.LINE_SEPERATOR);
		System.out.println("A. Average student skill competency for each project team 						\n" + 
				"B. Percentage of students who got their first and second preferences in each of the teams. \n" + 
				"C. Skills shortfall for each project														\n" + 
				"Q. Quit SubMenu						"
				);
		
	}

	// Get user choice in terms of operation. It will 
	public String getChoice() {
		return scannerUtil.readString("Please Enter Your Choice :");
	}


	public static void main(String[] args) {

		Menu menuInstance = new Menu();

		menuInstance.run();
				
		System.exit(0);
	}

}
