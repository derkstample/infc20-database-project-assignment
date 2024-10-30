package se.lu.ics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import se.lu.ics.controllers.CustomerViewController;
import se.lu.ics.controllers.FruitBasketViewController;
import se.lu.ics.controllers.PurchaseViewController;

public class Main extends Application {

    private Stage primaryStage;
    private Scene scene;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadCustomerView();
    }

    public void loadCustomerView(){
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/fxml/CustomerView.fxml"));
            AnchorPane customerRoot = loader.load();

            // Create a scene and set it on the stage
            scene = new Scene(customerRoot);
            primaryStage.setScene(scene);

            // Set the stage title and show it
            primaryStage.setTitle("Customers");

            // CustomerViewController needs a reference to this to switch views
            CustomerViewController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFruitBasketView(){
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/fxml/FruitBasketView.fxml"));
            AnchorPane basketRoot = loader.load();

            // Update the root of the scene
            scene.setRoot(basketRoot);

            // Set the stage title and show it
            primaryStage.setTitle("Fruit Basket");

            // FruitBasketViewController needs a reference to this to switch views
            FruitBasketViewController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPurchaseView(){
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/se/lu/ics/fxml/PurchaseView.fxml"));
            AnchorPane purchaseRoot = loader.load();

            // Update the root of the scene
            scene.setRoot(purchaseRoot);

            // Set the stage title and show it
            primaryStage.setTitle("Purchase");

            // PurchaseViewController needs a reference to this to switch views
            PurchaseViewController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}