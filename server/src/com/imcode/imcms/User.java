package com.imcode.imcms;

public interface User {
   String getLoginName();
   String getPassword() throws NoPermissionException;
   String getCompany();
}
