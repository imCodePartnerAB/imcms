package com.imcode
package imcms
package admin.doc.projection.container

import com.vaadin.data.{Property, Container}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.data._

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.index.{DocumentIndex, DocumentStoredFields, IndexSearchResult}
import _root_.imcode.server.Imcms
import _root_.imcode.server.document.DocumentDomainObject

import java.util.{Date, Collections, Arrays}

import org.apache.solr.client.solrj.SolrQuery
import com.imcode.imcms.vaadin.component.{Theme, UndefinedSize, NoMargin, Spacing}
import com.imcode.imcms.api.Document

import scala.collection.JavaConverters._

// todo: Implement sorting by status - ???sort by solr query???
// todo: Implement Selection base on in-memory solr.
class IndexedDocsContainer(
  user: UserDomainObject,
  parentsRenderer: (DocumentStoredFields => Component) = Function.const(null),
  childrenRenderer: (DocumentStoredFields => Component) = Function.const(null)
)
extends Container
with ContainerWithTypedItemId[Index]
with ReadOnlyOrderedContainer
with Container.Sortable
with ContainerItemSetChangeNotifier
with ImcmsServicesSupport {

  private class Items(val searchResultOpt: Option[IndexSearchResult]) {
    val size: Int = if (searchResultOpt.isDefined) searchResultOpt.get.size() else 0
    val isEmpty: Boolean = searchResultOpt.isEmpty
    val nonEmpty: Boolean = searchResultOpt.nonEmpty
    val contains: (Int => Boolean) = if (searchResultOpt.isEmpty) Function.const(false) else searchResultOpt.get.contains
    val documentStoredFieldsList: JList[DocumentStoredFields] = if (searchResultOpt.isEmpty) Collections.emptyList[DocumentStoredFields] else searchResultOpt.get.documentStoredFieldsList()
  }

  @transient
  private var items: Items = new Items(None)

  def setQueryOpt(queryOpt: Option[SolrQuery]) {
    items = new Items(queryOpt.map(query => imcmsServices.getDocumentMapper.getDocumentIndex.search(query, user)))

    notifyItemSetChanged()
  }


  /**
   * Returns indexed documents id (inclusive) range.
   *
   * @return Some(min, max) or None if there are no indexed documents.
   */
  def docIdRange(): Option[(DocId, DocId)] = {
    // A range can also be queried from db instead of index:
    // imcmsServices.getDocumentMapper.getDocumentIdRange.asOption.map { idsRange =>
    //   (idsRange.getMinimumInteger: DocId, idsRange.getMaximumInteger: DocId)
    // }

    val query = new SolrQuery("*:*") |>> { q =>
      q.setGetFieldStatistics(DocumentIndex.FIELD__META_ID)
      q.setRows(0)
    }

    imcmsServices.getDocumentMapper.getDocumentIndex.getService.query(query).toOption.flatMap { queryResponse =>
      queryResponse.getFieldStatsInfo.get(DocumentIndex.FIELD__META_ID) match {
        case null => None
        case stats => Some(stats.getMin.toString.toInt, stats.getMax.toString.toInt)
      }
    }
  }

  override val getContainerPropertyIds: JCollection[_] = java.util.Arrays.asList(PropertyId.values() : _*)

  override def getType(propertyId: AnyRef): Class[_] = propertyId.asInstanceOf[PropertyId].getType

  override def getContainerProperty(itemId: AnyRef, propertyId: AnyRef): Property[_] = getItem(itemId).getItemProperty(propertyId)

  @transient
  override def size(): Int = items.size

  override def getItemIds(): JCollection[_] = new java.util.AbstractList[Index] {
    override def get(index: Int): Index = index
    override val size: Int = items.size
  }

  override def containsId(itemId: AnyRef): Boolean = itemId match {
    case id: Index => items.contains(id)
    case _ => false
  }

  override def isFirstId(itemId: AnyRef): Boolean = itemId match {
    case index: Index if items.nonEmpty => index == 0
    case _ => false
  }

  override def isLastId(itemId: AnyRef): Boolean = itemId match {
    case index: Index if items.nonEmpty => index == items.size - 1
    case _ => false
  }

  override def firstItemId: Index = if (items.isEmpty) null else 0

  override def lastItemId: Index = if (items.isEmpty) null else items.size - 1

  override def prevItemId(itemId: AnyRef): Index = itemId match {
    case id: Index if items.contains(id - 1) => id - 1
    case _ => null
  }

  override def nextItemId(itemId: AnyRef): Index = itemId match {
    case id: Index if items.contains(id + 1) => id + 1
    case _ => null
  }

  override def getItem(itemId: AnyRef): IndexedDocItem = itemId match {
    case id: Index if items.contains(id) => mkItem(id, items.documentStoredFieldsList.get(id))
    case _ => null
  }

  override def sort(propertyId: Array[AnyRef], ascending: Array[Boolean]) {
    items.searchResultOpt.foreach { searchResult =>
      val query = searchResult.solrQuery()
      query.getSorts.asScala.foreach(query.removeSort)

      for ((id: PropertyId, asc) <- propertyId.zip(ascending)) {
        val sortField = id match {
          case PropertyId.META_ID => DocumentIndex.FIELD__META_ID
          // case PropertyId.PHASE => todo: create filter query
          case PropertyId.TYPE => DocumentIndex.FIELD__DOC_TYPE_ID
          case PropertyId.LANGUAGE => DocumentIndex.FIELD__LANGUAGE_CODE
          case PropertyId.ALIAS => DocumentIndex.FIELD__ALIAS
          case PropertyId.HEADLINE => DocumentIndex.FIELD__META_HEADLINE
          case PropertyId.CREATED_DT => DocumentIndex.FIELD__CREATED_DATETIME
          case PropertyId.MODIFIED_DT => DocumentIndex.FIELD__MODIFIED_DATETIME
          case PropertyId.PUBLICATION_START_DT => DocumentIndex.FIELD__PUBLICATION_START_DATETIME
          case PropertyId.ARCHIVE_DT => DocumentIndex.FIELD__ARCHIVED_DATETIME
          case PropertyId.PUBLICATION_END_DT => DocumentIndex.FIELD__PUBLICATION_END_DATETIME
          case PropertyId.PARENTS => DocumentIndex.FIELD__PARENTS_COUNT
          case PropertyId.CHILDREN => DocumentIndex.FIELD__CHILDREN_COUNT
        }

        query.addSort(sortField, if (asc) SolrQuery.ORDER.asc else SolrQuery.ORDER.desc)
      }

      setQueryOpt(Some(query))
    }
  }

  override val getSortableContainerPropertyIds: JCollection[_] = Arrays.asList(
    PropertyId.META_ID,
    PropertyId.LANGUAGE,
    PropertyId.TYPE,
    /*PropertyId.PHASE,*/
    PropertyId.ALIAS,
    PropertyId.HEADLINE,
    PropertyId.CREATED_DT,
    PropertyId.MODIFIED_DT,
    PropertyId.PUBLICATION_START_DT,
    PropertyId.ARCHIVE_DT,
    PropertyId.PUBLICATION_END_DT,
    PropertyId.PARENTS,
    PropertyId.CHILDREN
  )


  private def mkItem(index: Index, fields: DocumentStoredFields): IndexedDocItem = new IndexedDocItem(index, fields) {

    private val doc = (DocumentDomainObject.fromDocumentTypeId(fields.documentType()) : DocumentDomainObject) |>> { doc =>
      doc.setCreatedDatetime(fields.createdDt())
      doc.setArchivedDatetime(fields.archivingDt())
      doc.setPublicationStartDatetime(fields.publicationStartDt())
      doc.setPublicationEndDatetime(fields.publicationEndDt())
      doc.setPublicationStatus(Document.PublicationStatus.of(fields.publicationStatusId()))
      doc.setLanguage(imcmsServices.getDocumentLanguages.getByCode(fields.languageCode()))
    }

    private val properties = scala.collection.mutable.Map.empty[AnyRef, Property[AnyRef]]

    private def formatDt(dt: Date): String = dt.asOption.map(dt => "%1$td.%1$tm.%1$tY %1$tH:%1$tM".format(dt)).getOrElse("")

    override val getItemPropertyIds: JCollection[_] = getContainerPropertyIds

    override def getItemProperty(id: AnyRef): Property[AnyRef] = properties.getOrElseUpdate(id, id match {
      case PropertyId.INDEX => LazyProperty(index + 1 : JInteger)
      case PropertyId.META_ID => LazyProperty(
        new HorizontalLayout with Spacing with NoMargin with UndefinedSize |>> { lyt =>
          val icon = new Image(null, Theme.Icon.Doc.phase(doc.getLifeCyclePhase))
          val label = new Label(fields.id().toString)

          lyt.addComponent(icon)
          lyt.addComponent(label)

          lyt.setComponentAlignment(icon, Alignment.MIDDLE_LEFT)
          lyt.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
        }
      )

      case PropertyId.HEADLINE => LazyProperty(fields.headline())
      case PropertyId.TYPE => LazyProperty(doc.getDocumentType.getName.toLocalizedString(Imcms.getUser))
      case PropertyId.LANGUAGE => LazyProperty(
        new HorizontalLayout with Spacing with NoMargin with UndefinedSize |>> { lyt =>
          val language = doc.getLanguage
          val icon = new Image(null, Theme.Icon.Language.flag(language))
          val label = new Label(language.getNativeName)

          lyt.addComponent(icon)
          lyt.addComponent(label)

          lyt.setComponentAlignment(icon, Alignment.MIDDLE_LEFT)
          lyt.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
        }
      )

      case PropertyId.ALIAS => LazyProperty(fields.alias())
      case PropertyId.PHASE => LazyProperty("doc_publication_phase.%s".format(doc.getLifeCyclePhase).i)
      case PropertyId.CREATED_DT => LazyProperty(formatDt(fields.createdDt()))
      case PropertyId.MODIFIED_DT => LazyProperty(formatDt(fields.modifiedDt()))

      case PropertyId.PUBLICATION_START_DT => LazyProperty(formatDt(fields.publicationStartDt()))
      case PropertyId.ARCHIVE_DT => LazyProperty(formatDt(fields.archivingDt()))
      case PropertyId.PUBLICATION_END_DT => LazyProperty(formatDt(fields.publicationEndDt()))

      case PropertyId.PARENTS => LazyProperty(parentsRenderer(fields))
      case PropertyId.CHILDREN => LazyProperty(childrenRenderer(fields))
    })
  }
}