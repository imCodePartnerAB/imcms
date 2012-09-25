package com.imcode.imcms.vaadin

/**
 * A container property.
 *
 * @param A container property class
 * @pram id container property id
 * @pram defaultValue container property default value
 */
case class PropertyDescriptor[A <: PropertyValue : Manifest](id: AnyRef, defaultValue: A = null) {
  val clazz = implicitly[Manifest[A]].erasure
}