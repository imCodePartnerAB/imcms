package com.imcode.imcms;

public interface UserMapper {
   User[] getAllUsers() throws NoPermissionException;
}
