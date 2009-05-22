package com.imcode.imcms.mapping.aop;

import imcode.server.document.HtmlDocumentDomainObject;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.imcode.imcms.mapping.DocumentInitializingVisitor;

/**
 * Html document lazy loading aspect.
 * Provides lazy-loading functionality for html document's fields.
 */
@Aspect
public class HtmlDocumentLazyLoadingAspect {
			
    private boolean loaded;
    
	private DocumentInitializingVisitor documentInitializingVisitor;
	
	public HtmlDocumentLazyLoadingAspect(DocumentInitializingVisitor documentInitializingVisitor) {
		this.documentInitializingVisitor = documentInitializingVisitor;
	}    
	
    /** 
     * @param document 	
     */
	@Before("(execution(* *Html*(..)) || execution(* clone())) && target(document)")
	public void load(HtmlDocumentDomainObject document) {
		if (!loaded) {
			documentInitializingVisitor.visitHtmlDocument(document);
			loaded = true;
		}
	}
}