package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property
import scala.reflect.ClassTag

abstract class AbstractProperty[A <: TPropertyValue : ClassTag] extends Property[A] {
  override val getType: Class[_ <: A] = scala.reflect.classTag[A].runtimeClass.asInstanceOf[Class[A]]
  override def toString: String = Option(getValue).map(_.toString).getOrElse("")
}
