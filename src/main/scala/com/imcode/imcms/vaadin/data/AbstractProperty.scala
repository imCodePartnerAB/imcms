package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

abstract class AbstractProperty[A <: PropertyValue : ClassManifest] extends Property with GenericProperty[A] {
  override val getType: Class[_] = classManifest[A].erasure
  override def toString: String = Option(getValue).map(_.toString).getOrElse("")
}
