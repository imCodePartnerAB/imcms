package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

/**
 * Some Vaain classes which extends Property hard codes AnyRef as the type parameter (ex. AbstractSelect).
 * This effectively turns all its descendant (ex. ComboBox) to non-generics
 * which leads to redundant type casts in a client code.
 *
 * @tparam A real property value type
 */
trait GenericProperty[A <: AnyRef] extends Property[AnyRef] {
  protected def getGenericValue(): A = getValue.asInstanceOf[A]
  protected def setGenericValue(value: A) { setValue(value) }
}
