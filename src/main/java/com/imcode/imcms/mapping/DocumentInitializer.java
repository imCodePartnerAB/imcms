package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.DocumentPermissionSets;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.user.RoleId;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.MetaDao;

public class DocumentInitializer {
    private final DocumentMapper documentMapper;

    /** Permission to create child documents. * */
    public final static int PERM_CREATE_DOCUMENT = 8;

    MetaDao metaDao;

    public DocumentInitializer(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
        //this.database = documentMapper.getDatabase();
        
        metaDao = (MetaDao)Imcms.getServices().getSpringBean("metaDao");
    }

    /**
     * documents in list have some properties loaded with hibernate; 
     */
    void initDocuments(DocumentList documentList) {
        Set documentIds = documentList.getMap().keySet();

        DocumentInitializingVisitor documentInitializingVisitor = new DocumentInitializingVisitor(documentMapper, documentIds, documentMapper);
        for ( Iterator iterator = documentList.iterator(); iterator.hasNext(); ) {
            final DocumentDomainObject document = (DocumentDomainObject) iterator.next();
            
            Meta meta = document.getMeta();
            
            document.setSectionIds(meta.getSectionIds());
            document.setCategoryIds(meta.getCategoryIds());
            document.setProperties(meta.getProperties());
            
            // Initializing RoleId
            RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings = 
            	new RoleIdToDocumentPermissionSetTypeMappings();
            
            for (Map.Entry<Integer, Integer> roleRight: meta.getRoleRights().entrySet()) {
            	rolePermissionMappings.setPermissionSetTypeForRole(
            			new RoleId(roleRight.getKey()),
            			DocumentPermissionSetTypeDomainObject.fromInt(roleRight.getValue()));
            }
            
            document.setRoleIdsMappedToDocumentPermissionSetTypes(rolePermissionMappings);
            // End of Initializing RoleId
            
            // Initializing Permission set:
            initDocumentsPermissionSets(document, meta);
            initDocumentsPermissionSets(document, meta);
            
            initDocumentsPermissionSetsForNew(document, meta);
            
            document.accept(documentInitializingVisitor);
        }
    }
    
    private void initDocumentsPermissionSets(DocumentDomainObject document, Meta meta) {
    	DocumentPermissionSets permissionSets = new DocumentPermissionSets();
    	Map<Integer, Integer> permissionSetBitsMap = meta.getPermissionSetBits();
    	
    	for (Meta.DocPermisionSetEx ex: meta.getDocPermisionSetEx()) {
    		Integer setId = ex.getSetId();
    		Integer permissionSetBits = permissionSetBitsMap.get(setId);
    		DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);
    		        		
            if ( 0 != permissionSetBits && restricted.isEmpty() ) {
                restricted.setFromBits(permissionSetBits);
            }
            
            setPermissionData(restricted, ex.getPermissionId(), ex.getPermissionData());        		
    	}
    	
    	document.setPermissionSets(permissionSets);
    }
    
    
    private void initDocumentsPermissionSetsForNew(DocumentDomainObject document, Meta meta) {
    	DocumentPermissionSets permissionSets = new DocumentPermissionSets();
    	Map<Integer, Integer> permissionSetBitsMap = meta.getPermissionSetBitsForNew();
    	
    	for (Meta.DocPermisionSetEx ex: meta.getDocPermisionSetExForNew()) {
    		Integer setId = ex.getSetId();
    		Integer permissionSetBits = permissionSetBitsMap.get(setId);
    		DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);
    		        		
            if ( 0 != permissionSetBits && restricted.isEmpty() ) {
                restricted.setFromBits(permissionSetBits);
            }
            
            setPermissionData(restricted, ex.getPermissionId(), ex.getPermissionData());        		
    	}
    	
    	document.setPermissionSetsForNew(permissionSets);
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
}
