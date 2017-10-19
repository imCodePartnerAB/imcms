package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.mock.MockDatabase;
import com.imcode.imcms.api.DocumentLanguages;
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
import imcode.util.net.SMTP;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.security.KeyStore;
import java.util.Date;
import java.util.List;

public class MockImcmsServices implements ImcmsServices {

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper;
    private Database database = new MockDatabase();
    private KeyStore keyStore;
    private LanguageMapper languageMapper = new LanguageMapper(null, null);
    private RoleGetter roleGetter;
    private ProcedureExecutor procedureExecutor;
    private Config config = new Config();

    @Override
    public <T> T getManagedBean(Class<T> requiredType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public UserDomainObject verifyUser(String login, String password) {
        return null;
    }

    public UserDomainObject verifyUser(String clientPrincipalName) {
        return null;
    }

    public void incrementSessionCounter() {
    }

    // set  session counter date
    public Date getSessionCounterDate() {
        return null;
    }

    // set  session counter date
    public void setSessionCounterDate(Date date) {
    }

    // parsedoc use template
    public String getAdminTemplate(String adminTemplateName, UserDomainObject user, List tagsWithReplacements) {
        return null;
    }

    // parseExternaldoc use template
    public String getTemplateFromDirectory(String adminTemplateName, UserDomainObject user, List variables,
                                           String directory) {
        return null;
    }

    public SystemData getSystemData() {
        return null;
    }

    public void setSystemData(SystemData sd) {
    }

    public int getSessionCounter() {
        return 0;
    }

    // set session counter
    public void setSessionCounter(int value) {
    }

    public void updateMainLog(String logMessage) {
    }

    public DocumentMapper getDocumentMapper() {
        return null;
    }

    public ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper() {
        return imcmsAuthenticatorAndUserAndRoleMapper;
    }

    public void setImcmsAuthenticatorAndUserAndRoleMapper(
            ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper) {
        this.imcmsAuthenticatorAndUserAndRoleMapper = imcmsAuthenticatorAndUserAndRoleMapper;
    }

    public TemplateMapper getTemplateMapper() {
        return null;
    }

    public SMTP getSMTP() {
        return null;
    }

    public VelocityEngine getVelocityEngine(UserDomainObject user) {
        return null;
    }

    public VelocityContext getVelocityContext(UserDomainObject user) {
        return null;
    }

    public Config getConfig() {
        return this.config;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public CategoryMapper getCategoryMapper() {
        return null;
    }

    public LanguageMapper getLanguageMapper() {
        return languageMapper;
    }

    public void setLanguageMapper(LanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }

    public CachingFileLoader getFileCache() {
        return null;
    }

    public RoleGetter getRoleGetter() {
        return roleGetter;
    }

    public void setRoleGetter(RoleGetter roleGetter) {
        this.roleGetter = roleGetter;
    }

    public ProcedureExecutor getProcedureExecutor() {
        return procedureExecutor;
    }

    public void setProcedureExecutor(ProcedureExecutor procedureExecutor) {
        this.procedureExecutor = procedureExecutor;
    }

    public UserDomainObject verifyUserByIpOrDefault(String remoteAddr) {
        return null;
    }

    public LocalizedMessageProvider getLocalizedMessageProvider() {
        throw new NotImplementedException("imcode.server.MockImcmsServices.getLocalizedMessageFactory");
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public KerberosLoginService getKerberosLoginService() {
        return null;
    }

    public DocumentLanguages getDocumentLanguages() {
        return null;
    }
}
