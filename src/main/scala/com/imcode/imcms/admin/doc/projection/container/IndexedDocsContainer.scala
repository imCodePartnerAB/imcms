package com.imcode
package imcms
package admin.doc.projection.container

import com.vaadin.data.{Item, Property, Container}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.data._

import _root_.imcode.server.user.UserDomainObject
import imcode.server.document.index.{DocumentStoredFields, SearchResult}

import java.util.{Date, Collections, Arrays}

import org.apache.solr.client.solrj.SolrQuery
import com.imcode.imcms.vaadin.ui.{Theme, UndefinedSize, NoMargin, Spacing}
import imcode.server.Imcms
import imcode.server.document.DocumentDomainObject
import com.imcode.imcms.api.Document

// todo: Implement sorting
// todo: Selection: memory solr - copy solr doc from main solr to the RAM
class IndexedDocsContainer(
    user: UserDomainObject,
    parentsRenderer: (DocumentStoredFields => Component) = (_ => null),
    childrenRenderer: (DocumentStoredFields => Component) = (_ => null)
) extends Container
with ContainerWithTypedItemId[Index]
with ReadOnlyOrderedContainer
with Container.Sortable
with ContainerItemSetChangeNotifier
with ImcmsServicesSupport {

  private class Items(val searchResultOpt: Option[SearchResult]) {
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
   * Returns inclusive range of visible docs ids.
   *
   * @return Some(range) or None if there are no visible docs in this container.
   */
  def visibleDocsRange(): Option[(DocId, DocId)] = imcmsServices.getDocumentMapper.getDocumentIdRange.asOption.map {
    idsRange => (idsRange.getMinimumInteger: DocId, idsRange.getMaximumInteger: DocId)
  }

  override val getContainerPropertyIds: JCollection[_] = PropertyId.valuesCollection()

  override def getType(propertyId: AnyRef): Class[_] = propertyId.asInstanceOf[PropertyId].getType

  override def getContainerProperty(itemId: AnyRef, propertyId: AnyRef): Property[_] = getItem(itemId).getItemProperty(propertyId)

  @transient
  override def size(): Int = items.size

  override def getItemIds(): JCollection[_] = new java.util.AbstractList[Index] {
    override def get(index: Int): Index = index
    override val size: Int = IndexedDocsContainer.this.size
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

  override def sort(propertyId: Array[AnyRef], ascending: Array[Boolean]) {}

  override val getSortableContainerPropertyIds: JCollection[_] = Arrays.asList(
    PropertyId.META_ID, PropertyId.LANGUAGE, PropertyId.TYPE, PropertyId.PHASE, PropertyId.ALIAS, PropertyId.HEADLINE
  )


  private def mkItem(index: Index, fields: DocumentStoredFields): IndexedDocItem = new IndexedDocItem(index, fields) {

    private val doc = (DocumentDomainObject.fromDocumentTypeId(fields.documentType()) : DocumentDomainObject) |>> { doc =>
      doc.setCreatedDatetime(fields.createdDt())
      doc.setArchivedDatetime(fields.archivingDt())
      doc.setPublicationStartDatetime(fields.publicationStartDt())
      doc.setPublicationEndDatetime(fields.publicationEndDt())
      doc.setPublicationStatus(Document.PublicationStatus.of(fields.publicationStatusId()))
      doc.setLanguage(imcmsServices.getDocumentI18nSupport.getByCode(fields.languageCode()))
    }

    private val properties = scala.collection.mutable.Map.empty[AnyRef, Property[AnyRef]]

    private def formatDt(dt: Date): String = dt.asOption.map(dt => "%1$td.%1$tm.%1$tY %1$tH:%1$tM".format(dt)).getOrElse("")

    override def getItemPropertyIds: JCollection[_] = PropertyId.valuesCollection()

    override def getItemProperty(id: AnyRef): Property[AnyRef] = properties.getOrElseUpdate(id, id match {
      case PropertyId.INDEX => LazyProperty(index + 1 : JInteger)
      case PropertyId.META_ID => LazyProperty(
        new HorizontalLayout with Spacing with NoMargin with UndefinedSize |>> { lyt =>
          val icon = new Image(null, Theme.Icon.Doc.phase(doc.getLifeCyclePhase))
          val label = new Label(fields.metaId().toString)

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
      case PropertyId.ARCHIVING_DT => LazyProperty(formatDt(fields.archivingDt()))
      case PropertyId.EXPIRATION_DT => LazyProperty(formatDt(fields.publicationEndDt()))

      case PropertyId.PARENTS => LazyProperty(parentsRenderer(fields))
      case PropertyId.CHILDREN => LazyProperty(childrenRenderer(fields))
    })
  }
}