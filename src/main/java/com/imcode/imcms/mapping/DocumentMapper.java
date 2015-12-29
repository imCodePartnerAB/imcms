package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.mapping.container.*;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

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
		this.imcmsServices = services;
		this.database = database;

		Config config = services.getConfig();
		int documentCacheMaxSize = config.getDocumentCacheMaxSize();

		documentLoader = services.getManagedBean(DocumentLoader.class);
		documentLoaderCachingProxy = new DocumentLoaderCachingProxy(documentVersionMapper, documentLoader, services.getDocumentLanguages(), documentCacheMaxSize);

		nativeQueries = services.getManagedBean(NativeQueries.class);
		categoryMapper = services.getManagedBean(CategoryMapper.class);

		documentSaver = services.getManagedBean(DocumentSaver.class);
		documentSaver.setDocumentMapper(this);

		documentContentMapper = services.getManagedBean(DocumentContentMapper.class);
	}

	static void deleteFileDocumentFilesAccordingToFileFilter(FileFilter fileFilter) {
		File filePath = Imcms.getServices().getConfig().getFilePath();
		File[] filesToDelete = filePath.listFiles(fileFilter);
		for (File aFilesToDelete : filesToDelete) {
			aFilesToDelete.delete();
		}
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
	 *
	 * @param documentTypeId
	 * @param parentDoc
	 * @param user
	 * @return
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
	 *
	 * @param newTextDocument
	 * @param user
	 * @param parent
	 */
	void setTemplateForNewTextDocument(TextDocumentDomainObject newTextDocument, UserDomainObject user,
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

	void makeDocumentLookNew(DocumentDomainObject document, UserDomainObject user) {
		makeDocumentLookNew(document.getMeta(), user);
	}

	void makeDocumentLookNew(DocumentMeta documentMeta, UserDomainObject user) {
		Date now = new Date();

		documentMeta.setCreatorId(user.getId());
		setCreatedAndModifiedDatetimes(documentMeta, now);
		documentMeta.setPublicationStartDatetime(now);
		documentMeta.setArchivedDatetime(null);
		documentMeta.setPublicationEndDatetime(null);
		documentMeta.setPublicationStatus(Document.PublicationStatus.NEW);
	}

	public DocumentReference getDocumentReference(DocumentDomainObject document) {
		return getDocumentReference(document.getId());
	}

	public DocumentReference getDocumentReference(int childId) {
		return new GetterDocumentReference(childId, this);
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	/**
	 * Saves doc as new.
	 * <p>
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
	public <T extends DocumentDomainObject> T saveNewDocument(T doc, Map<DocumentLanguage, DocumentCommonContent> appearances,
															  EnumSet<SaveOpts> saveOpts,
															  UserDomainObject user)
			throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

		if (appearances.isEmpty()) {
			throw new IllegalArgumentException("Unable to save new document. i18nMetas must not be empty.");
		}

		T docClone = (T) doc.clone();

		int docId = documentSaver.saveNewDocument(docClone, appearances, saveOpts, user);

		invalidateDocument(docId);

		return (T) getWorkingDocument(docId, docClone.getLanguage());
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
	 * @param doc
	 * @param appearances
	 * @param user
	 * @param <T>
	 * @return
	 * @throws DocumentSaveException
	 * @throws imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException
	 * @since 6.0
	 */
	public <T extends DocumentDomainObject> T saveNewDocument(T doc, Map<DocumentLanguage, DocumentCommonContent> appearances, UserDomainObject user)
			throws DocumentSaveException, NoPermissionToAddDocumentToMenuException {

		return saveNewDocument(doc, appearances, EnumSet.noneOf(SaveOpts.class), user);
	}

	/**
	 * Updates existing document.
	 */
	public void saveDocument(DocumentDomainObject doc, UserDomainObject user)
			throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {
		saveDocument(doc, Collections.singletonMap(doc.getLanguage(), doc.getCommonContent()), user);
	}

	/**
	 * Updates existing document.
	 *
	 * @since 6.0
	 */
	public void saveDocument(DocumentDomainObject doc, Map<DocumentLanguage, DocumentCommonContent> commonContents, UserDomainObject user)
			throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

		DocumentDomainObject docClone = doc.clone();
		DocumentDomainObject oldDoc = getCustomDocument(doc.getRef());

		try {
			documentSaver.updateDocument(docClone, commonContents, oldDoc, user);
		} finally {
			invalidateDocument(doc.getId());
		}
	}

	/**
	 * Saves document menu.
	 *
	 * @since 6.0
	 */
	public void saveTextDocMenu(TextDocMenuContainer container, UserDomainObject user)
			throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException {

		try {
			documentSaver.saveMenu(container, user);
		} finally {
			invalidateDocument(container.getDocId());
		}
	}

	/**
	 * Creates next document version.
	 * <p>
	 * Saves document's working version copy as next document version.
	 *
	 * @return new document version.
	 * @since 6.0
	 */
	public DocumentVersion makeDocumentVersion(int docId, UserDomainObject user)
			throws DocumentSaveException {

		List<DocumentDomainObject> docs = new LinkedList<>();

		for (DocumentLanguage language : imcmsServices.getDocumentLanguages().getAll()) {
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
		if (document instanceof TextDocumentDomainObject) {
			TextDocumentDomainObject textDoc = (TextDocumentDomainObject) document;

			imcmsServices.getImageCacheMapper().deleteDocumentImagesCache(textDoc.getImages());
		}

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
	 * @throws imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException
	 * @throws DocumentSaveException
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
	public int copyDocumentsWithCommonMetaAndVersion(VersionRef versionRef, UserDomainObject user)
			throws NoPermissionToAddDocumentToMenuException, DocumentSaveException {

		// todo: put into resource file.
		String copyHeadlineSuffix = "(Copy/Kopia)";

		DocumentMeta documentMeta = documentLoader.loadMeta(versionRef.getDocId());
		Map<DocumentLanguage, DocumentCommonContent> dccMap = documentContentMapper.getCommonContents(versionRef.getDocId());
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
	@SuppressWarnings("unchecked")
	public <T extends DocumentDomainObject> T getDocument(int docId) {
		UserDomainObject user = Imcms.getUser();
		DocGetterCallback callback = user == null ? null : user.getDocGetterCallback();

		return callback == null
				? (T) getDefaultDocument(docId)
				: (T) callback.getDoc(docId, this);
	}

	/**
	 * @param docId document id
	 * @param language language
	 * @return working document
	 * @since 6.0
	 */
	public <T extends DocumentDomainObject> T getWorkingDocument(int docId, DocumentLanguage language) {
		return getWorkingDocument(docId, language.getCode());
	}

	/**
	 * @param docId document id
	 * @param docLanguageCode language code
	 * @return working document
	 * @since 6.0
	 */
	public <T extends DocumentDomainObject> T getWorkingDocument(int docId, String docLanguageCode) {
		return documentLoaderCachingProxy.getWorkingDoc(docId, docLanguageCode);
	}

	/**
	 * @param docId document id
	 * @param language language
	 * @return default document
	 * @since 6.0
	 */
	public <T extends DocumentDomainObject> T getDefaultDocument(int docId, DocumentLanguage language) {
		return documentLoaderCachingProxy.getDefaultDoc(docId, language.getCode());
	}


	/**
	 * @param docId document id
	 * @param languageCode language code
	 * @return default document
	 * @since 6.0
	 */
	public <T extends DocumentDomainObject> T getDefaultDocument(int docId, String languageCode) {
		return documentLoaderCachingProxy.getDefaultDoc(docId, languageCode);
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
		return documentLoaderCachingProxy.getCustomDoc(docRef);
	}


	public CategoryMapper getCategoryMapper() {
		return categoryMapper;
	}

	public void setCategoryMapper(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}

	public Database getDatabase() {
		return database;
	}

	public ImcmsServices getImcmsServices() {
		return imcmsServices;
	}

	/**
	 * @param documentMeta
	 * @param date
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
	 * Saves images and non-saved enclosing content loop if any.
	 * <p>
	 * Non saved content loop might be added to the document by ContentLoopTag2.
	 *
	 * @see com.imcode.imcms.servlet.tags.LoopTag
	 * @since 6.0
	 */
	public synchronized void saveTextDocImages(TextDocImagesContainer container, UserDomainObject user)
			throws NoPermissionInternalException, DocumentSaveException {
		try {
			documentSaver.saveImages(container, user);
		} finally {
			invalidateDocument(container.getDocId());
		}
	}

	/**
	 * Saves images and non-saved enclosing content loop if any.
	 * <p>
	 * Non saved content loop might be added to the document by ContentLoopTag2.
	 *
	 * @see com.imcode.imcms.servlet.tags.LoopTag
	 * @since 6.0
	 */
	public synchronized void saveTextDocImage(TextDocImageContainer container, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException {
		try {
			documentSaver.saveImage(container, user);
		} finally {
			invalidateDocument(container.getDocRef().getId());
		}
	}

	/**
	 * @param documentIds id's of documents
	 * @return default documents.
	 */
	public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
		UserDomainObject user = Imcms.getUser();
		DocGetterCallback callback = user == null ? null : user.getDocGetterCallback();
		DocumentLanguage language = callback != null
				? callback.getLanguage()
				: imcmsServices.getDocumentLanguages().getDefault();

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
		categories.stream().filter(category -> !category.getType().isInherited()).forEach(category -> document.removeCategoryId(category.getId()));
	}

	public Map<DocumentLanguage, DocumentCommonContent> getCommonContents(int docId) {
		return documentContentMapper.getCommonContents(docId);
	}

	public String[][] getAllMimeTypesWithDescriptions(UserDomainObject user) {
		List<String[]> result = nativeQueries.getAllMimeTypesWithDescriptions(user.getLanguageIso639_2());

		String[][] mimeTypes = new String[result.size()][];

		for (int i = 0; i < mimeTypes.length; i++) {
			mimeTypes[i] = result.get(i);
		}

		return mimeTypes;
	}

	public String[] getAllMimeTypes() {
		List<String> allMimeTypes = nativeQueries.getAllMimeTypes();
		return allMimeTypes.toArray(new String[allMimeTypes.size()]);
	}

	public List<Integer> getParentDocsIds(DocumentDomainObject doc) {
		return menuRepository.getParentDocsIds(doc.getId(), doc.getVersionNo());
	}

	public IntRange getDocumentIdRange() {
		Integer[] minMaxPair = documentSaver.getDocRepository().getMinMaxDocumentIds();

		return minMaxPair[0] == null ? null : new IntRange(minMaxPair[0], minMaxPair[1]);
	}

	public <T extends DocumentDomainObject> T getCustomDocumentInDefaultLanguage(DocRef docRef) {
		return getCustomDocument(
				DocRef.buillder(docRef)
						.languageCode(imcmsServices.getDocumentLanguages().getDefault().getCode())
						.build()
		);
	}

	@SuppressWarnings("unchecked")
	public <T extends DocumentDomainObject> List<T> findDocumentsByHeadline(String term) {
		List<Integer> ids = getAllDocumentIds();
		List<T> result = new ArrayList<>();
		term = term.toLowerCase();
		for (Integer id : ids) {
			DocumentDomainObject document = getDocument(id);
			if (term.isEmpty() || document.getHeadline().toLowerCase().contains(term))
				result.add((T) document);
		}
		return result;
	}

	/////////	unused candidates to delete

	public DocumentLoaderCachingProxy getDocumentLoaderCachingProxy() {
		return documentLoaderCachingProxy;
	}

	/**
	 * Saves text and non-saved enclosing content loop the text may refer.
	 * Updates doc's last modified datetime.
	 * <p>
	 * Non saved enclosing content loop might be added to the doc by ContentLoopTag2.
	 *
	 * @param container - texts being saved
	 * @throws IllegalStateException if text 'docNo', 'versionNo', 'no' or 'language' is not set
	 * @see com.imcode.imcms.servlet.tags.LoopTag
	 */
	public synchronized void saveTextDocTexts(TextDocTextsContainer container, UserDomainObject user)
			throws NoPermissionInternalException, DocumentSaveException {
		try {
			documentSaver.saveTexts(container, user);
		} finally {
			invalidateDocument(container.getDocId());
		}
	}

	@Deprecated
	void setCreatedAndModifiedDatetimes(DocumentDomainObject document, Date date) {
		setCreatedAndModifiedDatetimes(document.getMeta(), date);
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