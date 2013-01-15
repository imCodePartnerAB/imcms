package com.imcode.imcms.vaadin.data

import scala.collection.JavaConverters._
import com.vaadin.data.{Item, Container}
import com.imcode._

// todo: ???itemsIds as Seq???
trait ContainerWithGenericItemId[A <: TItemId] { this: Container =>
  def itemIds: JCollection[A] = getItemIds.asInstanceOf[JCollection[A]]
  def itemIds_=(ids: JCollection[A]) {
    removeAllItems()
    ids.asScala.foreach(addItem _)
  }

  def item(id: A): Item = getItem(id)

  def firstItemIdOpt: Option[A] = itemIds.asScala.headOption
}