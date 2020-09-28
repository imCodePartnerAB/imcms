package com.imcode.imcms.domain.factory;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.MailService;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.AuthenticationProvidersService;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.domain.service.VersionService;
import imcode.server.ImcmsServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImcmsServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private ImcmsServices imcmsServices;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private MailService mailService;

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


    @Test
    public void languageService_Expect_CorrectBeanClass() {
        assertEquals(languageService, imcmsServices.getLanguageService());
    }

    @Test
    public void mailService_Expect_CorrectBeanClass() {
        assertEquals(mailService, imcmsServices.getMailService());
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

}
