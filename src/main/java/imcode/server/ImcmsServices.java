package imcode.server;

import com.imcode.db.Database;
import com.imcode.imcms.api.*;
import com.imcode.imcms.components.ImageCompressor;
import com.imcode.imcms.db.ProcedureExecutor;
import com.imcode.imcms.domain.component.UserLockValidator;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.server.document.TemplateMapper;
import imcode.server.kerberos.KerberosLoginService;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleGetter;
import imcode.server.user.UserDomainObject;
import imcode.util.CachingFileLoader;

import java.security.KeyStore;
import java.util.Date;

public interface ImcmsServices {

    /**
     * Verify a Internet/Intranet user. Data from any SQL Database. *
     */
    UserDomainObject verifyUser(String login, String password);

    // Verify a Intranet user based on a Kerberos client principal name.
    UserDomainObject verifyUser(String clientPrincipalName);

    void incrementSessionCounter();

    // set  session counter date
    Date getSessionCounterDate();

    // set  session counter date
    void setSessionCounterDate(Date date);

    /**
     * @deprecated use {@link TemplateService#getTemplateAdminPath(String)}
     */
    @Deprecated
    // return template path
    String getAdminTemplatePath(String adminTemplateName);

    // parsedoc use template
    String getAdminTemplate(String adminTemplateName, UserDomainObject user, java.util.List<String> tagsWithReplacements);

    // parseExternaldoc use template
    String getTemplateFromDirectory(String adminTemplateName, UserDomainObject user, java.util.List<String> variables,
                                    String directory);

    SystemData getSystemData();

    void setSystemData(SystemData sd);

    void reloadSystemData();

    int getSessionCounter();

    // set session counter
    void setSessionCounter(int value);

    void updateMainLog(String logMessage);

    DocumentMapper getDocumentMapper();

    ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper();

    TemplateMapper getTemplateMapper();

    Config getConfig();

    KeyStore getKeyStore();

    Database getDatabase();

    CategoryMapper getCategoryMapper();

    LanguageMapper getLanguageMapper();

    CachingFileLoader getFileCache();

    RoleGetter getRoleGetter();

    ProcedureExecutor getProcedureExecutor();

    LocalizedMessageProvider getLocalizedMessageProvider();

    KerberosLoginService getKerberosLoginService();

    @Deprecated
    DocumentLanguages getDocumentLanguages();

    LanguageService getLanguageService();

    <T> T getManagedBean(Class<T> requiredType);

    <T> T getManagedBean(String name, Class<T> requiredType);

    DatabaseService getDatabaseService();

    MailService getMailService();

	SmsService getSmsService();

    TemplateService getTemplateService();

	TemplateCSSService getTemplateCSSService();

    MenuService getMenuService();

    AccessService getAccessService();

    AuthenticationProvidersService getAuthenticationProvidersService();

    DelegatingByTypeDocumentService getDocumentService();

    CommonContentService getCommonContentService();

    DocumentUrlService getDocumentUrlService();

    ImageService getImageService();

    LoopService getLoopService();

    TextDocumentTemplateService getTextDocumentTemplateService();

    UserService getUserService();

    VersionService getVersionService();

    DocumentRolesService getDocumentRolesService();

    UserPropertyService getUserPropertyService();

    DocumentDataService getDocumentDataService();

    SearchDocumentService getSearchDocumentService();

    DocumentWasteBasketService getDocumentWasteBasketService();

    UserLockValidator getUserLockValidator();

    ImageCompressor getImageCompressor();

	MultiFactorAuthenticationService getMultiFactorAuthenticationService();
}
