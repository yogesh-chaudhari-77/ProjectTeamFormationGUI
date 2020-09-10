import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
References :
[1] kordamp/bootstrapfx kordamp/bootstrapfx (2020). Available at: https://github.com/kordamp/bootstrapfx (Accessed: 2 September 2020).

[2] supported, I., Annikov, K., Marati, A., Sithole, S. and Jansen, V.
supported, I. et al. (2020) IntelliJ: Error:java: error: release version 5 not supported, Stack Overflow. Available at: https://stackoverflow.com/questions/59601077/intellij-errorjava-error-release-version-5-not-supported (Accessed: 2 September 2020).
 */
// Revised HelloWorld application that uses the JavaFX base class
// The main method is optional in this case!

public class HelloFX extends Application {

    // The main method is optional with Application classes.
    // Use launch() to cause the other methods to be run in sequence.
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        System.out.println("Entering start method");
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
        gridPane.setMinSize(640, 480);

        // Set padding - 10 from all sides
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        // Gaps between 2 columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        // Set alignment of the grid
        //gridPane.setAlignment(Pos.CENTER);

        GridPane teamPane;
        teamPane = generateATeamPanel();
        GridPane teamPane2 = generateATeamPanel();
        GridPane teamPane3 = generateATeamPanel();
        GridPane teamPane4 = generateATeamPanel();
        GridPane teamPane5 = generateATeamPanel();
        // Start arranging nodes on the grid pane

        gridPane.addRow(0);

        gridPane.addColumn(0, teamPane);
        gridPane.addColumn(1, teamPane2);
        gridPane.addColumn(2, teamPane3);
        gridPane.addColumn(3, teamPane4);
        gridPane.addColumn(4, teamPane5);

        gridPane.addRow(1);

        gridPane.addRow(2, studentIdInput, addStudentBtn);

        gridPane.addRow(3,swapStudentsBtn);

        Scene scene = new Scene(gridPane, 640, 480);

        // We can have multiple scenes. Setup this one, and tell the stage to show it.
        stage.setScene(scene);
        stage.show();
    }

    public GridPane generateATeamPanel(){

        GridPane teamPane = new GridPane();

        Text [] studentIdsText = new Text[4];
        CheckBox [] studentSelectCb = new CheckBox[4];

        for(int i = 0; i < 4; i++){
            studentIdsText[i] = new Text("s"+i);
            studentSelectCb[i] = new CheckBox();
            teamPane.addRow(i, studentIdsText[i], studentSelectCb[i]);
        }

        return teamPane;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Entering init method");
    }
}