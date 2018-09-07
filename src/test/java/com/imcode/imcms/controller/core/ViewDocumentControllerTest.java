package com.imcode.imcms.controller.core;

import com.imcode.imcms.api.DocumentLanguageDisabledException;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static imcode.server.ImcmsConstants.VIEW_DOC_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ViewDocumentControllerTest {

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

    @Before
    public void setUp() {
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

    @Test(expected = DocumentLanguageDisabledException.class)
    public void getDocument_WhenEnLanguageSetAndUserHasAllDisabledLanguages_Expect_DisabledException()
            throws Throwable {

        testWhenAllUsersLanguagesIsDisabled(0);
    }

    @Test(expected = DocumentLanguageDisabledException.class)
    public void getDocument_WhenSvLanguageSetAndUserHasAllDisabledLanguages_Expect_DisabledException()
            throws Throwable {

        testWhenAllUsersLanguagesIsDisabled(1);
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

    @Test(expected = DocumentLanguageDisabledException.class)
    public void getDocument_whenEnLanguageSetAndUserDoesNotHaveThisLanguage_Expect_DisabledException()
            throws Throwable {

        testWhenUserDoesNotHaveSpecificLanguageAndOptionDO_NOT_SHOWSet(0);

    }

    @Test(expected = DocumentLanguageDisabledException.class)
    public void getDocument_whenSvLanguageSetAndUserDoesNotHaveThisLanguage_Expect_DisabledException()
            throws Throwable {

        testWhenUserDoesNotHaveSpecificLanguageAndOptionDO_NOT_SHOWSet(1);
    }

    private void testWhenUserDoesNotHaveSpecificLanguageAndOptionDO_NOT_SHOWSet(int languageIndex)
            throws Throwable {

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

        try {
            mockMvc.perform(get(VIEW_DOC + docId));
        } catch (Exception e) {
            throw e.getCause();
        }
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