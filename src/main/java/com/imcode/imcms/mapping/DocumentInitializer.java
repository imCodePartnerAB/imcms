package com.imcode.imcms.mapping;

import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.document.CopyableHashSet;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.DocumentPermissionSets;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.CopyableHashMap;
import imcode.server.user.RoleId;
import imcode.util.LazilyLoadedObject;
import imcode.util.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.dbutils.ResultSetHandler;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.dao.IncludeDao;
import com.imcode.imcms.dao.MetaDao;
import com.imcode.util.CountingIterator;

public class DocumentInitializer {
    private final DocumentMapper documentMapper;

    /** Permission to create child documents. * */
    public final static int PERM_CREATE_DOCUMENT = 8;
    private Database database;

    HashMap documentsPermissionSets ;
    HashMap documentsPermissionSetsForNew ;
    MetaDao metaDao;

    public DocumentInitializer(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
        this.database = documentMapper.getDatabase();
        
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
            final Integer documentId = new Integer(document.getId()) ;

            final LazilyLoadedObject permissionSets = new LazilyLoadedObject(new DocumentPermissionSetsLoader(documentIds, false, documentId));
            LazilyLoadedObject permissionSetsForNew = new LazilyLoadedObject(new DocumentPermissionSetsLoader(documentIds, true, documentId));

            document.setLazilyLoadedPermissionSets(permissionSets);
            document.setLazilyLoadedPermissionSetsForNew(permissionSetsForNew);
            
            Meta meta = document.getMeta();
            
            document.setSectionIds(meta.getSectionIds());
            document.setCategoryIds(meta.getCategoryIds());
            document.setProperties(meta.getProperties());
            
            //
            RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings = 
            	new RoleIdToDocumentPermissionSetTypeMappings();
            
            for (Map.Entry<Integer, Integer> roleRight: meta.getRoleRights().entrySet()) {
            	rolePermissionMappings.setPermissionSetTypeForRole(
            			new RoleId(roleRight.getKey()),
            			DocumentPermissionSetTypeDomainObject.fromInt(roleRight.getValue()));
            }
            
            document.setRoleIdsMappedToDocumentPermissionSetTypes(rolePermissionMappings);
            

            document.accept(documentInitializingVisitor);
        }
    }


    private static Integer[] appendInClause(StringBuffer sql, Collection documentIds) {
        sql.append("IN (");
        Integer[] documentIdsArray = new Integer[documentIds.size()];
        for ( CountingIterator iterator = new CountingIterator(documentIds.iterator()); iterator.hasNext(); ) {
            Integer documentId = (Integer) iterator.next();
            documentIdsArray[iterator.getCount() - 1] = new Integer(documentId.intValue());
            sql.append('?');
            if ( iterator.hasNext() ) {
                sql.append(',');
            }
        }
        sql.append(')');
        return documentIdsArray;
    }

    static void executeWithAppendedIntegerInClause(Database database, String sqlString, Collection documentIds,
                                                   ResultSetHandler resultSetHandler
    ) {
        if (documentIds.isEmpty()) {
            throw new IllegalArgumentException("documentIds is empty") ;
        }
        StringBuffer sql = new StringBuffer(sqlString);
        Integer[] parameters = appendInClause(sql, documentIds);
        database.execute(new SqlQueryCommand(sql.toString(), parameters, resultSetHandler));
    }


    private class DocumentPermissionSetsLoader implements LazilyLoadedObject.Loader {

        private final Integer documentId;
        private final Collection documentIds;
        private boolean forNew;

        DocumentPermissionSetsLoader(Collection documentIds,
                                     boolean forNew, Integer documentId) {
            this.documentIds = documentIds;
            this.forNew = forNew ;
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            if (null == documentsPermissionSets) {
                documentsPermissionSets = new HashMap();
                initDocumentsPermissionSets(documentIds, documentsPermissionSets, false);
            }
            if (null == documentsPermissionSetsForNew ) {
                documentsPermissionSetsForNew = new HashMap();
                initDocumentsPermissionSets(documentIds, documentsPermissionSetsForNew, true);
            }
            Map map = forNew ? documentsPermissionSetsForNew : documentsPermissionSets ;
            DocumentPermissionSets documentPermissionSets = (DocumentPermissionSets) map.get(documentId) ;
            if (null == documentPermissionSets) {
                documentPermissionSets = new DocumentPermissionSets();
            }
            return documentPermissionSets ;
        }

        public void initDocumentsPermissionSets(final Collection documentIds, final Map documentsPermissionSets,
                                                boolean forNew) {

            executeWithAppendedIntegerInClause(DocumentInitializer.this.database,
                                               "SELECT d.meta_id, d.set_id, d.permission_id, de.permission_id, de.permission_data\n"
                                               + "FROM "+(forNew ? "new_": "")+"doc_permission_sets d\n"
                                               + "LEFT JOIN "+(forNew ? "new_": "")+"doc_permission_sets_ex de ON d.meta_id = de.meta_id AND d.set_id = de.set_id\n"
                                               + "WHERE d.meta_id ", documentIds, new ResultSetHandler() {
                public Object handle(ResultSet resultSet) throws SQLException {
                    while ( resultSet.next() ) {
                        Integer documentId = new Integer(resultSet.getInt(1));
                        int setId = resultSet.getInt(2);
                        int permissionSetBits = resultSet.getInt(3);
                        Integer permissionId = Utility.getInteger(resultSet.getObject(4));
                        Integer permissionData = Utility.getInteger(resultSet.getObject(5));

                        DocumentPermissionSets permissionSets = (DocumentPermissionSets) documentsPermissionSets.get(documentId);
                        if ( null == permissionSets ) {
                            permissionSets = new DocumentPermissionSets();
                            documentsPermissionSets.put(documentId, permissionSets);
                        }

                        DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);
                        if ( 0 != permissionSetBits && restricted.isEmpty() ) {
                            restricted.setFromBits(permissionSetBits);
                        }
                        setPermissionData(restricted, permissionId, permissionData);
                    }
                    return null;
                }
            });
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
}
