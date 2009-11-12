package com.imcode.imcms.dao

import org.dbunit.dataset.xml.FlatXmlDataSet
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import org.testng.annotations.BeforeTest
import static org.testng.Assert.*
import imcode.server.document.textdocument.TextDomainObject
import org.testng.Assert

import com.imcode.imcms.dao.TextDao
import com.imcode.imcms.dao.LanguageDao

//todo: Test named queries
public class IncludeDaoTest extends DaoTestG {
	
	MetaDao dao;
	
	@BeforeClass void init() {
		dao = Context.getBean("metaDao")
	}		
		
	@Override
	def getDataSetFileName() {
		"dbunit-includes-data.xml"
	}
	

	/*
    @Test void getNonExistingInclude() {
        def include = dao.getDocumentInclude(10001, 1)
        
        assertNull(include)
    }
    /*
    
    /*
    @Test void deleteDocumentInclude() {
        def includes = dao.getDocumentIncludes(1001)
        
        assertTrue(includes.size() == 4)
        
        def rowCount = dao.deleteDocumentIncludes(1001)
        
        assertTrue(rowCount == 4)
        
        includes = includeDao.getDocumentIncludes(1001)
        
        assertTrue(includes.size() == 0)
    }    
    
    
    @Test void insertInclude() {
        def include = includeDao.getDocumentInclude(1001, 1000)
        
        assertNull(include)
        
        include = new Include()
        
        include.setDocId 1001
        include.setIndex 1000
        include.setIncludedMetaId 1001        
        
        includeDao.saveInclude(include);
        
        include = includeDao.getDocumentInclude(1001, 1000)
        assertNotNull(include)
    }
    */
    
    /*
    @Test void getExistingInclude() {
        def include = dao.getDocumentInclude(1001, 1)
        
        assertNotNull(include)
        assertTrue(include.includedMetaId == 1001)
    }
    */
	
	@Test void getExistingIncludes() {
		def includes = dao.getIncludes(1001)
		
		assertTrue(includes.size() == 4)
	} 
}