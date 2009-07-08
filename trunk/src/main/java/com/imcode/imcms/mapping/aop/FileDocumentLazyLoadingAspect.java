package com.imcode.imcms.mapping.aop;

import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.imcode.imcms.mapping.DocumentInitializingVisitor;

/**
 * File document lazy loading aspect.
 * Provides lazy-loading functionality for file document's fields.
 */
@Aspect
public class FileDocumentLazyLoadingAspect {
			
    private boolean loaded;
    
	private DocumentInitializingVisitor documentInitializingVisitor;
	
	public FileDocumentLazyLoadingAspect(DocumentInitializingVisitor documentInitializingVisitor) {
		this.documentInitializingVisitor = documentInitializingVisitor;
	}    
	
	@Before("(execution(* *File*(..)) || execution(* clone())) && target(document)")
	public void load(FileDocumentDomainObject document) {
		if (!loaded) {
			documentInitializingVisitor.visitFileDocument(document);
			loaded = true;
		}
	}
	
	@Around("execution(public void accept(..)) && args(documentVisitor)")
	public void accept(ProceedingJoinPoint pjp, DocumentVisitor documentVisitor) {
		documentVisitor.visitFileDocument((FileDocumentDomainObject)pjp.getThis());
	}	
}