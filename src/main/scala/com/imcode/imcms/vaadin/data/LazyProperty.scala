package com.imcode.imcms.vaadin.data

import scala.reflect.ClassTag

object LazyProperty {
  def apply[A <: PropertyValue : ClassTag](byName: => A): LazyProperty[A] = new LazyProperty(byName)
}

class LazyProperty[A <: PropertyValue : ClassTag](byName: => A) extends AbstractProperty[A] with ReadOnlyProperty[A] {
  def getValue: A = byName
}


