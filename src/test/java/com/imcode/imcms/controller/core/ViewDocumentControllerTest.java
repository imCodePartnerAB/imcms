package com.imcode.imcms.controller.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.DocumentLanguageDisabledException;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.MetaRepository;
import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static imcode.server.ImcmsConstants.VIEW_DOC_PATH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ViewDocumentControllerTest extends WebAppSpringTestConfig {

    private static final String VIEW_DOC = VIEW_DOC_PATH + "/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private TextDocumentDataInitializer documentDataInitializer;

    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private MetaRepository metaRepository;

    private List<LanguageDTO> languages;
    private TextDocumentDTO textDocument;

    @BeforeEach
    public void setUp() {
        documentDataInitializer.cleanRepositories();
        languageDataInitializer.cleanRepositories();
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        this.languages = languageDataInitializer.createData();
        this.textDocument = documentDataInitializer.createTextDocument();
    }

    // 0 -> en
    // 1 -> sv

    @Test
    public void getDocument_WhenEnLanguageSetAndUserHasAllEnabledLanguages_Expect_ReturnedEnDocument()
            throws Exception {

        testWhenAllUsersLanguagesIsEnabled(0);
    }

    @Test
    public void getDocument_WhenSvLanguageSetAndUserHasAllEnabledLanguages_Expect_ReturnedSvDocument()
            throws Exception {

        testWhenAllUsersLanguagesIsEnabled(1);
    }

    @Test
    public void getDocument_WhenEnLanguageSetAndUserHasAllDisabledLanguages_Expect_DisabledException() {

        assertThrows(DocumentLanguageDisabledException.class,
                () -> testWhenAllUsersLanguagesIsDisabled(0));
    }

    @Test
    public void getDocument_WhenSvLanguageSetAndUserHasAllDisabledLanguages_Expect_DisabledException() {

        assertThrows(DocumentLanguageDisabledException.class,
                () -> testWhenAllUsersLanguagesIsDisabled(1));
    }

    @Test
    public void getDocument_WhenEnLanguageSetAndUserHasEnabledOnlyEnLanguage_Expect_ReturnedEnDocument()
            throws Exception {

        testWhenAllUsersHaveSpecifiedLanguageCorrespondingToRequested(0);
    }

    @Test
    public void getDocument_WhenSvLanguageSetAndUserHasEnabledOnlySvLanguage_Expect_ReturnedSvDocument()
            throws Exception {

        testWhenAllUsersHaveSpecifiedLanguageCorrespondingToRequested(1);
    }

    @Test
    public void getDocument_whenEnLanguageSetAndUserDoesNotHaveThisLanguage_Expect_ReturnedDocumentInDefaultLanguage()
            throws Exception {

        testWhenUserDoesNotHaveSpecificLanguageAndOptionSHOW_IN_DEFAULT_LANGUAGESet(0);

    }

    @Test
    public void getDocument_whenSvLanguageSetAndUserDoesNotHaveThisLanguage_Expect_ReturnedDocumentInDefaultLanguage()
            throws Exception {

        testWhenUserDoesNotHaveSpecificLanguageAndOptionSHOW_IN_DEFAULT_LANGUAGESet(1);
    }

    @Test
    public void getDocument_whenEnLanguageSetAndSuperAdminDoesNotHaveThisLanguage_Expect_OkStatus() {
        assertTrue(Imcms.getUser().isSuperAdmin());
        assertDoesNotThrow(() -> testWhenUserDoesNotHaveSpecificLanguageAndOptionDO_NOT_SHOWSet(0).andExpect(status().isOk()));
    }

    @Test
    public void getDocument_whenSvLanguageSetAndSuperAdminDoesNotHaveThisLanguage_Expect_OkStatus() {
        assertTrue(Imcms.getUser().isSuperAdmin());
        assertDoesNotThrow(() -> testWhenUserDoesNotHaveSpecificLanguageAndOptionDO_NOT_SHOWSet(1).andExpect(status().isOk()));
    }

    @Test
    public void getDocument_whenEnLanguageSetAndDefaultUserDoesNotHaveThisLanguage_Expect_NotFound404Response() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.USER.getId());
        Imcms.setUser(user);
        assertDoesNotThrow(() -> testWhenUserDoesNotHaveSpecificLanguageAndOptionDO_NOT_SHOWSet(0).andExpect(status().isNotFound()));
    }

    @Test
    public void getDocument_whenSvLanguageSetAndDefaultUserDoesNotHaveThisLanguage_Expect_NotFound404Response() {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
        user.addRoleId(Roles.USER.getId());
        Imcms.setUser(user);
        assertDoesNotThrow(() -> testWhenUserDoesNotHaveSpecificLanguageAndOptionDO_NOT_SHOWSet(1).andExpect(status().isNotFound()));
    }

    private ResultActions testWhenUserDoesNotHaveSpecificLanguageAndOptionDO_NOT_SHOWSet(int languageIndex) throws Exception {

        final LanguageDTO language = languages.get(languageIndex);
        Imcms.setLanguage(language);

        final Integer docId = textDocument.getId();

        final Meta metaDoc = metaRepository.getOne(docId);
        metaDoc.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.DO_NOT_SHOW);
        metaRepository.save(metaDoc);

        final List<CommonContent> commonContents = textDocument.getCommonContents();

        commonContents.stream()
                .filter(commonContent -> commonContent.getLanguage().getCode().equals(language.getCode()))
                .forEach(commonContent -> commonContent.setEnabled(false));

        commonContentService.save(docId, commonContents);
        return mockMvc.perform(get(VIEW_DOC + docId));
    }

    private void testWhenUserDoesNotHaveSpecificLanguageAndOptionSHOW_IN_DEFAULT_LANGUAGESet(int languageIndex)
            throws Exception {

        final String languageIso639_2 = "eng";

        Imcms.getUser().setLanguageIso639_2(languageIso639_2); // default user language

        final LanguageDTO language = languages.get(languageIndex);
        Imcms.setLanguage(language);

        final Integer docId = textDocument.getId();

        final Meta metaDoc = metaRepository.getOne(docId);
        metaDoc.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        metaRepository.save(metaDoc);

        final List<CommonContent> commonContents = textDocument.getCommonContents();

        commonContents.stream()
                .filter(commonContent -> commonContent.getLanguage().getCode().equals(language.getCode()))
                .forEach(commonContent -> commonContent.setEnabled(false));

        commonContentService.save(docId, commonContents);

        mockMvc.perform(get(VIEW_DOC + docId))
                .andExpect(status().is(200))
                .andExpect(model().attribute("language", LanguageMapper.convert639_2to639_1(languageIso639_2)));
    }

    private void testWhenAllUsersLanguagesIsEnabled(int languageIndex)
            throws Exception {

        final LanguageDTO language = languages.get(languageIndex);
        Imcms.setLanguage(language);

        final Integer docId = textDocument.getId();

        mockMvc.perform(get(VIEW_DOC + docId))
                .andExpect(status().is(200))
                .andExpect(model().attribute("language", language.getCode()));
    }

    private void testWhenAllUsersLanguagesIsDisabled(int languageIndex) throws Throwable {
        final LanguageDTO language = languages.get(languageIndex);
        Imcms.setLanguage(language);

        final Integer docId = textDocument.getId();
        final List<CommonContent> commonContents = textDocument.getCommonContents();

        commonContents.forEach(commonContent -> commonContent.setEnabled(false));
        commonContentService.save(docId, commonContents);

        try {
            mockMvc.perform(get(VIEW_DOC + docId));
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    private void testWhenAllUsersHaveSpecifiedLanguageCorrespondingToRequested(int languageIndex)
            throws Exception {

        final LanguageDTO language = languages.get(languageIndex);
        Imcms.setLanguage(language);

        final Integer docId = textDocument.getId();
        final List<CommonContent> commonContents = textDocument.getCommonContents();

        commonContents.stream()
                .filter(commonContent -> !commonContent.getLanguage().getCode().equals(language.getCode()))
                .forEach(commonContent -> commonContent.setEnabled(false));

        commonContentService.save(docId, commonContents);

        mockMvc.perform(get(VIEW_DOC + docId))
                .andExpect(status().is(200))
                .andExpect(model().attribute("language", language.getCode()));
    }
}