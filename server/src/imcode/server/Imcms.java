package imcode.server;

import com.imcode.db.*;
import com.imcode.db.Database;
import com.imcode.imcms.db.*;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.util.Prefs;
import imcode.util.CachingFileLoader;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

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
        Properties serverprops = getServerProperties();
        LOG.debug("Creating main DataSource.");
        Database database = createDatabase(serverprops);
        LocalizedMessageProvider localizedMessageProvider = new CachingLocalizedMessageProvider(new ImcmsPrefsLocalizedMessageProvider());
        InputStreamReader initScriptReader = new InputStreamReader(new FileInputStream(new File(getPath(), "WEB-INF/sql/init.sql")), "UTF-8");
        org.apache.ddlutils.model.Database wantedDdl = DatabaseUtils.getWantedDdl();
        DatabaseUpgrade upgrade = new StartupDatabaseUpgrade(wantedDdl, new ImcmsDatabaseCreator(initScriptReader, localizedMessageProvider));
        upgrade.upgrade(database);
        sanityCheckDatabase(database, wantedDdl);

        final CachingFileLoader fileLoader = new CachingFileLoader();
        DefaultImcmsServices defaultImcmsServices = new DefaultImcmsServices(database, serverprops, localizedMessageProvider, fileLoader, new DefaultProcedureExecutor(database, fileLoader));

        defaultImcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper().encryptUnencryptedUsersLoginPasswords();

        return defaultImcmsServices;
    }

    private static void sanityCheckDatabase(Database database, org.apache.ddlutils.model.Database wantedDdl) {
        DatabaseSanityCheck databaseSanityCheck = new DatabaseSanityCheck(database, wantedDdl);
        Collection<SanityCheck.Problem> problems = databaseSanityCheck.execute();
        for ( SanityCheck.Problem problem : problems ) {
            if ( SanityCheck.Problem.Severity.ERROR == problem.getSeverity() ) {
                LOG.error(problem.getDescription());
            } else if (SanityCheck.Problem.Severity.WARNING == problem.getSeverity()) {
                LOG.warn(problem.getDescription());
            } else if (SanityCheck.Problem.Severity.UNKNOWN == problem.getSeverity()) {
                LOG.debug(problem.getDescription());
            }
        }
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
        } catch ( SQLException ex ) {
            String message = "Could not connect to database "+ jdbcUrl + " with driver " + jdbcDriver + ": "+ex.getMessage()+" Error code: "
                             + ex.getErrorCode() + " SQL State: " + ex.getSQLState();
            LOG.fatal(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    public static String getDefaultLanguage() {
        Properties props = getServerProperties();
        String language = props.getProperty("DefaultLanguage");

        return (!StringUtils.isBlank(language)) ? language : "eng";
    }

    public static class StartupException extends RuntimeException {

        public StartupException(String message, Exception e) {
            super(message, e) ;
        }
    }

}
