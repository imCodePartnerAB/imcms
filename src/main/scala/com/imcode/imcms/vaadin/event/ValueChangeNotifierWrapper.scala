package com.imcode.imcms.vaadin.event

import com.vaadin.data.Property
import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}


// implicit
class ValueChangeNotifierWrapper(vcn: Property.ValueChangeNotifier) {
  def addValueChangeHandler(handler: Property.ValueChangeEvent => Unit) {
    vcn.addValueChangeListener(
      new ValueChangeListener {
        def valueChange(event: ValueChangeEvent): Unit = handler(event)
      }
    )
  }
}
