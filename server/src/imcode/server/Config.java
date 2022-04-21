package imcode.server;

import imcode.server.user.RoleId;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.*;

public class Config {

    private File templatePath;
    private File includePath;
    private File filePath;
    private File imagePath;
    private File imageCachePath;
    private String imageCacheAllowedPaths;
    private long imageCacheMaxSize;
    // Maximum number of cached ImageSize objects for realSize of an ImageDomainObject
    private int imageSizeCacheObjects = 10000;
    private String imageUrl;
    private String smtpServer;
    private int smtpPort;
    private String defaultLanguage;
    private String sessionCookieDomain;
    private String fileAdminRootPaths;
    private float indexingSchedulePeriodInMinutes;
    private String documentPathPrefix;
    private int documentCacheMaxSize = 100;
    private String keyStorePath;
    private String keyStoreType;
    private String workaroundUriEncoding;
    private boolean secureLoginRequired;
    private boolean denyMultipleUserLogin;
    private String imageArchiveUrl;
    private String imageArchiveAllowedRoleIds;
    private List<RoleId> imageArchiveAllowedRoleIdList = Collections.emptyList();
    private String chooseFileAllowedRoleIds;
    private List<RoleId> chooseFileAllowedRoleIdsList = Collections.emptyList();
    private File imageArchiveImagePath;
    private String imageArchiveImageUrl;
    private File imageMagickPath;
    private DatabaseVendor databaseVendor;

    private boolean ssoEnabled;
    private boolean ssoUseLocalJaasConfig;
    private String ssoJaasConfigName;
    private String ssoJaasPrincipalPassword;
    private boolean ssoUseLocalKrbConfig;
    private boolean ssoKerberosDebug;

    private String loginPasswordEncryptionSalt;
    private boolean loginPasswordEncryptionEnabled;
    private boolean superadminLoginPasswordResetAllowed;
    private String indexDisabledFileExtensions;
    private String indexDisabledFileMimes;
    private Set<String> indexDisabledFileExtensionsSet = Collections.emptySet();
    private Set<String> indexDisabledFileMimesSet = Collections.emptySet();
    private Map<String, AuthenticationMethodConfiguration> authenticationConfiguration = new HashMap<>();
    private String cgiUserRoleName;
    private String cgiMetadataUrl;
    private String serverName;
	private String allowFileAdmin;
    private static List<RoleId> convertRoleIds(String roleIdsString) {
        String[] ids = StringUtils.split(roleIdsString, ',');
        List<RoleId> roleIds = new ArrayList<>(ids.length);

        for (String id : ids) {
            try {
                roleIds.add(new RoleId(Integer.parseInt(id.trim())));
            } catch (NumberFormatException ex) {
            }
        }

        return roleIds;
    }

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

    public Map<String, AuthenticationMethodConfiguration> getAuthenticationConfiguration() {
        return authenticationConfiguration;
    }

    public void setAuthenticationConfiguration(String methods) {
        String[] methodList = methods.split(",\\s?+");
        int index = 1;

        for (String authenticationMethodName : methodList) {
            AuthenticationMethodConfiguration configuration = new AuthenticationMethodConfiguration()
                    .setName(authenticationMethodName)
                    .setOrder(index);

            authenticationConfiguration.put(authenticationMethodName, configuration);
            ++index;
        }
    }

    public String getWorkaroundUriEncoding() {
        return workaroundUriEncoding;
    }

    public void setWorkaroundUriEncoding(String workaroundUriEncoding) {
        Charset charset = StringUtils.isNotBlank(workaroundUriEncoding) ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset();
        workaroundUriEncoding = charset.name();
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

    public float getIndexingSchedulePeriodInMinutes() {
        return indexingSchedulePeriodInMinutes;
    }

    public void setIndexingSchedulePeriodInMinutes(float indexingSchedulePeriodInMinutes) {
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

    public String getImageArchiveUrl() {
        return imageArchiveUrl;
    }

    public void setImageArchiveUrl(String imageArchiveUrl) {
        this.imageArchiveUrl = imageArchiveUrl;
    }

    public File getImageArchiveImagePath() {
        return imageArchiveImagePath;
    }

    public void setImageArchiveImagePath(File imageArchiveImagePath) {
        this.imageArchiveImagePath = imageArchiveImagePath;
    }

    public String getImageArchiveImageUrl() {
        return imageArchiveImageUrl;
    }

    public void setImageArchiveImageUrl(String imageArchiveImageUrl) {
        this.imageArchiveImageUrl = imageArchiveImageUrl;
    }

    public String getImageArchiveAllowedRoleIds() {
        return imageArchiveAllowedRoleIds;
    }

    public void setImageArchiveAllowedRoleIds(String imageArchiveAllowedRoleIds) {
        this.imageArchiveAllowedRoleIds = imageArchiveAllowedRoleIds;

        if (imageArchiveAllowedRoleIds != null) {
            imageArchiveAllowedRoleIdList = convertRoleIds(imageArchiveAllowedRoleIds);
        }
    }

    public List<RoleId> getImageArchiveAllowedRoleIdList() {
        return imageArchiveAllowedRoleIdList;
    }

    public void setImageArchiveAllowedRoleIdList(List<RoleId> imageArchiveAllowedRoleIdList) {
        this.imageArchiveAllowedRoleIdList = imageArchiveAllowedRoleIdList;
    }

    public String getChooseFileAllowedRoleIds() {
        return chooseFileAllowedRoleIds;
    }

    public void setChooseFileAllowedRoleIds(String chooseFileAllowedRoleIds) {
        this.chooseFileAllowedRoleIds = chooseFileAllowedRoleIds;

        if (chooseFileAllowedRoleIds != null) {
            chooseFileAllowedRoleIdsList = convertRoleIds(chooseFileAllowedRoleIds);
        }
    }

    public List<RoleId> getChooseFileAllowedRoleIdsList() {
        return chooseFileAllowedRoleIdsList;
    }

    public void setChooseFileAllowedRoleIdsList(List<RoleId> chooseFileAllowedRoleIdsList) {
        this.chooseFileAllowedRoleIdsList = chooseFileAllowedRoleIdsList;
    }

    public File getImageCachePath() {
        return imageCachePath;
    }

    public void setImageCachePath(File imageCachePath) {
        this.imageCachePath = imageCachePath;
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

    public File getImageMagickPath() {
        return imageMagickPath;
    }

    public void setImageMagickPath(File imageMagickPath) {
        this.imageMagickPath = imageMagickPath;
    }

    public DatabaseVendor getDatabaseVendor() {
        return databaseVendor;
    }

    public void setDatabaseVendor(DatabaseVendor databaseVendor) {
        this.databaseVendor = databaseVendor;
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

    public boolean isLoginPasswordEncryptionEnabled() {
        return loginPasswordEncryptionEnabled;
    }

    public void setLoginPasswordEncryptionEnabled(boolean loginPasswordEncryptionEnabled) {
        this.loginPasswordEncryptionEnabled = loginPasswordEncryptionEnabled;
    }

    public String getLoginPasswordEncryptionSalt() {
        return loginPasswordEncryptionSalt;
    }

    public void setLoginPasswordEncryptionSalt(String loginPasswordEncryptionSalt) {
        this.loginPasswordEncryptionSalt = loginPasswordEncryptionSalt;
    }

    public boolean isSuperadminLoginPasswordResetAllowed() {
        return superadminLoginPasswordResetAllowed;
    }

    public void setSuperadminLoginPasswordResetAllowed(boolean superadminLoginPasswordResetAllowed) {
        this.superadminLoginPasswordResetAllowed = superadminLoginPasswordResetAllowed;
    }

    public int getImageSizeCacheObjects() {
        return imageSizeCacheObjects;
    }

    public void setImageSizeCacheObjects(int imageSizeCacheObjects) {
        this.imageSizeCacheObjects = imageSizeCacheObjects;
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

    public String getCgiUserRoleName() {
        return this.cgiUserRoleName;
    }

    public void setCgiUserRoleName(String cgiUserRoleName) {
        this.cgiUserRoleName = cgiUserRoleName;
    }

    public String getCgiMetadataUrl() {
        return this.cgiMetadataUrl;
    }

    public void setCgiMetadataUrl(String cgiMetadataUrl) {
        this.cgiMetadataUrl = cgiMetadataUrl;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

	public String getAllowFileAdmin() {
		return allowFileAdmin;
	}

	public void setAllowFileAdmin(String allowFileAdmin) {
		this.allowFileAdmin = allowFileAdmin;
	}
}
