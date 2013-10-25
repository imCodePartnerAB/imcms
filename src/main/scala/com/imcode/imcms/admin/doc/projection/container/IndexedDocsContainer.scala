package com.imcode
package imcms
package admin.doc.projection.container

import com.vaadin.data.{Property, Item, Container}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.data._

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.user.UserDomainObject
import java.util.Arrays
import org.apache.solr.client.solrj.SolrQuery

// todo: Implement sorting
// todo: Visible docs must be referenced by I18nDocRef
class IndexedDocsContainer(
    user: UserDomainObject,
    private var solrQueryOpt: Option[SolrQuery] = None,
    private var visibleDocsFilterOpt: Option[Set[DocId]] = None,
    parentsRenderer: (DocumentDomainObject => Component) = _ => null,
    childrenRenderer: (DocumentDomainObject => Component) = _ => null
) extends Container
with ContainerWithTypedItemId[Index]
with ReadOnlyContainer
with Container.Sortable
with ContainerItemSetChangeNotifier
with ImcmsServicesSupport {

  private class VisibleDocs(val docs: JList[DocumentDomainObject] = java.util.Collections.emptyList[DocumentDomainObject]) {
    val size: Int = docs.size()
    val isEmpty: Boolean = docs.isEmpty
    val nonEmpty: Boolean = !isEmpty
    val contains: (Int => Boolean) = if (isEmpty) Function.const(false) else { i => i >= 0 && i < size }
  }

  private var visibleDocs = new VisibleDocs()

  def getVisibleDocsFilterOpt: Option[Set[DocId]] = visibleDocsFilterOpt

  def setVisibleDocsFilterOpt(visibleItemsFilterOpt: Option[Set[DocId]]) {
    if (this.visibleDocsFilterOpt != visibleItemsFilterOpt) {
      this.visibleDocsFilterOpt = visibleItemsFilterOpt
      updateVisibleDocsIds()
    }
  }

  def getSolrQueryOpt: Option[SolrQuery] = solrQueryOpt

  def setSolrQueryOpt(solrQueryOpt: Option[SolrQuery]) {
    if (this.solrQueryOpt.isDefined || solrQueryOpt.isDefined) {
      this.solrQueryOpt = solrQueryOpt
      updateVisibleDocsIds()
    }
  }

  private def updateVisibleDocsIds() {
    val docs: JList[DocumentDomainObject] = (solrQueryOpt, visibleDocsFilterOpt) match {
      case (None, _) => java.util.Collections.emptyList[DocumentDomainObject]
      case (_, Some(ids)) if ids.isEmpty => java.util.Collections.emptyList[DocumentDomainObject]
      case (Some(solrQuery), None) =>
        imcmsServices.getDocumentMapper.getDocumentIndex.search(solrQuery, user)
      case (Some(solrQuery), Some(ids)) =>
        // todo: apply visible docs filter
        imcmsServices.getDocumentMapper.getDocumentIndex.search(solrQuery, user)
    }

    visibleDocs = new VisibleDocs(docs)

    notifyItemSetChanged()
  }


  /**
   * Returns inclusive range of visible docs ids.
   *
   * @return Some(range) or None if there are no visible docs in this container.
   */
  def visibleDocsRange(): Option[(DocId, DocId)] = visibleDocsFilterOpt match {
    case Some(ids) => if (ids.isEmpty) None else Some(ids.min, ids.max)
    case _ => imcmsServices.getDocumentMapper.getDocumentIdRange |> { idsRange =>
      Some(idsRange.getMinimumInteger: DocId, idsRange.getMaximumInteger: DocId)
    }
  }

  override val getContainerPropertyIds: JCollection[_] = PropertyId.valuesCollection()

  override def getType(propertyId: AnyRef): Class[_] = propertyId.asInstanceOf[PropertyId].getType

  override def getContainerProperty(itemId: AnyRef, propertyId: AnyRef): Property[AnyRef] = getItem(itemId).getItemProperty(propertyId)

  @transient
  override def size(): Int = visibleDocs.size

  override def getItemIds(): JCollection[_] = new java.util.AbstractList[Index] {
    def get(index: Int): Index = index
    def size(): Int = IndexedDocsContainer.this.size
  }

  override def containsId(itemId: AnyRef): Boolean = itemId match {
    case id: Index => visibleDocs.contains(id)
    case _ => false
  }

  override def isFirstId(itemId: AnyRef): Boolean = itemId match {
    case index: Index if visibleDocs.nonEmpty => index == 0
    case _ => false
  }

  override def isLastId(itemId: AnyRef): Boolean = itemId match {
    case index: Index if visibleDocs.nonEmpty => index == visibleDocs.size - 1
    case _ => false
  }

  override def firstItemId: Index = if (visibleDocs.isEmpty) null else 0

  override def lastItemId: Index = if (visibleDocs.isEmpty) null else visibleDocs.size - 1

  override def prevItemId(itemId: AnyRef): Index = itemId match {
    case id: Index if visibleDocs.contains(id - 1) => id - 1
    case _ => null
  }

  override def nextItemId(itemId: AnyRef): Index = itemId match {
    case id: Index if visibleDocs.contains(id + 1) => id + 1
    case _ => null
  }

  override def getItem(itemId: AnyRef): DocItem = itemId match {
    case id: Index if visibleDocs.contains(id) => DocItem(id, visibleDocs.docs.get(id))
    case _ => null
  }

  override def sort(propertyId: Array[AnyRef], ascending: Array[Boolean]) {}

  override val getSortableContainerPropertyIds: JCollection[_] = Arrays.asList(
    PropertyId.META_ID, PropertyId.LANGUAGE, PropertyId.TYPE, PropertyId.PHASE, PropertyId.ALIAS, PropertyId.HEADLINE
  )

  override def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef): Item = throw new UnsupportedOperationException

  override def addItemAfter(previousItemId: AnyRef): Item = throw new UnsupportedOperationException
}