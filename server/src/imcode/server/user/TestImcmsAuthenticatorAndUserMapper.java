package imcode.server.user;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import imcode.server.*;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.Document;
import imcode.readrunner.ReadrunnerUserData;
import imcode.util.poll.PollHandlingSystem;
import imcode.util.shop.ShoppingOrderSystem;

import java.io.IOException;
import java.io.File;
import java.util.*;

public class TestImcmsAuthenticatorAndUserMapper extends Log4JInitTestCase {
   private ImcmsAuthenticatorAndUserMapper imcmsAAUM;
   private IMCServiceInterface service;

   protected void setUp() throws Exception {
      Logger logger = Logger.getLogger( this.getClass()  );
      service = new MockIMCServiceInterface();
      imcmsAAUM = new ImcmsAuthenticatorAndUserMapper(service, logger);
   }

   public void testFalseUser() {
      boolean exists = imcmsAAUM.authenticate("aösdlhf","asdöflkjaödfs");
      assertTrue( !exists );
   }
   public void testAdmin() {
      boolean exists = imcmsAAUM.authenticate("admin","admin");
      assertTrue( exists );
   }
   public void testUserUser() {
      String loginName = "user";
      boolean exists = imcmsAAUM.authenticate(loginName,"user");
      assertTrue( exists );
      User user = imcmsAAUM.getUser(loginName) ;
      assertTrue( user.getFirstName().equalsIgnoreCase(loginName));
   }
}

