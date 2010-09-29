package com.imcode.imcms.servlet.superadmin.vaadin

import com.imcode._
import java.lang.{Class => JClass, Boolean => JBoolean, Integer => JInteger}
import com.vaadin.ui.{Table, Component, AbstractComponentContainer, Button}
import com.vaadin.data.Property
import com.vaadin.data.Property._

package object ui {
  
  implicit def funToButtonClickListener(eventHandler: Button#ClickEvent => Unit) =
    new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent) = eventHandler(event)
    }

  implicit def blockToButtonClickListener(block: => Unit) = funToButtonClickListener { _ => block}

  implicit def blockToPropertyValueChangeListener(block: => Unit): Property.ValueChangeListener =
    new Property.ValueChangeListener {
      def valueChange(event: ValueChangeEvent) = block
    }

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