SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddExistingDocToMenu]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddExistingDocToMenu]
GO

CREATE PROCEDURE AddExistingDocToMenu 

	@meta_id int,
	@existing_meta_id int,
	@doc_menu_no int
as

begin
	-- test if this is the first child
	declare @countItem int , @manualSortOrder int, @newSortOrder int
	
	select @countItem = count(*) from childs 
	where meta_id = @meta_id  and menu_sort = @doc_menu_no
	
	set @manualSortOrder = 500

	if @countItem > 0   -- update manual_sort_order 
	begin
		select @manualSortOrder = max(manual_sort_order) from childs 
		where meta_id = @meta_id and menu_sort = @doc_menu_no
		
		if @manualSortOrder > 0 
			set @manualSortOrder = @manualSortOrder + 10 
	end

	-- test if child already exist in this menu. If not, then we will add the child to the menu.
	select @countItem = count(*) from childs 
	where meta_id = @meta_id and to_meta_id = @existing_meta_id and menu_sort = @doc_menu_no
	
	if @countItem = 0
	begin  
		insert into childs( meta_id, to_meta_id, menu_sort, manual_sort_order)
		values( @meta_id, @existing_meta_id, @doc_menu_no, @manualSortOrder )
		
	end	
	
end
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

