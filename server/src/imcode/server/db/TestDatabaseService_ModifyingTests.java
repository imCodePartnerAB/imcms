package imcode.server.db;

/**
 * These tests is slow because we set up the database before every test
 */
public class TestDatabaseService_ModifyingTests extends TestDatabaseService {
    protected void setUp() throws Exception {
        super.initDatabases();
    }
}
