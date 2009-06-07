package com.imcode.imcms.mapping;

import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.DocumentPermissionSets;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.user.RoleId;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionSelector;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.aop.FileDocumentLazyLoadingAspect;
import com.imcode.imcms.mapping.aop.HtmlDocumentLazyLoadingAspect;
import com.imcode.imcms.mapping.aop.TextDocumentLazyLoadingAspect;
import com.imcode.imcms.mapping.aop.UrlDocumentLazyLoadingAspect;

/**
 * Retrieves documents from the database. 
 * Instantiated by spring-framework and initialized in DocumentMapper constructor. 
 */
public class DatabaseDocumentGetter implements DocumentGetter {
	
    /** Permission to create child documents. * */
    public final static int PERM_CREATE_DOCUMENT = 8;	

    /** Injected by DocumentMapper. */
    private ImcmsServices services;
    
    private MetaDao metaDao;
        
    /**
     * Initializes document's fields.
     */
    private DocumentInitializingVisitor documentInitializingVisitor;
    
    /**
     * Returns latest (working) version of a document.
     * TODO: OPTIMIZE!!
     */
    public DocumentDomainObject getDocument(Integer documentId) {
    	return getWorkingDocument(documentId);
    }
    
    public DocumentDomainObject getDocument(Integer documentId, Integer versionNumber) {
    	return initDocument(loadDocument(documentId, versionNumber));
    }	    
        
    public DocumentDomainObject getPublishedDocument(Integer documentId) {
    	return initDocument(loadDocument(documentId, DocumentVersionSelector.PUBLISHED_SELECTOR));
    }	
	
	public DocumentDomainObject getWorkingDocument(Integer documentId) {
		return initDocument(loadDocument(documentId, DocumentVersionSelector.WORKING_SELECTOR));
	}
	
    /**
     * TODO: Investigate what documents to return 
     * Returns published/latest documents.
     */
    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        List<DocumentDomainObject> documents = new LinkedList<DocumentDomainObject>();
        
    	for (Integer documentId: documentIds) {
    		DocumentDomainObject document = initDocument(loadDocument(documentId, DocumentVersionSelector.PUBLISHED_SELECTOR));
    		
    		// ??? do not add in case of null
    		if (document != null) {
    			documents.add(document);    			
    		}
    	}
                                
        return documents;
    } 
    

    /**
     * Loads document
     */
    private DocumentDomainObject loadDocument(Integer documentId, DocumentVersionSelector versionSpecifier) {		
    	Meta meta = metaDao.getMeta(documentId, versionSpecifier);
		
		return initMeta(meta);
    }
    
    /**
     * Loads document
     */
    private DocumentDomainObject loadDocument(Integer documentId, Integer versionNumber) {		
    	Meta meta = metaDao.getMeta(documentId, versionNumber);
		
		return initMeta(meta);
    }    
    
    /**
     * Initializes document's meta.
     */
    private DocumentDomainObject initMeta(Meta meta) {		
		if (meta == null) {
			return null;
		}
		
		DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());
		document.setMeta(meta);
		
		document.setActualModifiedDatetime(meta.getModifiedDatetime());
        
        Document.PublicationStatus publicationStatus = publicationStatusFromInt(
        		meta.getPublicationStatusInt());            
        document.setPublicationStatus(publicationStatus);
                
        initRoleIdToPermissionSetIdMap(document, meta);
        initDocumentsPermissionSets(document, meta);
        initDocumentsPermissionSetsForNew(document, meta);            
        
        return document;
    }
    
    /**
     * Initializes document's fields.
     * 
     * TODO: Refactor out AOP aspects creation and copy-paste.
     */
    private DocumentDomainObject initDocument(DocumentDomainObject document) {
    	if (document == null) return null;
    	
    	/*
    	AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(document); 
    	aspectJProxyFactory.setProxyTargetClass(true);
    	
    	switch (document.getMeta().getDocumentType()) {
    	case DocumentTypeDomainObject.TEXT_ID:
            aspectJProxyFactory.addAspect(new TextDocumentLazyLoadingAspect(
            		documentInitializingVisitor.getTextDocumentInitializer()));       
           break;
            
    	case DocumentTypeDomainObject.FILE_ID:
            aspectJProxyFactory.addAspect(new FileDocumentLazyLoadingAspect(documentInitializingVisitor));
            break;
            
    	case DocumentTypeDomainObject.URL_ID:
            aspectJProxyFactory.addAspect(new UrlDocumentLazyLoadingAspect(documentInitializingVisitor));
            break;    
            
    	case DocumentTypeDomainObject.HTML_ID:
            aspectJProxyFactory.addAspect(new HtmlDocumentLazyLoadingAspect(documentInitializingVisitor));
            break;            
    		
        default:
        	throw new AssertionError("Unknown document type id: " + document.getMeta().getDocumentType());
    	}
        
        return aspectJProxyFactory.getProxy();
        */
        document.accept(documentInitializingVisitor);
        
        return document;
    }  
    
    private Document.PublicationStatus publicationStatusFromInt(int publicationStatusInt) {
        Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;
        if ( Document.STATUS_PUBLICATION_APPROVED == publicationStatusInt ) {
            publicationStatus = Document.PublicationStatus.APPROVED;
        } else if ( Document.STATUS_PUBLICATION_DISAPPROVED == publicationStatusInt ) {
            publicationStatus = Document.PublicationStatus.DISAPPROVED;
        }
        return publicationStatus;
    }
    
    // Moved from  DocumentInitializer.initDocuments
    private void initRoleIdToPermissionSetIdMap(DocumentDomainObject document, Meta meta) {
        RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings = 
        	new RoleIdToDocumentPermissionSetTypeMappings();
        
        for (Map.Entry<Integer, Integer> roleIdToPermissionSetId: meta.getRoleIdToPermissionSetIdMap().entrySet()) {
        	rolePermissionMappings.setPermissionSetTypeForRole(
        			new RoleId(roleIdToPermissionSetId.getKey()),
        			DocumentPermissionSetTypeDomainObject.fromInt(roleIdToPermissionSetId.getValue()));
        }
        
        document.setRoleIdsMappedToDocumentPermissionSetTypes(rolePermissionMappings);    	
    }
    
    private void initDocumentsPermissionSets(DocumentDomainObject document, Meta meta) {
    	DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
    			meta.getPermissionSetBitsMap(), meta.getPermisionSetEx());
    	
    	document.setPermissionSets(permissionSets);
    }
    
    
    private void initDocumentsPermissionSetsForNew(DocumentDomainObject document, Meta meta) {
    	DocumentPermissionSets permissionSets = createDocumentsPermissionSets(
    			meta.getPermissionSetBitsForNewMap(), meta.getPermisionSetExForNew());
    	
    	document.setPermissionSetsForNew(permissionSets);
    }
    
    
    private DocumentPermissionSets createDocumentsPermissionSets(
    		Map<Integer, Integer> permissionSetBitsMap,
    		Set<Meta.PermisionSetEx> permissionSetEx) {
    	
    	DocumentPermissionSets permissionSets = new DocumentPermissionSets();
    	
    	for (Map.Entry<Integer, Integer> permissionSetBitsEntry: permissionSetBitsMap.entrySet()) {
    		Integer setId = permissionSetBitsEntry.getKey();
    		Integer permissionSetBits = permissionSetBitsEntry.getValue();
    		DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);
    		
            if (permissionSetBits != 0 && restricted.isEmpty()) {
                restricted.setFromBits(permissionSetBits);
            }    		    		
    	}    	
    	
    	for (Meta.PermisionSetEx ex: permissionSetEx) {
    		Integer setId = ex.getSetId();
    		DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);
    		        		
            setPermissionData(restricted, ex.getPermissionId(), ex.getPermissionData());        		
    	}
    	
    	return permissionSets;
    }    
    

    private void setPermissionData(DocumentPermissionSetDomainObject permissionSet, Integer permissionId, Integer permissionData) {
        if (null != permissionId) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) permissionSet ;
            switch(permissionId.intValue()) {
                case PERM_CREATE_DOCUMENT:
                    textDocumentPermissionSet.addAllowedDocumentTypeId(permissionData.intValue());
                    break;
                case ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE:
                    textDocumentPermissionSet.addAllowedTemplateGroupId(permissionData.intValue());
                    break;
                default:
            }
        }
    }   


	public ImcmsServices getServices() {
		return services;
	}

	public void setServices(ImcmsServices services) {
		this.services = services;
	}

	public MetaDao getMetaDao() {
		return metaDao;
	}

	public void setMetaDao(MetaDao metaDao) {
		this.metaDao = metaDao;
	}

	public DocumentInitializingVisitor getDocumentInitializingVisitor() {
		return documentInitializingVisitor;
	}

	public void setDocumentInitializingVisitor(
			DocumentInitializingVisitor documentInitializingVisitor) {
		this.documentInitializingVisitor = documentInitializingVisitor;
	}      
}
