package com.imcode.imcms.vaadin.data

import scala.reflect.ClassTag

case class FunctionProperty[A <: TPropertyValue : ClassTag](valueFn: () => A) extends AbstractProperty[A] with ReadOnlyProperty[A] {
  override def getValue: A = valueFn()
}
