package se.lu.ics.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import se.lu.ics.data.DaoException;
import se.lu.ics.Main;
import se.lu.ics.data.FruitBasketDao;
import se.lu.ics.models.FruitBasket;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for managing FruitBasket-related operations in the view.
 * This class interacts with the FruitBasketDao to perform CRUD operations and 
 * updates the UI (TableView) accordingly. It handles user actions such as 
 * adding baskets and loading basket data into the TableView.
 */
public class FruitBasketViewController {

    @FXML
    private TableView<FruitBasket> tableViewFruitBasket;

    @FXML
    private TableColumn<FruitBasket, String> tableColumnFruitBasketBasketNo;

    @FXML
    private TableColumn<FruitBasket, String> tableColumnFruitBasketName; 

    @FXML
    private TableColumn<FruitBasket, Double> tableColumnFruitBasketPrice;

    @FXML
    private TextField textFieldFruitBasketBasketNo;

    @FXML
    private TextField textFieldFruitBasketName; 

    @FXML
    private TextField textFieldFruitBasketPrice;

    @FXML
    private Button btnFruitBasketAddUpdate;

    @FXML
    private Button btnFruitBasketDelete;

    @FXML
    private Label labelErrorMessage;

    private FruitBasketDao basketDao;

    private Main mainApp;

    /**
     * Constructor for FruitBasketController.
     * It initializes the FruitBasketDao to manage database interactions. 
     * If there is an error initializing the database, it will display an error message.
     */
    public FruitBasketViewController() {
        try {
            basketDao = new FruitBasketDao();
        } catch (IOException e) {
            displayErrorMessage("Error initializing database connection: " + e.getMessage());
        }
    }

    /**
     * Setter for Main mainApp, used for switching between views
     * @param mainApp the Main application handling this FruitBasketViewController
     */

     public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    /**
     * Handles the event when the "Customer Table" button is clicked. 
     * It calls Main.loadCustomerView(), which changes the currently loaded scene to be controlled by CustomerViewController
     *
     * @param event MouseEvent triggered when the "Customer Table" button is clicked.
     */

     @FXML
     private void buttonFruitBasketSwitchToCustomer_OnClick(MouseEvent e){
         mainApp.loadCustomerView();
     }

      /**
     * Handles the event when the "Purchase Table" button is clicked. 
     * It calls Main.loadPurchaseView(), which changes the currently loaded scene to be controlled by PurchaseViewController
     *
     * @param event MouseEvent triggered when the "Purchase Table" button is clicked.
     */

    @FXML
    private void buttonFruitBasketSwitchToPurchase_OnClick(MouseEvent e){
        mainApp.loadPurchaseView();
    }

    /**
     * Initializes the TableView by setting up the columns and loading the initial 
     * list of baskets from the database.
     */
    @FXML
    public void initialize() {
        // Set up table columns for displaying basket data
        tableColumnFruitBasketBasketNo.setCellValueFactory(new PropertyValueFactory<>("basketNo"));
        tableColumnFruitBasketName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnFruitBasketPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        // set up listener for tableView selection changes, thank you copilot
        tableViewFruitBasket.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> populateFields(newValue));

        // Load basket data from the database
        loadBaskets();
    }

    /**
     * Populates the TextFields of the FruitBasketView with the attributes of the
     * currently selected basket in the TableView, or clears the fields if
     * a basket is deselected
     * 
     * @param basket the basket whose attributes will populate the text fields
     */
    private void populateFields(FruitBasket basket){
        if(basket != null){
            textFieldFruitBasketBasketNo.setText(basket.getBasketNo());
            textFieldFruitBasketName.setText(basket.getName());
            textFieldFruitBasketPrice.setText(basket.getPrice()+"");
            btnFruitBasketAddUpdate.setText("Update");
        }else{
            clearFields(); // surrounding with a try/catch didn't seem as appropriate here as just
            // checking if basket is null, since it shouldn't be an error if it is
        }
    }

/**
     * Handles the event when the "Add/Update" button is clicked. 
     * Depending on if a preexisting basket is selected or not,
     * it reads the input from the text fields, updates or creates a new FruitBasket object,
     * and updates or saves it to the database using FruitBasketDao. If successful, it refreshes
     * the TableView to display the newly updated/added basket.
     *
     * @param event MouseEvent triggered when the "Add/Update" button is clicked.
     */
    @FXML
    private void buttonFruitBasketAddUpdate_OnClick(MouseEvent event) {
        clearErrorMessage();

        if(btnFruitBasketAddUpdate.getText().equals("Add")) addFruitBasket();
        else updateFruitBasket();
    }

    private void addFruitBasket(){
        try {
            // Retrieve input from text fields
            String basketNo = textFieldFruitBasketBasketNo.getText();
            String basketName = textFieldFruitBasketName.getText();
            Double basketPrice = Double.parseDouble(textFieldFruitBasketPrice.getText());

            // Create a new FruitBasket object
            FruitBasket newBasket = new FruitBasket(basketNo, basketName, basketPrice);

            // Save the new basket to the database
            basketDao.save(newBasket);

            // Refresh the TableView to display the new customer
            loadBaskets();

            // Clear input fields after successful addition
            clearFields();
        } catch (DaoException e) {
            displayErrorMessage(e.getMessage());
        } catch (NumberFormatException e){
            displayErrorMessage("Price must be a decimal value!");
        }
    }

    private void updateFruitBasket(){
        try{
            // get selected basket from tableView
            FruitBasket selectedBasket = tableViewFruitBasket.getSelectionModel().getSelectedItem();

            String newBasketNo = textFieldFruitBasketBasketNo.getText();
            String newName = textFieldFruitBasketName.getText();
            Double newPrice = Double.parseDouble(textFieldFruitBasketPrice.getText());

            if(selectedBasket.getBasketNo().equals(newBasketNo)){ // we can only update if the basket number is unchanged
                // set the new name and price for update
                selectedBasket.setName(newName);
                selectedBasket.setPrice(newPrice);

                // use the Dao to update
                basketDao.update(selectedBasket);
                
                // Refresh the TableView to display the updated basket
                loadBaskets();

                // Clear input fields after successful update
                clearFields();
            }else{ // if user tries to change basket number, display an error
                displayErrorMessage("Cannot update fruit basket number!");
            }
        } catch(DaoException e){
            displayErrorMessage(e.getMessage());
        } catch(NullPointerException e){
            displayErrorMessage("No basket selected!");
        } catch(NumberFormatException e){
            displayErrorMessage("Price must be a decimal value!");
        }
    }

    private void clearFields(){
        textFieldFruitBasketBasketNo.clear();
        textFieldFruitBasketName.clear();
        textFieldFruitBasketPrice.clear();
        btnFruitBasketAddUpdate.setText("Add");
        tableViewFruitBasket.getSelectionModel().clearSelection();
    }

    /**
     * Handles the event when the "Delete" button is clicked. 
     * It reads the basketNo of the currently highlighted
     * basket on the table and deletes it using the FruitBasketDao. 
     * If successful, it refreshes the TableView.
     *
     * @param event MouseEvent triggered when the "Delete" button is clicked.
     */

    @FXML
    private void buttonFruitBasketDelete_OnClick(MouseEvent event){
        clearErrorMessage();

        try{
            // get selected basket
            FruitBasket selectedBasket = tableViewFruitBasket.getSelectionModel().getSelectedItem();
            String basketNo = selectedBasket.getBasketNo();

            // use Dao to delete it
            basketDao.deleteByBasketNo(basketNo);

            // Refresh the TableView
            loadBaskets();
        } catch(DaoException e){
            displayErrorMessage(e.getMessage());
        } catch(NullPointerException e){
            displayErrorMessage("No basket selected to delete!");
        }
    }

    /**
     * Loads the list of baskets from the database and populates the TableView.
     * It retrieves all baskets using the FruitBasketDao and displays them in the table.
     */
    private void loadBaskets() {
        clearErrorMessage();
        try {
            // Fetch all baskets from the database
            List<FruitBasket> basketList = basketDao.getAll();
            // Convert the list to an ObservableList for TableView
            ObservableList<FruitBasket> basketObservableList = FXCollections.observableArrayList(basketList);
            // Set the items in the TableView
            tableViewFruitBasket.setItems(basketObservableList);
        } catch (DaoException e) {
            displayErrorMessage("Error loading fruit baskets: " + e.getMessage());
        }
    }

    /**
     * Displays an error message in the label and changes its text color to red.
     *
     * @param message The error message to display.
     */
    private void displayErrorMessage(String message) {
        labelErrorMessage.setText(message);
        labelErrorMessage.setStyle("-fx-text-fill: red;");
    }

    /**
     * Clears any displayed error messages in the label.
     */
    private void clearErrorMessage() {
        labelErrorMessage.setText("");
    }
}