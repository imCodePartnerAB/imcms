CREATE PROCEDURE Shop_GetShoppingOrdersForUser @user_id INT AS
/**
	Get all shopping orders for a single user
**/

SELECT	order_id, order_datetime, user_id
FROM		shopping_orders
WHERE	shopping_orders.user_id = @user_id
ORDER BY	order_datetime
;
