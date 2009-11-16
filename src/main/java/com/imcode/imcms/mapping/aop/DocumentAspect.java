package com.imcode.imcms.mapping.aop;

import imcode.server.document.DocumentDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.imcode.imcms.api.I18nLanguage;

/**
 * Prototype implementation for showing translated document's fields
 * in a chosen language.
 */
@Aspect
public class DocumentAspect {

    /*
	private I18nLanguage language;
	
	public DocumentAspect(I18nLanguage language) {
		this.language = language;
	}
			
    @Around("execution(* getHeadline())")
    public Object getHeadline(ProceedingJoinPoint pjp) throws Throwable {
    	return ((DocumentDomainObject)pjp.getTarget())
    		.getHeadline(language);
    }
    
    
    @Around("execution(* getMenuImage())")
    public Object getMenuImage(ProceedingJoinPoint pjp) throws Throwable {
    	return ((DocumentDomainObject)pjp.getTarget())
    		.getMenuImage(language);
    }
    
    
    @Around("execution(* getMenuText())")
    public Object getMenuText(ProceedingJoinPoint pjp) throws Throwable {
    	return ((DocumentDomainObject)pjp.getTarget())
    		.getMenuText(language);
    }
    	   */
}