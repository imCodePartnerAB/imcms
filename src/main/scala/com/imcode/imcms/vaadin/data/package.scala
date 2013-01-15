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


  implicit def fnToPropertyValueChangeListener(fn: (Property.ValueChangeEvent => Any)): Property.ValueChangeListener = {
    new Property.ValueChangeListener {
      def valueChange(event: Property.ValueChangeEvent): Unit = fn(event)
    }
  }

  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier) = new {
    def addValueChangeHandler(handler: => Unit): Unit = vcn.addValueChangeListener { _: Property.ValueChangeEvent => handler }
  }

  trait GenericProperty[A <: AnyRef] extends Property[AnyRef] {
    protected def getGenericValue(): A = getValue.asInstanceOf[A]
    protected def setGenericValue(value: A) { setValue(value) }
  }

  implicit def mkGenericPropertyOps[A <: AnyRef](genericProperty: GenericProperty[A]): PropertyOps[A] = {
    new PropertyOps(genericProperty.asInstanceOf[Property[A]])
  }
}


trait LowPriorityPropertyImplicits {

  class PropertyOps[A <: AnyRef](property: Property[A]) {
    def value = property.getValue
    def value_=(v: A): Unit = property.setValue(v)

    def valueOpt: Option[A] = Option(value)

    def clear(implicit ev: A =:= String): Unit = value = "".asInstanceOf[A]
    def trim(implicit ev: A =:= String): String = value.trim
    def trimOpt(implicit ev: A =:= String): Option[String] = trim match {
      case "" => None
      case v => Some(v)
    }
    def isBlank(implicit ev: A =:= String): Boolean = trim.isEmpty
    def notBlank(implicit ev: A =:= String): Boolean = !isBlank
  }

  implicit def mkPropertyOps[A <: AnyRef](property: Property[A]): PropertyOps[A] = new PropertyOps(property)
}
