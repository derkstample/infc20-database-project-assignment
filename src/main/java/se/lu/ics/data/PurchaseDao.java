package se.lu.ics.data;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.lu.ics.models.Purchase;

public class PurchaseDao {

    private ConnectionHandler connectionHandler;

    public PurchaseDao() throws IOException {
        this.connectionHandler = new ConnectionHandler();
    }

    /**
     * Retrieves a Customer purchasing a FruitBasket by AccountNo and BasketNo from the database.
     * This method executes the stored procedure uspGetPurchaseByAccountNoBasketNo.
     *
     * @param accountNo The account number.
     * @param basketNo The basket number.
     * @return A Customer object.
     * @throws DaoException If there is an error accessing the database.
     */
    public Purchase getByAccountNoBasketNo(String accountNo, String basketNo) {
        String callProcedure = "{CALL uspGetPurchaseByAccountNoBasketNo(?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            statement.setString(1, accountNo);
            statement.setString(2, basketNo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Purchase(accountNo, basketNo, resultSet.getString("PurchaseDate"));
                } else {
                    return null; // basket not found
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error fetching purchase with AccountNo: " + accountNo + ", BasketNo:" +basketNo, e);
        }
    }

    /**
     * Retrieves all purchases from the database.
     * This method executes the stored procedure uspGetAllPurchases
     *
     * @return A list of Purchase objects.
     * @throws DaoException If there is an error accessing the database.
     */
    public List<Purchase> getAll() {
        String callProcedure = "{CALL uspGetAllPurchases}";
        List<Purchase> purchases = new ArrayList<>();

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure);
                ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set and map each row to a Purchase object
            while (resultSet.next()) {
                purchases.add(mapToPurchase(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException("Error fetching all purchases.", e);
        }

        return purchases;
    }

    /**
     * Saves a new purchase to the database.
     * This method executes the stored procedure uspAddPurchase
     *
     * @param customer The Customer object purchasing
     * @param basket The FruitBasket object being purchased
     * @param date The date of purchase
     * @throws DaoException If there is an error saving the purchase (e.g., if the unique key {AccountNo, BasketNo} already exists).
     */
    public void save(Purchase purchase) {
        String callProcedure = "{CALL uspAddPurchase(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set purchase data into the prepared statement
            statement.setString(1, purchase.getBasketNo());
            statement.setString(2, purchase.getAccountNo());
            statement.setString(3, purchase.getPurchaseDate());

            // Execute the insert operation
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627) { // Unique constraint violation
                throw new DaoException("A purchase with this AccountNo, BasketNo already exists.", e);
            } else {
                throw new DaoException("Error saving purchase " + purchase.getAccountNo() + ", "+purchase.getBasketNo() + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Updates an existing purchase's details in the database.
     * This method executes the stored procedure uspUpdatePurchase
     *
     * @param customer The new Customer object purchasing
     * @param basket The new FruitBasket object being purchased
     * @param date The new date of purchase
     * @throws DaoException If there is an error updating the purchase's data.
     */
    public void update(Purchase purchase) {
        String callProcedure = "{CALL uspUpdatePurchase(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set updated basket data into the prepared statement
            statement.setString(1, purchase.getAccountNo());
            statement.setString(2, purchase.getBasketNo());
            statement.setString(3, purchase.getPurchaseDate());

            // Execute the update operation
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error updating purchase: " + purchase.getAccountNo() + ", "+purchase.getBasketNo(), e);
        }
    }

    /**
     * Deletes a purchase from the database by {AccountNo, BasketNo}
     * This method executes the stored procedure uspDeletePurchase
     *
     * @param customer The Customer involved in the purchase
     * @param basket The FruitBasket involved in the purchase
     * @throws DaoException If there is an error deleting the purchase.
     */
    public void deleteByAccountNoBasketNo(String accountNo, String basketNo) {
        String callProcedure = "{CALL uspDeletePurchase(?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)) {

            // Set BasketNo in the prepared statement
            statement.setString(1, accountNo);
            statement.setString(2, basketNo);

            // Execute the delete operation
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error deleting purchase with AccountNo: " + accountNo + ", BasketNo: " + basketNo, e);
        }
    }

    /**
     * Maps a row in the ResultSet to a Purchase object.
     * This method is a helper function used to convert the result of a SQL query into a Customer object.
     *
     * @param resultSet The ResultSet containing the customer data.
     * @return A Customer object with the data from the ResultSet.
     * @throws SQLException If there is an error accessing the data in the ResultSet.
     */
    private Purchase mapToPurchase(ResultSet resultSet) throws SQLException {
        return new Purchase(
                resultSet.getString("AccountNo"),
                resultSet.getString("BasketNo"),
                resultSet.getString("PurchaseDate"));
    }
}