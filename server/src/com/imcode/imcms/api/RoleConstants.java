package com.imcode.imcms.api;

/**
 * @deprecated Use {@link UserService#getRole(int)} and {@link Role#getName()} instead. Will be removed in imCMS 3.0 or later.
 */
public interface RoleConstants {
    final static String USERS = "Users";
    final static String USER_ADMIN = "Useradmin";
    final static String SUPER_ADMIN = "Superadmin";
}
