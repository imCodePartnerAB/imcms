package com.imcode.imcms.vaadin.data

import scala.language.implicitConversions

import com.vaadin.data.Property

trait LowPriorityPropertyImplicits {
  implicit def wrapProperty[A <: AnyRef](property: Property[A]): PropertyWrapper[A] = new PropertyWrapper(property)
  implicit def wrapStringProperty[A <: String](property: Property[String]): StringPropertyWrapper = new StringPropertyWrapper(property)
}
