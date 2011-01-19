package com.imcode
package imcms

import com.vaadin.ui._
import com.vaadin.data.{Container, Property}
import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.Application

package object vaadin {

  // Current IDEA plugin can not resolve certain types from vaadin package without the import line below.
  // However, it is not required by scala compiler
  // todo: remove when the plugin will become smarter
  import vaadin._

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

  def addComponents(container: ComponentContainer, component: Component, components: Component*) = {
    component +: components foreach { c => container addComponent c }
    container
  }

  def addNamedComponents(container: CustomLayout, component: (String, Component), components: (String, Component)*) = {
    for ((location, component) <- component +: components) container.addComponent(component, location)
    container
  }

  def addContainerProperties(container: Container, properties: ContainerProperty[_]*) =
    properties foreach { p =>
      container.addContainerProperty(p.id, p.clazz, p.defaultValue)
    }

  @deprecated("")
  def addContainerProperties(table: Table, properties: (AnyRef, JClass[_], AnyRef)*) =
    for ((propertyId, propertyType, defaultValue) <- properties)
      table.addContainerProperty(propertyId, propertyType, defaultValue)

//  def whenSelected[A, B](property: Property)(fn: A => B): Option[B] = property.getValue match {
//    case null => None
//    case value: A => Some(fn(value))
//    case other => error("Unexpected field value: %s." format other)
//  }

  def whenSelected[A >: Null, B](property: ValueType[A] with AbstractSelect)(fn: A => B): Option[B] = property.value match {
    case null => None
    case value: JCollection[_] if value.isEmpty => None
    case value => Some(fn(value))
  }

  @deprecated("prototype code")
  def addItem(table: Table, id: AnyRef, data: AnyRef*) = table.addItem(data.toArray[AnyRef], id)

  /** Text field value type is always String */
  implicit def wrapTextField(textField: TextField) = new TextField(textField) with ValueType[String]

  /** Label value type is always String */
  implicit def wrapLabel(label: Label) = new Label(label) with ValueType[String]  

  /** Checkbox value type is always JBoolean */
  implicit def wrapCheckBox(checkBox: CheckBox) = new CheckBox("", checkBox) with ValueType[JBoolean]

  /** Date field value type is always Date */
  implicit def wrapDateField(dateField: DateField) = new DateField(dateField) with ValueType[java.util.Date]


  implicit def applicationToImcmsApplication(app: Application) = app.asInstanceOf[ImcmsApplication]

  implicit def wrapApplication(app: Application) = new ApplicationWrapper(app)

  implicit def wrapMenuBar(mb: MenuBar) = new MenuBarWrapper(mb)

  implicit def wrapMenuItem(mi: MenuBar#MenuItem) = new MenuItemWrapper(mi)

}