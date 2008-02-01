package com.imcode.imcms.api;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.imcode.imcms.dao.MetaDao;


public class SpringIntergrationTest {
	
	private static ApplicationContext context;
	
	private MetaDao metaDao;
	
	@BeforeClass
	public static void setUpClass() {
		context = new ClassPathXmlApplicationContext(
		        new String[] {"applicationContext.xml"});		
	}
	
	@Before 
	public void setUp() {
		metaDao = (MetaDao)context.getBean("metaDao");
	}

	@Test @Ignore public void updateMeta() {
		final I18nMetaPart meta = new I18nMetaPart();
		
		//meta.setLanguageId(3);
		meta.setMetaId(1001);
		meta.setHeadline("xyz");
		meta.setMenuText("text");
		meta.setImageURL("img");
		
		Meta m = metaDao.getMeta(1001);
		
		m.getI18nParts().add(meta);
		metaDao.updateMeta(m);
		
		
	}
	
	@Test public void getMeta() {
		Meta meta = metaDao.getMeta(1001);
		
		I18nKeyword kw = new I18nKeyword();
		
		kw.setValue("xa");
		
		meta.getI18nParts().get(0).getKeywords().add(kw);
		
		metaDao.updateMeta(meta);
		
		Assert.assertTrue(meta.getI18nParts().size() == 3);
	}	
}
