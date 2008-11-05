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
        def meta = dao.getMeta(1001)
        
        assertNotNull(meta)
        
        meta.getProperties().each {k, v ->
        	println "property: ${k}=${v}" 
        }
        
        
        
        meta.roleRights.each {k, v ->
        	println "Role to set: ${k}=${v}"        	
        }
        
        meta.permissionSetBits.each {k, v ->
        	println "Perm set it to bits: ${k}=${v}"  
        }

        meta.docPermisionSetEx.each {
        	println it.dump()
        }
        
        meta.docPermisionSetExForNew.each {
        	println it.dump()
        }
    }
}