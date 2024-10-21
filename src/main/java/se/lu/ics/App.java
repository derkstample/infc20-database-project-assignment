package se.lu.ics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        //this is autogenerated from maven's javafx project; set up a simple window that toggles between a primary view and a secondary view
        scene = new Scene(loadFXML("primary"), 640, 480); //different views/scenes are defined in fxml files located in resources
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        
        //TODO: move this out of main, will need to be broken into separate classes
        //use jdbc driver to communicate with Azure server using config.properties in resources folder
        Properties connectionProps = new Properties();
        try{
            FileInputStream stream = new FileInputStream("src/main/resources/se/lu/ics/config.properties");
            connectionProps.load(stream);

            //collect relevant properties
            String databaseServerName = (String) connectionProps.get("database.server.name");
            String databaseServerPort = (String) connectionProps.get("database.server.port");
            String databaseName = (String) connectionProps.get("database.name");
            String databaseUserName = (String) connectionProps.get("database.user.name");
            String databaseUserPassword = (String) connectionProps.get("database.user.password");

            //build our connection string
            String connectionURL = "jdbc:sqlserver://"
            + databaseServerName + ":"
            + databaseServerPort + ";"
            + "database=" + databaseName + ";"
            + "user=" + databaseUserName + ";"
            + "password=" + databaseUserPassword + ";"
            + "encrypt=true;" // required for JDBC driver v10.2
            + "trustServerCertificate=true;"; // required for JDBC driver v10.2

            //what we will query the database with
            String query = "SELECT *";

            Connection connection = DriverManager.getConnection(connectionURL);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            //iterate through each tuple returned by the query
            while(resultSet.next()){
                //TODO: do something relevant with the returned tuples
                System.out.println("Course:" + resultSet.getString("CourseCode"));
            }

            //make sure to close our connection
            connection.close();
            resultSet.close();

        }catch(IOException e){
            System.out.println("Could not load properties file!");
            System.exit(1);
        }catch(SQLException e){
            e.printStackTrace();
            System.exit(1);
        }

        //launch the javafx window
        launch();
    }
}