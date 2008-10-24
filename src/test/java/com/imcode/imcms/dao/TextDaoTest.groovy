package com.imcode.imcms.dao

import org.dbunit.dataset.xml.FlatXmlDataSetimport org.testng.annotations.BeforeClassimport org.testng.annotations.Testimport org.testng.annotations.BeforeTest
import org.testng.Assertimport com.imcode.imcms.dao.TextDao
import com.imcode.imcms.dao.LanguageDao

import static org.junit.Assert.*

//todo: Test named queries
public class TextDaoTest extends DaoTest {
	
	TextDao textDao;
	
	LanguageDao languageDao;
		
	@BeforeClass void init() {
		textDao = Context.getBean("textDao")
		languageDao = Context.getBean("languageDao")
	}		
		
	@Override
	def getDataSetFileName() {
		"dbunit-texts-data.xml"
	}
		
	
	@Test void getExistingText() {
		def text = textDao.getText(1001, 1, 1)
		
		Assert.assertNotNull(text)
	}
	
    @Test void getTexts() {
        assertTrue(false)
    }	
	
	@Test void saveText() {
		assertTrue(false)
	}
}