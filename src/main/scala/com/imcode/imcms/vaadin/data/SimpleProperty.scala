package com.imcode.imcms.vaadin.data

import scala.reflect.ClassTag

object SimpleProperty {
  def apply[A <: TPropertyValue : ClassTag](value: A): SimpleProperty[A] = new SimpleProperty(value)
}

class SimpleProperty[A <: TPropertyValue : ClassTag](override val getValue: A) extends AbstractProperty[A] with ReadOnlyProperty[A]
