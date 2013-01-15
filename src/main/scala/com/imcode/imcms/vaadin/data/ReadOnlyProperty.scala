package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

trait ReadOnlyProperty[A <: PropertyValue] { this: Property[A] =>
  override val isReadOnly: Boolean = true
  override def setValue(newValue: A): Unit = throw new UnsupportedOperationException
  override def setReadOnly(newStatus: Boolean): Unit = throw new UnsupportedOperationException
}
