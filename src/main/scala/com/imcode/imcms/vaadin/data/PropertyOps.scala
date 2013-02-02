package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

class PropertyOps[A <: AnyRef](property: Property[A]) {
  def value = property.getValue
  def value_=(v: A): Unit = property.setValue(v)

  def valueOpt: Option[A] = Option(value)

  def clear(implicit ev: A =:= String): Unit = value = "".asInstanceOf[A]
  def trim(implicit ev: A =:= String): String = value.trim
  def trimOpt(implicit ev: A =:= String): Option[String] = trim match {
    case "" => None
    case v => Some(v)
  }
  def isBlank(implicit ev: A =:= String): Boolean = trim.isEmpty
  def notBlank(implicit ev: A =:= String): Boolean = !isBlank
}
