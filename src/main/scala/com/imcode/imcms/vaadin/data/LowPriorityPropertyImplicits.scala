package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

trait LowPriorityPropertyImplicits {
  implicit def mkPropertyOps[A <: AnyRef](property: Property[A]): PropertyOps[A] = new PropertyOps(property)
}
