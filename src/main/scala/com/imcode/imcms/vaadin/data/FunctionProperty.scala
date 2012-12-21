package com.imcode.imcms.vaadin.data

case class FunctionProperty[A <: PropertyValue : ClassManifest](valueFn: () => A) extends AbstractProperty[A] with ReadOnlyProperty {
  override def getValue: AnyRef = valueFn().asInstanceOf[AnyRef]
}
