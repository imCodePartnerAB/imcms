package com.imcode.imcms;

class UserImpl implements User {
   private imcode.server.user.User internalUser;

   public UserImpl( imcode.server.user.User internalUser ) {
      this.internalUser = internalUser;
   }

   public String getLoginName() {
      return internalUser.getLoginName();
   }

   public String getPassword() throws NoPermissionException {
      SecurityChecker.getInstance( this.getClass() ).checkPermisions();

      String result = internalUser.getPassword();
      return result;
   }
}
