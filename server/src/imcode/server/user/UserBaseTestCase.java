package imcode.server.user;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;

import java.io.File;
import java.io.IOException;

abstract class UserBaseTestCase extends TestCase {
   protected static final String LOGIN_NAME_HASBRA = "hasbra";
   protected static final String LOGIN_NAME_ADMIN = "admin";

   protected static final String[] SQL_RESULT_ADMIN = {"1", "admin", "admin", "Admin", "Super", "", "", "", "", "", "", "", "", "1", "se", "1", "1", "2003-05-12 00:00:00", "0"};
   protected final static String[] SQL_RESULT_USER = {"2", "user", "user", "User", "Extern", "", "", "", "", "", "", "", "", "1", "se", "1", "1", "2003-05-12 00:00:00", "0"};
   protected static final String[] SQL_RESULT_HASBRA = {"3", "hasbra", "hasbra", "Hasse", "Brattberg", "", "", "", "", "", "", "", "", "1", "se", "1", "1", "2003-05-12 00:00:00", "1"};

   protected static final String SPROC_GETUSERBYLOGIN = "GetUserByLogin";
   protected static final String SPROC_GETALLROLES = "GetAllRoles";
   protected static final String SPROC_GETUSERROLES = "GetUserRoles";
   protected static final String SPROC_ROLEADDNEW = "RoleAddNew";
   protected static final String SPROC_ROLEFINDNAME = "RoleFindName";
   protected static final String SPROC_ADDUSERROLE = "AddUserRole";


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
