package imcode.server.db;

import java.sql.Timestamp;

/**
 * These tests is slow because we set up the database before every test
 */
public class TestDatabaseService_ModifyingTests extends TestDatabaseService {
    protected void setUp() throws Exception {
        super.initAllDatabases();
    }

    public void test_sproc_AddNewuser() {
        DatabaseService.Table_users[] mimerUsersBefore = super.mimer.sproc_getallusers();
        DatabaseService.Table_users[] sqlServerUsersBefore = super.sqlServer.sproc_getallusers();

        int nextFreeUserId = 3;
        DatabaseService.Table_users user = new DatabaseService.Table_users(
            nextFreeUserId,
            "test login name",
            "test password",
            "First name",
            "Last name",
            "Titel",
            "Company",
            "Adress",
            "City",
            "Zip",
            "Country",
            "Country council",
            "Email adress",
            0,
            1001,
            0,
            1,
            1,
            1,
            new Timestamp( new java.util.Date().getTime() )
        );

        super.mimer.sproc_AddNewuser( user );
        super.sqlServer.sproc_AddNewuser( user );

        DatabaseService.Table_users[] mimerUsersAfter = super.mimer.sproc_getallusers();
        DatabaseService.Table_users[] sqlServerUsersAfter = super.sqlServer.sproc_getallusers();

        assertTrue( mimerUsersAfter.length == mimerUsersBefore.length + 1);
        assertTrue( sqlServerUsersAfter.length == sqlServerUsersBefore.length + 1);
    }
}
