package com.imcode.imcms.dao;

import static org.junit.Assert.*;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.TextHistory;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.mapping.orm.Include;
import com.imcode.imcms.mapping.orm.TemplateNames;
import imcode.server.document.textdocument.ImageDomainObject;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

//todo: Test named queries
public class TemplateNamesDaoTest {
		
	MetaDao metaDao;

	@BeforeClass
    public static void init() {
        Script.recreateDB();
	}


    @Before
    public void resetDBData() {

        SessionFactory sf = Script.createHibernateSessionFactory(
                new Class[] {TemplateNames.class});

        metaDao = new MetaDao();
        metaDao.setSessionFactory(sf);

        Script.runDBScripts("template_names_dao.sql");
    }
	
	
    @Test public void getNonExistingTemplateNames() {
        TemplateNames tns = metaDao.getTemplateNames(10001);
        
        assertNull(tns);
    }
    
    @Test public void getTemplateNames() {
        TemplateNames tns = metaDao.getTemplateNames(1001);
        
        assertNotNull(tns);
    }   
    
    @Test public void updateTemplateNames() {
        TemplateNames tns = metaDao.getTemplateNames(1001);
        assertNotNull(tns);
        
        tns.setTemplateName("UPDATED");
        metaDao.saveTemplateNames(tns);
        
        tns = metaDao.getTemplateNames(1001);
        assertEquals("UPDATED", tns.getTemplateName());
    }   
    
    @Test public void deleteTemplateNames() {
        TemplateNames tns = metaDao.getTemplateNames(1001);
        assertNotNull(tns);
        
        metaDao.deleteTemplateNames(1001);
        
        tns = metaDao.getTemplateNames(1001);
        assertNull(tns);
    } 
    
    
    @Test public void insertTemplateNames() {
        TemplateNames tns = metaDao.getTemplateNames(1001);
        assertNotNull(tns);
        
        metaDao.deleteTemplateNames(1001);        
        TemplateNames tns2 = metaDao.getTemplateNames(1001);
        Assert.assertNull(tns2);
        
        metaDao.saveTemplateNames(tns);
        tns = metaDao.getTemplateNames(1001);       
        assertNotNull(tns);        
    }
}