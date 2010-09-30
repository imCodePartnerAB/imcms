package com.imcode.imcms.servlet.superadmin.vaadin

import com.imcode._
import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger}
import com.vaadin.ui.{Table, Component, AbstractComponentContainer, Button, MenuBar}
import com.vaadin.data.Property
import com.vaadin.data.Property._
import _root_.com.vaadin.ui.MenuBar

package object ui {

  def unit(block: => Unit) = block _

  def menuCommand(handler: MenuBar#MenuItem => Unit) = new MenuBar.Command {
    def menuSelected(mi: MenuBar#MenuItem) = handler(mi)
  }
  
  implicit def unitToMenuCommand(u: () => Unit) = menuCommand { _ => u() }

  def buttonClickListener(eventHandler: Button#ClickEvent => Unit) =
    new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent) = eventHandler(event)
    }

  implicit def unitToButtonClickListener(u: () => Unit) = buttonClickListener { _ => u() }

  def propertyValueChangeListener(handler: ValueChangeEvent => Unit): Property.ValueChangeListener =
    new Property.ValueChangeListener {
      def valueChange(event: ValueChangeEvent) = handler(event)
    }
 
  implicit def unitToPropertyValueChangeListenerB(u: () => Unit) = propertyValueChangeListener { _ => u() }



//  def addValueChangeHandler(target: AbstractField)(handler: ValueChangeEvent => Unit) {
//    target addListener new Property.ValueChangeListener {
//      def valueChange(event: ValueChangeEvent) = handler(event)
//    }
//  }

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

  def addContainerProperties(table: Table, properties: (AnyRef, JClass[_], AnyRef)*) =
    for ((propertyId, propertyType, defaultValue) <- properties)
      table.addContainerProperty(propertyId, propertyType, defaultValue)
}