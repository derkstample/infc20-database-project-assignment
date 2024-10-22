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
import se.lu.ics.data.StudentDao;
import se.lu.ics.models.Student;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for managing Student-related operations in the view.
 * This class interacts with the StudentDao to perform CRUD operations and 
 * updates the UI (TableView) accordingly. It handles user actions such as 
 * adding students and loading student data into the TableView.
 */
public class StudentViewController {

    @FXML
    private TableView<Student> tableViewStudent;

    @FXML
    private TableColumn<Student, String> tableColumnStudentPersonalNo;

    @FXML
    private TableColumn<Student, String> tableColumnStudentName; 

    @FXML
    private TableColumn<Student, String> tableColumnStudentEmail;

    @FXML
    private TextField textFieldStudentPersonalNo;

    @FXML
    private TextField textFieldStudentName; 

    @FXML
    private TextField textFieldStudentEmail;

    @FXML
    private Button btnStudentAdd;

    @FXML
    private Label labelErrorMessage;

    private StudentDao studentDao;

    /**
     * Constructor for StudentController.
     * It initializes the StudentDao to manage database interactions. 
     * If there is an error initializing the database, it will display an error message.
     */
    public StudentViewController() {
        try {
            studentDao = new StudentDao();
        } catch (IOException e) {
            displayErrorMessage("Error initializing database connection: " + e.getMessage());
        }
    }

    /**
     * Initializes the TableView by setting up the columns and loading the initial 
     * list of students from the database.
     */
    @FXML
    public void initialize() {
        // Set up table columns for displaying student data
        tableColumnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnStudentEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Load student data from the database
        loadStudents();
    }

    /**
     * Handles the event when the "Add" button is clicked. 
     * It reads the input from the text fields, creates a new Student object,
     * and saves it to the database using StudentDao. If successful, it refreshes
     * the TableView to display the newly added student.
     *
     * @param event MouseEvent triggered when the "Add" button is clicked.
     */
    @FXML
    private void buttonStudentAdd_OnClick(MouseEvent event) {
        clearErrorMessage();

        try {
            // Retrieve input from text fields
            String studentPersonalNo = textFieldStudentPersonalNo.getText();
            String studentName = textFieldStudentName.getText();
            String studentEmail = textFieldStudentEmail.getText();

            // Create a new Student object
            Student newStudent = new Student(studentPersonalNo, studentName, studentEmail);

            // Save the new student to the database
            studentDao.save(newStudent);

            // Refresh the TableView to display the new student
            loadStudents();

            // Clear input fields after successful addition
            textFieldStudentName.clear(); 
            textFieldStudentEmail.clear();
        } catch (DaoException e) {
            displayErrorMessage(e.getMessage());
        }
    }

    /**
     * Loads the list of students from the database and populates the TableView.
     * It retrieves all students using the StudentDao and displays them in the table.
     */
    private void loadStudents() {
        clearErrorMessage();
        try {
            // Fetch all students from the database
            List<Student> studentList = studentDao.getAll();
            // Convert the list to an ObservableList for TableView
            ObservableList<Student> studentObservableList = FXCollections.observableArrayList(studentList);
            // Set the items in the TableView
            tableViewStudent.setItems(studentObservableList);
        } catch (DaoException e) {
            displayErrorMessage("Error loading students: " + e.getMessage());
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