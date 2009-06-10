package com.imcode.imcms.dao

import org.dbunit.dataset.xml.FlatXmlDataSetimport org.testng.annotations.BeforeClassimport org.testng.annotations.Testimport org.testng.annotations.BeforeTest
import static org.testng.Assert.*import imcode.server.document.textdocument.TextDomainObjectimport org.testng.Assertimport imcode.server.document.textdocument.MenuDomainObjectimport imcode.server.document.textdocument.MenuItemDomainObjectimport imcode.server.document.textdocument.TreeSortKeyDomainObjectimport com.imcode.imcms.api.DocumentVersionTag
public class MetaDaoTest extends DaoTest {
	
	MetaDao dao;
	
	@BeforeClass void init() {
		dao = Context.getBean("metaDao")
	}		
		
	@Override
	def getDataSetFileName() {
		"dbunit-meta-data.xml"
	}
	
	
	@Test void getDocumentVersions() {
		def versions = dao.getDocumentVersions(1001);
		
		assertTrue(versions.size() > 0)
	}

    @Test void getPublishedMeta() {
        def meta = dao.getMeta(1001, DocumentVersionTag.PUBLISHED)
        
        assertNotNull(meta)        
    }
    
    @Test void getWorkingMeta() {
        def meta = dao.getMeta(1001, DocumentVersionTag.WORKING)
        
        assertNotNull(meta)        
    }    

    @Test void getMetaById() {
        def meta = dao.getMeta(1001L)
        
        assertNotNull(meta)        
    }    

    
    @Test void getMetaByVersion() {
        def meta = dao.getMeta(1001, 1)
        
        assertNotNull(meta)        
    }    
}