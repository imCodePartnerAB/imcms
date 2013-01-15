package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

trait NullableProperty[A <: PropertyValue] extends GenericProperty[A] { this: Property[A] =>

}
