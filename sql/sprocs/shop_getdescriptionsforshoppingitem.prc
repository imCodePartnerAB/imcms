SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Shop_GetDescriptionsForShoppingItem]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Shop_GetDescriptionsForShoppingItem]
GO

CREATE PROCEDURE Shop_GetDescriptionsForShoppingItem  @item_id INT AS

SELECT	number, description
FROM		shopping_order_item_descriptions
WHERE	shopping_order_item_descriptions.item_id = @item_id
ORDER BY	number
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

