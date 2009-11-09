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
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.DocumentShowSettings;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Clock;
import imcode.util.SystemClock;
import imcode.util.io.FileUtility;

import java.io.File;
import java.io.FileFilter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.math.IntRange;
import org.apache.oro.text.perl.Perl5Util;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import com.imcode.db.Database;
import com.imcode.imcms.api.*;
import com.imcode.imcms.dao.NativeQueriesDao;
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

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private Database database;
    private DocumentIndex documentIndex;
    
    private Clock clock = new SystemClock();
    private ImcmsServices imcmsServices;
    
    private NativeQueriesDao nativeQueriesDao;
    
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
        // in order to support declarative (AOP) transactions.        
        databaseDocumentGetter = (DatabaseDocumentGetter)services.getSpringBean("databaseDocumentGetter");
        databaseDocumentGetter.getDocumentInitializingVisitor().getTextDocumentInitializer().setDocumentGetter(this);
        
        cachingDocumentGetter = new CachingDocumentGetter(databaseDocumentGetter, documentCacheMaxSize);        
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
    public DocumentVersionSupport getDocumentVersionSupport(Integer documentId) {
    	return cachingDocumentGetter.getDocumentVersionSupport(documentId);
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

    public DocumentReference getDocumentReference(DocumentDomainObject document, DocumentVersionSelector versionSelector) {
        return getDocumentReference(document.getId(), versionSelector);
    }
    

    public DocumentReference getDocumentReference(int childId, DocumentVersionSelector versionSelector) {
        return new GetterDocumentReference(childId, cachingDocumentGetter, versionSelector);
    }

    public void saveNewDocument(DocumentDomainObject document, UserDomainObject user, boolean copying)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        documentSaver.saveNewDocument(user, document, copying);
        
        if (document.getMeta().getPublicationStatusInt() == Document.PublicationStatus.APPROVED.asInt()) {
        	publishWorkingDocument(document.clone(), user);
        }
    }

    /**
     * Updates existing document.
     */
    public void saveDocument(DocumentDomainObject document,
                             final UserDomainObject user) throws DocumentSaveException , NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException
    {

    	DocumentDomainObject oldDocument = 
    		getDocument(document.getId(), document.getMeta().getVersion().getNumber());

    	try {
    		documentSaver.updateDocument(document, oldDocument, user);
    	} finally {
    		invalidateDocument(document);
    	}
        
        Meta meta = document.getMeta();        
        
        if (meta.getPublicationStatusInt() == Document.PublicationStatus.APPROVED.asInt()) {
        	Integer documentId = meta.getId();
        	DocumentVersionSupport versionSupport = getDocumentVersionSupport(documentId);
        	if (!versionSupport.hasPublishedVersion()) {
            	document = getWorkingDocument(document.getMeta().getId());
            	publishWorkingDocument(document.clone(), user);
        	}
        }        
    }
    
    /**
     * Published working version of a document.
     */
    // TODO: Check exceptions 
    public void publishWorkingDocument(DocumentDomainObject document, UserDomainObject user) 
    throws DocumentSaveException, NoPermissionToEditDocumentException {	
    	try {
    		documentSaver.publishWorkingDocument(document, user);
    	} finally {
    		invalidateDocument(document);
    	}
	}
    
    
    /**
     * Creates document's working version from any previous version.
     * 
     * @param documentId existing document id
     * @param documentVersion any previous document version.
     * 
     * @return new working version which is a copy of any previous version.
     */
    // TODO: Check exceptions 
    public void createWorkingDocument(Integer documentId, Integer documentVersion, UserDomainObject user) 
    throws DocumentSaveException, NoPermissionToEditDocumentException {
    	DocumentDomainObject document = getDocument(documentId, documentVersion);
    	
	    document = documentSaver.createWorkingDocumentFromExisting(document, user);

        invalidateDocument(document);
	}
            
    
    /**
     * Returns document by its id and version.
     * 
     * Expensive call - returned document may not be cached.
     * 
     * @param documentId document id
     * @param documentVersion document version. If not given (null) then published version is returned.
     * @return document or null if document can not be found.  
     */
    public DocumentDomainObject getDocument(Integer documentId, Integer documentVersion) {
    	return cachingDocumentGetter.getDocument(documentId, documentVersion);
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
        Integer documentId = document.getId();
        
        cachingDocumentGetter.removeDocumentFromCache(documentId);


        if (document.getVersion().getTag() == DocumentVersionTag.WORKING) {
            documentIndex.indexDocument(document);
        }
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
        
        cachingDocumentGetter.removeDocumentFromCache(document.getId());
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
            
            TextDocumentDomainObject containingDocument = (TextDocumentDomainObject) getPublishedDocument(containingDocumentId);
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
    	return ArrayUtils.toPrimitive(ids.toArray(new Integer[] {}));
    }
    
    public IntRange getDocumentIdRange() {
        Integer[] minMaxPair = documentSaver.getMetaDao().getMinMaxDocumentIds();
        
        return new IntRange(minMaxPair[0], minMaxPair[1]);
    }

    // TODO: refactor
    public Set<String> getAllDocumentAlias() {
    	List<String> aliasesList = databaseDocumentGetter.getMetaDao().getAllAliases();
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
     * @return custom document's version for showing.
     */
    public DocumentDomainObject getDocumentForShowing(String documentIdString, Integer versionNumber, UserDomainObject user) {
    	DocumentDomainObject document = getDocument(documentIdString, versionNumber);
    	
    	return createDocumentShowInterceptor(document, user);
    }
    
    /** 
     * @param documentIdentity document id or alias.
     * @param versionNumber document version number.
     * 
     * @return document or null if document can not be found. 
     */
    public DocumentDomainObject getDocument(String documentIdentity, Integer versionNumber) {
        Integer documentId = toDocumentId(documentIdentity);
        
        return documentId == null 
        	? null
        	: getDocument(documentId, versionNumber);
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
    		return cachingDocumentGetter.getDocumentIdByAlias(documentIdentity);
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

    public List<DocumentDomainObject> getDocumentsWithPermissionsForRole(final RoleDomainObject role) {
    	
        return new AbstractList<DocumentDomainObject>() {
        	private List<Integer> documentIds = nativeQueriesDao.getDocumentsWithPermissionsForRole(role.getId().intValue()); 
            
        	public DocumentDomainObject get(int index) {
                return getPublishedDocument(documentIds.get(index));
            }

            public int size() {
                return documentIds.size();
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
    public DocumentDomainObject getLatestDocumentVersionForShowing(Integer documentId, UserDomainObject user) { 
        DocumentDomainObject document = getDocument(documentId);
        
        return document == null ? null : createDocumentShowInterceptor(document, user);
    }    
    
    /**
     * Returns working document version.
     * 
     * @param documentId document id
     * 
     * @return working on of the document or null if document does not exist.
     */
    public DocumentDomainObject getDocument(Integer documentId) { 
        return cachingDocumentGetter.getDocument(documentId);
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
     * Returns published or working (depending on user show settings) document for showing.
     * 
     * @param documentIdentity document's id or alias
     * @param user an user requesting a document 
     * 
     * @return published or working AOP adviced document or null if document does not exist.  
     */
    public DocumentDomainObject getDocumentForShowing(String documentIdentity, UserDomainObject user) {
        Integer documentId = toDocumentId(documentIdentity);
        
        return documentId == null
        	? null
        	: getDocumentForShowing(documentId, user);	
    }
    
    public DocumentDomainObject getPublishedDocumentForShowing(String documentIdentity, UserDomainObject user) {
        Integer documentId = toDocumentId(documentIdentity);
        
        return documentId == null
        	? null
        	: getPublishedDocument(documentId);	
    }  
    
    public DocumentDomainObject getWorkingDocumentForShowing(String documentIdentity, UserDomainObject user) {
        Integer documentId = toDocumentId(documentIdentity);
        
        return documentId == null
        	? null
        	: getWorkingDocument(documentId);	
    }    
    
    /**
     * @return all document's versions 
     */
    public List<DocumentVersion> getDocumentVersions(Integer documentId) {
    	DocumentVersionSupport support = getDocumentVersionSupport(documentId);
    	
    	if (support != null) {
    		return support.getVersions();
    	} else {
    		return new LinkedList<DocumentVersion>();
    	}
    	
    	//return documentSaver.getMetaDao().getDocumentVersions(documentId);
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
    	DocumentShowSettings showSettings = user.getDocumentShowSettings();
    	DocumentDomainObject document = showSettings.getVersionSelector()
    		.getDocument(this, documentId);
    	
    	return createDocumentShowInterceptor(document, user);    	
    } 
    
    /**
     * Creates document interceptor based on document show settings.
     * 
     * TODO: optimize proxy creation - pooling is possible solution. 
     * TODO: Implement two strategies - with intercepting for multi-langual projects 
     * and w/o intercepting for single-language projects.
     */
    private DocumentDomainObject createDocumentShowInterceptor(DocumentDomainObject document, UserDomainObject user) {
    	if (document != null) {
    		/*
    		 * Determines document's content language.
    		 * 
    		 * If an user is allowed to see a document's content in a current language
    		 * then document language is set to current language, otherwise
    		 * it is set to default language. 
    		 */
    		I18nLanguage currentDocumentLanguage = I18nSupport.getCurrentLanguage();
    		DocumentShowSettings showSettings = user.getDocumentShowSettings();
    		
    		if (!I18nSupport.getCurrentIsDefault() && !showSettings.isIgnoreI18nShowMode() ) {            
    			I18nMeta i18nMeta = document.getI18nMeta(I18nSupport.getCurrentLanguage());
            
    			if (!i18nMeta.getEnabled()) {
    				if (document.getMeta().isShowDisabledI18nContentInDefaultLanguage()) {
    					currentDocumentLanguage = I18nSupport.getDefaultLanguage();
    				} else {
    					throw new I18nDisabledException(document, I18nSupport.getCurrentLanguage());
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
    		documentSaver.saveText(document, text, user);            
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

    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        return cachingDocumentGetter.getDocuments(documentIds) ;
    }
    
    public List<DocumentDomainObject> getPublishedDocuments(Collection<Integer> documentIds) {
        return cachingDocumentGetter.getPublishedDocuments(documentIds) ;
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

	public CachingDocumentGetter getDocumentGetter() {
		return cachingDocumentGetter;
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