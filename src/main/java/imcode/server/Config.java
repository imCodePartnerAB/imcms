package imcode.server;

import java.io.File;
import java.nio.charset.Charset;
import java.security.KeyStore;

import org.apache.commons.lang.StringUtils;

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
    private String imageUrl;
    private String smtpServer;
    private int smtpPort;

    /** Admin interface language. Not related to default content language (I18nLanguage). */
    private String defaultLanguage;
    
    private String sessionCookieDomain;
    private String fileAdminRootPaths;
    private float indexingSchedulePeriodInMinutes;
    private String documentPathPrefix;
    private int documentCacheMaxSize = 100 ;
    private String keyStorePath ;
    private String keyStoreType ;
    private String workaroundUriEncoding;
    private boolean secureLoginRequired;

    /**  */
    private boolean denyMultipleUserLogin;

    public String getWorkaroundUriEncoding() {
        return workaroundUriEncoding;
    }

    public void setWorkaroundUriEncoding(String workaroundUriEncoding) {
        Charset charset = StringUtils.isNotBlank(workaroundUriEncoding) ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset() ;
        this.workaroundUriEncoding = charset.name();
    }

    public void setTemplatePath( File templatePath ) {
        this.templatePath = templatePath;
    }

    public void setIncludePath( File includePath ) {
        this.includePath = includePath;
    }

    public void setFilePath( File filePath ) {
        this.filePath = filePath;
    }

    public void setImageUrl( String imageUrl ) {
        this.imageUrl = imageUrl;
    }

    public void setSmtpServer( String smtpServer ) {
        this.smtpServer = smtpServer;
    }

    public void setSmtpPort( int smtpPort ) {
        this.smtpPort = smtpPort;
    }

    public void setDefaultLanguage( String defaultLanguage ) {
        try {
            if ( defaultLanguage.length() < 3 ) {
                defaultLanguage = LanguageMapper.convert639_1to639_2( defaultLanguage );
            }
        } catch ( LanguageMapper.LanguageNotSupportedException e1 ) {
            defaultLanguage = null;
        }
        this.defaultLanguage = defaultLanguage;
    }

    public File getFilePath() {
        return filePath;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public File getTemplatePath() {
        return templatePath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public File getIncludePath() {
        return includePath;
    }

    public String getSessionCookieDomain() {
        return sessionCookieDomain;
    }

    public void setSessionCookieDomain( String sessionCookieDomain ) {
        this.sessionCookieDomain = sessionCookieDomain;
    }

    public File getImagePath() {
        return imagePath;
    }

    public void setImagePath( File imagePath ) {
        this.imagePath = imagePath;
    }

    public String getFileAdminRootPaths() {
        return fileAdminRootPaths;
    }

    public void setFileAdminRootPaths( String fileAdminRootPaths ) {
        this.fileAdminRootPaths = fileAdminRootPaths;
    }

    public float getIndexingSchedulePeriodInMinutes() {
        return indexingSchedulePeriodInMinutes;
    }

    public void setIndexingSchedulePeriodInMinutes( float indexingSchedulePeriodInMinutes ) {
        this.indexingSchedulePeriodInMinutes = indexingSchedulePeriodInMinutes;
    }

    public String getDocumentPathPrefix() {
        return documentPathPrefix;
    }

    public void setDocumentPathPrefix( String documentPathPrefix ) {
        this.documentPathPrefix = documentPathPrefix;
    }

    public int getDocumentCacheMaxSize() {
        return documentCacheMaxSize;
    }

    public void setDocumentCacheMaxSize( int documentCacheMaxSize ) {
        this.documentCacheMaxSize = documentCacheMaxSize;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath( String keyStorePath ) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType( String keyStoreType ) {
        if (StringUtils.isBlank(keyStoreType)) {
            keyStoreType = KeyStore.getDefaultType() ;
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
}
