package com.imcode.imcms.vaadin.data

import com.vaadin.data.{Property, Item}

trait ReadOnlyItem { this: Item =>
  override def addItemProperty(id: AnyRef, property: Property[_]): Boolean = throw new UnsupportedOperationException
  override def removeItemProperty(id: AnyRef): Boolean = throw new UnsupportedOperationException
}
