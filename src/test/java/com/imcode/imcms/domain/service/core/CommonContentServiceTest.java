package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.persistence.entity.CommonContent;
import com.imcode.imcms.util.Value;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class CommonContentServiceTest {

    private static final int DOC_ID = 1001;
    private static final int VERSION_INDEX = 0;

    private UserDomainObject user;

    @Autowired
    private CommonContentService commonContentService;

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @Autowired
    private Function<CommonContent, CommonContentDTO> commonContentToDto;

    @Before
    public void setUp() throws Exception {
        tearDown();

        user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng");
    }

    @After
    public void tearDown() throws Exception {
        commonContentDataInitializer.cleanRepositories();
    }

    @Test
    public void getOrCreateCommonContent_When_Exist_Expect_CorrectDTO() {
        final List<CommonContentDTO> commonContentDTOS = commonContentDataInitializer.createData(DOC_ID, VERSION_INDEX)
                .stream()
                .map(commonContentToDto)
                .collect(Collectors.toList());

        final CommonContentDTO commonContentDTO = commonContentService.getOrCreate(DOC_ID, VERSION_INDEX, user);

        assertTrue(commonContentDTOS.contains(commonContentDTO));
    }

    @Test
    public void getOrCreateCommonContent_When_NotExist_Expect_CreatedAndCorrectDTO() {
        commonContentDataInitializer.createData(DOC_ID, VERSION_INDEX);
        assertNotNull(commonContentService.getOrCreate(DOC_ID, 100, user));
    }

    @Test
    public void saveCommonContent_When_ExistBefore_Expect_Saved() {
        final List<CommonContentDTO> contents = commonContentDataInitializer.createData(DOC_ID, VERSION_INDEX)
                .stream()
                .map(commonContentToDto)
                .collect(Collectors.toList());

        for (CommonContentDTO content : contents) {
            content.setHeadline("test_content_headline");
            commonContentService.save(content);
        }

        user.setLanguageIso639_2("eng");
        final CommonContentDTO engContent = commonContentService.getOrCreate(DOC_ID, VERSION_INDEX, user);

        user.setLanguageIso639_2("swe");
        final CommonContentDTO sweContent = commonContentService.getOrCreate(DOC_ID, VERSION_INDEX, user);

        assertTrue(contents.containsAll(Arrays.asList(engContent, sweContent)));
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
            user.setLanguageIso639_2(LanguageMapper.convert639_1to639_2(languageDTO.getCode()));
            final CommonContentDTO savedContent = commonContentService.getOrCreate(DOC_ID, VERSION_INDEX, user);
            commonContentDTO.setId(savedContent.getId());

            assertEquals(savedContent, commonContentDTO);
        }
    }

}
