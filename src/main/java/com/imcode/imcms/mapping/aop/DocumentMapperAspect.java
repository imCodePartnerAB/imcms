package com.imcode.imcms.mapping.aop;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.DocumentShowSettings;
import imcode.server.user.UserDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.imcode.imcms.api.I18nDisabledException;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.mapping.CachingDocumentGetter;
import com.imcode.imcms.mapping.DocumentMapper;

@Aspect
public class DocumentMapperAspect {
	
	private DocumentMapper documentMapper;
	
    @Around("execution(* getDocument(Integer))")
    public Object getDocument(ProceedingJoinPoint pjp) throws Throwable {
    	UserDomainObject user = Imcms.getUser();
    	DocumentShowSettings showSettings = user.getDocumentShowSettings();
    	
    	DocumentDomainObject document = null; 
    		
    	switch (showSettings.getShowVersionMode()) {
		case PUBLISHED:	
			document = (DocumentDomainObject)pjp.proceed();
			break;
			
		case WORKING:
	    	CachingDocumentGetter getter = (CachingDocumentGetter)pjp.getTarget();
	    	Integer documentId = (Integer)pjp.getArgs()[0];			
			document = documentMapper.getWorkingDocument(documentId);
			
			if (document == null) {
				document = getter.getDocument(documentId);
				
				if (document != null) {
					// clone
					documentMapper.saveAsWorkingWersion(document, user);
					document = getter.getWorkingDocument(documentId);
				}
			}
			
			break;			

		default:
			document = null;
		}
    	
    	if (document != null) {
    		if (user != null && !I18nSupport.getCurrentIsDefault() && showSettings.isIgnoreI18nShowMode() ) {            
    			I18nMeta i18nMeta = document.getI18nMeta(I18nSupport.getCurrentLanguage());
            
    			if (!i18nMeta.getEnabled() && !document.getMeta().isShowDisabledI18nContentInDefaultLanguage()) {
    				throw new I18nDisabledException(document, I18nSupport.getCurrentLanguage());
    			}
    		}
    	}
    	
    	return document;
    }

	public DocumentMapper getDocumentMapper() {
		return documentMapper;
	}

	public void setDocumentMapper(DocumentMapper mapper) {
		this.documentMapper = mapper;
	}        
}