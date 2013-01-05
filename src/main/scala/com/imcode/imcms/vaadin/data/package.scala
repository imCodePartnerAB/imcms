package com.imcode
package imcms.vaadin

import com.vaadin.data.{Property, Container}

package object data {
  type PropertyId = AnyRef
  type PropertyValue = AnyRef
  type ItemId = AnyRef
  type ColumnId = AnyRef

  def addContainerProperties(container: Container, descriptors: PropertyDescriptor[_]*): Unit =
    descriptors.foreach { pd =>
      container.addContainerProperty(pd.id, pd.runtimeClass, pd.defaultValue)
    }


  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier) = new {
    def addValueChangeListener(listener: Property.ValueChangeEvent => Unit): Unit =
      vcn.addListener(new Property.ValueChangeListener {
        def valueChange(event: Property.ValueChangeEvent): Unit = listener(event)
      })

    def addValueChangeHandler(handler: => Unit): Unit = addValueChangeListener(_ => handler)
  }
}
