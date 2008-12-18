package com.imcode.imcms.mapping.aop;

import imcode.server.document.DocumentDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.imcode.imcms.api.I18nSupport;

@Aspect
public class DocumentAspect {
			
    @Around("execution(* getHeadline())")
    public Object getHeadline(ProceedingJoinPoint pjp) throws Throwable {
    	return ((DocumentDomainObject)pjp.getTarget())
    		.getHeadline(I18nSupport.getDocumentLanguage());
    }
    
    
    @Around("execution(* getMenuImage())")
    public Object getMenuImage(ProceedingJoinPoint pjp) throws Throwable {
    	return ((DocumentDomainObject)pjp.getTarget())
    		.getMenuImage(I18nSupport.getDocumentLanguage());
    }
    
    
    @Around("execution(* getMenuText())")
    public Object getMenuText(ProceedingJoinPoint pjp) throws Throwable {
    	return ((DocumentDomainObject)pjp.getTarget())
    		.getMenuText(I18nSupport.getDocumentLanguage());
    }	   
}
