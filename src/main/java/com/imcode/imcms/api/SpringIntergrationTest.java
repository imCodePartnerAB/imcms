package com.imcode.imcms.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
		final I18nMeta meta = new I18nMeta();
		
		//meta.setLanguageId(3);
		meta.setMetaId(1001);
		meta.setHeadline("xyz");
		meta.setMenuText("text");
		meta.setMenuImageURL("img");
		
		Meta m = metaDao.getMeta(1001);
		
		m.getI18nMetas().add(meta);
		metaDao.updateMeta(m);
		
		
	}
	
	@Test public void getMeta() {
		Meta meta = metaDao.getMeta(1001);
		
		Keyword kw = new Keyword();
		
		kw.setValue("xa");
		
		meta.getI18nMetas().get(0).getKeywords().add(kw);
		
		metaDao.updateMeta(meta);
		
		Assert.assertTrue(meta.getI18nMetas().size() == 3);
	}	
}
