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
    private Button btnCustomerAdd;

    @FXML
    private Label labelErrorMessage;

    private CustomerDao customerDao;

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
     * Initializes the TableView by setting up the columns and loading the initial 
     * list of customers from the database.
     */
    @FXML
    public void initialize() {
        // Set up table columns for displaying customer data
        tableColumnCustomerAccountNo.setCellValueFactory(new PropertyValueFactory<>("accountNo"));
        tableColumnCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnCustomerDeliveryAddress.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));

        // Load customer data from the database
        loadCustomers();
    }

    /**
     * Handles the event when the "Add" button is clicked. 
     * It reads the input from the text fields, creates a new Customer object,
     * and saves it to the database using CustomerDao. If successful, it refreshes
     * the TableView to display the newly added customer.
     *
     * @param event MouseEvent triggered when the "Add" button is clicked.
     */
    @FXML
    private void buttonCustomerAdd_OnClick(MouseEvent event) {
        clearErrorMessage();

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
            textFieldCustomerAccountNo.clear();
            textFieldCustomerName.clear(); 
            textFieldCustomerDeliveryAddress.clear();
        } catch (DaoException e) {
            displayErrorMessage(e.getMessage());
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