SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Shop_AddShoppingItemDescription]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Shop_AddShoppingItemDescription]
GO

CREATE PROCEDURE Shop_AddShoppingItemDescription @item_id INT, @number INT, @description VARCHAR(100) AS
/**
	Add a description to an item in a shopping order.
**/
INSERT INTO	shopping_order_item_descriptions	(item_id,		number,		description)
VALUES						(@item_id,	@number,	@description)

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

