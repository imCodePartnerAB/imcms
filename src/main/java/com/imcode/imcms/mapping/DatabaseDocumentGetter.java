package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.LanguageMapper;
import imcode.server.document.DocumentDomainObject;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.imcode.db.Database;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.MetaDao;

public class DatabaseDocumentGetter extends AbstractDocumentGetter {

    private Database database;
    private ImcmsServices services;
    
    static final String SQL_SELECT_PERMISSON_DATA__PREFIX = "SELECT meta_id, set_id, permission_data FROM ";

    public DatabaseDocumentGetter(Database database, ImcmsServices services) {
        this.database = database;
        this.services = services;
    }

    public List getDocuments(final Collection documentIds) {
        // TODO: i18n refactor:
        MetaDao metaDao = (MetaDao) Imcms.getServices().getSpringBean("metaDao");
    	
    	// Just for test:
        /*
    	if (documentIds.size() > 1) {
    		throw new AssertionError("Too many getDocuments!!!: " + documentIds.size());
    	}
    	*/
    	// end just for test    	
    	
        if (documentIds.isEmpty()) {
            return Collections.EMPTY_LIST ;
        }
        
        // Replaced by meta initialization:
        /*
        LinkedHashMap documentMap = new LinkedHashMap();
                
        DocumentInitializer.executeWithAppendedIntegerInClause(database, SQL_GET_DOCUMENTS, documentIds, new CollectionHandler(new DocumentMapSet(documentMap), new DocumentFromRowFactory()));
		*/
        
        LinkedHashMap<Integer, DocumentDomainObject> documentMap = 
        	newInitDocuments(metaDao, documentIds);
        
        DocumentList documentList = new DocumentList(documentMap);
                        
        DocumentInitializer initializer = new DocumentInitializer(services.getDocumentMapper());
        initializer.initDocuments(documentList);
                
        for (DocumentDomainObject document: documentList) {
        	Meta meta = metaDao.getMeta(document.getId());
        	
        	document.setMeta(meta);
        }

        LinkedHashMap retMap = new LinkedHashMap();
        
        for (Iterator it = documentIds.iterator(); it.hasNext();) {
            Integer id = (Integer)it.next();
            retMap.put(id, documentMap.get(id));
        }

        return new DocumentList(retMap);
    }
    
    
    /**
     * Initializes documents - hibernate version  
     */
    private LinkedHashMap newInitDocuments(MetaDao metaDao, Collection<Integer> documentIds) {
    	LinkedHashMap<Integer, DocumentDomainObject> map = new LinkedHashMap<Integer, DocumentDomainObject>();
    	
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
            
            document.setMeta(meta);
            
            map.put(metaId, document);
    	}
    	
    	return map;
    }
    

    private class DocumentMapSet extends AbstractSet {

        private Map map ;

        DocumentMapSet(Map map) {
            this.map = map;
        }

        public int size() {
            return map.size();
        }

        public boolean add(Object o) {
            DocumentDomainObject document = (DocumentDomainObject) o ;
            return null == map.put(new Integer(document.getId()), document) ;
        }

        public Iterator iterator() {
            return map.values().iterator() ;
        }

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
  }
