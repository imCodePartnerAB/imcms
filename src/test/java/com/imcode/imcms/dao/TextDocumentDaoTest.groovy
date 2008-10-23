package com.imcode.imcms.dao

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*
public class DocumentDaoTest {
	
    static beanName = "contentLoopDaoo"
        
    static dao;
    
    @BeforeClass static void setUpBeforeClass() {
        dao = Context.getBean(beanName)
    }
	
	@AfterClass static void tearDownAfterClass() {
	}
	
	@Before void setUp() {
	}
	
	@After void tearDown() {
	}
	
	@Test void insert() {
		assertTrue(false)
	}
	
    @Test void update() {
        assertTrue(false)
    }
    
    @Test void delete() {
        assertTrue(false)
    }
    
    @Test void find() {
    }           
	
	static final junit.framework.Test suite(){
		return new junit.framework.JUnit4TestAdapter(DocumentDaoTest.class);
	}	
}