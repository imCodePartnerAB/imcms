SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Shop_GetShoppingOrderForUserById]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Shop_GetShoppingOrderForUserById]
;

CREATE PROCEDURE Shop_GetShoppingOrderForUserById @user_id INT, @order_id INT AS
/**
	Get a single shopping order by its id.
**/
SELECT	order_id, order_datetime, user_id
FROM		shopping_orders
WHERE	order_id = @order_id
AND		user_id = @user_id
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

