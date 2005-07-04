package com.imcode.imcms.mapping;

import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.api.Document;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.impl.MockDatabase;
import imcode.server.db.commands.CompositeDatabaseCommand;
import imcode.server.db.commands.DeleteWhereColumnEqualsDatabaseCommand;
import imcode.server.db.commands.UpdateDatabaseCommand;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.*;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Clock;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.math.IntRange;
import org.apache.oro.text.perl.Perl5Util;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

public class DefaultDocumentMapper implements DocumentMapper {

    private static final String SQL_GET_ALL_SECTIONS = "SELECT section_id, section_name FROM sections";

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private Database database;
    private DocumentPermissionSetMapper documentPermissionSetMapper;
    private DocumentIndex documentIndex;
    private Map documentCache;
    private Clock clock;
    private ImcmsServices imcmsServices;
    private DocumentGetter documentGetter ;
    private DocumentSaver documentSaver ;
    private CategoryMapper categoryMapper;

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

    public DefaultDocumentMapper() {
    }

    public DefaultDocumentMapper(ImcmsServices services, Database database, DocumentGetter documentGetter,
                                 DocumentPermissionSetMapper documentPermissionSetMapper, DocumentIndex documentIndex,
                                 Clock clock, Config config, CategoryMapper categoryMapper) {

        this.database = database;
        this.clock = clock;
        this.imcmsServices = services;
        this.documentPermissionSetMapper = documentPermissionSetMapper;
        this.documentIndex = documentIndex;
        int documentCacheMaxSize = config.getDocumentCacheMaxSize();
        documentCache = Collections.synchronizedMap(new LRUMap(documentCacheMaxSize)) ;
        setDocumentGetter(documentGetter);
        this.categoryMapper = categoryMapper;
        documentSaver = new DocumentSaver(this);
    }

    public void setDocumentGetter(DocumentGetter documentGetter) {
        this.documentGetter = new CachingDocumentGetter(documentGetter, documentCache);
    }

    public DocumentSaver getDocumentSaver() {
        return documentSaver ;
    }

    public DocumentDomainObject createDocumentOfTypeFromParent(int documentTypeId, final DocumentDomainObject parent,
                                                               UserDomainObject user) throws NoPermissionToCreateDocumentException {
        if (!user.canCreateDocumentOfTypeIdFromParent(documentTypeId, parent)) {
            throw new NoPermissionToCreateDocumentException("User can't create documents from document " + parent.getId());
        }
        DocumentDomainObject newDocument;
        try {
            if ( DocumentTypeDomainObject.TEXT_ID == documentTypeId) {
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

    void makeDocumentLookNew( DocumentDomainObject document, UserDomainObject user ) {
        Date now = new Date();
        document.setCreator(user);
        setCreatedAndModifiedDatetimes(document, now);
        document.setPublicationStartDatetime(now);
        document.setArchivedDatetime(null);
        document.setPublicationEndDatetime(null);
        document.setPublicationStatus(Document.PublicationStatus.NEW);
    }

    public SectionDomainObject[] getAllSections() {
        String[] parameters = new String[0];
        String[][] sqlRows = getDatabase().execute2dArrayQuery(SQL_GET_ALL_SECTIONS, parameters);
        SectionDomainObject[] allSections = new SectionDomainObject[sqlRows.length];
        for (int i = 0; i < sqlRows.length; i++) {
            int sectionId = Integer.parseInt(sqlRows[i][0]);
            String sectionName = sqlRows[i][1];
            allSections[i] = new SectionDomainObject(sectionId, sectionName);
        }
        Arrays.sort(allSections, new SectionNameComparator());
        return allSections;
    }

    public DocumentDomainObject getDocument(int documentId) {
        return getDocument(new DocumentId(documentId)) ;
    }

    public DocumentReference getDocumentReference(DocumentDomainObject document) {
        return getDocumentReference(document.getId());
    }

    DocumentReference getDocumentReference(int childId) {
        return new DocumentReference(childId, documentGetter);
    }

    public SectionDomainObject getSectionById(int sectionId) {
        String[] params = new String[]{"" + sectionId};
        String sectionName = getDatabase().executeStringQuery("SELECT section_name FROM sections WHERE section_id = ?", params);
        if (null == sectionName) {
            return null;
        }
        return new SectionDomainObject(sectionId, sectionName);
    }

    public SectionDomainObject getSectionByName(String name) {
        String[] params = new String[]{name};
        String[] sectionSqlRow = getDatabase().executeArrayQuery("SELECT section_id, section_name FROM sections WHERE section_name = ?", params);
        if (0 == sectionSqlRow.length) {
            return null;
        }
        int sectionId = Integer.parseInt(sectionSqlRow[0]);
        String sectionName = sectionSqlRow[1];
        return new SectionDomainObject(sectionId, sectionName);
    }

    public void saveNewDocument(DocumentDomainObject document, UserDomainObject user)
            throws MaxCategoryDomainObjectsOfTypeExceededException, NoPermissionToAddDocumentToMenuException {

        documentSaver.saveNewDocument(user, document);

    }

    public void saveDocument(DocumentDomainObject document,
                             final UserDomainObject user) throws MaxCategoryDomainObjectsOfTypeExceededException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException
    {

        DocumentDomainObject oldDocument = getDocument(document.getId());

        documentSaver.saveDocument(document, oldDocument, user);

    }

    public void invalidateDocument(DocumentDomainObject document) {
        documentIndex.indexDocument(document);
        documentCache.remove(new DocumentId(document.getId()));
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

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public String[][] getParentDocumentAndMenuIdsForDocument(DocumentDomainObject document) {
        String sqlStr = "SELECT meta_id,menu_index FROM childs, menus WHERE menus.menu_id = childs.menu_id AND to_meta_id = ?";
        String[] parameters = new String[]{"" + document.getId()};
        return getDatabase().execute2dArrayQuery(sqlStr, parameters);
    }

    public String[][] getAllMimeTypesWithDescriptions(UserDomainObject user) {
        String sqlStr = "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id";
        String[] parameters = new String[]{user.getLanguageIso639_2()};
        String[][] mimeTypes = getDatabase().execute2dArrayQuery(sqlStr, parameters);
        return mimeTypes;
    }

    public String[] getAllMimeTypes() {
        String sqlStr = "SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id";
        String[] params = new String[]{};
        String[] mimeTypes = getDatabase().executeArrayQuery(sqlStr, params);
        return mimeTypes;
    }

    public void addToMenu(TextDocumentDomainObject parentDocument, int parentMenuIndex,
                          DocumentDomainObject documentToAddToMenu, UserDomainObject user) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException {
        parentDocument.getMenu(parentMenuIndex).addMenuItem(new MenuItemDomainObject(this.getDocumentReference(documentToAddToMenu)));
        saveDocument(parentDocument, user);
    }

    public BrowserDocumentDomainObject.Browser[] getAllBrowsers() {
        String sqlStr = "SELECT browser_id, name, value FROM browsers WHERE browser_id != 0";
        String[] parameters = new String[0];
        String[][] sqlResult = getDatabase().execute2dArrayQuery(sqlStr, parameters);
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
        String[] sqlRow = getDatabase().executeArrayQuery(sqlStr, params);
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
        getDatabase().executeCommand(deleteDocumentCommand);
        document.accept(new DocumentDeletingVisitor());
        documentIndex.removeDocument(document);
        documentCache.remove(new DocumentId(document.getId()));
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
        String[][] rows = getDatabase().execute2dArrayQuery("SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type", parameters);
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
        String[][] sqlRows = getDatabase().execute2dArrayQuery(sqlSelectMenus, parameters);
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
        String[] documentIdStrings = getDatabase().executeArrayQuery(sqlSelectIds, params);
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    public int[] getAllDocumentIds() {
        String[] params = new String[0];
        String[] documentIdStrings = getDatabase().executeArrayQuery("SELECT meta_id FROM meta ORDER BY meta_id", params);
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

    public int getLowestDocumentId() {
        String[] params = new String[0];
        return Integer.parseInt(getDatabase().executeStringQuery("SELECT MIN(meta_id) FROM meta", params));
    }

    public int getHighestDocumentId() {
        String[] params = new String[0];
        return Integer.parseInt(getDatabase().executeStringQuery("SELECT MAX(meta_id) FROM meta", params));
    }

    public void copyDocument(DocumentDomainObject selectedChild,
                             UserDomainObject user) throws NoPermissionToAddDocumentToMenuException {
        String copyHeadlineSuffix = imcmsServices.getAdminTemplate(COPY_HEADLINE_SUFFIX_TEMPLATE, user, null);
        selectedChild.setHeadline(selectedChild.getHeadline() + copyHeadlineSuffix);
        makeDocumentLookNew(selectedChild, user);
        imcmsServices.getDefaultDocumentMapper().saveNewDocument(selectedChild, user);
    }

    public List getDocumentsWithPermissionsForRole(RoleDomainObject role) {
        String sqlStr = "SELECT meta_id FROM roles_rights WHERE role_id = ? ORDER BY meta_id";
        String[] documentIdStrings = getDatabase().executeArrayQuery(sqlStr, new String[]{"" + role.getId()});
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

    public DocumentDomainObject getDocument(DocumentId documentId) {
        return documentGetter.getDocument(documentId);
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

    public ImcmsServices getImcmsServices() {
        return imcmsServices;
    }

    void setCreatedAndModifiedDatetimes(DocumentDomainObject document, Date now) {
        document.setCreatedDatetime(now);
        document.setModifiedDatetime(now);
        document.setActualModifiedDatetime(now);
    }

    String[] getAllKeywords() {
        String[] params = new String[0];
        return getDatabase().executeArrayQuery("SELECT code FROM classification", params);
    }

    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public void setDatabase(MockDatabase database) {
        this.database = database;
    }

    public void setDocumentPermissionSetMapper(DocumentPermissionSetMapper documentPermissionSetMapper) {
        this.documentPermissionSetMapper = documentPermissionSetMapper;
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

        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException {
            Imcms.getServices().getDefaultDocumentMapper().saveDocument(document, user);
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
