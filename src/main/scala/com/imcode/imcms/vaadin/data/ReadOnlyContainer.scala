package com.imcode.imcms.vaadin.data

import com.vaadin.data.{Item, Container}

trait ReadOnlyContainer { this: Container =>

  def addItem(): AnyRef = throw new UnsupportedOperationException

  def addItem(itemId: AnyRef): Item = throw new UnsupportedOperationException

  def removeItem(itemId: AnyRef): Boolean = throw new UnsupportedOperationException

  def removeAllItems(): Boolean = throw new UnsupportedOperationException

  def addContainerProperty(propertyId: AnyRef, `type`: Class[_], defaultValue: AnyRef): Boolean = throw new UnsupportedOperationException

  def removeContainerProperty(propertyId: AnyRef): Boolean = throw new UnsupportedOperationException
}
