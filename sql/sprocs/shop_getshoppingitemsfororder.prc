SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Shop_GetShoppingItemsForOrder]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Shop_GetShoppingItemsForOrder]
GO

CREATE PROCEDURE Shop_GetShoppingItemsForOrder @order_id INT AS

SELECT	item_id, price, quantity
FROM		shopping_order_items
WHERE	shopping_order_items.order_id = @order_id
ORDER BY	item_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

