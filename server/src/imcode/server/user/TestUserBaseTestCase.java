package imcode.server.user;

import imcode.server.test.Log4JConfiguredTestCase;

abstract class TestUserBaseTestCase extends Log4JConfiguredTestCase {
   static final String LOGIN_NAME_HASBRA = "hasbra";
   static final String LOGIN_NAME_ADMIN = "admin";

   static final String[] SQL_RESULT_ADMIN = {"1", "admin", "admin", "Admin", "Super", "", "", "", "", "", "", "", "", "1", "se", "1", "1", "2003-05-12 00:00:00", "0"};
   final static String[] SQL_RESULT_USER = {"2", "user", "user", "User", "Extern", "", "", "", "", "", "", "", "", "1", "se", "1", "1", "2003-05-12 00:00:00", "0"};
   static final String[] SQL_RESULT_HASBRA = {"3", "hasbra", "hasbra", "Hasse", "Brattberg", "", "", "", "", "", "", "", "", "1", "se", "1", "1", "2003-05-12 00:00:00", "1"};

   static final String SPROC_GETUSERBYLOGIN = "GetUserByLogin";
   static final String SPROC_GETALLROLES = "GetAllRoles";
   static final String SPROC_GETUSERROLES = "GetUserRoles";
   static final String SPROC_ROLEADDNEW = "RoleAddNew";
   static final String SPROC_ROLEFINDNAME = "RoleFindName";
}
