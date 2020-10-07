import globals.Globals;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.ProjectTeamFormationMain;
import model.entities.Student;
import model.entities.Team;
import utilities.ScannerUtil;

import java.util.HashMap;

public class Menu extends Application {

	private boolean quit = false;
	static ProjectTeamFormationMain driver = new ProjectTeamFormationMain();
	private final ScannerUtil scannerUtil = ScannerUtil.createInstance().consoleReader();

	public Menu() {
		driver = new ProjectTeamFormationMain();
	}

	// Show menu -> Get user Choice -> Perform Operation -> repeate
	public void run() {

		// Prepares the system with provided test data
		driver.loadDataFromFiles();

		do {
			this.showMenu();
			String userChoice = this.getChoice();
			
			// Check if operation has any sub menu - Team Fitness Metric Handling
			if(userChoice.equalsIgnoreCase("H")) {
				
				do {
					this.showDisplayTeamFitnessSubMenu();
					userChoice = scannerUtil.readString("Enter Choice : ");
					driver.teamFitnessMetricOperations(userChoice);
					
				}while(userChoice.contentEquals("Q") == false);
				continue;
				
			}
			// Sub menu checking ends here
			
			driver.executeOperation(userChoice);
			
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


	// Main method
	public static void main(String[] args) {

		Application.launch();		
		System.exit(0);
	}


    @Override
    public void start(Stage stage) {
    	

        System.out.println("Entering start method");
        
//		Menu menuInstance = new Menu();

		Menu.driver.loadDataFromFiles();

        // A stage is the application window automatically created by the framework
        // The scene holds the content to be displayed, which is stored as tree


        // Initialise a new gridPane
        GridPane gridPane = new GridPane();

        // Initialise a input field for student ID
        TextField studentIdInput = new TextField ();

        // Button for adding a student
        Button addStudentBtn = new Button("Add Student");

        // Button for swapping a student
        Button swapStudentsBtn = new Button("Swap");

        // Set the size of the grid pane
        gridPane.setMinSize(800, 800);

        // Set padding - 10 from all sides
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        // Gaps between 2 columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        // Set alignment of the grid
        gridPane.setAlignment(Pos.CENTER);
        
        // Create cards for the teams
        System.out.println(Menu.driver.getTeamsList().size());
        
        int i = 0;
        for(Team teamRef : Menu.driver.getTeamsList().values()) {
        	System.out.println("adding team");
        	GridPane teamPane = generateATeamPanel( teamRef );
        	gridPane.addColumn(i, teamPane);
        	i += 1;
        }
        
        gridPane.setGridLinesVisible(true);
        
        // Start arranging nodes on the grid pane

        gridPane.addRow(1);

//        gridPane.addRow(2, studentIdInput, addStudentBtn);
//
//        gridPane.addRow(3,swapStudentsBtn);

        Scene scene = new Scene(gridPane, 640, 480);

        // We can have multiple scenes. Setup this one, and tell the stage to show it.
        stage.setScene(scene);
        stage.show();
    }

    public GridPane generateATeamPanel(Team teamRef){

        GridPane teamPane = new GridPane();
        teamPane.setGridLinesVisible(true);
        HashMap<String, Student> members = teamRef.getMembers();
        
        int i = 0;
        for(String idStr : members.keySet()){
        	System.out.println("Adding member");
            teamPane.addRow(i, new Text(idStr), new CheckBox());
            i += 1;
        }

        return teamPane;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Entering init method");
    }

}
