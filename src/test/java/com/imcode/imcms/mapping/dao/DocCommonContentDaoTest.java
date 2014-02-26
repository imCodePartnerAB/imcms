package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocCommonContent;
import com.imcode.imcms.mapping.orm.DocLanguage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@Transactional
public class DocCommonContentDaoTest {

    @Inject
    DocLanguageDao docLanguageDao;

    @Inject
    DocCommonContentDao docCommonContentDao;

    public List<DocCommonContent> recreateCommonContents() {
        docCommonContentDao.deleteAll();
        docLanguageDao.deleteAll();

        DocLanguage en = docLanguageDao.saveAndFlush(new DocLanguage("en", "English", "English"));
        DocLanguage se = docLanguageDao.saveAndFlush(new DocLanguage("se", "Swedish", "Svenska"));

        return Arrays.asList(
            docCommonContentDao.saveAndFlush(
                    new DocCommonContent(1001, en, "headline_en", "menuText_en", "menuImageUrl_en")
            ),

            docCommonContentDao.saveAndFlush(
                    new DocCommonContent(1001, se, "headline_se", "menuText_se", "menuImageUrl_se")
            )

        );
    }

    @Test
    public void testFindByDocId() throws Exception {
        recreateCommonContents();

        List<DocCommonContent> docCommonContents = docCommonContentDao.findByDocId(1001);

        assertThat(docCommonContents.size(), is(2));
    }

    @Test
    public void testFindByDocIdAndLanguage() throws Exception {
        recreateCommonContents();

        DocLanguage se = docLanguageDao.findByCode("se");
        DocCommonContent docCommonContent = docCommonContentDao.findByDocIdAndDocLanguage(1001, se);

        assertNotNull(docCommonContent);
        assertEquals("headline_se", docCommonContent.getHeadline());
    }

    @Test
    public void testFindByDocIdAndDocLanguageCode() throws Exception {
        recreateCommonContents();

        DocCommonContent docCommonContent = docCommonContentDao.findByDocIdAndDocLanguageCode(1001, "se");

        assertNotNull(docCommonContent);
        assertEquals("headline_se", docCommonContent.getHeadline());
    }
}
