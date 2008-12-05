package com.imcode.imcms.mapping.aop;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.imcode.imcms.api.I18nDisabledException;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nSupport;

@Aspect
public class CachingDocumentGetterAspect {
	
    @Around("execution(* getDocument(Integer))")
    public Object getDocument(ProceedingJoinPoint pjp) throws Throwable {
    	DocumentDomainObject document = (DocumentDomainObject)pjp.proceed();
    	
    	if (document != null) {
    		UserDomainObject user = Imcms.getUser();
    		
    		if (user != null && user.isInViewMode() && !I18nSupport.getCurrentIsDefault()) {            
    			I18nMeta i18nMeta = document.getI18nMeta(I18nSupport.getCurrentLanguage());
            
    			if (!i18nMeta.getEnabled() && !document.getMeta().getShowDisabledI18nDataInDefaultLanguage()) {
    				throw new I18nDisabledException(document, I18nSupport.getCurrentLanguage());
    			}
    		}
    	}
    	
    	return document;
    }
}