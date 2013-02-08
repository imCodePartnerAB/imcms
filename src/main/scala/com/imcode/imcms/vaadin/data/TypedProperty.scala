package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

/**
 * Prior to Vaadin v.7, Property interface was untyped.
 * Even when Property became generic, some Vaain classes which extend it define Object as their type parameter
 * (ex. AbstractSelect).
 * This effectively turns all descendants of such classes (ex. ComboBox, Table, Tree, etc) to non-generics
 * which leads to redundant type casts in a client code.
 *
 * @tparam A property value type
 */
trait TypedProperty[A <: AnyRef] { this: Property[AnyRef] =>
  protected def getTypedValue(): A = getValue.asInstanceOf[A]
}

//trait TypedProperty[A <: AnyRef] extends Property[AnyRef] {
//  abstract override def getValue(): A = super.getValue().asInstanceOf[A]
//}
