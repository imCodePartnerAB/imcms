package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.Keyword;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.MetaDao;

class DocumentSaver {

    private final DocumentMapper documentMapper ;

    public static final String SQL_DELETE_ROLE_DOCUMENT_PERMISSION_SET_ID = "DELETE FROM roles_rights WHERE role_id = ? AND meta_id = ?";
    public static final String SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID = "INSERT INTO roles_rights (role_id, meta_id, set_id) VALUES(?,?,?)";

    DocumentSaver(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    void saveDocument(DocumentDomainObject document, DocumentDomainObject oldDocument,
                      final UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        checkDocumentForSave(document);

        document.loadAllLazilyLoaded();
        
        try {
            Date lastModifiedDatetime = Utility.truncateDateToMinutePrecision(document.getActualModifiedDatetime());
            Date modifiedDatetime = Utility.truncateDateToMinutePrecision(document.getModifiedDatetime());
            boolean modifiedDatetimeUnchanged = lastModifiedDatetime.equals(modifiedDatetime);
            if (modifiedDatetimeUnchanged) {
                document.setModifiedDatetime(documentMapper.getClock().getCurrentDate());
            }

            saveMeta(document);

            if (user.canEditPermissionsFor(oldDocument)) {
                updateDocumentRolePermissions(document, user, oldDocument);

                documentMapper.getDocumentPermissionSetMapper().saveRestrictedDocumentPermissionSets(document, user, oldDocument);
            }

            document.accept(new DocumentSavingVisitor(oldDocument, getDatabase(), documentMapper.getImcmsServices(), user));
        } finally {
            documentMapper.invalidateDocument(document);
        }
    }


    static int convertPublicationStatusToInt(Document.PublicationStatus publicationStatus) {
        int publicationStatusInt = Document.STATUS_NEW;
        if ( Document.PublicationStatus.APPROVED.equals(publicationStatus) ) {
            publicationStatusInt = Document.STATUS_PUBLICATION_APPROVED ;
        } else if ( Document.PublicationStatus.DISAPPROVED.equals(publicationStatus) ) {
            publicationStatusInt = Document.STATUS_PUBLICATION_DISAPPROVED ;
        }
        return publicationStatusInt;
    }


    private Database getDatabase() {
        return documentMapper.getDatabase();
    }

    void saveNewDocument(UserDomainObject user,
                         DocumentDomainObject document, boolean copying) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        checkDocumentForSave(document);

        document.loadAllLazilyLoaded();
        
        // cloen shared references and
        // set metaId to null after cloning
        document.cloneSharedForNewDocument();       
        document.getMeta().setMetaId(null);        

        documentMapper.setCreatedAndModifiedDatetimes(document, new Date());

        int newMetaId = saveMeta(document);

        boolean inheritRestrictedPermissions = !user.isSuperAdminOrHasFullPermissionOn(document) && !copying;
        if (inheritRestrictedPermissions) {
            document.getPermissionSets().setRestricted1(document.getPermissionSetsForNewDocuments().getRestricted1());
            document.getPermissionSets().setRestricted2(document.getPermissionSetsForNewDocuments().getRestricted2());
        }
        
        document.setId(newMetaId);
        
        updateDocumentRolePermissions(document, user, null);

        documentMapper.getDocumentPermissionSetMapper().saveRestrictedDocumentPermissionSets(document, user, null);

        document.accept(new DocumentCreatingVisitor(getDatabase(), documentMapper.getImcmsServices(), user));
    	
        documentMapper.invalidateDocument(document);
    }
    
    
    /**
     * Temporary method
     * Copies data from attributes to meta and stores meta.
     * @return meta id
     */
    private int saveMeta(DocumentDomainObject document) {
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
    	
    	Integer metaId = meta.getMetaId();
    	
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
        			
        			Set<Keyword> keywords = i18nMeta.getKeywords();
        			
        			if (keywords != null) {
        				for (Keyword keyword: keywords) {
        					keyword.setId(null);
        				}
        			}
        		}
        	}        	
    	} 
    	
    	//for update
        //private static final int META_HEADLINE_MAX_LENGTH = 255;
        //private static final int META_TEXT_MAX_LENGTH = 1000;
        //String headlineThatFitsInDB = headline.substring(0, Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
        //String textThatFitsInDB = text.substring(0, Math.min(text.length(), META_TEXT_MAX_LENGTH - 1));
    	
    	//@Immutable
    	// temporal protection from saving
    	// currenlty they are instert by legacy queries
    	meta.getDocPermisionSetEx().clear();
    	meta.getDocPermisionSetExForNew().clear();
    	meta.getRoleRights().clear();
    	meta.getPermissionSetBits().clear();
    	meta.getPermissionSetBitsForNew().clear();
    	
    	// WHAT TO DO WITH THIS on copy save and on base save?    	
    	meta.setSectionIds(document.getSectionIds());
    	meta.setCategoryIds(document.getCategoryIds());
    	meta.setProperties(document.getProperties());
    	
    	MetaDao metaDao = (MetaDao) Imcms.getServices().getSpringBean("metaDao");     	
    	metaDao.updateMeta(document.getMeta());    	    	
    	
    	return meta.getMetaId();
    }
    

    private void checkDocumentForSave(DocumentDomainObject document) throws NoPermissionInternalException, DocumentSaveException {

        documentMapper.getCategoryMapper().checkMaxDocumentCategoriesOfType(document);
        checkIfAliasAlreadyExist(document);

    }

    void updateDocumentRolePermissions(DocumentDomainObject document, UserDomainObject user,
                                       DocumentDomainObject oldDocument) {
        RoleIdToDocumentPermissionSetTypeMappings mappings = new RoleIdToDocumentPermissionSetTypeMappings();

        if (null != oldDocument) {
            RoleIdToDocumentPermissionSetTypeMappings.Mapping[] oldDocumentMappings = oldDocument.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings();
            for ( int i = 0; i < oldDocumentMappings.length; i++ ) {
                RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = oldDocumentMappings[i];
                mappings.setPermissionSetTypeForRole(mapping.getRoleId(), DocumentPermissionSetTypeDomainObject.NONE);
            }
        }

        RoleIdToDocumentPermissionSetTypeMappings.Mapping[] documentMappings = document.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings() ;
        for ( int i = 0; i < documentMappings.length; i++ ) {
            RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = documentMappings[i];
            mappings.setPermissionSetTypeForRole(mapping.getRoleId(), mapping.getDocumentPermissionSetType());
        }

        RoleIdToDocumentPermissionSetTypeMappings.Mapping[] mappingsArray = mappings.getMappings();
        for ( int i = 0; i < mappingsArray.length; i++ ) {
            RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = mappingsArray[i];
            RoleId roleId = mapping.getRoleId();
            DocumentPermissionSetTypeDomainObject documentPermissionSetType = mapping.getDocumentPermissionSetType();

            if (null == oldDocument
                || user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(documentPermissionSetType, roleId, oldDocument)) {
                String[] params1 = new String[]{"" + roleId,
                                                "" + document.getId()};
                getDatabase().execute(new SqlUpdateCommand(SQL_DELETE_ROLE_DOCUMENT_PERMISSION_SET_ID, params1));
                if ( !DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType) ) {
                    String[] params = new String[]{
                        "" + roleId.intValue(), "" + document.getId(), "" + documentPermissionSetType };
                    getDatabase().execute(new SqlUpdateCommand(SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID, params));
                }
            }
        }
    }

    static void makeBooleanSqlUpdateClause(String columnName, boolean bool, List sqlUpdateColumns,
                                           List sqlUpdateValues) {
        sqlUpdateColumns.add(columnName + " = ?");
        sqlUpdateValues.add(bool ? "1" : "0");
    }

    static void makeDateSqlUpdateClause(String columnName, Date date, List sqlUpdateColumns,
                                        List sqlUpdateValues) {
        makeStringSqlUpdateClause(columnName, Utility.makeSqlStringFromDate(date), sqlUpdateColumns, sqlUpdateValues);
    }

    static void makeIntSqlUpdateClause(String columnName, Integer integer, ArrayList sqlUpdateColumns,
                                       ArrayList sqlUpdateValues) {
        if (null != integer) {
            sqlUpdateColumns.add(columnName + " = ?");
            sqlUpdateValues.add("" + integer);
        } else {
            sqlUpdateColumns.add(columnName + " = NULL");
        }
    }

    static void makeStringSqlUpdateClause(String columnName, String value, List sqlUpdateColumns,
                                          List sqlUpdateValues) {
        if (null != value) {
            sqlUpdateColumns.add(columnName + " = ?");
            sqlUpdateValues.add(value);
        } else {
            sqlUpdateColumns.add(columnName + " = NULL");
        }
    }


    Set getDocumentsAddedWithoutPermission(TextDocumentDomainObject textDocument,
                                           TextDocumentDomainObject oldTextDocument,
                                           final UserDomainObject user, DocumentGetter documentGetter) {
        Set documentIdsAdded = getDocumentIdsAdded(textDocument, oldTextDocument);
        List documents = documentGetter.getDocuments(documentIdsAdded);
        Collection documentsAddedWithoutPermission = CollectionUtils.select(documents, new Predicate() {
            public boolean evaluate(Object object) {
                return !user.canAddDocumentToAnyMenu((DocumentDomainObject) object) ;
            }
        }) ;
        return new HashSet(documentsAddedWithoutPermission);
    }

    private Set getDocumentIdsAdded(TextDocumentDomainObject textDocument, TextDocumentDomainObject oldTextDocument
    ) {
        Set documentIdsAdded;
        if (null != oldTextDocument) {
            documentIdsAdded = getChildDocumentIdsDifference(textDocument, oldTextDocument);
        } else {
            documentIdsAdded = textDocument.getChildDocumentIds() ;
        }
        return documentIdsAdded;
    }

    private Set getChildDocumentIdsDifference(TextDocumentDomainObject minuend, TextDocumentDomainObject subtrahend) {
        Set minuendChildDocumentIds = minuend.getChildDocumentIds() ;
        Set subtrahendChildDocumentIds = subtrahend.getChildDocumentIds() ;
        Set result = new HashSet(minuendChildDocumentIds) ;
        result.removeAll(subtrahendChildDocumentIds) ;
        return result ;
    }


    public void checkIfAliasAlreadyExist(DocumentDomainObject document) throws AliasAlreadyExistsInternalException {
        Set<String> allAlias = documentMapper.getAllDocumentAlias() ;
        String alias = document.getAlias();
        if(allAlias.contains(alias) && !documentMapper.getDocument(alias).equals(document) ) {
            throw new AliasAlreadyExistsInternalException("A document with alias '" + document.getAlias()
                                                                         + "' already exists");
        }
    }

}
