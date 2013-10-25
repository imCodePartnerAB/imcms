package com.imcode.imcms.vaadin

import scala.language.implicitConversions

import com.vaadin.event.ItemClickEvent
import com.vaadin.data.Property
import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}

package object event {

  implicit def wrapItemClickNotifier(notifier: ItemClickEvent.ItemClickNotifier) = new {
    def addItemClickHandler(handler: ItemClickEvent => Unit) {
      notifier.addItemClickListener(new ItemClickEvent.ItemClickListener {
        def itemClick(event: ItemClickEvent) {
          handler(event)
        }
      })
    }
  }

  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier) = new {
    def addValueChangeHandler(handler: Property.ValueChangeEvent => Unit): Unit = vcn.addValueChangeListener(
      new ValueChangeListener {
        def valueChange(event: ValueChangeEvent): Unit = handler(event)
      }
    )
  }
}
