CREATE TABLE Customer (
  CustomerID 		INTEGER IDENTITY(1,1),
  AccountNo     	VARCHAR(10) NOT NULL, 
  CustomerName   	VARCHAR(20),
  DeliveryAddress 	VARCHAR(20),

  CONSTRAINT PK_Customer_CustomerID PRIMARY KEY(CustomerID),
  CONSTRAINT UQ_Customer_AccountNo UNIQUE(AccountNo)
);

CREATE TABLE FruitBasket (
  BasketID    	INTEGER IDENTITY(1,1),
  BasketNo  	VARCHAR(10) NOT NULL,
  BasketName  	VARCHAR(30),
  Price     	DECIMAL(5,2),

  CONSTRAINT PK_FruitBasket_BasketID PRIMARY KEY(BasketID),
  CONSTRAINT UQ_FruitBasket_BasketNo UNIQUE(BasketNo),
);

CREATE TABLE Purchase (
  CustomerID 	INTEGER,
  BasketID  	INTEGER,
  PurchaseDate	DATE,

  CONSTRAINT PK_Purchase_CustomerID_BasketID PRIMARY KEY(CustomerID, BasketID),
  CONSTRAINT FK_Purchase_Customer_CustomerID FOREIGN KEY(CustomerID)
  	REFERENCES Customer(CustomerID) ON DELETE CASCADE,
  CONSTRAINT FK_Purchase_FruitBasket_BasketID FOREIGN KEY(BasketID)
  	REFERENCES FruitBasket(BasketID) ON DELETE CASCADE
);
