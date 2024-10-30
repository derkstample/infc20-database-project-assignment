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
import se.lu.ics.data.PurchaseDao;
import se.lu.ics.models.Purchase;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for managing Purchase-related operations in the view.
 * This class interacts with the PurchaseDao to perform CRUD operations and 
 * updates the UI (TableView) accordingly. It handles user actions such as 
 * adding Purchases and loading Purchase data into the TableView.
 */
public class PurchaseViewController {

    @FXML
    private TableView<Purchase> tableViewPurchase;

    @FXML
    private TableColumn<Purchase, String> tableColumnPurchaseAccountNo;

    @FXML
    private TableColumn<Purchase, String> tableColumnPurchaseBasketNo; 

    @FXML
    private TableColumn<Purchase, String> tableColumnPurchaseDate;

    @FXML
    private TextField textFieldPurchaseAccountNo;

    @FXML
    private TextField textFieldPurchaseBasketNo; 

    @FXML
    private TextField textFieldPurchaseDate;

    @FXML
    private Button btnPurchaseAddUpdate;

    @FXML
    private Button btnPurchaseDelete;

    @FXML
    private Label labelErrorMessage;

    private PurchaseDao purchaseDao;

    private Main mainApp;

    /**
     * Constructor for PurchaseViewController.
     * It initializes the PurchaseDao to manage database interactions. 
     * If there is an error initializing the database, it will display an error message.
     */
    public PurchaseViewController() {
        try {
            purchaseDao = new PurchaseDao();
        } catch (IOException e) {
            displayErrorMessage("Error initializing database connection: " + e.getMessage());
        }
    }

    /**
     * Setter for Main mainApp, used for switching between views
     * @param mainApp the Main application handling this PurchaseViewController
     */

    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    /**
     * Handles the event when the "Fruit Basket Table" button is clicked. 
     * It calls Main.loadFruitBasketView(), which changes the currently loaded scene to be controlled by FruitBasketViewController
     *
     * @param event MouseEvent triggered when the "Fruit Basket Table" button is clicked.
     */

     @FXML
     private void buttonPurchaseSwitchToFruitBasket_OnClick(MouseEvent e){
         mainApp.loadFruitBasketView();
     }

     /**
     * Handles the event when the "Customer Table" button is clicked. 
     * It calls Main.loadCustomerView(), which changes the currently loaded scene to be controlled by CustomerViewController
     *
     * @param event MouseEvent triggered when the "Customer Table" button is clicked.
     */

     @FXML
     private void buttonPurchaseSwitchToCustomer_OnClick(MouseEvent e){
         mainApp.loadCustomerView();
     }

    /**
     * Initializes the TableView by setting up the columns and loading the initial 
     * list of Purchases from the database.
     */
    @FXML
    public void initialize() {
        // Set up table columns for displaying Purchase data
        tableColumnPurchaseAccountNo.setCellValueFactory(new PropertyValueFactory<>("accountNo"));
        tableColumnPurchaseBasketNo.setCellValueFactory(new PropertyValueFactory<>("basketNo"));
        tableColumnPurchaseDate.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));

        // set up listener for tableView selection changes, thank you copilot
        tableViewPurchase.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> populateFields(newValue));

        // Load Purchase data from the database
        loadPurchases();
    }

    /**
     * Populates the TextFields of the PurchaseView with the attributes of the
     * currently selected Purchase in the TableView, or clears the fields if
     * a Purchase is deselected
     * 
     * @param purchase the Purchase whose attributes will populate the text fields
     */
    private void populateFields(Purchase purchase){
        if(purchase != null){
            textFieldPurchaseAccountNo.setText(purchase.getAccountNo());
            textFieldPurchaseBasketNo.setText(purchase.getBasketNo());
            textFieldPurchaseDate.setText(purchase.getPurchaseDate());
            btnPurchaseAddUpdate.setText("Update");
        }else{
            clearFields(); // surrounding with a try/catch didn't seem as appropriate here as just
            // checking if Purchase is null, since it shouldn't be an error if it is
        }
    }

    /**
     * Handles the event when the "Add/Update" button is clicked. 
     * Depending on if a preexisting Purchase is selected or not,
     * it reads the input from the text fields, updates or creates a new Purchase object,
     * and updates or saves it to the database using PurchaseDao. If successful, it refreshes
     * the TableView to display the newly updated/added Purchase.
     *
     * @param event MouseEvent triggered when the "Add/Update" button is clicked.
     */
    @FXML
    private void buttonPurchaseAddUpdate_OnClick(MouseEvent event) {
        clearErrorMessage();

        if(btnPurchaseAddUpdate.getText().equals("Add")) addPurchase();
        else updatePurchase();
    }

    private void addPurchase(){
        try {
            // Retrieve input from text fields
            String purchaseAccountNo = textFieldPurchaseAccountNo.getText();
            String purchaseBasketNo = textFieldPurchaseBasketNo.getText();
            String purchaseDate = textFieldPurchaseDate.getText();

            // Create a new Purchase object
            Purchase newPurchase = new Purchase(purchaseAccountNo, purchaseBasketNo, purchaseDate);

            // Save the new Purchase to the database
            purchaseDao.save(newPurchase);

            // Refresh the TableView to display the new Purchase
            loadPurchases();

            // Clear input fields after successful addition
            clearFields();
        } catch (DaoException e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void updatePurchase(){
        try{
            // get selected Purchase from tableView
            Purchase selectedPurchase = tableViewPurchase.getSelectionModel().getSelectedItem();

            String newAccountNo = textFieldPurchaseAccountNo.getText();
            String newBasketNo = textFieldPurchaseBasketNo.getText();
            String newDate = textFieldPurchaseDate.getText();

            if(selectedPurchase.getAccountNo().equals(newAccountNo) && selectedPurchase.getBasketNo().equals(newBasketNo)){ // we can only update if the primary key is unchanged
                // set the new date for update
                selectedPurchase.setPurchaseDate(newDate);

                // use the Dao to update
                purchaseDao.update(selectedPurchase);
                
                // Refresh the TableView to display the updated Purchase
                loadPurchases();

                // Clear input fields after successful update
                clearFields();
            }else{ // if user tries to change primary key, display an error
                displayErrorMessage("Cannot update account number or basket number! Delete old purchase and add a new one.");
            }
        } catch(DaoException e){
            displayErrorMessage(e.getMessage());
        } catch(NullPointerException e){
            displayErrorMessage("No purchase selected!");
        }
    }

    private void clearFields(){
        textFieldPurchaseAccountNo.clear();
        textFieldPurchaseBasketNo.clear();
        textFieldPurchaseDate.clear();
        btnPurchaseAddUpdate.setText("Add");
        tableViewPurchase.getSelectionModel().clearSelection();
    }

    /**
     * Handles the event when the "Delete" button is clicked. 
     * It reads the accountNo + basketNo of the currently highlighted
     * Purchase on the table and deletes it using the PurchaseDao. 
     * If successful, it refreshes the TableView.
     *
     * @param event MouseEvent triggered when the "Delete" button is clicked.
     */

    @FXML
    private void buttonPurchaseDelete_OnClick(MouseEvent event){
        clearErrorMessage();

        try{
            // get selected Purchase
            Purchase selectedPurchase = tableViewPurchase.getSelectionModel().getSelectedItem();
            String accountNo = selectedPurchase.getAccountNo();
            String basketNo = selectedPurchase.getBasketNo();

            // use Dao to delete
            purchaseDao.deleteByAccountNoBasketNo(accountNo,basketNo);

            // Refresh the TableView
            loadPurchases();
        } catch(DaoException e){
            displayErrorMessage(e.getMessage());
        } catch(NullPointerException e){
            displayErrorMessage("No purchase selected to delete!");
        }
    }

    /**
     * Loads the list of Purchases from the database and populates the TableView.
     * It retrieves all Purchases using the PurchaseDao and displays them in the table.
     */
    private void loadPurchases() {
        clearErrorMessage();
        try {
            // Fetch all Purchases from the database
            List<Purchase> purchaseList = purchaseDao.getAll();
            // Convert the list to an ObservableList for TableView
            ObservableList<Purchase> purchaseObservableList = FXCollections.observableArrayList(purchaseList);
            // Set the items in the TableView
            tableViewPurchase.setItems(purchaseObservableList);
        } catch (DaoException e) {
            displayErrorMessage("Error loading purchases: " + e.getMessage());
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