package imcode.server;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Base application configuration.
 *
 * Fields names matches properties names in server.properties configuration file.
 */
public class Config {

    private File templatePath;
    private File includePath;
    private File filePath;
    private File imagePath;
    private File imageCachePath;
    private String imageCacheAllowedPaths;
    private long imageCacheMaxSize;
    /**
     * Path to ImageMagick 'bin' directory.<br/>
     * {@link imcode.server.DefaultImcmsServices#createConfigFromProperties DefaultImcmsServices.createConfigFromProperties} sets
     * {@link imcode.server.DefaultImcmsServices.WebappRelativeFileConverter WebappRelativeFileConverter} for File type, that's why this field is a String.
     */
    private String imageMagickPath;
    private String imageUrl;
    private String smtpServer;
    private int smtpPort;

    /**
     * Admin interface language. Not related to default content language (I18nLanguage).
     */
    private String defaultLanguage;

    private String sessionCookieDomain;
    private String fileAdminRootPaths;
    private long indexingSchedulePeriodInMinutes;
    private String documentPathPrefix;
    private int documentCacheMaxSize = 100;
    private int contentHistoryRecordsSize;
    private String keyStorePath;
    private String keyStoreType;
    private String workaroundUriEncoding;
    private boolean secureLoginRequired;

    /**  */
    private boolean denyMultipleUserLogin;

    /**
     * Remote SOLr URL.
     * The value is read from configuration file.
     */
    private String solrUrl;

    /**
     * Embedded SOLr home - absolute path.
     * The value is set manually at the service startup.
     */
    private String solrHome;

    private String loginPasswordEncryptionSalt;
    private boolean loginPasswordEncryptionEnabled;
    private boolean superadminLoginPasswordResetAllowed;

    private boolean ssoEnabled;
    private boolean ssoUseLocalJaasConfig;
    private String ssoJaasConfigName;
    private String ssoJaasPrincipalPassword;
    private boolean ssoUseLocalKrbConfig;
    private boolean ssoKerberosDebug;

    private String indexDisabledFileExtensions;
    private String indexDisabledFileMimes;
    private Set<String> indexDisabledFileExtensionsSet = Collections.emptySet();
    private Set<String> indexDisabledFileMimesSet = Collections.emptySet();

    private static List<String> splitCommaSeparatedString(String string) {
        StringTokenizer st = new StringTokenizer(StringUtils.trimToEmpty(string), " \t\n\r\f,");
        List<String> tokens = new LinkedList<>();

        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }

        return tokens;
    }

    private static Set<String> distinctLowerCased(List<String> strings) {
        Set<String> distinctStrings = new LinkedHashSet<>();

        for (String string : strings) {
            distinctStrings.add(string.toLowerCase());
        }

        return Collections.unmodifiableSet(distinctStrings);
    }

    public String getWorkaroundUriEncoding() {
        return workaroundUriEncoding;
    }

    public void setWorkaroundUriEncoding(String workaroundUriEncoding) {
        Charset charset = StringUtils.isNotBlank(workaroundUriEncoding) ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset();
        this.workaroundUriEncoding = charset.name();
    }

    public File getFilePath() {
        return filePath;
    }

    public void setFilePath(File filePath) {
        this.filePath = filePath;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public File getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(File templatePath) {
        this.templatePath = templatePath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        try {
            if (defaultLanguage.length() < 3) {
                defaultLanguage = LanguageMapper.convert639_1to639_2(defaultLanguage);
            }
        } catch (LanguageMapper.LanguageNotSupportedException e1) {
            defaultLanguage = null;
        }
        this.defaultLanguage = defaultLanguage;
    }

    public File getIncludePath() {
        return includePath;
    }

    public void setIncludePath(File includePath) {
        this.includePath = includePath;
    }

    public String getSessionCookieDomain() {
        return sessionCookieDomain;
    }

    public void setSessionCookieDomain(String sessionCookieDomain) {
        this.sessionCookieDomain = sessionCookieDomain;
    }

    public File getImagePath() {
        return imagePath;
    }

    public void setImagePath(File imagePath) {
        this.imagePath = imagePath;
    }

    public String getFileAdminRootPaths() {
        return fileAdminRootPaths;
    }

    public void setFileAdminRootPaths(String fileAdminRootPaths) {
        this.fileAdminRootPaths = fileAdminRootPaths;
    }

    public long getIndexingSchedulePeriodInMinutes() {
        return indexingSchedulePeriodInMinutes;
    }

    public void setIndexingSchedulePeriodInMinutes(long indexingSchedulePeriodInMinutes) {
        this.indexingSchedulePeriodInMinutes = indexingSchedulePeriodInMinutes;
    }

    public String getDocumentPathPrefix() {
        return documentPathPrefix;
    }

    public void setDocumentPathPrefix(String documentPathPrefix) {
        this.documentPathPrefix = documentPathPrefix;
    }

    public int getDocumentCacheMaxSize() {
        return documentCacheMaxSize;
    }

    public void setDocumentCacheMaxSize(int documentCacheMaxSize) {
        this.documentCacheMaxSize = documentCacheMaxSize;
    }

    public int getContentHistoryRecordsSize() {
        return contentHistoryRecordsSize;
    }

    public void setContentHistoryRecordsSize(int contentHistoryRecordsSize) {
        this.contentHistoryRecordsSize = contentHistoryRecordsSize;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        if (StringUtils.isBlank(keyStoreType)) {
            keyStoreType = KeyStore.getDefaultType();
        }
        this.keyStoreType = keyStoreType;
    }

    public boolean getSecureLoginRequired() {
        return secureLoginRequired;
    }

    public void setSecureLoginRequired(boolean secureLoginRequired) {
        this.secureLoginRequired = secureLoginRequired;
    }

    public boolean isDenyMultipleUserLogin() {
        return denyMultipleUserLogin;
    }

    public void setDenyMultipleUserLogin(boolean denyMultipleUserLogin) {
        this.denyMultipleUserLogin = denyMultipleUserLogin;
    }

    public String getImageCacheAllowedPaths() {
        return imageCacheAllowedPaths;
    }

    public void setImageCacheAllowedPaths(String imageCacheAllowedPaths) {
        this.imageCacheAllowedPaths = imageCacheAllowedPaths;
    }

    public long getImageCacheMaxSize() {
        return imageCacheMaxSize;
    }

    public void setImageCacheMaxSize(long imageCacheMaxSize) {
        this.imageCacheMaxSize = imageCacheMaxSize;
    }

    public File getImageCachePath() {
        return imageCachePath;
    }

    public void setImageCachePath(File imageCachePath) {
        this.imageCachePath = imageCachePath;
    }

    public String getImageMagickPath() {
        return imageMagickPath;
    }

    public void setImageMagickPath(String imageMagickPath) {
        this.imageMagickPath = imageMagickPath;
    }

    public String getSolrUrl() {
        return solrUrl;
    }

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = StringUtils.trimToNull(solrUrl);
    }

    public String getSolrHome() {
        return solrHome;
    }

    public void setSolrHome(String solrHome) {
        this.solrHome = solrHome;
    }

    /**
     * @since 4.0.7
     */
    public boolean isLoginPasswordEncryptionEnabled() {
        return loginPasswordEncryptionEnabled;
    }

    /**
     * @since 4.0.7
     */
    public void setLoginPasswordEncryptionEnabled(boolean loginPasswordEncryptionEnabled) {
        this.loginPasswordEncryptionEnabled = loginPasswordEncryptionEnabled;
    }

    /**
     * @since 4.0.7
     */
    public String getLoginPasswordEncryptionSalt() {
        return loginPasswordEncryptionSalt;
    }

    /**
     * @since 4.0.7
     */
    public void setLoginPasswordEncryptionSalt(String loginPasswordEncryptionSalt) {
        this.loginPasswordEncryptionSalt = loginPasswordEncryptionSalt;
    }

    /**
     * @since 4.0.7
     */
    public boolean isSuperadminLoginPasswordResetAllowed() {
        return superadminLoginPasswordResetAllowed;
    }

    /**
     * @since 4.0.7
     */
    public void setSuperadminLoginPasswordResetAllowed(boolean superadminLoginPasswordResetAllowed) {
        this.superadminLoginPasswordResetAllowed = superadminLoginPasswordResetAllowed;
    }

    public boolean isSsoEnabled() {
        return ssoEnabled;
    }

    public void setSsoEnabled(boolean ssoEnabled) {
        this.ssoEnabled = ssoEnabled;
    }

    public boolean isSsoKerberosDebug() {
        return ssoKerberosDebug;
    }

    public void setSsoKerberosDebug(boolean ssoKerberosDebug) {
        this.ssoKerberosDebug = ssoKerberosDebug;
    }

    public String getSsoJaasConfigName() {
        return ssoJaasConfigName;
    }

    public void setSsoJaasConfigName(String ssoJaasConfigName) {
        this.ssoJaasConfigName = ssoJaasConfigName;
    }

    public boolean isSsoUseLocalJaasConfig() {
        return ssoUseLocalJaasConfig;
    }

    public void setSsoUseLocalJaasConfig(boolean ssoUseLocalJaasConfig) {
        this.ssoUseLocalJaasConfig = ssoUseLocalJaasConfig;
    }

    public String getSsoJaasPrincipalPassword() {
        return ssoJaasPrincipalPassword;
    }

    public void setSsoJaasPrincipalPassword(String ssoJaasPrincipalPassword) {
        this.ssoJaasPrincipalPassword = ssoJaasPrincipalPassword;
    }

    public boolean isSsoUseLocalKrbConfig() {
        return ssoUseLocalKrbConfig;
    }

    public void setSsoUseLocalKrbConfig(boolean ssoUseLocalKrbConfig) {
        this.ssoUseLocalKrbConfig = ssoUseLocalKrbConfig;
    }

    public String getIndexDisabledFileExtensions() {
        return indexDisabledFileExtensions;
    }

    public void setIndexDisabledFileExtensions(String indexDisabledFileExtensions) {
        this.indexDisabledFileExtensions = indexDisabledFileExtensions;
        this.indexDisabledFileExtensionsSet = distinctLowerCased(splitCommaSeparatedString(indexDisabledFileExtensions));
    }

    public Set<String> getIndexDisabledFileExtensionsAsSet() {
        return indexDisabledFileExtensionsSet;
    }

    public String getIndexDisabledFileMimes() {
        return indexDisabledFileMimes;
    }

    public void setIndexDisabledFileMimes(String indexDisabledFileMimes) {
        this.indexDisabledFileMimes = indexDisabledFileMimes;
        this.indexDisabledFileMimesSet = distinctLowerCased(splitCommaSeparatedString(indexDisabledFileMimes));
    }

    public Set<String> getIndexDisabledFileMimesAsSet() {
        return indexDisabledFileMimesSet;
    }
}
