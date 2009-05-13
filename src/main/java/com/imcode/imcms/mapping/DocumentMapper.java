package com.imcode.imcms.mapping;

import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.SectionDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.DocumentShowSettings;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Clock;
import imcode.util.LazilyLoadedObject;
import imcode.util.SystemClock;
import imcode.util.Utility;
import imcode.util.io.FileUtility;

import java.io.File;
import java.io.FileFilter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.commands.CompositeDatabaseCommand;
import com.imcode.db.commands.DeleteWhereColumnsEqualDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateDatabaseCommand;
import com.imcode.db.handlers.CollectionHandler;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionSelector;
import com.imcode.imcms.api.I18nDisabledException;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.mapping.aop.DocumentAspect;
import com.imcode.imcms.mapping.aop.TextDocumentAspect;

/**
 * NOTES:
 * 
 * DocumentSaver is instantiated using SpringFramework factory
 * in order to support declared (AOP) transactions.
 *   
 * DatabseDocumentGetter is instantiated using SpringFramework factory
 * in order to support declared (AOP) transactions.
 * 
 * There is a big difference between getDocument and getDocumentForShowing:
 * 
 * getDocument returns document instance unmodified.
 * getDocumentForShowing will throw an exception if a user who have requested 
 * the document does not has appropriate permissions or settings. 
 * Additionally returned document instance is advised using AOP interceptors  
 * to provide workaround for legacy code which does not support translated 
 * (i18n) content - currently this is a prototype implementation.
 */
public class DocumentMapper implements DocumentGetter {

	private static final String SQL_GET_ALL_SECTIONS = "SELECT section_id, section_name FROM sections";
    private static final String SQL_GET_DOCUMENT_ID_FROM_PROPERTIES = "SELECT meta_id FROM document_properties WHERE key_name=? AND value=?";

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private Database database;
    private DocumentPermissionSetMapper documentPermissionSetMapper;
    private DocumentIndex documentIndex;
    
    private Clock clock = new SystemClock();
    private ImcmsServices imcmsServices;
    
    /**
     * Gets documents directly form a database bypassing cache.
     * Instantiated using SpringFramework.
     */
    private DatabaseDocumentGetter databaseDocumentGetter;
    
    /** 
     * Provides documents caching. Wraps databaseDocumentGetter.
     */
    private CachingDocumentGetter cachingDocumentGetter;
    
    /**
     * Contain document saving and updating routines. 
     * Instantiated using SpringFramework.
     */
    private DocumentSaver documentSaver ;
    
    private CategoryMapper categoryMapper;

    private LazilyLoadedObject sections;
    private static final SectionNameComparator SECTION_NAME_COMPARATOR = new SectionNameComparator();

    /**
     * Empty constructor for unit testing. 
     */
    public DocumentMapper() {}
    
    public DocumentMapper(ImcmsServices services, Database database) {
        this.imcmsServices = services;
        this.database = database;
        Config config = services.getConfig();
        int documentCacheMaxSize = config.getDocumentCacheMaxSize();
        
        // old code:
        // setDocumentGetter(new FragmentingDocumentGetter(new DatabaseDocumentGetter(services)));
        
        // Document getter is used directly without Fragmented getter
        // DatabseDocumentGetter is instantiated using SpringFramework factory
        // in order to support declared (AOP) transactions.        
        databaseDocumentGetter = (DatabaseDocumentGetter)services.getSpringBean("databaseDocumentGetter");
        databaseDocumentGetter.setServices(services);
        databaseDocumentGetter.getDocumentInitializingVisitor().getTextDocumentInitializer().setDocumentGetter(this);
        
        this.cachingDocumentGetter = new CachingDocumentGetter(databaseDocumentGetter, documentCacheMaxSize);
        
        this.documentPermissionSetMapper = new DocumentPermissionSetMapper(database);
        this.categoryMapper = new CategoryMapper(database);
        // old code:
        // documentSaver = new DocumentSaver(this);
        
        // DocumentSaver is instantiated using SpringFramework
        // in order to support declarative (AOP) transactions.
        this.documentSaver = (DocumentSaver)services.getSpringBean("documentSaver");
        this.documentSaver.setDocumentMapper(this);
        
        initSections();
    }

    public void initSections() {
        sections = new LazilyLoadedObject(new SectionsSetLoader());
    }

    public DocumentSaver getDocumentSaver() {
        return documentSaver ;
    }

    public DocumentDomainObject createDocumentOfTypeFromParent(int documentTypeId, final DocumentDomainObject parent, UserDomainObject user) {
        DocumentDomainObject newDocument;
        try {
            if ( DocumentTypeDomainObject.TEXT_ID == documentTypeId) {
                newDocument = (DocumentDomainObject) parent.clone();
                TextDocumentDomainObject newTextDocument = (TextDocumentDomainObject) newDocument;
                newTextDocument.removeAllTexts();
                newTextDocument.removeAllImages();
                newTextDocument.removeAllIncludes();
                newTextDocument.removeAllMenus();
                newTextDocument.removeAllContentLoops();
                setTemplateForNewTextDocument( newTextDocument, user, parent );
            } else {
                newDocument = DocumentDomainObject.fromDocumentTypeId(documentTypeId);
                newDocument.setAttributes((DocumentDomainObject.Attributes) parent.getAttributes().clone());
                newDocument.setMeta(parent.getMeta().clone());
            }
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
        
        Meta meta = newDocument.getMeta();
        
        meta.setId(null);
        //meta.setDocumentVersion(null);
        //meta.setDocumentVersionTag(null);
        
        for (I18nMeta i18nMeta: meta.getI18nMetas()) {
        	i18nMeta.setHeadline("");
        	i18nMeta.setMenuText("");
        	i18nMeta.setMenuImageURL("");
        	i18nMeta.getKeywords().clear();
        } 
                
        newDocument.setProperties(new HashMap());
        makeDocumentLookNew( newDocument, user );
        removeNonInheritedCategories(newDocument) ;
        return newDocument;
    }

    void setTemplateForNewTextDocument( TextDocumentDomainObject newTextDocument, UserDomainObject user,
                                        final DocumentDomainObject parent ) {
        DocumentPermissionSetTypeDomainObject documentPermissionSetType = user.getDocumentPermissionSetTypeFor( parent );
        String templateName = null;
        if ( DocumentPermissionSetTypeDomainObject.RESTRICTED_1.equals(documentPermissionSetType) ) {
            templateName = newTextDocument.getDefaultTemplateNameForRestricted1();
        } else if ( DocumentPermissionSetTypeDomainObject.RESTRICTED_2.equals(documentPermissionSetType) ) {
            templateName = newTextDocument.getDefaultTemplateNameForRestricted2();
        }
        if ( null == templateName && parent instanceof TextDocumentDomainObject ) {
            templateName = ( (TextDocumentDomainObject)parent ).getDefaultTemplateName();
        }
        if ( null != templateName ) {
            newTextDocument.setTemplateName( templateName );
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
        String[][] sqlRows = (String[][]) getDatabase().execute(new SqlQueryCommand(SQL_GET_ALL_SECTIONS, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        SectionDomainObject[] allSections = new SectionDomainObject[sqlRows.length];
        for (int i = 0; i < sqlRows.length; i++) {
            int sectionId = Integer.parseInt(sqlRows[i][0]);
            String sectionName = sqlRows[i][1];
            allSections[i] = new SectionDomainObject(sectionId, sectionName);
        }
        Arrays.sort(allSections, SECTION_NAME_COMPARATOR);
        return allSections;
    }

    public DocumentReference getDocumentReference(DocumentDomainObject document) {
        return getDocumentReference(document.getId());
    }

    public DocumentReference getDocumentReference(int childId) {
        return new GetterDocumentReference(childId, cachingDocumentGetter);
    }

    public SectionDomainObject getSectionById(int sectionId) {
        SectionsSet sectionsSet = (SectionsSet) sections.get();
        return sectionsSet.getSectionById(sectionId) ;
    }

    public SectionDomainObject getSectionByName(String name) {
        SectionsSet sectionsSet = (SectionsSet) sections.get();
        return sectionsSet.getSectionByName(name) ;
    }

    public void saveNewDocument(DocumentDomainObject document, UserDomainObject user, boolean copying)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        documentSaver.saveNewDocument(user, document, copying);

    }

    /**
     * Updates existing document.
     */
    public void saveDocument(DocumentDomainObject document,
                             final UserDomainObject user) throws DocumentSaveException , NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException
    {

    	DocumentDomainObject oldDocument = 
    		getDocument(document.getId(), document.getMeta().getVersion().getNumber());

        documentSaver.updateDocument(document, oldDocument, user);
    }
    
    /**
     * Published working version of a document.
     */
    // TODO: Check exceptions 
    public void publishWorkingDocument(DocumentDomainObject document, UserDomainObject user) 
    throws DocumentSaveException, NoPermissionToEditDocumentException {	
	    documentSaver.publishWorkingDocument(document, user);	    
	}
    
    
    /**
     * Creates document's working version from previous (existing) version.
     * 
     * @param documentId document id
     * @param documentVersion any existing document version
     * 
     * @return new working version of a document from previous (existing) version.
     */
    // TODO: Check exceptions 
    public void createWorkingDocument(Integer documentId, Integer documentVersion, UserDomainObject user) 
    throws DocumentSaveException, NoPermissionToEditDocumentException {
    	DocumentDomainObject document = getDocument(documentId, documentVersion);
    	
	    documentSaver.createWorkingDocumentFromExisting(document, user);
	}
            
    
    /**
     * Returns document by its id and version.
     * 
     * Expensive call - returned document is not cached.
     * 
     * @param documentId document id
     * @param documentVersion document version. If not given (null) then published version is returned.
     * @return document or null if document can not be found.  
     */
    public DocumentDomainObject getDocument(Integer documentId, Integer documentVersion) {
    	return databaseDocumentGetter.getDocument(documentId, documentVersion);
	}
    
    
    /**
     * Returns document for showing by document id and version. 
     * 
     * Expensive call - returned document is not cached.
     */
    public DocumentDomainObject getDocumentForShowing(Integer documentId, Integer versionNumber, UserDomainObject user) {
    	DocumentDomainObject document = getDocument(documentId, versionNumber);
    	
    	return createDocumentShowInterceptor(document, user);
	}
    
    
    public boolean hasPublishedVersion(Integer documentId) {
    	return cachingDocumentGetter.getPublishedDocument(documentId) != null;
    }    

    public void invalidateDocument(DocumentDomainObject document) {
        documentIndex.indexDocument(document);
        
        cachingDocumentGetter.removeDocumentFromCache(document.getId());
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public String[][] getParentDocumentAndMenuIdsForDocument(DocumentDomainObject document) {
        String sqlStr = "SELECT meta_id,menu_index FROM childs, menus WHERE menus.menu_id = childs.menu_id AND to_meta_id = ?";
        String[] parameters = new String[]{"" + document.getId()};
        return (String[][]) getDatabase().execute(new SqlQueryCommand(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
    }

    public String[][] getAllMimeTypesWithDescriptions(UserDomainObject user) {
        String sqlStr = "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id";
        String[] parameters = new String[]{user.getLanguageIso639_2()};
        return (String[][]) getDatabase().execute(new SqlQueryCommand(sqlStr, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
    }

    public String[] getAllMimeTypes() {
        String sqlStr = "SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id";
        String[] params = new String[]{};
        return (String[]) getDatabase().execute(new SqlQueryCommand(sqlStr, params, Utility.STRING_ARRAY_HANDLER));
    }


    public void deleteDocument(final DocumentDomainObject document, UserDomainObject user) {
        DatabaseCommand deleteDocumentCommand = createDeleteDocumentCommand(document);
        getDatabase().execute(deleteDocumentCommand);
        document.accept(new DocumentDeletingVisitor());
        documentIndex.removeDocument(document);
        
        cachingDocumentGetter.removeDocumentFromCache(document.getId());
    }

    // TODO: DELETE DOCUMENT
    private DatabaseCommand createDeleteDocumentCommand(final DocumentDomainObject document) {
        final String metaIdStr = "" + document.getId();
        final String metaIdColumn = "meta_id";
        return new CompositeDatabaseCommand(new DatabaseCommand[]{
            new DeleteWhereColumnsEqualDatabaseCommand("document_categories", metaIdColumn, metaIdStr),
            // TODO: classification and meta_classification is replaced by keywords table.
            // new DeleteWhereColumnsEqualDatabaseCommand("meta_classification", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("childs", "to_meta_id", metaIdStr),
            new SqlUpdateDatabaseCommand("DELETE FROM childs WHERE menu_id IN (SELECT menu_id FROM menus WHERE meta_id = ?)", new String[]{metaIdStr}),
            new DeleteWhereColumnsEqualDatabaseCommand("menus", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("text_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("texts", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("images", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("roles_rights", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("user_rights", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("url_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("fileupload_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("frameset_docs", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("new_doc_permission_sets_ex", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("new_doc_permission_sets", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("doc_permission_sets_ex", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("doc_permission_sets", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("includes", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("includes", "included_meta_id", metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("texts_history", metaIdColumn, metaIdStr ),
            new DeleteWhereColumnsEqualDatabaseCommand("images_history", metaIdColumn, metaIdStr ),
            new DeleteWhereColumnsEqualDatabaseCommand("childs_history", "to_meta_id", metaIdStr ),
            new SqlUpdateDatabaseCommand("DELETE FROM childs_history WHERE menu_id IN (SELECT menu_id FROM menus_history WHERE meta_id = ?)", new String[] {metaIdStr} ),
            new DeleteWhereColumnsEqualDatabaseCommand("menus_history", metaIdColumn, metaIdStr ),
            new DeleteWhereColumnsEqualDatabaseCommand("document_properties", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("meta_section", metaIdColumn, metaIdStr),
            new DeleteWhereColumnsEqualDatabaseCommand("meta", metaIdColumn, metaIdStr)
        });
    }

    public Map getAllDocumentTypeIdsAndNamesInUsersLanguage(UserDomainObject user) {
        String[] parameters = new String[]{
            user.getLanguageIso639_2()
        };
        String[][] rows = (String[][]) getDatabase().execute(new SqlQueryCommand("SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type", parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
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
        String[][] sqlRows = (String[][]) getDatabase().execute(new SqlQueryCommand(sqlSelectMenus, parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        TextDocumentMenuIndexPair[] documentMenuPairs = new TextDocumentMenuIndexPair[sqlRows.length];
        for (int i = 0; i < sqlRows.length; i++) {
            String[] sqlRow = sqlRows[i];
            int containingDocumentId = Integer.parseInt(sqlRow[0]);
            int menuIndex = Integer.parseInt(sqlRow[1]);
            TextDocumentDomainObject containingDocument = (TextDocumentDomainObject) getPublishedDocument(containingDocumentId);
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
        String[] documentIdStrings = (String[]) getDatabase().execute(new SqlQueryCommand(sqlSelectIds, params, Utility.STRING_ARRAY_HANDLER));
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    public int[] getAllDocumentIds() {
        String[] params = new String[0];
        String[] documentIdStrings = (String[]) getDatabase().execute(new SqlQueryCommand("SELECT meta_id FROM meta ORDER BY meta_id", params, Utility.STRING_ARRAY_HANDLER));
        int[] documentIds = new int[documentIdStrings.length];
        for (int i = 0; i < documentIdStrings.length; i++) {
            documentIds[i] = Integer.parseInt(documentIdStrings[i]);
        }
        return documentIds;
    }

    public Set<String> getAllDocumentAlias() {
        Set<String> allDocumentAlias = new HashSet<String>();
        String[] allAlias = (String[]) getDatabase().execute(new SqlQueryCommand("SELECT value FROM document_properties where key_name = ? ORDER BY value", new String[] { DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS}, Utility.STRING_ARRAY_HANDLER));
        for (int i = 0; i < allAlias.length; i ++) {
            allDocumentAlias.add(allAlias[i].toLowerCase()) ;
        }
        return allDocumentAlias;
    }
    
    public DocumentDomainObject getDocument(String documentIdString) {
        Integer documentId = getDocumentId(documentIdString);
        
        return documentId == null 
        	? null
        	: getDocument(documentId);
    }
    
    /**
     * @return custom document's version for showing.
     */
    public DocumentDomainObject getDocumentForShowing(String documentIdString, Integer versionNumber, UserDomainObject user) {
    	DocumentDomainObject document = getDocument(documentIdString, versionNumber);
    	
    	return createDocumentShowInterceptor(document, user);
    }
    
    public DocumentDomainObject getDocument(String documentIdString, Integer versionNumber) {
        Integer documentId = getDocumentId(documentIdString);
        
        return documentId == null 
        	? null
        	: getDocument(documentId, versionNumber);
    }    
    
    private Integer getDocumentId(String documentIdString) {
    	if (documentIdString == null) {
    		return null;
    	}
    	
        if (NumberUtils.isDigits(documentIdString)) {
        	return Integer.valueOf(documentIdString);
        }
        

        String[] documentIds = (String[]) getDatabase().execute(
                new SqlQueryCommand(SQL_GET_DOCUMENT_ID_FROM_PROPERTIES,
                        new String[] { DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, documentIdString.toLowerCase() },
                        Utility.STRING_ARRAY_HANDLER));

        if (documentIds.length > 0 && NumberUtils.isDigits(documentIds[0])) {
        	return Integer.valueOf(documentIds[0]);
        } else {
        	return null;
        }    	
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
        return Integer.parseInt((String) getDatabase().execute(new SqlQueryCommand("SELECT MIN(meta_id) FROM meta", params, Utility.SINGLE_STRING_HANDLER)));
    }

    public int getHighestDocumentId() {
        String[] params = new String[0];
        return Integer.parseInt((String) getDatabase().execute(new SqlQueryCommand("SELECT MAX(meta_id) FROM meta", params, Utility.SINGLE_STRING_HANDLER)));
    }

    public DocumentDomainObject copyDocument(DocumentDomainObject document,
                             UserDomainObject user) throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {
    	document = document.clone();
        String copyHeadlineSuffix = imcmsServices.getAdminTemplate(COPY_HEADLINE_SUFFIX_TEMPLATE, user, null);

        document.setAlias(null);
        makeDocumentLookNew(document, user);

        for (I18nMeta i18nMeta: document.getMeta().getI18nMetas()) {
            i18nMeta.setHeadline(i18nMeta.getHeadline() + copyHeadlineSuffix);
        }

        saveNewDocument(document, user, true);
        
        return document;
    }

    public List getDocumentsWithPermissionsForRole(RoleDomainObject role) {
        String sqlStr = "SELECT meta_id FROM roles_rights WHERE role_id = ? ORDER BY meta_id";
        final Object[] parameters = new String[]{"" + role.getId()};
        String[] documentIdStrings = (String[]) getDatabase().execute(new SqlQueryCommand(sqlStr, parameters, Utility.STRING_ARRAY_HANDLER));
        final int[] documentIds = Utility.convertStringArrayToIntArray(documentIdStrings);
        return new AbstractList() {
            public Object get(int index) {
                return getPublishedDocument(documentIds[index]);
            }

            public int size() {
                return documentIds.length;
            }
        };
    }
    
    
    /** 
     * @return custom version of a document.
     */
    public DocumentDomainObject getDocument(Integer documentId, DocumentVersionSelector versionSelector) {
    	return versionSelector.getDocument(this, documentId);
    }
    
    /**
     * Returns latest version of a document.
     *  
     * Please note this call is expensive since returned document is not cached.
     * 
     * @param documentId document id
     * @returns latest version of a document
     */
    private DocumentDomainObject getLatestDocumentVersion(Integer documentId) { 
        return databaseDocumentGetter.getDocument(documentId);
    }
    
    /**
     * Returns latest version of a document.
     *  
     * Please note this call is expensive since returned document is not cached.
     * 
     * @param documentId document id
     * @returns latest version of a document
     */
    public DocumentDomainObject getLatestDocumentVersionForShowing(Integer documentId, UserDomainObject user) { 
        DocumentDomainObject document = getLatestDocumentVersion(documentId);
        
        return document == null ? null : createDocumentShowInterceptor(document, user);
    }    
    
    /**
     * Returns latest document version.
     * 
     * @param documentId document id
     * 
     * @return latest version of the document or null if document does not exist.
     * 
     * TODO: optimize, use cache
     */
    public DocumentDomainObject getDocument(Integer documentId) { 
        //return cachingDocumentGetter.getDocument(documentId);
    	return getLatestDocumentVersion(documentId);
    } 
    
    /**
     * Returns published version of a document.
     * 
     * @param documentId document id
     * 
     * @return published version of the document or null if document does not exist.
     * 
     * TODO: Check all calls to this method and replace with getDocument where appropriate.
     */
    public DocumentDomainObject getPublishedDocument(Integer documentId) { 
        return cachingDocumentGetter.getPublishedDocument(documentId);
    }    
            
    /**
     * Returns working version of a document.
     * 
     * @param documentId document id
     * 
     * @return working version of a document or null if document does not exist
     */
    public DocumentDomainObject getWorkingDocument(Integer documentId) {
    	return cachingDocumentGetter.getWorkingDocument(documentId);
    }    
    
    /**
     * If working document does not exists creates one from public version.
     */
    /*
    public DocumentDomainObject getWorkingDocument(Integer documentId,
    		UserDomainObject user) {
    	
    	DocumentDomainObject document = cachingDocumentGetter.getWorkingDocument(documentId);
    	
        if (document == null) {
        	document = cachingDocumentGetter.getDocument(documentId);
        	
        	if (document != null) {
        		try {
        			saveAsWorkingWersion(document, user);
        		} catch (DocumentSaveException e) {
        			throw new RuntimeException(e);
        		}
        	}
        } 
        
        return document;
    }
    */
    
    /**
     * Returns publlished or working (depending on user show settings) document for showing.
     * 
     * @param documentIdString document's id or alias
     * @param user an user requesting a document 
     * 
     * @return published or working AOP adviced document or null if document does not exist.  
     */
    public DocumentDomainObject getDocumentForShowing(String documentIdString, UserDomainObject user) {
        Integer documentId = getDocumentId(documentIdString);
        
        return documentId == null
        	? null
        	: getDocumentForShowing(documentId, user);	
    }
    
    /**
     * @return all document's versions 
     */
    public List<DocumentVersion> getDocumentVersions(Integer documentId) {
    	return documentSaver.getMetaDao().getDocumentVersions(documentId);
    }
    
        
    /** 
     * Returns published, working or custom document version depending on user's view settings
     * and i18n meta settings. 
     * 
     * @param documentId document id
     * @param user an user requesting a document 
     *  
     * @return advised published, working or custom document version depending on user's view settings
     * or null if document does not exist
     */
    public DocumentDomainObject getDocumentForShowing(Integer documentId, UserDomainObject user) {
    	DocumentDomainObject document = null;
    	DocumentShowSettings showSettings = user.getDocumentShowSettings();
		
    	switch (showSettings.getVersionSelector().getType()) {
		case PUBLISHED:	
			document = getPublishedDocument(documentId);
			break;
			
		case WORKING:
			document = getWorkingDocument(documentId);
			
			if (document == null) {
				document = getPublishedDocument(documentId);
				
				if (document != null) {					
					try {
						createWorkingDocument(documentId, document.getMeta().getVersion().getNumber(), user);
					} catch (DocumentSaveException e) {
						throw new RuntimeException(e);
					}
					
					document = getWorkingDocument(documentId);
				}
			}
			
			break;			

		case CUSTOM:
			document = getDocument(documentId, showSettings.getVersionSelector().getVersionNumber());
			break;
			
		default:
			document = null;
		}
    	
    	return createDocumentShowInterceptor(document, user);    	
    } 
    
    /**
     * Creates document interceptor based on document show settings.
     * 
     * TODO: prototype implementation - optimize 
     */
    private DocumentDomainObject createDocumentShowInterceptor(DocumentDomainObject document, UserDomainObject user) {    	
    	DocumentShowSettings showSettings = user.getDocumentShowSettings();
    	
    	if (document != null) {
    		/*
    		 * Current document language.
    		 * 
    		 * If an user is allowed to see a document's content in a current language
    		 * then current document language is set current language otherwise
    		 * it is set to default language. 
    		 */
    		I18nLanguage currentDocumentLanguage = I18nSupport.getCurrentLanguage();
    		
    		if (!I18nSupport.getCurrentIsDefault() && !showSettings.isIgnoreI18nShowMode() ) {            
    			I18nMeta i18nMeta = document.getI18nMeta(I18nSupport.getCurrentLanguage());
            
    			if (!i18nMeta.getEnabled()) {
    				if (document.getMeta().isShowDisabledI18nContentInDefaultLanguage()) {
    					currentDocumentLanguage = I18nSupport.getDefaultLanguage();
    				} else {
    					throw new I18nDisabledException(document, I18nSupport.getCurrentLanguage());
    					// 	return null
    				}
    			}
    		}
    		
    		// TODO: prototype implementation - optimize 	
        	AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(document);            	
            aspectJProxyFactory.setProxyTargetClass(true);
            aspectJProxyFactory.addAspect(new DocumentAspect(currentDocumentLanguage));       
            
            if (document instanceof TextDocumentDomainObject) {
                aspectJProxyFactory.addAspect(new TextDocumentAspect(currentDocumentLanguage));            	
            }
                    	
        	document = aspectJProxyFactory.getProxy();    		
    	}
    	
    	return document;
    	
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

    // TODO: classification and meta_classification is replaced by keywords table.
//    String[] getAllKeywords() {
//        String[] params = new String[0];
//        return (String[]) getDatabase().execute(new SqlQueryCommand("SELECT code FROM classification", params, Utility.STRING_ARRAY_HANDLER));
//    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public void setDocumentPermissionSetMapper(DocumentPermissionSetMapper documentPermissionSetMapper) {
        this.documentPermissionSetMapper = documentPermissionSetMapper;
    }

    public void setDocumentIndex(DocumentIndex documentIndex) {
        this.documentIndex = documentIndex;
    }

    public List getDocuments(Collection documentIds) {
        return cachingDocumentGetter.getDocuments(documentIds) ;
    }

    public Set getSections(Collection sectionIds) {
        Set sections = new HashSet() ;
        for ( Iterator iterator = sectionIds.iterator(); iterator.hasNext(); ) {
            Integer sectionId = (Integer) iterator.next();
            sections.add(getSectionById(sectionId.intValue())) ;
        }
        return sections ;
    }

    public CachingDocumentGetter getDocumentGetter() {
        return cachingDocumentGetter;
    }

    private void removeNonInheritedCategories(DocumentDomainObject document) {
        Set categories = getCategoryMapper().getCategories(document.getCategoryIds());
        for ( Iterator iterator = categories.iterator(); iterator.hasNext(); ) {
            CategoryDomainObject category = (CategoryDomainObject)iterator.next();
            if (!category.getType().isInherited()) {
                document.removeCategoryId(category.getId());
            }
        }
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
        int index;

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
            
            int documentId = documentIds[index++];
            DocumentDomainObject document = getPublishedDocument(documentId);
            
            if (document == null) {
            	document = getWorkingDocument(documentId);
            }
            
            return document;
        }
    }

    public static class SaveEditedDocumentCommand implements DocumentPageFlow.SaveDocumentCommand {

        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().saveDocument(document, user);
        }
    }
    
    
    public static class PublushDocumentCommand implements DocumentPageFlow.SaveDocumentCommand {

        public void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().publishWorkingDocument( document, user );
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

    private static class SectionNameComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            SectionDomainObject section1 = (SectionDomainObject) o1;
            SectionDomainObject section2 = (SectionDomainObject) o2;
            return section1.getName().compareToIgnoreCase(section2.getName());
        }
    }

    private static class SectionsSet extends AbstractSet implements LazilyLoadedObject.Copyable {

        private Map byId = new HashMap() ;
        private Map byName = new HashMap() ;

        public boolean add(Object o) {
            SectionDomainObject section = (SectionDomainObject) o ;
            byName.put(section.getName().toLowerCase(), section) ;
            return null == byId.put(new Integer(section.getId()), section) ;
        }

        public int size() {
            return byId.size() ;
        }

        public Iterator iterator() {
            return byId.values().iterator() ;
        }

        public SectionDomainObject getSectionById(int sectionId) {
            return (SectionDomainObject) byId.get(new Integer(sectionId)) ;
        }

        public SectionDomainObject getSectionByName(String name) {
            return (SectionDomainObject) byName.get(name.toLowerCase()) ;
        }

        public LazilyLoadedObject.Copyable copy() {
            return this ;
        }
    }

    private class SectionsSetLoader implements LazilyLoadedObject.Loader {

        public LazilyLoadedObject.Copyable load() {
            return (SectionsSet) getDatabase().execute(new SqlQueryCommand("SELECT section_id, section_name FROM sections", null, new CollectionHandler(new SectionsSet(), new RowTransformer() {
                public Object createObjectFromResultSetRow(ResultSet rs) throws SQLException {
                    int sectionId = rs.getInt(1);
                    String sectionName = rs.getString(2);
                    return new SectionDomainObject(sectionId, sectionName);
                }

                public Class getClassOfCreatedObjects() {
                    return SectionDomainObject.class;
                }
            }))) ;
        }
    }

	public CachingDocumentGetter getCachingDocumentGetter() {
		return cachingDocumentGetter;
	}

	public void setCachingDocumentGetter(CachingDocumentGetter cachingDocumentGetter) {
		this.cachingDocumentGetter = cachingDocumentGetter;
	}

	public void setDocumentSaver(DocumentSaver documentSaver) {
		this.documentSaver = documentSaver;
	}

	public DatabaseDocumentGetter getDatabaseDocumentGetter() {
		return databaseDocumentGetter;
	}

	public void setDatabaseDocumentGetter(
			DatabaseDocumentGetter databaseDocumentGetter) {
		this.databaseDocumentGetter = databaseDocumentGetter;
	}
}
