SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Shop_GetShoppingOrdersForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Shop_GetShoppingOrdersForUser]
;

CREATE PROCEDURE Shop_GetShoppingOrdersForUser @user_id INT AS
/**
	Get all shopping orders for a single user
**/

SELECT	order_id, order_datetime, user_id
FROM		shopping_orders
WHERE	shopping_orders.user_id = @user_id
ORDER BY	order_datetime
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

