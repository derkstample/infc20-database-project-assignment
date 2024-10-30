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

public class FruitBasketDao {

    private ConnectionHandler connectionHandler;

    public FruitBasketDao() throws IOException {
        this.connectionHandler = new ConnectionHandler();
    }

    /**
     * Retrieves all fruit baskets from the database.
     * This method executes the stored procedure uspGetAllBaskets
     *
     * @return A list of Basket objects.
     * @throws DaoException If there is an error accessing the database.
     */
    public List<FruitBasket> getAll() {
        String callProcedure = "{CALL uspGetAllBaskets}";
        List<FruitBasket> baskets = new ArrayList<>();

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure);
                ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set and map each row to a FruitBasket object
            while (resultSet.next()) {
                baskets.add(mapToFruitBasket(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException("Error fetching all baskets.", e);
        }

        return baskets;
    }

    /**
     * Retrieves a FruitBasket by BasketNo from the database.
     * This method executes the stored procedure uspGetBasketByBasketNo.
     *
     * @param basketNo The basket number.
     * @return A FruitBasket object.
     * @throws DaoException If there is an error accessing the database.
     */
    public FruitBasket getByBasketNo(String basketNo) {
        String callProcedure = "{CALL uspGetBasketByBasketNo(?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            statement.setString(1, basketNo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToFruitBasket(resultSet);
                } else {
                    return null; // basket not found
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error fetching basket with BasketNo: " + basketNo, e);
        }
    }

    /**
     * Saves a new basket to the database.
     * This method executes the stored procedure uspAddBasket
     *
     * @param basket The FruitBasket object containing the data to be saved.
     * @throws DaoException If there is an error saving the basket (e.g., if the BasketNo already exists).
     */
    public void save(FruitBasket basket) {
        String callProcedure = "{CALL uspAddBasket(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set basket data into the prepared statement
            statement.setString(1, basket.getBasketNo());
            statement.setString(2, basket.getName());
            statement.setDouble(3, basket.getPrice());

            // Execute the insert operation
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627) { // Unique constraint violation
                throw new DaoException("A basket with this BasketNo already exists.", e);
            } else {
                throw new DaoException("Error saving basket " + basket.getBasketNo() + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Updates an existing basket's details in the database.
     * This method executes the stored procedure uspUpdateBasket
     *
     * @param basket The FruitBasket object containing the updated data.
     * @throws DaoException If there is an error updating the basket's data.
     */
    public void update(FruitBasket basket) {
        String callProcedure = "{CALL uspUpdateBasket(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set updated basket data into the prepared statement
            statement.setString(1, basket.getBasketNo());
            statement.setString(2, basket.getName());
            statement.setDouble(3, basket.getPrice());

            // Execute the update operation
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error updating basket: " + basket.getBasketNo(), e);
        }
    }

    /**
     * Deletes a basket from the database by BasketNo
     * This method executes the stored procedure uspDeleteBasket
     *
     * @param basketNo The BasketNo of the basket to be deleted.
     * @throws DaoException If there is an error deleting the basket.
     */
    public void deleteByBasketNo(String basketNo) {
        String callProcedure = "{CALL uspDeleteBasket(?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set BasketNo in the prepared statement
            statement.setString(1, basketNo);

            // Execute the delete operation
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error deleting basket with BasketNo: " + basketNo, e);
        }
    }

    /**
     * Retrieves all baskets and their respective customers.
     * This method executes the stored procedure uspGetAllPurchases.
     *
     * @return A list of FruitBasket objects, each containing their customers.
     * @throws DaoException If there is an error accessing the database.
     */
    public List<FruitBasket> getAllBasketsWithCustomers() {
        String callProcedure = "{CALL uspGetAllPurchases}";
        List<FruitBasket> baskets = new ArrayList<>();

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure);
                ResultSet resultSet = statement.executeQuery()) {

            // Map to hold FruitBasket by BasketNo
            Map<String, FruitBasket> basketMap = new HashMap<>();

            while (resultSet.next()) {
                String customerAccountNo = resultSet.getString("AccountNo");
                String customerName = resultSet.getString("CustomerName");
                String customerDeliveryAddress = resultSet.getString("DeliveryAddress");
                String basketNo = resultSet.getString("BasketNo");
                String basketName = resultSet.getString("BasketName");
                double basketPrice = resultSet.getInt("Price");
                // Get or create FruitBasket object
                FruitBasket basket = basketMap.get(basketNo);
                if (basket == null) {
                    basket = new FruitBasket(basketNo, basketName, basketPrice);
                    basketMap.put(basketNo, basket);
                }

                // Create Customer object with name and deliveryAddress and add to the basket's customer list
                Customer customer = new Customer(customerAccountNo, customerName, customerDeliveryAddress);
                basket.getCustomers().add(customer);
            }

            // Add all baskets to the list
            baskets.addAll(basketMap.values());

        } catch (SQLException e) {
            throw new DaoException("Error fetching baskets and their customers.", e);
        }

        return baskets;
    }

    /**
     * Maps a row in the ResultSet to a FruitBasket object.
     * This method is a helper function used to convert the result of a SQL query into a FruitBasket object.
     *
     * @param resultSet The ResultSet containing the basket data.
     * @return A FruitBasket object with the data from the ResultSet.
     * @throws SQLException If there is an error accessing the data in the ResultSet.
     */
    private FruitBasket mapToFruitBasket(ResultSet resultSet) throws SQLException {
        return new FruitBasket(
                resultSet.getString("BasketNo"),
                resultSet.getString("Name"),
                resultSet.getDouble("Price"));
    }
}