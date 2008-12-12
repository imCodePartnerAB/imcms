package com.imcode.imcms.mapping;

import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.LanguageMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.DocumentPermissionSets;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.user.RoleId;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.MetaDao;

public class DatabaseDocumentGetter implements DocumentGetter {
	
    /** Permission to create child documents. * */
    public final static int PERM_CREATE_DOCUMENT = 8;	

    private ImcmsServices services;
    
   // private MetaDao metaDao = (MetaDao)services.getSpringBean("metaDao");
    
    public DatabaseDocumentGetter(ImcmsServices services) {
        this.services = services;
        //this.metaDao = (MetaDao)services.getSpringBean("metaDao");
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        return initDocument(loadDocument(documentId, Meta.DocumentVersionStatus.PUBLISHED));
    }
	
	public DocumentDomainObject getWorkingDocument(Integer documentId) {
		return initDocument(loadDocument(documentId, Meta.DocumentVersionStatus.WORKING));
	}
	
    /**
     * Returns published documents.
     */
    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        List<DocumentDomainObject> documents = new LinkedList<DocumentDomainObject>();
        
    	for (Integer documentId: documentIds) {
    		DocumentDomainObject document = initDocument(loadDocument(documentId, Meta.DocumentVersionStatus.PUBLISHED));
    		
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
    private DocumentDomainObject loadDocument(Integer documentId, Meta.DocumentVersionStatus versionStatus) {		
    	MetaDao metaDao = (MetaDao)services.getSpringBean("metaDao");
    	Meta meta = metaDao.getMeta(documentId, versionStatus);
		
		if (meta == null) {
			return null;
		}
		
		DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());
		
        document.setId(meta.getDocumentId());
        document.setCreatorId(meta.getCreatorId());
        document.setRestrictedOneMorePrivilegedThanRestrictedTwo(meta.getRestrictedOneMorePrivilegedThanRestrictedTwo());
        
        document.setLinkableByOtherUsers(meta.getLinkableByOtherUsers());
        document.setLinkedForUnauthorizedUsers(meta.getLinkedForUnauthorizedUsers());
        
        // Not related to i18nl language
        String language = LanguageMapper.getAsIso639_2OrDefaultLanguage(
        		meta.getLanguageIso639_2(), 
        		services.getLanguageMapper().getDefaultLanguage());
        
        document.setLanguageIso639_2(language);
        
        document.setCreatedDatetime(meta.getCreatedDatetime());            
        document.setModifiedDatetime(meta.getModifiedDatetime());            
        document.setActualModifiedDatetime(meta.getModifiedDatetime());
        
        document.setSearchDisabled(meta.getSearchDisabled());
        document.setTarget(meta.getTarget());
        
        document.setArchivedDatetime(meta.getArchivedDatetime());            
        document.setPublisherId(meta.getPublisherId());
        
        Document.PublicationStatus publicationStatus = publicationStatusFromInt(
        		meta.getPublicationStatusInt());            
        document.setPublicationStatus(publicationStatus);
        
        document.setPublicationStartDatetime(meta.getPublicationStartDatetime());
        document.setPublicationEndDatetime(meta.getPublicationEndDatetime());
                    
        // moved from DocumentInitializer.initDocuments
        document.setSectionIds(meta.getSectionIds());
        document.setCategoryIds(meta.getCategoryIds());
        document.setProperties(meta.getProperties());
        
        initRoleIdToPermissionSetIdMap(document, meta);
        initDocumentsPermissionSets(document, meta);
        initDocumentsPermissionSetsForNew(document, meta);            
        // end of moved from DocumentInitializer.initDocuments
                    
        document.setMeta(meta);
        
        return document;
    }
    
    private DocumentDomainObject initDocument(DocumentDomainObject document) {
    	if (document == null) return null;
    	
    	DocumentMapper documentMapper = services.getDocumentMapper();    	
        DocumentInitializingVisitor documentInitializingVisitor = new DocumentInitializingVisitor(documentMapper, null, documentMapper);
        
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
    
    private List<DocumentDomainObject> initDocuments(List<DocumentDomainObject> documents) {
    	DocumentMapper documentMapper = services.getDocumentMapper();    	
        DocumentInitializingVisitor documentInitializingVisitor = new DocumentInitializingVisitor(documentMapper, null, documentMapper);
        
        for (DocumentDomainObject document: documents) {
            document.accept(documentInitializingVisitor);
        }
        
        return documents;
    }      
}
