package imcode.server;

import imcode.util.CachingFileLoader;
import imcode.util.Prefs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.Database;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;

public class Imcms {
	
    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

    private final static Logger LOG = Logger.getLogger(Imcms.class.getName());
    private static ImcmsServices services;
    private static BasicDataSource apiDataSource;
    private static BasicDataSource dataSource;
    private static File path;
    
    // Worakround
    public static WebApplicationContext webApplicationContext;
    
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

    public synchronized static void start() throws StartupException {
        try {
            services = createServices();
        } catch (Exception e) {
            throw new StartupException("imCMS could not be started. Please see the log file in WEB-INF/logs/ for details.", e);
        }
    }

    private synchronized static ImcmsServices createServices() throws Exception {
    	if (webApplicationContext == null) {
    		throw new NullPointerException("Spring WebApplicationContext is not set.");
    	}
    	
        Properties serverprops = getServerProperties();
        LOG.debug("Creating main DataSource.");
        Database database = createDatabase(serverprops);
        LocalizedMessageProvider localizedMessageProvider = new CachingLocalizedMessageProvider(new ImcmsPrefsLocalizedMessageProvider());
                
        final CachingFileLoader fileLoader = new CachingFileLoader();
        return new DefaultImcmsServices(webApplicationContext, database, serverprops, localizedMessageProvider, fileLoader, new DefaultProcedureExecutor(database, fileLoader));
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

    public static Properties getServerProperties() {
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
            basicDataSource.setTestOnBorrow(true);
            basicDataSource.setValidationQuery("select 1");

            logDatabaseVersion(basicDataSource);

            return basicDataSource;
        } catch ( SQLException ex ) {
            String message = "Could not connect to database "+ jdbcUrl + " with driver " + jdbcDriver + ": "+ex.getMessage()+" Error code: "
                             + ex.getErrorCode() + " SQL GroupData: " + ex.getSQLState();
            LOG.fatal(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    public static class StartupException extends RuntimeException {

        public StartupException(String message, Exception e) {
            super(message, e) ;
        }
    }
}
