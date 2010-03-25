package com.imcode.imcms.dao;

import imcode.server.Imcms;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.imcode.imcms.mapping.DocumentLoader;
import com.imcode.imcms.mapping.DocumentSaver;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Utils {

    //public static final ApplicationContext ctx = new ClassPathXmlApplicationContext("file:src/main/web/WEB-INF/applicationContext.xml");


	// Essential, well known meta id.
	public final static Integer META_ID = 1001;
	
	// Spring initialized beans
	public final static MetaDao metaDao = (MetaDao)getBean("metaDao");
	
	public final static ContentLoopDao contentLoopDao = 
		(ContentLoopDao)getBean("contentLoopDao");
	
	public final static TextDao textDao = (TextDao)getBean("textDao");	
	
	public final static LanguageDao languageDao = (LanguageDao)getBean("languageDao");
	
	public final static DocumentSaver documentSaver = 
		(DocumentSaver)getBean("documentSaver");	
	

    public static Object getBean(String beanName) {
		//return ctx.getBean(beanName);
        return Imcms.getSpringBean(beanName);
	}
}