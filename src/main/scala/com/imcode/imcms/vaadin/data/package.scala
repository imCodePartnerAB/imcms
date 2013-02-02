package com.imcode
package imcms
package vaadin

import com.vaadin.data.{Property, Container}

package object data extends LowPriorityPropertyImplicits {

  type TPropertyId = AnyRef
  type TPropertyValue = AnyRef
  type TItemId = AnyRef
  type TColumnId = AnyRef

  def addContainerProperties(container: Container, descriptors: PropertyDescriptor[_]*): Unit =
    descriptors.foreach { pd =>
      container.addContainerProperty(pd.id, pd.runtimeClass, pd.defaultValue)
    }


  implicit def fnToPropertyValueChangeListener(fn: Property.ValueChangeEvent => Any): Property.ValueChangeListener = {
    new Property.ValueChangeListener {
      def valueChange(event: Property.ValueChangeEvent): Unit = fn(event)
    }
  }

  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier) = new {
    def addValueChangeHandler(handler: => Unit): Unit = vcn.addValueChangeListener { _: Property.ValueChangeEvent => handler }
  }

  implicit def mkGenericPropertyOps[A <: AnyRef](genericProperty: GenericProperty[A]): PropertyOps[A] = {
    new PropertyOps(genericProperty.asInstanceOf[Property[A]])
  }
}