SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Shop_AddShoppingItemToOrder]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Shop_AddShoppingItemToOrder]
GO

CREATE PROCEDURE Shop_AddShoppingItemToOrder @order_id INT, @price DECIMAL, @quantity INT AS
/**
	Add a shopping item to a shopping order
**/

INSERT INTO	shopping_order_items	(order_id,     price,    quantity)
VALUES				(@order_id, @price, @quantity)

SELECT @@IDENTITY

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

