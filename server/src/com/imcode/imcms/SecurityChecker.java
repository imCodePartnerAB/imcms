package com.imcode.imcms;

class SecurityChecker {
   static SecurityChecker getInstance( Class aClass ) {
      return new SecurityChecker();
   }

   void checkPermisions() throws NoPermissionException {
   }
}
