package com.imcode.imcms.vaadin.data

import scala.reflect.ClassTag

object LazyProperty {
  def apply[A <: TPropertyValue : ClassTag](value: => A): LazyProperty[A] = new LazyProperty(value)
}

class LazyProperty[A <: TPropertyValue : ClassTag](value: => A) extends AbstractProperty[A] with ReadOnlyProperty[A] {
  def getValue: A = value
}


