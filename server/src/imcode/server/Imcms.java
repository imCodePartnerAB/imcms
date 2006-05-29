package imcode.server;

import com.imcode.db.*;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.db.handlers.SingleObjectHandler;
import com.imcode.imcms.db.DatabaseUpgrade;
import com.imcode.imcms.db.DatabaseVersion;
import com.imcode.imcms.db.StartupDatabaseUpgrade;
import com.imcode.imcms.db.UpgradeException;
import com.imcode.imcms.db.DatabaseUtils;
import imcode.util.Prefs;
import imcode.util.Utility;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Imcms {

    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String DEFAULT_ENCODING = "UTF-8";

    private final static Logger LOG = Logger.getLogger(Imcms.class.getName());
    private static ImcmsServices services;
    private static BasicDataSource apiDataSource;
    private static BasicDataSource dataSource;
    private static File path;

    private Imcms() {
    }

    public synchronized static ImcmsServices getServices() {
        if ( null == services ) {
            start();
        }
        return services;
    }

    public static void setPath(File path) {
        Imcms.path = path;
    }

    public static File getPath() {
        return path;
    }

    public static void start() throws StartupException {
        try {
            services = createServices();
        } catch (Exception e) {
            throw new Imcms.StartupException("imCMS could not be started. Please see the LOG file in WEB-INF/logs for details.", e);
        }
    }

    private synchronized static ImcmsServices createServices() throws Exception {
        Properties serverprops = getServerProperties();
        LOG.debug("Creating main DataSource.");
        Database database = createDatabase(serverprops);
        DatabaseUpgrade upgrade = new StartupDatabaseUpgrade(DatabaseUtils.getDdl());
        upgrade.upgrade(database);
        return new DefaultImcmsServices(database, serverprops);
    }

    private static Database createDatabase(Properties serverprops) {
        dataSource = createDataSource(serverprops);
        return new DataSourceDatabase(dataSource);
    }

    public synchronized static DataSource getApiDataSource() {
        if ( null == apiDataSource ) {
            Properties serverprops = getServerProperties();
            LOG.debug("Creating API DataSource.");
            apiDataSource = createDataSource(serverprops);
        }
        return apiDataSource;
    }

    private static Properties getServerProperties() {
        try {
            return Prefs.getProperties(SERVER_PROPERTIES_FILENAME);
        } catch ( IOException e ) {
            LOG.fatal("Failed to initialize imCMS", e);
            throw new UnhandledException(e);
        }
    }

    private static BasicDataSource createDataSource(Properties props) {

        String jdbcDriver = props.getProperty("JdbcDriver");
        String jdbcUrl = props.getProperty("JdbcUrl");
        String user = props.getProperty("User");
        String password = props.getProperty("Password");
        int maxConnectionCount = Integer.parseInt(props.getProperty("MaxConnectionCount"));

        LOG.debug("JdbcDriver = " + jdbcDriver);
        LOG.debug("JdbcUrl = " + jdbcUrl);
        LOG.debug("User = " + user);
        LOG.debug("MaxConnectionCount = " + maxConnectionCount);

        return createDataSource(jdbcDriver, jdbcUrl, user, password, maxConnectionCount);
    }

    public synchronized static void restart() {
        stop();
        start();
    }

    public static void stop() {
        if ( null != apiDataSource ) {
            try {
                LOG.debug("Closing API DataSource.");
                apiDataSource.close();
            } catch ( SQLException e ) {
                LOG.error(e, e);
            }
        }
        if ( null != dataSource ) {
            try {
                LOG.debug("Closing main DataSource.");
                dataSource.close();
            } catch ( SQLException e ) {
                LOG.error(e, e);
            }
        }
        Prefs.flush();
    }

    private static void logDatabaseVersion(BasicDataSource basicDataSource) throws SQLException {
        Connection connection = basicDataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        LOG.info("Database product version = " + metaData.getDatabaseProductVersion());
        connection.close();
    }

    public static BasicDataSource createDataSource(String jdbcDriver, String jdbcUrl,
                                                   String user, String password,
                                                   int maxConnectionCount) {
        try {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(jdbcDriver);
            basicDataSource.setUsername(user);
            basicDataSource.setPassword(password);
            basicDataSource.setUrl(jdbcUrl);

            basicDataSource.setMaxActive(maxConnectionCount);
            basicDataSource.setMaxIdle(maxConnectionCount);
            basicDataSource.setDefaultAutoCommit(true);
            basicDataSource.setPoolPreparedStatements(true);

            logDatabaseVersion(basicDataSource);

            return basicDataSource;
        } catch ( Exception ex ) {
            LOG.fatal("Failed to create connection pool. Url: " + jdbcUrl + " Driver: " + jdbcDriver, ex);
            throw new RuntimeException(ex);
        }
    }

    public static class StartupException extends RuntimeException {

        public StartupException(String message, Exception e) {
            super(message, e) ;
        }
    }

}
