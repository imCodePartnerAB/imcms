package com.imcode.imcms.dao;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.imcode.imcms.mapping.DatabaseDocumentGetter;
import com.imcode.imcms.mapping.DocumentSaver;
public class Utils {

	public static final ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("spring-hibernate.xml");
	
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
	
	public final static DatabaseDocumentGetter databaseDocumentGetter = 
		(DatabaseDocumentGetter)getBean("databaseDocumentGetter");		
	
	public static Object getBean(String beanName) {
		return classPathXmlApplicationContext.getBean(beanName);
	}
}