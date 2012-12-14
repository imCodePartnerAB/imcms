package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

trait ReadOnlyProperty { this: Property =>
  val isReadOnly: Boolean = true
  def setValue(newValue: AnyRef): Unit = throw new UnsupportedOperationException
  def setReadOnly(newStatus: Boolean): Unit = throw new UnsupportedOperationException
}
