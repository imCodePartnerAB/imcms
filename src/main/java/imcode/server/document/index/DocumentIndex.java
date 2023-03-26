package imcode.server.document.index;

import com.imcode.imcms.api.SearchResult;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.List;

// todo: pagination should return stored docs fields instead of real documents
public interface DocumentIndex {

	String FIELD__ID = "id";
	String FIELD__TIMESTAMP = "timestamp";
	String FIELD__LANGUAGE_CODE = "language";
	String FIELD__ENABLED_LANGUAGE_CODE = "enabled_language";
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
	String FIELD__META_ALIAS = "meta_alias";
	String FIELD__META_ALIAS_KEYWORD = "meta_alias_keyword";
	String FIELD__VERSION_NO = "version_no";
	String FIELD__META_TEXT = "meta_text";
	String FIELD__MODIFIED_DATETIME = "modified_datetime";
	String FIELD__PARENT_ID = "parent_id";
	String FIELD__PARENT_MENU_ID = "parent_menu_id";
	String FIELD__HAS_PARENTS = "has_parents";
	String FIELD__PUBLICATION_END_DATETIME = "publication_end_datetime";
	String FIELD__PUBLICATION_START_DATETIME = "publication_start_datetime";
	String FIELD__ROLE_ID = "role_id";
	String FIELD__VISIBLE = "visible";
	String FIELD__LINKABLE_UNAUTHORIZED = "linkable_unauthorized";
	String FIELD__LINKABLE_OTHER = "linkable_other";
	String FIELD__STATUS = "status";
	String FIELD__URL = "url";
	String FIELD__DISABLED_LANGUAGE_SHOW_MODE = "disabled_language_show_mode";
	String FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED = "default_language_alias_enabled";

	String FIELD__PARENTS_COUNT = "parents_count";
	String FIELD__CHILDREN_COUNT = "children_count";

	/**
	 * This field is not stored in an index and can not be used in direct SOLr queries.
	 * Queries containing this field must be rewritten using combination of {@link #FIELD__STATUS} and lifecycle dates
	 * intervals before submission to SOLr.
	 *
	 * @see imcode.server.PhaseQueryFixingDocumentIndex
     */
    String FIELD__PHASE = "phase";
    String FIELD__MIME_TYPE = "mime_type";
    String FIELD__CREATOR_ID = "creator_id";
    String FIELD__PUBLISHER_ID = "publisher_id";
    String FIELD__CREATOR_NAME = "creator_name";
    String FIELD__MODIFIER_NAME = "modifier_name";
	String FIELD__PUBLISHER_NAME = "publisher_name";
	String FIELD__PROPERTY_PREFIX = "property.";
	String FIELD__TEMPLATE = "template";
	// Applies to text document only
	String FIELD__CHILD_ID = "child_id";
	// Applies to text document only
	String FIELD__HAS_CHILDREN = "has_children";

	String FIELD__SEARCH_ENABLED = "search_enabled";

	//TODO: Add configuration in solrconfig file for ignore case sensitivity! and remove this field!
	String FIELD_META_HEADLINE = "meta_headline_l";
	String FIELD_META_ALIAS = "meta_alias_l";

	String FIELD__IN_WASTE_BASKET = "in_waste_basket";

	/**
	 * Searches documents.
	 */
	@Deprecated
	List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException;

	@Deprecated
	SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException;

    void rebuild() throws IndexException;

    void indexDocument(DocumentDomainObject document) throws IndexException;

    void removeDocument(DocumentDomainObject document) throws IndexException;

	void updateDocumentVersion(DocumentDomainObject document) throws IndexException;

    /**
     * @since 6.0
     */
    IndexSearchResult search(SolrQuery query) throws IndexException;

    /**
     * Adds default document to index.
     * @since 6.0
     */
    void indexDocument(int docId) throws IndexException;

    /**
     * Removes default document from index.
     * @since 6.0
     */
    void removeDocument(int docId) throws IndexException;

	void updateDocumentVersion(int docId) throws IndexException;

    /**
     * Returns underlying service.
     *
     * @return underlying service
     * @since 6.0
     */
    DocumentIndexService getService();

    boolean isUpdateDone();
}
