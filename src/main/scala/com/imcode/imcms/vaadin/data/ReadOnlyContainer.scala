package com.imcode.imcms.vaadin.data

import com.vaadin.data.{Item, Container}

trait ReadOnlyContainer {
  this: Container =>

  override def addItem(): AnyRef = throw new UnsupportedOperationException

  override def addItem(itemId: AnyRef): Item = throw new UnsupportedOperationException

  override def removeItem(itemId: AnyRef): Boolean = throw new UnsupportedOperationException

  override def removeAllItems(): Boolean = throw new UnsupportedOperationException

  override def addContainerProperty(propertyId: AnyRef, `type`: Class[_], defaultValue: AnyRef): Boolean = throw new UnsupportedOperationException

  override def removeContainerProperty(propertyId: AnyRef): Boolean = throw new UnsupportedOperationException
}
