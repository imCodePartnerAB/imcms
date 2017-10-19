package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.NativeQueries;
import com.imcode.imcms.mapping.jpa.doc.PropertyRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.MenuRepository;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.stream.Stream;

import static imcode.server.ImcmsConstants.*;

/**
 * Spring is used to instantiate but not to initialize the instance.
 * Init must be called to complete initialization.
 */
@Component
public class DocumentMapper implements DocumentGetter {

    private Database database;
    private DocumentIndex documentIndex;
    private ImcmsServices imcmsServices;
    /**
     * Document loader caching proxy. Intercepts calls to DocumentLoader.
     */
    private DocumentLoaderCachingProxy documentLoaderCachingProxy;
    @Inject
    private NativeQueries nativeQueries;
    @Inject
    private DocumentLoader documentLoader;
    @Inject
    private DocumentSaver documentSaver;
    @Inject
    private CategoryMapper categoryMapper;
    @Inject
    private DocumentContentMapper documentContentMapper;
    @Inject
    private DocumentVersionMapper documentVersionMapper;
    @Inject
    private MenuRepository menuRepository;
    @Inject
    private PropertyRepository propertyRepository;

    public DocumentMapper() {
    }

    /**
     * @deprecated init should be used instead
     */
    @Deprecated
    public DocumentMapper(ImcmsServices services, Database database) {
        throw new UnsupportedOperationException("Use com.imcode.imcms.mapping.DocumentMapper#init() method instead");
    }

    private static void deleteFileDocumentFilesAccordingToFileFilter(FileFilter fileFilter) {
        File filePath = Imcms.getServices().getConfig().getFilePath();
        File[] filesToDelete = filePath.listFiles(fileFilter);
        Stream.of(filesToDelete).forEach(File::delete);
    }

    static void deleteAllFileDocumentFiles(FileDocumentDomainObject fileDocument) {
        deleteFileDocumentFilesAccordingToFileFilter(new FileDocumentFileFilter(fileDocument));
    }

    static void deleteOtherFileDocumentFiles(FileDocumentDomainObject fileDocument) {
        deleteFileDocumentFilesAccordingToFileFilter(new SuperfluousFileDocumentFilesFileFilter(fileDocument));
    }

    public void init(ImcmsServices services, Database database, DocumentIndex documentIndex) {
        this.imcmsServices = services;
        this.database = database;
        this.documentIndex = documentIndex;

        Config config = services.getConfig();
        int documentCacheMaxSize = config.getDocumentCacheMaxSize();

        documentLoaderCachingProxy = new DocumentLoaderCachingProxy(documentVersionMapper, documentLoader, services.getDocumentLanguages(), documentCacheMaxSize);

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
     * <p>
     * Doc's CommonContent and content (texts, images, urls, files, etc) are not inherited.
     */
    public DocumentDomainObject createDocumentOfTypeFromParent(
            int documentTypeId,
            DocumentDomainObject parentDoc,
            UserDomainObject user) {

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

        newDocument.getMeta().setId(null);
        newDocument.getMeta().setDocumentType(documentTypeId);

        newDocument.setVersionNo(0);

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
     * <p>
     * By default if parent doc type is {@link imcode.server.document.textdocument.TextDocumentDomainObject} its default template is used.
     * It might be overridden however if most privileged permission set type for the current user is either
     * {@link imcode.server.document.DocumentPermissionSetTypeDomainObject#RESTRICTED_1}
     * or
     * {@link imcode.server.document.DocumentPermissionSetTypeDomainObject#RESTRICTED_2}
     * and there is a default template associated with that set type.
     * <p>
     * Please note:
     * According to specification only doc of type {@link imcode.server.document.textdocument.TextDocumentDomainObject}
     * can be used as parent (of a 'profile').
     * NB! for some (undocumented) reason a doc of any type might be used as a parent.
     */
    private void setTemplateForNewTextDocument(TextDocumentDomainObject newTextDocument, UserDomainObject user,
                                               DocumentDomainObject parent) {
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

    private void makeDocumentLookNew(DocumentDomainObject document, UserDomainObject user) {
        makeDocumentLookNew(document.getMeta(), user);
    }

    private void makeDocumentLookNew(DocumentMeta documentMeta, UserDomainObject user) {
        Date now = new Date();

        documentMeta.setCreatorId(user.getId());
        setCreatedAndModifiedDatetimes(documentMeta, now);
        documentMeta.setPublicationStartDatetime(now);
        documentMeta.setPublisherId(user.getId());
        documentMeta.setArchivedDatetime(null);
        documentMeta.setArchiverId(null);
        documentMeta.setPublicationEndDatetime(null);
        documentMeta.setDepublisherId(null);
        documentMeta.setPublicationStatus(Document.PublicationStatus.NEW);
    }

    public DocumentReference getDocumentReference(DocumentDomainObject document) {
        return getDocumentReference(document.getId());
    }

    private DocumentReference getDocumentReference(int childId) {
        return new GetterDocumentReference(childId, this);
    }

    /**
     * Saves doc as new.
     *
     * @return saved document.
     * @see #createDocumentOfTypeFromParent(int, imcode.server.document.DocumentDomainObject, imcode.server.user.UserDomainObject)
     * @see imcode.server.document.DocumentDomainObject#fromDocumentTypeId(int)
     */
    public <T extends DocumentDomainObject> T saveNewDocument(T doc, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {
        T docClone = (T) doc.clone();
        DocumentLanguage language = docClone.getLanguage();

        if (language == null) {
            language = imcmsServices.getDocumentLanguages().getDefault();
            docClone.setLanguage(language);
        }

        return saveNewDocument(doc, Collections.singletonMap(doc.getLanguage(), doc.getCommonContent()), user);
    }

    /**
     * Saves doc as new.
     * <p>
     * According to the spec, new doc creation UI allows to provide i18nMeta texts
     * in all languages available in the system.
     * However, a DocumentDomainObject has one-to-one relationship with i18nMeta.
     * To workaround this limitation and provide backward compatibility with legacy API,
     * appearances are passed in a separate parameter and doc's appearance is ignored.
     *
     * @return saved document
     * @since 6.0
     */
    private <T extends DocumentDomainObject> T saveNewDocument(T doc, Map<DocumentLanguage, DocumentCommonContent> appearances,
                                                               EnumSet<SaveOpts> saveOpts,
                                                               UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        if (appearances.isEmpty()) {
            throw new IllegalArgumentException("Unable to save new document. i18nMetas must not be empty.");
        }

        T docClone = (T) doc.clone();

        int docId = documentSaver.saveNewDocument(docClone, appearances, saveOpts, user);

        invalidateDocument(docId);

        return getWorkingDocument(docId, docClone.getLanguage());
    }

    /**
     * Saves doc as new.
     * <p>
     * According to the spec, new doc creation UI allows to provide i18nMeta texts
     * in all languages available in the system.
     * However, a DocumentDomainObject has one-to-one relationship with i18nMeta.
     * To workaround this limitation and provide backward compatibility with legacy API,
     * i18nMeta-s are passed in a separate parameter and doc's i18nMeta is ignored.
     *
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T saveNewDocument(T doc, Map<DocumentLanguage, DocumentCommonContent> appearances, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        return saveNewDocument(doc, appearances, EnumSet.noneOf(SaveOpts.class), user);
    }

    /**
     * Updates existing document.
     */
    public int saveDocument(DocumentDomainObject doc, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
        return saveDocument(doc, Collections.singletonMap(doc.getLanguage(), doc.getCommonContent()), user);
    }

    /**
     * Updates existing document.
     *
     * @since 6.0
     */
    public int saveDocument(DocumentDomainObject doc, Map<DocumentLanguage, DocumentCommonContent> commonContents, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

        DocumentDomainObject docClone = doc.clone();
        DocumentDomainObject oldDoc = getCustomDocument(doc.getRef());

        try {
            documentSaver.updateDocument(docClone, commonContents, oldDoc, user);
        } finally {
            invalidateDocument(doc.getId());
        }

        return doc.getId();
    }

    /**
     * Creates next document version.
     * <p>
     * Saves document's working version copy as next document version.
     *
     * @return new document version.
     * @since 6.0
     */
    DocumentVersion makeDocumentVersion(int docId, UserDomainObject user)
            throws DocumentSaveException {

        List<DocumentDomainObject> docs = new LinkedList<>();

        for (DocumentLanguage language : imcmsServices.getDocumentLanguages().getAll()) {
            DocumentDomainObject doc = getWorkingDocument(docId, language);
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
    void changeDocumentDefaultVersion(int docId, int newDocDefaultVersionNo, UserDomainObject publisher)
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

    private void invalidateDocument(int docId) {
        documentLoaderCachingProxy.removeDocFromCache(docId);
        documentIndex.indexDocument(docId);
    }

    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public void setDocumentIndex(DocumentIndex documentIndex) {
        this.documentIndex = documentIndex;
    }

    public List<Integer[]> getParentDocumentAndMenuIdsForDocument(DocumentDomainObject document) {
        return menuRepository.getParentDocumentAndMenuIdsForDocument(document.getId(), document.getVersionNo());
    }

    public void deleteDocument(int docId) {
        deleteDocument(getDefaultDocument(docId));
    }

    public void deleteDocument(DocumentDomainObject document) {
        documentSaver.getDocRepository().deleteDocument(document.getId());
        document.accept(new DocumentDeletingVisitor());
        documentIndex.removeDocument(document);
        documentLoaderCachingProxy.removeDocFromCache(document.getId());
    }

    public Map<Integer, String> getAllDocumentTypeIdsAndNamesInUsersLanguage(UserDomainObject user) {
        return nativeQueries.getAllDocumentTypeIdsAndNamesInUsersLanguage(user.getLanguageIso639_2());
    }

    public TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingDocument(DocumentDomainObject document) {
        List<Integer[]> rows = menuRepository.getDocumentMenuPairsContainingDocument(document.getId(), document.getVersionNo());

        TextDocumentMenuIndexPair[] documentMenuPairs = new TextDocumentMenuIndexPair[rows.size()];

        for (int i = 0; i < documentMenuPairs.length; i++) {
            Object[] row = rows.get(i);

            int containingDocumentId = (int) row[0];
            int menuIndex = (int) row[1];

            TextDocumentDomainObject containingDocument = getDocument(containingDocumentId);
            documentMenuPairs[i] = new TextDocumentMenuIndexPair(containingDocument, menuIndex);
        }

        return documentMenuPairs;
    }

    public Iterator<DocumentDomainObject> getDocumentsIterator(IntRange idRange) {
        return new DocumentsIterator(getDocumentIds(idRange));
    }

    private int[] getDocumentIds(IntRange idRange) {
        List<Integer> ids = documentSaver.getDocRepository().getDocumentIdsInRange(
                idRange.getMinimumInteger(),
                idRange.getMaximumInteger());

        return ArrayUtils.toPrimitive(ids.toArray(new Integer[ids.size()]));
    }

    public List<Integer> getAllDocumentIds() {
        return documentSaver.getDocRepository().getAllDocumentIds();
    }

    public List<String> getAllDocumentAlias() {
        return propertyRepository.findAllAliases();
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
    private Integer toDocumentId(String documentIdentity) {
        if (documentIdentity == null) {
            return null;
        }

        try {
            return Integer.valueOf(documentIdentity);
        } catch (NumberFormatException e) {
            return documentLoaderCachingProxy.getDocIdByAlias(documentIdentity);
        }
    }

    public int getLowestDocumentId() {
        return documentSaver.getDocRepository().getMaxDocumentId();
    }

    public int getHighestDocumentId() {
        return documentSaver.getDocRepository().getMinDocumentId();
    }

    /**
     * Creates a new doc as a copy of an existing doc.
     * <p>
     * Please note that provided document is not used as a new document prototype/template; it is used as a DTO
     * to pass existing doc identities (id, version, language) to the method.
     *
     * @param doc  existing doc.
     * @param user the user
     * @return working version of new saved document in source document's language.
     */
    public <T extends DocumentDomainObject> T copyDocument(T doc, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        Integer docId = copyDocumentsWithCommonMetaAndVersion(doc.getVersionRef(), user);

        @SuppressWarnings("unchecked")
        T workingDocument = getWorkingDocument(docId, doc.getLanguage());

        return workingDocument;
    }

    /**
     * Copies docs that share the same document id and version no.
     * Copied docs version is {@link com.imcode.imcms.api.DocumentVersion#WORKING_VERSION_NO}
     *
     * @return copied doc id.
     * @since 6.0
     */
    private int copyDocumentsWithCommonMetaAndVersion(VersionRef versionRef, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

        // todo: put into resource file.
        String copyHeadlineSuffix = "(Copy/Kopia)";

        DocumentMeta documentMeta = documentLoader.loadMeta(versionRef.getDocId());
        Map<DocumentLanguage, DocumentCommonContent> dccMap = documentContentMapper
                .getCommonContents(versionRef.getDocId(), versionRef.getNo());
        List<DocumentDomainObject> newDocs = new LinkedList<>();

        makeDocumentLookNew(documentMeta, user);
        documentMeta.setId(null);
        documentMeta.removeAlis();

        for (Map.Entry<DocumentLanguage, DocumentCommonContent> e : dccMap.entrySet()) {
            DocumentLanguage language = e.getKey();
            DocumentCommonContent dcc = e.getValue();

            DocumentDomainObject newDoc = getCustomDocument(DocRef.of(versionRef, language.getCode())).clone();
            DocumentCommonContent newDcc = DocumentCommonContent.builder(dcc).headline(copyHeadlineSuffix + " " + dcc.getHeadline()).build();

            newDoc.setMeta(documentMeta);
            newDoc.setCommonContent(newDcc);

            newDocs.add(newDoc);
        }

        if (newDocs.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Unable to copy. Source document does not exists. DocVersionRef: %s.", versionRef));
        }

        Integer docCopyId = documentSaver.saveNewDocsWithCommonMetaAndVersion(newDocs, user);

        invalidateDocument(docCopyId);

        return docCopyId;
    }

    public List<DocumentDomainObject> getDocumentsWithPermissionsForRole(final RoleDomainObject role) {
        return new AbstractList<DocumentDomainObject>() {
            private List<Integer> documentIds = nativeQueries.getDocumentsWithPermissionsForRole(role.getId().intValue());

            public DocumentDomainObject get(int index) {
                return getDocument(documentIds.get(index));
            }

            public int size() {
                return documentIds.size();
            }
        };
    }

    /**
     * @param docId document id
     * @return default document in default language.
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getDefaultDocument(int docId) {
        return getDefaultDocument(docId, imcmsServices.getDocumentLanguages().getDefault());
    }

    /**
     * @param docId document id
     * @return working document in default language.
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getWorkingDocument(int docId) {
        return getWorkingDocument(docId, imcmsServices.getDocumentLanguages().getDefault());
    }

    /**
     * Returns document.
     * <p>
     * Delegates call to a callback associated with a user.
     * If there is no callback then a default document is returned.
     *
     * @param docId document id.
     */
    public <T extends DocumentDomainObject> T getDocument(int docId) {
        UserDomainObject user = Imcms.getUser();
        DocGetterCallback callback = user == null ? null : user.getDocGetterCallback();

        return callback == null
                ? getDefaultDocument(docId)
                : callback.getDoc(docId, this);
    }

    /**
     * @param docId    document id
     * @param language language
     * @return working document
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getWorkingDocument(int docId, DocumentLanguage language) {
        return getWorkingDocument(docId, language.getCode());
    }

    /**
     * @param docId           document id
     * @param docLanguageCode language code
     * @return working document
     * @since 6.0
     */
    private <T extends DocumentDomainObject> T getWorkingDocument(int docId, String docLanguageCode) {
        return documentLoaderCachingProxy.getWorkingDoc(docId, docLanguageCode);
    }

    /**
     * @param docId    document id
     * @param language language
     * @return default document
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getDefaultDocument(int docId, DocumentLanguage language) {
        return (Imcms.isVersioningAllowed())
                ? documentLoaderCachingProxy.getDefaultDoc(docId, language.getCode())
                : documentLoaderCachingProxy.getWorkingDoc(docId, language.getCode());
    }

    /**
     * @param docId        document id
     * @param languageCode language code
     * @return default document
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getDefaultDocument(int docId, String languageCode) {
        return (Imcms.isVersioningAllowed())
                ? documentLoaderCachingProxy.getDefaultDoc(docId, languageCode)
                : documentLoaderCachingProxy.getWorkingDoc(docId, languageCode);
    }

    /**
     * Returns custom document.
     * <p>
     * Custom document is never cached.
     *
     * @return custom document
     * @since 6.0
     */
    <T extends DocumentDomainObject> T getCustomDocument(DocRef docRef) {
        if (!Imcms.isVersioningAllowed()) {
            // force version changing to working
            docRef = DocRef.of(docRef.getId(), DocumentVersion.WORKING_VERSION_NO, docRef.getLanguageCode());
        }
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

    /**
     * @since 6.0
     */
    void setCreatedAndModifiedDatetimes(DocumentMeta documentMeta, Date date) {
        documentMeta.setCreatedDatetime(date);
        documentMeta.setModifiedDatetime(date);
        documentMeta.setActualModifiedDatetime(date);
    }

    /**
     * Saves text and non-saved enclosing content loop the text may refer.
     * Updates doc's last modified datetime.
     * <p>
     * Non saved enclosing content loop might be added to the doc by ContentLoopTag2.
     *
     * @param container - text being saved
     * @throws IllegalStateException if text 'docNo', 'versionNo', 'no' or 'language' is not set
     */
    public synchronized void saveTextDocText(TextDocTextContainer container, UserDomainObject user)
            throws NoPermissionInternalException, DocumentSaveException {
        try {
            documentSaver.saveText(container, user);
        } finally {
            invalidateDocument(container.getDocId());
        }
    }

    /**
     * @param documentIds id's of documents
     * @return default documents.
     */
    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        UserDomainObject user = Imcms.getUser();

        DocumentLanguage language = user == null
                ? imcmsServices.getDocumentLanguages().getDefault()
                : user.getDocGetterCallback().getLanguage();

        List<DocumentDomainObject> docs = new LinkedList<>();

        for (Integer docId : documentIds) {
            DocumentDomainObject doc = getDefaultDocument(docId, language);
            if (doc != null) {
                docs.add(doc);
            }
        }

        return docs;
    }

    /**
     * Get document version by document id or alias, parameters in request and language code
     *
     * @param documentIdentity document id or alias
     * @return document with needed version and language or null
     * @since 6.0
     */
    public <T extends DocumentDomainObject> T getVersionedDocument(String documentIdentity,
                                                                   String langCode,
                                                                   ServletRequest request) {
        Integer documentId = toDocumentId(documentIdentity);

        return (documentId != null)
                ? getVersionedDocument(documentId, langCode, request)
                : null;
    }

    /**
     * Get document version by document id, parameters in request and language code
     *
     * @return document with needed version and language or null
     * @since 6.0
     */
    private <T extends DocumentDomainObject> T getVersionedDocument(int docId, String langCode, ServletRequest request) {
        return (isWorkingDocumentVersion(request))
                ? getWorkingDocument(docId, langCode)
                : getDefaultDocument(docId, langCode);
    }

    /**
     * Checks is request parameters points to working document version or not
     *
     * @return true if requesting a working version
     * @since 6.0
     */
    private boolean isWorkingDocumentVersion(ServletRequest request) {
        return ("" + PERM_EDIT_TEXT_DOCUMENT_TEXTS).equals(request.getParameter("flags"))
                || BooleanUtils.toBoolean(request.getParameter(REQUEST_PARAM__WORKING_PREVIEW))
                || ((HttpServletRequest) request).getRequestURI().contains(SINGLE_EDITOR_VIEW);
    }

    private void removeNonInheritedCategories(DocumentDomainObject document) {
        Set<CategoryDomainObject> categories = getCategoryMapper().getCategories(document.getCategoryIds());
        categories.stream()
                .filter(category -> !category.getType().isInherited())
                .forEach(category -> document.removeCategoryId(category.getId()));
    }

    public Map<DocumentLanguage, DocumentCommonContent> getCommonContents(int docId, int versionNo) {
        return documentContentMapper.getCommonContents(docId, versionNo);
    }

    /**
     * Document save options.
     * Currently applies to text documents only.
     */
    public enum SaveOpts {
        CopyDocCommonContentIntoTextFields
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

    /**
     * Sets default document version.
     *
     * @since 6.0
     */
    public static class SetDefaultDocumentVersionCommand extends DocumentPageFlow.SaveDocumentCommand {

        private static final long serialVersionUID = 7423525552360211171L;
        private Integer docVersionNo;

        public SetDefaultDocumentVersionCommand(Integer docVersionNo) {
            this.docVersionNo = docVersionNo;
        }

        @Override
        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionToEditDocumentException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
            Imcms.getServices().getDocumentMapper().changeDocumentDefaultVersion(document.getId(), docVersionNo, user);
        }
    }

    /**
     * Makes new version from a working/draft version and sets it as default version for current doc.
     *
     * @since 6.0
     */
    public static class PublishWorkingVersionCommand extends DocumentPageFlow.SaveDocumentCommand {

        private static final long serialVersionUID = -8230649834443025925L;

        @Override
        public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
            final DocumentMapper mapper = Imcms.getServices().getDocumentMapper();
            final DocumentVersion newVersion = mapper.makeDocumentVersion(document.getId(), user);
            mapper.changeDocumentDefaultVersion(document.getId(), newVersion.getNo(), user);
        }
    }

    private static class FileDocumentFileFilter implements FileFilter {

        protected final FileDocumentDomainObject fileDocument;

        FileDocumentFileFilter(FileDocumentDomainObject fileDocument) {
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
            boolean correctFileForFileDocumentFile = file.equals(DocumentSavingVisitor.getFileForFileDocumentFile(
                    VersionRef.of(fileDocumentId, fileDocument.getVersionNo()), fileId));
            boolean fileDocumentHasFile = null != fileDocument.getFile(fileId);
            return fileDocumentId == fileDocument.getId()
                    && docVersionNo == fileDocument.getVersionNo()
                    && (!correctFileForFileDocumentFile || !fileDocumentHasFile);
        }
    }

    private class DocumentsIterator implements Iterator<DocumentDomainObject> {

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

        public DocumentDomainObject next() {

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int documentId = documentIds[index++];

            return getDocument(documentId);
        }
    }


}