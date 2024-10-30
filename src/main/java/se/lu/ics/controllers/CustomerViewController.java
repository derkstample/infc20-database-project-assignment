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
import se.lu.ics.data.CustomerDao;
import se.lu.ics.models.Customer;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for managing Customer-related operations in the view.
 * This class interacts with the CustomerDao to perform CRUD operations and 
 * updates the UI (TableView) accordingly. It handles user actions such as 
 * adding customers and loading customer data into the TableView.
 */
public class CustomerViewController {

    @FXML
    private TableView<Customer> tableViewCustomer;

    @FXML
    private TableColumn<Customer, String> tableColumnCustomerAccountNo;

    @FXML
    private TableColumn<Customer, String> tableColumnCustomerName; 

    @FXML
    private TableColumn<Customer, String> tableColumnCustomerDeliveryAddress;

    @FXML
    private TextField textFieldCustomerAccountNo;

    @FXML
    private TextField textFieldCustomerName; 

    @FXML
    private TextField textFieldCustomerDeliveryAddress;

    @FXML
    private Button btnCustomerAddUpdate;

    @FXML
    private Button btnCustomerDelete;

    @FXML
    private Label labelErrorMessage;

    private CustomerDao customerDao;

    private Main mainApp;

    /**
     * Constructor for CustomerController.
     * It initializes the CustomerDao to manage database interactions. 
     * If there is an error initializing the database, it will display an error message.
     */
    public CustomerViewController() {
        try {
            customerDao = new CustomerDao();
        } catch (IOException e) {
            displayErrorMessage("Error initializing database connection: " + e.getMessage());
        }
    }

    /**
     * Setter for Main mainApp, used for switching between views
     * @param mainApp the Main application handling this CustomerViewController
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
     private void buttonCustomerSwitchToFruitBasket_OnClick(MouseEvent e){
         mainApp.loadFruitBasketView();
     }

     /**
     * Handles the event when the "Purchase Table" button is clicked. 
     * It calls Main.loadPurchaseView(), which changes the currently loaded scene to be controlled by PurchaseViewController
     *
     * @param event MouseEvent triggered when the "Purchase Table" button is clicked.
     */

     @FXML
     private void buttonCustomerSwitchToPurchase_OnClick(MouseEvent e){
         mainApp.loadPurchaseView();
     }

    /**
     * Initializes the TableView by setting up the columns and loading the initial 
     * list of customers from the database.
     */
    @FXML
    public void initialize() {
        // Set up table columns for displaying customer data
        tableColumnCustomerAccountNo.setCellValueFactory(new PropertyValueFactory<>("accountNo"));
        tableColumnCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnCustomerDeliveryAddress.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));

        // set up listener for tableView selection changes, thank you copilot
        tableViewCustomer.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> populateFields(newValue));

        // Load customer data from the database
        loadCustomers();
    }

    /**
     * Populates the TextFields of the CustomerView with the attributes of the
     * currently selected customer in the TableView, or clears the fields if
     * a customer is deselected
     * 
     * @param customer the customer whose attributes will populate the text fields
     */
    private void populateFields(Customer customer){
        if(customer != null){
            textFieldCustomerAccountNo.setText(customer.getAccountNo());
            textFieldCustomerName.setText(customer.getName());
            textFieldCustomerDeliveryAddress.setText(customer.getDeliveryAddress());
            btnCustomerAddUpdate.setText("Update");
        }else{
            clearFields(); // surrounding with a try/catch didn't seem as appropriate here as just
            // checking if customer is null, since it shouldn't be an error if it is
        }
    }

    /**
     * Handles the event when the "Add/Update" button is clicked. 
     * Depending on if a preexisting customer is selected or not,
     * it reads the input from the text fields, updates or creates a new Customer object,
     * and updates or saves it to the database using CustomerDao. If successful, it refreshes
     * the TableView to display the newly updated/added customer.
     *
     * @param event MouseEvent triggered when the "Add/Update" button is clicked.
     */
    @FXML
    private void buttonCustomerAddUpdate_OnClick(MouseEvent event) {
        clearErrorMessage();

        if(btnCustomerAddUpdate.getText().equals("Add")) addCustomer();
        else updateCustomer();
    }

    private void addCustomer(){
        try {
            // Retrieve input from text fields
            String customerAccountNo = textFieldCustomerAccountNo.getText();
            String customerName = textFieldCustomerName.getText();
            String customerDeliveryAddress = textFieldCustomerDeliveryAddress.getText();

            // Create a new Customer object
            Customer newCustomer = new Customer(customerAccountNo, customerName, customerDeliveryAddress);

            // Save the new customer to the database
            customerDao.save(newCustomer);

            // Refresh the TableView to display the new customer
            loadCustomers();

            // Clear input fields after successful addition
            clearFields();
        } catch (DaoException e) {
            displayErrorMessage(e.getMessage());
        }
    }

    private void updateCustomer(){
        try{
            // get selected customer from tableView
            Customer selectedCustomer = tableViewCustomer.getSelectionModel().getSelectedItem();

            String newAccountNo = textFieldCustomerAccountNo.getText();
            String newName = textFieldCustomerName.getText();
            String newDeliveryAddress = textFieldCustomerDeliveryAddress.getText();

            if(selectedCustomer.getAccountNo().equals(newAccountNo)){ // we can only update if the account number is unchanged
                // set the new name and delivery address for update
                selectedCustomer.setName(newName);
                selectedCustomer.setDeliveryAddress(newDeliveryAddress);

                // use the Dao to update
                customerDao.update(selectedCustomer);
                
                // Refresh the TableView to display the updated customer
                loadCustomers();

                // Clear input fields after successful update
                clearFields();
            }else{ // if user tries to change account number, display an error
                displayErrorMessage("Cannot update customer account number!");
            }
        } catch(DaoException e){
            displayErrorMessage(e.getMessage());
        } catch(NullPointerException e){
            displayErrorMessage("No customer selected!");
        }
    }

    private void clearFields(){
        textFieldCustomerAccountNo.clear();
        textFieldCustomerName.clear();
        textFieldCustomerDeliveryAddress.clear();
        btnCustomerAddUpdate.setText("Add");
        tableViewCustomer.getSelectionModel().clearSelection();
    }

    /**
     * Handles the event when the "Delete" button is clicked. 
     * It reads the accountNo of the currently highlighted
     * customer on the table and deletes it using the CustomerDao. 
     * If successful, it refreshes the TableView.
     *
     * @param event MouseEvent triggered when the "Delete" button is clicked.
     */

    @FXML
    private void buttonCustomerDelete_OnClick(MouseEvent event){
        clearErrorMessage();

        try{
            // get selected customer
            Customer selectedCustomer = tableViewCustomer.getSelectionModel().getSelectedItem();
            String accountNo = selectedCustomer.getAccountNo();

            // use Dao to delete
            customerDao.deleteByAccountNo(accountNo);

            // Refresh the TableView
            loadCustomers();
        } catch(DaoException e){
            displayErrorMessage(e.getMessage());
        } catch(NullPointerException e){
            displayErrorMessage("No customer selected to delete!");
        }
    }

    /**
     * Loads the list of customers from the database and populates the TableView.
     * It retrieves all customers using the CustomerDao and displays them in the table.
     */
    private void loadCustomers() {
        clearErrorMessage();
        try {
            // Fetch all customers from the database
            List<Customer> customerList = customerDao.getAll();
            // Convert the list to an ObservableList for TableView
            ObservableList<Customer> customerObservableList = FXCollections.observableArrayList(customerList);
            // Set the items in the TableView
            tableViewCustomer.setItems(customerObservableList);
        } catch (DaoException e) {
            displayErrorMessage("Error loading customers: " + e.getMessage());
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