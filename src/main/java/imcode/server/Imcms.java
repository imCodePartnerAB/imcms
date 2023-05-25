package imcode.server;

import com.imcode.imcms.model.Language;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Set;

public class Imcms {

    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

    public static final String ERROR_LOGGER_URL = "https://errors.imcode.com/ErrorLogger";

    private static final Logger logger = LogManager.getLogger(Imcms.class);
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
     * Current language for user
     */
    private static InheritableThreadLocal<Language> languages = new InheritableThreadLocal<>();

    private static volatile FallbackDecoder defaultFallbackDecoder;

    /**
     * Flag variable that shows is document versioning feature are turned on in server properties
     */
    private static boolean isVersioningAllowed;

    private static volatile boolean startInvoked;

    public Imcms(ServletContext servletContext,
                 ImcmsServices imcmsServices,
                 Properties imcmsProperties) {

        Imcms.services = imcmsServices;
        Imcms.properties = imcmsProperties;

        final String versioningProperty = imcmsProperties.getProperty(DOCUMENT_VERSIONING_PROPERTY, "true");
        Imcms.isVersioningAllowed = Boolean.parseBoolean(versioningProperty);

        setRootPath(servletContext.getRealPath("/"));
        setDefaultFallbackDecoder(imcmsServices);
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
    private static synchronized void start() throws StartupException {
        try {
            users = new InheritableThreadLocal<>();
            services.getDocumentMapper().getDocumentIndex().getService().rebuildIfEmpty();
            startInvoked = false;

        } catch (Exception e) {
            String msg = "Application could not be started. Please see the log file in WEB-INF/logs/ for details.";
            logger.error(msg, e);
            throw new StartupException(msg, e);
        }
    }

    public static File getPath() {
        return path;
    }

    /**
     * Two-phase start invoking, one from Servlet Filter, another by Spring using @PostConstruct
     */
    public static void invokeStart() {
        if (startInvoked) {
            start();

        } else {
            startInvoked = true;
        }
    }

    public static Properties getServerProperties() {
        return properties;
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

    /**
     * @return a language associated with a current user
     */
    public static Language getLanguage() {
        return languages.get();
    }

    /**
     * Associates a language with a current user
     */
    public static void setLanguage(Language language) {
        languages.set(language);
    }

    /**
     * Returns is document versioning feature are turned on in server properties or not
     */
    public static boolean isVersioningAllowed() {
        return isVersioningAllowed;
    }

    public static DataSource getApiDataSource() {
        return services.getDatabaseService().getDataSource();
    }

    public static FallbackDecoder getDefaultFallbackDecoder(){
        return defaultFallbackDecoder;
    }

    @PostConstruct
    private void init() {
        invokeStart();
	    new Thread(() -> {
		    final Set<String> templateNames = services.getTemplateService().checkTemplates();
		    services.getTemplateCSSService().sync(templateNames);
	    }).start();
        new Thread(services.getImageService()::regenerateImages).start();
    }

    public synchronized void restartCms() {
        stop();
        start();
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
        Imcms.path = new File(path);
    }

    private void setDefaultFallbackDecoder(ImcmsServices services){
        final String workaroundUriEncoding = services.getConfig().getWorkaroundUriEncoding();
        defaultFallbackDecoder = new FallbackDecoder(
                Charset.forName(DEFAULT_ENCODING),
                (null != workaroundUriEncoding) ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset()
        );
    }

    public static class StartupException extends RuntimeException {
        private static final long serialVersionUID = -8220639564136797058L;

        StartupException(String message, Exception e) {
            super(message, e);
        }
    }
}
