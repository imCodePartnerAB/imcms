package com.imcode.imcms.vaadin.data

import com.vaadin.data.{Container, Item}

trait ReadOnlyOrderedContainer extends ReadOnlyContainer { this: Container.Ordered =>
  override def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef): Item = throw new UnsupportedOperationException

  override def addItemAfter(previousItemId: AnyRef): Item = throw new UnsupportedOperationException
}
