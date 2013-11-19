package com.imcode
package imcms
package vaadin

import scala.language.implicitConversions

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

  /**
   * Takes precedence over LowPriorityPropertyImplicits implicit:
   *   def wrapProperty[A <: AnyRef](property: Property[A]): PropertyOps[A]
   */
  implicit def wrapTypedProperty[A <: AnyRef](typedProperty: TypedProperty[A]): PropertyWrapper[A] = {
    new PropertyWrapper(typedProperty.asInstanceOf[Property[A]])
  }
}