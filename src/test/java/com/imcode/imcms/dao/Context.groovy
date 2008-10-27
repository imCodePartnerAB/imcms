package com.imcode.imcms.dao

import org.springframework.context.support.ClassPathXmlApplicationContext
public class Context {

	static context = new ClassPathXmlApplicationContext("spring-hibernate.xml")
	
	static getBean(beanName) {
		context.getBean(beanName)
	}
}