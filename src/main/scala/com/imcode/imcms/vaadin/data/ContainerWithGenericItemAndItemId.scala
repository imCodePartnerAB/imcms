package com.imcode.imcms.vaadin.data

import com.vaadin.data.{Item, Container}


trait ContainerWithGenericItemAndItemId[A <: TItemId, B <: Item] extends ContainerWithGenericItemId[A] { this: Container =>
  override def item(id: A): B = getItem(id).asInstanceOf[B]
}
