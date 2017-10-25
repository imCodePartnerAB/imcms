package imcode.server;

import imcode.server.user.UserDomainObject;
import imcode.util.PropertyManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

@Component
public class Imcms {

    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

    public static final String ERROR_LOGGER_URL = "https://errors.imcode.com/ErrorLogger";

    /**
     * Default Embedded SOLr home directory relative to deployment path
     */
    private static final String DEFAULT_SQLR_HOME = "WEB-INF/solr";

    private static final Logger logger = Logger.getLogger(Imcms.class);
    private static final String DOCUMENT_VERSIONING_PROPERTY = "document.versioning";
    private static Properties properties;
    /**
     * imCMS deployment (real context) path.
     */
    private static volatile File path;
    /**
     * Core services.
     */
    private static volatile ImcmsServices services;
    /**
     * Users associated with servlet requests.
     *
     * @see com.imcode.imcms.servlet.ImcmsSetupFilter
     */
    private static InheritableThreadLocal<UserDomainObject> users = new InheritableThreadLocal<>();

    /**
     * Flag variable that shows is document versioning feature are turned on in server properties
     */
    private static boolean isVersioningAllowed;

    private static DataSource dataSource;

    private static boolean startInvoked;

    @Autowired
    public Imcms(ServletContext servletContext,
                 @Qualifier("dataSourceWithAutoCommit") DataSource dataSource,
                 ImcmsServices imcmsServices,
                 Properties imcmsProperties) {

        Imcms.dataSource = dataSource;
        Imcms.services = imcmsServices;
        Imcms.properties = imcmsProperties;

        final String versioningProperty = imcmsProperties.getProperty(DOCUMENT_VERSIONING_PROPERTY, "true");
        Imcms.isVersioningAllowed = Boolean.parseBoolean(versioningProperty);

        setRootPath(servletContext.getRealPath("/"));
    }

    /**
     * @return ImcmsServices
     */
    public static ImcmsServices getServices() {
        return services;
    }

    /**
     * Initializes services.
     * <p>
     * Path and ApplicationContext must be set.
     */
    public static synchronized void start() throws StartupException {
        try {
            if (path == null) {
                throw new IllegalStateException("Imcms path is not set.");
            }

            users = new InheritableThreadLocal<>();
            services.init();

            if (services.getDocumentMapper().getDocumentIndex().getService().rebuildIfEmpty().isDefined()) {
                logger.info("Document index is empty, initiated index rebuild.");
            }

            startInvoked = false;

        } catch (Exception e) {
            String msg = "Application could not be started. Please see the log file in WEB-INF/logs/ for details.";
            logger.error(msg, e);
            throw new StartupException(msg, e);
        }
    }

    public static DataSource getApiDataSource() {
        return dataSource;
    }

    public static File getPath() {
        return path;
    }

    public static void invokeStart() {
        if (startInvoked) {
            start();

        } else {
            startInvoked = true;
        }
    }

    @PostConstruct
    private void init() {
        invokeStart();
    }

    public static Properties getServerProperties() {
        return properties;
    }

    private void setPath(File path) {
        Imcms.path = path;
    }

    public synchronized void restartCms() {
        stop();
        start();
    }

    /**
     * Removes a user from a current request thread.
     * Must not be called from a client code.
     */
    public static void removeUser() {
        users.remove();
    }

    /**
     * @return a user associated with a current request thread.
     */
    public static UserDomainObject getUser() {
        return users.get();
    }

    /**
     * Associates a user with a current request thread.
     * Must not be called from a client code.
     */
    public static void setUser(UserDomainObject user) {
        users.set(user);
    }

    private static String getSolrHome() {
        if (path == null) throw new IllegalStateException("Application path is not set.");

        return new File(path.getAbsolutePath(), DEFAULT_SQLR_HOME).getAbsolutePath();
    }

    /**
     * Returns is document versioning feature are turned on in server properties or not
     */
    public static boolean isVersioningAllowed() {
        return isVersioningAllowed;
    }

    @PreDestroy
    public synchronized void stop() {
        PropertyManager.flush();

        if (services != null) {
            services.getDocumentMapper().getDocumentIndex().getService().shutdown();
        }

        services = null;
    }

    private void setRootPath(String path) {
        PropertyManager.setRoot(path);
        setPath(new File(path));
    }

    public static class StartupException extends RuntimeException {
        private static final long serialVersionUID = -8220639564136797058L;

        StartupException(String message, Exception e) {
            super(message, e);
        }
    }
}
