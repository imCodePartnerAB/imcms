package com.imcode.imcms.vaadin.data

case class FunctionProperty[A <: PropertyValue : ClassManifest](valueFn: () => A) extends AbstractProperty[A] with ReadOnlyProperty {
  def getValue: AnyRef = valueFn().asInstanceOf[AnyRef]
}
