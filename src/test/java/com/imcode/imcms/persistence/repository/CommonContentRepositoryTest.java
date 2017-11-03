package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import com.imcode.imcms.persistence.entity.Language;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
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
    private CommonContentDataInitializer commonContentDataInitializer;

    @Before
    public void recreateCommonContents() {
        commonContentDataInitializer.cleanRepositories();
        commonContentDataInitializer.createData(DOC_ID, VERSION_NO);
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
        CommonContent commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(DOC_ID, VERSION_NO, ENG_CODE);

        assertNotNull(commonContent);
        assertEquals("headline_en", commonContent.getHeadline());
    }
}
