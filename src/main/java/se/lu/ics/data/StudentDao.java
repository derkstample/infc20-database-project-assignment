package se.lu.ics.data;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.lu.ics.models.Course;
import se.lu.ics.models.Student;

public class StudentDao {

    private ConnectionHandler connectionHandler;

    public StudentDao() throws IOException {
        this.connectionHandler = new ConnectionHandler();
    }

    /**
     * Retrieves all students from the database.
     * This method executes the stored procedure uspGetAllStudents
     *
     * @return A list of Student objects.
     * @throws DaoException If there is an error accessing the database.
     */
    public List<Student> getAll() {
        String callProcedure = "{CALL uspGetAllStudents}";
        List<Student> students = new ArrayList<>();

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure);
                ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set and map each row to a Student object
            while (resultSet.next()) {
                students.add(mapToStudent(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException("Error fetching all students.", e);
        }

        return students;
    }

    /**
     * Retrieves a Student by PersonalNo from the database.
     * This method executes the stored procedure uspGetStudentByPersonalNo.
     *
     * @param personalNo The personal number.
     * @return A Student object.
     * @throws DaoException If there is an error accessing the database.
     */
    public Student getByPersonalNo(String personalNo) {
        String callProcedure = "{CALL uspGetStudentByPersonalNo(?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            statement.setString(1, personalNo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToStudent(resultSet);
                } else {
                    return null; // student not found
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error fetching student with PersonalNo: " + personalNo, e);
        }
    }

    /**
     * Saves a new student to the database.
     * This method executes the stored procedure uspInsertStudent
     *
     * @param student The Student object containing the data to be saved.
     * @throws DaoException If there is an error saving the student (e.g., if the Student No already exists).
     */
    public void save(Student student) {
        String callProcedure = "{CALL uspInsertStudent(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set student data into the prepared statement
            statement.setString(1, student.getPersonalNo());
            statement.setString(2, student.getName());
            statement.setString(3, student.getEmail());

            // Execute the insert operation
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627) { // Unique constraint violation
                throw new DaoException("A student with this PersonalNo already exists.", e);
            } else {
                throw new DaoException("Error saving student: " + student.getPersonalNo(), e);
            }
        }
    }

    /**
     * Updates an existing student's details in the database.
     * This method executes the stored procedure uspUpdateStudent
     *
     * @param student The Student object containing the updated data.
     * @throws DaoException If there is an error updating the student's data.
     */
    public void update(Student student) {
        String callProcedure = "{CALL uspUpdateStudent(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set updated student data into the prepared statement
            statement.setString(1, student.getPersonalNo());
            statement.setString(2, student.getName());
            statement.setString(3, student.getEmail());

            // Execute the update operation
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error updating student: " + student.getPersonalNo(), e);
        }
    }

    /**
     * Deletes an student from the database by PersonalNo
     * This method executes the stored procedure uspDeleteStudent
     *
     * @param personalNo The PersonalNo of the student to be deleted.
     * @throws DaoException If there is an error deleting the student.
     */
    public void deleteByNo(String personalNo) {
        String callProcedure = "{CALL uspDeleteStudent(?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set PersonalNo in the prepared statement
            statement.setString(1, personalNo);

            // Execute the delete operation
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error deleting student with PersonalNo: " + personalNo, e);
        }
    }

    /**
     * Retrieves all students and their respective courses.
     * This method executes the stored procedure uspGetAllStudentsWithDepartments.
     *
     * @return A list of Student objects, each containing their courses.
     * @throws DaoException If there is an error accessing the database.
     */
    public List<Student> getAllStudentsWithCourses() {
        String callProcedure = "{CALL uspGetAllStudentsWithDepartments}";
        List<Student> students = new ArrayList<>();

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure);
                ResultSet resultSet = statement.executeQuery()) {

            // Map to hold Student by PersonalNo
            Map<String, Student> studentMap = new HashMap<>();

            while (resultSet.next()) {
                String studentPersonalNo = resultSet.getString("StudentPersonalNo");
                String studentName = resultSet.getString("StudentName");
                String studentEmail = resultSet.getString("StudentEmail");
                String courseCode = resultSet.getString("CourseCode");
                String courseName = resultSet.getString("CourseName");
                int courseCredits = resultSet.getInt("CourseCredits");
                // Get or create Student object
                Student student = studentMap.get(studentPersonalNo);
                if (student == null) {
                    student = new Student(studentPersonalNo, studentName, studentEmail);
                    studentMap.put(studentPersonalNo, student);
                }

                // Create Course object with credits and add to the student's course list
                Course course = new Course(courseCode, courseName, courseCredits);
                student.getCourses().add(course);
            }

            // Add all students to the list
            students.addAll(studentMap.values());

        } catch (SQLException e) {
            throw new DaoException("Error fetching students and their courses.", e);
        }

        return students;
    }

    /**
     * Maps a row in the ResultSet to a Student object.
     * This method is a helper function used to convert the result of a SQL query into an Student object.
     *
     * @param resultSet The ResultSet containing the student data.
     * @return An Student object with the data from the ResultSet.
     * @throws SQLException If there is an error accessing the data in the ResultSet.
     */
    private Student mapToStudent(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getString("PersonalNo"),
                resultSet.getString("Name"),
                resultSet.getString("Email"));
    }
}