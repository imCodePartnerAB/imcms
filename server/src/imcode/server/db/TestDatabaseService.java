package imcode.server.db;
import imcode.server.test.Log4JConfiguredTestCase;

public abstract class TestDatabaseService extends Log4JConfiguredTestCase {
    protected static final String DB_HOST = "localhost";

    protected static final int SQLSERVER_PORT = 1433;
    protected static final String SQLSERVER_DATABASE_NAME = "test";
    protected static final String SQLSERVE_DATABASE_USER = "sa";
    protected static final String SQLSERVE_DATABASE_PASSWORD = "sa";

    protected static final int MIMER_PORT = 1360;
    protected static final String MIMMER_DATABASE_NAME = "test";
    protected static final String MIMMER_DATABASE_USER = "sysadm";
    protected static final String MIMMER_DATABASE_PASSWORD = "admin";

    protected static int MYSQL_PORT = 3306;
    protected static String MYSQL_DATABASE_NAME = "test";
    protected static String MYSQL_DATABASE_USER = "root";
    protected static String MYSQL_DATABASE_PASSWORD = "";

}
