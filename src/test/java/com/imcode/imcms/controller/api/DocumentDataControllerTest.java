package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.DocumentContentDataInitializer;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentDataDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import static com.imcode.imcms.persistence.entity.Version.WORKING_VERSION_INDEX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Transactional
public class DocumentDataControllerTest  extends AbstractControllerTest {

    @Autowired
    private DocumentDataInitializer documentDataInitializer;
    @Autowired
    private DocumentContentDataInitializer documentContentDataInitializer;
    @Autowired
    private VersionDataInitializer versionDataInitializer;
    @Autowired
    private LanguageDataInitializer languageDataInitializer;
    @Autowired
    private DocumentService<DocumentDTO> documentService;

    @BeforeEach
    public void setUp() {
        documentContentDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();

        final UserDomainObject userSuperAdmin = new UserDomainObject(1);
        userSuperAdmin.setLanguageIso639_2("eng");
        userSuperAdmin.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(userSuperAdmin);

        final Language currentLanguage = languageDataInitializer.createData().get(0);
        Imcms.setLanguage(currentLanguage);
    }

    @Override
    protected String controllerPath() {
        return "/document/all-data";
    }

    @Test
    public void getDocumentData_When_DocIdExistAndWorkingVersion_Expected_OkAndCorrectResult() throws Exception {
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        int docId = documentDTO.getId();

        assertEquals(documentDTO.getCurrentVersion().getId(), (Integer) WORKING_VERSION_INDEX);

        Version version = versionDataInitializer.createData(WORKING_VERSION_INDEX, docId);
        DocumentDataDTO documentDataDTO = documentContentDataInitializer.createData(version);

        String url = controllerPath() + "/" + docId;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(documentDataDTO));
    }

    @Test
    public void getDocumentData_When_DocIdExistAndLatestVersion_Expected_EmptyResult() throws Exception {
        final DocumentDTO documentDTO = documentDataInitializer.createData();
        int docId = documentDTO.getId();

        assertEquals(documentDTO.getLatestVersion().getId(), (Integer) WORKING_VERSION_INDEX);

        documentService.publishDocument(docId, Imcms.getUser().getId());
        final DocumentDTO publishedDocument = documentService.get(docId);
        assertNotEquals(publishedDocument.getLatestVersion().getId(), WORKING_VERSION_INDEX);

        Version version = versionDataInitializer.createData(WORKING_VERSION_INDEX, docId);
        documentContentDataInitializer.createData(version);
        final DocumentDataDTO emptyDoc = documentContentDataInitializer.createData();

        String url = controllerPath() + "/" + docId;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(emptyDoc));
    }

    @Test
    public void getAllData_When_DocIdNotExist_Expected_OkAndEmptyResult() throws Exception {
        int fakeDocId = 0;
        String url = controllerPath() + "/" + fakeDocId;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);
        performRequestBuilderExpectException(DocumentNotExistException.class, requestBuilder);

    }

    private void setCommonUser() {
        final UserDomainObject commonUser = new UserDomainObject(2);
        commonUser.setLanguageIso639_2("eng");
        commonUser.addRoleId(Roles.USER.getId());
        Imcms.setUser(commonUser);
    }
}
