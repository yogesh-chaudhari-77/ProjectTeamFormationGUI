package controller;

/*
[1] Using JavaFX Charts: Bar Chart | JavaFX 2 Tutorials and Documentation
Using JavaFX Charts: Bar Chart | JavaFX 2 Tutorials and Documentation (2020). Available at: https://docs.oracle.com/javafx/2/charts/bar-chart.htm (Accessed: 9 September 2020).
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.ProjectTeamFormationMain;
import model.entities.Project;
import model.entities.Student;
import model.entities.Team;
import model.exceptions.*;

import java.net.URL;
import java.util.*;

public class VisualSensitiveAnalysisController implements Initializable {
    @FXML
    public GridPane teamsGrid;
    @FXML
    public GridPane analysisChartsGrid;
    @FXML
    public TextField studentIdText;
    @FXML
    public Button addBtn;
    @FXML
    public Button swapBtn;

    // Individual grid panes
    @FXML
    public GridPane teamGridPane_1;

    @FXML
    public GridPane teamGridPane_2;

    @FXML
    public GridPane teamGridPane_3;

    @FXML
    public GridPane teamGridPane_4;

    @FXML
    public GridPane teamGridPane_5;

    @FXML
    public Text cardProjIdText_1;

    @FXML
    public Text cardProjIdText_2;

    @FXML
    public Text cardProjIdText_3;

    @FXML
    public Text cardProjIdText_4;

    @FXML
    public Text cardProjIdText_5;

    @FXML
    public Button saveTeamsBtn;

    @FXML
    public CheckBox t1s1Cb;

    @FXML
    public CheckBox t1s2Cb;

    @FXML
    public CheckBox t1s3Cb;

    @FXML
    public CheckBox t1s4Cb;

    @FXML
    public CheckBox t2s1Cb;

    @FXML
    public CheckBox t2s2Cb;

    @FXML
    public CheckBox t2s3Cb;

    @FXML
    public CheckBox t2s4Cb;

    @FXML
    public CheckBox t3s1Cb;

    @FXML
    public CheckBox t3s2Cb;

    @FXML
    public CheckBox t3s3Cb;

    @FXML
    public CheckBox t3s4Cb;

    @FXML
    public CheckBox t4s1Cb;

    @FXML
    public CheckBox t4s2Cb;

    @FXML
    public CheckBox t4s3Cb;

    @FXML
    public CheckBox t4s4Cb;

    @FXML
    public CheckBox t5s1Cb;

    @FXML
    public CheckBox t5s2Cb;

    @FXML
    public CheckBox t5s3Cb;

    @FXML
    public CheckBox t5s4Cb;

    // Parent of all elements for this view
    @FXML
    public AnchorPane rootAnchorPane;

    // List element that shows the shortlisted projects
    @FXML
    public ListView shortListedProjectsListView;

    // List element that shows the formed teams
    @FXML
    public ListView teamsListView;

    // List element that shows students which are yet to be shortlisted
    @FXML
    public ListView studentsListView;

    //Reference to preference allocation graph at the bottom (number of lines = number of teams formed)
    @FXML
    public BarChart prefAllocStdDevGraph;

    // Reference to avg skill competency across team graph (number of lines = number of teams formed)
    @FXML
    public BarChart avgSkillComptStdDevGraph;

    // Reference to skill gap across team graph - (number of lines = number of teams formed)
    @FXML
    public BarChart skillGapStdDevGraph;
    public TextFlow projectInfoTextFlow;
    public TextFlow studentInfoTextFlow;
    public MenuItem saveAndCloseBtn;
    public Label statusbarLabel;
    public ProgressBar statusbarProgress;

    @FXML
    public Label stdPrefLabel;
    @FXML
    public Label stdAvgProjCompLabel;
    @FXML
    public Label stdSkillShortFallLabel;

    // Holds the references of all top grid panes where teams are rendered. 5 Grids
    GridPane [] teamsGridColln = null;

    // Holds the references of all project ids text elements inside respective teamGrid - 5 teams 5 text boxes
    Text [] cardProjeIdColln = null;

    // Holds references of checkboxes in all teamsGrid panes. - 5 teams x 4 students = 20 checkboxes
    CheckBox [] cardCheckBoxes = null;

    ProjectTeamFormationMain pj = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Initialising collections of teamGridPanes, checkboxes and TextElements - Mainly from top pane
        this.teamsGridColln = new GridPane[] {teamGridPane_1, teamGridPane_2, teamGridPane_3, teamGridPane_4, teamGridPane_5};
        this.cardProjeIdColln = new Text[] {cardProjIdText_1, cardProjIdText_2, cardProjIdText_3, cardProjIdText_4, cardProjIdText_5};
        this.cardCheckBoxes = new CheckBox[] {t1s1Cb, t1s2Cb, t1s3Cb, t1s4Cb, t2s1Cb, t2s2Cb, t2s3Cb, t2s4Cb, t3s1Cb, t3s2Cb, t3s3Cb, t3s4Cb, t4s1Cb, t4s2Cb, t4s3Cb, t4s4Cb, t5s1Cb, t5s2Cb, t5s3Cb, t5s4Cb};

        pj = new ProjectTeamFormationMain();

        pj.loadDataFromFiles();

        // Setting controller for updates
        System.out.println("1");
        pj.setController(this);

        System.out.println("2");
        // Render formed teams i.e. team id and team members
        renderTeams();

        System.out.println("3");
        // Render graphs as per formed teams i.e. team id vs value
        renderGraphs();

        System.out.println("4");
        /**
         * Renders all the project Ids that are being shortlisted as the popular projects
         */
        for(String projId : this.pj.getShortListedProjectsList().keySet()){
            this.shortListedProjectsListView.getItems().add(projId);
        }
        System.out.println("5");
        /**
         * Renders all the teams that have been formed to the listview
         */
        for(String teamId : this.pj.getTeamsList().keySet()){
            this.teamsListView.getItems().add(teamId);
        }

        System.out.println("6");

        populateStudentListView();
        System.out.println("7");
    }


    /**
     * Populating student list view
     * If the current project allocation is not null then only it will be added to the project
     */
     public void populateStudentListView(){

         for(Student s : this.pj.getStudentsList().values()){
             //System.out.println("In Student View Populatation");
             System.out.println(s.getCurrProjAssoc());

             if(s.getCurrProjAssoc() == null || s.getCurrProjAssoc() == "" || s.getCurrProjAssoc().isEmpty() || s.getCurrProjAssoc().contentEquals("")){
                 this.studentsListView.getItems().add(s.getId());
             }
         }
     }

    /**
     * Renders all teams onto the top grid panes.
     * Populates student text elements
     */
    public void renderTeams(){

        statusbarLabel.setText("Rendering team");

        Iterator it = this.pj.getTeamsList().values().iterator();

        int i = 0;
        while (it.hasNext()) {
            Team teamRef = (Team)it.next();
            generateATeamPanel(teamsGridColln[i], teamRef);
            i += 1;
        }
    }


    /**
     * Paints given team grid pane with the supplied team and it's members
     * @param gridPaneRef : Grid pane that needs to be painted
     * @param teamRef : With this team
     */
    public void generateATeamPanel(GridPane gridPaneRef, Team teamRef){

        HashMap<String, Student> members = teamRef.getMembers();
        int gridPaneNumber = Integer.parseInt(gridPaneRef.getId().split("_")[1]);
        Text currProjIdText = (Text) gridPaneRef.lookup("#cardProjIdText_"+gridPaneNumber);
        currProjIdText.setText(teamRef.getProjectRef().getId());

        // Clear existing student ids
        for(int r = 0; r < 4; r++){
            Text s = (Text) gridPaneRef.lookup("#t"+gridPaneNumber+"s"+(r+1));
            s.setText("");
        }

        int i = 0;
        for(String idStr : members.keySet()){
            Text s = (Text) gridPaneRef.lookup("#t"+gridPaneNumber+"s"+(i+1));
            System.out.println(s.getId());
            s.setText(idStr);
            i += 1;
        }
    }


    /**
     * Render or render all 3 types of graph available on this screen
     * This is mapping of standard deviation against teams
     */
    public void renderGraphs(){
        statusbarLabel.setText("Rendering Graphs");

        // Rendering graphs
        renderPreferencesGraph();
        renderAvgCompetencyLevelGraph();
        renderSkillGapGraph();

        // Calculating the Standard deviations real quick
        this.pj.sDInSkillCompetency();
        this.pj.sDInProjPrefAllocPrct();
        this.pj.sDInShortFallAcrossTeam();

        stdPrefLabel.setText( "Pref Alloc Std Dev : : "+ String.format("%.2f", Double.isNaN(pj.getsDInProjPrefAllocPrct()) ? 0.0 : pj.getsDInProjPrefAllocPrct()) );
        stdAvgProjCompLabel.setText( "Skill Comp. Std Dev : "+ String.format("%.2f", Double.isNaN(pj.getsDInSkillCompetencyAcrossProj()) ? 0.0 : pj.getsDInSkillCompetencyAcrossProj() )  );
        stdSkillShortFallLabel.setText( "Skill Shortfall Std Dev : "+ String.format("%.2f", Double.isNaN(pj.getsDInSkillShortfall()) ? 0.0 : pj.getsDInSkillShortfall()) );
    }

    /**
     * Graph showing percentage distribution of student's who received their first and second preference.
     * This is in terms of standard deviation
     */
    public void renderPreferencesGraph(){

        prefAllocStdDevGraph.getData().clear();

        prefAllocStdDevGraph.setTitle("Preference Allocation");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        XYChart.Series series1 = new XYChart.Series();
        this.pj.getTeamsList().values().stream().forEach((team) -> {
            series1.getData().add(new XYChart.Data(team.getTeamId(), team.getPrctStudentReceivedPreference()));
        });
        prefAllocStdDevGraph.getData().add(series1);
    }

    /**
     * Graph showing percentage distribution of average competency level
     * This is in terms of standard deviation
     */
    public void renderAvgCompetencyLevelGraph(){
        avgSkillComptStdDevGraph.getData().clear();

        avgSkillComptStdDevGraph.setTitle("Average Competency Level");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        XYChart.Series series1 = new XYChart.Series();
        this.pj.getTeamsList().values().stream().forEach((team) -> {
            series1.getData().add(new XYChart.Data(team.getTeamId(), team.getAvgProjSkillComp()));
        });
        avgSkillComptStdDevGraph.getData().add(series1);
        
    }

    /**
     * Graph showing skills gap
     * This is in terms of standard deviation
     * Reference : [1]
     */
    public void renderSkillGapGraph(){

        skillGapStdDevGraph.getData().clear();

        skillGapStdDevGraph.setTitle("Skill Gap");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        XYChart.Series series1 = new XYChart.Series();
        this.pj.getTeamsList().values().stream().forEach((team) -> {
            series1.getData().add(new XYChart.Data(team.getTeamId(), team.getTotalSkillShortage()));
        });
        skillGapStdDevGraph.getData().add(series1);
    }


    /**
     * Adds the supplied student into one of the selected project
     * Project manager selects Project Id from list view, enteres student ID and then hits enter.
     * Student is then added to selected project id.
     * @param actionEvent
     */
    public void addStudentEvent(ActionEvent actionEvent) {

        // Basic error handling - Project must be selected before adding a member to team
        String currProjectId = "";
        try{
            currProjectId = shortListedProjectsListView.getSelectionModel().getSelectedItem().toString();
        }catch (NullPointerException npe){
            showAlert(Alert.AlertType.ERROR, "No Project Selected", "Please select a project from list view and try again");
            return;
        }

        Project projRef = this.pj.getShortListedProjectsList().get(currProjectId);
        String teamId = currProjectId.replace("Pr", "T");
        String enteredStudentId = studentIdText.getText();

        // Basic error handling - do not accept blank or empty strings as student IDs
        if(enteredStudentId.isEmpty() || enteredStudentId.isBlank()){
            showAlert(Alert.AlertType.ERROR, "Invalid Student ID", "Empty or Blank Student Id. Please provide a valid student ID and try again.");
            return;
        }

        Student studentRef = this.pj.getStudentsList().get(enteredStudentId);

        // Validate if the student is present or not
        if(this.pj.getStudentsList().containsKey(enteredStudentId)){

            Team teamRef = this.pj.getTeamsList().get(teamId);

            if(teamRef == null){
                teamRef = new Team(teamId, projRef);
                this.pj.getTeamsList().put(teamId, teamRef);
                this.teamsListView.getItems().add(teamId);
            }

            if(teamRef != null){
                try {
                    teamRef.addMember(projRef, studentRef);
                } catch (InvalidMemberException | RepeatedMemberException | StudentConflictException | ExcessMemberException e) {
                    showAlert(Alert.AlertType.ERROR, e.getClass().getName(), e.getMessage()+" Your last addition will be reverted.");
                } catch (NoLeaderException e) {
                    showAlert(Alert.AlertType.ERROR, "No Leader", "There is no leader in formed team. Reverting last addition");
                    teamRef.removeMember(projRef, studentRef);
                }catch (PersonalityImbalanceException p){
                    showAlert(Alert.AlertType.ERROR, "Personality Imbalanced", "Personalities in team are imbalanced. Your last addition will be reverted.");
                    teamRef.removeMember(projRef, studentRef);
                }
            }

            //teamRef = this.pj.getTeamsList().get(teamId);
            GridPane gridPaneRef = getGridPaneForThisTeam(currProjectId);
            generateATeamPanel(gridPaneRef, teamRef);
            teamRef.updateStatistics();

        }else{
            Alert a = new Alert(Alert.AlertType.ERROR, enteredStudentId+" is not present in the student's list. Please try again.");
            a.show();
            return;
        }

        // Remove student from list view - if the student is not present in listView then it means that he/she have been allocated to project
        if(studentRef.getCurrTeamAssoc() != null){
            this.studentsListView.getItems().remove(enteredStudentId);
        }

        this.renderGraphs();
    }

    /**
     * Initiate student Swap
     * Requires exactly 2 checkboxes to be selected
     * Does not throw error even if 2 checkboxes from same team has been selected. Because it would not cause any problem to the system.
     * @param actionEvent
     */
    public void swapStudentEvent(ActionEvent actionEvent) {

        CheckBox [] selectedCbs = Arrays.stream(this.cardCheckBoxes).filter(x -> x.isSelected() == true).toArray(CheckBox[]::new);

        if(selectedCbs.length < 2 || selectedCbs.length >= 3){

            this.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "You need to select 2 students to");
            Arrays.stream(selectedCbs).forEach(c -> c.setSelected(false));
            return;
        }

        if(selectedCbs.length == 2){
            System.out.println("Only 2 checkboxed selected");
            // Get checked checkboxes ids
            String s1TextId = selectedCbs[0].getId().replace("Cb", "");
            String s2TextId = selectedCbs[1].getId().replace("Cb", "");

            // Get the adjecent checkboxes which similar ids - which would have studentIds as text propert
            String s1Id = ((Text) rootAnchorPane.lookup("#"+s1TextId)).getText();
            String s2Id = ((Text) rootAnchorPane.lookup("#"+s2TextId)).getText();

            // Error checking - swaping students when there are no students
            if(s1Id.isBlank() || s1TextId.isEmpty() || s2Id.isBlank() || s2Id.isEmpty() || s1Id == null || s2Id == null ){
                showAlert(Alert.AlertType.ERROR, "Invalid Selection", "There are no student against selected checkbox");
                Arrays.stream(selectedCbs).forEach(c -> c.setSelected(false));
                return;
            }

            // Get the student reference from those ids
            Student s1Ref = this.pj.getStudentsList().get(s1Id);
            Student s2Ref = this.pj.getStudentsList().get(s2Id);

            // Remove those students from the current team
            Team team1Ref = s1Ref.getCurrTeamAssoc();
            team1Ref.removeMember(null, s1Ref);

            Team team2Ref = s2Ref.getCurrTeamAssoc();
            team2Ref.removeMember(null, s2Ref);

            try {
                team1Ref.addMember( team1Ref.getProjectRef(), s2Ref );
                team2Ref.addMember( team2Ref.getProjectRef(), s1Ref );

            } catch (InvalidMemberException | RepeatedMemberException | StudentConflictException | ExcessMemberException | NoLeaderException | PersonalityImbalanceException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Cant Swap", e.getMessage());
                // Rectifying the swap if something goes wrong
                try {
                    team1Ref.removeMember(team1Ref.getProjectRef(), s2Ref);
                    team1Ref.addMember(team1Ref.getProjectRef(), s1Ref);
                    team2Ref.addMember(team2Ref.getProjectRef(), s2Ref);
                } catch (InvalidMemberException | RepeatedMemberException | StudentConflictException | ExcessMemberException | NoLeaderException | PersonalityImbalanceException e1) {
                    e1.printStackTrace();
                }
            }

            team1Ref.updateStatistics();
            team2Ref.updateStatistics();
        }

        // Deselecting the selection
        selectedCbs[0].setSelected(false);
        selectedCbs[1].setSelected(false);

        // Render graphs and teams again
        renderGraphs();
        renderTeams();
    }

    /**
     * Method for getting the student info on selection of student Id from list view
     * @param mouseEvent
     */
    public void studentListViewClicked(MouseEvent mouseEvent) {

        String studentId;
        try{
            studentId = studentsListView.getSelectionModel().getSelectedItem().toString();
        }catch (NullPointerException ne){
            showAlert(Alert.AlertType.ERROR, "", "Invalid Student ID. Please try again");
            return;
        }

        paintStudentInfo( this.pj.getStudentsList().get(studentId));
    }

    /**
     * Project manager selects one of the shortlisted Project before adding member to the team.
     * If the team is already present for that project then render the team
     * Otherwise allow adding new people
     * @param mouseEvent
     */
    public void shortListedProjectClicked(MouseEvent mouseEvent) {

         //System.out.println(shortListedProjectsListView.getSelectionModel().getSelectedItem().toString());

         String currProjectId = shortListedProjectsListView.getSelectionModel().getSelectedItem().toString();

         // Render the project info
         paintProjectInfo( this.pj.getShortListedProjectsList().get(currProjectId) );

        // Check if the current selection already has a card
         for(int i = 0; i < this.cardProjeIdColln.length; i++){

             if(this.cardProjeIdColln[i].getText().contentEquals(currProjectId)){
                 // Update status bar
                 //System.out.println("The project already has a card");
                 return;
             }
         }

        // Assign this current selection a new card. Either empty or last available
        for(int i = 0; i < this.cardProjeIdColln.length; i++){
            System.out.println("I am in for");
            if(this.cardProjeIdColln[i].getText().contentEquals("") || this.cardProjeIdColln[i].getText().isBlank() || this.cardProjeIdColln[i].getText().isEmpty()){
                //System.out.println("I am in If");
                this.cardProjeIdColln[i].setText(currProjectId);
                return;
            }
        }
    }


    /**
     * Wrapper function for generating generic alerts
     * @param alertType : Error, success, prompt
     * @param message : Error message to be displayed with the alert
     */
    public void showAlert(Alert.AlertType alertType, String headerText, String message){
        Alert myAlert = new Alert(alertType);
        myAlert.setTitle("Require Attention");
        myAlert.setHeaderText(headerText);
        myAlert.setContentText(message);
        myAlert.getDialogPane().setMinWidth(550);
        myAlert.setResizable(true);
        myAlert.show();
    }


    /**
     * Save button clicked
     * Saving teams list to a binary file
     * @param mouseEvent
     */
    public void saveTeamsClicked(MouseEvent mouseEvent) {

        statusbarLabel.setText("Saving started ");
        statusbarLabel.setText("Saving to files");
        statusbarProgress.setProgress(0);

        // Saving data to files
        this.pj.saveDataToFiles();

        // Same copy will be saved to database as well
        statusbarLabel.setText("Saving to database");
        statusbarProgress.setProgress(0);
        this.pj.saveDataToDatabase();
        statusbarProgress.setProgress(100);

        statusbarLabel.setText("Saved successfully");
    }


    /**
     * Helper function that returns the ref to grid pane that has team with certain project ID
     * @param currProjectId : TeamGridPane for which project
     * @return : reference to that grid pane if found otherwise null
     */
    public GridPane getGridPaneForThisTeam(String currProjectId){

        // Check if the current selection already has a card
        for(int i = 0; i < this.cardProjeIdColln.length; i++){

            if(this.cardProjeIdColln[i].getText().contentEquals(currProjectId)){
                GridPane ref = (GridPane) this.cardProjeIdColln[i].getParent();
                System.out.println(ref.getId()+" is the ID");
                return ref;
            }
        }

        return null;
    }


    public void paintStudentInfo(Student stu){

        // Remove all existing datainside textflow
        this.studentInfoTextFlow.getChildren().removeAll( this.studentInfoTextFlow.getChildren() );

        StringBuilder studentInfo = new StringBuilder();

        // Student ID
        Text textEle = new Text("");

        studentInfo.append("Student ID : "+stu.getId()+"\n");
        studentInfo.append("Personality : "+ stu.getPersoanlity()+"\n");
        studentInfo.append("Conflicts : "+ stu.getCantWorkWith()+"\n");
        studentInfo.append("Grades : \n"+ stu.getGrades().toString()+"\n");
        studentInfo.append("Preferences : \n"+stu.getProjPreferences().toString()+"\n");

        textEle.setText(studentInfo.toString());
        studentInfoTextFlow.getChildren().add(textEle);
    }


    public void paintProjectInfo(Project pro){

        // Remove all existing datainside textflow
        this.projectInfoTextFlow.getChildren().removeAll( this.projectInfoTextFlow.getChildren() );

        StringBuilder projectInfo = new StringBuilder();

        // Student ID
        Text textEle = new Text("");

        projectInfo.append("Project ID : "+pro.getId()+"\n");
        projectInfo.append("Title : \n"+ pro.getTitle().substring(0,20)+"..."+"\n");
        projectInfo.append("Description :\n "+ pro.getDescription().substring(0,20)+ "..." +"\n");
        projectInfo.append("Requirements : \n"+ pro.getSoughtSkills().toString()+"\n");

        textEle.setText(projectInfo.toString());
        projectInfoTextFlow.getChildren().add(textEle);
    }

    /**
     * Removing students from the list
     * @param mouseEvent
     */
    public void removeStudentClicked(MouseEvent mouseEvent) {
        CheckBox [] selectedCbs = Arrays.stream(this.cardCheckBoxes).filter(x -> x.isSelected() == true).toArray(CheckBox[]::new);

        if(selectedCbs.length == 0){
            this.showAlert(Alert.AlertType.ERROR, "Invalid Selection", "You need to select atleast one student.");
            return;
        }

        for(CheckBox cb : selectedCbs){
            String stuTextId = cb.getId().replace("Cb", "");
            String stuId = ((Text) rootAnchorPane.lookup("#"+stuTextId)).getText();

            if(stuId == "" || stuId == null || stuId.isBlank() || stuId.isEmpty()){
                cb.setSelected(false);
                continue;
            }

            // Get the student reference from those ids
            Student s1Ref = this.pj.getStudentsList().get(stuId);

            // Remove that student from the current team
            Team team1Ref = s1Ref.getCurrTeamAssoc();
            team1Ref.removeMember(team1Ref.getProjectRef(), s1Ref);

            ((Text) rootAnchorPane.lookup("#"+stuTextId)).setText("");
            cb.setSelected(false);
        }

        populateStudentListView();
        renderTeams();
        renderGraphs();
    }

    /**
     * Menu option to clear all the teams
     * @param actionEvent
     */
    public void startOver(ActionEvent actionEvent) {

        this.pj.setTeamsList( new LinkedHashMap<String, Team>() );
        renderTeams();
        renderGraphs();
    }


    /**
     * Menu option to save and exit at the same time
     * @param actionEvent
     */
    public void saveAndClose(ActionEvent actionEvent) {

        this.pj.saveDataToFiles();
        this.pj.saveDataToDatabase();
        System.exit(0);
    }
}
