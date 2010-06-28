package com.imcode.imcms.dao;

import static org.junit.Assert.*;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.TextHistory;
import com.imcode.imcms.dao.TextDao;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.mapping.orm.Include;
import imcode.server.document.textdocument.ImageDomainObject;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

public class IncludeDaoTest {
	
	MetaDao metaDao;
	
	@BeforeClass
    public static void init() {
        Script.recreateDB();
	}


    @Before
    public void resetDBData() {

        SessionFactory sf = Script.createHibernateSessionFactory(
                new Class[] {Include.class});

        metaDao = new MetaDao();
        metaDao.setSessionFactory(sf);

        Script.runDBScripts("include_dao.sql");
    }
	

    @Test
    public void getIncludes() {
        Collection<Include> includes = metaDao.getIncludes(1001);

        assertEquals(3, includes.size());
    }


    @Test
    public void saveInclude() {
        Include include = new Include();
        include.setMetaId(1002);
        include.setIncludedDocumentId(1001);

        metaDao.saveInclude(include);

        assertNotNull(include.getId());
    }


    @Test
    public void deleteIncludes() {
        int deletedCount = metaDao.deleteIncludes(1001);

        assertEquals(deletedCount, 3);
    }
}