package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.util.CountingIterator;
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
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.dbutils.ResultSetHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DocumentInitializer {
    public static final String SQL_GET_SECTION_IDS_FOR_DOCUMENT = "SELECT ms.meta_id, ms.section_id\n"
            + "FROM meta_section ms\n"
            + "WHERE ms.meta_id ";
    /**
     * Permission to create child documents. *
     */
    public final static int PERM_CREATE_DOCUMENT = 8;
    private static final String SQL_GET_KEYWORDS = "SELECT mc.meta_id, c.code FROM classification c JOIN meta_classification mc ON mc.class_id = c.class_id WHERE mc.meta_id ";
    private static final String SQL_GET_DOCUMENT_PROPERTIES = "SELECT key_name, value FROM document_properties WHERE meta_id=?";
    private final DocumentMapper documentMapper;
    private MultiValuedMap<Integer, Integer> documentsSectionIds;
    private MultiValuedMap<Integer, String> documentsKeywords;
    private MultiValuedMap<Integer, Integer> documentsCategoryIds;
    private HashMap<Integer, Map<String, String>> documentsProperties;
    private HashMap<Integer, RoleIdToDocumentPermissionSetTypeMappings> documentsRolePermissionMappings;
    private HashMap<Integer, DocumentPermissionSets> documentsPermissionSets;
    private HashMap<Integer, DocumentPermissionSets> documentsPermissionSetsForNew;
    private Database database;

    public DocumentInitializer(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
        this.database = documentMapper.getDatabase();
    }

    private static Integer[] appendInClause(StringBuffer sql, Collection<Integer> documentIds) {
        sql.append("IN (");
        Integer[] documentIdsArray = new Integer[documentIds.size()];
        for (CountingIterator<Integer> iterator = new CountingIterator<>(documentIds.iterator()); iterator.hasNext(); )
        {
            Integer documentId = iterator.next();
            documentIdsArray[iterator.getCount() - 1] = documentId;
            sql.append('?');
            if (iterator.hasNext()) {
                sql.append(',');
            }
        }
        sql.append(')');
        return documentIdsArray;
    }

    static <T> void executeWithAppendedIntegerInClause(Database database, String sqlString, Collection<Integer> documentIds,
                                                       ResultSetHandler<T> resultSetHandler
    ) {
        if (documentIds.isEmpty()) {
            throw new IllegalArgumentException("documentIds is empty");
        }
        StringBuffer sql = new StringBuffer(sqlString);
        Integer[] parameters = appendInClause(sql, documentIds);
        database.execute(new SqlQueryCommand<>(sql.toString(), parameters, resultSetHandler));
    }

    void initDocuments(DocumentList documentList) {
        Set<Integer> documentIds = documentList.getMap().keySet();

        DocumentInitializingVisitor documentInitializingVisitor = new DocumentInitializingVisitor(documentMapper, documentIds, documentMapper);
        for (final DocumentDomainObject document : documentList) {
            final Integer documentId = document.getId();

            document.setLazilyLoadedPermissionSets(new LazilyLoadedObject<>(new DocumentPermissionSetsLoader(documentIds, false, documentId)));
            document.setLazilyLoadedPermissionSetsForNew(new LazilyLoadedObject<>(new DocumentPermissionSetsLoader(documentIds, true, documentId)));
            document.setLazilyLoadedRoleIdsMappedToDocumentPermissionSetTypes(new LazilyLoadedObject<>(new DocumentRolePermissionsLoader(documentIds, documentId)));
            document.setLazilyLoadedSectionIds(new LazilyLoadedObject<>(new DocumentSectionIdsLoader(documentIds, documentId)));
            document.setLazilyLoadedKeywords(new LazilyLoadedObject<>(new DocumentKeywordsLoader(documentIds, documentId)));
            document.setLazilyLoadedCategoryIds(new LazilyLoadedObject<>(new DocumentCategoryIdsLoader(documentIds, documentId)));
            document.setLazilyLoadedProperties(new LazilyLoadedObject<>(new DocumentPropertiesLoader(documentIds, documentId)));

            document.accept(documentInitializingVisitor);
        }
    }

    private class DocumentSectionIdsLoader implements LazilyLoadedObject.Loader<CopyableHashSet<Integer>> {

        private final Collection<Integer> documentIds;
        private final Integer documentId;

        DocumentSectionIdsLoader(Collection<Integer> documentIds, Integer documentId
        ) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public CopyableHashSet<Integer> load() {
            initDocumentsSectionIds(documentIds);
            Collection<Integer> sectionIds = documentsSectionIds.get(documentId);
            if (null == sectionIds) {
                sectionIds = Collections.emptyList();
            }
            return new CopyableHashSet<>(sectionIds);
        }

        private void initDocumentsSectionIds(final Collection<Integer> documentIds) {
            if (null != documentsSectionIds) {
                return;
            }
            documentsSectionIds = new ArrayListValuedHashMap<>();
            executeWithAppendedIntegerInClause(DocumentInitializer.this.database, SQL_GET_SECTION_IDS_FOR_DOCUMENT, documentIds, rs -> {
                while (rs.next()) {
                    int documentId = rs.getInt(1);
                    int sectionId = rs.getInt(2);
                    documentsSectionIds.put(documentId, sectionId);
                }
                return null;
            });
        }

    }

    private class DocumentKeywordsLoader implements LazilyLoadedObject.Loader<CopyableHashSet<String>> {

        private final Collection<Integer> documentIds;
        private final Integer documentId;

        DocumentKeywordsLoader(Collection<Integer> documentIds, Integer documentId) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public CopyableHashSet<String> load() {
            initDocumentsKeywords(documentIds);
            Collection<String> documentKeywords = documentsKeywords.get(documentId);
            if (null == documentKeywords) {
                documentKeywords = Collections.emptyList();
            }
            return new CopyableHashSet<>(documentKeywords);
        }

        private void initDocumentsKeywords(Collection<Integer> documentIds) {
            if (null != documentsKeywords) {
                return;
            }
            documentsKeywords = new ArrayListValuedHashMap<>();
            executeWithAppendedIntegerInClause(DocumentInitializer.this.database, SQL_GET_KEYWORDS, documentIds, rs -> {
                while (rs.next()) {
                    int documentId = rs.getInt(1);
                    String keyword = rs.getString(2);
                    documentsKeywords.put(documentId, keyword);
                }
                return null;
            });
        }
    }

    private class DocumentPropertiesLoader implements LazilyLoadedObject.Loader<CopyableHashMap<String, String>> {

        private final Collection documentIds;
        private final Integer documentId;

        DocumentPropertiesLoader(Collection documentIds, Integer documentId) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public CopyableHashMap<String, String> load() {
            initDocumentsProperties(documentIds);
            Map<String, String> documentProperties = documentsProperties.get(documentId);
            if (null == documentProperties) {
                documentProperties = new HashMap<>();
            }
            return new CopyableHashMap<>(documentProperties);
        }

        private void initDocumentsProperties(Collection documentIds) {
            if (null != documentsProperties) {
                return;
            }
            documentsProperties = new HashMap<>();
            StringBuilder sql = new StringBuilder(SQL_GET_DOCUMENT_PROPERTIES);
            for (Object documentId1 : documentIds) {
                final Integer documentId = (Integer) documentId1;
                database.execute(new SqlQueryCommand<>(sql.toString(), new String[]{documentId + ""}, rs -> {
                    Map<String, String> properties = new HashMap<>(rs.getFetchSize());
                    while (rs.next()) {
                        String keyName = rs.getString(1);
                        String value = rs.getString(2);
                        properties.put(keyName, value);
                    }
                    documentsProperties.put(documentId, properties);
                    return null;
                }));
            }
        }

    }

    private class DocumentCategoryIdsLoader implements LazilyLoadedObject.Loader<CopyableHashSet<Integer>> {

        private final Collection<Integer> documentIds;
        private final Integer documentId;

        DocumentCategoryIdsLoader(Collection<Integer> documentIds, Integer documentId) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public CopyableHashSet<Integer> load() {
            initDocumentsCategoryIds(documentIds);
            Collection<Integer> documentCategoryIds = documentsCategoryIds.get(documentId);
            if (null == documentCategoryIds) {
                documentCategoryIds = Collections.emptyList();
            }
            return new CopyableHashSet<>(documentCategoryIds);
        }

        private void initDocumentsCategoryIds(Collection<Integer> documentIds) {
            if (null != documentsCategoryIds) {
                return;
            }
            documentsCategoryIds = new ArrayListValuedHashMap<>();
            executeWithAppendedIntegerInClause(DocumentInitializer.this.database, CategoryMapper.SQL__GET_DOCUMENT_CATEGORIES, documentIds, rs -> {
                while (rs.next()) {
                    int documentId = rs.getInt(1);
                    int categoryId = rs.getInt(2);
                    documentsCategoryIds.put(documentId, categoryId);
                }
                return null;
            });
        }

    }

    private class DocumentRolePermissionsLoader implements LazilyLoadedObject.Loader<RoleIdToDocumentPermissionSetTypeMappings> {

        private final Collection<Integer> documentIds;
        private final Integer documentId;

        DocumentRolePermissionsLoader(Collection<Integer> documentIds,
                                      Integer documentId) {
            this.documentIds = documentIds;
            this.documentId = documentId;
        }

        public RoleIdToDocumentPermissionSetTypeMappings load() {
            initDocumentsRolePermissionMappings(documentIds);
            RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings = documentsRolePermissionMappings.get(documentId);
            if (null == rolePermissionMappings) {
                rolePermissionMappings = new RoleIdToDocumentPermissionSetTypeMappings();
            }
            return rolePermissionMappings;
        }

        public void initDocumentsRolePermissionMappings(Collection<Integer> documentIds) {
            if (null != documentsRolePermissionMappings) {
                return;
            }
            documentsRolePermissionMappings = new HashMap<>();

            executeWithAppendedIntegerInClause(database, "SELECT "
                    + "meta_id, role_id, set_id\n"
                    + "FROM  roles_rights\n"
                    + "WHERE meta_id ", documentIds, rs -> {
                while (rs.next()) {
                    Integer documentId = rs.getInt(1);
                    int roleId = rs.getInt(2);
                    int setId = rs.getInt(3);
                    RoleIdToDocumentPermissionSetTypeMappings rolePermissionMappings = documentsRolePermissionMappings.get(documentId);
                    if (null == rolePermissionMappings) {
                        rolePermissionMappings = new RoleIdToDocumentPermissionSetTypeMappings();
                        documentsRolePermissionMappings.put(documentId, rolePermissionMappings);
                    }
                    rolePermissionMappings.setPermissionSetTypeForRole(new RoleId(roleId), DocumentPermissionSetTypeDomainObject.fromInt(setId));
                }
                return null;
            });

        }
    }

    private class DocumentPermissionSetsLoader implements LazilyLoadedObject.Loader<DocumentPermissionSets> {

        private final Integer documentId;
        private final Collection<Integer> documentIds;
        private boolean forNew;

        DocumentPermissionSetsLoader(Collection<Integer> documentIds,
                                     boolean forNew, Integer documentId) {
            this.documentIds = documentIds;
            this.forNew = forNew;
            this.documentId = documentId;
        }

        public DocumentPermissionSets load() {
            if (null == documentsPermissionSets) {
                documentsPermissionSets = new HashMap<>();
                initDocumentsPermissionSets(documentIds, documentsPermissionSets, false);
            }
            if (null == documentsPermissionSetsForNew) {
                documentsPermissionSetsForNew = new HashMap<>();
                initDocumentsPermissionSets(documentIds, documentsPermissionSetsForNew, true);
            }
            Map map = forNew ? documentsPermissionSetsForNew : documentsPermissionSets;
            DocumentPermissionSets documentPermissionSets = (DocumentPermissionSets) map.get(documentId);
            if (null == documentPermissionSets) {
                documentPermissionSets = new DocumentPermissionSets();
            }
            return documentPermissionSets;
        }

        public void initDocumentsPermissionSets(final Collection<Integer> documentIds, final Map<Integer, DocumentPermissionSets> documentsPermissionSets,
                                                boolean forNew) {

            executeWithAppendedIntegerInClause(DocumentInitializer.this.database,
                    "SELECT d.meta_id, d.set_id, d.permission_id, de.permission_id, de.permission_data\n"
                            + "FROM " + (forNew ? "new_" : "") + "doc_permission_sets d\n"
                            + "LEFT JOIN " + (forNew ? "new_" : "") + "doc_permission_sets_ex de ON d.meta_id = de.meta_id AND d.set_id = de.set_id\n"
                            + "WHERE d.meta_id ", documentIds, resultSet -> {
                        while (resultSet.next()) {
                            Integer documentId = resultSet.getInt(1);
                            int setId = resultSet.getInt(2);
                            int permissionSetBits = resultSet.getInt(3);
                            Integer permissionId = Utility.getInteger(resultSet.getObject(4));
                            Integer permissionData = Utility.getInteger(resultSet.getObject(5));

                            DocumentPermissionSets permissionSets = documentsPermissionSets.get(documentId);
                            if (null == permissionSets) {
                                permissionSets = new DocumentPermissionSets();
                                documentsPermissionSets.put(documentId, permissionSets);
                            }

                            DocumentPermissionSetDomainObject restricted = permissionSets.getRestricted(setId);
                            if (0 != permissionSetBits && restricted.isEmpty()) {
                                restricted.setFromBits(permissionSetBits);
                            }
                            setPermissionData(restricted, permissionId, permissionData);
                        }
                        return null;
                    });
        }

        private void setPermissionData(DocumentPermissionSetDomainObject permissionSet, Integer permissionId, Integer permissionData) {
            if (null != permissionId) {
                TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) permissionSet;
                switch (permissionId) {
                    case PERM_CREATE_DOCUMENT:
                        textDocumentPermissionSet.addAllowedDocumentTypeId(permissionData);
                        break;
                    case ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE:
                        textDocumentPermissionSet.addAllowedTemplateGroupId(permissionData);
                        break;
                    default:
                }
            }
        }

    }
}
