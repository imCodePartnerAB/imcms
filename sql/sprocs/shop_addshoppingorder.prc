SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Shop_AddShoppingOrder]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Shop_AddShoppingOrder]
;

CREATE PROCEDURE Shop_AddShoppingOrder @user_id INT, @datetime DATETIME AS
/**
	Add a shopping order to the database
**/

INSERT INTO	shopping_orders	(user_id, order_datetime)
VALUES			(@user_id, @datetime)

SELECT @@IDENTITY

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

