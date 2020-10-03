import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/views/VisualSensitiveAnalysis.fxml"));
        primaryStage.setTitle("Project Team Formation v 0.1");
        primaryStage.setScene(new Scene(root, 750, 581));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
