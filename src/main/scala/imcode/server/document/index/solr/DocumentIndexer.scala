package imcode.server.document.index.solr

import com.imcode._
import scala.collection.JavaConverters._
import imcode.server.document.DocumentDomainObject
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings
import org.apache.solr.common.SolrInputDocument
import com.imcode.Log4jLoggerSupport
import scala.reflect.BeanProperty
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import imcode.server.document.index.DocumentIndex

/**
 * Lucene & SOLr indexing differences:
 * - properties - key - value not possible ???
 * - text* dynamic field - add underscore | conflict with text | RE format ???
 * - virtual field - phase
 */
// todo: ??? Truncate date fields to minute ???, check for null or insert default ("" - empty string) ???
// todo: ??? null handling in SOLR ???
class DocumentIndexer(
  @BeanProperty var documentMapper: DocumentMapper,
  @BeanProperty var categoryMapper: CategoryMapper,
  @BeanProperty var contentIndexer: DocumentContentIndexer
  ) extends Log4jLoggerSupport {

  def this() = this(null, null, null)

  /**
   * Creates SolrInputDocument based on provided document.
   *
   * @return SolrInputDocument
   */
  def index(doc: DocumentDomainObject): SolrInputDocument = new SolrInputDocument |>> { indexDoc =>
    def addFieldIfNotNull(name: String, value: AnyRef) = if (value != null) indexDoc.addField(name, value)

    val documentId = doc.getId

    indexDoc.addField(DocumentIndex.FIELD__META_ID, documentId)
    indexDoc.addField(DocumentIndex.FIELD__META_ID_LEXICOGRAPHIC, documentId)

    doc.getI18nMeta |> { l =>
      val headline = l.getHeadline
      val menuText = l.getMenuText

      indexDoc.addField(DocumentIndex.FIELD__META_HEADLINE, headline)
      indexDoc.addField(DocumentIndex.FIELD__META_HEADLINE_KEYWORD, headline)
      indexDoc.addField(DocumentIndex.FIELD__META_TEXT, menuText)
    }

    indexDoc.addField(DocumentIndex.FIELD__DOC_TYPE_ID, doc.getDocumentTypeId)
    indexDoc.addField(DocumentIndex.FIELD__CREATOR_ID, doc.getCreatorId)

    addFieldIfNotNull(DocumentIndex.FIELD__PUBLISHER_ID, doc.getPublisherId)

    // todo: ??? or "" ???
    indexDoc.addField(DocumentIndex.FIELD__CREATED_DATETIME, doc.getCreatedDatetime)
    indexDoc.addField(DocumentIndex.FIELD__MODIFIED_DATETIME, doc.getModifiedDatetime)
    indexDoc.addField(DocumentIndex.FIELD__ACTIVATED_DATETIME, doc.getPublicationStartDatetime)
    indexDoc.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, doc.getPublicationStartDatetime)
    indexDoc.addField(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, doc.getPublicationEndDatetime)
    indexDoc.addField(DocumentIndex.FIELD__ARCHIVED_DATETIME, doc.getArchivedDatetime)

    indexDoc.addField(DocumentIndex.FIELD__STATUS, doc.getPublicationStatus.asInt())

    for (category <- categoryMapper.getCategories(doc.getCategoryIds).asScala) {
      indexDoc.addField(DocumentIndex.FIELD__CATEGORY, category.getName)
      indexDoc.addField(DocumentIndex.FIELD__CATEGORY_ID, category.getId)

      val categoryType = category.getType
      indexDoc.addField(DocumentIndex.FIELD__CATEGORY_TYPE, categoryType.getName)
      indexDoc.addField(DocumentIndex.FIELD__CATEGORY_TYPE_ID, categoryType.getId)
    }

    for (documentKeyword <- doc.getKeywords.asScala) {
        indexDoc.addField(DocumentIndex.FIELD__KEYWORD, documentKeyword)
    }

    documentMapper.getDocumentMenuPairsContainingDocument(doc).map(p => p.getDocument.getId -> p.getMenuIndex) |> {
      parentDocumentAndMenuIds =>

        for ((parentId, menuId) <- parentDocumentAndMenuIds) {
          indexDoc.addField(DocumentIndex.FIELD__PARENT_ID, parentId)
          indexDoc.addField(DocumentIndex.FIELD__PARENT_MENU_ID, parentId + "_" + menuId)
        }

        indexDoc.addField(DocumentIndex.FIELD__HAS_PARENTS, parentDocumentAndMenuIds.nonEmpty)
    }

    addFieldIfNotNull(DocumentIndex.FIELD__ALIAS, doc.getAlias)

    for ((key, value) <- doc.getProperties.asScala) {
      indexDoc.addField(DocumentIndex.FIELD__PROPERTY_PREFIX + key, value)
    }

    val roleIdMappings: RoleIdToDocumentPermissionSetTypeMappings = doc.getRoleIdsMappedToDocumentPermissionSetTypes
    for (mapping <- roleIdMappings.getMappings) {
      indexDoc.addField(DocumentIndex.FIELD__ROLE_ID, mapping.getRoleId.intValue)
    }

    try {
      contentIndexer.index(doc, indexDoc)
    } catch {
      case e =>
        logger.error("Failed to index doc's content. Doc id: %d, language: %s, type: %s".
          format(documentId, doc.getLanguage, doc.getDocumentType), e
        )
    }
  }
}