package imcode.server.db;

public class TestDatabaseService_NoneModifyingTests extends TestDatabaseService {

    DatabaseService sqlServer;
    DatabaseService mimer;

    public static void main( String[] args ) throws Exception {
        initDatabases();
    }

    protected void setUp() throws Exception {
        if( sqlServer == null ) {
            sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, DB_HOST, SQL_SERVER_PORT, SQLSERVER_DATABASE_NAME, SQLSERVE_DATABASE_USER, SQLSERVE_DATABASE_PASSWORD );
        }
        if( mimer == null ) {
            mimer = new DatabaseService( DatabaseService.MIMER, DB_HOST, MIMER_PORT, MIMMER_DATABASE_NAME, MIMMER_DATABASE_USER, MIMMER_DATABASE_PASSWORD );
        }
    }

    private static void static_assertEquals( Object[] oneArr, Object[] anotherArr ) {
        if( oneArr == null ) {
            assertNotNull( anotherArr );
        } else if( anotherArr == null ) {
            fail( "The second array is null, but not the first oneArr" );
        } else {
            assertTrue( oneArr != anotherArr );
            assertEquals( oneArr.length, anotherArr.length );
            for( int i = 0; i < oneArr.length; i++ ) {
                Object one = oneArr[i];
                Object another = anotherArr[i];
                assertTrue( one != another );
                assertEquals( one, another );
                System.out.println( "one: " + one.toString() );
                System.out.println( "another: " + another.toString() );
            }
        }
    }

    public void testSameResultFromSprocGetAllRoles() {
        static_assertEquals( sqlServer.sproc_getallroles(), mimer.sproc_getallroles() );
    }

    public void testSameResultFromSprocGetAllUsers() {
        static_assertEquals( sqlServer.sproc_getallusers(), mimer.sproc_getallusers() );
    }

    public void testSameResultFromSprocGetTamplatesInGroup() {
        assertEquals( sqlServer.sproc_getTemplatesInGroup(0), mimer.sproc_getTemplatesInGroup(0) );
        assertEquals( sqlServer.sproc_getTemplatesInGroup(1), mimer.sproc_getTemplatesInGroup(1) );
        assertEquals( sqlServer.sproc_getTemplatesInGroup(2), mimer.sproc_getTemplatesInGroup(2) );
    }
}
