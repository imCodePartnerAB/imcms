if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SearchForUsersByName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SearchForUsersByName]
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

CREATE PROCEDURE SearchForUsersByName

 /*
Used from servlet AdminUser 
Get all users with ( firstname || lastname || loginname ) like searchString
but for a Useradmin we only get those users with roles that the Useradmin has permission to administrate.
and not user with role Superadmin or Useradmin.
Parameter @showAll = 1 : all users don't care about serchstring 
          @showAll = 0 : only users like serchstring 		 
          @active = 1 : only active users (where active = 1)
          @active = 0 : all users  (where active= 0 or 1)
    
We never show USER where loginname = 'user'
 */

 @searchString varchar(20),
 @userId int,
 @showAll bit,
 @active int
  
AS

DECLARE @isSuperadmin int 

SELECT @isSuperadmin = count(*) FROM user_roles_crossref 
WHERE role_id =0 AND user_id = @userId

SELECT @searchString = replace(@searchString, '*', '%' ) 

   IF @isSuperadmin = 1 BEGIN
	IF @showAll = 0 BEGIN
		SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
		FROM users
		WHERE ( first_name like @searchString+'%' OR last_name like @searchString+'%' OR login_name like @searchString+'%' ) 
		       AND ( active=1 or active=@active )
	               AND NOT ( login_name like 'user' )
		ORDER BY last_name
	END
	ELSE BEGIN  
	  	SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
		FROM users
		WHERE  ( active=1 or active=@active ) 
	               AND NOT ( login_name like 'user' )
		ORDER BY last_name
	END	
     END
     
     ELSE BEGIN
	IF @showAll = 0 BEGIN
		SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
		FROM users
		WHERE ( first_name like @searchString+'%' OR last_name like @searchString+'%' OR login_name like @searchString+'%' ) 
		      AND ( active=1 or active=@active )
			  AND user_id in (
				select user_id from user_roles_crossref 
				WHERE role_id IN (
						select role_id from useradmin_role_crossref 
						WHERE user_id = @userId) )
		      AND NOT user_id IN (   
	 			SELECT user_roles_crossref.user_id FROM user_roles_crossref INNER JOIN
	                      	roles ON user_roles_crossref.role_id = roles.role_id 
				WHERE     (roles.admin_role > 0) )  
			  AND NOT (	login_name like 'user' )
	
		ORDER BY last_name
        END
	ELSE BEGIN 
		SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
		FROM users
		WHERE 	( active=1 or active=@active )
			AND user_id in (
				select user_id from user_roles_crossref 
				WHERE role_id IN (
						select role_id from useradmin_role_crossref 
						WHERE user_id = @userId) )
		      AND NOT user_id IN (   
	 			SELECT user_roles_crossref.user_id FROM user_roles_crossref INNER JOIN
	                      	roles ON user_roles_crossref.role_id = roles.role_id 
				WHERE     (roles.admin_role > 0) )  
			  AND NOT (	login_name like 'user' )
	
		ORDER BY last_name
	END

      END	

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

