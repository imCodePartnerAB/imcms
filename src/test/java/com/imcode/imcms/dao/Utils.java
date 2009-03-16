package com.imcode.imcms.dao;

import org.springframework.context.support.ClassPathXmlApplicationContext;
public class Utils {

	static ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("spring-hibernate.xml");
	
	public static Object getBean(String beanName) {
		return classPathXmlApplicationContext.getBean(beanName);
	}
	
	public static ContentLoopDao getContentLoopDao() {
		return (ContentLoopDao)getBean("contentLoopDao");
	}
}