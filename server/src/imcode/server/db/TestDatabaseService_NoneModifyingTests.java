package imcode.server.db;

import junit.framework.TestCase;

public class TestDatabaseService_NoneModifyingTests extends TestCase {

    DatabaseService sqlServer;
    DatabaseService mimer;
    private static final String DB_HOST = "localhost";

    private static final int SQL_SERVER_PORT = 1433;
    private static final String SQLSERVER_DATABASE_NAME = "test";

    private static final int MIMER_PORT = 1360;
    private static final String MIMMER_DATABASE_NAME = "test";

    public static void main( String[] args ) throws Exception {
/*
        Timestamp t1 = new Timestamp( new java.util.Date().getTime() );
        Thread.sleep( 2000 );
        Timestamp t2 = new Timestamp( new java.util.Date().getTime() );
        System.out.println( "t1 = " + t1.toString() );
        System.out.println( "t2 = " + t2.toString() );
        System.out.println( t1.equals(t2) );
*/

/*
        DatabaseService mimer = new DatabaseService( DatabaseService.MIMER, DB_HOST, MIMER_PORT, MIMMER_DATABASE_NAME );
        mimer.initializeDatabase();
*/
        DatabaseService sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, DB_HOST, SQL_SERVER_PORT, SQLSERVER_DATABASE_NAME );
        sqlServer.initializeDatabase();
    }

    protected void setUp() throws Exception {
        if( sqlServer == null ) {
            sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, DB_HOST, SQL_SERVER_PORT, SQLSERVER_DATABASE_NAME );
        }
        if( mimer == null ) {
            mimer = new DatabaseService( DatabaseService.MIMER, DB_HOST, MIMER_PORT, MIMMER_DATABASE_NAME );
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
        assertEquals( sqlServer.sproc_getTamplatesInGroup(0), mimer.sproc_getTamplatesInGroup(0) );
        assertEquals( sqlServer.sproc_getTamplatesInGroup(1), mimer.sproc_getTamplatesInGroup(1) );
        assertEquals( sqlServer.sproc_getTamplatesInGroup(2), mimer.sproc_getTamplatesInGroup(2) );
    }
}
