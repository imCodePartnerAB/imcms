package imcode.server;

import java.io.File;

public class Config {

    private File templatePath;
    private File includePath;
    private File fortunePath;
    private File filePath;
    private File imcmsPath;
    private File imagePath;
    private String imageUrl;
    private String smtpServer;
    private int smtpPort;
    private String defaultLanguage;
    private String sessionCookieDomain;
    private String fileAdminRootPaths;
    private int indexingSchedulePeriodInMinutes = 1440 ;
    private String documentPathPrefix;
    private int documentCacheMaxSize = 100 ;
    private String keyStoreUrl;

    public void setTemplatePath( File templatePath ) {
        this.templatePath = templatePath;
    }

    public void setIncludePath( File includePath ) {
        this.includePath = includePath;
    }

    public void setFortunePath( File fortunePath ) {
        this.fortunePath = fortunePath;
    }

    public void setFilePath( File filePath ) {
        this.filePath = filePath;
    }

    public void setImcmsPath( File imcmsPath ) {
        this.imcmsPath = imcmsPath;
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

    public File getImcmsPath() {
        return imcmsPath;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public File getFortunePath() {
        return fortunePath;
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

    public int getIndexingSchedulePeriodInMinutes() {
        return indexingSchedulePeriodInMinutes;
    }

    public void setIndexingSchedulePeriodInMinutes( int indexingSchedulePeriodInMinutes ) {
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

    public String getKeyStoreUrl() {
        return keyStoreUrl;
    }

    public void setKeyStoreUrl( String keyStoreUrl ) {
        this.keyStoreUrl = keyStoreUrl;
    }
}
