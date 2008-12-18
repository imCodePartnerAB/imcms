package com.imcode.imcms.mapping.aop;

import imcode.server.document.textdocument.TextDocumentDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.imcode.imcms.api.I18nSupport;

@Aspect
public class TextDocumentAspect {

	@Around("execution(* getText(int))")
    public Object getText(ProceedingJoinPoint pjp) throws Throwable {
		return ((TextDocumentDomainObject)pjp.getTarget())
			.getText(I18nSupport.getDocumentLanguage(), (Integer)pjp.getArgs()[0]);
    }
    
    
    @Around("execution(* getImage(int))")
    public Object getImage(ProceedingJoinPoint pjp) throws Throwable {
    	return ((TextDocumentDomainObject)pjp.getTarget())
    		.getImage(I18nSupport.getDocumentLanguage(), (Integer)pjp.getArgs()[0]);
    }  
}
