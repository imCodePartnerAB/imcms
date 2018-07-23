package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.TextRepository;
import com.imcode.imcms.util.Value;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.imcode.imcms.model.Text.Type.TEXT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
public class TextControllerTest extends AbstractControllerTest {

    private static final int DOC_ID = 1001;
    private static final int VERSION_NO = 0;
    private static final String ENG_CODE = "en";
    private static final String SWE_CODE = "sv";
    private static final int MIN_TEXT_INDEX = 1;
    private static final int MAX_TEXT_INDEX = 10;

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private TextService textService;

    private List<LanguageJPA> languages;

    @Override
    protected String controllerPath() {
        return "/texts";
    }

    @Before
    public void setUp() throws Exception {
        clearRepos();

        versionDataInitializer.createData(VERSION_NO, DOC_ID);
        languages = Arrays.asList(languageRepository.findByCode(ENG_CODE), languageRepository.findByCode(SWE_CODE));
        // both langs should already be created

        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
    }

    @After
    public void clearRepos() {
        textRepository.deleteAll();
        textRepository.flush();
    }

    @Test
    public void saveText_When_NotInLoop_Expect_CorrectSavedText() throws Exception {
        final List<Text> texts = new ArrayList<>();

        for (LanguageJPA language : languages) {
            final String languageCode = language.getCode();

            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final int finalIndex = index;

                texts.add(Value.with(new TextDTO(), text -> {
                    text.setDocId(DOC_ID);
                    text.setIndex(finalIndex);
                    text.setLangCode(languageCode);
                    text.setType(TEXT);
                    text.setText("test content " + finalIndex);
                }));
            }
        }

        for (Text text : texts) {
            final MockHttpServletRequestBuilder requestBuilder = post(controllerPath())
                    .param("docId", "" + text.getDocId())
                    .param("index", "" + text.getIndex())
                    .param("langCode", text.getLangCode())
                    .param("type", text.getType().name())
                    .param("text", text.getText());

            performRequestBuilderExpectedOk(requestBuilder);

            final Text savedText = textService.getText(text);
            Assert.assertEquals(savedText, text);
        }

    }

    @Test
    public void saveText_When_InLoop_Expect_CorrectSavedText() throws Exception {
        final List<Text> textDTOS = new ArrayList<>();
        final LoopEntryRef loopEntryRef = new LoopEntryRefDTO(1, 1);

        for (LanguageJPA language : languages) {
            final String languageCode = language.getCode();

            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final int finalIndex = index;

                textDTOS.add(Value.with(new TextDTO(), text -> {
                    text.setDocId(DOC_ID);
                    text.setIndex(finalIndex);
                    text.setLoopEntryRef(loopEntryRef);
                    text.setLangCode(languageCode);
                    text.setType(TEXT);
                    text.setText("test content " + finalIndex);
                }));
            }
        }

        for (Text textDTO : textDTOS) {
            final MockHttpServletRequestBuilder requestBuilder = post(controllerPath())
                    .param("docId", "" + textDTO.getDocId())
                    .param("index", "" + textDTO.getIndex())
                    .param("loopEntryRef.loopIndex", "" + textDTO.getLoopEntryRef().getLoopIndex())
                    .param("loopEntryRef.loopEntryIndex", "" + textDTO.getLoopEntryRef().getLoopEntryIndex())
                    .param("langCode", textDTO.getLangCode())
                    .param("type", textDTO.getType().name())
                    .param("text", textDTO.getText());

            performRequestBuilderExpectedOk(requestBuilder);

            final Text savedText = textService.getText(textDTO);
            Assert.assertEquals(savedText, textDTO);
        }

    }

    @Test
    public void saveText_When_UserIsNotAdmin_Expect_CorrectException() throws Exception {
        final UserDomainObject user = new UserDomainObject(2);
        Imcms.setUser(user); // current user is not admin now

        final MockHttpServletRequestBuilder requestBuilder = post(controllerPath())
                .param("docId", "" + DOC_ID)
                .param("index", "1")
                .param("langCode", ENG_CODE)
                .param("type", TEXT.name())
                .param("text", "testestest");

        performRequestBuilderExpectException(NoPermissionToEditDocumentException.class, requestBuilder);
    }
}
