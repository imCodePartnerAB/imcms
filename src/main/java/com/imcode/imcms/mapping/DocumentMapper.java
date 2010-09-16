package com.imcode.imcms.mapping;

import com.imcode.imcms.DocIdentityCleanerVisitor;
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
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.*;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Clock;
import imcode.util.SystemClock;
import imcode.util.io.FileUtility;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.oro.text.perl.Perl5Util;

import com.imcode.db.Database;
import com.imcode.imcms.api.*;
import com.imcode.imcms.dao.NativeQueriesDao;
import com.imcode.imcms.flow.DocumentPageFlow;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * NOTES:
 * 
 * DocumentSaver is instantiated using SpringFramework factory
 * in order to support declared (AOP) transactions.
 *   
 * DocumentLoader is instantiated using SpringFramework factory
 * in order to support declared (AOP) transactions.
 */
public class DocumentMapper implements DocumentGetter {

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private Database database;
    private DocumentIndex documentIndex;
    
    private Clock clock = new SystemClock();
    
    private ImcmsServices imcmsServices;
    
    private NativeQueriesDao nativeQueriesDao;

    /**
     * Instantiated using SpringFramework.
     */
    private DocumentLoader documentLoader;
    
    /** Document loader caching proxy. Intercepts calls to DocumentLoader. */
    private DocumentLoaderCachingProxy documentLoaderCachingProxy;
    
    /**
     * Contain document saving and updating routines. 
     * Instantiated using SpringFramework.
     */
    private DocumentSaver documentSaver ;

    private CategoryMapper categoryMapper;

    /**
     * Empty constructor for unit testing. 
     */
    public DocumentMapper() {}
    
    public DocumentMapper(ImcmsServices services, Database database) {
        this.imcmsServices = services;
        this.database = database;
        
        Config config = services.getConfig();
        int documentCacheMaxSize = config.getDocumentCacheMaxSize();

        documentLoader = (DocumentLoader)services.getSpringBean("documentLoader");
        documentLoader.getDocumentInitializingVisitor().getTextDocumentInitializer().setDocumentGetter(this);
        
        documentLoaderCachingProxy = new DocumentLoaderCachingProxy(documentLoader, documentCacheMaxSize);
        categoryMapper = (CategoryMapper)services.getSpringBean("categoryMapper");
        
        documentSaver = (DocumentSaver)services.getSpringBean("documentSaver");
        documentSaver.setDocumentMapper(this);
          
        nativeQueriesDao = (NativeQueriesDao)services.getSpringBean("nativeQueriesDao");
    }
    
    /**
     * @param documentId document id.
     * 
     * @return version support for a given document or null if document does not exist.
     */
    public DocumentVersionInfo getDocumentVersionInfo(Integer documentId) {
    	return documentLoaderCachingProxy.getDocumentVersionInfo(documentId);
    }


    /**
     * Creates new Document which inherits parent doc's meta excluding keywords and properties.
     *
     * Document's data (labels, texts, images, urls, files, etc) is not inherited.
     * 
     * @param documentTypeId
     * @param parentDoc
     * @param user
     * @return
     */
    public DocumentDomainObject createDocumentOfTypeFromParent(int documentTypeId, final DocumentDomainObject parentDoc, final UserDomainObject user) {
        DocumentDomainObject newDocument;

        if (DocumentTypeDomainObject.TEXT_ID == documentTypeId) {
            newDocument = parentDoc.clone();
            TextDocumentDomainObject newTextDocument = (TextDocumentDomainObject) newDocument;
            newTextDocument.removeAllTexts();
            newTextDocument.removeAllImages();
            newTextDocument.removeAllIncludes();
            newTextDocument.removeAllMenus();
            newTextDocument.removeAllContentLoops();
            setTemplateForNewTextDocument( newTextDocument, user, parentDoc );
        } else {
            newDocument = DocumentDomainObject.fromDocumentTypeId(documentTypeId);
            newDocument.setMeta(parentDoc.getMeta().clone());
            newDocument.setLanguage(parentDoc.getLanguage().clone());            
        }

        newDocument.getMeta().setDocumentType(documentTypeId);

        newDocument.accept(new DocIdentityCleanerVisitor());

        newDocument.setHeadline( "" );
        newDocument.setMenuText( "" );
        newDocument.setMenuImage( "" );        
        newDocument.getKeywords().clear();
        newDocument.getProperties().clear();
                
        makeDocumentLookNew( newDocument, user );
        removeNonInheritedCategories(newDocument);
        
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

    public DocumentReference getDocumentReference(DocumentDomainObject document) {
        return getDocumentReference(document.getId());
    }
    

    public DocumentReference getDocumentReference(int childId) {
        return new GetterDocumentReference(childId);
    }

    
    /**
     * Saves document as new.
     * 
     * @param doc
     * @param user
     * 
     * @return saved document.
     *
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     *
     *
     * @see #createDocumentOfTypeFromParent(int, imcode.server.document.DocumentDomainObject, imcode.server.user.UserDomainObject)
     * @see imcode.server.document.DocumentDomainObject#fromDocumentTypeId(int)
     */
    public <T extends DocumentDomainObject> T saveNewDocument(final T doc, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        return saveNewI18nDocument(doc, null, user);
    }


    /**
     * By design spec, when an user creates a new document using imCMS web interface he has ability
     * to fill labels in all languages available in the system.
     * However, DocumentDomainObject has one-to-one relationship with Labels. 
     * To workaround this limitation and preserve backward compatibility with legacy API,
     * doc labels can be passed in a separate parameters. 
     *
     * @param doc
     * @param labelsMap mignt be null
     * @param user
     * @param <T>
     * @return
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     */
    public <T extends DocumentDomainObject> T saveNewI18nDocument(final T doc, Map<I18nLanguage, DocumentLabels> labelsMap, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        T docClone = (T)doc.clone();
        I18nLanguage language = docClone.getLanguage();

        if (language == null) {
            language = Imcms.getI18nSupport().getDefaultLanguage();
            docClone.setLanguage(language);
        }

        if (labelsMap == null) {
            labelsMap = new HashMap<I18nLanguage, DocumentLabels>();
        }

        labelsMap.put(docClone.getLanguage(), docClone.getLabels());                

        Integer docId = documentSaver.saveNewDocument(docClone, labelsMap, user);

        invalidateDocument(docId);

        return (T)getWorkingDocument(docId, language);
    }


    /**
     * Updates existing document.
     */
    public void saveDocument(final DocumentDomainObject doc, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {        
        saveI18nDocument(doc, null, user);
    }


    /**
     * Updates existing document.
     * See {@link #saveNewI18nDocument(imcode.server.document.DocumentDomainObject, java.util.Map, imcode.server.user.UserDomainObject)}
     * to learn more about parameters.
     * 
     */
    public void saveI18nDocument(final DocumentDomainObject doc, Map<I18nLanguage, DocumentLabels> labelsMap, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

        DocumentDomainObject docClone = doc.clone();
        DocumentDomainObject oldDoc = getCustomDocument(doc.getId(), doc.getVersionNo(), doc.getLanguage());
        
        if (labelsMap == null) {
            labelsMap = new HashMap<I18nLanguage, DocumentLabels>();
        }

        labelsMap.put(docClone.getLanguage(), docClone.getLabels());
        
        try {
            documentSaver.updateDocument(doc.clone(), labelsMap, oldDoc.clone(), user);
    	} finally {
    		invalidateDocument(doc.getId());
    	}
    }




    
    /**
     * todo: implement?
     * Updates existing document content.
     * Meta is skipped.
     */
    public void saveDocumentContent(final DocumentDomainObject doc, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
        throw new NotImplementedException();
    }


    /**
     * Updates existing document(s).
     *
     * @param docs documents to update.
     * @param user
     *
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws NoPermissionToEditDocumentException
     *
     * @since 6.0
     */
//    public void saveDocument(Meta meta, Map<I18nLanguage, DocumentDomainObject> docs, UserDomainObject user)
//            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
//        try {
//            DocumentDomainObject doc = docs.values().iterator().next();
//
//            // todo: old meta.
//            DocumentDomainObject oldDoc = getCustomDocument(doc.getId(), doc.getVersionNo());
//
//            documentSaver.updateDocument(meta, docs, oldDoc, user);
//    	} finally {
//    		invalidateDocument(meta.getId());
//    	}
//    }


    /**
     * Saves document menu.
     * @since 6.0
     */
    public void saveTextDocMenu(TextDocumentDomainObject doc, MenuDomainObject menu, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
        try {
    		documentSaver.saveMenu(doc, menu, user);
    	} finally {
    		invalidateDocument(doc);
    	}
    }


    /**
     * Creates next document version.
     * 
     * Saves document's working version copy as next document version.
     *
     * @return new document version.
     *
     * @since 6.0
     */
    public DocumentVersion makeDocumentVersion(final Integer docId, final UserDomainObject user)
        throws DocumentSaveException {

        Meta meta = documentLoaderCachingProxy.getMeta(docId); 

        Map<I18nLanguage, DocumentDomainObject> docs = new HashMap<I18nLanguage, DocumentDomainObject>();

        for (I18nLanguage language: Imcms.getI18nSupport().getLanguages()) {
            DocumentDomainObject doc = documentLoaderCachingProxy.getCustomDocument(docId, DocumentVersion.WORKING_VERSION_NO, language);

            if (doc != null) {                
                docs.put(language, doc);
            }
        }

        if (docs.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Unable to make next document version. Working document does not exists: docId: %d.",
                    docId));
        }

        
        DocumentVersion version = documentSaver.makeDocumentVersion(meta, docs, user);

        invalidateDocument(docId);

        return  version;
    }


    /**
     * Changes document's active version.
     */
    public void changeDocumentDefaultVersion(Integer docId, Integer newDocDefaultVersionNo, UserDomainObject user)
    throws DocumentSaveException, NoPermissionToEditDocumentException {
        try {
    	    documentSaver.changeDocumentDefaultVersion(docId, newDocDefaultVersionNo, user);
        } finally {
            invalidateDocument(docId);
        }
	}


    public void invalidateDocument(DocumentDomainObject document) {        
        invalidateDocument(document.getId());
    }


    public void invalidateDocument(Integer docId) {
        documentLoaderCachingProxy.removeDocumentFromCache(docId);        
        documentIndex.indexDocument(docId);
    }


    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public List<Integer[]> getParentDocumentAndMenuIdsForDocument(DocumentDomainObject document) {
        List<Object[]> tuples = nativeQueriesDao.getParentDocumentAndMenuIdsForDocument(document.getId());
        
        List<Integer[]> result = new ArrayList<Integer[]>(tuples.size());
        
        for (Object[] tuple : tuples) {
            Integer[] pair = new Integer[] {
                (Integer) tuple[0], 
                (Integer) tuple[1]
            };
            
            result.add(pair);
        }
        
        return result;
    }

    public String[][] getAllMimeTypesWithDescriptions(UserDomainObject user) {
    	List<String[]> result = nativeQueriesDao.getAllMimeTypesWithDescriptions(user.getLanguageIso639_2());
    	
    	String[][] mimeTypes = new String[result.size()][]; 
    	
    	for (int i = 0; i < mimeTypes.length; i++) {
    		mimeTypes[i] = result.get(i);
    	}
    	
    	return mimeTypes;
    }

    public String[] getAllMimeTypes() {
    	return nativeQueriesDao.getAllMimeTypes().toArray(new String[] {});    
    }


    public void deleteDocument(final DocumentDomainObject document, UserDomainObject user) {
        if (document instanceof TextDocumentDomainObject) {
    		TextDocumentDomainObject textDoc = (TextDocumentDomainObject) document;

    		imcmsServices.getImageCacheMapper().deleteDocumentImagesCache(document.getId(), textDoc.getImages());
    	}

        documentSaver.getMetaDao().deleteDocument(document.getId());
        document.accept(new DocumentDeletingVisitor());
        documentIndex.removeDocument(document);
        
        documentLoaderCachingProxy.removeDocumentFromCache(document.getId());
    }

    public Map<Integer, String> getAllDocumentTypeIdsAndNamesInUsersLanguage(UserDomainObject user) {
        return nativeQueriesDao.getAllDocumentTypeIdsAndNamesInUsersLanguage(user.getLanguageIso639_2());
    }

    public TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingDocument(DocumentDomainObject document) {
        List<Integer[]> rows = nativeQueriesDao.getDocumentMenuPairsContainingDocument(document.getId());
        
        TextDocumentMenuIndexPair[] documentMenuPairs = new TextDocumentMenuIndexPair[rows.size()];
        
        for (int i = 0; i < documentMenuPairs.length; i++) {
        	Integer[] row = rows.get(i);
        	
            int containingDocumentId = row[0];
            int menuIndex = row[1];
            
            TextDocumentDomainObject containingDocument = (TextDocumentDomainObject) getDocument(containingDocumentId);
            documentMenuPairs[i] = new TextDocumentMenuIndexPair(containingDocument, menuIndex);
        }
        
        return documentMenuPairs;
    }

    public Iterator<DocumentDomainObject> getDocumentsIterator(final IntRange idRange) {
        return new DocumentsIterator(getDocumentIds(idRange));
    }
   
    // TODO: refactor
    private int[] getDocumentIds(IntRange idRange) {
    	List<Integer> ids = documentSaver.getMetaDao().getDocumentIdsInRange(
    			idRange.getMinimumInteger(),
    			idRange.getMaximumInteger());
    	
    	// Optimize
    	return ArrayUtils.toPrimitive(ids.toArray(new Integer[] {}));
    }
    

    public List<Integer> getAllDocumentIds() {
    	return documentSaver.getMetaDao().getAllDocumentIds();
    }
    
    public IntRange getDocumentIdRange() {
        Integer[] minMaxPair = documentSaver.getMetaDao().getMinMaxDocumentIds();
        
        return new IntRange(minMaxPair[0], minMaxPair[1]);
    }

    // TODO: refactor
    public Set<String> getAllDocumentAlias() {
    	List<String> aliasesList = documentLoader.getMetaDao().getAllAliases();
    	Set<String> aliasesSet = new HashSet<String>();
    	Transformer transformer = new Transformer() {
    		public String transform(Object alias) {
    			return ((String)alias).toLowerCase();
    		}
    	};
    	
    	return (Set<String>)CollectionUtils.collect(
    			aliasesList, transformer, aliasesSet);
    }
    
    /** 
     * @param documentIdentity document id or alias.
     * 
     * @return latest version of a document or null if document can not be found.
     */
    public DocumentDomainObject getDocument(String documentIdentity) {
        Integer documentId = toDocumentId(documentIdentity);
        
        return documentId == null 
        	? null
        	: getDocument(documentId);
    }
    
    /** 
     * @param documentIdentity document id or alias
     * 
     * @return document id or null if there is no document with such identity.
     */
    public Integer toDocumentId(String documentIdentity) {
    	if (documentIdentity == null) {
    		return null;
    	}
    	 
    	try {
    		return Integer.valueOf(documentIdentity);
    	} catch (NumberFormatException e) {
    		return documentLoaderCachingProxy.getDocId(documentIdentity);
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


    static void deleteOtherFileDocumentFiles(final FileDocumentDomainObject fileDocument) {
        deleteFileDocumentFilesAccordingToFileFilter(new SuperfluousFileDocumentFilesFileFilter(fileDocument));
    }

    public int getLowestDocumentId() {
    	return documentSaver.getMetaDao().getMaxDocumentId();
    }

    public int getHighestDocumentId() {
        return documentSaver.getMetaDao().getMinDocumentId();
    }

    
    /**
     * Creates a new doc as a copy of an existing doc.
     *
     * Please note that provided document is not used as a new document prototype; it is used as a structure
     * to pass existing doc identities (id, version, language) to the method.
     *
     * @param doc existing doc.
     * @param user
     *
     * @return working version of new saved document in source document's language.
     *
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    public <T extends DocumentDomainObject> T copyDocument(final T doc, final UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        Integer docId = copyDocument(doc.getId(), doc.getVersionNo(), user);

        return (T)getWorkingDocument(docId, doc.getLanguage());
    }


    /**
     * Creates a new doc as a copy of an existing doc.
     * Not a part of public API - used by admin interface.
     *
     * @return new doc id.
     * 
     * @since 6.0
     */
    public Integer copyDocument(final Integer docId, final Integer docVersionNo, final UserDomainObject user)
        throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        // todo: put into resource file.
        String copyHeadlineSuffix = "(Copy/Kopia)";

        Map<I18nLanguage, DocumentDomainObject> docs = new HashMap<I18nLanguage, DocumentDomainObject>();

        for (I18nLanguage language: Imcms.getI18nSupport().getLanguages()) {
            DocumentDomainObject doc = getCustomDocument(docId, docVersionNo, language).clone();
            
            if (doc != null) {
                doc.setAlias(null);
                makeDocumentLookNew(doc, user);
                DocumentLabels labels = doc.getLabels();
                labels.setHeadline(labels.getHeadline() + copyHeadlineSuffix);
                
                doc.accept(new DocIdentityCleanerVisitor());

                docs.put(language, doc);
            }
        }

        
        if (docs.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Unable to copy. Source document does not exists. DocId: %d, doc version no: %d.",
                    docId, docVersionNo));
        }

        
        return documentSaver.copyDocument(docs.values().iterator().next().getMeta(), docs, user);
    }
    

    public List<DocumentDomainObject> getDocumentsWithPermissionsForRole(final RoleDomainObject role) {
    	
        return new AbstractList<DocumentDomainObject>() {
        	private List<Integer> documentIds = nativeQueriesDao.getDocumentsWithPermissionsForRole(role.getId().intValue()); 
            
        	public DocumentDomainObject get(int index) {
                return getDocument(documentIds.get(index));
            }

            public int size() {
                return documentIds.size();
            }
        };
    }


    /**
     * @param docId
     * @return default document in default language.
     * @since 6.0
     */
    public DocumentDomainObject getDefaultDocument(Integer docId) {
        return getDefaultDocument(docId, Imcms.getI18nSupport().getDefaultLanguage());
    }


    /**
     * @param docId
     * @return working document in default language.
     * @since 6.0
     */
    public DocumentDomainObject getWorkingDocument(Integer docId) {
        return getWorkingDocument(docId, Imcms.getI18nSupport().getDefaultLanguage());
    }

    
    /**
     * @param docId
     * @return custom document in default language.
     * @since 6.0
     */
    public DocumentDomainObject getCustomDocument(Integer docId, Integer docVersionNo) {
        return getCustomDocument(docId, docVersionNo, Imcms.getI18nSupport().getDefaultLanguage());
    }
    
    
    /**
     * Returns document.
     *
     * Delegates call to a callback associated with a current thread.
     * If there is no callback then a default document is returned.
     *
     * @param docId document id.
     */
    public DocumentDomainObject getDocument(Integer docId) {
        GetDocumentCallback callback = Imcms.getGetDocumentCallback();

        return callback == null
            ? getDefaultDocument(docId)
            : callback.getDoc(this, docId);
    }


    /**
     * @param docId
     * @param language
     * @return working document
     * @since 6.0
     */
    public DocumentDomainObject getWorkingDocument(Integer docId, I18nLanguage language) {
        return documentLoaderCachingProxy.getWorkingDocument(docId, language);
    }

    /**
     * @param docId
     * @param language
     * @return default document
     * @since 6.0
     */
    public DocumentDomainObject getDefaultDocument(Integer docId, I18nLanguage language) {
        return documentLoaderCachingProxy.getDefaultDocument(docId, language);
    }

    
    /**
     * Returns custom document.
     * 
     * Custom document is never cached. ???
     *
     * @param docId
     * @param docVersionNo
     * @param language
     * @return custom document
     * @since 6.0
     */
    public DocumentDomainObject getCustomDocument(Integer docId, Integer docVersionNo, I18nLanguage language) {
        return documentLoaderCachingProxy.getCustomDocument(docId, docVersionNo, language);
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


    @Deprecated
    void setCreatedAndModifiedDatetimes(DocumentDomainObject document, Date now) {
        setCreatedAndModifiedDatetimes(document.getMeta(), now);
    }


    /**
     * @param meta
     * @param now
     * @since 6.0
     */
    void setCreatedAndModifiedDatetimes(Meta meta, Date now) {
        meta.setCreatedDatetime(now);
        meta.setModifiedDatetime(now);
        meta.setActualModifiedDatetime(now);
    }


    /**
     * Saves text and non-saved enclosing content loop if any.
     * 
     * Non saved content loop might be added to the document by ContentLoopTag2.
     *
     * @see com.imcode.imcms.servlet.admin.SaveText
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     */
    public synchronized void saveTextDocText(TextDocumentDomainObject document, TextDomainObject text, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
    	try {
            documentSaver.saveText(document, text, user);
	    } finally {
	        invalidateDocument(document);
	    }    	
    }

    /**
     * Saves images and non-saved enclosing content loop if any.
     *
     * Non saved content loop might be added to the document by ContentLoopTag2.
     *
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     * @since 6.0
     */
    public synchronized void saveTextDocImages(TextDocumentDomainObject document, Collection<ImageDomainObject> images, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
    	try {
    		documentSaver.saveImages(document, images, user);
	    } finally {
	        invalidateDocument(document);
	    }
    }


    /**
     * Saves images and non-saved enclosing content loop if any.
     *
     * Non saved content loop might be added to the document by ContentLoopTag2.
     *
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     * @since 6.0
     */
    public synchronized void saveTextDocImage(TextDocumentDomainObject document, ImageDomainObject image, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
    	try {
    		documentSaver.saveImage(document, image, user);
	    } finally {
	        invalidateDocument(document);
	    }
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public void setDocumentIndex(DocumentIndex documentIndex) {
        this.documentIndex = documentIndex;
    }

    
    /**
     * @param documentIds
     * @return default documents.
     */
    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        GetDocumentCallback callback = Imcms.getGetDocumentCallback();
        I18nLanguage language = callback != null
                ? callback.getLanguage()
                : Imcms.getI18nSupport().getDefaultLanguage();

        List<DocumentDomainObject> docs = new LinkedList<DocumentDomainObject>();

        for (Integer docId: documentIds) {
            DocumentDomainObject doc = getDefaultDocument(docId, language);
            if (doc != null) {
                docs.add(doc);
            }
        }

        return docs;
    }

    

    private void removeNonInheritedCategories(DocumentDomainObject document) {
        Set<CategoryDomainObject> categories = getCategoryMapper().getCategories(document.getCategoryIds());
        for (CategoryDomainObject category: categories) {
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

    private class DocumentsIterator implements Iterator<DocumentDomainObject> {

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

        public DocumentDomainObject next() {
        	
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            int documentId = documentIds[index++];
            
            return getDocument(documentId);
        }
    }

    public static class SaveEditedDocumentCommand extends DocumentPageFlow.SaveDocumentCommand {

        @Override
        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().saveDocument(document, user);
        }

        @Override
        public void saveI18nDocument(DocumentDomainObject document, Map<I18nLanguage, DocumentLabels> labelsMap, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().saveI18nDocument(document, labelsMap, user);
        }
    }

    /**
     * Makes a version from a working/draft version.
     */
    public static class MakeDocumentVersionCommand extends DocumentPageFlow.SaveDocumentCommand {

        @Override
        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().makeDocumentVersion(document.getId(), user);
        }
    }


    /**
     * Sets default document version.
     */
    public static class SetDefaultDocumentVersionCommand extends DocumentPageFlow.SaveDocumentCommand {

        private Integer docVersionNo;

        public SetDefaultDocumentVersionCommand(Integer docVersionNo) {
            this.docVersionNo = docVersionNo; 
        }

        @Override
        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().changeDocumentDefaultVersion(document.getId(), docVersionNo, user);
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
            if (perl5Util.match("/(?:(\\d+)(?:_(\\d+))?)(?:_se|\\.(.*))?/", filename)) {
                String idStr = perl5Util.group(1);
                String variantName = FileUtility.unescapeFilename(StringUtils.defaultString(perl5Util.group(3)));
                String docVersionNo = perl5Util.group(2);
                return accept(file,
                        Integer.parseInt(idStr),
                        docVersionNo == null ? 0 : Integer.parseInt(docVersionNo), 
                        variantName);
            }
            return false;
        }

        public boolean accept(File file, int fileDocumentId, int docVersionNo, String fileId) {
            return fileDocumentId == fileDocument.getId();
        }
    }

    private static class SuperfluousFileDocumentFilesFileFilter extends FileDocumentFileFilter {

        private SuperfluousFileDocumentFilesFileFilter(FileDocumentDomainObject fileDocument) {
            super(fileDocument);
        }

        @Override
        public boolean accept(File file, int fileDocumentId, int docVersionNo, String fileId) {
            boolean correctFileForFileDocumentFile = file.equals(DocumentSavingVisitor.getFileForFileDocumentFile(fileDocumentId, fileDocument.getVersionNo(), fileId));
            boolean fileDocumentHasFile = null != fileDocument.getFile(fileId);
            return fileDocumentId == fileDocument.getId()
                   && docVersionNo == fileDocument.getVersionNo() 
                   && (!correctFileForFileDocumentFile || !fileDocumentHasFile);
        }
    }

    public DocumentLoaderCachingProxy getDocumentLoaderCachingProxy() {
        return documentLoaderCachingProxy;
    }
}