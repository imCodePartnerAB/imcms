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
    
    /** TODO: hide. */
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
        
        // old code:
        // setDocumentGetter(new FragmentingDocumentGetter(new DocumentLoader(services)));
        
        // Document getter is used directly without Fragmented getter
        // DatabseDocumentGetter is instantiated using SpringFramework factory
        // in order to support declarative (AOP) transactions.        
        documentLoader = (DocumentLoader)services.getSpringBean("documentLoader");
        documentLoader.getDocumentInitializingVisitor().getTextDocumentInitializer().setDocumentGetter(this);
        
        documentLoaderCachingProxy = new DocumentLoaderCachingProxy(documentLoader, documentCacheMaxSize);
        categoryMapper = (CategoryMapper)services.getSpringBean("categoryMapper");
        
        // DocumentSaver is instantiated using SpringFramework
        // in order to support declarative (AOP) transactions.
        documentSaver = (DocumentSaver)services.getSpringBean("documentSaver");
        documentSaver.setDocumentMapper(this);
          
        nativeQueriesDao = (NativeQueriesDao)services.getSpringBean("nativeQueriesDao");
    }
    
    /**
     * @param documentId document id.
     * @return version support for a given document or null if document does not exist.
     */
    public DocumentVersionInfo getDocumentVersionInfo(Integer documentId) {
    	return documentLoaderCachingProxy.getDocumentVersionInfo(documentId);
    }

//    public DocumentSaver getDocumentSaver() {
//        return documentSaver ;
//    }


    /**
     * Creates new Document which inherits parent doc's meta.
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
            newDocument.setAttributes(parentDoc.getAttributes().clone());
            newDocument.setMeta(parentDoc.getMeta().clone());
            newDocument.setMeta(parentDoc.getMeta().clone());
        }

        newDocument.accept(new DocIdentityCleanerVisitor());

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
     * Saves new document.
     * Document's meta might be a copy of an existing document.
     * 
     * @param document
     * @param user
     * @param copying controls how document's permissions are saved.
     * @return saved document id.
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     *
     * @see #copyDocument(imcode.server.document.DocumentDomainObject, imcode.server.user.UserDomainObject)
     */
    public Integer saveNewDocument(final DocumentDomainObject document, final UserDomainObject user, boolean copying)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        Collection<DocumentLabels> labelsColl = new LinkedList<DocumentLabels>();

        for (I18nLanguage language: Imcms.getI18nSupport().getLanguages()) {
            DocumentLabels labelsClone = document.getLabels().clone();
            labelsClone.setLanguage(language);

            labelsColl.add(labelsClone);
        }

        return saveNewDocument(document, labelsColl, user, copying);
    }

    /**
     * Saves new document.
     * Document's meta might be a copy of an existing document.
     * 
     * @param document
     * @param labelsColl 
     * @param user
     * @param copying
     * @return
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     * 
     * @since 6.0
     */
    public Integer saveNewDocument(DocumentDomainObject document, Collection<DocumentLabels> labelsColl, UserDomainObject user, boolean copying)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        return documentSaver.saveNewDocument(user, document, labelsColl, copying);
    }

    /**
     * Updates existing document.
     */
    public void saveDocument(DocumentDomainObject document, final UserDomainObject user)
            throws DocumentSaveException , NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
    	try {
    		documentSaver.updateDocument(document, document, user);
    	} finally {
    		invalidateDocument(document);
    	}
    }


    /**
     * Saves document menu.
     */
    public void saveDocumentMenu(TextDocumentDomainObject doc, MenuDomainObject menu, UserDomainObject user)
            throws DocumentSaveException , NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
        try {
    		documentSaver.saveMenu(doc, menu, user);
    	} finally {
    		invalidateDocument(doc);
    	}
    }


    /**
     * Updates an existing document's info - the data edited on InformationPage.
     * 
     * @param document working document in default language. 
     * @param labels labels for every language.
     * @param user
     * 
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws NoPermissionToEditDocumentException
     * 
     * @since 6.0
     */
    public void saveDocument(DocumentDomainObject document, Collection<DocumentLabels> labels, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

        DocumentDomainObject oldDocument = getDocument(document.getId());

        try {
            documentSaver.updateDocument(document, labels, user);
    	} finally {
    		invalidateDocument(document);
    	}
    }

    
    /**
     * Makes next version of a working document.
     * TODO: Optional - add comments
     */
    public DocumentVersion makeDocumentVersion(Integer docId, UserDomainObject user)
    throws DocumentSaveException, NoPermissionToEditDocumentException {
        try {
    	    return documentSaver.makeDocumentVersion(docId, user);
        } finally {
            invalidateDocument(docId);
        }
	}


    /**
     * Changes document's active version.
     */
    public void setDocumentDefaultVersion(Integer docId, Integer docVersionNo, UserDomainObject user)
    throws DocumentSaveException, NoPermissionToEditDocumentException {
        try {
    	    documentSaver.setDocumentDefaultVersion(docId, docVersionNo);
        } finally {
            invalidateDocument(docId);
        }
	}


    public void invalidateDocument(DocumentDomainObject document) {        
        Integer documentId = document.getId();
        
        documentLoaderCachingProxy.removeDocumentFromCache(documentId);

        if (document.getVersion().getNo() == 0) {
            documentIndex.indexDocument(document);
        }
    }


    public void invalidateDocument(Integer docId) {
        documentLoaderCachingProxy.removeDocumentFromCache(docId);
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
            
            //TextDocumentDomainObject containingDocument = (TextDocumentDomainObject) getDefaultDocument(containingDocumentId);
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
    			idRange.getMaximumInteger(),
    			idRange.getMaximumInteger());
    	
    	// Optimize
    	return ArrayUtils.toPrimitive(ids.toArray(new Integer[] {}));
    }

    // TODO: refactor
    public int[] getAllDocumentIds() {
    	List<Integer> ids = documentSaver.getMetaDao().getAllDocumentIds();
    	
    	// Optimize
    	return ArrayUtils.toPrimitive(getAllDocumentIdsAsList().toArray(new Integer[] {}));
    }

    public List<Integer> getAllDocumentIdsAsList() {
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
     * @return document id or null if there is no document with such identity.
     */
    private Integer toDocumentId(String documentIdentity) {
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
     * Creates new working document based on existing document.
     *
     * @param document existing doc.
     * @param user
     * @return
     * @throws NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    public DocumentDomainObject copyDocument(final DocumentDomainObject document, final UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        Integer docId = document.getId();
        Integer docVersionNo = document.getVersion().getNo();

        return copyDocument(docId, docVersionNo, user);
    }


    /**
     * @return copied document in default language.
     * 
     * @since 6.0
     */
    public DocumentDomainObject copyDocument(final Integer docId, final Integer docVersionNo, final UserDomainObject user)
        throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        // TODO: Does not work
        //String copyHeadlineSuffix = imcmsServices.getAdminTemplate(COPY_HEADLINE_SUFFIX_TEMPLATE, user, null);

        Map<I18nLanguage, DocumentDomainObject> docMap = new HashMap<I18nLanguage, DocumentDomainObject>();

        for (I18nLanguage language: Imcms.getI18nSupport().getLanguages()) {
            DocumentDomainObject doc =  documentLoaderCachingProxy.getCustomDocument(docId, docVersionNo, language);
            if (doc != null) {
                doc.setAlias(null);
                makeDocumentLookNew(doc, user);

                doc.accept(new DocIdentityCleanerVisitor());

                docMap.put(language, doc);
            }
        }

        if (docMap.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Unable to copy. Source document does not exists. DocId: %s, doc version no: %s.",
                    docId, docVersionNo));
        }

        Integer docCopyId = documentSaver.copyDocument(docMap, user);
        // TODO: index saved doc.

        DocumentDomainObject defaultWorkingDoc = getWorkingDocument(docCopyId, Imcms.getI18nSupport().getDefaultLanguage());

        return defaultWorkingDoc;        
    }

    public List<DocumentDomainObject> getDocumentsWithPermissionsForRole(final RoleDomainObject role) {
    	
        return new AbstractList<DocumentDomainObject>() {
        	private List<Integer> documentIds = nativeQueriesDao.getDocumentsWithPermissionsForRole(role.getId().intValue()); 
            
        	public DocumentDomainObject get(int index) {
                //return getDefaultDocument(documentIds.get(index));
                return getDocument(documentIds.get(index));
            }

            public int size() {
                return documentIds.size();
            }
        };
    }
    
    
    /**
     * Returns document.
     *
     * Returned document is based on document info associated with a thread.
     * If there is no request info then default document in default language is returned.
     *
     * @param docId document id.
     */
    public DocumentDomainObject getDocument(Integer docId) {
        Meta meta = documentLoaderCachingProxy.getMeta(docId);

        if (meta == null) {
            return null;
        }
        
        DocumentRequestInfo documentRequestInfo = Imcms.getDocumentRequestInfo();

        if (documentRequestInfo == null) {
            return getDefaultDocument(docId, Imcms.getI18nSupport().getDefaultLanguage());
        }

        UserDomainObject user = documentRequestInfo.getUser();
        I18nLanguage language = documentRequestInfo.getLanguage();
        DocumentRequestInfo.DocVersionMode docVersionMode = documentRequestInfo.getDocVersionMode();
        
        if (user.isSuperAdmin()) {
            DocumentRequestInfo.CustomDoc customDoc = documentRequestInfo.getCustomDoc();

            if (customDoc != null && docId.equals(customDoc.id)) {
                return getCustomDocument(docId, customDoc.versionNo, language);
            }

            return docVersionMode == DocumentRequestInfo.DocVersionMode.WORKING
                ? getWorkingDocument(docId, language)
                : getDefaultDocument(docId, language);
        }
        

        if (!language.isDefault() && !meta.getLanguages().contains(language)) {
            if (meta.getDisabledLanguageShowSetting() == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE) {
                language = Imcms.getI18nSupport().getDefaultLanguage();
            } else {
                return null;
            }
        }

        return docVersionMode == DocumentRequestInfo.DocVersionMode.WORKING
            ? getWorkingDocument(docId, language)
            : getDefaultDocument(docId, language);
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

    void setCreatedAndModifiedDatetimes(DocumentDomainObject document, Date now) {
        document.setCreatedDatetime(now);
        document.setModifiedDatetime(now);
        document.setActualModifiedDatetime(now);
    }


    /**
     * Saves text and non-saved enclosing content loop if any.
     * 
     * Non saved content loop might be added to the document by ContentLoopTag2.
     *
     * @see com.imcode.imcms.servlet.admin.SaveText
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     */
    public synchronized void saveText(TextDocumentDomainObject document, TextDomainObject text, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
    	try {
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
     */
    public synchronized void saveImages(TextDocumentDomainObject document, Collection<ImageDomainObject> images, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
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
     */
    public synchronized void saveImage(TextDocumentDomainObject document, ImageDomainObject image, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
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
     *
     * @param documentIds
     * @return
     */
    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        DocumentRequestInfo ri = Imcms.getDocumentRequestInfo();
        I18nLanguage language = ri != null
                ? ri.getLanguage()
                : Imcms.getI18nSupport().getDefaultLanguage();

        List<DocumentDomainObject> docs = new LinkedList<DocumentDomainObject>();

        // todo: filter docs for 
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
        public void saveDocument(DocumentDomainObject document, Collection<DocumentLabels> labels, UserDomainObject user)
               throws NoPermissionInternalException, DocumentSaveException {
           Imcms.getServices().getDocumentMapper().saveDocument(document, labels, user);
        }
    }

    /**
     * Makes a version from a working/draft version.
     */
    public static class MakeDocumentVersionCommand extends DocumentPageFlow.SaveDocumentCommand {

        public void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
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

        public void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().setDocumentDefaultVersion(document.getId(), docVersionNo, user);
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

	public void setDocumentSaver(DocumentSaver documentSaver) {
		this.documentSaver = documentSaver;
	}

    public DocumentLoaderCachingProxy getDocumentLoaderCachingProxy() {
        return documentLoaderCachingProxy;
    }

    public void setDocumentLoaderCachingProxy(DocumentLoaderCachingProxy documentLoaderCachingProxy) {
        this.documentLoaderCachingProxy = documentLoaderCachingProxy;
    }
}