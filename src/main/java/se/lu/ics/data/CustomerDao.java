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

import se.lu.ics.models.FruitBasket;
import se.lu.ics.models.Customer;

public class CustomerDao {

    private ConnectionHandler connectionHandler;

    public CustomerDao() throws IOException {
        this.connectionHandler = new ConnectionHandler();
    }

    /**
     * Retrieves all customers from the database.
     * This method executes the stored procedure uspGetAllCustomers
     *
     * @return A list of Customer objects.
     * @throws DaoException If there is an error accessing the database.
     */
    public List<Customer> getAll() {
        String callProcedure = "{CALL uspGetAllCustomers}";
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure);
                ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set and map each row to a Customer object
            while (resultSet.next()) {
                customers.add(mapToCustomer(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException("Error fetching all customers.", e);
        }

        return customers;
    }

    /**
     * Retrieves a Customer by AccountNo from the database.
     * This method executes the stored procedure uspGetCustomerByAccountNo.
     *
     * @param accountNo The account number.
     * @return A Customer object.
     * @throws DaoException If there is an error accessing the database.
     */
    public Customer getByAccountNo(String accountNo) {
        String callProcedure = "{CALL uspGetCustomerByAccountNo(?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            statement.setString(1, accountNo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToCustomer(resultSet);
                } else {
                    return null; // customer not found
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error fetching customer with AccountNo: " + accountNo, e);
        }
    }

    /**
     * Saves a new customer to the database.
     * This method executes the stored procedure uspAddCustomer
     *
     * @param customer The Customer object containing the data to be saved.
     * @throws DaoException If there is an error saving the customer (e.g., if the AccountNo already exists).
     */
    public void save(Customer customer) {
        String callProcedure = "{CALL uspAddCustomer(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set student data into the prepared statement
            statement.setString(1, customer.getAccountNo());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getDeliveryAddress());

            // Execute the insert operation
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627) { // Unique constraint violation
                throw new DaoException("A customer with this AccountNo already exists.", e);
            } else {
                throw new DaoException("Error saving customer: " + customer.getAccountNo(), e);
            }
        }
    }

    /**
     * Updates an existing customer's details in the database.
     * This method executes the stored procedure uspUpdateCustomer
     *
     * @param customer The Customer object containing the updated data.
     * @throws DaoException If there is an error updating the customer's data.
     */
    public void update(Customer customer) {
        String callProcedure = "{CALL uspUpdateCustomer(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set updated customer data into the prepared statement
            statement.setString(1, customer.getAccountNo());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getDeliveryAddress());

            // Execute the update operation
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error updating customer: " + customer.getAccountNo(), e);
        }
    }

    /**
     * Deletes a customer from the database by AccountNo
     * This method executes the stored procedure uspDeleteCustomer
     *
     * @param accountNo The AccountNo of the customer to be deleted.
     * @throws DaoException If there is an error deleting the customer.
     */
    public void deleteByAccountNo(String accountNo) {
        String callProcedure = "{CALL uspDeleteStudent(?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set PersonalNo in the prepared statement
            statement.setString(1, accountNo);

            // Execute the delete operation
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error deleting customer with AccountNo: " + accountNo, e);
        }
    }

    /**
     * Retrieves all customers and their respective fruit baskets.
     * This method executes the stored procedure uspGetAllPurchases.
     *
     * @return A list of Customer objects, each containing their baskets.
     * @throws DaoException If there is an error accessing the database.
     */
    public List<Customer> getAllCustomersWithBaskets() {
        String callProcedure = "{CALL uspGetAllPurchases}";
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure);
                ResultSet resultSet = statement.executeQuery()) {

            // Map to hold Customer by AccountNo
            Map<String, Customer> customerMap = new HashMap<>();

            while (resultSet.next()) {
                String customerAccountNo = resultSet.getString("AccountNo");
                String customerName = resultSet.getString("CustomerName");
                String customerDeliveryAddress = resultSet.getString("DeliveryAddress");
                String basketNo = resultSet.getString("BasketNo");
                String basketName = resultSet.getString("BasketName");
                double basketPrice = resultSet.getInt("Price");
                // Get or create Customer object
                Customer customer = customerMap.get(customerAccountNo);
                if (customer == null) {
                    customer = new Customer(customerAccountNo, customerName, customerDeliveryAddress);
                    customerMap.put(customerAccountNo, customer);
                }

                // Create FruitBasket object with price and add to the customer's basket list
                FruitBasket basket = new FruitBasket(basketNo, basketName, basketPrice);
                customer.getBaskets().add(basket);
            }

            // Add all customers to the list
            customers.addAll(customerMap.values());

        } catch (SQLException e) {
            throw new DaoException("Error fetching customers and their baskets.", e);
        }

        return customers;
    }

    /**
     * Maps a row in the ResultSet to a Customer object.
     * This method is a helper function used to convert the result of a SQL query into a Customer object.
     *
     * @param resultSet The ResultSet containing the customer data.
     * @return A Customer object with the data from the ResultSet.
     * @throws SQLException If there is an error accessing the data in the ResultSet.
     */
    private Customer mapToCustomer(ResultSet resultSet) throws SQLException {
        return new Customer(
                resultSet.getString("AccountNo"),
                resultSet.getString("Name"),
                resultSet.getString("Address"));
    }
}