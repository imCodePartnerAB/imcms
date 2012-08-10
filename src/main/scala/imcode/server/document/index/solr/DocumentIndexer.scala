package imcode.server.document.index.solr

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings
import _root_.imcode.server.document.index.DocumentIndex
import com.imcode._
import com.imcode.Log4jLoggerSupport
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrInputDocument
import scala.reflect.BeanProperty
import java.util.Date

/**
 *
 */
// todo: ??? Truncate date fields to minute ???
class DocumentIndexer(
  @BeanProperty var documentMapper: DocumentMapper,
  @BeanProperty var categoryMapper: CategoryMapper,
  @BeanProperty var contentIndexer: DocumentContentIndexer
  ) extends Log4jLoggerSupport {

  def this() = this(null, null, null)

  /**
   * Creates SolrInputDocument based on provided DocumentDomainObject.
   *
   * @return SolrInputDocument
   */
  def index(doc: DocumentDomainObject): SolrInputDocument = new SolrInputDocument |>> { indexDoc =>
    def addFieldIfNotNull(name: String, value: AnyRef): Unit = if (value != null) indexDoc.addField(name, value)

    val docId = doc.getId
    val languageCode = doc.getLanguage.getCode

    indexDoc.addField(DocumentIndex.FIELD__ID, "%d_%s".format(docId, languageCode))
    indexDoc.addField(DocumentIndex.FIELD__TIMESTAMP, new Date)
    indexDoc.addField(DocumentIndex.FIELD__META_ID, docId)
    indexDoc.addField(DocumentIndex.FIELD__LANGUAGE, languageCode)

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

    addFieldIfNotNull(DocumentIndex.FIELD__CREATED_DATETIME, doc.getCreatedDatetime)
    addFieldIfNotNull(DocumentIndex.FIELD__MODIFIED_DATETIME, doc.getModifiedDatetime)
    addFieldIfNotNull(DocumentIndex.FIELD__ACTIVATED_DATETIME, doc.getPublicationStartDatetime)
    addFieldIfNotNull(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, doc.getPublicationStartDatetime)
    addFieldIfNotNull(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, doc.getPublicationEndDatetime)
    addFieldIfNotNull(DocumentIndex.FIELD__ARCHIVED_DATETIME, doc.getArchivedDatetime)

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

    documentMapper.getParentDocumentAndMenuIdsForDocument(doc) |> { parentDocumentAndMenuIds =>
      for (Array(parentId, menuId) <- parentDocumentAndMenuIds.asScala) {
        indexDoc.addField(DocumentIndex.FIELD__PARENT_ID, parentId)
        indexDoc.addField(DocumentIndex.FIELD__PARENT_MENU_ID, parentId + "_" + menuId)
      }

      indexDoc.addField(DocumentIndex.FIELD__HAS_PARENTS, !parentDocumentAndMenuIds.isEmpty)
    }

    addFieldIfNotNull(DocumentIndex.FIELD__ALIAS, doc.getAlias)

    for ((key, value) <- doc.getProperties.asScala) {
      indexDoc.addField(DocumentIndex.FIELD__PROPERTY_PREFIX + key, value)
      // Legacy document property indexing support: property name as field name -> property value as field value.
      // See also schema.xml * field
      // indexDoc.addField(key, value)
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
            format(docId, doc.getLanguage, doc.getDocumentType), e)
    }
  }
}