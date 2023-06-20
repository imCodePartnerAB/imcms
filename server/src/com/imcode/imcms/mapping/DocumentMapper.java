package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.commands.CompositeDatabaseCommand;
import com.imcode.db.commands.DeleteWhereColumnsEqualDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.handlers.CollectionHandler;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.servlet.ImageCacheManager;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Clock;
import imcode.util.LazilyLoadedObject;
import imcode.util.SystemClock;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.oro.text.perl.Perl5Util;

import java.io.File;
import java.io.FileFilter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class DocumentMapper implements DocumentGetter {

    private static final String SQL_GET_ALL_SECTIONS = "SELECT section_id, section_name FROM sections";
    private static final String SQL_GET_DOCUMENT_ID_FROM_PROPERTIES = "SELECT meta_id FROM document_properties WHERE key_name=? AND value=?";

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";
    private static final SectionNameComparator SECTION_NAME_COMPARATOR = new SectionNameComparator();
    private Database database;
    private DocumentPermissionSetMapper documentPermissionSetMapper;
    private DocumentIndex documentIndex;
    private Map<String, String[]> aliasCache;
    private Map<Integer, DocumentDomainObject> documentCache;
    private Clock clock = new SystemClock();
    private ImcmsServices imcmsServices;
    private CachingDocumentGetter documentGetter;
    private DocumentSaver documentSaver;
    private CategoryMapper categoryMapper;
    private LazilyLoadedObject sections;

    public DocumentMapper(ImcmsServices services, Database database) {
        this.imcmsServices = services;
        this.database = database;
        Config config = services.getConfig();
        int documentCacheMaxSize = config.getDocumentCacheMaxSize();
        documentCache = Collections.synchronizedMap(new LRUMap<>(documentCacheMaxSize));
        aliasCache = Collections.synchronizedMap(new LRUMap<>(documentCacheMaxSize));
        setDocumentGetter(new FragmentingDocumentGetter(new DatabaseDocumentGetter(database, services)));
        this.documentPermissionSetMapper = new DocumentPermissionSetMapper(database);
        this.categoryMapper = new CategoryMapper(database);
        documentSaver = new DocumentSaver(this);
        initSections();
    }

    static void deleteFileDocumentFilesAccordingToFileFilter(FileFilter fileFilter) {
        File filePath = Imcms.getServices().getConfig().getFilePath();
        File[] filesToDelete = filePath.listFiles(fileFilter);
        if (filesToDelete == null) {
            return;
        }

        for (File aFilesToDelete : filesToDelete) {
            aFilesToDelete.delete();
        }
    }

    static void deleteAllFileDocumentFiles(FileDocumentDomainObject fileDocument) {
        deleteFileDocumentFilesAccordingToFileFilter(new FileDocumentFileFilter(fileDocument));
    }

    static void deleteOtherFileDocumentFiles(final FileDocumentDomainObject fileDocument) {
        deleteFileDocumentFilesAccordingToFileFilter(new SuperfluousFileDocumentFilesFileFilter(fileDocument));
    }

    public void initSections() {
        sections = new LazilyLoadedObject(new SectionsSetLoader());
    }

    public DocumentSaver getDocumentSaver() {
        return documentSaver;
    }

    public DocumentDomainObject createDocumentOfTypeFromParent(int documentTypeId, final DocumentDomainObject parent, UserDomainObject user) {
        DocumentDomainObject newDocument;
        try {
            if (DocumentTypeDomainObject.TEXT_ID == documentTypeId) {
                newDocument = (DocumentDomainObject) parent.clone();
                TextDocumentDomainObject newTextDocument = (TextDocumentDomainObject) newDocument;
                newTextDocument.removeAllTexts();
                newTextDocument.removeAllImages();
                newTextDocument.removeAllIncludes();
                newTextDocument.removeAllMenus();
                setTemplateForNewTextDocument(newTextDocument, user, parent);
            } else {
                newDocument = DocumentDomainObject.fromDocumentTypeId(documentTypeId);
                newDocument.setAttributes((DocumentDomainObject.Attributes) parent.getAttributes().clone());
            }
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
        newDocument.setId(0);
        newDocument.setHeadline("");
        newDocument.setMenuText("");
        newDocument.setMenuImage("");
        newDocument.setProperties(new HashMap());
        makeDocumentLookNew(newDocument, user);
        removeNonInheritedCategories(newDocument);
        return newDocument;
    }

    void setTemplateForNewTextDocument(TextDocumentDomainObject newTextDocument, UserDomainObject user,
                                       final DocumentDomainObject parent) {
        DocumentPermissionSetTypeDomainObject documentPermissionSetType = user.getDocumentPermissionSetTypeFor(parent);
        String templateName = null;
        if (DocumentPermissionSetTypeDomainObject.RESTRICTED_1.equals(documentPermissionSetType)) {
            templateName = newTextDocument.getDefaultTemplateNameForRestricted1();
        } else if (DocumentPermissionSetTypeDomainObject.RESTRICTED_2.equals(documentPermissionSetType)) {
            templateName = newTextDocument.getDefaultTemplateNameForRestricted2();
        }
        if (null == templateName && parent instanceof TextDocumentDomainObject) {
            templateName = ((TextDocumentDomainObject) parent).getDefaultTemplateName();
        }
        if (null != templateName) {
            newTextDocument.setTemplateName(templateName);
        }
    }

    void makeDocumentLookNew(DocumentDomainObject document, UserDomainObject user) {
        Date now = new Date();
        document.setCreator(user);
        setCreatedAndModifiedDatetimes(document, now);
        document.setPublicationStartDatetime(now);
        document.setArchivedDatetime(null);
        document.setPublicationEndDatetime(null);
        document.setPublicationStatus(Document.PublicationStatus.NEW);
    }

    public DocumentReference getDocumentReference(DocumentDomainObject document) {
        return getDocumentReference(document.getId());
    }

    public DocumentReference getDocumentReference(int childId) {
        return new GetterDocumentReference(childId, documentGetter);
    }

    public SectionDomainObject getSectionById(int sectionId) {
        SectionsSet sectionsSet = (SectionsSet) sections.get();
        return sectionsSet.getSectionById(sectionId);
    }

    public SectionDomainObject getSectionByName(String name) {
        SectionsSet sectionsSet = (SectionsSet) sections.get();
        return sectionsSet.getSectionByName(name);
    }

    public void saveNewDocument(DocumentDomainObject document, UserDomainObject user, boolean copying)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        documentSaver.saveNewDocument(user, document, copying);

    }

    public void saveDocument(DocumentDomainObject document,
                             final UserDomainObject user) throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

        DocumentDomainObject oldDocument = getDocument(document.getId());

        documentSaver.saveDocument(document, oldDocument, user);

    }

    public void invalidateDocument(DocumentDomainObject document) {
        documentIndex.indexDocument(document);
        documentCache.remove(document.getId());
        if (StringUtils.isNotBlank(document.getAlias())) {
            aliasCache.remove(document.getAlias());
        }
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public void setDocumentIndex(DocumentIndex documentIndex) {
        this.documentIndex = documentIndex;
    }

    public SectionDomainObject[] getAllSections() {
        String[] parameters = new String[0];
        String[][] sqlRows = getDatabase().execute(new SqlQueryCommand<>(SQL_GET_ALL_SECTIONS, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        SectionDomainObject[] allSections = new SectionDomainObject[sqlRows.length];
        for (int i = 0; i < sqlRows.length; i++) {
            int sectionId = Integer.parseInt(sqlRows[i][0]);
            String sectionName = sqlRows[i][1];
            allSections[i] = new SectionDomainObject(sectionId, sectionName);
        }
        Arrays.sort(allSections, SECTION_NAME_COMPARATOR);
        return allSections;
    }

    public String[][] getParentDocumentAndMenuIdsForDocument(DocumentDomainObject document) {
        String sqlStr = "SELECT meta_id,menu_index FROM childs, menus WHERE menus.menu_id = childs.menu_id AND to_meta_id = ?";
        String[] parameters = new String[]{"" + document.getId()};
        return getDatabase().execute(new SqlQueryCommand<>(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
    }

    public String[][] getAllMimeTypesWithDescriptions(UserDomainObject user) {
        String sqlStr = "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id";
        String[] parameters = new String[]{user.getLanguageIso639_2()};
        return getDatabase().execute(new SqlQueryCommand<>(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
    }

    public String[] getAllMimeTypes() {
        String sqlStr = "SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id";
        String[] params = new String[]{};
        return getDatabase().execute(new SqlQueryCommand<>(sqlStr, params, Utility.STRING_ARRAY_HANDLER));
    }

    public BrowserDocumentDomainObject.Browser[] getAllBrowsers() {
        String sqlStr = "SELECT browser_id, name, value FROM browsers WHERE browser_id != 0";
        String[] parameters = new String[0];
        String[][] sqlResult = getDatabase().execute(new SqlQueryCommand<>(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        List<BrowserDocumentDomainObject.Browser> browsers = new ArrayList<>();
        for (String[] aSqlResult : sqlResult) {
            browsers.add(createBrowserFromSqlRow(aSqlResult));
        }
        return browsers.toArray(new BrowserDocumentDomainObject.Browser[browsers.size()]);
    }

    protected BrowserDocumentDomainObject.Browser createBrowserFromSqlRow(String[] sqlRow) {
        int browserId = Integer.parseInt(sqlRow[0]);
        String browserName = sqlRow[1];
        int browserSpecificity = Integer.parseInt(sqlRow[2]);
        return new BrowserDocumentDomainObject.Browser(browserId, browserName, browserSpecificity);
    }

    public void deleteDocument(final DocumentDomainObject document, UserDomainObject user) {
        DatabaseCommand deleteDocumentCommand = createDeleteDocumentCommand(document);
        getDatabase().execute(deleteDocumentCommand);
        document.accept(new DocumentDeletingVisitor());
        documentIndex.removeDocument(document);
        documentCache.remove(document.getId());
        if (StringUtils.isNotBlank(document.getAlias())) {
            aliasCache.remove(document.getAlias());
        }
    }

    public BrowserDocumentDomainObject.Browser getBrowserById(int browserIdToGet) {
        if (browserIdToGet == BrowserDocumentDomainObject.Browser.DEFAULT.getId()) {
            return BrowserDocumentDomainObject.Browser.DEFAULT;
        }
        String sqlStr = "SELECT browser_id, name, value FROM browsers WHERE browser_id = ?";
        String[] params = new String[]{"" + browserIdToGet};
        String[] sqlRow = getDatabase().execute(new SqlQueryCommand<>(sqlStr, params, Utility.STRING_ARRAY_HANDLER));
        return createBrowserFromSqlRow(sqlRow);
    }

    private DatabaseCommand createDeleteDocumentCommand(final DocumentDomainObject document) {
        if (document instanceof TextDocumentDomainObject) {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) document;

            for (int imageIndex : textDoc.getImages().keySet()) {
                ImageCacheManager.clearCacheEntries(textDoc.getId(), imageIndex);
            }
        }

        final String metaIdStr = "" + document.getId();
        final String metaIdColumn = "meta_id";
        return new CompositeDatabaseCommand(new DatabaseCommand[]{
                new DeleteWhereColumnsEqualDatabaseCommand("document_categories", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("meta_classification", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("childs", "to_meta_id", metaIdStr),
                new SqlUpdateCommand("DELETE FROM childs WHERE menu_id IN (SELECT menu_id FROM menus WHERE meta_id = ?)", new String[]{metaIdStr}),
                new DeleteWhereColumnsEqualDatabaseCommand("menus", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("text_docs", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("texts", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("images", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("roles_rights", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("user_rights", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("url_docs", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("browser_docs", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("fileupload_docs", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("frameset_docs", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("new_doc_permission_sets_ex", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("new_doc_permission_sets", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("doc_permission_sets_ex", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("doc_permission_sets", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("includes", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("includes", "included_meta_id", metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("texts_history", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("images_history", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("childs_history", "to_meta_id", metaIdStr),
                new SqlUpdateCommand("DELETE FROM childs_history WHERE menu_id IN (SELECT menu_id FROM menus_history WHERE meta_id = ?)", new String[]{metaIdStr}),
                new DeleteWhereColumnsEqualDatabaseCommand("menus_history", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("document_properties", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("meta_section", metaIdColumn, metaIdStr),
                new DeleteWhereColumnsEqualDatabaseCommand("meta", metaIdColumn, metaIdStr)
        });
    }

    public Map getAllDocumentTypeIdsAndNamesInUsersLanguage(UserDomainObject user) {
        String[] parameters = new String[]{
                user.getLanguageIso639_2()
        };
        String[][] rows = getDatabase().execute(new SqlQueryCommand<>("SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type", parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        Map<Integer, String> allDocumentTypeIdsAndNamesInUsersLanguage = new TreeMap<>();
        for (String[] row : rows) {
            Integer documentTypeId = Integer.valueOf(row[0]);
            String documentTypeNameInUsersLanguage = row[1];
            allDocumentTypeIdsAndNamesInUsersLanguage.put(documentTypeId, documentTypeNameInUsersLanguage);
        }
        return allDocumentTypeIdsAndNamesInUsersLanguage;
    }

    public Iterator getDocumentsIterator(final IntRange idRange) {
        return new DocumentsIterator(getDocumentIds(idRange));
    }

    public Iterator getDocumentsIterator(final Integer[] documentIds){
        return new DocumentsIterator(getDocumentIds(documentIds));
    }

    public TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingDocument(DocumentDomainObject document) {
        String sqlSelectMenus = "SELECT meta_id, menu_index FROM menus, childs WHERE menus.menu_id = childs.menu_id AND childs.to_meta_id = ? ORDER BY meta_id, menu_index";
        String[] parameters = new String[]{"" + document.getId()};
        String[][] sqlRows = getDatabase().execute(new SqlQueryCommand<>(sqlSelectMenus, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
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

    private int[] getDocumentIds(IntRange idRange) {
        String sqlSelectIds = "SELECT meta_id FROM meta WHERE meta_id >= ? AND meta_id <= ? ORDER BY meta_id";
        String[] params = new String[]{
                "" + idRange.getMinimumInteger(),
                "" + idRange.getMaximumInteger()
        };
        String[] documentIdStrings = getDatabase().execute(new SqlQueryCommand<>(sqlSelectIds, params, Utility.STRING_ARRAY_HANDLER));
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    private int[] getDocumentIds(Integer[] ids){
        String[] placeholders = new String[ids.length];
        Arrays.fill(placeholders, "?");
        String sqlSelectIds = "SELECT meta_id FROM meta WHERE meta_id in (" + String.join(", ", placeholders) + ") ORDER BY meta_id";

        String[] documentIdStrings = getDatabase().execute(new SqlQueryCommand<>(sqlSelectIds, ids, Utility.STRING_ARRAY_HANDLER));
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    public int[] getAllDocumentIds() {
        String[] params = new String[0];
        String[] documentIdStrings = getDatabase().execute(new SqlQueryCommand<>("SELECT meta_id FROM meta ORDER BY meta_id", params, Utility.STRING_ARRAY_HANDLER));
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    public int[] getAllDocumentIdsForIndexing() {
        String[] params = new String[0];
        String[] documentIdStrings = getDatabase().execute(new SqlQueryCommand<>("SELECT meta_id FROM meta ORDER BY doc_type ASC, date_modified DESC", params, Utility.STRING_ARRAY_HANDLER));
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    public Set<Integer> getDocumentIdsForIndexing() {
        final String getDocIdsSQL = "SELECT meta_id FROM meta ORDER BY doc_type ASC, date_modified DESC";

        final SqlQueryCommand<List<Integer>> databaseCommand = new SqlQueryCommand<>(
                getDocIdsSQL, new String[0], Utility.INTEGER_LIST_HANDLER
        );

        return new HashSet<>(getDatabase().execute(databaseCommand));
    }

    public Set<String> getAllDocumentAlias() {
        Set<String> allDocumentAlias = new HashSet<>();
        String[] allAlias = getDatabase().execute(new SqlQueryCommand<>("SELECT value FROM document_properties where key_name = ? ORDER BY value", new String[]{DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS}, Utility.STRING_ARRAY_HANDLER));
        for (String allAlia : allAlias) {
            allDocumentAlias.add(allAlia.toLowerCase());
        }
        return allDocumentAlias;
    }

    public DocumentDomainObject getDocument(String documentIdString) {
        DocumentDomainObject document = null;

        if (null != documentIdString) {
            if (NumberUtils.isDigits(documentIdString)) {
                document = getDocument(new Integer(documentIdString));
            } else {
                String documentIdStringLower = documentIdString.toLowerCase();
                String[] documentIds = aliasCache.get(documentIdStringLower);
                if (documentIds == null || documentIds.length == 0) {
                    documentIds = getDatabase().execute(
                            new SqlQueryCommand<>(SQL_GET_DOCUMENT_ID_FROM_PROPERTIES,
                                    new String[]{DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, documentIdStringLower},
                                    Utility.STRING_ARRAY_HANDLER));
                    aliasCache.put(documentIdStringLower, documentIds);
                }

                if (documentIds.length > 0 && NumberUtils.isDigits(documentIds[0])) {
                    document = getDocument(new Integer(documentIds[0]));
                }
            }
        }
        return document;
    }

    public DocumentPermissionSetMapper getDocumentPermissionSetMapper() {
        return documentPermissionSetMapper;
    }

    public void setDocumentPermissionSetMapper(DocumentPermissionSetMapper documentPermissionSetMapper) {
        this.documentPermissionSetMapper = documentPermissionSetMapper;
    }

    public int getLowestDocumentId() {
        String[] params = new String[0];
        return Integer.parseInt(getDatabase().execute(new SqlQueryCommand<>("SELECT MIN(meta_id) FROM meta", params, Utility.SINGLE_STRING_HANDLER)));
    }

    public int getHighestDocumentId() {
        String[] params = new String[0];
        return Integer.parseInt(getDatabase().execute(new SqlQueryCommand<>("SELECT MAX(meta_id) FROM meta", params, Utility.SINGLE_STRING_HANDLER)));
    }

    public void copyDocument(DocumentDomainObject document,
                             UserDomainObject user) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
        String copyHeadlineSuffix = imcmsServices.getAdminTemplate(COPY_HEADLINE_SUFFIX_TEMPLATE, user, null);
        document.setHeadline(document.getHeadline() + copyHeadlineSuffix);
        document.setAlias(null);
        makeDocumentLookNew(document, user);
        saveNewDocument(document, user, true);
    }

    public List getDocumentsWithPermissionsForRole(RoleDomainObject role) {
        String sqlStr = "SELECT meta_id FROM roles_rights WHERE role_id = ? ORDER BY meta_id";
        final Object[] parameters = new String[]{"" + role.getId()};
        String[] documentIdStrings = getDatabase().execute(new SqlQueryCommand<>(sqlStr, parameters, Utility.STRING_ARRAY_HANDLER));
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

	public void markDocumentAsExported(int docId, final UserDomainObject user){
		log.info("Marking document as exported");
		final DocumentDomainObject document = getDocument(docId);

		try {
			document.setExported(true);
			saveDocument(document, user);

			log.info(String.format("Document marked as exported, id: %d", docId));
		} catch (DocumentSaveException e) {
			log.error(String.format("Failed to save document, id: %d", docId));
		}
	}

	public void updateExportStatus(int docId, boolean exported, final UserDomainObject user){
		log.info("Changing document export status!");

		final DocumentDomainObject document = getDocument(docId);
		final Integer id = document.getId();
		final String type = document.getDocumentType().getName().toLocalizedString("eng");

		try {
			document.setExportAllowed(exported);
			saveDocument(document, user);

			log.info(String.format("Document 'id = %s', 'type = %s' changed export status to = %s", id, type, document.isExportAllowed()));
		} catch (DocumentSaveException e) {
			log.error(String.format("Cannot save document with id = %s, type = %s", id, type), e);
		}
	}

    public DocumentDomainObject getDocument(Integer documentId) {
        return getDocument(documentId, false);
    }

    public DocumentDomainObject getDocument(Integer documentId, boolean renewCache) {
        return documentGetter.getDocument(documentId, renewCache);
    }

    public CategoryMapper getCategoryMapper() {
        return categoryMapper;
    }

    public Database getDatabase() {
        return database;
    }

    public Clock getClock() {
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public ImcmsServices getImcmsServices() {
        return imcmsServices;
    }

    void setCreatedAndModifiedDatetimes(DocumentDomainObject document, Date now) {
        document.setCreatedDatetime(now);
        document.setModifiedDatetime(now);
        document.setActualModifiedDatetime(now);
    }

    String[] getAllKeywords() {
        return getAllKeywords(getDatabase());
    }

    String[] getAllKeywords(Database database) {
        String[] params = new String[0];
        return database.execute(new SqlQueryCommand<>("SELECT code FROM classification", params, Utility.STRING_ARRAY_HANDLER));
    }

    public List<DocumentDomainObject> getDocuments(Collection documentIds) {
        return documentGetter.getDocuments(documentIds);
    }

	public List<DocumentDomainObject> getDocuments(IntRange idRange){
		return documentGetter.getDocuments(Arrays.stream(getDocumentIds(idRange)).boxed().collect(Collectors.toList()));
	}

    public Set<SectionDomainObject> getSections(Collection<Integer> sectionIds) {
        Set<SectionDomainObject> sections = new HashSet<>();
        for (Integer sectionId : sectionIds) {
            sections.add(getSectionById(sectionId));
        }
        return sections;
    }

    public DocumentGetter getDocumentGetter() {
        return documentGetter;
    }

    public void setDocumentGetter(DocumentGetter documentGetter) {
        this.documentGetter = new CachingDocumentGetter(documentGetter, documentCache);
    }

    private void removeNonInheritedCategories(DocumentDomainObject document) {
        Set<CategoryDomainObject> categories = getCategoryMapper().getCategories(document.getCategoryIds());
        for (CategoryDomainObject category : categories) {
            if (!category.getType().isInherited()) {
                document.removeCategoryId(category.getId());
            }
        }
    }

    /**
     * Removes all image cache entries.
     */
    public void clearImageCache() {
        ImageCacheManager.clearAllCacheEntries();
    }

    /**
     * Removes all image cache entries that have been created for a document that is identified
     * with {@code metaId}.
     * <p>
     * If a document contains 3 image fields (1, 2, 3), then the cache entries for these 3 images will be removed.
     *
     * @param metaId the ID of a text document
     */
    public void clearImageCache(int metaId) {
        ImageCacheManager.clearCacheEntries(metaId);
    }

    /**
     * Removes a specific image cache entry that is identified with a document ID ({@code metaId}) and an image field
     * number ({@code no}).
     *
     * @param metaId the ID of a text document
     * @param no     the ID of an image field
     */
    public void clearImageCache(int metaId, int no) {
        ImageCacheManager.clearCacheEntries(metaId, no);
    }

    /**
     * Removes a specific image cache entry that is identified with a document ID ({@code metaId}) and a
     * file ID ({@code fileNo}).
     *
     * @param metaId the ID of a text document
     * @param fileNo the file ID of a {@link FileDocumentDomainObject}
     */
    public void clearImageCache(int metaId, String fileNo) {
        ImageCacheManager.clearCacheEntries(metaId, fileNo);
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

    private static class SectionNameComparator implements Comparator<SectionDomainObject> {
        public int compare(SectionDomainObject section1, SectionDomainObject section2) {
            return section1.getName().compareToIgnoreCase(section2.getName());
        }
    }

    public static class SaveEditedDocumentCommand implements DocumentPageFlow.SaveDocumentCommand {

        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
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
            boolean correctFileForFileDocumentFile = file.equals(DocumentSavingVisitor.getFileForFileDocumentFile(fileDocumentId, fileId));
            boolean fileDocumentHasFile = null != fileDocument.getFile(fileId);
            return super.accept(file, fileDocumentId, fileId)
                    && (!correctFileForFileDocumentFile || !fileDocumentHasFile);
        }
    }

    private static class SectionsSet extends AbstractSet<SectionDomainObject> implements LazilyLoadedObject.Copyable {

        private Map<Integer, SectionDomainObject> byId = new HashMap<>();
        private Map<String, SectionDomainObject> byName = new HashMap<>();

        public boolean add(SectionDomainObject section) {
            byName.put(section.getName().toLowerCase(), section);
            return null == byId.put(section.getId(), section);
        }

        public int size() {
            return byId.size();
        }

        public Iterator<SectionDomainObject> iterator() {
            return byId.values().iterator();
        }

        public SectionDomainObject getSectionById(int sectionId) {
            return byId.get(sectionId);
        }

        public SectionDomainObject getSectionByName(String name) {
            return byName.get(name.toLowerCase());
        }

        public LazilyLoadedObject.Copyable copy() {
            return this;
        }
    }

    private class DocumentsIterator implements Iterator {

        int[] documentIds;
        int index;

        DocumentsIterator(int[] documentIds) {
            this.documentIds = documentIds.clone();
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

    private class SectionsSetLoader implements LazilyLoadedObject.Loader {

        public LazilyLoadedObject.Copyable load() {
            final RowTransformer<SectionDomainObject> rowTransformer = new RowTransformer<SectionDomainObject>() {
                public SectionDomainObject createObjectFromResultSetRow(ResultSet rs) throws SQLException {
                    int sectionId = rs.getInt(1);
                    String sectionName = rs.getString(2);
                    return new SectionDomainObject(sectionId, sectionName);
                }

                public Class<SectionDomainObject> getClassOfCreatedObjects() {
                    return SectionDomainObject.class;
                }
            };
            final CollectionHandler<SectionDomainObject, SectionsSet> collectionHandler = new CollectionHandler<>(new SectionsSet(), rowTransformer);
            return getDatabase().execute(new SqlQueryCommand<>("SELECT section_id, section_name FROM sections", null, collectionHandler));
        }
    }
}
