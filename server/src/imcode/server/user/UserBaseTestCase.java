package imcode.server.user;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;

import java.io.File;
import java.io.IOException;

abstract class UserBaseTestCase extends TestCase {
   static final String LOGIN_NAME_HASBRA = "hasbra";
   static final String LOGIN_NAME_ADMIN = "admin";

   static final String DEFAULT_LANGUAGE = "en";

   static final String[] SQL_RESULT_ADMIN = {"1", "admin", "admin", "Admin", "Super", "", "", "", "", "", "", "", "", "1", DEFAULT_LANGUAGE, "1", "1", "2003-05-12 00:00:00", "0"};
   final static String[] SQL_RESULT_USER = {"2", "user", "user", "User", "Extern", "", "", "", "", "", "", "", "", "1", DEFAULT_LANGUAGE, "1", "1", "2003-05-12 00:00:00", "0"};
   static final String[] SQL_RESULT_HASBRA = {"3", "hasbra", "hasbra", "Hasse", "Brattberg", "", "", "", "", "", "", "", "", "1", DEFAULT_LANGUAGE, "1", "1", "2003-05-12 00:00:00", "1"};

   static final String SPROC_GETUSERBYLOGIN = "GetUserByLogin";
   static final String SPROC_GETALLROLES = "GetAllRoles";
   static final String SPROC_GETUSERROLES = "GetUserRoles";
   static final String SPROC_ROLEADDNEW = "RoleAddNew";
   static final String SPROC_ROLEFINDNAME = "RoleFindName";


   public UserBaseTestCase() {
      super( "UserBaseTestCase" );
      try {
         initLog4J();
      } catch( IOException e ) {
         e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
   }

   private void initLog4J() throws IOException {
      String tmpDir = System.getProperty( "java.io.tmpdir" );
      File tmpFile = new File( tmpDir, "log4joutput.log" );
      BasicConfigurator.configure( new FileAppender( new SimpleLayout(), tmpFile.toString() ) );
   }
}
