package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

trait ReadOnlyProperty { this: Property =>
  override val isReadOnly: Boolean = true
  override def setValue(newValue: AnyRef): Unit = throw new UnsupportedOperationException
  override def setReadOnly(newStatus: Boolean): Unit = throw new UnsupportedOperationException
}
