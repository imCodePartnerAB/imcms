package com.imcode
package imcms
package vaadin

import scala.language.implicitConversions

import scala.collection.JavaConverters._
import com.vaadin.ui._
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.ui.Layout.AlignmentHandler
import com.vaadin.data.{Item, Property}
import com.imcode.imcms.vaadin.data._
import com.vaadin.server.{Sizeable, Resource}
import com.vaadin.ui.themes.{Reindeer, ChameleonTheme, BaseTheme}
import com.vaadin.shared.ui.datefield.Resolution


package object component {

  implicit def wrapUI(ui: UI): UIWrapper = new UIWrapper(ui)

  def whenSelected[A <: AnyRef, B](select: AbstractSelect with TypedProperty[A])(fn: A => B): Option[B] = select.value match {
    case null => None
    case value: JCollection[_] if value.isEmpty => None
    case value => Some(fn(value))
  }

//  def menuCommand(handler: (MenuBar#MenuItem => Unit)) = new MenuBar.Command {
//    def menuSelected(mi: MenuBar#MenuItem): Unit = handler(mi)
//  }
//
//  implicit def fn0ToMenuCommand(fn: () => Unit) = menuCommand { _ => fn() }


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

  trait ContainerWithDefaultAlignment extends Layout with AlignmentHandler {

    protected def defaultAlignment: Alignment

    abstract override def addComponent(c: Component) {
      super.addComponent(c)
      setComponentAlignment(c, defaultAlignment)
    }
  }


  trait LeftBottomAlignment extends ContainerWithDefaultAlignment {
    protected val defaultAlignment = Alignment.BOTTOM_LEFT
  }


  trait MiddleLeftAlignment extends ContainerWithDefaultAlignment {
    protected val defaultAlignment = Alignment.MIDDLE_LEFT
  }

  trait MiddleCenterAlignment extends ContainerWithDefaultAlignment {
    protected val defaultAlignment = Alignment.MIDDLE_CENTER
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
  // todo: remove type
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
    setStyleName(BaseTheme.BUTTON_LINK)
  }

  trait SmallStyle { this: Button =>
    addStyleName(Reindeer.BUTTON_SMALL)
  }

  trait SmallDefaultStyle { this: Button =>
    addStyleName(Reindeer.BUTTON_SMALL)
    addStyleName(Reindeer.BUTTON_DEFAULT)
  }



  trait LightStyle { this: Panel =>
    setStyleName(Reindeer.PANEL_LIGHT)
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

    // todo: move to sel ops
    def isSelected: Boolean
    def selectFirst(): Unit
  }


  trait SingleSelect[A <: TItemId] extends SelectWithTypedItemId[A] with TypedProperty[A] {
    setMultiSelect(false)

    override final def setMultiSelect(multiSelect: Boolean) {
      require(!multiSelect, "must be false")
      super.setMultiSelect(multiSelect)
    }

    override def isSelected: Boolean = selectionOpt.isDefined

    override def selectFirst() {
      firstItemIdOpt.foreach(id => selection = id)
    }

    // remove??? use value instead ???
    def selection: A = getTypedValue

    def selection_=(v: A) { setValue(v) }

    def selectionOpt: Option[A] = getTypedValue.asOption
  }


  trait MultiSelect[A <: TItemId] extends SelectWithTypedItemId[A] with TypedProperty[JCollection[A]] {
    setMultiSelect(true)

    override def setMultiSelect(multiSelect: Boolean) {
      require(multiSelect, "must be true")
      super.setMultiSelect(multiSelect)
    }

    override def isSelected: Boolean = getTypedValue.asScala.nonEmpty

    override def selectFirst() {
      selection = firstItemIdOpt.toSeq
    }

    def selection: Seq[A] = getTypedValue.asScala.toSeq

    def selection_=(v: Seq[A]) { setValue(v.asJava) }

    def selection_=(v: A) { selection = Option(v).toSeq }

    def firstOpt: Option[A] = getTypedValue.asScala.headOption
  }


  /**
   * <code>value<code> property always returns a collection.
   */
  // todo: ??? Multiselect'Read'Behavior ???
  trait MultiSelectBehavior[A <: TItemId] extends SelectWithTypedItemId[A] with TypedProperty[JCollection[A]] {

    def selection: Seq[A] = getTypedValue.asScala.toSeq
    def selection_=(v: Seq[A]) { setValue(v.asJava) }

    override def isSelected: Boolean = getTypedValue.asScala.nonEmpty

    override def selectFirst() {
      selection = firstItemIdOpt.toSeq
    }

    /**
     * @return collection of selected items or empty collection if there is no selected item(s).
     */
    final override def getValue: JCollection[A] = super.getValue |> { value =>
      if (isMultiSelect) value.asInstanceOf[JCollection[A]]
      else value.asInstanceOf[A].asOption.toSeq.asJavaCollection
    }

    final override def setValue(value: AnyRef) {
      super.setValue(
        value match {
          case null => if (isMultiSelect) java.util.Collections.emptyList else null
          case coll: JCollection[_] => if (isMultiSelect) value else coll.asScala.headOption.orNull
          case _ => if (isMultiSelect) java.util.Collections.singletonList(value) else value
        }
      )
    }

    //  ?????????????????????????????????????????????????????????
    //  final override def setMultiSelect(multiSelect: Boolean) =
    //    if (value.isEmpty) super.setMultiSelect(multiSelect)
    //    else throw new IllegalStateException("Multi-select value can not be changed on non-empty select.")
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