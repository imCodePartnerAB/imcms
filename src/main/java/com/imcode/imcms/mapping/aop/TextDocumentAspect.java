package com.imcode.imcms.mapping.aop;

import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nSupport;

@Aspect
public class TextDocumentAspect {

    @Around("execution(* getText(int))")
    public Object getText(ProceedingJoinPoint pjp) throws Throwable {
    	if (!I18nSupport.isEnabled()) {
    		throw new IllegalStateException("I18nSupport is disabled");
    	}
    	
    	UserDomainObject user = Imcms.getUser();
    	
    	if (user == null) {
    		throw new NullPointerException("User is not specified.");
    	}
    	
    	TextDocumentDomainObject document = (TextDocumentDomainObject)pjp.getTarget();
    	I18nLanguage currentLanguage = I18nSupport.getCurrentLanguage();
    	I18nMeta currentI18nMeta = document.getI18nMeta(currentLanguage);
    	
    	if (user.getDocumentShowSettings().isIgnoreI18nShowMode()
    			|| currentI18nMeta.getEnabled()
    			|| I18nSupport.getCurrentIsDefault()) {
    		return pjp.proceed();
    	}
    	
    	if (!document.getMeta().isShowDisabledI18nContentInDefaultLanguage()) {
    		return null;
    	} else {
        	I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
        	return document.getText(defaultLanguage, (Integer)pjp.getArgs()[0]);
    	}
    }
    
    
    @Around("execution(* getImage(int))")
    public Object getImage(ProceedingJoinPoint pjp) throws Throwable {
    	if (!I18nSupport.isEnabled()) {
    		throw new IllegalStateException("I18nSupport is disabled");
    	}
    	
    	UserDomainObject user = Imcms.getUser();
    	
    	if (user == null) {
    		throw new NullPointerException("User is not specified.");
    	}
    	
    	TextDocumentDomainObject document = (TextDocumentDomainObject)pjp.getTarget();
    	I18nLanguage currentLanguage = I18nSupport.getCurrentLanguage();
    	I18nMeta currentI18nMeta = document.getI18nMeta(currentLanguage);
    	
    	if (user.getDocumentShowSettings().isIgnoreI18nShowMode()
    			|| currentI18nMeta.getEnabled()
    			|| I18nSupport.getCurrentIsDefault()) {
    		return pjp.proceed();
    	}
    	
    	if (!document.getMeta().getShowDisabledI18nDataInDefaultLanguage()) {
    		return null;
    	} else {
        	I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
        	return document.getImage(defaultLanguage, (Integer)pjp.getArgs()[0]);
    	}
    }    
}
