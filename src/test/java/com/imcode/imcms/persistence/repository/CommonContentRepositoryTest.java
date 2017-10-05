package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import com.imcode.imcms.util.RepositoryTestDataCleaner;
import com.imcode.imcms.util.datainitializer.VersionDataInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
public class CommonContentRepositoryTest {

    private static final int DOC_ID = 1001;
    private static final int VERSION_NO = 0;
    private static final String ENG_CODE = "en";
    private static final String SWE_CODE = "sv";
    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private CommonContentRepository commonContentRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;
    @Autowired
    private VersionRepository versionRepository;
    private RepositoryTestDataCleaner testDataCleaner;

    @PostConstruct
    public void init() {
        testDataCleaner = new RepositoryTestDataCleaner(commonContentRepository, versionRepository);
    }

    @Before
    public void recreateCommonContents() {
        testDataCleaner.cleanRepositories();

        Language en = languageRepository.findByCode(ENG_CODE);
        Language se = languageRepository.findByCode(SWE_CODE);
        // both langs should be already created

        versionDataInitializer.createData(VERSION_NO, DOC_ID);

        commonContentRepository.saveAndFlush(new CommonContent(
                DOC_ID, en, "headline_en", "menuText_en", "menuImageUrl_en", true, VERSION_NO
        ));
        commonContentRepository.saveAndFlush(new CommonContent(
                DOC_ID, se, "headline_se", "menuText_se", "menuImageUrl_se", true, VERSION_NO
        ));
    }

    @Test
    public void testFindByDocId() throws Exception {
        List<CommonContent> commonContents = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, VERSION_NO);

        assertThat(commonContents.size(), is(2));
    }

    @Test
    public void testFindByDocIdAndLanguage() throws Exception {
        Language se = languageRepository.findByCode(SWE_CODE);
        CommonContent commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguage(DOC_ID, VERSION_NO, se);

        assertNotNull(commonContent);
        assertEquals("headline_se", commonContent.getHeadline());
    }

    @Test
    public void testFindByDocIdAndDocLanguageCode() throws Exception {
        CommonContent commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(DOC_ID, VERSION_NO, SWE_CODE);

        assertNotNull(commonContent);
        assertEquals("headline_se", commonContent.getHeadline());
    }
}
