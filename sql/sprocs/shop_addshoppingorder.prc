CREATE PROCEDURE Shop_AddShoppingOrder @user_id INT, @datetime DATETIME AS
/**
	Add a shopping order to the database
**/

INSERT INTO	shopping_orders	(user_id, order_datetime)
VALUES			(@user_id, @datetime)

SELECT @@IDENTITY

;
