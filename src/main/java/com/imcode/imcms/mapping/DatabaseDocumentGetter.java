package com.imcode.imcms.mapping;

import imcode.server.Imcms;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.imcode.db.Database;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.MetaDao;

public class DatabaseDocumentGetter extends AbstractDocumentGetter {
	
    /** Permission to create child documents. * */
    public final static int PERM_CREATE_DOCUMENT = 8;	

    private ImcmsServices services;
    
    public DatabaseDocumentGetter(Database database, ImcmsServices services) {
        this.services = services;
    }

    public List getDocuments(final Collection documentIds) {
        if (documentIds.isEmpty()) {
            return Collections.EMPTY_LIST ;
        }
                
        Map<Integer, DocumentDomainObject> documentsMap = initDocuments(documentIds);
                                
        initDocuments(documentsMap);
        
        return new DocumentList(documentsMap);
    }
    
    
    /**
     * Initializes documents - hibernate version  
     */
    private Map<Integer, DocumentDomainObject> initDocuments(Collection<Integer> documentIds) {
    	MetaDao metaDao = (MetaDao) Imcms.getServices().getSpringBean("metaDao");
    	
    	Map<Integer, DocumentDomainObject> map = new LinkedHashMap<Integer, DocumentDomainObject>();
    	
    	for (Integer metaId: documentIds) {
    		Meta meta = metaDao.getMeta(metaId);
    		
    		DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());
    		
            document.setId(meta.getMetaId());
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
            map.put(metaId, document);
    	}
    	
    	return map;
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

    
    void initDocuments(Map<Integer, DocumentDomainObject> documentsMap) {
    	DocumentMapper documentMapper = services.getDocumentMapper();    	
        Set<Integer> documentIds = documentsMap.keySet();

        DocumentInitializingVisitor documentInitializingVisitor = new DocumentInitializingVisitor(documentMapper, documentIds, documentMapper);
        
        for (DocumentDomainObject document: documentsMap.values()) {
            document.accept(documentInitializingVisitor);
        }
    }    
}
