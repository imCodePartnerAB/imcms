package com.imcode.imcms.dao

import org.dbunit.dataset.xml.FlatXmlDataSetimport org.testng.annotations.BeforeClassimport org.testng.annotations.Testimport org.testng.annotations.BeforeTest
import static org.testng.Assert.*import imcode.server.document.textdocument.TextDomainObjectimport org.testng.Assertimport imcode.server.document.textdocument.ImagesPathRelativePathImageSourceimport com.imcode.imcms.dao.TextDao
import com.imcode.imcms.dao.LanguageDao

//todo: Test named queries
public class ImageDaoTest extends DaoTest {
	
	ImageDao imageDao	
	LanguageDao languageDao
		
	@BeforeClass void init() {
		imageDao = Context.getBean("imageDao")
		languageDao = Context.getBean("languageDao")
	}		
		
	@Override
	def getDataSetFileName() {
		"dbunit-images-data.xml"
	}
		
	/*
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
    	    	
    	textDao.insertOrUpdateText(textDO)
    	
    	textDO = textDao.getText(1001, 1, 1)
    	
        assertTrue(newText.equals(textDO.text))
    }
    */
    
    @Test void getDocumentImagesByLanguage() {
    	def language = languageDao.defaultLanguage  	
        def images = imageDao.getDocumentImagesByLanguage(1001, language.id)
        
        assertTrue(images.size() == 3)
        
        images.each {
    		assertTrue it.language.equals(language) 
    	}
    } 
    
    @Test void getDocumentImagesByIndexWithCreate() {
    	def metaId = 1001
        def index = 10
        def images = imageDao.getDocumentImagesByIndex(metaId, index, true)
        
        assertTrue(images.size() == 2)
    } 
    
    
    @Test void insertDocumentImages() {
        def metaId = 1001
        def index = 10
        def images = imageDao.getDocumentImagesByIndex(metaId, index, true)
        
        assertTrue(images.size() == 2)
        
        String url = "file:///fake.gif"
        def imageSource = new ImagesPathRelativePathImageSource(url)
        
        images.each {
        	assertTrue(it.type == null)
        	
            it.imageUrl = url        	
        	it.source = imageSource
        	it.type = imageSource.typeId
        }
        
        images = imageDao.saveDocumentImages(metaId, images)
        
        images = imageDao.getDocumentImagesByIndex(metaId, index, false)
        
        assertTrue(images.size() == 2)
        
        images.each {
            assertTrue it.metaId == metaId 
        }
    }    

    /*
    @Test void getDocumentImagesByIndexNoCreate() {
        def index = 1
        def images = imageDao.getDocumentImagesByIndex(1001, index, false)
        
        assertTrue(images.size() == 2)
        
    
        //images.each {
        //    assertTrue it.language.equals(language) 
        //}
        
    } 
    */	
    
    /*
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
				
		textDao.insertText(text)
		
		text = textDao.getText(metaId, textIndex, language.id)
		
		assertNotNull(text)		
	}
	*/
	
    @Test void getImages() {
        def metaId = 1001
        def images = imageDao.getImages(metaId)
        
        assertTrue(images.size() > 0)
    }	
}