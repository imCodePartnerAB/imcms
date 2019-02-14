package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.CommonContentDataInitializer;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CommonContentRepositoryTest extends WebAppSpringTestConfig {

    private static final int DOC_ID = 1001;
    private static final int VERSION_NO = 0;
    private static final String ENG_CODE = "en";
    private static final String SWE_CODE = "sv";

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private CommonContentRepository commonContentRepository;

    @Autowired
    private CommonContentDataInitializer commonContentDataInitializer;

    @BeforeEach
    public void recreateCommonContents() {
        commonContentDataInitializer.cleanRepositories();
        commonContentDataInitializer.createData(DOC_ID, VERSION_NO);
    }

    @Test
    public void testFindByDocId() {
        List<CommonContentJPA> commonContents = commonContentRepository.findByDocIdAndVersionNo(DOC_ID, VERSION_NO);

        assertThat(commonContents.size(), is(2));
    }

    @Test
    public void testFindByDocIdAndLanguage() {
        LanguageJPA se = languageRepository.findByCode(SWE_CODE);
        CommonContentJPA commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguage(DOC_ID, VERSION_NO, se);

        assertNotNull(commonContent);
        assertEquals("headline_se", commonContent.getHeadline());
    }

    @Test
    public void testFindByDocIdAndDocLanguageCode() {
        CommonContentJPA commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(DOC_ID, VERSION_NO, ENG_CODE);

        assertNotNull(commonContent);
        assertEquals("headline_en", commonContent.getHeadline());
    }
}
