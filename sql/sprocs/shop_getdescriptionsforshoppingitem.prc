CREATE PROCEDURE Shop_GetDescriptionsForShoppingItem  @item_id INT AS

SELECT	number, description
FROM		shopping_order_item_descriptions
WHERE	shopping_order_item_descriptions.item_id = @item_id
ORDER BY	number
;
