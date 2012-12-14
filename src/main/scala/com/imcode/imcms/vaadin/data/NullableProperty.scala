package com.imcode.imcms.vaadin
package data

trait NullableProperty[A <: PropertyValue] extends GenericProperty[A] {
  def valueOpt: Option[A] = Option(value)
}
