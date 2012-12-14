package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

/**
 * Property value type.
 *
 * Adds type-checked access to property value.
 */
trait GenericProperty[A <: PropertyValue] { this: Property =>
  def value = getValue.asInstanceOf[A]
  def value_=(v: A): Unit = setValue(v)

  def clear(implicit ev: A =:= String): Unit = setValue("")
  def trim(implicit ev: A =:= String): String = value.trim
  def trimOpt(implicit ev: A =:= String): Option[String] = trim match {
    case "" => None
    case v => Some(v)
  }
  def isBlank(implicit ev: A =:= String): Boolean = trim.isEmpty
  def notBlank(implicit ev: A =:= String): Boolean = !isBlank
}
