package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.MetaDao;

public class DocumentSaver {

    private DocumentMapper documentMapper;
    
    public DocumentSaver() {}
    
    public DocumentSaver(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }
    
    @Transactional     //experemental
    public void saveDocumentFragment(DocumentDomainObject document, UserDomainObject user, HibernateCallback hibernateCallback) throws NoPermissionInternalException, DocumentSaveException {
    	checkDocumentForSave(document);
    	
    	try {
    		HibernateTemplate template = (HibernateTemplate)Imcms.getServices().getSpringBean("hibernateTemplate");
    		
    		template.execute(hibernateCallback); 

            Date lastModifiedDatetime = Utility.truncateDateToMinutePrecision(document.getActualModifiedDatetime());
            Date modifiedDatetime = Utility.truncateDateToMinutePrecision(document.getModifiedDatetime());
            boolean modifiedDatetimeUnchanged = lastModifiedDatetime.equals(modifiedDatetime);
            
            if (modifiedDatetimeUnchanged) {            	
            	modifiedDatetime = documentMapper.getClock().getCurrentDate();
            }
            
    		template.bulkUpdate("update Meta m set m.modifiedDatetime = ? where m.id = ?", 
    			new Object[] {modifiedDatetime, document.getMeta().getId()});
	    } finally {
	        documentMapper.invalidateDocument(document);
	    }    	
    }

    @Transactional
    public void saveDocument(DocumentDomainObject document, DocumentDomainObject oldDocument,
                      final UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        checkDocumentForSave(document);

        //document.loadAllLazilyLoaded();
        
        try {
            Date lastModifiedDatetime = Utility.truncateDateToMinutePrecision(document.getActualModifiedDatetime());
            Date modifiedDatetime = Utility.truncateDateToMinutePrecision(document.getModifiedDatetime());
            boolean modifiedDatetimeUnchanged = lastModifiedDatetime.equals(modifiedDatetime);
            if (modifiedDatetimeUnchanged) {
                document.setModifiedDatetime(documentMapper.getClock().getCurrentDate());
            }

            if (user.canEditPermissionsFor(oldDocument)) {
                newUpdateDocumentRolePermissions(document, user, oldDocument);
                documentMapper.getDocumentPermissionSetMapper().saveRestrictedDocumentPermissionSets(document, user, oldDocument);
            }
            
            DocumentSavingVisitor savingVisitor = new DocumentSavingVisitor(oldDocument, documentMapper.getImcmsServices(), user);
            
            saveMeta(document);
                        
            document.accept(savingVisitor);
        } finally {
            documentMapper.invalidateDocument(document);
        }
    }


    @Transactional
    public void saveNewDocument(UserDomainObject user,
                         DocumentDomainObject document, boolean copying) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        checkDocumentForSave(document);

        //document.loadAllLazilyLoaded();
        
        document.setDependenciesMetaIdToNull(); 
        Meta meta = document.getMeta();
        
        // Version
        // if publicating aprowed - publish else ??? 
        meta.setDocumentVersionStatus(Meta.DocumentVersionStatus.PUBLISHED);

        documentMapper.setCreatedAndModifiedDatetimes(document, new Date());

        boolean inheritRestrictedPermissions = !user.isSuperAdminOrHasFullPermissionOn(document) && !copying;
        if (inheritRestrictedPermissions) {
            document.getPermissionSets().setRestricted1(document.getPermissionSetsForNewDocuments().getRestricted1());
            document.getPermissionSets().setRestricted2(document.getPermissionSetsForNewDocuments().getRestricted2());
        }
        
        newUpdateDocumentRolePermissions(document, user, null);

        // Updates permissions - method does not saves but instead just updates meta 
        documentMapper.getDocumentPermissionSetMapper().saveRestrictedDocumentPermissionSets(document, user, null);
        
        meta = saveMeta(document);
                
        document.setId(meta.getDocumentId());
        
        document.accept(new DocumentCreatingVisitor(documentMapper.getImcmsServices(), user));
    	
        documentMapper.invalidateDocument(document);
    }
    
    
    /**
     * Temporary method
     * Copies data from attributes to meta and stores meta.
     * 
     * @return meta id
     */
    private Meta saveMeta(DocumentDomainObject document) {
    	Meta meta = document.getMeta();
    	
    	meta.setCreatorId(document.getCreatorId());
    	meta.setRestrictedOneMorePrivilegedThanRestrictedTwo(
    			document.isRestrictedOneMorePrivilegedThanRestrictedTwo());
    	
    	meta.setLinkableByOtherUsers(document.isLinkableByOtherUsers());
    	meta.setLinkedForUnauthorizedUsers(document.isLinkedForUnauthorizedUsers());
    	meta.setLanguageIso639_2(document.getLanguageIso639_2());
    	meta.setCreatedDatetime(document.getCreatedDatetime());
    	meta.setModifiedDatetime(document.getModifiedDatetime());
    	meta.setSearchDisabled(document.isSearchDisabled());
    	meta.setTarget(document.getTarget());
    	
    	meta.setArchivedDatetime(document.getArchivedDatetime());
    	meta.setPublisherId(document.getPublisherId());
    	meta.setPublicationStatusInt(document.getPublicationStatus().asInt());
    	meta.setPublicationStartDatetime(document.getPublicationStartDatetime());
    	meta.setPublicationEndDatetime(document.getPublicationEndDatetime());
    	
    	Long metaId = meta.getId();
    	
    	if (metaId == null) {
    		//insert
        	meta.setDocumentType(document.getDocumentTypeId());
        	meta.setActivate(1);
        	
        	// is required; -> when update do the same?? but assign meta id???
        	List<I18nMeta> i18nMetas = meta.getI18nMetas();
        	
        	if (i18nMetas != null) {
        		for (I18nMeta i18nMeta: i18nMetas) {
        			i18nMeta.setId(null);
        			i18nMeta.setMetaId(metaId);
        		}
        	}        	
    	} 
    	
    	//for update
        //private static final int META_HEADLINE_MAX_LENGTH = 255;
        //private static final int META_TEXT_MAX_LENGTH = 1000;
        //String headlineThatFitsInDB = headline.substring(0, Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
        //String textThatFitsInDB = text.substring(0, Math.min(text.length(), META_TEXT_MAX_LENGTH - 1));
    	
    	//@Immutable
    	// inserted by legacy queries - not any more
    	
    	// Converted from legacy queries:
    	// Should be handled separately from meta???
    	//meta.getRoleIdToPermissionSetIdMap();
    	//meta.getDocPermisionSetEx().clear();
    	//meta.getDocPermisionSetExForNew().clear();    	
    	//meta.getPermissionSetBitsMap().clear();
    	//meta.getPermissionSetBitsForNewMap().clear();    	    	
    	
    	// WHAT TO DO WITH THIS on copy save and on base save?    	
    	meta.setSectionIds(document.getSectionIds());
    	meta.setCategoryIds(document.getCategoryIds());
    	meta.setProperties(document.getProperties());
    	
    	MetaDao metaDao = (MetaDao) Imcms.getServices().getSpringBean("metaDao");     	
    	metaDao.updateMeta(meta);    	    	
    	
    	return meta;
    }
    

    private void checkDocumentForSave(DocumentDomainObject document) throws NoPermissionInternalException, DocumentSaveException {

        documentMapper.getCategoryMapper().checkMaxDocumentCategoriesOfType(document);
        checkIfAliasAlreadyExist(document);

    }
    
    
    /**
     * Update meta roles to permissions set mapping.
     * Modified copy of legacy updateDocumentRolePermissions method.  
     * NB! Compared to legacy this method does not update database.
     */
    void newUpdateDocumentRolePermissions(DocumentDomainObject document, UserDomainObject user,
            DocumentDomainObject oldDocument) {

    	// Original (old) and modified or new document permission set type mapping.
		RoleIdToDocumentPermissionSetTypeMappings mappings = new RoleIdToDocumentPermissionSetTypeMappings();
		
		// Copy original document' roles to mapping with NONE(4) permissions-set assigned
		if (null != oldDocument) {
			RoleIdToDocumentPermissionSetTypeMappings.Mapping[] oldDocumentMappings = oldDocument.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings();
			for ( int i = 0; i < oldDocumentMappings.length; i++ ) {
				RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = oldDocumentMappings[i];
				mappings.setPermissionSetTypeForRole(mapping.getRoleId(), DocumentPermissionSetTypeDomainObject.NONE);
			}
		}
		
		// Copy modified or new document' roles to mapping
		RoleIdToDocumentPermissionSetTypeMappings.Mapping[] documentMappings = document.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings() ;
		for ( int i = 0; i < documentMappings.length; i++ ) {
			RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = documentMappings[i];
			mappings.setPermissionSetTypeForRole(mapping.getRoleId(), mapping.getDocumentPermissionSetType());
		}
		
		RoleIdToDocumentPermissionSetTypeMappings.Mapping[] mappingsArray = mappings.getMappings();
		Map<Integer, Integer> roleIdToPermissionSetIdMap = document.getMeta().getRoleIdToPermissionSetIdMap();
		
		for ( int i = 0; i < mappingsArray.length; i++ ) {
			RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = mappingsArray[i];
			RoleId roleId = mapping.getRoleId();
			DocumentPermissionSetTypeDomainObject documentPermissionSetType = mapping.getDocumentPermissionSetType();
			
			if (null == oldDocument
					|| user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(documentPermissionSetType, roleId, oldDocument)) {
				
				// DB designed not to save NONE 
				if (documentPermissionSetType.equals(DocumentPermissionSetTypeDomainObject.NONE)) {
					roleIdToPermissionSetIdMap.remove(roleId.intValue());
				} else {
					roleIdToPermissionSetIdMap.put(roleId.intValue(), documentPermissionSetType.getId());
				}
			}
		}
	}

    public void checkIfAliasAlreadyExist(DocumentDomainObject document) throws AliasAlreadyExistsInternalException {
        Set<String> allAlias = documentMapper.getAllDocumentAlias() ;
        String alias = document.getAlias();
        if(allAlias.contains(alias) && !documentMapper.getDocument(alias).equals(document) ) {
            throw new AliasAlreadyExistsInternalException("A document with alias '" + document.getAlias()
                                                                         + "' already exists");
        }
    }

	public DocumentMapper getDocumentMapper() {
		return documentMapper;
	}

	public void setDocumentMapper(DocumentMapper documentMapper) {
		this.documentMapper = documentMapper;
	}
}
