package com.imcode.imcms;

import imcode.server.user.ImcmsAuthenticatorAndUserMapper;

public class UserMapperImpl implements UserMapper {
   private ImcmsAuthenticatorAndUserMapper mapper;

   UserMapperImpl( ImcmsAuthenticatorAndUserMapper mapper ) {
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
}
