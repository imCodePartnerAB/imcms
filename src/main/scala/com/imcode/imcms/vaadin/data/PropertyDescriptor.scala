package com.imcode.imcms.vaadin.data

import scala.reflect.ClassTag

/**
 * A container property descriptor.
 *
 * @tparam A container property class
 * @param id container property id
 * @param defaultValue container property default value
 */
case class PropertyDescriptor[A <: TPropertyValue : ClassTag](id: AnyRef, defaultValue: A = null) {
  val runtimeClass = scala.reflect.classTag[A].runtimeClass
}