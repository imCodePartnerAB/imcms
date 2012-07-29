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
 * NativeQueriesDao, DocumentSaver, DocumentLoader and CategoryMapper are instantiated by SpringFramework
 * in order to support declared (AOP) transactions.
 */
public class DocumentMapper implements DocumentGetter {

    public enum SaveDirectives {
        // Applies to text document only.
        CopyI18nMetaTextsIntoTextFields
    }

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private Database database;
    private DocumentIndex documentIndex;

    private ImcmsServices imcmsServices;

    /** injected by SpringFramework. */
    private NativeQueriesDao nativeQueriesDao;

    /** injected by SpringFramework. */
    private DocumentLoader documentLoader;

    /** Document loader caching proxy. Intercepts calls to DocumentLoader. */
    private DocLoaderCachingProxy documentLoaderCachingProxy;

    /**
     * Contain document saving and updating routines.
     * instantiated by SpringFramework.
     */
    private DocumentSaver documentSaver;

    /** instantiated by SpringFramework. */
    private CategoryMapper categoryMapper;

    /** Empty constructor for unit testing. */
    public DocumentMapper() {}

    public DocumentMapper(ImcmsServices services, Database database) {
        this.imcmsServices = services;
        this.database = database;

        Config config = services.getConfig();
        int documentCacheMaxSize = config.getDocumentCacheMaxSize();

        documentLoader = services.getComponent(DocumentLoader.class);
        documentLoader.getDocumentInitializingVisitor().getTextDocumentInitializer().setDocumentGetter(this);

        documentLoaderCachingProxy = new DocLoaderCachingProxy(documentLoader, Imcms.getI18nSupport().getLanguages(), documentCacheMaxSize);

        nativeQueriesDao = services.getComponent(NativeQueriesDao.class);
        categoryMapper = services.getComponent(CategoryMapper.class);

        documentSaver = services.getComponent(DocumentSaver.class);
        documentSaver.setDocumentMapper(this);
    }

    /**
     * @param documentId document id.
     * @return version info for a given document or null if document does not exist.
     */
    public DocumentVersionInfo getDocumentVersionInfo(int documentId) {
        return documentLoaderCachingProxy.getDocVersionInfo(documentId);
    }


    /**
     * Creates new Document which inherits parent doc's meta excluding keywords and properties.
     * <p/>
     * Doc's i18nMeta(s) and content (texts, images, urls, files, etc) are not inherited.
     *
     * @param documentTypeId
     * @param parentDoc
     * @param user
     * @return
     */
    public DocumentDomainObject createDocumentOfTypeFromParent(
            final int documentTypeId,
            final DocumentDomainObject parentDoc,
            final UserDomainObject user) {

        DocumentDomainObject newDocument;

        if (documentTypeId == DocumentTypeDomainObject.TEXT_ID) {
            newDocument = parentDoc.clone();
            TextDocumentDomainObject newTextDocument = (TextDocumentDomainObject) newDocument;
            newTextDocument.removeAllTexts();
            newTextDocument.removeAllImages();
            newTextDocument.removeAllIncludes();
            newTextDocument.removeAllMenus();
            newTextDocument.removeAllContentLoops();

            setTemplateForNewTextDocument(newTextDocument, user, parentDoc);
        } else {
            newDocument = DocumentDomainObject.fromDocumentTypeId(documentTypeId);
            newDocument.setMeta(parentDoc.getMeta().clone());
            newDocument.setLanguage(parentDoc.getLanguage());
        }

        newDocument.getMeta().setDocumentType(documentTypeId);

        newDocument.accept(new DocIdentityCleanerVisitor());

        newDocument.setHeadline("");
        newDocument.setMenuText("");
        newDocument.setMenuImage("");
        newDocument.getKeywords().clear();
        newDocument.getProperties().clear();

        makeDocumentLookNew(newDocument, user);
        removeNonInheritedCategories(newDocument);

        return newDocument;
    }


    /**
     * Sets text doc's template.
     *
     * By default if parent doc type is {@link TextDocumentDomainObject} its default template is used.
     * It might be overridden however if most privileged permission set type for the current user is either
     * {@link DocumentPermissionSetTypeDomainObject#RESTRICTED_1}
     * or
     * {@link DocumentPermissionSetTypeDomainObject#RESTRICTED_2}
     * and there is a default template associated with that set type.
     *
     * Please note:
     *   According to specification only doc of type {@link TextDocumentDomainObject}
     *   can be used as parent (of a 'profile').
     *   NB! for some (undocumented) reason a doc of any type might be used as a parent.
     *
     * @param newTextDocument
     * @param user
     * @param parent
     */
    void setTemplateForNewTextDocument(TextDocumentDomainObject newTextDocument, UserDomainObject user,
                                       final DocumentDomainObject parent) {
        DocumentPermissionSetTypeDomainObject documentPermissionSetType = user.getDocumentPermissionSetTypeFor(parent);
        String templateName = null;

        if (documentPermissionSetType == DocumentPermissionSetTypeDomainObject.RESTRICTED_1) {
            templateName = newTextDocument.getDefaultTemplateNameForRestricted1();
        } else if (documentPermissionSetType == DocumentPermissionSetTypeDomainObject.RESTRICTED_2) {
            templateName = newTextDocument.getDefaultTemplateNameForRestricted2();
        }

        if (templateName == null && parent instanceof TextDocumentDomainObject) {
            templateName = ((TextDocumentDomainObject)parent).getDefaultTemplateName();
        }

        if (templateName != null) {
            newTextDocument.setTemplateName(templateName);
        }
    }


    void makeDocumentLookNew(DocumentDomainObject document, UserDomainObject user) {
        makeDocumentLookNew(document.getMeta(), user);
    }

    void makeDocumentLookNew(Meta meta, UserDomainObject user) {
        Date now = new Date();

        meta.setCreatorId(user.getId());
        setCreatedAndModifiedDatetimes(meta, now);
        meta.setPublicationStartDatetime(now);
        meta.setArchivedDatetime(null);
        meta.setPublicationEndDatetime(null);
        meta.setPublicationStatus(Document.PublicationStatus.NEW);
    }

    public DocumentReference getDocumentReference(DocumentDomainObject document) {
        return getDocumentReference(document.getId());
    }


    public DocumentReference getDocumentReference(int childId) {
        return new GetterDocumentReference(childId);
    }


    /**
     * Saves doc as new.
     *
     * @param doc
     * @param user
     * @return saved document.
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     *
     * @see #createDocumentOfTypeFromParent(int, imcode.server.document.DocumentDomainObject, imcode.server.user.UserDomainObject)
     * @see imcode.server.document.DocumentDomainObject#fromDocumentTypeId(int)
     */
    public <T extends DocumentDomainObject> T saveNewDocument(final T doc, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {
        T docClone = (T) doc.clone();
        I18nLanguage language = docClone.getLanguage();

        if (language == null) {
            language = Imcms.getI18nSupport().getDefaultLanguage();
            docClone.setLanguage(language);
        }

        I18nMeta i18nMeta = docClone.getI18nMeta();
        i18nMeta.setLanguage(language);

        Map<I18nLanguage, I18nMeta> i18nMetas = new HashMap<I18nLanguage, I18nMeta>();
        i18nMetas.put(language, i18nMeta);

        return saveNewDocument(doc, i18nMetas, user);
    }


    /**
     * Saves doc as new.
     *
     * According to the spec, new doc creation UI allows to provide i18nMeta texts
     * in all languages available in the system.
     * However, a DocumentDomainObject has one-to-one relationship with i18nMeta.
     * To workaround this limitation and provide backward compatibility with legacy API,
     * i18nMeta-s are passed in a separate parameter and doc's i18nMeta is ignored.
     *
     * @param doc
     * @param i18nMetas
     * @param user
     * @param directives
     * @param <T>
     * @return saved document
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     *
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T saveNewDocument(final T doc, Map<I18nLanguage, I18nMeta> i18nMetas,
                                                              EnumSet<SaveDirectives> directives,
                                                              final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        if (i18nMetas.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Unable to save new document. i18nMetas must not be empty."));
        }

        T docClone = (T) doc.clone();
        List<I18nMeta> i18nMetasList = new LinkedList<I18nMeta>();

        for (Map.Entry<I18nLanguage, I18nMeta> e : i18nMetas.entrySet()) {
            i18nMetasList.add(e.getValue().clone());
        }

        int docId = documentSaver.saveNewDocument(docClone, i18nMetasList, directives, user);

        invalidateDocument(docId);

        return (T) getWorkingDocument(docId, docClone.getLanguage());
    }

    /**
     * Saves doc as new.
     *
     * According to the spec, new doc creation UI allows to provide i18nMeta texts
     * in all languages available in the system.
     * However, a DocumentDomainObject has one-to-one relationship with i18nMeta.
     * To workaround this limitation and provide backward compatibility with legacy API,
     * i18nMeta-s are passed in a separate parameter and doc's i18nMeta is ignored.
     *
     * @param doc
     * @param i18nMetas
     * @param user
     * @param <T>
     * @return
     * @throws DocumentSaveException
     * @throws NoPermissionToAddDocumentToMenuException
     *
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T saveNewDocument(final T doc, Map<I18nLanguage, I18nMeta> i18nMetas, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        return saveNewDocument(doc, i18nMetas, EnumSet.noneOf(SaveDirectives.class), user);
    }


    /**
     * Updates existing document.
     */
    public void saveDocument(final DocumentDomainObject doc, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
        DocumentDomainObject docClone = doc.clone();
        I18nLanguage language = docClone.getLanguage();
        I18nMeta i18nMeta = docClone.getI18nMeta();

        Map<I18nLanguage, I18nMeta> i18nMetas = new HashMap<I18nLanguage, I18nMeta>();
        i18nMetas.put(language, i18nMeta);

        saveDocument(doc, i18nMetas, user);
    }


    /**
     * Updates existing document.
     *
     * See {@link #saveNewDocument(imcode.server.document.DocumentDomainObject, java.util.Map, imcode.server.user.UserDomainObject)}
     * to learn more about parameters.
     *
     * @since 6.0
     */
    public void saveDocument(final DocumentDomainObject doc, Map<I18nLanguage, I18nMeta> i18nMetas, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

        DocumentDomainObject docClone = doc.clone();
        List<I18nMeta> i18nMetasClone = new LinkedList<I18nMeta>();

        for (Map.Entry<I18nLanguage, I18nMeta> e : i18nMetas.entrySet()) {
            i18nMetasClone.add(e.getValue().clone());
        }

        DocumentDomainObject oldDoc = getCustomDocument(doc.getId(), doc.getVersionNo(), doc.getLanguage());

        try {
            documentSaver.updateDocument(docClone, i18nMetasClone, oldDoc.clone(), user);
        } finally {
            invalidateDocument(doc.getId());
        }
    }


    /**
     * Saves document menu.
     *
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
     * <p/>
     * Saves document's working version copy as next document version.
     *
     * @return new document version.
     * @since 6.0
     */
    public DocumentVersion makeDocumentVersion(final int docId, final UserDomainObject user)
            throws DocumentSaveException {

        List<DocumentDomainObject> docs = new LinkedList<DocumentDomainObject>();

        for (I18nLanguage language : Imcms.getI18nSupport().getLanguages()) {
            DocumentDomainObject doc = documentLoaderCachingProxy.getCustomDoc(docId, DocumentVersion.WORKING_VERSION_NO, language);
            docs.add(doc);
        }

        if (docs.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Unable to make next document version. Working document does not exists: docId: %d.",
                    docId));
        }

        DocumentVersion version = documentSaver.makeDocumentVersion(docs, user);

        invalidateDocument(docId);

        return version;
    }


    /**
     * Changes doc's default version.
     *
     * @since 6.0
     */
    public void changeDocumentDefaultVersion(int docId, int newDocDefaultVersionNo, UserDomainObject publisher)
            throws DocumentSaveException, NoPermissionToEditDocumentException {
        try {
            documentSaver.changeDocumentDefaultVersion(docId, newDocDefaultVersionNo, publisher);
        } finally {
            invalidateDocument(docId);
        }
    }


    public void invalidateDocument(DocumentDomainObject document) {
        invalidateDocument(document.getId());
    }


    public void invalidateDocument(int docId) {
        documentLoaderCachingProxy.removeDocFromCache(docId);
        documentIndex.indexDocuments(docId);
    }


    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public List<Integer[]> getParentDocumentAndMenuIdsForDocument(DocumentDomainObject document) {
        return nativeQueriesDao.getParentDocumentAndMenuIdsForDocument(document.getId());
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
        return nativeQueriesDao.getAllMimeTypes().toArray(new String[]{});
    }


    public void deleteDocument(final DocumentDomainObject document, UserDomainObject user) {
        if (document instanceof TextDocumentDomainObject) {
            TextDocumentDomainObject textDoc = (TextDocumentDomainObject) document;

            imcmsServices.getImageCacheMapper().deleteDocumentImagesCache(document.getId(), textDoc.getImages());
        }

        documentSaver.getMetaDao().deleteDocument(document.getId());
        document.accept(new DocumentDeletingVisitor());
        documentIndex.removeDocument(document);

        documentLoaderCachingProxy.removeDocFromCache(document.getId());
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
        return ArrayUtils.toPrimitive(ids.toArray(new Integer[]{}));
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
                return ((String) alias).toLowerCase();
            }
        };

        return (Set<String>) CollectionUtils.collect(
                aliasesList, transformer, aliasesSet);
    }

    /**
     * @param documentIdentity document id or alias.
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
     * <p/>
     * Please note that provided document is not used as a new document prototype/template; it is used as a DTO
     * to pass existing doc identities (id, version, language) to the method.
     *
     * @param doc  existing doc.
     * @param user
     * @return working version of new saved document in source document's language.
     * @throws NoPermissionToAddDocumentToMenuException
     *
     * @throws DocumentSaveException
     */
    public <T extends DocumentDomainObject> T copyDocument(final T doc, final UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        Integer docId = copyDocument(doc.getId(), doc.getVersionNo(), user);

        @SuppressWarnings("unchecked")
        T workingDocument = (T)getWorkingDocument(docId, doc.getLanguage());

        return workingDocument;
    }


    /**
     * Creates a new doc as a copy of an existing doc.
     * Not a part of public API - used by admin interface.
     *
     * @return new doc id.
     * @since 6.0
     */
    public Integer copyDocument(final int docId, final int docVersionNo, final UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        // todo: put into resource file.
        String copyHeadlineSuffix = "(Copy/Kopia)";

        Meta meta = documentSaver.getMetaDao().getMeta(docId);
        List<I18nMeta> i18nMetas = documentSaver.getMetaDao().getI18nMetas(docId);
        List<DocumentDomainObject> docs = new LinkedList<DocumentDomainObject>();

        makeDocumentLookNew(meta, user);
        meta.setId(null);
        meta.removeAlis();

        for (I18nMeta i18nMeta : i18nMetas) {
            i18nMeta.setId(null);
            i18nMeta.setDocId(null);

            i18nMeta.setHeadline(i18nMeta.getHeadline() + copyHeadlineSuffix);

            I18nLanguage language = i18nMeta.getLanguage();
            DocumentDomainObject doc = getCustomDocument(docId, docVersionNo, language).clone();

            doc.accept(new DocIdentityCleanerVisitor());
            doc.setMeta(meta);
            doc.setI18nMeta(i18nMeta);

            docs.add(doc);
        }

        if (docs.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Unable to copy. Source document does not exists. DocId: %d, doc version no: %d.",
                    docId, docVersionNo));
        }

        Integer docCopyId = documentSaver.copyDocument(docs, user);

        invalidateDocument(docCopyId);

        return docCopyId;
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
    public DocumentDomainObject getDefaultDocument(int docId) {
        return getDefaultDocument(docId, Imcms.getI18nSupport().getDefaultLanguage());
    }


    /**
     * @param docId
     * @return working document in default language.
     * @since 6.0
     */
    public DocumentDomainObject getWorkingDocument(int docId) {
        return getWorkingDocument(docId, Imcms.getI18nSupport().getDefaultLanguage());
    }


    /**
     * @param docId
     * @return custom document in default language.
     * @since 6.0
     */
    public DocumentDomainObject getCustomDocument(int docId, int docVersionNo) {
        return getCustomDocument(docId, docVersionNo, Imcms.getI18nSupport().getDefaultLanguage());
    }


    /**
     * Returns document.
     * <p/>
     * Delegates call to a callback associated with a user.
     * If there is no callback then a default document is returned.
     *
     * @param docId document id.
     */
    public DocumentDomainObject getDocument(int docId) {
        UserDomainObject user = Imcms.getUser();
        DocGetterCallback callback = user == null ? null : user.getDocGetterCallback();

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
    public DocumentDomainObject getWorkingDocument(int docId, I18nLanguage language) {
        return documentLoaderCachingProxy.getWorkingDoc(docId, language);
    }

    /**
     * @param docId
     * @param language
     * @return default document
     * @since 6.0
     */
    public DocumentDomainObject getDefaultDocument(int docId, I18nLanguage language) {
        return documentLoaderCachingProxy.getDefaultDoc(docId, language);
    }


    /**
     * @param docId
     * @param languageCode
     * @return default document
     * @since 6.0
     */
    public DocumentDomainObject getDefaultDocument(int docId, String languageCode) {
        return documentLoaderCachingProxy.getDefaultDoc(docId, getImcmsServices().getI18nSupport().getByCode(languageCode));
    }


    /**
     * Returns custom document.
     * <p/>
     * Custom document is never cached.
     *
     * @param docId
     * @param docVersionNo
     * @param language
     * @return custom document
     * @since 6.0
     */
    public DocumentDomainObject getCustomDocument(int docId, int docVersionNo, I18nLanguage language) {
        return documentLoaderCachingProxy.getCustomDoc(docId, docVersionNo, language);
    }


    public CategoryMapper getCategoryMapper() {
        return categoryMapper;
    }

    public Database getDatabase() {
        return database;
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
     * Saves text and non-saved enclosing content loop the text may refer.
     * Updates doc's last modified datetime.
     * <p/>
     * Non saved enclosing content loop might be added to the doc by ContentLoopTag2.
     *
     * @param doc - existing document
     * @param text - text being saved
     *
     * @see com.imcode.imcms.servlet.admin.SaveText
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     *
     * @throws IllegalStateException if text 'no' is not set
     */
    public synchronized void saveTextDocText(TextDocumentDomainObject doc, TextDomainObject text, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {

        if (text.getNo() == null)
            throw new IllegalStateException("text no is not set");

        try {
            documentSaver.saveText(doc, text, user);
        } finally {
            invalidateDocument(doc);
        }
    }

    /**
     * Saves images and non-saved enclosing content loop if any.
     * <p/>
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
     * <p/>
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

    public void setDocumentIndex(DocumentIndex documentIndex) {
        this.documentIndex = documentIndex;
    }


    /**
     * @param documentIds
     * @return default documents.
     */
    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        UserDomainObject user = Imcms.getUser();
        DocGetterCallback callback = user == null ? null : user.getDocGetterCallback();
        I18nLanguage language = callback != null
                ? callback.getParams().language()
                : Imcms.getI18nSupport().getDefaultLanguage();

        List<DocumentDomainObject> docs = new LinkedList<DocumentDomainObject>();

        for (Integer docId : documentIds) {
            DocumentDomainObject doc = getDefaultDocument(docId, language);
            if (doc != null) {
                docs.add(doc);
            }
        }

        return docs;
    }


    private void removeNonInheritedCategories(DocumentDomainObject document) {
        Set<CategoryDomainObject> categories = getCategoryMapper().getCategories(document.getCategoryIds());
        for (CategoryDomainObject category : categories) {
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

    public I18nMeta getI18nMeta(int docId, I18nLanguage language) {
        return documentSaver.getMetaDao().getI18nMeta(docId, language);
    }

    public List<I18nMeta> getI18nMetas(int docId) {
        return documentSaver.getMetaDao().getI18nMetas(docId);
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
        public void saveDocumentWithI18nSupport(DocumentDomainObject document, Map<I18nLanguage, I18nMeta> labelsMap, EnumSet<SaveDirectives> saveParams, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().saveDocument(document, labelsMap, user);
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
     * @since 6.0
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

    public DocLoaderCachingProxy getDocumentLoaderCachingProxy() {
        return documentLoaderCachingProxy;
    }

    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

}