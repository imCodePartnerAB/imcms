package com.imcode.imcms.dao;

import static org.junit.Assert.*;

import com.imcode.imcms.Script;
import imcode.server.document.textdocument.TextDomainObject;

import com.imcode.imcms.dao.TextDao;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TextDaoTest {
	
	static TextDao textDao;
	
	static MetaDao metaDao;
	
	static LanguageDao languageDao;
		
	@BeforeClass
    public static void init() {
        Script.recreateDB();
	}		

    @Before
    public void resetDBData() {
        Script.runDBScripts("text_dao.sql");

        SessionFactory sf = Script.createHibernateSessionFactory(
                new Class[] {TextDomainObject.class},
                "src/main/resources/Text.hbm.xml");

        textDao = new TextDao();
        textDao.setSessionFactory(sf);
    }	
}