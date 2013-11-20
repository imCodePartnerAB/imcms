package com.imcode.imcms.vaadin

import scala.language.implicitConversions

import com.vaadin.event.ItemClickEvent
import com.vaadin.data.Property
import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}

package object event {

  implicit def wrapItemClickNotifier(notifier: ItemClickEvent.ItemClickNotifier): ItemClickNotifierWrapper =
    new ItemClickNotifierWrapper(notifier)

  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier): ValueChangeNotifierWrapper =
    new ValueChangeNotifierWrapper(vcn)
}
