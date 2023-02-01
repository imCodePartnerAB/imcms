package com.imcode.imcms.domain.factory;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.MailService;
import com.imcode.imcms.api.MultiFactorAuthenticationService;
import com.imcode.imcms.api.SmsService;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.persistence.entity.User;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import static com.imcode.imcms.servlet.VerifyUser.REQUEST_ATTRIBUTE__ERROR;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ImcmsServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private ImcmsServices imcmsServices;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private MailService mailService;

	@Autowired
	private SmsService smsService;

    @Autowired
    private AccessService accessService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AuthenticationProvidersService authenticationProvidersService;

    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private DocumentUrlService documentUrlService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LoopService loopService;

    @Autowired
    private TextDocumentTemplateService textDocumentTemplateService;

    @Autowired
    private UserService userService;

    @Autowired
    private VersionService versionService;

    @Autowired
    private UserDataInitializer userDataInitializer;

	@Autowired
	private MultiFactorAuthenticationService multiFactorAuthenticationService;

    @Test
    public void languageService_Expect_CorrectBeanClass() {
        assertEquals(languageService, imcmsServices.getLanguageService());
    }

    @Test
    public void mailService_Expect_CorrectBeanClass() {
        assertEquals(mailService, imcmsServices.getMailService());
    }

	@Test
	public void smsService_Expect_CorrectBeanClass() {
		assertEquals(smsService, imcmsServices.getSmsService());
	}

    @Test
    public void accessService_Expect_CorrectBeanClass() {
        assertEquals(accessService, imcmsServices.getAccessService());
    }

    @Test
    public void templateService_Expect_CorrectBeanClass() {
        assertEquals(templateService, imcmsServices.getTemplateService());
    }

    @Test
    public void authenticationProvidersService_Expect_CorrectBeanClass() {
        assertEquals(authenticationProvidersService, imcmsServices.getAuthenticationProvidersService());
    }

    @Test
    public void commonContentService_Expect_CorrectBeanClass() {
        assertEquals(commonContentService, imcmsServices.getCommonContentService());
    }

    @Test
    public void documentUrlService_Expect_CorrectBeanClass() {
        assertEquals(documentUrlService, imcmsServices.getDocumentUrlService());
    }

    @Test
    public void imageService_Expect_CorrectBeanClass() {
        assertEquals(imageService, imcmsServices.getImageService());
    }

    @Test
    public void loopService_Expect_CorrectBeanClass() {
        assertEquals(loopService, imcmsServices.getLoopService());
    }

    @Test
    public void textDocumentTemplateService_Expect_CorrectBeanClass() {
        assertEquals(textDocumentTemplateService, imcmsServices.getTextDocumentTemplateService());
    }

    @Test
    public void userService_Expect_CorrectBeanClass() {
        assertEquals(userService, imcmsServices.getUserService());
    }

    @Test
    public void versionService_Expect_CorrectBeanClass() {
        assertEquals(versionService, imcmsServices.getVersionService());
    }

	@Test
	public void multiFactorAuthenticationService_Expect_CorrectBeanClass(){
		assertEquals(multiFactorAuthenticationService, imcmsServices.getMultiFactorAuthenticationService());
	}

    @Test
    public void verifyUser_WhenUserExist_And_LoginAndPasswordIsCorrect_Expect_CorrectResult(){
        final UserDomainObject imcmsUser = new UserDomainObject(1);
        imcmsUser.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(imcmsUser);

        userDataInitializer.cleanRepositories();

        User user = userDataInitializer.createData("login");
        user.setLanguageIso639_2("eng");
        userService.saveUser(new UserFormData(user));

        assertNotNull(user);
        assertNull(user.getLastLoginDate());

        User expectedUser = new User(imcmsServices.verifyUser(user.getLogin(), user.getPassword()));
        assertNotNull(expectedUser);

        user = userService.getUser(user.getId());
        assertNotNull(user.getLastLoginDate());
        assertEquals(expectedUser.getLastLoginDate().getTime()/1000,
                        user.getLastLoginDate().getTime()/1000);
    }

	@Test
	public void verifyUser_WhenUserExist_And_LoginAndPasswordIsCorrect_And_2FAEnabled_And_PhoneNumberIsEmpty_Expect_CorrectResult(){
        final UserDomainObject imcmsUser = new UserDomainObject(1);
        imcmsUser.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(imcmsUser);

		userDataInitializer.cleanRepositories();

		User user = userDataInitializer.createData("login");
		user.setTwoFactoryAuthenticationEnabled(true);
		user.setLanguageIso639_2("eng");

		userService.saveUser(new UserFormData(user));

		assertNotNull(user);
		assertNull(user.getLastLoginDate());

		assertNull(imcmsServices.verifyUser(user.getLogin(), user.getPassword()));

		user = userService.getUser(user.getId());
		assertNull(user.getLastLoginDate());

		final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		final HttpServletRequest request = servletRequestAttributes.getRequest();
		assertNotNull(request.getAttribute(REQUEST_ATTRIBUTE__ERROR));
	}

    @Test
    public void verifyUser_WhenUserExist_And_LoginAndPasswordIsNotCorrect_Expect_CorrectResult(){
        userDataInitializer.cleanRepositories();

        User user = userDataInitializer.createData("login");
        user.setLanguageIso639_2("eng");
        userService.saveUser(new UserFormData(user));

        assertNotNull(user);
        assertNull(user.getLastLoginDate());

        int attempts = user.getAttempts();

        UserDomainObject expectedUser = imcmsServices.verifyUser(user.getLogin(), "wrong password");
        assertNull(expectedUser);

        user = userService.getUser(user.getId());
        assertNull(user.getLastLoginDate());
        assertEquals(attempts + 1 , (int) user.getAttempts());
    }

	@Test
	public void verifyUser_WhenUserExist_And_LoginAndPasswordIsNotCorrect_And_2FAEnabled_Expect_CorrectResult(){
        final UserDomainObject imcmsUser = new UserDomainObject(1);
        imcmsUser.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        Imcms.setUser(imcmsUser);

        userDataInitializer.cleanRepositories();

		User user = userDataInitializer.createData("login");
		user.setTwoFactoryAuthenticationEnabled(true);
		user.setLanguageIso639_2("eng");
		userService.saveUser(new UserFormData(user));

		assertNotNull(user);
		assertNull(user.getLastLoginDate());

		int attempts = user.getAttempts();

		UserDomainObject expectedUser = imcmsServices.verifyUser(user.getLogin(), "wrong password");
		assertNull(expectedUser);

		user = userService.getUser(user.getId());
		assertNull(user.getLastLoginDate());
		assertEquals(attempts + 1 , (int) user.getAttempts());
	}
}
