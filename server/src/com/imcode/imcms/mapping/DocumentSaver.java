package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.SingleConnectionDatabase;
import com.imcode.db.commands.*;
import com.imcode.imcms.api.Document;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

class DocumentSaver {

    public static final String SQL_DELETE_ROLE_DOCUMENT_PERMISSION_SET_ID = "DELETE FROM roles_rights WHERE role_id = ? AND meta_id = ?";
    public static final String SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID = "INSERT INTO roles_rights (role_id, meta_id, set_id) VALUES(?,?,?)";
    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;
    private final DocumentMapper documentMapper;

    Logger log = LogManager.getLogger(DocumentSaver.class);

    DocumentSaver(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
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

    void saveDocument(final DocumentDomainObject document, final DocumentDomainObject oldDocument,
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
            document.setProperty(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_MODIFIED_BY, "" + user.getId());

            getDatabase().execute(new TransactionDatabaseCommand() {
                public Object executeInTransaction(DatabaseConnection connection) throws DatabaseException {
                    final SingleConnectionDatabase database = new SingleConnectionDatabase(connection);

                    sqlUpdateMeta(document, database);

                    updateDocumentSectionsCategoriesKeywords(document, oldDocument, database);

                    updateDocumentProperties(document, oldDocument, database);

                    if (user.canEditPermissionsFor(oldDocument)) {
                        updateDocumentRolePermissions(document, user, oldDocument, database);

                        new DocumentPermissionSetMapper(database).saveRestrictedDocumentPermissionSets(document, user, oldDocument);
                    }

                    return null;
                }
            });

            document.accept(new DocumentSavingVisitor(oldDocument, getDatabase(), documentMapper.getImcmsServices(), user));

        } catch (Exception e) {
            log.error("Exception while saving a document", e);
        } finally {
            documentMapper.invalidateDocument(document);
        }
    }

    private void sqlUpdateMeta(DocumentDomainObject document) {
        sqlUpdateMeta(document, getDatabase());
    }

    private void sqlUpdateMeta(DocumentDomainObject document, Database database) {
        String headline = document.getHeadline();
        String text = document.getMenuText();

        StringBuffer sqlStr = new StringBuffer("update meta set ");

        ArrayList sqlUpdateColumns = new ArrayList();
        ArrayList sqlUpdateValues = new ArrayList();

        makeDateSqlUpdateClause("publication_start_datetime", document.getPublicationStartDatetime(), sqlUpdateColumns, sqlUpdateValues);
        makeDateSqlUpdateClause("publication_end_datetime", document.getPublicationEndDatetime(), sqlUpdateColumns, sqlUpdateValues);
        makeDateSqlUpdateClause("archived_datetime", document.getArchivedDatetime(), sqlUpdateColumns, sqlUpdateValues);
        makeDateSqlUpdateClause("date_created", document.getCreatedDatetime(), sqlUpdateColumns, sqlUpdateValues);
        String headlineThatFitsInDB = headline.substring(0, Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
        makeStringSqlUpdateClause("meta_headline", headlineThatFitsInDB, sqlUpdateColumns, sqlUpdateValues);
        makeStringSqlUpdateClause("meta_image", document.getMenuImage(), sqlUpdateColumns, sqlUpdateValues);
        makeDateSqlUpdateClause("date_modified", document.getModifiedDatetime(), sqlUpdateColumns, sqlUpdateValues);
        makeStringSqlUpdateClause("target", document.getTarget(), sqlUpdateColumns, sqlUpdateValues);
        String textThatFitsInDB = text.substring(0, Math.min(text.length(), META_TEXT_MAX_LENGTH - 1));
        makeStringSqlUpdateClause("meta_text", textThatFitsInDB, sqlUpdateColumns, sqlUpdateValues);
        makeStringSqlUpdateClause("lang_prefix", document.getLanguageIso639_2(), sqlUpdateColumns, sqlUpdateValues);
        makeBooleanSqlUpdateClause("disable_search", document.isSearchDisabled(), sqlUpdateColumns, sqlUpdateValues);
        makeBooleanSqlUpdateClause("shared", document.isLinkableByOtherUsers(), sqlUpdateColumns, sqlUpdateValues);
        makeBooleanSqlUpdateClause("show_meta", document.isLinkedForUnauthorizedUsers(), sqlUpdateColumns, sqlUpdateValues);
        makeBooleanSqlUpdateClause("permissions", document.isRestrictedOneMorePrivilegedThanRestrictedTwo(), sqlUpdateColumns, sqlUpdateValues);
	    makeBooleanSqlUpdateClause("export_allowed", document.isExportAllowed(), sqlUpdateColumns, sqlUpdateValues);
	    makeBooleanSqlUpdateClause("exported", document.isExported(), sqlUpdateColumns, sqlUpdateValues);
        makeIntSqlUpdateClause("publisher_id", document.getPublisherId(), sqlUpdateColumns,
                sqlUpdateValues);
        makeIntSqlUpdateClause("owner_id", document.getCreatorId(), sqlUpdateColumns,
                sqlUpdateValues);
        Document.PublicationStatus publicationStatus = document.getPublicationStatus();
        makeIntSqlUpdateClause("status", publicationStatus.hashCode(), sqlUpdateColumns, sqlUpdateValues);

        sqlStr.append(StringUtils.join(sqlUpdateColumns.iterator(), ","));
        sqlStr.append(" where meta_id = ?");
        sqlUpdateValues.add("" + document.getId());
        String[] params = (String[]) sqlUpdateValues.toArray(new String[0]);
        database.execute(new SqlUpdateCommand(sqlStr.toString(), params));
    }

    private void updateDocumentSectionsCategoriesKeywords(DocumentDomainObject document, DocumentDomainObject oldDocument, Database database) {
        if(!oldDocument.getSectionIds().equals(document.getSectionIds()))
            updateDocumentSections(document.getId(), document.getSectionIds(), database);

        if(!oldDocument.getCategoryIds().equals(document.getCategoryIds()))
            new CategoryMapper(database).updateDocumentCategories(document);

        if(!oldDocument.getKeywords().equals(document.getKeywords()))
            updateDocumentKeywords(document, database);
    }

    private void updateDocumentSectionsCategoriesKeywords(DocumentDomainObject document, Database database) {
        updateDocumentSections(document.getId(), document.getSectionIds(), database);

        new CategoryMapper(database).updateDocumentCategories(document);

        updateDocumentKeywords(document, database);
    }

    private void updateDocumentSectionsCategoriesKeywords(DocumentDomainObject document) {
        updateDocumentSections(document.getId(), document.getSectionIds());

        new CategoryMapper(getDatabase()).updateDocumentCategories(document);

        updateDocumentKeywords(document);
    }

    private Database getDatabase() {
        return documentMapper.getDatabase();
    }

    void saveNewDocument(UserDomainObject user,
                         DocumentDomainObject document, boolean copying) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        checkDocumentForSave(document);

        document.loadAllLazilyLoaded();

        documentMapper.setCreatedAndModifiedDatetimes(document, new Date());

        int newMetaId = sqlInsertIntoMeta(document);

        boolean inheritRestrictedPermissions = !user.isSuperAdminOrHasFullPermissionOn(document) && !copying;
        if (inheritRestrictedPermissions) {
            document.getPermissionSets().setRestricted1(document.getPermissionSetsForNewDocuments().getRestricted1());
            document.getPermissionSets().setRestricted2(document.getPermissionSetsForNewDocuments().getRestricted2());
        }

        document.setId(newMetaId);

        updateDocumentSectionsCategoriesKeywords(document);

        updateDocumentProperties(document);

        updateDocumentRolePermissions(document, user, null);

        documentMapper.getDocumentPermissionSetMapper().saveRestrictedDocumentPermissionSets(document, user, null);

        document.accept(new DocumentCreatingVisitor(getDatabase(), documentMapper.getImcmsServices(), user));

        documentMapper.invalidateDocument(document);
    }

    private void checkDocumentForSave(DocumentDomainObject document) throws NoPermissionInternalException, DocumentSaveException {

        documentMapper.getCategoryMapper().checkMaxDocumentCategoriesOfType(document);
        checkIfAliasAlreadyExist(document);

    }

    void updateDocumentRolePermissions(DocumentDomainObject document, UserDomainObject user,
                                       DocumentDomainObject oldDocument) {
        updateDocumentRolePermissions(document, user, oldDocument, getDatabase());
    }

    void updateDocumentRolePermissions(DocumentDomainObject document, UserDomainObject user,
                                       DocumentDomainObject oldDocument, Database database) {
        RoleIdToDocumentPermissionSetTypeMappings mappings = new RoleIdToDocumentPermissionSetTypeMappings();

        if (null != oldDocument) {
            RoleIdToDocumentPermissionSetTypeMappings.Mapping[] oldDocumentMappings = oldDocument.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings();
            for (int i = 0; i < oldDocumentMappings.length; i++) {
                RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = oldDocumentMappings[i];
                mappings.setPermissionSetTypeForRole(mapping.getRoleId(), DocumentPermissionSetTypeDomainObject.NONE);
            }
        }

        RoleIdToDocumentPermissionSetTypeMappings.Mapping[] documentMappings = document.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings();
        for (int i = 0; i < documentMappings.length; i++) {
            RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = documentMappings[i];
            mappings.setPermissionSetTypeForRole(mapping.getRoleId(), mapping.getDocumentPermissionSetType());
        }

        RoleIdToDocumentPermissionSetTypeMappings.Mapping[] mappingsArray = mappings.getMappings();
        for (int i = 0; i < mappingsArray.length; i++) {
            RoleIdToDocumentPermissionSetTypeMappings.Mapping mapping = mappingsArray[i];
            RoleId roleId = mapping.getRoleId();
            DocumentPermissionSetTypeDomainObject documentPermissionSetType = mapping.getDocumentPermissionSetType();

            if (null == oldDocument
                    || user.canSetDocumentPermissionSetTypeForRoleIdOnDocument(documentPermissionSetType, roleId, oldDocument)) {
                String[] params1 = new String[]{"" + roleId,
                        "" + document.getId()};
                database.execute(new SqlUpdateCommand(SQL_DELETE_ROLE_DOCUMENT_PERMISSION_SET_ID, params1));
                if (!DocumentPermissionSetTypeDomainObject.NONE.equals(documentPermissionSetType)) {
                    String[] params = new String[]{
                            "" + roleId.intValue(), "" + document.getId(), "" + documentPermissionSetType};
                    database.execute(new SqlUpdateCommand(SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID, params));
                }
            }
        }
    }

    private int sqlInsertIntoMeta(DocumentDomainObject document) {

        final Number documentId = getDatabase().execute(new InsertIntoTableDatabaseCommand("meta", new String[][]{
                {"doc_type", document.getDocumentTypeId() + ""},
                {"meta_headline", document.getHeadline()},
                {"meta_text", document.getMenuText()},
                {"meta_image", document.getMenuImage()},
                {"owner_id", document.getCreatorId() + ""},
                {"permissions", makeSqlStringFromBoolean(document.isRestrictedOneMorePrivilegedThanRestrictedTwo())},
                {"shared", makeSqlStringFromBoolean(document.isLinkableByOtherUsers())},
                {"show_meta", makeSqlStringFromBoolean(document.isLinkedForUnauthorizedUsers())},
                {"lang_prefix", document.getLanguageIso639_2()},
                {"date_created", Utility.makeSqlStringFromDate(document.getCreatedDatetime())},
                {"date_modified", Utility.makeSqlStringFromDate(document.getModifiedDatetime())},
                {"disable_search", makeSqlStringFromBoolean(document.isSearchDisabled())},
                {"target", document.getTarget()},
                {"activate", "1"},
                {"archived_datetime", Utility.makeSqlStringFromDate(document.getArchivedDatetime())},
                {"publisher_id", null != document.getPublisherId() ? document.getPublisherId() + "" : null},
                {"status", "" + document.getPublicationStatus()},
                {"publication_start_datetime", Utility.makeSqlStringFromDate(document.getPublicationStartDatetime())},
                {"publication_end_datetime", Utility.makeSqlStringFromDate(document.getPublicationEndDatetime())}
        }));
        return documentId.intValue();
    }

    private String makeSqlStringFromBoolean(final boolean bool) {
        return bool ? "1" : "0";
    }

    Set getDocumentsAddedWithoutPermission(TextDocumentDomainObject textDocument,
                                           TextDocumentDomainObject oldTextDocument,
                                           final UserDomainObject user, DocumentGetter documentGetter) {
        Set documentIdsAdded = getDocumentIdsAdded(textDocument, oldTextDocument);
        List documents = documentGetter.getDocuments(documentIdsAdded);
        Collection documentsAddedWithoutPermission = CollectionUtils.select(documents, new Predicate() {
            public boolean evaluate(Object object) {
                return !user.canAddDocumentToAnyMenu((DocumentDomainObject) object);
            }
        });
        return new HashSet(documentsAddedWithoutPermission);
    }

    private Set getDocumentIdsAdded(TextDocumentDomainObject textDocument, TextDocumentDomainObject oldTextDocument
    ) {
        Set documentIdsAdded;
        if (null != oldTextDocument) {
            documentIdsAdded = getChildDocumentIdsDifference(textDocument, oldTextDocument);
        } else {
            documentIdsAdded = textDocument.getChildDocumentIds();
        }
        return documentIdsAdded;
    }

    private Set getChildDocumentIdsDifference(TextDocumentDomainObject minuend, TextDocumentDomainObject subtrahend) {
        Set minuendChildDocumentIds = minuend.getChildDocumentIds();
        Set subtrahendChildDocumentIds = subtrahend.getChildDocumentIds();
        Set result = new HashSet(minuendChildDocumentIds);
        result.removeAll(subtrahendChildDocumentIds);
        return result;
    }

    void updateDocumentKeywords(DocumentDomainObject document) {
        int meta_id = document.getId();
        Set keywords = document.getKeywords();
        Set allKeywords = new HashSet(Arrays.asList(documentMapper.getAllKeywords()));
        deleteKeywordsFromDocument(meta_id);
        for (Iterator iterator = keywords.iterator(); iterator.hasNext(); ) {
            String keyword = (String) iterator.next();
            final boolean keywordExists = allKeywords.contains(keyword);
            if (!keywordExists) {
                addKeyword(keyword);
            }
            addExistingKeywordToDocument(meta_id, keyword);
        }
        deleteUnusedKeywords();
    }

    void updateDocumentKeywords(DocumentDomainObject document, Database database) {
        int meta_id = document.getId();
        Set keywords = document.getKeywords();
        Set allKeywords = new HashSet(Arrays.asList(documentMapper.getAllKeywords(database)));
        deleteKeywordsFromDocument(meta_id, database);
        for (Iterator iterator = keywords.iterator(); iterator.hasNext(); ) {
            String keyword = (String) iterator.next();
            final boolean keywordExists = allKeywords.contains(keyword);
            if (!keywordExists) {
                addKeyword(keyword, database);
            }
            addExistingKeywordToDocument(meta_id, keyword, database);
        }
        deleteUnusedKeywords(database);
    }

    void updateDocumentProperties(DocumentDomainObject document) {
        int meta_id = document.getId();
        Map properties = document.getProperties();
        deletePropertiesFromDocument(meta_id);
        for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String[] params = new String[]{meta_id + "", key, (String) properties.get(key)};
            getDatabase().execute(new SqlUpdateCommand("INSERT INTO document_properties (meta_id, key_name, value) VALUES(?,?,?)", params));
        }
    }

    void updateDocumentProperties(DocumentDomainObject document, Database database) {
        int meta_id = document.getId();
        Map properties = document.getProperties();
        deletePropertiesFromDocument(meta_id, database);
        for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String[] params = new String[]{meta_id + "", key, (String) properties.get(key)};
            database.execute(new SqlUpdateCommand("INSERT INTO document_properties (meta_id, key_name, value) VALUES(?,?,?)", params));
        }
    }

    void updateDocumentProperties(DocumentDomainObject document, DocumentDomainObject oldDocument, Database database) {
        if(!oldDocument.getProperties().equals(document.getProperties()))
            updateDocumentProperties(document, database);
    }

    private void deletePropertiesFromDocument(int meta_id) {
        deletePropertiesFromDocument(meta_id, getDatabase());
    }

    private void deletePropertiesFromDocument(int meta_id, Database database) {
        String[] params = new String[]{meta_id + ""};
        database.execute(new SqlUpdateCommand("DELETE FROM document_properties WHERE meta_id = ?", params));
    }

    void updateDocumentSections(int metaId,
                                Set sectionIds) {
        removeAllSectionsFromDocument(metaId);
        for (Object sectionId1 : sectionIds) {
            Integer sectionId = (Integer) sectionId1;
            addSectionIdToDocument(metaId, sectionId);
        }
    }

    void updateDocumentSections(int metaId,
                                Set sectionIds, Database database) {
        removeAllSectionsFromDocument(metaId, database);
        for (Object sectionId1 : sectionIds) {
            Integer sectionId = (Integer) sectionId1;
            addSectionIdToDocument(metaId, sectionId, database);
        }
    }

    private void addSectionIdToDocument(int metaId, Integer sectionId) {
        addSectionIdToDocument(metaId, sectionId, getDatabase());
    }

    private void addSectionIdToDocument(int metaId, Integer sectionId, Database database) {
        Integer[] params = new Integer[]{metaId, sectionId};
        database.execute(new SqlUpdateDatabaseCommand("INSERT INTO meta_section VALUES(?,?)", params));
    }

    private void deleteKeywordsFromDocument(int meta_id) {
        deleteKeywordsFromDocument(meta_id, getDatabase());
    }

    private void deleteKeywordsFromDocument(int meta_id, Database database) {
        String sqlDeleteKeywordsFromDocument = "DELETE FROM meta_classification WHERE meta_id = ?";
        String[] params = new String[]{"" + meta_id};
        database.execute(new SqlUpdateCommand(sqlDeleteKeywordsFromDocument, params));
    }

    private void deleteUnusedKeywords() {
        deleteUnusedKeywords(getDatabase());
    }

    private void deleteUnusedKeywords(Database database) {
        String[] params = new String[0];
        database.execute(new SqlUpdateCommand("DELETE FROM classification WHERE class_id NOT IN (SELECT class_id FROM meta_classification)", params));
    }

    private void addKeyword(String keyword) {
        addKeyword(keyword, getDatabase());
    }

    private void addKeyword(String keyword, Database database) {
        String[] params = new String[]{keyword};
        database.execute(new SqlUpdateCommand("INSERT INTO classification (code) VALUES(?)", params));
    }

    private void removeAllSectionsFromDocument(int metaId) {
        removeAllSectionsFromDocument(metaId, getDatabase());
    }

    private void removeAllSectionsFromDocument(int metaId, Database database) {
        String[] params = new String[]{"" + metaId};
        database.execute(new SqlUpdateCommand("DELETE FROM meta_section WHERE meta_id = ?", params));
    }

    private void addExistingKeywordToDocument(int meta_id, String keyword) {
        addExistingKeywordToDocument(meta_id, keyword, getDatabase());
    }

    private void addExistingKeywordToDocument(int meta_id, String keyword, Database database) {
        String[] params1 = new String[]{
                keyword
        };
        int keywordId = Integer.parseInt(getDatabase().execute(new SqlQueryCommand<>("SELECT class_id FROM classification WHERE code = ?", params1, Utility.SINGLE_STRING_HANDLER)));
        String[] params = new String[]{"" + meta_id, "" + keywordId};
        database.execute(new SqlUpdateCommand("INSERT INTO meta_classification (meta_id, class_id) VALUES(?,?)", params));
    }

    public void checkIfAliasAlreadyExist(DocumentDomainObject document) throws AliasAlreadyExistsInternalException {
        Set<String> allAlias = documentMapper.getAllDocumentAlias();
        String alias = document.getAlias();
        if (allAlias.contains(alias) && !documentMapper.getDocument(alias).equals(document)) {
            throw new AliasAlreadyExistsInternalException("A document with alias '" + document.getAlias()
                    + "' already exists");
        }
    }

}
