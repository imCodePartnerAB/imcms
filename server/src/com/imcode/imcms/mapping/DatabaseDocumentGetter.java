package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.handlers.CollectionHandler;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.imcms.api.Document;
import imcode.server.ImcmsServices;
import imcode.server.LanguageMapper;
import imcode.server.document.DocumentDomainObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseDocumentGetter extends AbstractDocumentGetter {

    private Database database;
    private ImcmsServices services;
    public static final String SQL_GET_DOCUMENTS = "SELECT meta_id,\n"
                                                   + "doc_type,\n"
                                                   + "meta_headline,\n"
                                                   + "meta_text,\n"
                                                   + "meta_image,\n"
                                                   + "owner_id,\n"
                                                   + "permissions,\n"
                                                   + "shared,\n"
                                                   + "show_meta,\n"
                                                   + "lang_prefix,\n"
                                                   + "date_created,\n"
                                                   + "date_modified,\n"
                                                   + "disable_search,\n"
                                                   + "target,\n"
                                                   + "archived_datetime,\n"
                                                   + "publisher_id,\n"
                                                   + "status,\n"
                                                   + "publication_start_datetime,\n"
                                                   + "publication_end_datetime\n"
                                                   + "FROM meta\n"
                                                   + "WHERE meta_id ";
    static final String SQL_SELECT_PERMISSON_DATA__PREFIX = "SELECT meta_id, set_id, permission_data FROM ";

    public DatabaseDocumentGetter(Database database, ImcmsServices services) {
        this.database = database;
        this.services = services;
    }

    public List getDocuments(final Collection documentIds) {
        if (documentIds.isEmpty()) {
            return Collections.EMPTY_LIST ;
        }
        LinkedHashMap documentMap = new LinkedHashMap();
        DocumentInitializer.executeWithAppendedIntegerInClause(database, SQL_GET_DOCUMENTS, documentIds, new CollectionHandler(new DocumentMapSet(documentMap), new DocumentFromRowFactory()));

        DocumentList documentList = new DocumentList(documentMap);

        DocumentInitializer initializer = new DocumentInitializer(services.getDocumentMapper());
        initializer.initDocuments(documentList);

        LinkedHashMap retMap = new LinkedHashMap();
        
        for (Iterator it = documentIds.iterator(); it.hasNext();) {
            Integer id = (Integer)it.next();
            retMap.put(id, documentMap.get(id));
        }

        return new DocumentList(retMap);
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

    private class DocumentFromRowFactory implements RowTransformer {

        public Object createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
            final int documentTypeId = resultSet.getInt(2);
            DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(documentTypeId);

            int documentId = resultSet.getInt(1);
            document.setId(documentId);
            document.setHeadline(resultSet.getString(3));
            document.setMenuText(resultSet.getString(4));
            document.setMenuImage(resultSet.getString(5));

            document.setCreatorId(resultSet.getInt(6));
            document.setRestrictedOneMorePrivilegedThanRestrictedTwo(resultSet.getBoolean(7));
            document.setLinkableByOtherUsers(resultSet.getBoolean(8));
            document.setLinkedForUnauthorizedUsers(resultSet.getBoolean(9));
            document.setLanguageIso639_2(LanguageMapper.getAsIso639_2OrDefaultLanguage(resultSet.getString(10), services.getLanguageMapper().getDefaultLanguage()));
            document.setCreatedDatetime(resultSet.getTimestamp(11));
            Date modifiedDatetime = resultSet.getTimestamp(12);
            document.setModifiedDatetime(modifiedDatetime);
            document.setActualModifiedDatetime(modifiedDatetime);
            document.setSearchDisabled(resultSet.getBoolean(13));
            document.setTarget(resultSet.getString(14));
            document.setArchivedDatetime(resultSet.getTimestamp(15));
            Number publisherId = (Number) resultSet.getObject(16);
            document.setPublisherId(publisherId == null ? null : new Integer(publisherId.intValue()));
            int publicationStatusInt = resultSet.getInt(17);
            Document.PublicationStatus publicationStatus = publicationStatusFromInt(publicationStatusInt);
            document.setPublicationStatus(publicationStatus);
            document.setPublicationStartDatetime(resultSet.getTimestamp(18));
            document.setPublicationEndDatetime(resultSet.getTimestamp(19));

            return document;
        }

        public Class getClassOfCreatedObjects() {
            return DocumentDomainObject.class;
        }
    }

}
