package com.imcode.imcms;

import imcode.server.user.User;

import java.util.HashSet;
import java.util.Arrays;

public class SecurityChecker {

   private final static String SUPERADMIN_ROLE = "Superadmin";
   private final static String USER_ADMIN = "Useradmin";

   private User accessor;
   private HashSet accessorRoles;

   private boolean isSuperAdmin;
   private boolean isUserAdmin;

   public SecurityChecker( User accessor, String[] accessorRoles ) {
      this.accessor = accessor;
      this.accessorRoles = new HashSet( Arrays.asList(accessorRoles) );

      isSuperAdmin = this.accessorRoles.contains( SUPERADMIN_ROLE );
      isUserAdmin = this.accessorRoles.contains( USER_ADMIN );
   }

   public void loggedIn() throws NoPermissionException {
      if( null == accessor ){
         throw new NoPermissionException("User not loged in");
      }
   }

   public void isSuperAdmin()  throws NoPermissionException  {
      if( !isSuperAdmin ) {
         throw new NoPermissionException("User is not " +  SUPERADMIN_ROLE );
      }
   }

   public void isUserAdmin()  throws NoPermissionException  {
      if( !isUserAdmin ) {
         throw new NoPermissionException("User is not " +  USER_ADMIN );
      }
   }

   public void isSuperAdminOrIsUserAdminOrIsSameUser( UserBean userBean ) throws NoPermissionException  {
     boolean isSameUser = userBean.getLoginName().equalsIgnoreCase( accessor.getLoginName() );
      if( !isSuperAdmin && !isUserAdmin && !isSameUser ) {
         throw new NoPermissionException("User is not superadmin, useradmin nor the same user.");
      }
   }
}
