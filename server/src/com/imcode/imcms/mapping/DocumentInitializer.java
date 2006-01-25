package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.util.CountingIterator;
import imcode.server.ImcmsConstants;
import imcode.server.document.*;
import imcode.server.user.RoleId;
import imcode.util.LazilyLoadedObject;
import imcode.util.Utility;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DocumentInitializer {
    private static final String SQL_GET_KEYWORDS = "SELECT mc.meta_id, c.code FROM classification c JOIN meta_classification mc ON mc.class_id = c.class_id WHERE mc.meta_id ";

    private final DocumentMapper documentMapper;
    public static final String SQL_GET_SECTION_IDS_FOR_DOCUMENT = "SELECT ms.meta_id, ms.section_id\n"
                                                                  + "FROM meta_section ms\n"
                                                                  + "WHERE ms.meta_id ";
    /** Permission to create child documents. * */
    public final static int PERM_CREATE_DOCUMENT = 8;
    private Database database;

    MultiHashMap documentsSectionIds ;
    MultiHashMap documentsKeywords ;
    MultiHashMap documentsCategoryIds ;
    HashMap documentsRolePermissionMappings ;
    HashMap documentsPermissionSets ;
    HashMap documentsPermissionSetsForNew ;

    public DocumentInitializer(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
        this.database = documentMapper.getDatabase();
    }

    void initDocuments(DocumentList documentList) {
        final Map documentMap = documentList.getMap();

        DocumentInitializingVisitor documentInitializingVisitor = new DocumentInitializingVisitor(documentMapper, documentMap.keySet(), documentMapper);
        for ( Iterator iterator = documentList.iterator(); iterator.hasNext(); ) {
            final DocumentDomainObject document = (DocumentDomainObject) iterator.next();
            final Integer documentId = new Integer(document.getId()) ;

            final LazilyLoadedObject permissionSets = new LazilyLoadedObject(new DocumentPermissionSetsLoader(documentMap.keySet(), false, documentId));
            LazilyLoadedObject permissionSetsForNew = new LazilyLoadedObject(new DocumentPermissionSetsLoader(documentMap.keySet(), true, documentId));
            LazilyLoadedObject rolePermissionMappings = new LazilyLoadedObject(new DocumentRolePermissionsLoader(documentMap.keySet(), documentId));
            LazilyLoadedObject sectionIds = new LazilyLoadedObject(new DocumentSectionIdsLoader(documentMap.keySet(), documentId));
            LazilyLoadedObject keywords = new LazilyLoadedObject(new DocumentKeywordsLoader(documentMap.keySet(), documentId));
            LazilyLoadedObject categoryIds = new LazilyLoadedObject(new DocumentCategoryIdsLoader(documentMap.keySet(), documentId));

            document.setLazilyLoadedPermissionSets(permissionSets);
            document.setLazilyLoadedPermissionSetsForNew(permissionSetsForNew);
            document.setLazilyLoadedRoleIdsMappedToDocumentPermissionSetTypes(rolePermissionMappings);
            document.setLazilyLoadedSectionIds(sectionIds);
            document.setLazilyLoadedKeywords(keywords);
            document.setLazilyLoadedCategoryIds(categoryIds);

            document.accept(documentInitializingVisitor);
        }
    }


    static Integer[] appendInClause(StringBuffer sql, Collection documentIds) {
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

    private class DocumentSectionIdsLoader implements LazilyLoadedObject.Loader {

        private final Collection documentIds;
        private final Integer documentId;

        DocumentSectionIdsLoader(Collection documentIds, Integer documentId
        ) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsSectionIds(documentIds);
            Collection sectionIds = (Collection) documentsSectionIds.get(documentId);
            if ( null == sectionIds ) {
                sectionIds = Collections.EMPTY_SET;
            }
            return new CopyableHashSet(sectionIds);
        }

        private void initDocumentsSectionIds(final Collection documentIds) {
            if ( null != documentsSectionIds ) {
                return ;
            }
            documentsSectionIds = new MultiHashMap();
            StringBuffer sql = new StringBuffer(SQL_GET_SECTION_IDS_FOR_DOCUMENT);
            Integer[] parameters = appendInClause(sql, documentIds);
            database.execute(new SqlQueryCommand(sql.toString(), parameters, new ResultSetHandler() {
                public Object handle(ResultSet rs) throws SQLException {
                    while ( rs.next() ) {
                        int documentId = rs.getInt(1);
                        int sectionId = rs.getInt(2);
                        documentsSectionIds.put(new Integer(documentId), new Integer(sectionId));
                    }
                    return null;
                }
            }));
        }

    }

    private class DocumentKeywordsLoader implements LazilyLoadedObject.Loader {

        private final Collection documentIds;
        private final Integer documentId;

        DocumentKeywordsLoader(Collection documentIds, Integer documentId) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
                initDocumentsKeywords(documentIds);
            Collection documentKeywords = (Collection) documentsKeywords.get(documentId);
            if ( null == documentKeywords ) {
                documentKeywords = Collections.EMPTY_SET;
            }
            return new CopyableHashSet(documentKeywords);
        }

        private void initDocumentsKeywords(Collection documentIds) {
            if ( null != documentsKeywords ) {
                return ;
            }
            documentsKeywords = new MultiHashMap();
            StringBuffer sql = new StringBuffer(SQL_GET_KEYWORDS);
            Integer[] parameters = appendInClause(sql, documentIds);
            database.execute(new SqlQueryCommand(sql.toString(), parameters, new ResultSetHandler() {
                public Object handle(ResultSet rs) throws SQLException {
                    while ( rs.next() ) {
                        int documentId = rs.getInt(1);
                        String keyword = rs.getString(2);
                        documentsKeywords.put(new Integer(documentId), keyword);
                    }
                    return null;
                }
            }));
        }
    }

    private class DocumentCategoryIdsLoader implements LazilyLoadedObject.Loader {

        private final Collection documentIds;
        private final Integer documentId;

        DocumentCategoryIdsLoader(Collection documentIds, Integer documentId) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsCategoryIds(documentIds);
            Collection documentCategoryIds = documentsCategoryIds.getCollection(documentId);
            if ( null == documentCategoryIds ) {
                documentCategoryIds = Collections.EMPTY_SET;
            }
            return new CopyableHashSet(documentCategoryIds);
        }

        private void initDocumentsCategoryIds(Collection documentIds) {
            if (null != documentsCategoryIds) {
                return ;
            }
            documentsCategoryIds = new MultiHashMap();
            StringBuffer sql = new StringBuffer(CategoryMapper.SQL__GET_DOCUMENT_CATEGORIES);
            Integer[] parameters = appendInClause(sql, documentIds);
            database.execute(new SqlQueryCommand(sql.toString(), parameters, new ResultSetHandler() {
                public Object handle(ResultSet rs) throws SQLException {
                    while ( rs.next() ) {
                        int documentId = rs.getInt(1);
                        int categoryId = rs.getInt(2);
                        documentsCategoryIds.put(new Integer(documentId), new Integer(categoryId));
                    }
                    return null;
                }
            }));
        }
    }

    private class DocumentRolePermissionsLoader implements LazilyLoadedObject.Loader {

        private final Collection documentIds;
        private final Integer documentId;

        DocumentRolePermissionsLoader(Collection documentIds,
                                             Integer documentId) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsRolePermissionMappings(documentIds);
            RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings = (RoleIdToDocumentPermissionSetTypeMappings) documentsRolePermissionMappings.get(documentId);
            if ( null == rolePermissionMappings ) {
                rolePermissionMappings = new RoleIdToDocumentPermissionSetTypeMappings();
            }
            return rolePermissionMappings;
        }

        public void initDocumentsRolePermissionMappings(Collection documentIds) {
            if ( null != documentsRolePermissionMappings ) {
                return;
            }
            documentsRolePermissionMappings = new HashMap();
            
            StringBuffer sql = new StringBuffer("SELECT "
                                                + "meta_id, role_id, set_id\n"
                                                + "FROM  roles_rights\n"
                                                + "WHERE meta_id ");
            Integer[] parameters = appendInClause(sql, documentIds);

            database.execute(new SqlQueryCommand(sql.toString(), parameters, new ResultSetHandler() {
                public Object handle(ResultSet rs) throws SQLException {
                    while ( rs.next() ) {
                        Integer documentId = new Integer(rs.getInt(1));
                        int roleId = rs.getInt(2);
                        int setId = rs.getInt(3);
                        RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings = (RoleIdToDocumentPermissionSetTypeMappings) documentsRolePermissionMappings.get(documentId);
                        if ( null == rolePermissionMappings ) {
                            rolePermissionMappings = new RoleIdToDocumentPermissionSetTypeMappings();
                            documentsRolePermissionMappings.put(documentId, rolePermissionMappings);
                        }
                        rolePermissionMappings.setPermissionSetTypeForRole(new RoleId(roleId), DocumentPermissionSetTypeDomainObject.fromInt(setId));
                    }
                    return null;
                }
            }));

        }
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

            StringBuffer sql = new StringBuffer(
                    "SELECT d.meta_id, d.set_id, d.permission_id, de.permission_id, de.permission_data\n"
                    + "FROM "+(forNew ? "new_": "")+"doc_permission_sets d\n"
                    + "LEFT JOIN "+(forNew ? "new_": "")+"doc_permission_sets_ex de ON d.meta_id = de.meta_id AND d.set_id = de.set_id\n"
                    + "WHERE d.meta_id ");
            Integer[] parameters = appendInClause(sql, documentIds);
            database.execute(new SqlQueryCommand(sql.toString(), parameters, new ResultSetHandler() {
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
            }));
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
