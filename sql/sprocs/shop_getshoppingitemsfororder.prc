CREATE PROCEDURE Shop_GetShoppingItemsForOrder @order_id INT AS

SELECT	item_id, price, quantity
FROM		shopping_order_items
WHERE	shopping_order_items.order_id = @order_id
ORDER BY	item_id
