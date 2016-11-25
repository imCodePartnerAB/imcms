package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.JpaConfiguration;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@Transactional
public class CommonContentRepositoryTest {

    @Inject
    LanguageRepository languageRepository;

    @Inject
    CommonContentRepository commonContentRepository;

    public List<CommonContent> recreateCommonContents() {
        commonContentRepository.deleteAll();
        languageRepository.deleteAll();

        Language en = languageRepository.saveAndFlush(new Language("en", "English", "English"));
        Language se = languageRepository.saveAndFlush(new Language("se", "Swedish", "Svenska"));

        return Arrays.asList(
            commonContentRepository.saveAndFlush(
                    new CommonContent(1001, en, "headline_en", "menuText_en", "menuImageUrl_en")
            ),

            commonContentRepository.saveAndFlush(
                    new CommonContent(1001, se, "headline_se", "menuText_se", "menuImageUrl_se")
            )

        );
    }

    @Test
    public void testFindByDocId() throws Exception {
        recreateCommonContents();

        List<CommonContent> commonContents = commonContentRepository.findByDocIdAndVersionNo(1001, 0);

        assertThat(commonContents.size(), is(2));
    }

    @Test
    public void testFindByDocIdAndLanguage() throws Exception {
        recreateCommonContents();

        Language se = languageRepository.findByCode("se");
        CommonContent commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguage(1001, 0, se);

        assertNotNull(commonContent);
        assertEquals("headline_se", commonContent.getHeadline());
    }

    @Test
    public void testFindByDocIdAndDocLanguageCode() throws Exception {
        recreateCommonContents();

        CommonContent commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(1001, 0, "se");

        assertNotNull(commonContent);
        assertEquals("headline_se", commonContent.getHeadline());
    }
}
