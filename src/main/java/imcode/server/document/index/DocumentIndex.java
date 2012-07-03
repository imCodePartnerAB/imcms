package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.List;

public interface DocumentIndex {

    String FIELD__DOC_TYPE_ID = "doc_type_id";
    String FIELD__IMAGE_LINK_URL = "image_link_url";
    String FIELD__NONSTRIPPED_TEXT = "nonstripped_text";
    String FIELD__TEXT = "text";
    String FIELD__KEYWORD = "keyword";
    String FIELD__ACTIVATED_DATETIME = "activated_datetime";
    String FIELD__ARCHIVED_DATETIME = "archived_datetime";
    String FIELD__CATEGORY = "category";
    String FIELD__CATEGORY_ID = "category_id";
    String FIELD__CATEGORY_TYPE = "category_type";
    String FIELD__CATEGORY_TYPE_ID = "category_type_id";
    String FIELD__CREATED_DATETIME = "created_datetime";
    String FIELD__META_HEADLINE = "meta_headline";
    String FIELD__META_HEADLINE_KEYWORD = "meta_headline_keyword";
    String FIELD__META_ID = "meta_id";
    String FIELD__META_ID_LEXICOGRAPHIC = "meta_id_lexicographic";
    String FIELD__META_TEXT = "meta_text";
    String FIELD__MODIFIED_DATETIME = "modified_datetime";
    String FIELD__PARENT_ID = "parent_id";
    String FIELD__PARENT_MENU_ID = "parent_menu_id";
    String FIELD__HAS_PARENTS = "has_parents";
    String FIELD__PUBLICATION_END_DATETIME = "publication_end_datetime";
    String FIELD__PUBLICATION_START_DATETIME = "publication_start_datetime";
    String FIELD__ROLE_ID = "role_id";
    String FIELD__STATUS = "status";
    String FIELD__PHASE = "phase" ;
    String FIELD__MIME_TYPE = "mime_type";
    String FIELD__CREATOR_ID = "creator_id";
    String FIELD__PUBLISHER_ID = "publisher_id";
    String FIELD__PROPERTY_PREFIX = "property.";
    String FIELD__ALIAS = "alias";
    String FIELD__TEMPLATE = "template";
    String FIELD__CHILD_ID = "child_id";
    String FIELD__HAS_CHILDREN = "has_children";

    List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException;

    void rebuild() throws IndexException;

    /**
     * Adds default document(s) to index.
     *
     * @param metaId
     * @throws IndexException
     * @since 6.0
     */
    void indexDocuments(int metaId) throws IndexException;

    /**
     * Removes default document(s) from index.
     *
     * @param metaId
     * @throws IndexException
     * @since 6.0
     */
    void removeDocuments(int metaId) throws IndexException;

    @Deprecated
    void indexDocument(DocumentDomainObject document) throws IndexException;

    @Deprecated
    void removeDocument(DocumentDomainObject document) throws IndexException;
}