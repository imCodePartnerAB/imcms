CREATE PROCEDURE Shop_AddShoppingItemDescription @item_id INT, @number INT, @description VARCHAR(100) AS
/**
	Add a description to an item in a shopping order.
**/
INSERT INTO	shopping_order_item_descriptions	(item_id,		number,		description)
VALUES						(@item_id,	@number,	@description)
