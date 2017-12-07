package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.util.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class CommonContentServiceTest {

    private static final int DOC_ID = 1001;
    private static final int VERSION_INDEX = 0;

    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Before
    public void setUp() throws Exception {
        tearDown();
    }

    @After
    public void tearDown() {
        commonContentDataInitializer.cleanRepositories();
    }

    @Test
    public void getOrCreateCommonContent_When_Exist_Expect_CorrectDTO() {
        final List<CommonContentDTO> commonContentDTOS = commonContentDataInitializer.createData(DOC_ID, VERSION_INDEX)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toList());

        for (LanguageDTO languageDTO : languageDataInitializer.createData()) {
            final CommonContentDTO commonContentDTO = commonContentService.getOrCreate(DOC_ID, VERSION_INDEX, languageDTO);
            assertTrue(commonContentDTOS.contains(commonContentDTO));
        }
    }

    @Test
    public void getOrCreateCommonContent_When_NotExist_Expect_CreatedAndCorrectDTO() {
        final int newVersion = 100;
        versionDataInitializer.createData(newVersion, DOC_ID);
        commonContentDataInitializer.createData(DOC_ID, VERSION_INDEX);
        for (LanguageDTO languageDTO : languageDataInitializer.createData()) {
            assertNotNull(commonContentService.getOrCreate(DOC_ID, newVersion, languageDTO));
        }
    }

    @Test
    public void saveCommonContent_When_ExistBefore_Expect_Saved() {
        final List<CommonContentDTO> contents = commonContentDataInitializer.createData(DOC_ID, VERSION_INDEX)
                .stream()
                .map(CommonContentDTO::new)
                .collect(Collectors.toList());

        for (CommonContentDTO content : contents) {
            content.setHeadline("test_content_headline");
            commonContentService.save(content);
        }

        final List<CommonContentDTO> commonContents = new ArrayList<>();

        for (LanguageDTO languageDTO : languageDataInitializer.createData()) {
            commonContents.add(commonContentService.getOrCreate(DOC_ID, VERSION_INDEX, languageDTO));
        }

        assertTrue(contents.containsAll(commonContents));
    }

    @Test
    public void saveCommonContent_When_NotExistBefore_Expect_Saved() {
        versionDataInitializer.createData(VERSION_INDEX, DOC_ID);

        for (LanguageDTO languageDTO : languageDataInitializer.createData()) {
            final CommonContentDTO commonContentDTO = Value.with(new CommonContentDTO(), contentDTO -> {
                contentDTO.setVersionNo(VERSION_INDEX);
                contentDTO.setEnabled(true);
                contentDTO.setMenuImageURL("menu_image_url_test");
                contentDTO.setMenuText("menu_text_test");
                contentDTO.setHeadline("test_headline");
                contentDTO.setDocId(DOC_ID);
                contentDTO.setLanguage(languageDTO);
            });

            commonContentService.save(commonContentDTO);
            final CommonContentDTO savedContent = commonContentService.getOrCreate(DOC_ID, VERSION_INDEX, languageDTO);
            commonContentDTO.setId(savedContent.getId());

            assertEquals(savedContent, commonContentDTO);
        }
    }

    @Test
    public void delete() {
        commonContentDataInitializer.createData(DOC_ID, VERSION_INDEX);
        assertFalse(commonContentService.getOrCreateCommonContents(DOC_ID, VERSION_INDEX).isEmpty());

        commonContentService.deleteByDocId(DOC_ID);
        assertTrue(commonContentService.getCommonContents(DOC_ID, VERSION_INDEX).isEmpty());
    }
}
