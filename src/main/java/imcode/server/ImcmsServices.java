package imcode.server;

import com.imcode.db.Database;
import com.imcode.imcms.api.DatabaseService;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.api.MailService;
import com.imcode.imcms.db.ProcedureExecutor;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.server.document.TemplateMapper;
import imcode.server.kerberos.KerberosLoginService;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleGetter;
import imcode.server.user.UserDomainObject;
import imcode.util.CachingFileLoader;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

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

    // parsedoc use template
    String getAdminTemplate(String adminTemplateName, UserDomainObject user, java.util.List<String> tagsWithReplacements);

    // parseExternaldoc use template
    String getTemplateFromDirectory(String adminTemplateName, UserDomainObject user, java.util.List<String> variables,
                                    String directory);

    SystemData getSystemData();

    void setSystemData(SystemData sd);

    int getSessionCounter();

    // set session counter
    void setSessionCounter(int value);

    void updateMainLog(String logMessage);

    DocumentMapper getDocumentMapper();

    ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper();

    TemplateMapper getTemplateMapper();

    VelocityEngine getVelocityEngine(UserDomainObject user);

    VelocityContext getVelocityContext(UserDomainObject user);

    Config getConfig();

    KeyStore getKeyStore();

    Database getDatabase();

    CategoryMapper getCategoryMapper();

    LanguageMapper getLanguageMapper();

    CachingFileLoader getFileCache();

    RoleGetter getRoleGetter();

    ProcedureExecutor getProcedureExecutor();

    UserDomainObject verifyUserByIpOrDefault(String remoteAddr);

    LocalizedMessageProvider getLocalizedMessageProvider();

    KerberosLoginService getKerberosLoginService();

    DocumentLanguages getDocumentLanguages();

    <T> T getManagedBean(Class<T> requiredType);

    void init();

    DatabaseService getDatabaseService();

    MailService getMailService();
}
