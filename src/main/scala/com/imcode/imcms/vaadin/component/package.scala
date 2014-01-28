package com.imcode
package imcms
package vaadin

import _root_.java.lang.AssertionError
import _root_.java.util.Collections
import scala.language.implicitConversions

import scala.collection.JavaConverters._
import com.vaadin.ui._
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.data.Item
import com.imcode.imcms.vaadin.data._
import com.vaadin.server.{Sizeable, Resource}
import com.vaadin.ui.themes.{Reindeer, BaseTheme}
import com.vaadin.shared.ui.datefield.Resolution


package object component {

  implicit def wrapUI(ui: UI): UIWrapper = new UIWrapper(ui)

  def whenSelected[A <: AnyRef, B](select: AbstractSelect with SingleSelect[A])(fn: A => B): Option[B] = select.firstSelectedOpt.map(fn)
  def whenSelected[A <: AnyRef, B](select: AbstractSelect with MultiSelect[A])(fn: Seq[A] => B): Option[B] = select.selection match {
    case Nil => None
    case ids => Some(fn(ids))
  }

  implicit def fnToTableCellStyleGenerator(fn: (TItemId,  TPropertyId) => String ) =
    new Table.CellStyleGenerator {
      def getStyle(table: Table, itemId: AnyRef, propertyId: AnyRef) = fn(itemId, propertyId)
    }

  implicit def fnToTableColumnGenerator(fn: (Table, TItemId, TColumnId) => AnyRef) =
    new Table.ColumnGenerator {
      def generateCell(source: Table, itemId: TItemId, columnId: AnyRef) = fn(source, itemId, columnId)
    }

  implicit def wrapComponent(c: Component) = new ComponentWrapper(c)

  implicit def wrapComponentContainer(cc: ComponentContainer) = new ComponentContainerWrapper(cc)

  implicit def wrapCustomLayout(cl: CustomLayout) = new CustomLayoutWrapper(cl)

  implicit def wrapMenuBar(mb: MenuBar) = new MenuBarWrapper(mb)

  implicit def wrapMenuItem(mi: MenuBar#MenuItem) = new MenuItemWrapper(mi)

  implicit def wrapButton(button: Button) = new ButtonWrapper(button)

  implicit def wrapTree(tree: Tree) = new TreeWrapper(tree)

  implicit def wrapTable[A <: TItemId](table: Table with ContainerWithTypedItemId[A]) = new TableWrapper[A](table)

  implicit def wrapCheckBox(checkBox: CheckBox) = new CheckBoxWrapper(checkBox)

  implicit def wrapWindow(window: Window) = new WindowWrapper(window)

  implicit def wrapSizeable(sizeable: Sizeable) = new {
    def setSize(width: Float, height: Float, units: Sizeable.Unit = Sizeable.Unit.PIXELS) {
      sizeable.setWidth(width, units)
      sizeable.setHeight(height, units)
    }

    def setSize(width: String, height: String) {
      sizeable.setWidth(width)
      sizeable.setHeight(height)
    }
  }


  def updateDisabled[A <: Component](component: A)(fn: A => Unit) {
    component.setEnabled(true)
    try {
      fn(component)
    } finally {
      component.setEnabled(false)
    }
  }

  def updateReadOnly[A <: Component](component: A)(fn: A => Unit) {
    component.setReadOnly(false)
    try {
      fn(component)
    } finally {
      component.setReadOnly(true)
    }
  }


  /**
   * Ensures this button have no more than one click listener.
   */
  trait SingleClickListener extends Button {
    private val clickListenerRef = new AtomicReference(Option.empty[Button.ClickListener])

    override def addListener(listener: Button.ClickListener) {
      clickListenerRef.synchronized {
        for (currentListener <- clickListenerRef.getAndSet(listener.asOption)) {
          super.removeListener(currentListener)
        }

        super.addListener(listener)
      }
    }

    override def removeListener(listener: Button.ClickListener) {
      clickListenerRef.synchronized {
        for (currentListener <- clickListenerRef.get if currentListener eq listener) {
          super.removeListener(currentListener)
          clickListenerRef.set(None)
        }
      }
    }
  }

  trait LeftBottomAlignment { this: Layout.AlignmentHandler =>
    setDefaultComponentAlignment(Alignment.BOTTOM_LEFT)
  }


  trait MiddleLeftAlignment { this: Layout.AlignmentHandler =>
    setDefaultComponentAlignment(Alignment.MIDDLE_LEFT)
  }

  trait MiddleCenterAlignment { this: Layout.AlignmentHandler =>
    setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
  }

  trait NoChildrenAllowed extends Tree {
    override def addItem(itemId: AnyRef): Item = super.addItem(itemId) |>> { _ =>
      setChildrenAllowed(itemId, false)
    }
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

  trait ExposeValueChange[A] extends AbstractField[A] {
    override def fireValueChange(repaintIsNotNeeded: Boolean = true): Unit = super.fireValueChange(repaintIsNotNeeded)
  }

  trait OnceOnlyAttachAction extends AbstractComponent {

    var attachActionOpt = Option.empty[this.type => Unit]

    override def attach() {
      super.attach()
      attachActionOpt.foreach(action => action.apply(this))
      attachActionOpt = None
    }
  }

  /**
   * By default a fields does not fire ValueChangeEvent when assigned value equals to existing.
   * This traits overrides default behavior and always fires ValueChangeEvent on value change.
   */
  // todo: remove type ???
  trait AlwaysFireValueChange[T <: AnyRef] extends AbstractField[T] {
    override def setValue(value: T) {
      if (getValue == value) super.fireValueChange(true)
      else super.setValue(value)
    }
  }

  trait Margin { this: Layout.MarginHandler =>
    setMargin(true)
  }

  trait NoMargin { this: Layout.MarginHandler =>
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
    addStyleName(BaseTheme.BUTTON_LINK)
  }

  trait SmallStyle { this: Button =>
    addStyleName(Reindeer.BUTTON_SMALL)
  }

  trait SmallDefaultStyle { this: Button =>
    addStyleName(Reindeer.BUTTON_SMALL)
    addStyleName(Reindeer.BUTTON_DEFAULT)
  }

  trait MinimalStyle { this: TabSheet =>
    addStyleName(Reindeer.TABSHEET_MINIMAL)
  }

  trait TabSheetSmallStyle { this: TabSheet =>
    addStyleName(Reindeer.TABSHEET_SMALL)
  }


  trait LightStyle { this: Panel =>
    addStyleName(Reindeer.PANEL_LIGHT)
  }

  trait BorderlessStyle { this: Table =>
    addStyleName(Reindeer.TABLE_BORDERLESS)
  }

  trait MenuBarInTabStyle { this: MenuBar =>
    addStyleName("in-tab")
  }

  trait Immediate { this: AbstractComponent =>
    setImmediate(true)
  }

  trait Invisible { this: AbstractComponent =>
    setVisible(false)
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

  trait SelectWithTypedItemId[A <: TItemId] extends AbstractSelect with ContainerWithTypedItemId[A] {
    def addItem(id: A, caption: String, icon: Resource = null): Item = addItem(id) |>> { _ =>
      setItemCaption(id, caption)
      setItemIcon(id, icon)
    }

    def isSelected: Boolean = (getValue, isMultiSelect) match {
      case (value, false) => value != null
      case (ids: JCollection[_], true) => ids.size() != 0
      case _ => throw new AssertionError("Invalid content")
    }

    def selectFirst() {
      firstItemIdOpt.foreach(id => setValue(if (isMultiSelect) Collections.singletonList(id) else id))
    }

    def firstSelectedOpt: Option[A] = Option(firstSelected)

    def firstSelected: A = (getValue, isMultiSelect) match {
      case (value, false) => value.asInstanceOf[A]
      case (ids: JCollection[_], true) => (if (ids.size() == 0) null else ids.iterator().next()).asInstanceOf[A]
      case _ => throw new AssertionError("Invalid content")
    }


    def selection_=(v: A) {
      setValue(if (!isMultiSelect) v else if (v == null) Collections.emptyList() else Collections.singletonList(v))
    }

    def selection_=(v: Seq[A]) {
      setValue(if (!isMultiSelect) v.headOption.orNull else v.asJavaCollection)
    }

    def selection: Seq[A] = (getValue, isMultiSelect) match {
      case (value, false) => Option(value).toSeq.asInstanceOf[Seq[A]]
      case (ids: JCollection[_], true) => ids.asScala.toSeq.asInstanceOf[Seq[A]]
      case _ => throw new AssertionError("Invalid content")
    }

    def clearSelection() {
      setValue(if (isMultiSelect) Collections.emptyList() else null)
    }
  }


  trait SingleSelect[A <: TItemId] extends SelectWithTypedItemId[A] {
    setMultiSelect(false)

    override final def setMultiSelect(multiSelect: Boolean) {
      require(!multiSelect, "must be false")
      super.setMultiSelect(multiSelect)
    }
  }


  trait MultiSelect[A <: TItemId] extends SelectWithTypedItemId[A] {
    setMultiSelect(true)

    override final def setMultiSelect(multiSelect: Boolean) {
      require(multiSelect, "must be true")
      super.setMultiSelect(multiSelect)
    }
  }


  trait Now { this: DateField =>
    setValue(new java.util.Date)
  }

  trait YearResolution { this: DateField =>
    setResolution(Resolution.YEAR)
  }

  trait MonthResolution { this: DateField =>
    setResolution(Resolution.MONTH)
  }

  trait DayResolution { this: DateField =>
    setResolution(Resolution.DAY)
  }

  trait MinuteResolution { this: DateField =>
    setResolution(Resolution.MINUTE)
  }

  trait Required { this: Field[_] =>
    setRequired(true)
  }

  trait NoTextInput { this: ComboBox =>
    setTextInputAllowed(false)
  }
}