package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.mock.MockDatabase;
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
import org.apache.commons.lang3.NotImplementedException;

import java.security.KeyStore;
import java.util.Date;
import java.util.List;

public class MockImcmsServices implements ImcmsServices {

    private ImcmsAuthenticatorAndUserAndRoleMapper imcmsAuthenticatorAndUserAndRoleMapper;
    private Database database = new MockDatabase();
    private LanguageMapper languageMapper = new LanguageMapper(null, null);
    private RoleGetter roleGetter;
    private ProcedureExecutor procedureExecutor;
    private final Config config = new Config();

    @Override
    public <T> T getManagedBean(Class<T> requiredType) {
        return null;
    }

    @Override
    public <T> T getManagedBean(String name, Class<T> requiredType) {
        return null;
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

    public void reloadSystemData() {
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
        return null;
    }

    public KerberosLoginService getKerberosLoginService() {
        return null;
    }

    public DocumentLanguages getDocumentLanguages() {
        return null;
    }

    public LanguageService getLanguageService() {
        return null;
    }

    @Override
    public DatabaseService getDatabaseService() {
        return null;
    }

    @Override
    public MailService getMailService() {
        return null;
    }

	@Override
	public SmsService getSmsService() {
		return null;
	}

	@Override
    public TemplateService getTemplateService() {
        return null;
    }

	@Override
	public TemplateCSSService getTemplateCSSService() {
		return null;
	}

	@Override
    public MenuService getMenuService() {
        return null;
    }

    @Override
    public AccessService getAccessService() {
        return null;
    }

    @Override
    public AuthenticationProvidersService getAuthenticationProvidersService() {
        return null;
    }

    @Override
    public DelegatingByTypeDocumentService getDocumentService() {
        return null;
    }

    @Override
    public CommonContentService getCommonContentService() {
        return null;
    }

    @Override
    public DocumentUrlService getDocumentUrlService() {
        return null;
    }

    @Override
    public ImageService getImageService() {
        return null;
    }

    @Override
    public LoopService getLoopService() {
        return null;
    }

    @Override
    public TextDocumentTemplateService getTextDocumentTemplateService() {
        return null;
    }

    @Override
    public UserService getUserService() {
        return null;
    }

    @Override
    public VersionService getVersionService() {
        return null;
    }

    @Override
    public DocumentRolesService getDocumentRolesService() {
        return null;
    }

    @Override
    public UserPropertyService getUserPropertyService() {
        return null;
    }

    @Override
    public DocumentDataService getDocumentDataService() {
        return null;
    }

    @Override
    public SearchDocumentService getSearchDocumentService(){
        return null;
    }

    @Override
    public DocumentWasteBasketService getDocumentWasteBasketService() {
        return null;
    }

    @Override
    public UserLockValidator getUserLockValidator() {
        return null;
    }

    @Override
    public ImageCompressor getImageCompressor(){
        return null;
    }
	@Override
	public MultiFactorAuthenticationService getMultiFactorAuthenticationService() {
		return null;
	}

    public DelegatingByTypeDocumentService getDelegatingByTypeDocService() {
        return null;
    }

    @Override
    public String getAdminTemplatePath(String adminTemplateName) {
        return null;
    }
}
