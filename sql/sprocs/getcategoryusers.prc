SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetCategoryUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetCategoryUsers]
GO


CREATE PROCEDURE GetCategoryUsers

/*
Used from servlet AdminUser 
Get all user in selected category and have ( fistname || lastname || loginname ) like searchString 
but for a Useradmin we only get those users with roles that the Useradmin has permission to administrate.
and not user with role Superadmin or Useradmin.
show only aktive users when @show= 1,  if show=0 show also not active users (active=0) 
*/
 @category int,
 @searchString varchar(20),
 @userId int,
 @show int
  
AS

DECLARE @isSuperadmin int 

SELECT @isSuperadmin = count(*) FROM user_roles_crossref 
WHERE role_id =0 AND user_id = @userId

SELECT @searchString = replace(@searchString, '*', '%' ) 

IF @category = -1 BEGIN  -- if all category

     IF @isSuperadmin = 1 BEGIN    	
	SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
	FROM users
	WHERE ( first_name like @searchString+'%' OR last_name like @searchString+'%' OR login_name like @searchString+'%' ) 
	       AND ( active=1 or active=@show )
	ORDER BY last_name
     END
     
     ELSE BEGIN	-- useradmin
	SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
	FROM users
	WHERE ( first_name like @searchString+'%' OR last_name like @searchString+'%' OR login_name like @searchString+'%' ) 
	      AND ( active=1 or active=@show )
		  AND user_id in (
			select user_id from user_roles_crossref 
			WHERE role_id IN (
					select role_id from useradmin_role_crossref 
					WHERE user_id = @userId) )
	      AND NOT user_id IN (   
 			SELECT user_roles_crossref.user_id FROM user_roles_crossref INNER JOIN
                      	roles ON user_roles_crossref.role_id = roles.role_id 
			WHERE     (roles.admin_role > 0) )  

	ORDER BY last_name
        
      END	
END

ELSE BEGIN  -- select only user with selected category

    IF @isSuperadmin = 1 BEGIN  -- if not a superadmin	
	SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
	FROM users
	WHERE user_type = @category
	     AND ( first_name like @searchString+'%' OR last_name like @searchString+'%' OR login_name like @searchString+'%' )
		 AND ( active=1 or active=@show )	
	ORDER BY last_name
    END

    ELSE BEGIN -- useradmin
	SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
	FROM users
	WHERE user_type = @category
		 AND ( first_name like @searchString+'%' OR last_name like @searchString+'%' OR login_name like @searchString+'%' )
		 AND ( active=1 or active=@show )
	     AND user_id in (
			select user_id from user_roles_crossref 
			WHERE role_id IN (
					select role_id from useradmin_role_crossref 
					WHERE user_id = @userId) )
	      AND NOT user_id IN (
 			SELECT user_roles_crossref.user_id FROM user_roles_crossref INNER JOIN
                      	roles ON user_roles_crossref.role_id = roles.role_id 
			WHERE     (roles.admin_role > 0) )
	
	ORDER BY last_name
	
    END
END

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

