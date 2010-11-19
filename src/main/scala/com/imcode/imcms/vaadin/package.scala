package com.imcode.imcms

import com.vaadin.ui.{Table, Component, AbstractComponentContainer, Button, MenuBar}
import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.data.{Container, Property}
import com.imcode._
import vaadin.{ValueType, ContainerProperty}

package object vaadin {

  @deprecated("Use block instead")
  def unit(block: => Unit) = block _

  def block(b: => Unit) = b _

  def menuCommand(handler: MenuBar#MenuItem => Unit) = new MenuBar.Command {
    def menuSelected(mi: MenuBar#MenuItem) = handler(mi)
  }
  
  def buttonClickListener(eventHandler: Button#ClickEvent => Unit) =
    new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent) = eventHandler(event)
    }

  def propertyValueChangeListener(handler: ValueChangeEvent => Unit): ValueChangeListener =
    new Property.ValueChangeListener {
      def valueChange(event: ValueChangeEvent) = handler(event)
    }

  implicit def fn0ToButtonClickListener(f: () => Unit) = buttonClickListener { _ => f() }
  
  implicit def fn0ToMenuCommand(f: () => Unit) = menuCommand { _ => f() }

  implicit def fn0ToPropertyValueChangeListenerB(f: () => Unit) = propertyValueChangeListener { _ => f() }

  def addComponents(container: AbstractComponentContainer, component: Component, components: Component*) = {
    component +: components foreach { c => container addComponent c }
    container
  }


//  implicit def wrapAbstractComponentContainer(container: AbstractComponentContainer): AbstractComponentContainerWrapper =
//    new AbstractComponentContainerWrapper(container)

//  def addComponents(container: AbstractComponentContainer, component: Component, components: Component*) = {
//    component +: components foreach container.addComponent
//    container
//  }

  def addContainerProperties(container: Container, properties: ContainerProperty[_]*) =
    properties foreach { p =>
      container.addContainerProperty(p.id, p.clazz, p.defaultValue)
    }

  def addContainerProperties(table: Table, properties: (AnyRef, JClass[_], AnyRef)*) =
    for ((propertyId, propertyType, defaultValue) <- properties)
      table.addContainerProperty(propertyId, propertyType, defaultValue)

//  def whenSelected[A, B](property: Property)(fn: A => B): Option[B] = property.getValue match {
//    case null => None
//    case value: A => Some(fn(value))
//    case other => error("Unexpected field value: %s." format other)
//  }

  def whenSelected[A, B](property: ValueType[A])(fn: A => B): Option[B] = property.value match {
    case null => None
    case value => Some(fn(value))
  }
}