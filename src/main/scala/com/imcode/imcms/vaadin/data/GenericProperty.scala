package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

trait GenericProperty[A <: AnyRef] extends Property[AnyRef] {
  protected def getGenericValue(): A = getValue.asInstanceOf[A]
  protected def setGenericValue(value: A) { setValue(value) }
}
