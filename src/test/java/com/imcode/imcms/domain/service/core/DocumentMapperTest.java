package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentService;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.Text;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class DocumentMapperTest extends WebAppSpringTestConfig {

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private TextService textService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private com.imcode.imcms.domain.service.DocumentService<TextDocumentDTO> textDocumentService;


    @BeforeEach
    public void setUp() {
        documentDataInitializer.cleanRepositories();
        versionDataInitializer.cleanRepositories();

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());

        DocGetterCallback docGetterCallback = user.getDocGetterCallback();
        DocumentLanguage language = DocumentLanguage.builder()
                .code("en")
                .build();

        user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
        docGetterCallback.setLanguage(language);
        Imcms.setUser(user);
    }


    @Test
    public void saveChanges_When_TextDocumentHasTextFields_Expected_CorrectResult() {
        final ContentManagementSystem contentManagementSystem = ContentManagementSystem.create(Imcms.getServices(), Imcms.getUser());
        final Integer docId = documentDataInitializer.createData().getId();
        versionDataInitializer.createData(0, docId);
        DocumentService documentService = contentManagementSystem.getDocumentService();

        TextDocument textDocument = documentService.getTextDocument(docId);

        final String testText = "someText";
        final String testText2 = "someText2";

        textDocument.setHtmlTextField(1, testText, true);
        textDocument.setHtmlTextField(2, testText2);

        Integer savedDocId = documentService.saveChanges(textDocument);

        assertEquals(docId, savedDocId);

        final TextDocumentDomainObject savedTextDoc = documentService.getTextDocument(savedDocId).getInternal();

        final Collection<TextDomainObject> texts = savedTextDoc.getTexts().values();

        assertFalse(texts.isEmpty());

        assertEquals(2, texts.size());

        assertEquals(testText, savedTextDoc.getText(1).getText());
        assertEquals(testText2, savedTextDoc.getText(2).getText());

    }


    @Test
    public void saveChanges_When_TextDocumentHasNotTextFields_Expected_EmptyResult() {
        final ContentManagementSystem contentManagementSystem = ContentManagementSystem.create(Imcms.getServices(), Imcms.getUser());
        final Integer docId = documentDataInitializer.createData().getId();
        versionDataInitializer.createData(0, docId);
        DocumentService documentService = contentManagementSystem.getDocumentService();

        TextDocument textDocument = documentService.getTextDocument(docId);

        Integer savedDocId = documentService.saveChanges(textDocument);

        assertEquals(docId, savedDocId);

        final TextDocumentDomainObject savedTextDoc = documentService.getTextDocument(savedDocId).getInternal();

        final Collection<TextDomainObject> texts = savedTextDoc.getTexts().values();

        assertTrue(texts.isEmpty());
    }


    @Test
    public void saveChanges_When_TextDocumentHasPublishedTextFields_Expected_CorrectResult() {
        final ContentManagementSystem contentManagementSystem = ContentManagementSystem.create(Imcms.getServices(), Imcms.getUser());
        final Integer docId = documentDataInitializer.createData().getId();
        versionDataInitializer.createData(0, docId);
        DocumentService documentService = contentManagementSystem.getDocumentService();

        TextDocument textDocument = documentService.getTextDocument(docId);

        final String testText = "someText";
        final String testText2 = "someText2";

        textDocument.setHtmlTextField(1, testText, true);
        textDocument.setHtmlTextField(2, testText2, true);

        Integer savedDocId = documentService.saveChanges(textDocument);

        assertEquals(docId, savedDocId);

        final TextDocumentDomainObject savedTextDoc = documentService.getTextDocument(savedDocId).getInternal();

        final Collection<TextDomainObject> texts = savedTextDoc.getTexts().values();

        assertFalse(texts.isEmpty());

        assertEquals(2, texts.size());

        assertEquals(testText, savedTextDoc.getText(1).getText());
        assertEquals(testText2, savedTextDoc.getText(2).getText());

        final Set<Text> publicTexts = textService.getPublicTexts(savedTextDoc.getId(), languageService.findByCode(Imcms.getUser().getLanguage()));

        assertFalse(publicTexts.isEmpty());

        assertEquals(2, publicTexts.size());
    }

    @Test
    public void saveChanges_When_TextDocumentDiffTextFields_Expected_CorrectResult() {
        final ContentManagementSystem contentManagementSystem = ContentManagementSystem.create(Imcms.getServices(), Imcms.getUser());
        final Integer docId = documentDataInitializer.createData().getId();
        final UserDomainObject user = Imcms.getUser();

        versionDataInitializer.createData(1, docId);

        DocumentService documentService = contentManagementSystem.getDocumentService();

        TextDocument textDocument = documentService.getTextDocument(docId);

        final String testText = "someText";
        final String testText2 = "someText2";


        textDocument.setHtmlTextField(1, testText);
        textDocument.setHtmlTextField(2, testText2, true);

        Integer savedDocId = documentService.saveChanges(textDocument);

        assertEquals(docId, savedDocId);

        TextDocumentDomainObject savedTextDoc = documentService.getTextDocument(savedDocId).getInternal();

        final Collection<TextDomainObject> texts = savedTextDoc.getTexts().values();

        assertFalse(texts.isEmpty());

        assertEquals(2, texts.size());

        assertEquals(testText, savedTextDoc.getText(1).getText());
        assertEquals(testText2, savedTextDoc.getText(2).getText());


        final Set<Text> publicTexts = textService.getPublicTexts(savedTextDoc.getId(), languageService.findByCode(user.getLanguage()));

        assertFalse(publicTexts.isEmpty());

        assertEquals(1, publicTexts.size());
    }
}
