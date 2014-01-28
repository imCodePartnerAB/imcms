package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

class StringPropertyWrapper(property: Property[String]) {

  def clear(): Unit = property.setValue("")

  def trimmedValue: String = property.getValue.trim

  def trimmedValueOpt: Option[String] = trimmedValue match {
    case "" => None
    case v => Some(v)
  }

  def isBlank: Boolean = trimmedValue.isEmpty

  def notBlank: Boolean = !isBlank
}
