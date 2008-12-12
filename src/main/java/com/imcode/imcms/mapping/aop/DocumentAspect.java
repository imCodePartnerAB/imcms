package com.imcode.imcms.mapping.aop;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nSupport;

// TODO: prototype - refactor
@Aspect
public class DocumentAspect {
	
	/*
	I18nLanguage language = I18nSupport.getCurrentLanguage();
	I18nLanguage defaLanguage = I18nSupport.getDefaultLanguage();

	String value = getMenuText(language);

	return getI18nMeta(language).getEnabled() ? value
			: substituteWithDefault(language, defaLanguage) ? getMenuText(defaLanguage)
					: "";
	*/	

	// TODO: cache?
	// 	 getViewModeHeadline for language l
	// 	 getEditModeHeadline for language l
	
    @Around("execution(* getHeadline())")
    public Object getHeadline(ProceedingJoinPoint pjp) throws Throwable {
    	if (!I18nSupport.isEnabled()) {
    		return "";
    	}
    	
    	UserDomainObject user = Imcms.getUser();
    	
    	if (user == null) {
    		return "";
    	}
    	
    	DocumentDomainObject document = (DocumentDomainObject)pjp.getTarget();
    	I18nLanguage currentLanguage = I18nSupport.getCurrentLanguage();
    	I18nMeta currentI18nMeta = document.getI18nMeta(currentLanguage);
    	
    	if (user.getDocumentShowSettings().isIgnoreI18nShowMode()
    			|| currentI18nMeta.getEnabled()
    			|| I18nSupport.getCurrentIsDefault()) {
    		return document.getHeadline(currentLanguage);
    	}
    	
    	if (!document.getMeta().getShowDisabledI18nDataInDefaultLanguage()) {
    		return "";
    	} else {
        	I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
        	return document.getHeadline(defaultLanguage);
    	}
    }
    
    
    @Around("execution(* getMenuImage())")
    public Object getMenuImage(ProceedingJoinPoint pjp) throws Throwable {
    	if (!I18nSupport.isEnabled()) {
    		return "";
    	}
    	
    	UserDomainObject user = Imcms.getUser();
    	
    	if (user == null) {
    		return "";
    	}
    	
    	DocumentDomainObject document = (DocumentDomainObject)pjp.getTarget();
    	I18nLanguage currentLanguage = I18nSupport.getCurrentLanguage();
    	I18nMeta currentI18nMeta = document.getI18nMeta(currentLanguage);
    	
    	if (user.getDocumentShowSettings().isIgnoreI18nShowMode()
    			|| currentI18nMeta.getEnabled()
    			|| I18nSupport.getCurrentIsDefault()) {
    		return document.getMenuImage(currentLanguage);    	
    	}
    	
    	if (!document.getMeta().getShowDisabledI18nDataInDefaultLanguage()) {
    		return "";
    	} else {
        	I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
        	return document.getMenuImage(defaultLanguage);
    	}
    }
    
    
    @Around("execution(* getMenuText())")
    public Object getMenuText(ProceedingJoinPoint pjp) throws Throwable {
    	if (!I18nSupport.isEnabled()) {
    		return "";
    	}
    	
    	UserDomainObject user = Imcms.getUser();
    	
    	if (user == null) {
    		return "";
    	}
    	
    	DocumentDomainObject document = (DocumentDomainObject)pjp.getTarget();
    	I18nLanguage currentLanguage = I18nSupport.getCurrentLanguage();
    	I18nMeta currentI18nMeta = document.getI18nMeta(currentLanguage);
    	
    	if (user.getDocumentShowSettings().isIgnoreI18nShowMode()
    			|| currentI18nMeta.getEnabled()
    			|| I18nSupport.getCurrentIsDefault()) {
    		return document.getMenuText(currentLanguage);    	
    	}
    	
    	/// ????????????????????????? ///
    	if (!document.getMeta().getShowDisabledI18nDataInDefaultLanguage()) {
    		return "";
    	} else {
        	I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage();
        	return document.getMenuText(defaultLanguage);
    	}
    }	    
}
