package com.imcode.imcms.vaadin.data

import com.vaadin.data.{Property, Item}

trait ReadOnlyItem { this: Item =>
  override def addItemProperty(id: Any, property: Property): Boolean = throw new UnsupportedOperationException
  override def removeItemProperty(id: Any): Boolean = throw new UnsupportedOperationException
}
