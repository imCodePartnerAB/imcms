CREATE PROCEDURE Shop_AddShoppingItemToOrder @order_id INT, @price DECIMAL, @quantity INT AS
/**
	Add a shopping item to a shopping order
**/

INSERT INTO	shopping_order_items	(order_id,     price,    quantity)
VALUES				(@order_id, @price, @quantity)

SELECT @@IDENTITY

;
