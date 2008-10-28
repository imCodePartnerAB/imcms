package com.imcode.imcms.dao

import org.dbunit.dataset.xml.FlatXmlDataSetimport org.testng.annotations.BeforeClassimport org.testng.annotations.Testimport org.testng.annotations.BeforeTest
import static org.testng.Assert.*import imcode.server.document.textdocument.TextDomainObjectimport org.testng.Assertimport com.imcode.imcms.dao.TextDao
import com.imcode.imcms.dao.LanguageDao

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

	
    @Test void getNonExistingText() {
        def text = textDao.getText(10001, 1, 1)
        
        assertNull(text, "Text does not exists")
    }
	
	
	
    @Test void updateText() {
    	def textDO = textDao.getText(1001, 1, 1)    	
        String newText = "new text"    	
    	    	
        assertFalse(newText.equals(textDO.text))
        
    	textDO.text = newText
    	    	
    	textDao.saveText(textDO)
    	
    	textDO = textDao.getText(1001, 1, 1)
    	
        assertTrue(newText.equals(textDO.text))
    }
    
    
    @Test void getTextsByLanguage() {
    	def language = languageDao.defaultLanguage  	
        def texts = textDao.getTexts(1001, language.id)
        
        assertTrue(texts.size() == 2)
        
        texts.each {
    		assertTrue it.language.equals(language) 
    	}
    }    
	
    
	@Test void insertText() {
		def language = languageDao.defaultLanguage
		def metaId = 1001
		def textIndex = 1000
		
		def text = textDao.getText(metaId, textIndex, language.id)
		 		
		assertNull(text)
		
		text = new TextDomainObject()
		text.setMetaId(metaId)
		text.setIndex(textIndex)
		text.setLanguage(language)
				
		textDao.saveText(text)
		
		text = textDao.getText(metaId, textIndex, language.id)
		
		assertNotNull(text)		
	}
}