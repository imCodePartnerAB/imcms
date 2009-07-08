package com.imcode.imcms.mapping.aop;

import imcode.server.document.DocumentVisitor;
import imcode.server.document.UrlDocumentDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.imcode.imcms.mapping.DocumentInitializingVisitor;

/**
 * Url document lazy loading aspect.
 * Provides lazy-loading functionality for url document's fields.
 */
@Aspect
public class UrlDocumentLazyLoadingAspect {
			
    private boolean loaded;
    
	private DocumentInitializingVisitor documentInitializingVisitor;
	
	public UrlDocumentLazyLoadingAspect(DocumentInitializingVisitor documentInitializingVisitor) {
		this.documentInitializingVisitor = documentInitializingVisitor;
	}    
	
	@Before("(execution(* *Url*(..)) || execution(* clone())) && target(document)")
	public void load(UrlDocumentDomainObject document) {
		if (!loaded) {
			documentInitializingVisitor.visitUrlDocument(document);
			loaded = true;
		}
	}
	
	@Around("execution(public void accept(..)) && args(documentVisitor)")
	public void accept(ProceedingJoinPoint pjp, DocumentVisitor documentVisitor) {
		documentVisitor.visitUrlDocument((UrlDocumentDomainObject)pjp.getThis());
	}		
}