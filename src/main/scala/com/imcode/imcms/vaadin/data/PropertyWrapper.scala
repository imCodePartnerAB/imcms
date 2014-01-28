package com.imcode
package imcms.vaadin.data

import com.vaadin.data.Property

/**
 * This class serves as a wrapper for <code>Property</code>
 * Where needed, instances of properties are implicitly converted into this class.
 *
 * @param property
 * @tparam A
 */
class PropertyWrapper[A <: AnyRef](property: Property[A]) {
  def value: A = property.getValue
  def value_=(v: A): Unit = property.setValue(v)

  def valueOpt: Option[A] = Option(value)
}
