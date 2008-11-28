package com.imcode.imcms.dao

import org.dbunit.dataset.xml.FlatXmlDataSetimport org.testng.annotations.BeforeClassimport org.testng.annotations.Testimport org.testng.annotations.BeforeTest
import static org.testng.Assert.*import imcode.server.document.textdocument.TextDomainObjectimport org.testng.Assertimport com.imcode.imcms.dao.TextDao
import com.imcode.imcms.dao.LanguageDao

//todo: Test named queries
public class TemplateNamesDaoTest extends DaoTest {
	
	MetaDao dao;
	
	@BeforeClass void init() {
		dao = Context.getBean("metaDao")
	}		
		
	@Override
	def getDataSetFileName() {
		"dbunit-text_docs-data.xml"
	}
	
	
    @Test void getNonExistingTemplateNames() {
        def tns = dao.getTemplateNames(10001)
        
        assertNull(tns)
    }
    
    @Test void getTemplateNames() {
        def tns = dao.getTemplateNames(1001)
        
        assertNotNull(tns)
    }   
    
    /*
    @Test void updateTemplateNames() {
        def tns = dao.getTemplateNames(1001)        
        assertNotNull(tns)
        
        tns.setTemplateName("UPDATED");
        dao.saveTemplateNames(tns);
        
        tns = dao.getTemplateNames(1001)
        Assert.assertEquals("UPDATED", tns.getTemplateName());
    }   
    
    @Test void deleteTemplateNames() {
        def tns = dao.getTemplateNames(1001)        
        assertNotNull(tns)
        
        dao.deleteTemplateNames(tns);
        
        tns = dao.getTemplateNames(1001)
        Assert.assertNull(tns);
    } 
    
    
    @Test void insertTemplateNames() {
        def tns = dao.getTemplateNames(1001)        
        assertNotNull(tns)
        
        dao.deleteTemplateNames(tns);        
        def deletedTns = dao.getTemplateNames(1001)
        Assert.assertNull(deletedTns);
        
        dao.saveTemplateNames(tns);
        tns = dao.getTemplateNames(1001)        
        assertNotNull(tns)        
    }  
    */  
}