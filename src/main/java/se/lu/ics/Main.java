package se.lu.ics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            String path = "src/main/resources/se/lu/ics/fxml/StudentView.fxml"; //TODO: this seems to not be the correct path.. crashes on line 17

            // Load root layout from fxml file (on the classpath).
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            AnchorPane root = loader.load();

            // Create a scene and set it on the stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            // Set the stage title and show it
            primaryStage.setTitle("Students");
            primaryStage.show();

        } catch (Exception e) {
            // TODO: Error handling
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}