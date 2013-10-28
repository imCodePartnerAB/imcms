package com.imcode
package imcms
package admin.doc.projection.container

import com.vaadin.data.{Property, Item, Container}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.data._

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.index.SearchResult

import java.util.Arrays

import org.apache.solr.client.solrj.SolrQuery

// todo: Implement sorting
class IndexedDocsContainer(
    user: UserDomainObject,
    parentsRenderer: ((DocId, JCollection[DocId]) => Component) = (_, _) => null,
    childrenRenderer: ((DocId, JCollection[DocId]) => Component) = (_, _) => null
) extends Container
with ContainerWithTypedItemId[Index]
with ReadOnlyContainer
with Container.Sortable
with ContainerItemSetChangeNotifier
with ImcmsServicesSupport {

  private class Items(val searchResultOpt: Option[SearchResult]) {
    val isEmpty: Boolean = searchResultOpt.isEmpty
    val nonEmpty: Boolean = searchResultOpt.nonEmpty
    val contains: (Index => Boolean) = if (searchResultOpt.isEmpty) Function.const(false) else searchResultOpt.get.contains
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
    idRange => (idsRange.getMinimumInteger: DocId, idsRange.getMaximumInteger: DocId)
  }

  override val getContainerPropertyIds: JCollection[_] = PropertyId.valuesCollection()

  override def getType(propertyId: AnyRef): Class[_] = propertyId.asInstanceOf[PropertyId].getType

  override def getContainerProperty(itemId: AnyRef, propertyId: AnyRef): Property[AnyRef] = getItem(itemId).getItemProperty(propertyId)

  @transient
  override def size(): Int = items.size

  override def getItemIds(): JCollection[_] = new java.util.AbstractList[Index] {
    def get(index: Int): Index = index
    def size(): Int = IndexedDocsContainer.this.size
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
    case id: Index if items.contains(id) => IndexedDocItem(id, items.docs.get(id))
    case _ => null
  }

  override def sort(propertyId: Array[AnyRef], ascending: Array[Boolean]) {}

  override val getSortableContainerPropertyIds: JCollection[_] = Arrays.asList(
    PropertyId.META_ID, PropertyId.LANGUAGE, PropertyId.TYPE, PropertyId.PHASE, PropertyId.ALIAS, PropertyId.HEADLINE
  )

  override def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef): Item = throw new UnsupportedOperationException

  override def addItemAfter(previousItemId: AnyRef): Item = throw new UnsupportedOperationException
}