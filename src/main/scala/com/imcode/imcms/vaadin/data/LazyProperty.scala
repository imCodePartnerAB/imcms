package com.imcode.imcms.vaadin.data

object LazyProperty {
  def apply[A <: PropertyValue : ClassManifest](byName: => A): LazyProperty[A] = new LazyProperty(byName)
}

class LazyProperty[A <: PropertyValue : ClassManifest](byName: => A) extends AbstractProperty[A] with ReadOnlyProperty {
  def getValue: AnyRef = byName.asInstanceOf[AnyRef]
}


