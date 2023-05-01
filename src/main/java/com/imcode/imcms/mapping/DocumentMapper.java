package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.controller.exception.NoPermissionInternalException;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.exception.DocumentSaveException;
import com.imcode.imcms.model.Language;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import lombok.Data;
import org.apache.commons.lang.math.IntRange;

import javax.servlet.ServletRequest;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"WeakerAccess"})
public interface DocumentMapper extends DocumentGetter {

    /**
     * @param documentId document id.
     * @return version info for a given document or null if document does not exist.
     */
    DocumentVersionInfo getDocumentVersionInfo(int documentId);

    /**
     * Creates new Document which inherits parent doc's meta excluding keywords and properties.
     * <p>
     * Doc's CommonContentJPA and content (texts, images, urls, files, etc) are not inherited.
     */
    DocumentDomainObject createDocumentOfTypeFromParent(
            int documentTypeId,
            DocumentDomainObject parentDoc,
            UserDomainObject user);

    DocumentReference getDocumentReference(DocumentDomainObject document);

    /**
     * Saves doc as new.
     *
     * @return saved document.
     * @see #createDocumentOfTypeFromParent(int, DocumentDomainObject, UserDomainObject)
     * @see DocumentDomainObject#fromDocumentTypeId(int)
     */
    <T extends DocumentDomainObject> T saveNewDocument(T doc, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException;

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
    <T extends DocumentDomainObject> T saveNewDocument(T doc, Map<Language, DocumentCommonContent> appearances, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException;

    /**
     * Updates existing document.
     */
    int saveDocument(DocumentDomainObject doc, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException;

    /**
     * Updates existing document.
     *
     * @since 6.0
     */
    int saveDocument(DocumentDomainObject doc, Map<Language, DocumentCommonContent> commonContents, UserDomainObject user)
            throws DocumentSaveException, NoPermissionToAddDocumentToMenuException, NoPermissionToEditDocumentException;

    /**
     * Creates next document version.
     * <p>
     * Saves document's working version copy as next document version.
     *
     * @return new document version.
     * @since 6.0
     */
    DocumentVersion makeDocumentVersion(int docId, UserDomainObject user);

    /**
     * Changes doc's default version.
     *
     * @since 6.0
     */
    void changeDocumentDefaultVersion(int docId, int newDocDefaultVersionNo, UserDomainObject publisher);

    void invalidateDocument(DocumentDomainObject document);

    void invalidateDocument(int docId);

    DocumentIndex getDocumentIndex();

    void setDocumentIndex(DocumentIndex documentIndex);

    void deleteDocument(int docId);

    void deleteDocument(DocumentDomainObject document);

    Map<Integer, String> getAllDocumentTypeIdsAndNamesInUsersLanguage(UserDomainObject user);

    TextDocumentMenuIndexPair[] getDocumentMenuPairsContainingDocument(DocumentDomainObject document);

    Iterator<DocumentDomainObject> getDocumentsIterator(IntRange idRange);

    List<Integer> getAllDocumentIds();

    List<String> getAllDocumentAlias();

    /**
     * @param documentIdentity document id or alias.
     * @return latest version of a document or null if document can not be found.
     */
    DocumentDomainObject getDocument(String documentIdentity);

    /**
     * @param documentIdentity document id or alias
     * @return document id or null if there is no document with such identity.
     */
    Integer toDocumentId(String documentIdentity);

    int getLowestDocumentId();

    int getHighestDocumentId();

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
    <T extends DocumentDomainObject> T copyDocument(T doc, UserDomainObject user)
            throws NoPermissionToAddDocumentToMenuException, DocumentSaveException;

    List<DocumentDomainObject> getDocumentsWithPermissionsForRole(RoleDomainObject role);

    /**
     * @param docId document id
     * @return default document in default language.
     * @since 6.0
     */
    <T extends DocumentDomainObject> T getDefaultDocument(int docId);

    /**
     * @param docId document id
     * @return working document in default language.
     * @since 6.0
     */
    <T extends DocumentDomainObject> T getWorkingDocument(int docId);

    /**
     * Returns document.
     * <p>
     * Delegates call to a callback associated with a user.
     * If there is no callback then a default document is returned.
     *
     * @param docId document id.
     */
    <T extends DocumentDomainObject> T getDocument(int docId);

    /**
     * @param docId    document id
     * @param language language
     * @return working document
     * @since 6.0
     */
    <T extends DocumentDomainObject> T getWorkingDocument(int docId, Language language);

    /**
     * @param docId    document id
     * @param language language
     * @return default document
     * @since 6.0
     */
    <T extends DocumentDomainObject> T getDefaultDocument(int docId, Language language);

    /**
     * @param docId        document id
     * @param languageCode language code
     * @return default document
     * @since 6.0
     */
    <T extends DocumentDomainObject> T getDefaultDocument(int docId, String languageCode);

    <T extends DocumentDomainObject> T getCustomDocument(DocRef docRef);

    CategoryMapper getCategoryMapper();

    Database getDatabase();

    /**
     * Saves text and non-saved enclosing content loop the text may refer.
     * Updates doc's last modified datetime.
     * <p>
     * Non saved enclosing content loop might be added to the doc by ContentLoopTag2.
     *
     * @param container - text being saved
     * @throws IllegalStateException if text 'docNo', 'versionNo', 'no' or 'language' is not set
     */
    void saveTextDocText(TextDocTextContainer container, UserDomainObject user)
            throws NoPermissionInternalException;

    void saveTextsDocText(TextDocument textDocument, UserDomainObject user)
            throws NoPermissionInternalException;

    /**
     * @param documentIds id's of documents
     * @return default documents.
     */
    List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds);

    /**
     * Get document version by document id or alias, parameters in request and language code
     *
     * @param documentIdentity document id or alias
     * @return document with needed version and language or null
     * @since 6.0
     */
    <T extends DocumentDomainObject> T getVersionedDocument(String documentIdentity,
                                                            String langCode,
                                                            ServletRequest request);

    Map<Language, DocumentCommonContent> getCommonContents(int docId, int versionNo);

    /**
     * Document save options.
     * Currently applies to text documents only.
     */
    enum SaveOpts {
        CopyDocCommonContentIntoTextFields
    }

    @Data
    class TextDocumentMenuIndexPair {

        private final TextDocumentDomainObject document;
        private final int menuIndex;

        public TextDocumentMenuIndexPair(TextDocumentDomainObject document, int menuIndex) {
            this.document = document;
            this.menuIndex = menuIndex;
        }
    }
}
