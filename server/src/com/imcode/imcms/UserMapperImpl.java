package com.imcode.imcms;

import imcode.server.user.ImcmsAuthenticatorAndUserMapper;

public class UserMapperImpl implements UserMapper {
   private ImcmsAuthenticatorAndUserMapper mapper;

   public UserMapperImpl( ImcmsAuthenticatorAndUserMapper mapper ) {
      this.mapper = mapper;
   }

   public User[] getAllUsers() throws NoPermissionException {
      SecurityChecker.getInstance( this.getClass() ).checkPermisions();

      imcode.server.user.User[] internalUsers = mapper.getAllUsers();
      User[] result = new User[internalUsers.length];
      for( int i = 0; i < result.length; i++ ) {
         imcode.server.user.User internalUser = internalUsers[i];
         result[i] = new UserImpl( internalUser );
      }
      return result;
   }

   public User getUser( String userLoginName ) throws NoPermissionException {
      imcode.server.user.User internalUser =  mapper.getUser( userLoginName );
      UserImpl result = new UserImpl( internalUser );
      return result;
   }

   public String[] getAllRolesNames() throws NoPermissionException {
      return mapper.getAllRoleNames();
   }

   public String[] getRoleNames( User user ) {
      UserImpl userImpl = (UserImpl)user;
      return mapper.getRoleNames( userImpl.getInternalUser() );
   }

   public void setUserRoles( User user, String[] roleNames ) {
      UserImpl userImpl = (UserImpl)user;
      mapper.setUserRoles( userImpl.getInternalUser() , roleNames );
   }
}
