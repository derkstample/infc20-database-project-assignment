-- Create the SQL login for java_app_user with a secure password (already present in database)
-- CREATE LOGIN java_app_user
-- WITH PASSWORD = '<Your Strong Password>';

-- Switch to the target database
USE FruityInc;

-- Create a user in the database mapped to the login
CREATE USER java_app_user FOR LOGIN java_app_user;

-- Grant EXECUTE permission on specific procedures

-- Customer CRUD procedures
GRANT EXECUTE ON uspAddCustomer 
TO java_app_user;

GRANT EXECUTE ON uspGetAllCustomers
TO java_app_user;

GRANT EXECUTE ON uspGetCustomerByAccountNo
TO java_app_user;

GRANT EXECUTE ON uspUpdateCustomer
TO java_app_user;

GRANT EXECUTE ON uspDeleteCustomer
TO java_app_user;

-- FruitBasket CRUD procedures
GRANT EXECUTE ON uspAddBasket
TO java_app_user;

GRANT EXECUTE ON uspGetAllBaskets
TO java_app_user;

GRANT EXECUTE ON uspGetBasketByBasketNo
TO java_app_user;

GRANT EXECUTE ON uspUpdateBasket
TO java_app_user;

GRANT EXECUTE ON uspDeleteBasket
TO java_app_user;

-- Purchase CRUD procedures
GRANT EXECUTE ON uspAddPurchase
TO java_app_user;

GRANT EXECUTE ON uspGetAllPurchases
TO java_app_user;

GRANT EXECUTE ON uspGetPurchaseByAccountNoBasketNo
TO java_app_user;

GRANT EXECUTE ON uspUpdatePurchase
TO java_app_user;

GRANT EXECUTE ON uspDeletePurchase
TO java_app_user;
