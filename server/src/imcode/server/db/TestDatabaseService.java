package imcode.server.db;
import imcode.server.test.Log4JConfiguredTestCase;

interface TestDatabaseService {
    static final String DB_HOST = "localhost";

    static final int SQLSERVER_PORT = 1433;
    static final String SQLSERVER_DATABASE_NAME = "test";
    static final String SQLSERVE_DATABASE_USER = "sa";
    static final String SQLSERVE_DATABASE_PASSWORD = "sa";

    static final int MIMER_PORT = 1360;
    static final String MIMMER_DATABASE_NAME = "test";
    static final String MIMMER_DATABASE_USER = "sysadm";
    static final String MIMMER_DATABASE_PASSWORD = "admin";

    static int MYSQL_PORT = 3306;
    static String MYSQL_DATABASE_NAME = "test";
    static String MYSQL_DATABASE_USER = "root";
    static String MYSQL_DATABASE_PASSWORD = "";
}
