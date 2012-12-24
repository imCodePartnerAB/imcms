package com.imcode.imcms.vaadin.data

import com.vaadin.data.{Item, Container}


trait GenericItemContainer[A <: ItemId, B <: Item] extends GenericContainer[A] { this: Container =>
  override def item(id: A): B = getItem(id).asInstanceOf[B]
}
