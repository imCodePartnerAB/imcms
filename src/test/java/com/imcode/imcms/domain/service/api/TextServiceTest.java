package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.Text;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import com.imcode.imcms.persistence.repository.TextRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.imcode.imcms.persistence.entity.TextBase.Type.PLAIN_TEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TextServiceTest {

    private static final int DOC_ID = 1001;
    private static final int VERSION_NO = 0;
    private static final String ENG_CODE = "en";
    private static final String SWE_CODE = "sv";
    private static final int MIN_TEXT_INDEX = 1;
    private static final int MAX_TEXT_INDEX = 10;

    @Autowired
    private TextService textService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private Function<Text, TextDTO> textToTextDTO;

    private Version version;
    private List<Language> languages;

    @Before
    public void setUp() throws Exception {
        clearRepos();

        version = versionDataInitializer.createData(VERSION_NO, DOC_ID);
        languages = Arrays.asList(languageRepository.findByCode(ENG_CODE), languageRepository.findByCode(SWE_CODE));
        // both langs should already be created
    }

    @After
    public void clearRepos() {
        textRepository.deleteAll();
        textRepository.flush();
    }

    @Test
    public void testTextServiceInjected() {
        assertNotNull(textService);
    }

    @Test
    public void getText_When_NotInLoop_Expect_CorrectDTO() {
        final List<TextDTO> textDTOS = new ArrayList<>();

        for (Language language : languages) {
            for (int index = MIN_TEXT_INDEX; index <= MAX_TEXT_INDEX; index++) {
                final Text text = new Text();
                text.setIndex(index);
                text.setLanguage(language);
                text.setText("test");
                text.setType(PLAIN_TEXT);
                text.setVersion(version);

                textRepository.save(text);
                textDTOS.add(textToTextDTO.apply(text));
            }
        }

        for (TextDTO textDTO : textDTOS) {
            final TextDTO savedText = textService.getText(textDTO);
            assertEquals(savedText, textDTO);
        }
    }

}
