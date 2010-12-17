package com.imcode
package imcms

import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.data.{Container, Property}
import com.vaadin.ui._

package object vaadin {

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
  
  /**
   * Creates zero arity fn from by-name parameter.
   * Used in listeners and menu commands.
   */
  def block(b: => Unit) = b _

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

  def addItem(table: Table, id: AnyRef, data: AnyRef*) = table.addItem(data.toArray[AnyRef], id)


  /**
   * Property value type.
   *
   * Adds type-checked access to property value.
   */
  trait ValueType[A >: Null] extends Property {
    def value = getValue.asInstanceOf[A]
    def value_=(v: A) = setValue(v)
  }

  trait ItemIdType[A >: Null] extends Container {
    def itemIds = getItemIds.asInstanceOf[JCollection[A]]
    def item(id: A) = getItem(id)
    //abstract override def addItem(id: A) = super.addItem(id)
  }

  trait DataType[A >: Null] extends AbstractComponent {
    def data = getData.asInstanceOf[A]
    def data_=(d: A) = setData(d)
  }

  //trait SelectType[V] extends ValueType[V] with AbstractSelect {
  //  def itemIds = getItemIds.asInstanceOf[JCollection[V]]
  //}

  trait Disabled { this: Component =>
    setEnabled(false)
  }

  trait Secret { this: TextField =>
    setSecret(true)
  }

  trait ReadOnly { this: Component =>
    setReadOnly(true)
  }

  trait Checked { this: CheckBox =>
    setValue(true)
  }

  trait Unchecked { this: CheckBox =>
    setValue(false)
  }

  trait ExposeFireClick extends Button {
    override def fireClick = super.fireClick
  }

  trait Margin { this: AbstractLayout =>
    setMargin(true)
  }

  trait Spacing { this: Layout.SpacingHandler =>
    setSpacing(true)
  }

  trait NoSpacing { this: Layout.SpacingHandler =>
    setSpacing(false)
  }

  trait UndefinedSize { this: AbstractComponent =>
    setSizeUndefined
  }

  trait Scrollable { this: com.vaadin.terminal.Scrollable =>
    setScrollable(true)
  }

  trait FullSize { this: AbstractComponent =>
    setSizeFull
  }

  trait FullWidth { this: AbstractComponent =>
    setWidth("100%")
  }

  trait FullHeight { this: AbstractComponent =>
    setHeight("100%")
  }

  trait LinkStyle { this: Button =>
    setStyleName(Button.STYLE_LINK)
  }

  trait LightStyle { this: Panel =>
    setStyleName(Panel.STYLE_LIGHT)
  }

  trait Immediate { this: AbstractField =>
    setImmediate(true)
  }

  trait Selectable { this: Table =>
    setSelectable(true)
  }

  trait NullSelection extends AbstractSelect {
    setNullSelectionAllowed(true)
  }

  trait NoNullSelection extends AbstractSelect {
    setNullSelectionAllowed(false)
  }

  trait MultiSelect extends AbstractSelect {
    setMultiSelect(true)
  }

  trait SingleSelect extends AbstractSelect {
    setMultiSelect(false)
  }

  trait Now extends DateField {
    setValue(new java.util.Date)
  }

  trait YearResolution extends DateField {
    setResolution(DateField.RESOLUTION_YEAR)
  }

  trait MonthResolution extends DateField {
    setResolution(DateField.RESOLUTION_MONTH)
  }

  trait DayResolution extends DateField {
    setResolution(DateField.RESOLUTION_DAY)
  }

  trait MinuteResolution extends DateField {
    setResolution(DateField.RESOLUTION_MIN)
  }

  //trait UndefiedWidth { this: AbstractComponent =>
  //  setSizeFull
  //  setWid
  //}
  //
  //trait UndefiedHeight { this: Layout.SpacingHandler =>
  //  setSpacing(true)
  //}


  trait Reloadable extends Table {
    //type ItemId <: AnyRef
    //type Value <: AnyRef

    var itemsProvider: () => Seq[(AnyRef, Seq[AnyRef])] =
      () => error("itemsProvider is not set.")

    def reload() {
      removeAllItems
      for ((id, values) <- itemsProvider()) addItem(values.toArray, id)
    }
  }

  
  /** Text field value type is always String */
  implicit def wrapTextField(textField: TextField) = new TextField(textField) with ValueType[String]

  /** Label value type is always String */
  implicit def wrapLabel(label: Label) = new Label(label) with ValueType[String]  

  /** Checkbox value type is always JBoolean */
  implicit def wrapCheckBox(checkBox: CheckBox) = new CheckBox("", checkBox) with ValueType[JBoolean]

  /** Date field value type is always Date */
  implicit def wrapDateField(dateField: DateField) = new DateField(dateField) with ValueType[java.util.Date]
}