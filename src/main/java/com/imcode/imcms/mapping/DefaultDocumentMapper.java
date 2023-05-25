package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.controller.exception.NoPermissionInternalException;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.MenuService;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.exception.DocumentSaveException;
import com.imcode.imcms.mapping.jpa.NativeQueries;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import imcode.server.document.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static imcode.server.ImcmsConstants.*;

@Transactional
@Service
public class DefaultDocumentMapper implements DocumentMapper {

    private final DocumentLoaderCachingProxy documentLoaderCachingProxy;
    private final CommonContentService commonContentService;
    private final Database database;
    private final NativeQueries nativeQueries;
    private final DocumentSaver documentSaver;
    private final CategoryMapper categoryMapper;
    private final DocumentContentMapper documentContentMapper;
    private final MenuRepository menuRepository;
    private final LanguageService languageService;
    private final MenuService defaultMenuService;
    private DocumentIndex documentIndex;

    public DefaultDocumentMapper(NativeQueries nativeQueries,
                                 DocumentSaver documentSaver,
                                 CategoryMapper categoryMapper,
                                 DocumentContentMapper documentContentMapper,
                                 MenuRepository menuRepository,
                                 Database database,
                                 CommonContentService commonContentService,
                                 DocumentLoaderCachingProxy documentLoaderCachingProxy,
                                 LanguageService languageService,
                                 MenuService defaultMenuService) {

        this.nativeQueries = nativeQueries;
        this.documentSaver = documentSaver;
        this.categoryMapper = categoryMapper;
        this.documentContentMapper = documentContentMapper;
        this.menuRepository = menuRepository;
        this.database = database;
        this.languageService = languageService;
        this.commonContentService = commonContentService;
        this.documentLoaderCachingProxy = documentLoaderCachingProxy;
        this.defaultMenuService = defaultMenuService;
    }

    private static void deleteFileDocumentFilesAccordingToFileFilter(FileFilter fileFilter) {
        final File filePath = Imcms.getServices().getConfig().getFilePath();
        final File[] filesToDelete = filePath.listFiles(fileFilter);

        if (filesToDelete != null) {
            //noinspection ResultOfMethodCallIgnored
            Stream.of(filesToDelete).forEach(File::delete);
        }
    }

    static void deleteAllFileDocumentFiles(FileDocumentDomainObject fileDocument) {
        deleteFileDocumentFilesAccordingToFileFilter(new FileDocumentFileFilter(fileDocument));
    }

    @PostConstruct
    public void init() {
        documentSaver.setDocumentMapper(this);
    }

    @Override
    public DocumentVersionInfo getDocumentVersionInfo(int documentId) {
        return documentLoaderCachingProxy.getDocVersionInfo(documentId);
    }

    @Override
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

            setTemplateForNewTextDocument(newTextDocument, parentDoc);
        } else {
            newDocument = DocumentDomainObject.fromDocumentTypeId(documentTypeId);
            newDocument.setMeta(parentDoc.getMeta().clone());
            newDocument.setLanguage(parentDoc.getLanguage());
        }

        newDocument.getMeta().setId(null);
        newDocument.getMeta().setDocumentTypeId(documentTypeId);

        newDocument.setVersionNo(0);

        newDocument.setHeadline("");
        newDocument.setMenuText("");
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
     * <p>
     * Please note:
     * According to specification only doc of type {@link imcode.server.document.textdocument.TextDocumentDomainObject}
     * can be used as parent (of a 'profile').
     * NB! for some (undocumented) reason a doc of any type might be used as a parent.
     */
    private void setTemplateForNewTextDocument(TextDocumentDomainObject newTextDocument,
                                               DocumentDomainObject parent) {
        String templateName = null;

        if (parent instanceof TextDocumentDomainObject) {
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

    @Override
    public DocumentReference getDocumentReference(DocumentDomainObject document) {
        return getDocumentReference(document.getId());
    }

    private DocumentReference getDocumentReference(int childId) {
        return new GetterDocumentReference(childId, this);
    }

    //    TODO  Check is that correct to be able to save empty string alias
    @Override
    @SuppressWarnings("unchecked")
    public <T extends DocumentDomainObject> T saveNewDocument(T doc, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {
        T docClone = (T) doc.clone();
        Language language = docClone.getLanguage();

        if (language == null) {
            language = languageService.getDefaultLanguage();
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
    @SuppressWarnings("unchecked")
    private <T extends DocumentDomainObject> T saveNewDocument(T doc, Map<Language, DocumentCommonContent> appearances,
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

    @Override
    public <T extends DocumentDomainObject> T saveNewDocument(T doc, Map<Language, DocumentCommonContent> appearances, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

        return saveNewDocument(doc, appearances, EnumSet.noneOf(SaveOpts.class), user);
    }

    @Override
    public int saveDocument(DocumentDomainObject doc, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
        return saveDocument(doc, Collections.singletonMap(doc.getLanguage(), doc.getCommonContent()), user);
    }

    @Override
    public int saveDocument(DocumentDomainObject doc, Map<Language, DocumentCommonContent> commonContents, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

        DocumentDomainObject docClone = doc.clone();

        try {
            documentSaver.updateDocument(docClone, commonContents, user);
        } finally {
            invalidateDocument(doc.getId());
        }

        return doc.getId();
    }

    @Override
    public DocumentVersion makeDocumentVersion(int docId, UserDomainObject user) {

        List<DocumentDomainObject> docs = new LinkedList<>();

        for (Language language : languageService.getAll()) {
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

    @Override
    public void changeDocumentDefaultVersion(int docId, int newDocDefaultVersionNo, UserDomainObject publisher) {
        try {
            documentSaver.changeDocumentDefaultVersion(docId, newDocDefaultVersionNo, publisher);
        } finally {
            invalidateDocument(docId);
        }
    }

    @Override
    public void invalidateDocument(DocumentDomainObject document) {
        invalidateDocument(document.getId());
    }

    @Override
    public void invalidateDocument(int docId) {
        Set<Integer> idsToInvalidate = new HashSet<>();
        idsToInvalidate.add(docId);

        List<Menu> menus = defaultMenuService.getAll();
        for (Menu menu : menus) {
            final int foundUsages = (int) menu.getMenuItems().stream()
                    .map(MenuItem::getDocumentId)
                    .filter(id -> docId == id).distinct().count();
            if (foundUsages > 0) {
                idsToInvalidate.add(menu.getVersion().getDocId());
            }
        }

        for (Integer id : idsToInvalidate) {
            documentLoaderCachingProxy.removeDocFromCache(id);
            documentIndex.indexDocument(id);
        }
    }

	@Override
	public void invalidateDocuments(int[] docIds) {
        for (int docId : docIds) {
            invalidateDocument(docId);
        }
	}

	@Override
    public DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    @Override
    public void setDocumentIndex(DocumentIndex documentIndex) {
        this.documentIndex = documentIndex;
    }

    @Override
    @Deprecated
    public void deleteDocument(int docId) {
        deleteDocument(getDefaultDocument(docId));
    }

    @Override
    @Deprecated
    public void deleteDocument(DocumentDomainObject document) {
        documentSaver.getDocRepository().deleteDocument(document.getId());
        document.accept(new DocumentDeletingVisitor());
        documentIndex.removeDocument(document);
        documentLoaderCachingProxy.removeDocFromCache(document.getId());
        invalidateDocument(document.getId());
    }

    @Override
    public Map<Integer, String> getAllDocumentTypeIdsAndNamesInUsersLanguage(UserDomainObject user) {
        return nativeQueries.getAllDocumentTypeIdsAndNamesInUsersLanguage(user.getLanguageIso639_2());
    }

    @Override
    public TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingDocument(DocumentDomainObject document) {
        return menuRepository
                .getDocIdsByLinkedDocIdAndVersionNo(document.getId(), document.getVersionNo())
                .stream()
                .map(menu -> new TextDocumentMenuIndexPair(getDocument(menu.getVersion().getDocId()), menu.getNo()))
                .toArray(TextDocumentMenuIndexPair[]::new);
    }

    @Override
    public Iterator<DocumentDomainObject> getDocumentsIterator(IntRange idRange) {
        return new DocumentsIterator(getDocumentIds(idRange));
    }

    private int[] getDocumentIds(IntRange idRange) {
        List<Integer> ids = documentSaver.getDocRepository().getDocumentIdsInRange(
                idRange.getMinimumInteger(),
                idRange.getMaximumInteger());

        return ArrayUtils.toPrimitive(ids.toArray(new Integer[0]));
    }

    @Override
    public List<Integer> getAllDocumentIds() {
        return documentSaver.getDocRepository().getAllDocumentIds();
    }

    @Override
    public List<String> getAllDocumentAlias() {
        return commonContentService.getAllAliases();
    }

    @Override
    public DocumentDomainObject getDocument(String documentIdentity) {
        Integer documentId = toDocumentId(documentIdentity);

        return documentId == null
                ? null
                : getDocument(documentId);
    }

    @Override
    public Integer toDocumentId(String documentIdentity) {
        if (documentIdentity == null) {
            return null;
        }

        try {
            return Integer.valueOf(documentIdentity);
        } catch (NumberFormatException e) {
            return documentLoaderCachingProxy.getDocIdByAlias(documentIdentity);
        }
    }

    @Override
    public int getLowestDocumentId() {
        return documentSaver.getDocRepository().getMaxDocumentId();
    }

    @Override
    public int getHighestDocumentId() {
        return documentSaver.getDocRepository().getMinDocumentId();
    }

    @Override
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

        DocumentMeta documentMeta = documentLoaderCachingProxy.getMeta(versionRef.getDocId());
        Map<Language, DocumentCommonContent> dccMap = documentContentMapper
                .getCommonContents(versionRef.getDocId(), versionRef.getNo());
        List<DocumentDomainObject> newDocs = new LinkedList<>();

        makeDocumentLookNew(documentMeta, user);
        documentMeta.setId(null);

        for (Map.Entry<Language, DocumentCommonContent> e : dccMap.entrySet()) {
            Language language = e.getKey();
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

        int docCopyId = documentSaver.saveNewDocsWithCommonMetaAndVersion(newDocs, user);

        invalidateDocument(docCopyId);

        return docCopyId;
    }

    @Override
    public List<DocumentDomainObject> getDocumentsWithPermissionsForRole(final RoleDomainObject role) {
        return new AbstractList<DocumentDomainObject>() {
            private final List<Integer> documentIds = nativeQueries.getDocumentsWithPermissionsForRole(role.getId());

            public DocumentDomainObject get(int index) {
                return getDocument(documentIds.get(index));
            }

            public int size() {
                return documentIds.size();
            }
        };
    }

    @Override
    public <T extends DocumentDomainObject> T getDefaultDocument(int docId) {
        return getDefaultDocument(docId, languageService.getDefaultLanguage());
    }

    @Override
    public <T extends DocumentDomainObject> T getWorkingDocument(int docId) {
        return getWorkingDocument(docId, languageService.getDefaultLanguage());
    }

    @Override
    public <T extends DocumentDomainObject> T getDocument(int docId) {
        UserDomainObject user = Imcms.getUser();
        DocGetterCallback callback = user == null ? null : user.getDocGetterCallback();

        return callback == null
                ? getDefaultDocument(docId)
                : callback.getDoc(docId, this);
    }

    @Override
    public <T extends DocumentDomainObject> T getWorkingDocument(int docId, Language language) {
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

    @Override
    public <T extends DocumentDomainObject> T getDefaultDocument(int docId, Language language) {
        return getDefaultDocument(docId, language.getCode());
    }

    @Override
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
    public <T extends DocumentDomainObject> T getCustomDocument(DocRef docRef) {
        if (!Imcms.isVersioningAllowed()) {
            // force version changing to working
            docRef = DocRef.of(docRef.getId(), DocumentVersion.WORKING_VERSION_NO, docRef.getLanguageCode());
        }
        return documentLoaderCachingProxy.getCustomDoc(docRef);
    }

    @Override
    public CategoryMapper getCategoryMapper() {
        return categoryMapper;
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    /**
     * @since 6.0
     */
    void setCreatedAndModifiedDatetimes(DocumentMeta documentMeta, Date date) {
        documentMeta.setCreatedDatetime(date);
        documentMeta.setModifiedDatetime(date);
        documentMeta.setActualModifiedDatetime(date);
    }

    @Override
    public synchronized void saveTextDocText(TextDocTextContainer container, UserDomainObject user)
            throws NoPermissionInternalException {
        try {
            documentSaver.saveText(container, user);
        } finally {
            invalidateDocument(container.getDocId());
        }
    }

    @Override
    public void saveTextsDocText(TextDocument textDocument, UserDomainObject user) throws NoPermissionInternalException {
        try {
            final TextDocumentDomainObject internalTextDoc = textDocument.getInternal();

            final Map<Language, TextDomainObject> langTexts = internalTextDoc.getTexts().values()
                    .stream()
                    .collect(Collectors.toMap(i -> internalTextDoc.getLanguage(), text -> text));

            final List<TextDocTextContainer> containers = langTexts.entrySet().stream()
                    .map(entry -> {
                        final TextDomainObject textDO = entry.getValue();
                        return TextDocTextContainer.of(internalTextDoc.getRef(), textDO.getNo(), textDO);
                    })
                    .collect(Collectors.toList());


            for (TextDocTextContainer container : containers) {
                documentSaver.saveText(container, user);
            }
        } finally {
            invalidateDocument(textDocument.getId());
        }
    }

    @Override
    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        UserDomainObject user = Imcms.getUser();

        Language language = user == null
                ? languageService.getDefaultLanguage()
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

    @Override
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
    public <T extends DocumentDomainObject> T getVersionedDocument(int docId, String langCode, ServletRequest request) {
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
        Set<CategoryDomainObject> categories = getCategoryMapper().getCategories(document.getCategories());
        categories.stream()
                .filter(category -> !category.getType().isInherited())
                .forEach(category -> document.removeCategoryId(category.getId()));
    }

    @Override
    public Map<Language, DocumentCommonContent> getCommonContents(int docId, int versionNo) {
        return documentContentMapper.getCommonContents(docId, versionNo);
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
