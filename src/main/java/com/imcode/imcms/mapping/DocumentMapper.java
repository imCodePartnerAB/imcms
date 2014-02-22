package com.imcode.imcms.mapping;

import com.google.common.collect.Sets;
import com.imcode.db.Database;
import com.imcode.imcms.DocIdentityCleanerVisitor;
import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.dao.NativeQueriesDao;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.mapping.orm.DocCommonContent;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.*;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.oro.text.perl.Perl5Util;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

/**
 * Note:
 * Spring is used to instantiate but not to inject NativeQueriesDao, DocumentSaver, DocumentLoader and CategoryMapper.
 */
@Service
public class DocumentMapper implements DocumentGetter {

    /**
     * Document save options.
     */
    public enum SaveOpts {
        // Applies to text document only.
        CopyDocCommonContentIntoTextFields
    }

    private final static String COPY_HEADLINE_SUFFIX_TEMPLATE = "copy_prefix.html";

    private Database database;

    private DocumentIndex documentIndex;

    private ImcmsServices imcmsServices;

    /**
     * instantiated by SpringFramework.
     */
    private NativeQueriesDao nativeQueriesDao;

    /**
     * instantiated by SpringFramework.
     */
    private DocumentLoader documentLoader;

    /**
     * Document loader caching proxy. Intercepts calls to DocumentLoader.
     */
    private DocLoaderCachingProxy documentLoaderCachingProxy;

    /**
     * Contain document saving and updating routines.
     * instantiated by SpringFramework.
     */
    private DocumentSaver documentSaver;

    /**
     * instantiated by SpringFramework.
     */
    private CategoryMapper categoryMapper;

    /**
     * Empty constructor for unit testing.
     */
    public DocumentMapper() {
    }

    public DocumentMapper(ImcmsServices services, Database database) {
        this.imcmsServices = services;
        this.database = database;

        Config config = services.getConfig();
        int documentCacheMaxSize = config.getDocumentCacheMaxSize();

        documentLoader = services.getManagedBean(DocumentLoader.class);
        documentLoader.getDocumentInitializingVisitor().getTextDocumentInitializer().setDocumentGetter(this);

        documentLoaderCachingProxy = new DocLoaderCachingProxy(documentLoader, services.getDocumentLanguageSupport(), documentCacheMaxSize);

        nativeQueriesDao = services.getManagedBean(NativeQueriesDao.class);
        categoryMapper = services.getManagedBean(CategoryMapper.class);

        documentSaver = services.getManagedBean(DocumentSaver.class);
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
     * <p/>
     * By default if parent doc type is {@link imcode.server.document.textdocument.TextDocumentDomainObject} its default template is used.
     * It might be overridden however if most privileged permission set type for the current user is either
     * {@link imcode.server.document.DocumentPermissionSetTypeDomainObject#RESTRICTED_1}
     * or
     * {@link imcode.server.document.DocumentPermissionSetTypeDomainObject#RESTRICTED_2}
     * and there is a default template associated with that set type.
     * <p/>
     * Please note:
     * According to specification only doc of type {@link imcode.server.document.textdocument.TextDocumentDomainObject}
     * can be used as parent (of a 'profile').
     * NB! for some (undocumented) reason a doc of any type might be used as a parent.
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
            templateName = ((TextDocumentDomainObject) parent).getDefaultTemplateName();
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
        return new GetterDocumentReference(childId, this);
    }


    /**
     * Saves doc as new.
     *
     * @param doc
     * @param user
     * @return saved document.
     * @throws DocumentSaveException
     * @throws imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException
     * @see #createDocumentOfTypeFromParent(int, imcode.server.document.DocumentDomainObject, imcode.server.user.UserDomainObject)
     * @see imcode.server.document.DocumentDomainObject#fromDocumentTypeId(int)
     */
    public <T extends DocumentDomainObject> T saveNewDocument(final T doc, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {
        T docClone = (T) doc.clone();
        DocumentLanguage language = docClone.getLanguage();

        if (language == null) {
            language = imcmsServices.getDocumentLanguageSupport().getDefaultLanguage();
            docClone.setLanguage(language);
        }

        return saveNewDocument(doc, Collections.singletonMap(doc.getLanguage(), doc.getCommonContent()), user);
    }


    /**
     * Saves doc as new.
     * <p/>
     * According to the spec, new doc creation UI allows to provide i18nMeta texts
     * in all languages available in the system.
     * However, a DocumentDomainObject has one-to-one relationship with i18nMeta.
     * To workaround this limitation and provide backward compatibility with legacy API,
     * appearances are passed in a separate parameter and doc's appearance is ignored.
     *
     * @param doc
     * @param appearances
     * @param user
     * @param saveOpts
     * @param <T>
     * @return saved document
     * @throws DocumentSaveException
     * @throws imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T saveNewDocument(final T doc, Map<DocumentLanguage, DocumentCommonContent> appearances,
                                                              EnumSet<SaveOpts> saveOpts,
                                                              final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        if (appearances.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Unable to save new document. i18nMetas must not be empty."));
        }

        T docClone = (T) doc.clone();

        int docId = documentSaver.saveNewDocument(docClone, appearances, saveOpts, user);

        invalidateDocument(docId);

        return (T) getWorkingDocument(docId, docClone.getLanguage());
    }

    /**
     * Saves doc as new.
     * <p/>
     * According to the spec, new doc creation UI allows to provide i18nMeta texts
     * in all languages available in the system.
     * However, a DocumentDomainObject has one-to-one relationship with i18nMeta.
     * To workaround this limitation and provide backward compatibility with legacy API,
     * i18nMeta-s are passed in a separate parameter and doc's i18nMeta is ignored.
     *
     * @param doc
     * @param appearances
     * @param user
     * @param <T>
     * @return
     * @throws DocumentSaveException
     * @throws imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T saveNewDocument(final T doc, Map<DocumentLanguage, DocumentCommonContent> appearances, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        return saveNewDocument(doc, appearances, EnumSet.noneOf(SaveOpts.class), user);
    }


    /**
     * Updates existing document.
     */
    public void saveDocument(final DocumentDomainObject doc, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
        saveDocument(doc, Collections.singletonMap(doc.getLanguage(), doc.getCommonContent()), user);
    }


    /**
     * Updates existing document.
     *
     * @since 6.0
     */
    public void saveDocument(final DocumentDomainObject doc, Map<DocumentLanguage, DocumentCommonContent> appearances, final UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

        DocumentDomainObject docClone = doc.clone();
        DocumentDomainObject oldDoc = getCustomDocument(doc.getRef());

        try {
            documentSaver.updateDocument(docClone, appearances, oldDoc, user);
        } finally {
            invalidateDocument(doc.getId());
        }
    }


    /**
     * Saves document menu.
     *
     * @since 6.0
     */
    public void saveTextDocMenu(TextDocumentMenuWrapper menuWrapper, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

        try {
            documentSaver.saveMenu(menuWrapper, user);
        } finally {
            invalidateDocument(menuWrapper.getDocId());
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

        List<DocumentDomainObject> docs = new LinkedList<>();

        for (DocumentLanguage language : imcmsServices.getDocumentLanguageSupport().getLanguages()) {
            DocRef docRef = DocRef.of(docId, DocumentVersion.WORKING_VERSION_NO, language.getCode());
            DocumentDomainObject doc = documentLoaderCachingProxy.getCustomDoc(docRef);
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
        documentIndex.indexDocument(docId);
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

    public void deleteDocument(final int docId, UserDomainObject user) {
        deleteDocument(getDefaultDocument(docId), user);
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

            TextDocumentDomainObject containingDocument = getDocument(containingDocumentId);
            documentMenuPairs[i] = new TextDocumentMenuIndexPair(containingDocument, menuIndex);
        }

        return documentMenuPairs;
    }

    public List<Integer> getParentDocsIds(DocumentDomainObject doc) {
        return nativeQueriesDao.getParentDocsIds(doc.getId());
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

    /**
     * @return documents id range or null if there are no documents.
     */
    public IntRange getDocumentIdRange() {
        Integer[] minMaxPair = documentSaver.getMetaDao().getMinMaxDocumentIds();

        return minMaxPair[0] == null ? null : new IntRange(minMaxPair[0], minMaxPair[1]);
    }

    // TODO: refactor
    public Set<String> getAllDocumentAlias() {
        List<String> aliasesList = documentLoader.getMetaDao().getAllAliases();
        Set<String> aliasesSet = new HashSet<>();
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
     * @throws imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException
     * @throws DocumentSaveException
     */
    public <T extends DocumentDomainObject> T copyDocument(final T doc, final UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        Integer docId = copyDocument(doc.getRef(), user);

        @SuppressWarnings("unchecked")
        T workingDocument = getWorkingDocument(docId, doc.getLanguage());

        return workingDocument;
    }


    /**
     * Creates a new doc as a copy of an existing doc.
     * Not a part of public API - used by admin interface.
     *
     * @return new doc id.
     * @since 6.0
     */
    //fixme: implement
    public Integer copyDocument(final DocRef docRef, final UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

//        // todo: put into resource file.
//        String copyHeadlineSuffix = "(Copy/Kopia)";
//
//        Meta meta = documentLoader.loadMeta(documentIdentity.getDocId());
//        List<DocAppearance> i18nMetas = documentSaver.getMetaDao().getI18nMetas(documentIdentity.getDocId());
//        List<DocumentDomainObject> docs = new LinkedList<>();
//
//        makeDocumentLookNew(meta, user);
//        meta.setId(null);
//        meta.removeAlis();
//
//        for (DocumentAppearance i18nMeta : i18nMetas) {
//            DocumentLanguage language = i18nMeta.getLanguage();
//            DocumentDomainObject doc = getCustomDocument(DocumentIdentity.buillder(documentIdentity).docLanguage(language).build());
//
//            doc.accept(new DocIdentityCleanerVisitor());
//            doc.setMeta(meta);
//            doc.setAppearance(DocumentAppearance.builder(i18nMeta).headline(i18nMeta.getHeadline() + copyHeadlineSuffix).build());
//
//            docs.add(doc);
//        }
//
//        if (docs.isEmpty()) {
//            throw new IllegalArgumentException(String.format(
//                    "Unable to copy. Source document does not exists. DocRef: %s.", documentIdentity));
//        }
//
//        Integer docCopyId = documentSaver.copyDocument(docs, user);
//
//        invalidateDocument(docCopyId);
//
//        return docCopyId;
        return 0;
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
    public <T extends DocumentDomainObject> T getDefaultDocument(int docId) {
        return getDefaultDocument(docId, imcmsServices.getDocumentLanguageSupport().getDefaultLanguage());
    }


    /**
     * @param docId
     * @return working document in default language.
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getWorkingDocument(int docId) {
        return getWorkingDocument(docId, imcmsServices.getDocumentLanguageSupport().getDefaultLanguage());
    }


    /**
     * @return custom document in default language.
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getCustomDocumentInDefaultLanguage(DocRef docRef) {
        return getCustomDocument(
                DocRef.buillder(docRef)
                        .docLanguageCode(imcmsServices.getDocumentLanguageSupport().getDefaultLanguage().getCode())
                        .build()
        );
    }


    /**
     * Returns document.
     * <p/>
     * Delegates call to a callback associated with a user.
     * If there is no callback then a default document is returned.
     *
     * @param docId document id.
     */
    public <T extends DocumentDomainObject> T getDocument(int docId) {
        UserDomainObject user = Imcms.getUser();
        DocGetterCallback callback = user == null ? null : user.getDocGetterCallback();

        return callback == null
                ? (T) getDefaultDocument(docId)
                : (T) callback.getDoc(docId, user, this);
    }

    /**
     * @param docId
     * @param language
     * @return working document
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getWorkingDocument(int docId, DocumentLanguage language) {
        return getWorkingDocument(docId, language.getCode());
    }

    /**
     * @param docId
     * @param docLanguageCode
     * @return working document
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getWorkingDocument(int docId, String docLanguageCode) {
        return documentLoaderCachingProxy.getWorkingDoc(docId, docLanguageCode);
    }

    /**
     * @param docId
     * @param language
     * @return default document
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getDefaultDocument(int docId, DocumentLanguage language) {
        return documentLoaderCachingProxy.getDefaultDoc(docId, language.getCode());
    }


    /**
     * @param docId
     * @param languageCode
     * @return default document
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getDefaultDocument(int docId, String languageCode) {
        return documentLoaderCachingProxy.getDefaultDoc(docId, languageCode);
    }


    /**
     * Returns custom document.
     * <p/>
     * Custom document is never cached.
     *
     * @return custom document
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getCustomDocument(DocRef docRef) {
        return documentLoaderCachingProxy.getCustomDoc(docRef);
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
     * @param textWrapper - text being saved
     * @throws IllegalStateException if text 'docNo', 'versionNo', 'no' or 'language' is not set
     */
    public synchronized void saveTextDocText(TextDocumentTextWrapper textWrapper, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        try {
            documentSaver.saveText(textWrapper, user);
        } finally {
            invalidateDocument(textWrapper.getDocRef().getDocId());
        }
    }


    /**
     * Saves text and non-saved enclosing content loop the text may refer.
     * Updates doc's last modified datetime.
     * <p/>
     * Non saved enclosing content loop might be added to the doc by ContentLoopTag2.
     *
     * @param texts - texts being saved
     * @throws IllegalStateException if text 'docNo', 'versionNo', 'no' or 'language' is not set
     * @see com.imcode.imcms.servlet.tags.ContentLoopTag2
     */
    public synchronized void saveTextDocTexts(Collection<TextDocumentTextWrapper> texts, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        try {
            documentSaver.saveTexts(texts, user);
        } finally {
            Set<Integer> docIds = Sets.newHashSet();
            for (TextDocumentTextWrapper textWrapper : texts) {
                docIds.add(textWrapper.getDocId());
            }

            for (Integer docId : docIds) {
                invalidateDocument(docId);
            }
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
    public synchronized void saveTextDocImages(Collection<TextDocumentImageWrapper> images, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {

        try {
            documentSaver.saveImages(images, user);
        } finally {
            Set<Integer> docIds = Sets.newHashSet();
            for (TextDocumentImageWrapper image : images) {
                docIds.add(image.getDocId());
            }

            for (Integer docId : docIds) {
                invalidateDocument(docId);
            }
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
    public synchronized void saveTextDocImage(TextDocumentImageWrapper image, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
        try {
            documentSaver.saveImage(image, user);
        } finally {
            invalidateDocument(image.getDocRef().getDocId());
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
        DocumentLanguage language = callback != null
                ? callback.documentLanguages().preferred()
                : imcmsServices.getDocumentLanguageSupport().getDefaultLanguage();

        List<DocumentDomainObject> docs = new LinkedList<>();

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

    public DocumentCommonContent getI18nMeta(DocRef docRef) {
        DocCommonContent ormAppearance = documentSaver.getMetaDao().getDocAppearance(docRef);

        return ormAppearance == null ? null : OrmToApi.toApi(ormAppearance);
    }

    public Map<DocumentLanguage, DocumentCommonContent> getAppearances(int docId) {
        Map<DocumentLanguage, DocumentCommonContent> appearancesMap = new HashMap<>();

        List<DocCommonContent> ormAppearances = documentSaver.getMetaDao().getAppearance(docId);

        for (DocCommonContent ormAppearance : ormAppearances) {
            appearancesMap.put(OrmToApi.toApi(ormAppearance.getLanguage()), OrmToApi.toApi(ormAppearance));
        }

        return appearancesMap;
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

    @Deprecated
    public static class SaveEditedDocumentCommand extends DocumentPageFlow.SaveDocumentCommand {

        @Override
        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
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
     *
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
            boolean correctFileForFileDocumentFile = file.equals(DocumentSavingVisitor.getFileForFileDocumentFile(DocRef.of(fileDocumentId, fileDocument.getVersionNo()), fileId));
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