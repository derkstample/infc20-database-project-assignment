-- Customer Stored Procedures --

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Find the corresponding CustomerID (surrogate key) from a given AccountNo (business key)
-- Parameters: @AccountNo - The account number of the customer.
-- ================================================
CREATE OR ALTER FUNCTION ufnGetCustomerID(@AccountNo VARCHAR(10))
RETURNS INT
AS
BEGIN
	DECLARE @CustomerID INT;
	
	-- Retrieve the CustomerID based on the provided AccountNo
	SELECT @CustomerID = CustomerID
	FROM Customer
	WHERE AccountNo = @AccountNo;
	
	RETURN @CustomerID
END;
GO

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Inserts a new customer record into the Customer table.
-- Parameters: @AccountNo - The account number of the customer.
-- @CustomerName - The name of the customer.
-- @DeliveryAddress - The customer's delivery address.
-- ================================================
CREATE OR ALTER PROCEDURE uspAddCustomer
	@AccountNo 	 VARCHAR(10),
	@CustomerName 	 VARCHAR(20),
	@DeliveryAddress VARCHAR(20)
AS
BEGIN
	SET NOCOUNT ON;
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Insert the customer record into the Customer table
		INSERT INTO Customer(AccountNo, CustomerName, DeliveryAddress)
		VALUES (@AccountNo, @CustomerName, @DeliveryAddress);
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		-- Rollback the transaction to discard any changes
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		-- Rethrow the original error
		;THROW;
	END CATCH
END;
GO

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Retrieves all customers attributes (exluding surrogate keys) from the Customer table.
-- ================================================
CREATE OR ALTER PROCEDURE uspGetAllCustomers
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Select all records from the Customer table
		SELECT
			AccountNo,
			CustomerName AS Name,
			DeliveryAddress AS Address
		FROM Customer
		
		-- Commit the transaction if successful
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		-- Rollback the transaction in case of an error
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		-- Rethrow the error to be handled by the caller
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Returns the customer details for the given AccountNo, excluding CustomerID (surrogate key).
-- =============================================
CREATE OR ALTER PROCEDURE uspGetCustomerByAccountNo
	@AccountNo VARCHAR(10)
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		SELECT
			AccountNo,
			CustomerName AS Name,
			DeliveryAddress AS Address
		FROM Customer
		WHERE AccountNo = @AccountNo
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Updates Customer details for the given AccountNo
-- Parameters:
-- @AccountNo - The account number (business key) of the customer to update.
-- @CustomerName - The new name of the customer.
-- @DeliveryAddress - The new delivery address of the customer.
-- =============================================
CREATE OR ALTER PROCEDURE uspUpdateCustomer
	@AccountNo 		VARCHAR(10),
	@CustomerName  		VARCHAR(20),
	@DeliveryAddress 	VARCHAR(20)
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	DECLARE @CustomerID INT;
	SET @CustomerID = dbo.ufnGetCustomerID(@AccountNo)
	
	-- Check if the CustomerID was found
	IF @CustomerID IS NULL
		BEGIN
		;THROW 50001, 'Customer not found', 1; -- Raise a custom error for Customer not found
	END
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		UPDATE Customer
		SET CustomerName = @CustomerName, DeliveryAddress = @DeliveryAddress
		WHERE CustomerID = @CustomerID;
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Deletes a customer based on the given AccountNo.
-- Parameters:
-- @AccountNo - The account number (business key) of the customer to delete.
-- =============================================
CREATE OR ALTER PROCEDURE uspDeleteCustomer
	@AccountNo 	VARCHAR(10)
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	DECLARE @CustomerID INT;
	SET @CustomerID = dbo.ufnGetCustomerID(@AccountNo)
	
	-- Check if the CustomerID was found
	IF @CustomerID IS NULL
		BEGIN
		;THROW 50001, 'Customer not found', 1; -- Raise a custom error for Customer not found
	END
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Delete the purchase where CustomerIDs match
		DELETE
		FROM Customer
		WHERE CustomerID = @CustomerID;
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-30]
-- Description: Raise error and prevent insertion of Customers with bad AccountNo formatting
-- ================================================

CREATE OR ALTER TRIGGER CustomerAccountNoFormatTrigger
ON Customer
AFTER INSERT
AS
BEGIN
	-- Check if any inserted row has bad formatting
	IF EXISTS(
		SELECT 1
		FROM inserted
		WHERE AccountNo NOT LIKE 'C[0-9][0-9][0-9]'
	)
	BEGIN
		;THROW 50000, 'Customer AccountNo must be formatted as Cnnn, where n is a decimal digit', 1;
		ROLLBACK  TRANSACTION
	END
END

-- FruitBasket Stored Procedures --

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Find the corresponding BasketID (surrogate key) from a given BasketNo (business key)
-- Parameters: @BasketNo - The number of the basket.
-- ================================================
CREATE OR ALTER FUNCTION ufnGetBasketID(@BasketNo VARCHAR(10))
RETURNS INT
AS
BEGIN
	DECLARE @BasketID INT;
	
	-- Retrieve the BasketID based on the provided BasketNo
	SELECT @BasketID = BasketID
	FROM FruitBasket
	WHERE BasketNo = @BasketNo;
	
	RETURN @BasketID
END;
GO

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Inserts a new fruit basket record into the FruitBasket table.
-- Parameters: @BasketNo - The number of the basket.
-- @BasketName - The name of the basket.
-- @Price - The price of the basket.
-- ================================================
CREATE OR ALTER PROCEDURE uspAddBasket
	@BasketNo 	 VARCHAR(10),
	@BasketName 	 VARCHAR(30),
	@Price	 	 DECIMAL(5,2)
AS
BEGIN
	SET NOCOUNT ON;
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Insert the basket record into the FruitBasket table
		INSERT INTO FruitBasket(BasketNo, BasketName, Price)
		VALUES (@BasketNo, @BasketName, @Price);
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		-- Rollback the transaction to discard any changes
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		-- Rethrow the original error
		;THROW;
	END CATCH
END;
GO

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Retrieves all baskets' attributes (exluding surrogate keys) from the FruitBasket table.
-- ================================================
CREATE OR ALTER PROCEDURE uspGetAllBaskets
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Select all records from the FruitBasket table
		SELECT
			BasketNo,
			BasketName AS Name,
			Price
		FROM FruitBasket
		
		-- Commit the transaction if successful
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		-- Rollback the transaction in case of an error
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		-- Rethrow the error to be handled by the caller
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Returns the basket details for the given BasketNo, excluding BasketID (surrogate key).
-- =============================================
CREATE OR ALTER PROCEDURE uspGetBasketByBasketNo
	@BasketNo VARCHAR(10)
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		SELECT
			BasketNo,
			BasketName AS Name,
			Price
		FROM FruitBasket
		WHERE BasketNo = @BasketNo
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Updates FruitBasket details for the given BasketNo
-- Parameters:
-- @BasketNo - The number (business key) of the basket to update.
-- @BasketName - The new name of the basket.
-- @Price - The new price of the basket.
-- =============================================
CREATE OR ALTER PROCEDURE uspUpdateBasket
	@BasketNo 		VARCHAR(10),
	@BasketName  		VARCHAR(30),
	@Price 			DECIMAL(5,2)
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	DECLARE @BasketID INT;
	SET @BasketID = dbo.ufnGetBasketID(@BasketNo)
	
	-- Check if the BasketID was found
	IF @BasketID IS NULL
		BEGIN
		;THROW 50001, 'Basket not found', 1; -- Raise a custom error for Basket not found
	END
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		UPDATE FruitBasket
		SET BasketName = @BasketName, Price = @Price
		WHERE BasketID = @BasketID;
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Deletes a basket based on the given BasketNo.
-- Parameters:
-- @BasketNo - The number (business key) of the basket to delete.
-- =============================================
CREATE OR ALTER PROCEDURE uspDeleteBasket
	@BasketNo 	VARCHAR(10)
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	DECLARE @BasketID INT;
	SET @BasketID = dbo.ufnGetBasketID(@BasketNo)
	
	-- Check if the BasketID was found
	IF @BasketID IS NULL
		BEGIN
		;THROW 50001, 'Basket not found', 1; -- Raise a custom error for Basket not found
	END
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Delete the purchase where BasketID match
		DELETE
		FROM FruitBasket
		WHERE BasketID = @BasketID;
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-30]
-- Description: Raise error and prevent insertion of FruitBaskets with bad BasketNo formatting
-- ================================================

CREATE OR ALTER TRIGGER FruitBasketBasketNoFormatTrigger
ON FruitBasket
AFTER INSERT
AS
BEGIN
	-- Check if any inserted row has bad formatting
	IF EXISTS(
		SELECT 1
		FROM inserted
		WHERE BasketNo NOT LIKE 'B[0-9][0-9]'
	)
	BEGIN
		;THROW 50000, 'FruitBasket BasketNo must be formatted as Bnn, where n is a decimal digit', 1;
		ROLLBACK  TRANSACTION
	END
END

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-30]
-- Description: Raise error and prevent insertion of FruitBaskets with bad Price formatting
-- ================================================

CREATE OR ALTER TRIGGER FruitBasketPriceFormatTrigger
ON FruitBasket
AFTER INSERT
AS
BEGIN
	-- Check if any inserted row has bad formatting
	IF EXISTS(
		SELECT 1
		FROM inserted
		WHERE Price NOT LIKE '[0-9]%.[0-9][0-9]' -- apparently % is used as + for regular expressions?
	)
	BEGIN
		;THROW 50000, 'FruitBasket Price must be formatted as numbers with two decimal digits', 1;
		ROLLBACK  TRANSACTION
	END
END

-- Purchase Stored Procedures --

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Find the corresponding CustomerID (surrogate key) from a given AccountNo (business key)
-- Parameters: @AccountNo - The account number of the customer.
-- ================================================
CREATE OR ALTER FUNCTION ufnGetCustomerID(@AccountNo VARCHAR(10))
RETURNS INT
AS
BEGIN
	DECLARE @CustomerID INT;
	
	-- Retrieve the CustomerID based on the provided AccountNo
	SELECT @CustomerID = CustomerID
	FROM Customer
	WHERE AccountNo = @AccountNo;
	
	RETURN @CustomerID
END;
GO

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Find the corresponding BasketID (surrogate key) from a given BasketNo (business key)
-- Parameters: @BasketNo - The number of the basket.
-- ================================================
CREATE OR ALTER FUNCTION ufnGetBasketID(@BasketNo VARCHAR(10))
RETURNS INT
AS
BEGIN
	DECLARE @BasketID INT;
	
	-- Retrieve the BasketID based on the provided BasketNo
	SELECT @BasketID = BasketID
	FROM FruitBasket
	WHERE BasketNo = @BasketNo;
	
	RETURN @BasketID
END;
GO

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Inserts a new purchase record into the Purchase table.
-- Parameters: @BasketNo - The number of the fruit basket the purchase belongs to.
-- @AccountNo - The number of the customer purchasing the basket.
-- @PurchaseDate - The date of purchase.
-- ================================================
CREATE OR ALTER PROCEDURE uspAddPurchase
	@BasketNo VARCHAR(10),
	@AccountNo VARCHAR(10),
	@PurchaseDate DATE
AS
BEGIN
	SET NOCOUNT ON;
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	
	DECLARE @CustomerID INT;
	DECLARE @BasketID INT;
	SET @CustomerID = dbo.ufnGetCustomerID(@AccountNo)
	SET @BasketID = dbo.ufnGetBasketID(@BasketNo)
	
	-- Check if the CustomerID was found
	IF @CustomerID IS NULL
		BEGIN
		;THROW 50001, 'Customer not found', 1; -- Raise a custom error for Customer not found
	END
	-- Check if the BasketID was found
	IF @BasketID IS NULL
		BEGIN
		;THROW 50001, 'Basket not found', 1; -- Raise a custom error for Basket not found
	END
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Insert the purchase record into the Purchase table
		INSERT INTO Purchase(CustomerID, BasketID, PurchaseDate)
		VALUES (@CustomerID, @BasketID, @PurchaseDate);
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		-- Rollback the transaction to discard any changes
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		-- Rethrow the original error
		;THROW;
	END CATCH
END;
GO

-- ================================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Retrieves all customers and their respective fruit baskets from the Purchase table.
-- ================================================
CREATE OR ALTER PROCEDURE uspGetAllPurchases
AS
BEGIN
	-- Set transaction isolation level to prevent dirty reads
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	-- Turn off the message that shows the number of rows affected
	SET NOCOUNT ON;
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Select all records from the Purchase table
		SELECT
			Customer.AccountNo,
			FruitBasket.BasketNo,
			Purchase.PurchaseDate
		FROM Customer
		JOIN Purchase ON Customer.CustomerID = Purchase.CustomerID
		JOIN FruitBasket ON Purchase.BasketID = FruitBasket.BasketID;
		
		-- Commit the transaction if successful
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		-- Rollback the transaction in case of an error
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		-- Rethrow the error to be handled by the caller
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Returns the purchase  details for the given EmpNo, excluding EmployeeID (surrogate key).
-- =============================================
CREATE OR ALTER PROCEDURE uspGetPurchaseByAccountNoBasketNo
	@AccountNo VARCHAR(10),
	@BasketNo  VARCHAR(10)
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		SELECT
			CustomerName,
			DeliveryAddress,
			BasketName,
			Price,
			PurchaseDate
		FROM Customer
		JOIN Purchase ON Customer.CustomerID = Purchase.CustomerID
		JOIN FruitBasket ON Purchase.BasketID = FruitBasket.BasketID
		WHERE AccountNo = @AccountNo AND BasketNo = @BasketNo
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Updates Purchase details for the given AccountNo and BasketNo
-- Parameters:
-- @AccountNo - The account number (business key) of the purchase to update.
-- @BasketNo - The basket number (business key) of the purchase to udpate.
-- @PurchaseDate - The new date of purchase.
-- =============================================
CREATE OR ALTER PROCEDURE uspUpdatePurchase
	@AccountNo 	VARCHAR(10),
	@BasketNo  	VARCHAR(10),
	@PurchaseDate 	DATE
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	DECLARE @CustomerID INT;
	DECLARE @BasketID INT;
	SET @CustomerID = dbo.ufnGetCustomerID(@AccountNo)
	SET @BasketID = dbo.ufnGetBasketID(@BasketNo)
	
	-- Check if the CustomerID was found
	IF @CustomerID IS NULL
		BEGIN
		;THROW 50001, 'Customer not found', 1; -- Raise a custom error for Customer not found
	END
	-- Check if the BasketID was found
	IF @BasketID IS NULL
		BEGIN
		;THROW 50001, 'Basket not found', 1; -- Raise a custom error for Basket not found
	END
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		UPDATE Purchase
		SET PurchaseDate = @PurchaseDate
		WHERE CustomerID = @CustomerID AND BasketID = @BasketID;
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;
GO

-- =============================================
-- Author: [Derek Rodriguez]
-- Create date: [2024-10-22]
-- Description: Deletes a purchase based on the given AccountNo and BasketNo.
-- Parameters:
-- @AccountNo - The account number (business key) of the purchase to delete.
-- @BasketNo - The basket number (business key) of the purchase to delete.
-- =============================================
CREATE OR ALTER PROCEDURE uspDeletePurchase
	@AccountNo 	VARCHAR(10),
	@BasketNo	VARCHAR(10)
AS
BEGIN
	SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
	SET NOCOUNT ON;
	
	DECLARE @CustomerID INT;
	DECLARE @BasketID INT;
	SET @CustomerID = dbo.ufnGetCustomerID(@AccountNo)
	SET @BasketID = dbo.ufnGetBasketID(@BasketNo)
	
	-- Check if the CustomerID was found
	IF @CustomerID IS NULL
		BEGIN
		;THROW 50001, 'Customer not found', 1; -- Raise a custom error for Customer not found
	END 
	-- Check if the BasketID was found
	IF @BasketID IS NULL
		BEGIN
		;THROW 50001, 'Basket not found', 1; -- Raise a custom error for Basket not found
	END
	
	BEGIN TRY
		BEGIN TRANSACTION;
		
		-- Delete the purchase where CustomerID and BasketID match
		DELETE
		FROM Purchase
		WHERE CustomerID = @CustomerID AND BasketID = @BasketID;
		
		COMMIT TRANSACTION;
	END TRY
	BEGIN CATCH
		IF @@TRANCOUNT > 0
		BEGIN
			ROLLBACK TRANSACTION;
		END
		;THROW;
	END CATCH
END;