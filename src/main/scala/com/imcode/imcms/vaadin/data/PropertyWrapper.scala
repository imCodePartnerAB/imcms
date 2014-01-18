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
class PropertyWrapper[A <: AnyRef] (property: Property[A]) {
  def value: A = property.getValue
  def value_=(v: A): Unit = property.setValue(v)

  def valueOpt: Option[A] = Option(value)

  def clear(implicit ev: A =:= String): Unit = property.setValue("".asInstanceOf[A])
  def trimmedValue(implicit ev: A =:= String): String = value.trim
  def trimmedValueOpt(implicit ev: A =:= String): Option[String] = trimmedValue match {
    case "" => None
    case v => Some(v)
  }
  def isBlank(implicit ev: A =:= String): Boolean = trimmedValue.isEmpty
  def notBlank(implicit ev: A =:= String): Boolean = !isBlank
}
