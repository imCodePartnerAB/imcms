package com.imcode.imcms.vaadin.data

/**
 * A container property.
 *
 * @tparam A container property class
 * @param id container property id
 * @param defaultValue container property default value
 */
case class PropertyDescriptor[A <: PropertyValue : ClassManifest](id: AnyRef, defaultValue: A = null) {
  val clazz = classManifest[A].erasure
}