package com.imcode.imcms.mapping.aop;

import imcode.server.document.textdocument.TextDocumentDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.imcode.imcms.api.I18nLanguage;

/**
 * Prototype implementation for showing translated document's fields
 * in a chosen language.
 */
@Aspect
public class TextDocumentAspect {

	private I18nLanguage language;
	
	public TextDocumentAspect(I18nLanguage language) {
		this.language = language;
	}
	
	@Around("execution(* getText(int))")
    public Object getText(ProceedingJoinPoint pjp) throws Throwable {
		return ((TextDocumentDomainObject)pjp.getTarget())
			.getText((Integer)pjp.getArgs()[0]);
    }
    
    
    @Around("execution(* getImage(int))")
    public Object getImage(ProceedingJoinPoint pjp) throws Throwable {
    	return ((TextDocumentDomainObject)pjp.getTarget())
    		.getImage((Integer)pjp.getArgs()[0]);
    }  
}
