package imcode.server.document.index.solr

import com.imcode._
//import java.text.DateFormat
import scala.collection.JavaConverters._
import imcode.server.document.DocumentDomainObject
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings

import java.util.Date
import imcode.util.DateConstants
import org.apache.solr.common.SolrInputDocument
import java.text.SimpleDateFormat
import com.imcode.Log4jLoggerSupport
import scala.reflect.BeanProperty
import com.imcode.imcms.mapping.{CategoryMapper, DocumentMapper}
import imcode.server.document.index.DocumentIndex

class DocumentIndexer(
  @BeanProperty var documentMapper: DocumentMapper,
  @BeanProperty var categoryMapper: CategoryMapper,
  @BeanProperty var contentIndexer: DocumentContentIndexer
  ) extends Log4jLoggerSupport {

  def this() = this(null, null, null)

  /**
   * Creates index document (SolrInputDocument) from doc.
   *
   * @return solr doc to be added into index.
   */
  def index(doc: DocumentDomainObject): SolrInputDocument = new SolrInputDocument |>> { indexDoc =>
    val documentId = doc.getId

    indexDoc.addField(DocumentIndex.FIELD__META_ID, documentId.toString)
    // ??? WHY INDEX ???
    indexDoc.addField(DocumentIndex.FIELD__META_ID_LEXICOGRAPHIC, documentId.toString)

    doc.getI18nMeta |> { l =>
      val headline = l.getHeadline
      val menuText = l.getMenuText

      indexDoc.addField(DocumentIndex.FIELD__META_HEADLINE, headline)
      indexDoc.addField(DocumentIndex.FIELD__META_HEADLINE_KEYWORD, headline)
      indexDoc.addField(DocumentIndex.FIELD__META_TEXT, menuText)
    }

    indexDoc.addField(DocumentIndex.FIELD__DOC_TYPE_ID, doc.getDocumentTypeId.toString)
    indexDoc.addField(DocumentIndex.FIELD__CREATOR_ID, doc.getCreatorId.toString)

    for (publisherId <- Option(doc.getPublisherId)) {
        indexDoc.addField(DocumentIndex.FIELD__PUBLISHER_ID, publisherId.toString)
    }

    addDateFieldToIndexDocument(documentId, indexDoc, DocumentIndex.FIELD__CREATED_DATETIME, doc.getCreatedDatetime)
    addDateFieldToIndexDocument(documentId, indexDoc, DocumentIndex.FIELD__MODIFIED_DATETIME, doc.getModifiedDatetime)
    addDateFieldToIndexDocument(documentId, indexDoc, DocumentIndex.FIELD__ACTIVATED_DATETIME, doc.getPublicationStartDatetime)
    addDateFieldToIndexDocument(documentId, indexDoc, DocumentIndex.FIELD__PUBLICATION_START_DATETIME, doc.getPublicationStartDatetime)
    addDateFieldToIndexDocument(documentId, indexDoc, DocumentIndex.FIELD__PUBLICATION_END_DATETIME, doc.getPublicationEndDatetime)
    addDateFieldToIndexDocument(documentId, indexDoc, DocumentIndex.FIELD__ARCHIVED_DATETIME, doc.getArchivedDatetime)

    indexDoc.addField(DocumentIndex.FIELD__STATUS, doc.getPublicationStatus.toString)

    for (category <- categoryMapper.getCategories(doc.getCategoryIds).asScala) {
      indexDoc.addField(DocumentIndex.FIELD__CATEGORY, category.getName)
      indexDoc.addField(DocumentIndex.FIELD__CATEGORY_ID, category.getId.toString)

      val categoryType = category.getType
      indexDoc.addField(DocumentIndex.FIELD__CATEGORY_TYPE, categoryType.getName)
      indexDoc.addField(DocumentIndex.FIELD__CATEGORY_TYPE_ID, categoryType.getId.toString)
    }

    for (documentKeyword <- doc.getKeywords.asScala) {
        indexDoc.addField(DocumentIndex.FIELD__KEYWORD, documentKeyword)
    }


    documentMapper.getDocumentMenuPairsContainingDocument(doc).map(p => p.getDocument.getId -> p.getMenuIndex) |> {
      parentDocumentAndMenuIds =>

        for ((parentId, menuId) <- parentDocumentAndMenuIds) {
            indexDoc.addField(DocumentIndex.FIELD__PARENT_ID, parentId.toString)
            indexDoc.addField(DocumentIndex.FIELD__PARENT_MENU_ID, parentId + "_" + menuId)
        }

        // WHY INDEX ???
        indexDoc.addField(DocumentIndex.FIELD__HAS_PARENTS, parentDocumentAndMenuIds.nonEmpty.toString)
    }

    for (alias <- Option(doc.getAlias)) {
      indexDoc.addField(DocumentIndex.FIELD__ALIAS, alias)
    }

    // HOW TO INDEX???
    // remove not needed???
//    for ((name, value) <- doc.getProperties.asScala) {
//        indexDoc.addField(name, value)
//    }

    for ((key, value) <- doc.getProperties.asScala) {
      indexDoc.addField(DocumentIndex.FIELD__PROPERTY_PREFIX + key, value.toString)
    }


    // ??? WHY INDEX ???
    val roleIdMappings: RoleIdToDocumentPermissionSetTypeMappings = doc.getRoleIdsMappedToDocumentPermissionSetTypes
    for (mapping <- roleIdMappings.getMappings) {
      indexDoc.addField(DocumentIndex.FIELD__ROLE_ID, mapping.getRoleId.intValue.toString)
    }

    try {
      contentIndexer.index(doc, indexDoc)
    } catch {
      case re => logger.error("Error indexing doc-type-specific data of doc " + documentId, re)
    }
  }

  // todo: refactor
  private def addDateFieldToIndexDocument(documentId: Int, indexDocument: SolrInputDocument, fieldName: String,
                                          date: Date) {
    if (null != date) {
      try {
        indexDocument.addField(fieldName, date)
        return
      } catch {
        case re =>
          val dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING)
          logger.warn("Failed to index datetime '" + dateFormat.format(date) + "' in field '" + fieldName + "' of doc " + documentId, re)
      }
    }
//        indexDocument.addField(fieldName, "")
  }

//    static Field unStoredKeyword(String fieldName, String fieldValue) {
//        return new Field(fieldName, fieldValue.toLowerCase(), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO)
//    }
//
//    private static Field unStoredKeyword(String fieldName, Date fieldValue) {
//        return new Field(fieldName, DateTools.dateToString(fieldValue, DateTools.Resolution.MINUTE), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO)
//    }
}