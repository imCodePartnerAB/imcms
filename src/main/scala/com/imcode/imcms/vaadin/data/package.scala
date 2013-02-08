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


  implicit def fnToPropertyValueChangeListener(fn: Property.ValueChangeEvent => Unit): Property.ValueChangeListener = {
    new Property.ValueChangeListener {
      def valueChange(event: Property.ValueChangeEvent): Unit = fn(event)
    }
  }


  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier) = new {
    def addValueChangeHandler(handler: => Unit): Unit = vcn.addValueChangeListener { _: Property.ValueChangeEvent => handler }
  }


  /**
   * Takes precedence over LowPriorityPropertyImplicits implicit:
   *   def mkPropertyOps[A <: AnyRef](property: Property[A]): PropertyOps[A]
   */
  implicit def mkTypedPropertyOps[A <: AnyRef](typedProperty: TypedProperty[A]): PropertyOps[A] = {
    new PropertyOps(typedProperty.asInstanceOf[Property[A]])
  }
}