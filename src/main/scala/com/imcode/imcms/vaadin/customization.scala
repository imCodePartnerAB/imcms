package com.imcode
package imcms.vaadin

import com.vaadin.ui._
import com.vaadin.data.{Item, Container, Property}

/**
 * A container property.
 *
 * @param A container property class
 * @pram id container property id
 * @pram defaultValue container property default value
 */
case class ContainerProperty[A >: Null : Manifest](id: AnyRef, defaultValue: AnyRef = null) {
  val clazz = implicitly[Manifest[A]].erasure
}

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
}

/**
 * Component data type.
 *
 * Adds type-checked access to data.
 */
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

/** Changes fireClick visibility from protected to public. */
trait ExposeFireClick extends Button {
  override def fireClick() = super.fireClick()
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

@deprecated("Prototype, replace with SingleSelect2")
trait SingleSelect extends AbstractSelect {
  setMultiSelect(false)
}

trait XSelect[T >: Null] extends AbstractSelect with ItemIdType[T] {
  def addItem(id: T, caption:String): Item = letret(addItem(id)) { _ =>
    setItemCaption(id, caption)
  }
}

trait SingleSelect2[T >: Null] extends XSelect[T] with ValueType[T] {
  setMultiSelect(false)

  def isSelected = value != null
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