package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import com.imcode.imcms.mapping.aop.DocumentAspect;
import com.imcode.imcms.mapping.aop.TextDocumentAspect;

/**
 * TODO: 
 *   cache -> published version cache
 *   workingCache -> working version cache
 *   customCacle -> custom version cache. by meta id
 */
public class CachingDocumentGetter extends DocumentGetterWrapper {

    private Map cache;
    
    // Used by CGLIB proxy generator
    public CachingDocumentGetter() {
    	super(null);
    }
    
    public CachingDocumentGetter(DocumentGetter documentGetter, Map cache) {
        super(documentGetter);
        this.cache = cache ;
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        DocumentDomainObject document = (DocumentDomainObject) cache.get(documentId) ;
        
        if (null == document) {
            document = super.getDocument(documentId) ;
            
            if (document != null) {
            	// experimental - TODO: Optimize
            	AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(document);            	
                aspectJProxyFactory.setProxyTargetClass(true);
                aspectJProxyFactory.addAspect(DocumentAspect.class);
                
                if (document instanceof TextDocumentDomainObject) {
                	aspectJProxyFactory.addAspect(TextDocumentAspect.class);
                }
            	
            	document = aspectJProxyFactory.getProxy();
            	
            	cache.put(documentId, document) ;
            }
        }
                
        return document;
    }
            
    
    public List getDocuments(Collection documentIds) {
        return super.getDocuments(documentIds) ;
    }    
}