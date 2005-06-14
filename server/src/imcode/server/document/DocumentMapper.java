package imcode.server.document;

import com.imcode.imcms.api.CategoryAlreadyExistsException;
import com.imcode.imcms.flow.DocumentPageFlow;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.commands.*;
import imcode.server.db.exceptions.DatabaseException;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Clock;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.NDC;
import org.apache.oro.text.perl.Perl5Util;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

public class DocumentMapper implements DocumentGetter {

    private static final int UNLIMITED_MAX_CATEGORY_CHOICES = 0;

    private static final int META_HEADLINE_MAX_LENGTH = 255;
    private static final int META_TEXT_MAX_LENGTH = 1000;

    // Stored procedure names used in this class
    // todo make sure all these is only used in one sprocMethod
    private static final String SPROC_GET_TEXT = "GetText";
    private static final String SQL_GET_ALL_SECTIONS = "SELECT section_id, section_name FROM sections";

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private final Database database;
    private final DocumentPermissionSetMapper documentPermissionSetMapper;
    private final DocumentIndex documentIndex;
    private final Map documentCache;
    private final Clock clock;
    private final ImcmsServices services;
    public static final String SQL_GET_ALL_CATEGORIES_OF_TYPE = "SELECT categories.category_id, categories.name, categories.description, categories.image\n"
            + "FROM categories\n"
            + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
            + "WHERE categories.category_type_id = ?\n"
            + "ORDER BY categories.name";
    public static final String SQL_GET_CATEGORY = "SELECT categories.category_id, categories.name, categories.description, categories.image\n"
            + "FROM categories\n"
            + "JOIN category_types\n"
            + "ON categories.category_type_id = category_types.category_type_id\n"
            + "WHERE category_types.name = ?\n"
            + "AND categories.name = ?";
    public static final String SQL_GET_DOCUMENT = "SELECT meta_id,\n"
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
            + "WHERE meta_id = ?";
    public static final String SQL_GET_SECTIONS_FOR_DOCUMENT = "SELECT s.section_id, s.section_name\n"
            + " FROM sections s, meta_section ms, meta m\n"
            + "where m.meta_id=ms.meta_id\n"
            + "and m.meta_id=?\n"
            + "and ms.section_id=s.section_id";
    public static final String SQL_DELETE_ROLE_DOCUMENT_PERMISSION_SET_ID = "DELETE FROM roles_rights WHERE role_id = ? AND meta_id = ?";
    public static final String SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID = "INSERT INTO roles_rights (role_id, meta_id, set_id) VALUES(?,?,?)";

    public static final String SQL__CATEGORY_TYPE__COLUMNS = "category_types.category_type_id, category_types.name, category_types.max_choices, category_types.inherited";

    public DocumentMapper(ImcmsServices services, Database database,
                          DocumentPermissionSetMapper documentPermissionSetMapper, DocumentIndex documentIndex,
                          Clock clock, Config config) {

        this.database = database;
        this.clock = clock;
        this.services = services;
        this.documentPermissionSetMapper = documentPermissionSetMapper;
        this.documentIndex = documentIndex;
        int documentCacheMaxSize = config.getDocumentCacheMaxSize();
        documentCache = Collections.synchronizedMap(new LRUMap(documentCacheMaxSize)) ;
    }

    public DocumentDomainObject createDocumentOfTypeFromParent(int documentTypeId, final DocumentDomainObject parent,
                                                               UserDomainObject user) {
        if (!user.canCreateDocumentOfTypeIdFromParent(documentTypeId, parent)) {
            throw new SecurityException("User can't create documents from document " + parent.getId());
        }
        DocumentDomainObject newDocument;
        try {
            if (DocumentTypeDomainObject.TEXT_ID == documentTypeId) {
                newDocument = (DocumentDomainObject) parent.clone();
                TextDocumentDomainObject newTextDocument = (TextDocumentDomainObject) newDocument;
                newTextDocument.removeAllTexts();
                newTextDocument.removeAllImages();
                newTextDocument.removeAllIncludes();
                newTextDocument.removeAllMenus();
                setTemplateForNewTextDocument( newTextDocument, user, parent );
            } else {
                newDocument = DocumentDomainObject.fromDocumentTypeId(documentTypeId);
                newDocument.setAttributes((DocumentDomainObject.Attributes) parent.getAttributes().clone());
            }
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
        newDocument.setId( 0 );
        newDocument.setHeadline( "" );
        newDocument.setMenuText( "" );
        newDocument.setMenuImage( "" );
        makeDocumentLookNew( newDocument, user );
        newDocument.removeNonInheritedCategories() ;
        return newDocument;
    }

    private void setTemplateForNewTextDocument( TextDocumentDomainObject newTextDocument, UserDomainObject user,
                                                final DocumentDomainObject parent ) {
        int permissionSetId = user.getPermissionSetIdFor( parent );
        TemplateDomainObject template = null;
        if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 == permissionSetId ) {
            template = ( (TextDocumentPermissionSetDomainObject)newTextDocument.getPermissionSetForRestrictedOneForNewDocuments() ).getDefaultTemplate();
        } else if ( DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2 == permissionSetId ) {
            template = ( (TextDocumentPermissionSetDomainObject)newTextDocument.getPermissionSetForRestrictedTwoForNewDocuments() ).getDefaultTemplate();
        } else if ( parent instanceof TextDocumentDomainObject ) {
            template = ( (TextDocumentDomainObject)parent ).getDefaultTemplate();
        }
        if ( null != template ) {
            newTextDocument.setTemplate( template );
        }
    }

    private void makeDocumentLookNew( DocumentDomainObject document, UserDomainObject user ) {
        Date now = new Date();
        makeDocumentLookCreated(document, user, now);
        document.setPublicationStartDatetime(now);
        document.setArchivedDatetime(null);
        document.setPublicationEndDatetime(null);
        document.setStatus(DocumentDomainObject.STATUS_NEW);
    }

    private void makeDocumentLookCreated(DocumentDomainObject document, UserDomainObject user, Date now) {
        document.setCreator(user);
        document.setCreatedDatetime(now);
        document.setModifiedDatetime(now);
    }

    public CategoryDomainObject[] getAllCategoriesOfType(CategoryTypeDomainObject categoryType) {
        String sqlQuery = SQL_GET_ALL_CATEGORIES_OF_TYPE;
        String[] parameters = new String[]{"" + categoryType.getId()};
        String[][] sqlResult = database.execute2dArrayQuery(sqlQuery, parameters);
        CategoryDomainObject[] categoryDomainObjects = new CategoryDomainObject[sqlResult.length];
        for (int i = 0; i < sqlResult.length; i++) {
            int categoryId = Integer.parseInt(sqlResult[i][0]);
            String categoryName = sqlResult[i][1];
            String categoryDescription = sqlResult[i][2];
            String categoryImage = sqlResult[i][3];

            categoryDomainObjects[i] =
                    new CategoryDomainObject(categoryId, categoryName, categoryDescription, categoryImage, categoryType);
        }
        return categoryDomainObjects;
    }

    public boolean isUniqueCategoryTypeName(String categoryTypeName) {
        CategoryTypeDomainObject[] categoryTypes = getAllCategoryTypes();
        for (int i = 0; i < categoryTypes.length; i++) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];
            if (categoryType.getName().equalsIgnoreCase(categoryTypeName)) {
                return false;
            }
        }
        return true;
    }

    public CategoryTypeDomainObject[] getAllCategoryTypes() {
        String sqlQuery = "SELECT " + SQL__CATEGORY_TYPE__COLUMNS + " FROM category_types ORDER BY name";
        String[][] sqlResult = database.execute2dArrayQuery(sqlQuery, new String[0]);

        CategoryTypeDomainObject[] categoryTypes = new CategoryTypeDomainObject[sqlResult.length];
        for ( int i = 0; i < categoryTypes.length; i++ ) {
            CategoryTypeDomainObject categoryType = createCategoryTypeFromSqlResult( sqlResult[i], 0 );
            categoryTypes[i] = categoryType;
        }

        return categoryTypes;
    }

    static CategoryTypeDomainObject createCategoryTypeFromSqlResult( String[] sqlRow, int offset ) {
        int categoryTypeId = Integer.parseInt( sqlRow[offset+0] );
        String typeName = sqlRow[offset+1];
        int maxChoices = Integer.parseInt( sqlRow[offset+2] );
        boolean inherited = 0 != Integer.parseInt( sqlRow[offset+3] ) ;
        CategoryTypeDomainObject categoryTypeDomainObject = new CategoryTypeDomainObject( categoryTypeId, typeName, maxChoices, inherited );
        return categoryTypeDomainObject;
    }

    public SectionDomainObject[] getAllSections() {
        String[] parameters = new String[0];
        String[][] sqlRows = database.execute2dArrayQuery(SQL_GET_ALL_SECTIONS, parameters);
        SectionDomainObject[] allSections = new SectionDomainObject[sqlRows.length];
        for (int i = 0; i < sqlRows.length; i++) {
            int sectionId = Integer.parseInt(sqlRows[i][0]);
            String sectionName = sqlRows[i][1];
            allSections[i] = new SectionDomainObject(sectionId, sectionName);
        }
        Arrays.sort(allSections, new SectionNameComparator());
        return allSections;
    }

    public CategoryDomainObject getCategory(CategoryTypeDomainObject categoryType, String categoryName) {
        String sqlQuery = SQL_GET_CATEGORY;
        String[] params = new String[]{categoryType.getName(), categoryName};
        String[] sqlResult = database.executeArrayQuery(sqlQuery, params);
        if (0 != sqlResult.length) {
            final int categoryId = Integer.parseInt(sqlResult[0]);
            final String categoryNameFromDb = sqlResult[1];
            final String categoryDescription = sqlResult[2];
            final String categoryImge = sqlResult[3];

            return new CategoryDomainObject(categoryId, categoryNameFromDb, categoryDescription, categoryImge,
                    categoryType);
        } else {
            return null;
        }
    }

    public CategoryDomainObject getCategoryById( int categoryId ) {
        String sqlQuery = "SELECT categories.name, categories.description, categories.image, "+SQL__CATEGORY_TYPE__COLUMNS+"\n"
                          + "FROM categories\n"
                          + "JOIN category_types ON categories.category_type_id = category_types.category_type_id\n"
                          + "WHERE categories.category_id = ?";

        String[] params = new String[]{"" + categoryId};
        String[] categorySqlResult = database.executeArrayQuery(sqlQuery, params);

        if (0 != categorySqlResult.length) {
            String categoryName = categorySqlResult[0];
            String categoryDescription = categorySqlResult[1];
            String categoryImage = categorySqlResult[2];
            CategoryTypeDomainObject categoryType = createCategoryTypeFromSqlResult( categorySqlResult, 3 ) ;

            return new CategoryDomainObject(categoryId, categoryName, categoryDescription, categoryImage, categoryType);
        } else {
            return null;
        }
    }

    public CategoryTypeDomainObject getCategoryType(String categoryTypeName) {
        String sqlStr = "SELECT "+SQL__CATEGORY_TYPE__COLUMNS+"\n"
                        + "FROM category_types\n"
                        + "WHERE category_types.name = ?";
        String[] sqlResult = database.executeArrayQuery(sqlStr, new String[]{categoryTypeName});

        if (null == sqlResult || 0 == sqlResult.length) {
            return null;
        } else {
            return createCategoryTypeFromSqlResult( sqlResult, 0 ) ;
        }
    }

    public CategoryTypeDomainObject getCategoryTypeById(int categoryTypeId) {
        String sqlStr = "select "+SQL__CATEGORY_TYPE__COLUMNS+" from category_types where category_type_id = ? ";
        String[] sqlResult = database.executeArrayQuery(sqlStr, new String[]{"" + categoryTypeId});

        if (null == sqlResult || 0 == sqlResult.length) {
            return null;
        } else {
            return createCategoryTypeFromSqlResult( sqlResult, 0 ) ;
        }
    }

    public void deleteCategoryTypeFromDb(CategoryTypeDomainObject categoryType) {
        String sqlstr = "delete from category_types where category_type_id = ?";
        String[] params = new String[]{categoryType.getId() + ""};
        database.executeUpdateQuery(sqlstr, params);
    }

    public CategoryTypeDomainObject addCategoryTypeToDb( final CategoryTypeDomainObject categoryType ) {
        DatabaseCommand addCategoryTypeCommand = new DatabaseCommand() {
            public Object executeOn(DatabaseConnection connection) throws DatabaseException {
                String sqlstr = "insert into category_types (name, max_choices, inherited) values(?,?,?)";
                String[] params = new String[]{categoryType.getName(), categoryType.getMaxChoices() + "", (categoryType.isInherited() ? "1" : "0")};
                return connection.executeUpdateAndGetGeneratedKey(sqlstr, params) ;
            }
        };
        Number newId = (Number) database.executeCommand(addCategoryTypeCommand) ;
        return getCategoryTypeById(newId.intValue());
    }

    public void updateCategoryType(CategoryTypeDomainObject categoryType) {
        String sqlstr = "update category_types set name= ?, max_choices= ?  where category_type_id = ? ";
        String[] params = new String[]{
            categoryType.getName(), categoryType.getMaxChoices() + "",
            categoryType.getId() + ""
        };
        database.executeUpdateQuery(sqlstr, params);
    }

    public CategoryDomainObject addCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        String sqlstr = "insert into categories  (category_type_id, name, description, image) values(?,?,?,?) SELECT @@IDENTITY";
        String[] params = new String[]{
            category.getType().getId() + "", category.getName(),
            category.getDescription(), category.getImageUrl()
        };
        String newId = database.executeStringQuery(sqlstr, params);
        int categoryId = Integer.parseInt(newId);
        category.setId(categoryId);
        return getCategoryById(categoryId);
    }

    public void updateCategory(CategoryDomainObject category) {
        String sqlstr = "update categories set category_type_id = ?, name= ?, description = ?, image = ?  where category_id = ? ";
        String[] params = new String[]{
            category.getType().getId() + "", category.getName(), category.getDescription(),
            category.getImageUrl(),
            category.getId() + ""
        };
        database.executeUpdateQuery(sqlstr, params);
    }

    public void deleteCategoryFromDb(CategoryDomainObject category) {
        String sqlstr = "delete from categories where category_id = ?";
        String[] params = new String[]{category.getId() + ""};
        database.executeUpdateQuery(sqlstr, params);
    }

    public DocumentDomainObject getDocument(int documentId) {
        return getDocument(new DocumentId(documentId)) ;
    }

    public DocumentDomainObject getDocument(DocumentId documentId) {
        NDC.push("getDocument");

        DocumentDomainObject document;
        final DatabaseDocumentGetter databaseDocumentGetter = new DatabaseDocumentGetter(database, services);
        document = getDocumentFromCache(documentId, databaseDocumentGetter);

        NDC.pop();
        return document;
    }

    public DocumentDomainObject getDocumentFromCache(DocumentId documentId, final DocumentGetter documentGetter) {
        try {
            DocumentDomainObject document = null ;
            SoftReference[] documentSoftReferenceArray = (SoftReference[]) documentCache.get(documentId);
            if (null != documentSoftReferenceArray && null != documentSoftReferenceArray[0]) {
                document = (DocumentDomainObject) documentSoftReferenceArray[0].get();
            }
            if (null == document) {
                documentSoftReferenceArray = new SoftReference[1];
                documentCache.put(documentId, documentSoftReferenceArray);
                document = documentGetter.getDocument(documentId);
                documentSoftReferenceArray[0] = new SoftReference(document);
            }
            if (null != document) {
                document = (DocumentDomainObject) document.clone();
            }
            return document;
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
    }

    public DocumentReference getDocumentReference(DocumentDomainObject document) {
        return getDocumentReference(document.getId());
    }

    DocumentReference getDocumentReference(int childId) {
        return new DocumentReference(childId, this);
    }

    public SectionDomainObject getSectionById(int sectionId) {
        String[] params = new String[]{"" + sectionId};
        String sectionName = database.executeStringQuery("SELECT section_name FROM sections WHERE section_id = ?", params);
        if (null == sectionName) {
            return null;
        }
        return new SectionDomainObject(sectionId, sectionName);
    }

    public SectionDomainObject getSectionByName(String name) {
        String[] params = new String[]{name};
        String[] sectionSqlRow = database.executeArrayQuery("SELECT section_id, section_name FROM sections WHERE section_name = ?", params);
        if (0 == sectionSqlRow.length) {
            return null;
        }
        int sectionId = Integer.parseInt(sectionSqlRow[0]);
        String sectionName = sectionSqlRow[1];
        return new SectionDomainObject(sectionId, sectionName);
    }

    public TextDomainObject getText(int metaId, int no) {
        String[] results = sprocGetText(metaId, no);

        if (results == null || results.length == 0) {
            /* There was no text. Return null. */
            return null;
        }

        /* Return the text */
        String text = results[0];
        int type = Integer.parseInt(results[1]);

        return new TextDomainObject(text, type);

    }

    public void removeInclusion(int includingMetaId, int includeIndex) {
        deleteInclude(includingMetaId, includeIndex);
    }

    public void saveNewDocument(DocumentDomainObject document, UserDomainObject user)
            throws MaxCategoryDomainObjectsOfTypeExceededException {

        if (!user.canEdit(document)) {
            return; // TODO: More specific check needed. Throw exception ?
        }

        checkMaxDocumentCategoriesOfType(document);

        makeDocumentLookCreated(document, user, new Date());

        int newMetaId = sqlInsertIntoMeta(document);

        if (!user.isSuperAdminOrHasFullPermissionOn(document)) {
            document.setPermissionSetForRestrictedOne(document.getPermissionSetForRestrictedOneForNewDocuments());
            document.setPermissionSetForRestrictedTwo(document.getPermissionSetForRestrictedTwoForNewDocuments());
        }

        document.setId(newMetaId);

        updateDocumentSectionsCategoriesKeywords(document);

        updateDocumentRolePermissions(document, user, null);

        documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(document, user, null);

        document.accept(new DocumentCreatingVisitor(user, database, services));

        invalidateDocument(document);
    }

    private void updateDocumentSectionsCategoriesKeywords(DocumentDomainObject document) {
        updateDocumentSections(document.getId(), document.getSections());

        updateDocumentCategories(document);

        updateDocumentKeywords(document);
    }

    private void updateDocumentKeywords(DocumentDomainObject document) {
        int meta_id = document.getId();
        Set keywords = document.getKeywords();
        Set allKeywords = new HashSet(Arrays.asList(getAllKeywords()));
        deleteKeywordsFromDocument(meta_id);
        for ( Iterator iterator = keywords.iterator(); iterator.hasNext(); ) {
            String keyword = (String) iterator.next();
            final boolean keywordExists = allKeywords.contains(keyword);
            if (!keywordExists) {
                addKeyword(keyword);
            }
            addExistingKeywordToDocument(meta_id, keyword);
        }
        deleteUnusedKeywords();
    }

    private int sqlInsertIntoMeta(DocumentDomainObject document) {

        final Number documentId = (Number) database.executeCommand(new InsertIntoTableDatabaseCommand("meta", new String[][]{
            { "doc_type", document.getDocumentTypeId() + ""},
            { "meta_headline", document.getHeadline()},
            { "meta_text", document.getMenuText()},
            { "meta_image", document.getMenuImage()},
            { "owner_id", document.getCreator().getId() + ""},
            { "permissions", makeSqlStringFromBoolean(document.isRestrictedOneMorePrivilegedThanRestrictedTwo())},
            { "shared", makeSqlStringFromBoolean(document.isLinkableByOtherUsers())},
            { "show_meta", makeSqlStringFromBoolean(document.isVisibleInMenusForUnauthorizedUsers())},
            { "lang_prefix", document.getLanguageIso639_2()},
            { "date_created", Utility.makeSqlStringFromDate(document.getCreatedDatetime()) },
            { "date_modified", Utility.makeSqlStringFromDate(document.getModifiedDatetime())},
            { "disable_search", makeSqlStringFromBoolean(document.isSearchDisabled())},
            { "target", document.getTarget()},
            { "activate", "1"},
            { "archived_datetime", Utility.makeSqlStringFromDate(document.getArchivedDatetime())},
            { "publisher_id", null != document.getPublisher() ? document.getPublisher().getId() + "" : null},
            { "status", "" + document.getStatus()},
            { "publication_start_datetime", Utility.makeSqlStringFromDate(document.getPublicationStartDatetime())},
            { "publication_end_datetime", Utility.makeSqlStringFromDate(document.getPublicationEndDatetime())}
        }));
        return documentId.intValue();
    }

    private String makeSqlStringFromBoolean(final boolean bool) {
        return bool ? "1" : "0";
    }

    public void saveDocument(DocumentDomainObject document,
                             UserDomainObject user) throws MaxCategoryDomainObjectsOfTypeExceededException {

        DocumentDomainObject oldDocument = getDocument(document.getId());

        if (!user.canEdit(oldDocument)) {
            return;
        }

        checkMaxDocumentCategoriesOfType(document);

        try {
            Date lastModifiedDatetime = Utility.truncateDateToMinutePrecision(document.getLastModifiedDatetime());
            Date modifiedDatetime = Utility.truncateDateToMinutePrecision(document.getModifiedDatetime());
            boolean modifiedDatetimeUnchanged = lastModifiedDatetime.equals(modifiedDatetime);
            if (modifiedDatetimeUnchanged) {
                document.setModifiedDatetime(this.clock.getCurrentDate());
            }

            sqlUpdateMeta(document);

            updateDocumentSectionsCategoriesKeywords(document);

            if (user.canEditPermissionsFor(oldDocument)) {
                updateDocumentRolePermissions(document, user, oldDocument);

                documentPermissionSetMapper.saveRestrictedDocumentPermissionSets(document, user, oldDocument);
            }

            document.accept(new DocumentSavingVisitor(user, oldDocument, database, services));
        } finally {
            invalidateDocument(document);
        }
    }

    public void invalidateDocument(DocumentDomainObject document) {
        documentIndex.indexDocument(document);
        documentCache.remove(new DocumentId(document.getId()));
    }

    void updateDocumentRolePermissions(DocumentDomainObject document, UserDomainObject user,
                                       DocumentDomainObject oldDocument) {
        Map rolesMappedtoPermissionSetIds = new HashMap();
        if (null != oldDocument) {
            Set rolesMappedToPermissionsForOldDocument = oldDocument.getRolesMappedToPermissionSetIds().keySet();
            for (Iterator iterator = rolesMappedToPermissionsForOldDocument.iterator(); iterator.hasNext();) {
                RoleDomainObject role = (RoleDomainObject) iterator.next();
                rolesMappedtoPermissionSetIds.put(role, new Integer(DocumentPermissionSetDomainObject.TYPE_ID__NONE));
            }
        }
        rolesMappedtoPermissionSetIds.putAll(document.getRolesMappedToPermissionSetIds());
        for (Iterator it = rolesMappedtoPermissionSetIds.entrySet().iterator(); it.hasNext();) {
            Map.Entry rolePermissionTuple = (Map.Entry) it.next();
            RoleDomainObject role = (RoleDomainObject) rolePermissionTuple.getKey();
            int permissionSetId = ((Integer) rolePermissionTuple.getValue()).intValue();

            if (null == oldDocument
                    || user.canSetPermissionSetIdForRoleOnDocument(permissionSetId, role, oldDocument)) {
                String[] params1 = new String[]{"" + role.getId(),
                                                "" + document.getId()};
                database.executeUpdateQuery(SQL_DELETE_ROLE_DOCUMENT_PERMISSION_SET_ID, params1);
                if (DocumentPermissionSetDomainObject.TYPE_ID__NONE != permissionSetId) {
                    String[] params = new String[]{
                        "" + role.getId(), "" + document.getId(), "" + permissionSetId};
                    database.executeUpdateQuery(SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID, params);
                }
            }
        }
    }

    private void checkMaxDocumentCategoriesOfType(DocumentDomainObject document)
            throws MaxCategoryDomainObjectsOfTypeExceededException {
        CategoryTypeDomainObject[] categoryTypes = getAllCategoryTypes();
        for (int i = 0; i < categoryTypes.length; i++) {
            CategoryTypeDomainObject categoryType = categoryTypes[i];
            int maxChoices = categoryType.getMaxChoices();
            CategoryDomainObject[] documentCategoriesOfType = document.getCategoriesOfType(categoryType);
            if (UNLIMITED_MAX_CATEGORY_CHOICES != maxChoices && documentCategoriesOfType.length > maxChoices) {
                throw new MaxCategoryDomainObjectsOfTypeExceededException("Document may have at most " + maxChoices
                        + " categories of type '"
                        + categoryType.getName()
                        + "'");
            }
        }
    }

    private void updateDocumentCategories(DocumentDomainObject document) {
        removeAllCategoriesFromDocument(document);
        CategoryDomainObject[] categories = document.getCategories();
        for (int i = 0; i < categories.length; i++) {
            CategoryDomainObject category = categories[i];
            addCategoryToDocument(category, document);
        }
    }

    private void addCategoryToDocument(CategoryDomainObject category, DocumentDomainObject document) {
        int categoryId = category.getId();
        String[] params = new String[]{"" + document.getId(), "" + categoryId};
        database.executeUpdateQuery("INSERT INTO document_categories (meta_id, category_id) VALUES(?,?)", params);
    }

    public String[] getAllDocumentsOfOneCategory(CategoryDomainObject category) {

        String sqlstr = "select meta_id from document_categories where category_id = ? ";
        String[] params = new String[]{category.getId() + ""};
        String[] res = database.executeArrayQuery(sqlstr, params);

        return res;
    }

    private void removeAllCategoriesFromDocument(DocumentDomainObject document) {
        String[] params = new String[]{"" + document.getId()};
        database.executeUpdateQuery("DELETE FROM document_categories WHERE meta_id = ?", params);
    }

    public void deleteOneCategoryFromDocument(DocumentDomainObject document, CategoryDomainObject category) {
        String[] params = new String[]{document.getId() + "", category.getId() + ""};
        database.executeUpdateQuery("DELETE FROM document_categories WHERE meta_id = ? and category_id = ?", params);
    }

    private void sqlUpdateMeta(DocumentDomainObject document) {
        String headline = document.getHeadline();
        String text = document.getMenuText();

        StringBuffer sqlStr = new StringBuffer("update meta set ");

        ArrayList sqlUpdateColumns = new ArrayList();
        ArrayList sqlUpdateValues = new ArrayList();

        makeDateSqlUpdateClause("publication_start_datetime", document.getPublicationStartDatetime(), sqlUpdateColumns, sqlUpdateValues);
        makeDateSqlUpdateClause("publication_end_datetime", document.getPublicationEndDatetime(), sqlUpdateColumns, sqlUpdateValues);
        makeDateSqlUpdateClause("archived_datetime", document.getArchivedDatetime(), sqlUpdateColumns, sqlUpdateValues);
        makeDateSqlUpdateClause("date_created", document.getCreatedDatetime(), sqlUpdateColumns, sqlUpdateValues);
        String headlineThatFitsInDB = headline.substring(0,
                Math.min(headline.length(), META_HEADLINE_MAX_LENGTH - 1));
        makeStringSqlUpdateClause("meta_headline", headlineThatFitsInDB, sqlUpdateColumns, sqlUpdateValues);
        makeStringSqlUpdateClause("meta_image", document.getMenuImage(), sqlUpdateColumns, sqlUpdateValues);
        makeDateSqlUpdateClause("date_modified", document.getModifiedDatetime(), sqlUpdateColumns, sqlUpdateValues);
        makeStringSqlUpdateClause("target", document.getTarget(), sqlUpdateColumns, sqlUpdateValues);
        String textThatFitsInDB = text.substring(0, Math.min(text.length(), META_TEXT_MAX_LENGTH - 1));
        makeStringSqlUpdateClause("meta_text", textThatFitsInDB, sqlUpdateColumns, sqlUpdateValues);
        makeStringSqlUpdateClause("lang_prefix", document.getLanguageIso639_2(), sqlUpdateColumns, sqlUpdateValues);
        makeBooleanSqlUpdateClause("disable_search", document.isSearchDisabled(), sqlUpdateColumns, sqlUpdateValues);
        makeBooleanSqlUpdateClause("shared", document.isLinkableByOtherUsers(), sqlUpdateColumns, sqlUpdateValues);
        makeBooleanSqlUpdateClause("show_meta", document.isVisibleInMenusForUnauthorizedUsers(), sqlUpdateColumns, sqlUpdateValues);
        makeBooleanSqlUpdateClause("permissions", document.isRestrictedOneMorePrivilegedThanRestrictedTwo(), sqlUpdateColumns, sqlUpdateValues);
        UserDomainObject publisher = document.getPublisher();
        makeIntSqlUpdateClause("publisher_id", publisher == null ? null
                : new Integer(publisher.getId()), sqlUpdateColumns,
                sqlUpdateValues);
        UserDomainObject creator = document.getCreator();
        if (null != creator) {
            makeIntSqlUpdateClause("owner_id", new Integer(creator.getId()), sqlUpdateColumns,
                    sqlUpdateValues);
        }
        makeIntSqlUpdateClause("status", new Integer(document.getStatus()), sqlUpdateColumns, sqlUpdateValues);

        sqlStr.append(StringUtils.join(sqlUpdateColumns.iterator(), ","));
        sqlStr.append(" where meta_id = ?");
        sqlUpdateValues.add("" + document.getId());
        String[] params = (String[]) sqlUpdateValues.toArray(new String[sqlUpdateValues.size()]);
        database.executeUpdateQuery(sqlStr.toString(), params);
    }

    public void setInclude(int includingMetaId, int includeIndex, int includedMetaId) {
        String procedure = "SetInclude";
        String[] params = new String[]{"" + includingMetaId, "" + includeIndex, "" + includedMetaId};
        database.executeUpdateProcedure(procedure, params);
    }

    public void deleteInclude(int including_meta_id, int include_id) {
        String[] params = new String[]{"" + including_meta_id, "" + include_id};
        database.executeUpdateProcedure("DeleteInclude", params);
    }

    private void addExistingKeywordToDocument(int meta_id, String keyword) {
        String[] params1 = new String[]{
            keyword
        };
        int keywordId = Integer.parseInt(database.executeStringQuery("SELECT class_id FROM classification WHERE code = ?", params1));
        String[] params = new String[]{"" + meta_id, "" + keywordId};
        database.executeUpdateQuery("INSERT INTO meta_classification (meta_id, class_id) VALUES(?,?)", params);
    }

    private void deleteUnusedKeywords() {
        String[] params = new String[0];
        database.executeUpdateQuery("DELETE FROM classification WHERE class_id NOT IN (SELECT class_id FROM meta_classification)", params);
    }

    private void addKeyword(String keyword) {
        String[] params = new String[]{keyword};
        database.executeUpdateQuery("INSERT INTO classification VALUES(?)", params);
    }

    private String[] getAllKeywords() {
        String[] params = new String[0];
        return database.executeArrayQuery("SELECT code FROM classification", params);
    }

    private void deleteKeywordsFromDocument(int meta_id) {
        String sqlDeleteKeywordsFromDocument = "DELETE FROM meta_classification WHERE meta_id = ?";
        String[] params = new String[]{"" + meta_id};
        database.executeUpdateQuery(sqlDeleteKeywordsFromDocument, params);
    }

    private void addSectionToDocument(int metaId, int sectionId) {
        String[] params = new String[]{"" + metaId, "" + sectionId};
        database.executeUpdateQuery("INSERT INTO meta_section VALUES(?,?)", params);
    }

    private static void makeBooleanSqlUpdateClause(String columnName, boolean bool, List sqlUpdateColumns,
                                                   List sqlUpdateValues) {
        sqlUpdateColumns.add(columnName + " = ?");
        sqlUpdateValues.add(bool ? "1" : "0");
    }

    private static void makeDateSqlUpdateClause(String columnName, Date date, List sqlUpdateColumns,
                                                List sqlUpdateValues) {
        makeStringSqlUpdateClause(columnName, Utility.makeSqlStringFromDate(date), sqlUpdateColumns, sqlUpdateValues);
    }

    private static void makeIntSqlUpdateClause(String columnName, Integer integer, ArrayList sqlUpdateColumns,
                                               ArrayList sqlUpdateValues) {
        if (null != integer) {
            sqlUpdateColumns.add(columnName + " = ?");
            sqlUpdateValues.add("" + integer);
        } else {
            sqlUpdateColumns.add(columnName + " = NULL");
        }
    }

    private static void makeStringSqlUpdateClause(String columnName, String value, List sqlUpdateColumns,
                                                  List sqlUpdateValues) {
        if (null != value) {
            sqlUpdateColumns.add(columnName + " = ?");
            sqlUpdateValues.add(value);
        } else {
            sqlUpdateColumns.add(columnName + " = NULL");
        }
    }

    private void removeAllSectionsFromDocument(int metaId) {
        String[] params = new String[]{"" + metaId};
        database.executeUpdateQuery("DELETE FROM meta_section WHERE meta_id = ?", params);
    }

    private void updateDocumentSections(int metaId,
                                        SectionDomainObject[] sections) {
        removeAllSectionsFromDocument(metaId);
        for (int i = 0; null != sections && i < sections.length; i++) {
            SectionDomainObject section = sections[i];
            addSectionToDocument(metaId, section.getId());
        }
    }

    static boolean getBooleanFromSqlResultString(final String columnValue) {
        return !"0".equals(columnValue);
    }

    static Date parseDateFormat(DateFormat dateFormat, String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (NullPointerException npe) {
            return null;
        } catch (ParseException pe) {
            return null;
        }
    }

    private String[] sprocGetText(int meta_id, int no) {
        String[] params = new String[]{"" + meta_id, "" + no};
        String sprocGetText = SPROC_GET_TEXT;
        String[] results = database.executeArrayProcedure(sprocGetText, params);
        return results;
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public String[][] getParentDocumentAndMenuIdsForDocument(DocumentDomainObject document) {
        String sqlStr = "SELECT meta_id,menu_index FROM childs, menus WHERE menus.menu_id = childs.menu_id AND to_meta_id = ?";
        String[] parameters = new String[]{"" + document.getId()};
        return database.execute2dArrayQuery(sqlStr, parameters);
    }

    public String[][] getAllMimeTypesWithDescriptions(UserDomainObject user) {
        String sqlStr = "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id";
        String[] parameters = new String[]{user.getLanguageIso639_2()};
        String[][] mimeTypes = database.execute2dArrayQuery(sqlStr, parameters);
        return mimeTypes;
    }

    public String[] getAllMimeTypes() {
        String sqlStr = "SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id";
        String[] params = new String[]{};
        String[] mimeTypes = database.executeArrayQuery(sqlStr, params);
        return mimeTypes;
    }

    public void addToMenu(TextDocumentDomainObject parentDocument, int parentMenuIndex,
                          DocumentDomainObject documentToAddToMenu, UserDomainObject user) {
        parentDocument.getMenu(parentMenuIndex).addMenuItem(new MenuItemDomainObject(this.getDocumentReference(documentToAddToMenu)));
        saveDocument(parentDocument, user);
    }

    public BrowserDocumentDomainObject.Browser[] getAllBrowsers() {
        String sqlStr = "SELECT browser_id, name, value FROM browsers WHERE browser_id != 0";
        String[] parameters = new String[0];
        String[][] sqlResult = database.execute2dArrayQuery(sqlStr, parameters);
        List browsers = new ArrayList();
        for (int i = 0; i < sqlResult.length; i++) {
            browsers.add(createBrowserFromSqlRow(sqlResult[i]));
        }
        return (BrowserDocumentDomainObject.Browser[]) browsers.toArray(new BrowserDocumentDomainObject.Browser[browsers.size()]);
    }

    public BrowserDocumentDomainObject.Browser getBrowserById(int browserIdToGet) {
        if (browserIdToGet == BrowserDocumentDomainObject.Browser.DEFAULT.getId()) {
            return BrowserDocumentDomainObject.Browser.DEFAULT;
        }
        String sqlStr = "SELECT browser_id, name, value FROM browsers WHERE browser_id = ?";
        String[] params = new String[]{"" + browserIdToGet};
        String[] sqlRow = database.executeArrayQuery(sqlStr, params);
        BrowserDocumentDomainObject.Browser browser = createBrowserFromSqlRow(sqlRow);
        return browser;
    }

    protected BrowserDocumentDomainObject.Browser createBrowserFromSqlRow(String[] sqlRow) {
        int browserId = Integer.parseInt(sqlRow[0]);
        String browserName = sqlRow[1];
        int browserSpecificity = Integer.parseInt(sqlRow[2]);
        BrowserDocumentDomainObject.Browser browser = new BrowserDocumentDomainObject.Browser(browserId, browserName, browserSpecificity);
        return browser;
    }

    public void deleteDocument(final DocumentDomainObject document, UserDomainObject user) {
        DatabaseCommand deleteDocumentCommand = createDeleteDocumentCommand(document);
        database.executeCommand(deleteDocumentCommand);
        document.accept(new DocumentDeletingVisitor());
        invalidateDocument(document);
    }

    private DatabaseCommand createDeleteDocumentCommand(final DocumentDomainObject document) {
        final String metaIdStr = "" + document.getId();
        final String metaIdColumn = "meta_id";
        DatabaseCommand composite = new CompositeDatabaseCommand(new DatabaseCommand[]{
            new DeleteWhereColumnEqualsDatabaseCommand("document_categories", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("meta_classification", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("childs", "to_meta_id", metaIdStr),
            new UpdateDatabaseCommand("DELETE FROM childs WHERE menu_id IN (SELECT menu_id FROM menus WHERE meta_id = ?)", new String[]{metaIdStr}),
            new DeleteWhereColumnEqualsDatabaseCommand("menus", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("text_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("texts", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("images", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("roles_rights", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("user_rights", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("url_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("browser_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("fileupload_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("frameset_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("new_doc_permission_sets_ex", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("new_doc_permission_sets", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("doc_permission_sets_ex", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("doc_permission_sets", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("includes", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("includes", "included_meta_id", metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("meta_section", metaIdColumn, metaIdStr),
            new DeleteWhereColumnEqualsDatabaseCommand("meta", metaIdColumn, metaIdStr),
        });
        return composite;
    }

    public Map getAllDocumentTypeIdsAndNamesInUsersLanguage(UserDomainObject user) {
        String[] parameters = new String[]{
            user.getLanguageIso639_2()
        };
        String[][] rows = database.execute2dArrayQuery("SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type", parameters);
        Map allDocumentTypeIdsAndNamesInUsersLanguage = new TreeMap();
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            Integer documentTypeId = Integer.valueOf(row[0]);
            String documentTypeNameInUsersLanguage = row[1];
            allDocumentTypeIdsAndNamesInUsersLanguage.put(documentTypeId, documentTypeNameInUsersLanguage);
        }
        return allDocumentTypeIdsAndNamesInUsersLanguage;
    }

    public TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingDocument(DocumentDomainObject document) {
        String sqlSelectMenus = "SELECT meta_id, menu_index FROM menus, childs WHERE menus.menu_id = childs.menu_id AND childs.to_meta_id = ? ORDER BY meta_id, menu_index";
        String[] parameters = new String[]{"" + document.getId()};
        String[][] sqlRows = database.execute2dArrayQuery(sqlSelectMenus, parameters);
        TextDocumentMenuIndexPair[] documentMenuPairs = new TextDocumentMenuIndexPair[sqlRows.length];
        for (int i = 0; i < sqlRows.length; i++) {
            String[] sqlRow = sqlRows[i];
            int containingDocumentId = Integer.parseInt(sqlRow[0]);
            int menuIndex = Integer.parseInt(sqlRow[1]);
            TextDocumentDomainObject containingDocument = (TextDocumentDomainObject) getDocument(containingDocumentId);
            documentMenuPairs[i] = new TextDocumentMenuIndexPair(containingDocument, menuIndex);
        }
        return documentMenuPairs;
    }

    public Iterator getDocumentsIterator(final IntRange idRange) {
        return new DocumentsIterator(getDocumentIds(idRange));
    }

    private int[] getDocumentIds(IntRange idRange) {
        String sqlSelectIds = "SELECT meta_id FROM meta WHERE meta_id >= ? AND meta_id <= ? ORDER BY meta_id";
        String[] params = new String[]{
            "" + idRange.getMinimumInteger(),
            "" + idRange.getMaximumInteger()
        };
        String[] documentIdStrings = database.executeArrayQuery(sqlSelectIds, params);
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    public int[] getAllDocumentIds() {
        String[] params = new String[0];
        String[] documentIdStrings = database.executeArrayQuery("SELECT meta_id FROM meta ORDER BY meta_id", params);
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    static void deleteFileDocumentFilesAccordingToFileFilter(FileFilter fileFilter) {
        File filePath = Imcms.getServices().getConfig().getFilePath();
        File[] filesToDelete = filePath.listFiles(fileFilter);
        for (int i = 0; i < filesToDelete.length; i++) {
            filesToDelete[i].delete();
        }
    }

    static void deleteAllFileDocumentFiles(FileDocumentDomainObject fileDocument) {
        deleteFileDocumentFilesAccordingToFileFilter(new FileDocumentFileFilter(fileDocument));
    }

    public DocumentPermissionSetMapper getDocumentPermissionSetMapper() {
        return documentPermissionSetMapper;
    }

    static void deleteOtherFileDocumentFiles(final FileDocumentDomainObject fileDocument) {
        deleteFileDocumentFilesAccordingToFileFilter(new SuperfluousFileDocumentFilesFileFilter(fileDocument));
    }

    public void clearDocumentCache() {
        documentCache.clear();
    }

    public int getLowestDocumentId() {
        String[] params = new String[0];
        return Integer.parseInt(database.executeStringQuery("SELECT MIN(meta_id) FROM meta", params));
    }

    public int getHighestDocumentId() {
        String[] params = new String[0];
        return Integer.parseInt(database.executeStringQuery("SELECT MAX(meta_id) FROM meta", params));
    }

    public void copyDocument(DocumentDomainObject selectedChild,
                             UserDomainObject user) {
        String copyHeadlineSuffix = services.getAdminTemplate(COPY_HEADLINE_SUFFIX_TEMPLATE, user, null);
        selectedChild.setHeadline(selectedChild.getHeadline() + copyHeadlineSuffix);
        makeDocumentLookNew(selectedChild, user);
        services.getDocumentMapper().saveNewDocument(selectedChild, user);
    }

    public void saveCategory(CategoryDomainObject category) throws CategoryAlreadyExistsException {
        CategoryDomainObject categoryInDb = getCategory(category.getType(), category.getName());
        if (null != categoryInDb && category.getId() != categoryInDb.getId()) {
            throw new CategoryAlreadyExistsException("A category with name \"" + category.getName()
                    + "\" already exists in category type \""
                    + category.getType().getName()
                    + "\".");
        }
        if (0 == category.getId()) {
            addCategory(category);
        } else {
            updateCategory(category);
        }
    }

    public List getDocumentsWithPermissionsForRole(RoleDomainObject role) {
        String sqlStr = "SELECT meta_id FROM roles_rights WHERE role_id = ? ORDER BY meta_id";
        String[] documentIdStrings = database.executeArrayQuery(sqlStr, new String[]{"" + role.getId()});
        final int[] documentIds = Utility.convertStringArrayToIntArray(documentIdStrings);
        return new AbstractList() {
            public Object get(int index) {
                return getDocument(documentIds[index]);
            }

            public int size() {
                return documentIds.length;
            }
        };
    }

    public static class TextDocumentMenuIndexPair {

        private TextDocumentDomainObject document;
        private int menuIndex;

        public TextDocumentMenuIndexPair(TextDocumentDomainObject document, int menuIndex) {
            this.document = document;
            this.menuIndex = menuIndex;
        }

        public TextDocumentDomainObject getDocument() {
            return document;
        }

        public int getMenuIndex() {
            return menuIndex;
        }
    }

    private class DocumentsIterator implements Iterator {

        int[] documentIds;
        int index = 0;

        DocumentsIterator(int[] documentIds) {
            this.documentIds = (int[]) documentIds.clone();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return index < documentIds.length;
        }

        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return getDocument(documentIds[index++]);
        }
    }

    public static class SaveEditedDocumentCommand implements DocumentPageFlow.SaveDocumentCommand {

        public void saveDocument(DocumentDomainObject document, UserDomainObject user) {
            Imcms.getServices().getDocumentMapper().saveDocument(document, user);
        }
    }

    private static class FileDocumentFileFilter implements FileFilter {

        protected final FileDocumentDomainObject fileDocument;

        protected FileDocumentFileFilter(FileDocumentDomainObject fileDocument) {
            this.fileDocument = fileDocument;
        }

        public boolean accept(File file) {
            String filename = file.getName();
            Perl5Util perl5Util = new Perl5Util();
            if (perl5Util.match("/(\\d+)(?:_se|\\.(.*))?/", filename)) {
                String idStr = perl5Util.group(1);
                String variantName = FileUtility.unescapeFilename(StringUtils.defaultString(perl5Util.group(2)));
                return accept(file, Integer.parseInt(idStr), variantName);
            }
            return false;
        }

        public boolean accept(File file, int fileDocumentId, String fileId) {
            return fileDocumentId == fileDocument.getId();
        }
    }

    private static class SuperfluousFileDocumentFilesFileFilter extends FileDocumentFileFilter {

        private SuperfluousFileDocumentFilesFileFilter(FileDocumentDomainObject fileDocument) {
            super(fileDocument);
        }

        public boolean accept(File file, int fileDocumentId, String fileId) {
            boolean correctFileForFileDocumentFile = file.equals(DocumentSavingVisitor.getFileForFileDocument(fileDocumentId, fileId));
            boolean fileDocumentHasFile = null != fileDocument.getFile(fileId);
            return super.accept(file, fileDocumentId, fileId)
                    && (!correctFileForFileDocumentFile || !fileDocumentHasFile);
        }
    }

    private static class SectionNameComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            SectionDomainObject section1 = (SectionDomainObject) o1;
            SectionDomainObject section2 = (SectionDomainObject) o2;
            return section1.getName().compareToIgnoreCase(section2.getName());
        }
    }

}

