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


  implicit def fnToPropertyValueChangeListener(fn: (Property.ValueChangeEvent => Any)): Property.ValueChangeListener = {
    new Property.ValueChangeListener {
      def valueChange(event: Property.ValueChangeEvent): Unit = fn(event)
    }
  }

  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier) = new {
    def addValueChangeHandler(handler: => Unit): Unit = vcn.addValueChangeListener { _: Property.ValueChangeEvent => handler }
  }


//  /**
//   * Some Vaadin's components such as TextFields, Labels, etc can act as as wrappers for other
//   * components of the same type.
//   * Ensures setValue and getValue are always called directly on wrapped property, not on wrapper itself.
//   */
//  trait WrappedPropertyValue[A <: PropertyValue] extends Property with Property.Viewer {
//    abstract override def setValue(value: AnyRef): Unit = getPropertyDataSource match {
//      case null => super.setValue(value)
//      case property => property.setValue(value)
//    }
//
//    abstract override def getValue(): AnyRef = getPropertyDataSource match {
//      case null => super.getValue()
//      case property => property.getValue()
//    }
//  }

  implicit class GenericPropertyWrapper[A <: PropertyValue](property: Property[A]) extends Property[A] with GenericProperty[A] {
    def getValue: A = ???

    def setValue(newValue: A) { ??? }

    def getType: Class[_ <: A] = ???

    def isReadOnly: Boolean = ???

    def setReadOnly(newStatus: Boolean) { ??? }
  }
}
