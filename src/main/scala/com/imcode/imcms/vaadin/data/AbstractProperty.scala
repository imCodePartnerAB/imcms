package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property
import scala.reflect.ClassTag

abstract class AbstractProperty[A <: PropertyValue : ClassTag] extends Property with GenericProperty[A] {
  override val getType: Class[_] = scala.reflect.classTag[A].runtimeClass
  override def toString: String = Option(getValue).map(_.toString).getOrElse("")
}
