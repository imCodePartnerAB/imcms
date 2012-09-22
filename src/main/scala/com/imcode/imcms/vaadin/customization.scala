package com.imcode
package imcms
package vaadin

import scala.collection.JavaConverters._
import com.vaadin.ui._
import com.vaadin.data.{Item, Container, Property}
import com.vaadin.terminal.Sizeable
import java.util.Collections

trait TCSDefaultI18n extends TwinColSelect {
  setLeftColumnCaption("tcs.col.available.caption".i)
  setRightColumnCaption("tcs.col.selected.caption".i)
}

trait Disabled { this: Component =>
  setEnabled(false)
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
  override def fireClick(): Unit = super.fireClick()
}

trait ExposeValueChange extends AbstractField {
  override def fireValueChange(repaintIsNotNeeded: Boolean = true): Unit = super.fireValueChange(repaintIsNotNeeded)
}

trait OnceOnlyAttachAction extends AbstractComponent {

  var attachAction = Option.empty[this.type => Unit]

  override def attach() {
    attachAction.foreach(_ apply this)
    attachAction = None
  }
}

/**
 * By default a fields does not fire ValueChangeEvent when assigned value equals to existing.
 * This traits overrides default behavior and always fires ValueChangeEvent on value change.
 */
trait AlwaysFireValueChange extends AbstractField {
  override def setValue(value: AnyRef) {
    if (getValue == value) super.fireValueChange(true)
    else super.setValue(value)
  }
}

trait Margin { this: Layout =>
  setMargin(true)
}

trait NoMargin { this: Layout =>
  setMargin(false)
}

trait Spacing { this: Layout.SpacingHandler =>
  setSpacing(true)
}

trait NoSpacing { this: Layout.SpacingHandler =>
  setSpacing(false)
}

trait UndefinedSize { this: AbstractComponent =>
  setSizeUndefined()
}


trait Scrollable { this: com.vaadin.terminal.Scrollable =>
  setScrollable(true)
}

trait FullSize { this: AbstractComponent =>
  setSizeFull()
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

trait SmallStyle { this: Button =>
  setStyleName("small")
}

trait LightStyle { this: Panel =>
  setStyleName(Panel.STYLE_LIGHT)
}

trait Immediate { this: AbstractComponent =>
  setImmediate(true)
}

// Tree and Table
trait Selectable { this: {def setSelectable(selectable: Boolean): Unit} =>
  setSelectable(true)
}

trait NotSelectable { this: {def setSelectable(selectable: Boolean): Unit} =>
  setSelectable(false)
}

trait NullSelection { this: AbstractSelect =>
  setNullSelectionAllowed(true)
}

trait NoNullSelection { this: AbstractSelect =>
  setNullSelectionAllowed(false)
}


trait GenericSelect[A <: ItemId] extends AbstractSelect with GenericContainer[A] {
  def addItem(id: A, caption: String): Item = addItem(id) |>> { _ =>
    setItemCaption(id, caption)
  }

  def isSelected: Boolean
}


trait SingleSelect[A <: ItemId] extends GenericSelect[A] with GenericProperty[A] {
  setMultiSelect(false)

  def isSelected = value != null

  override def setMultiSelect(multiSelect: Boolean) {
    require(!multiSelect, "must be false")
    super.setMultiSelect(multiSelect)
  }
}

trait MultiSelect[A <: ItemId] extends GenericSelect[A] with GenericProperty[JCollection[A]] {
  setMultiSelect(true)

  override def setMultiSelect(multiSelect: Boolean) {
    require(multiSelect, "must be true")
    super.setMultiSelect(multiSelect)
  }

  def isSelected: Boolean = value.asScala.nonEmpty

  def selection: Seq[A] = value.asScala.toSeq

  def selection_=(v: Seq[A]) { value = v.asJava }

  def selection_=(v: Option[A]) { selection = v.toSeq }

  def first: Option[A] = value.asScala.headOption
}




/**
 * <code>value<code> property always returns a collection.
 */
trait MultiSelectBehavior[A <: AnyRef] extends GenericSelect[A] with GenericProperty[JCollection[A]] {

  def selection: Seq[A] = value.asScala.toSeq

  def selection_=(v: Seq[A]) { value = v.asJava }

  def selection_=(v: Option[A]) { selection = v.toSeq }

  def first: Option[A] = value.asScala.headOption

  def isSelected: Boolean = value.asScala.nonEmpty

  /**
   * @return collection of selected items or empty collection if there is no selected item(s).
   */
  final override def getValue(): AnyRef = super.getValue |> { v => if (isMultiSelect) v else Option(v).toSeq.asJavaCollection }

  final override def setMultiSelect(multiSelect: Boolean) =
    if (value.isEmpty) super.setMultiSelect(multiSelect)
    else throw new IllegalStateException("Multi-select value can not be changed on non-empty select.")


  final override def setValue(v: AnyRef) {
    super.setValue(
      v match {
        case null => if (isMultiSelect) Collections.emptyList[AnyRef] else null
        case coll: JCollection[_] => if (isMultiSelect) coll else coll.asScala.headOption.orNull
        case _ => if (isMultiSelect) Collections.singletonList(v) else v
      }
    )
  }
}

trait Now { this: DateField =>
  setValue(new java.util.Date)
}

trait YearResolution { this: DateField =>
  setResolution(DateField.RESOLUTION_YEAR)
}

trait MonthResolution { this: DateField =>
  setResolution(DateField.RESOLUTION_MONTH)
}

trait DayResolution { this: DateField =>
  setResolution(DateField.RESOLUTION_DAY)
}

trait MinuteResolution { this: DateField =>
  setResolution(DateField.RESOLUTION_MIN)
}

trait Required { this: Field =>
  setRequired(true)
}

trait NoTextInput { this: ComboBox =>
  setTextInputAllowed(false)
}



object Checks {
  def assertFixedSize(c: Component) {
    require(c.getWidthUnits != Sizeable.UNITS_PERCENTAGE, "Component width must not be difined in percentage.")
    require(c.getHeightUnits != Sizeable.UNITS_PERCENTAGE, "Component height must not be difined in percentage.")
  }
}

