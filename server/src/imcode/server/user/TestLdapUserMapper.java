package imcode.server.user;


public class TestLdapUserMapper extends Log4JInitTestCase {
   private LdapUserMapper mapper;

   public void setUp() {
      try {
         mapper = new LdapUserMapper();
      } catch( LdapUserMapper.LdapInitException e ) {
         fail();
      }
   }

   public void testInvalidName() {
      imcode.server.user.User user = mapper.getUser("");
      assertNull( user );
   }

   public void testNonExistingUser() {
      imcode.server.user.User user = mapper.getUser("kalle banan som inte finns");
      assertNull( user );
   }

   public void testExistingUser() {
      imcode.server.user.User user = mapper.getUser("chrham");
      assertNotNull( user );
      assertEquals( "Christoffer", user.getFirstName());
   }
}
