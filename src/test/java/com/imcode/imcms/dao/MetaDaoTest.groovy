package com.imcode.imcms.dao

import org.dbunit.dataset.xml.FlatXmlDataSetimport org.testng.annotations.BeforeClassimport org.testng.annotations.Testimport org.testng.annotations.BeforeTest
import static org.testng.Assert.*import imcode.server.document.textdocument.TextDomainObjectimport org.testng.Assertimport com.imcode.imcms.api.Includeimport imcode.server.document.textdocument.MenuDomainObjectimport imcode.server.document.textdocument.MenuItemDomainObjectimport imcode.server.document.textdocument.TreeSortKeyDomainObject
//todo: Test named queries
public class MetaDaoTest extends DaoTest {
	
	MetaDao dao;
	
	@BeforeClass void init() {
		dao = Context.getBean("metaDao")
	}		
		
	@Override
	def getDataSetFileName() {
		"dbunit-meta-data.xml"
	}
	

    @Test void getMeta() {
        def menu = dao.getMeta(1001)
        
        assertNotNull(menu)
        
        menu.getProperties().each {k, v ->
        	println "property: ${k}=${v}" 
        }
    }
}