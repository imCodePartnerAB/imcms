CREATE PROCEDURE Shop_GetShoppingOrderForUserById @user_id INT, @order_id INT AS
/**
	Get a single shopping order by its id.
**/
SELECT	order_id, order_datetime, user_id
FROM		shopping_orders
WHERE	order_id = @order_id
AND		user_id = @user_id
