package com.imcode.imcms.vaadin
package data

/**
 * A container property.
 *
 * @tparam A container property class
 * @param id container property id
 * @param defaultValue container property default value
 */
case class PropertyDescriptor[A <: PropertyValue : Manifest](id: AnyRef, defaultValue: A = null) {
  val clazz = implicitly[Manifest[A]].erasure
}