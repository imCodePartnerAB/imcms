package com.imcode.imcms;

public interface UserMapper {
   com.imcode.imcms.User[] getAllUsers() throws NoPermissionException ;
   com.imcode.imcms.User getUser( String userLoginName ) throws NoPermissionException ;
   String[] getAllRolesNames() throws NoPermissionException ;
   String[] getRoleNames( User user );

   void setUserRoles( User user, String[] roleNames );
}
