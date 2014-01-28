package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

/**
 * Turns legacy (Vaadin v < 7) non-generic property or a property parametrized with AnyRef into typed property.
 *
 * @tparam A property value type
 */
trait TypedProperty[A <: AnyRef] extends Property[AnyRef] {
  abstract override def getValue: A = super.getValue.asInstanceOf[A]
}
