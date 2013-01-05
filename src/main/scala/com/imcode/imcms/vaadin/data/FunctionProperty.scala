package com.imcode.imcms.vaadin.data

import scala.reflect.ClassTag

case class FunctionProperty[A <: PropertyValue : ClassTag](valueFn: () => A) extends AbstractProperty[A] with ReadOnlyProperty {
  override def getValue: AnyRef = valueFn().asInstanceOf[AnyRef]
}
