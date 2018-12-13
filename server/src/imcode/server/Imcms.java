package imcode.server;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.Database;
import com.imcode.imcms.db.DatabaseUpgrade;
import com.imcode.imcms.db.DatabaseUtils;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.db.ImcmsDatabaseCreator;
import com.imcode.imcms.db.StartupDatabaseUpgrade;
import com.imcode.imcms.services.ServerSettings;
import com.imcode.imcms.services.ServerSettingsChecker;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.util.CachingFileLoader;
import imcode.util.Prefs;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Imcms {

    public static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;
    public final static String ERROR_LOGGER_URL = "https://errors.imcode.com/ErrorLogger";

    private final static Logger LOG = Logger.getLogger(Imcms.class.getName());
    private static ImcmsServices services;
    private static BasicDataSource apiDataSource;
    private static BasicDataSource dataSource;
    private static File path;
    private static File indexDirectory;

    private Imcms() {
    }

    public synchronized static ImcmsServices getServices() {
        if (null == services) {
            start();
        }
        return services;
    }

    public static File getPath() {
        return path;
    }

    public static void setPath(File path) {
        Imcms.path = path;
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
        InputStreamReader initScriptReader = new InputStreamReader(new FileInputStream(new File(getPath(), "WEB-INF/sql/init.sql")), StandardCharsets.UTF_8);
        org.apache.ddlutils.model.Database wantedDdl = DatabaseUtils.getWantedDdl();
        DatabaseUpgrade upgrade = new StartupDatabaseUpgrade(wantedDdl, new ImcmsDatabaseCreator(initScriptReader, localizedMessageProvider));
        upgrade.upgrade(database);
        sanityCheckDatabase(database, wantedDdl);

        final CachingFileLoader fileLoader = new CachingFileLoader();
        XMLConfig xmlConfig = new XMLConfig(new File(getPath(), "WEB-INF/conf/server.xml").getCanonicalPath());
        DefaultImcmsServices defaultImcmsServices = new DefaultImcmsServices(xmlConfig, database, serverprops, localizedMessageProvider, fileLoader, new DefaultProcedureExecutor(database, fileLoader));

        defaultImcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper().encryptUnencryptedUsersLoginPasswords();

        return defaultImcmsServices;
    }

    public static String getServerName() {
        return getServerProperties().getProperty("ServerName", "http://localhost:8080");
    }

    public static Map<String, AuthenticationMethodConfiguration> getAuthenticationConfiguration() {
        Map<String, AuthenticationMethodConfiguration> result = new HashMap<>();

        Properties prop = getServerProperties();
        String authenticationMethodName;
        int index = 1;
        while (!(authenticationMethodName =
                prop.getProperty(
                        AuthenticationMethodConfiguration.AUTHENTICATION_METHOD_NAMING_PATTERN
                                .replace(AuthenticationMethodConfiguration.AUTHENTICATION_REPLACEABLE, String.valueOf(index)),
                        AuthenticationMethodConfiguration.AUTHENTICATION_REPLACEABLE)).equals(AuthenticationMethodConfiguration.AUTHENTICATION_REPLACEABLE)) {
            result.put(
                    authenticationMethodName,
                    new AuthenticationMethodConfiguration()
                            .setName(authenticationMethodName)
                            .setOrder(index)
                            .setUrl(
                                    prop.getProperty(
                                            AuthenticationMethodConfiguration.AUTHENTICATION_URL_NAMING_PATTERN
                                                    .replace(AuthenticationMethodConfiguration.AUTHENTICATION_REPLACEABLE,
                                                            authenticationMethodName)
                                    )
                            )
            );
            index++;
        }


        return result;
    }

    private static void sanityCheckDatabase(Database database, org.apache.ddlutils.model.Database wantedDdl) {
        DatabaseSanityCheck databaseSanityCheck = new DatabaseSanityCheck(database, wantedDdl);
        Collection<SanityCheck.Problem> problems = databaseSanityCheck.execute();
        for (SanityCheck.Problem problem : problems) {
            if (SanityCheck.Problem.Severity.ERROR == problem.getSeverity()) {
                LOG.error(problem.getDescription());
            } else if (SanityCheck.Problem.Severity.WARNING == problem.getSeverity()) {
                LOG.warn(problem.getDescription());
            } else if (SanityCheck.Problem.Severity.UNKNOWN == problem.getSeverity()) {
                LOG.debug(problem.getDescription());
            }
        }
    }

    private static Database createDatabase(Properties serverProps) {
        dataSource = createDataSource(serverProps);
        dataSource.setJmxName("org.apache.dbcp:DataSource=dataSource,connectionPool=connections");
        return new DataSourceDatabase(dataSource);
    }

    public synchronized static DataSource getApiDataSource() {
        if (null == apiDataSource) {
            Properties serverprops = getServerProperties();
            LOG.debug("Creating API DataSource.");
            apiDataSource = createDataSource(serverprops);
            apiDataSource.setJmxName("org.apache.dbcp:DataSource=apiDataSource,connectionPool=apiConnections");
        }
        return apiDataSource;
    }

    public static Properties getServerProperties() {
        try {
            return Prefs.getProperties(SERVER_PROPERTIES_FILENAME);
        } catch (IOException e) {
            LOG.fatal("Failed to initialize imCMS - can't read server properties!", e);
            throw new UnhandledException(e);
        }
    }

    private static BasicDataSource createDataSource(Properties props) {

        String jdbcDriver = props.getProperty(ServerSettings.JDBC_DRIVER);
        String jdbcUrl = props.getProperty(ServerSettings.JDBC_URL);
        String user = props.getProperty(ServerSettings.DB_USER);
        String password = props.getProperty(ServerSettings.DB_PASSWORD);
        int maxConnectionCount = Integer.parseInt(props.getProperty(ServerSettings.DB_MAX_CONNECTIONS));

        return createDataSource(jdbcDriver, jdbcUrl, user, password, maxConnectionCount);
    }

    public synchronized static void restart() {
        stop();
        start();
    }

    public static void stop() {
        if (null != apiDataSource) {
            try {
                LOG.debug("Closing API DataSource.");
                apiDataSource.close();
            } catch (SQLException e) {
                LOG.error(e, e);
            }
        }
        if (null != dataSource) {
            try {
                LOG.debug("Closing main DataSource.");
                dataSource.close();
            } catch (SQLException e) {
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

            basicDataSource.setMaxTotal(maxConnectionCount);
            basicDataSource.setMaxIdle(maxConnectionCount);
            basicDataSource.setMaxConnLifetimeMillis(TimeUnit.SECONDS.toMillis(40));
            basicDataSource.setMaxWaitMillis(TimeUnit.SECONDS.toMillis(20));
            basicDataSource.setDefaultAutoCommit(true);
            basicDataSource.setPoolPreparedStatements(true);
            basicDataSource.setTestOnBorrow(true);
            basicDataSource.setLogAbandoned(true);
            basicDataSource.setLogExpiredConnections(true);
            basicDataSource.setRemoveAbandonedTimeout(60);
            basicDataSource.setValidationQuery("select 1");

            logDatabaseVersion(basicDataSource);

            return basicDataSource;
        } catch (SQLException ex) {
            String message;

            if (StringUtils.trimToNull(jdbcDriver) == null) {
                message = ServerSettingsChecker.getEmptyPropertyMessage(ServerSettings.JDBC_DRIVER);

            } else if (StringUtils.trimToNull(jdbcUrl) == null) {
                message = ServerSettingsChecker.getEmptyPropertyMessage(ServerSettings.JDBC_URL);

            } else if (StringUtils.trimToNull(user) == null) {
                message = ServerSettingsChecker.getEmptyPropertyMessage(ServerSettings.DB_USER);

            } else if (StringUtils.trimToNull(password) == null) {
                message = ServerSettingsChecker.getEmptyPropertyMessage(ServerSettings.DB_PASSWORD);

            } else {
                message = "Could not connect to database " + jdbcUrl + " with driver " + jdbcDriver + ": " + ex.getMessage() + " Error code: "
                        + ex.getErrorCode() + " SQL State: " + ex.getSQLState();
            }

            LOG.fatal(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    public static String getDefaultLanguage() {
        Properties props = getServerProperties();
        String language = props.getProperty("DefaultLanguage");

        return (!StringUtils.isBlank(language)) ? language : "eng";
    }

    public static File getIndexDirectory() {
        if (indexDirectory == null) {
            indexDirectory = new File(getPath(), "WEB-INF/index");
        }

        return indexDirectory;
    }

    public static class StartupException extends RuntimeException {

        public StartupException(String message, Exception e) {
            super(message, e);
        }
    }

}
