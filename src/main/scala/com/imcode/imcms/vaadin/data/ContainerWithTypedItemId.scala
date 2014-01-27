package com.imcode.imcms.vaadin.data

import scala.collection.JavaConverters._
import com.vaadin.data.{Item, Container}
import com.imcode._

trait ContainerWithTypedItemId[A <: TItemId] { this: Container  =>

  def addItems(ids: Seq[A]) {
    ids.foreach(id => addItem(id))
  }

  def addItems(ids: JCollection[A]) {
    ids.iterator().asScala.foreach(id => addItem(id))
  }

  def setItems(ids: Seq[A]) {
    removeAllItems()
    addItems(ids)
  }

  def setItems(ids: JCollection[A]) {
    removeAllItems()
    addItems(ids)
  }

  //def itemIds: JCollection[A] = getItemIds.asInstanceOf[JCollection[A]]
  def item(id: A): Item = getItem(id)

  def itemIds: JCollection[A] = getItemIds.asInstanceOf[JCollection[A]]

  def firstItemIdOpt: Option[A] = itemIds.asScala.headOption
}