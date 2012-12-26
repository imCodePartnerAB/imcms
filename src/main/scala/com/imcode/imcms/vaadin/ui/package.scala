package com.imcode
package imcms
package vaadin

import scala.collection.JavaConverters._
import com.vaadin.ui._
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.terminal.{Resource, Sizeable}
import com.vaadin.ui.Layout.AlignmentHandler
import com.vaadin.data.{Property, Item}
import com.imcode.imcms.vaadin.data.{NullableProperty, GenericContainer, GenericProperty, ItemId}

package object ui {


  def whenSelected[A <: AnyRef, B](select: AbstractSelect with GenericProperty[A])(fn: A => B): Option[B] = select.value match {
    case null => None
    case value: JCollection[_] if value.isEmpty => None
    case value => Some(fn(value))
  }

  /**
   * Some Vaadin's components such as TextFields, Labels, etc can act as as wrappers for other
   * components of the same type.
   * Ensures setValue and getValue are always called directly on wrapped property, not on wrapper itself.
   */
  trait WrappedPropertyValue extends Property with Property.Viewer {
    abstract override def setValue(value: AnyRef): Unit = getPropertyDataSource match {
      case null => super.setValue(value)
      case property => property.setValue(value)
    }

    abstract override def getValue(): AnyRef = getPropertyDataSource match {
      case null => super.getValue()
      case property => property.getValue()
    }
  }

  implicit def wrapComponent(c: Component) = new ComponentWrapper(c)

  implicit def wrapComponentContainer(cc: ComponentContainer) = new ComponentContainerWrapper(cc)

  implicit def wrapCustomLayout(cl: CustomLayout) = new CustomLayoutWrapper(cl)

  implicit def wrapWindow(window: Window) = new WindowWrapper(window)

  implicit def wrapMenuBar(mb: MenuBar) = new MenuBarWrapper(mb)

  implicit def wrapMenuItem(mi: MenuBar#MenuItem) = new MenuItemWrapper(mi)

  implicit def wrapButton(button: Button) = new ButtonWrapper(button)

  implicit def wrapTable[A <: ItemId](table: Table with GenericContainer[A]) = new TableWrapper[A](table)

  /** Text field value type is always String */
  implicit def wrapTextField(textField: TextField) = new TextField(textField) with GenericProperty[String] with WrappedPropertyValue

  /** Password field value type is always String */
  implicit def wrapPasswordField(field: PasswordField) = new PasswordField(field) with GenericProperty[String] with WrappedPropertyValue

  /** Text area field value type is always String */
  implicit def wrapTextArea(textArea: TextArea) = new TextArea(textArea) with GenericProperty[String] with WrappedPropertyValue

  /** Label value type is always String */
  implicit def wrapLabel(label: Label) = new Label(label) with GenericProperty[String] with WrappedPropertyValue


  /** Checkbox value type is always JBoolean */
  implicit def wrapCheckBox(checkBox: CheckBox) = new CheckBox("", checkBox) with CheckBoxWrapper with GenericProperty[JBoolean] with WrappedPropertyValue

  /** Date field value type is always Date */
  implicit def wrapDateField(dateField: DateField) = new DateField(dateField) with NullableProperty[java.util.Date] with WrappedPropertyValue

  implicit def wrapSizeable(sizeable: Sizeable) = new {
    def setSize(width: Float, height: Float, units: Int = Sizeable.UNITS_PIXELS) {
      sizeable.setWidth(width, units)
      sizeable.setHeight(height, units)
    }

    def setSize(width: String, height: String) {
      sizeable.setWidth(width)
      sizeable.setHeight(height)
    }
  }


  trait CheckBoxWrapper { this: CheckBox =>
    def isChecked: Boolean = checked
    def isUnchecked: Boolean = !isChecked

    def checked: Boolean = booleanValue
    def checked_=(value: Boolean): Unit = setValue(value.asInstanceOf[AnyRef])

    def check() { checked = true }
    def uncheck() { checked = false }
  }


  def updateDisabled[A <: Component](component: A)(f: A => Unit) {
    component.setEnabled(true)
    try {
      f(component)
    } finally {
      component.setEnabled(false)
    }
  }

  def updateReadOnly[A <: Component](component: A)(f: A => Unit) {
    component.setReadOnly(false)
    try {
      f(component)
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
        for (currentListener <- clickListenerRef.getAndSet(listener |> opt)) {
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

  @deprecated
  trait LightStyle { this: Panel =>
    setStyleName(Panel.STYLE_LIGHT)
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

  trait GenericSelect[A <: ItemId] extends AbstractSelect with GenericContainer[A] {
    def addItem(id: A, caption: String, icon: Resource = null): Item = addItem(id) |>> { _ =>
      setItemCaption(id, caption)
      setItemIcon(id, icon)
    }

    def isSelected: Boolean
  }


  trait SingleSelect[A <: ItemId] extends GenericSelect[A] with NullableProperty[A] {
    setMultiSelect(false)

    def isSelected = value != null

    def selection = value

    def selection_=(v: A) { value = v }

    def selectionOpt = valueOpt

    override final def setMultiSelect(multiSelect: Boolean) {
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

    def selection_=(v: A) { selection = Option(v).toSeq }

    def firstOpt: Option[A] = value.asScala.headOption
  }




  /**
   * <code>value<code> property always returns a collection.
   */
  trait MultiSelectBehavior[A <: ItemId] extends GenericSelect[A] with GenericProperty[JCollection[A]] {

    def selection: Seq[A] = value.asScala.toSeq

    def selection_=(v: Seq[A]) { value = v.asJava }

    def selection_=(v: A) { selection = Option(v).toSeq }

    def firstOpt: Option[A] = value.asScala.headOption

    def isSelected: Boolean = value.asScala.nonEmpty

    override def value: JCollection[A] = getValue |> { v =>
      if (isMultiSelect) v.asInstanceOf[JCollection[A]] else Option(v.asInstanceOf[A]).toSeq.asJavaCollection
    }

    override def value_=(v: JCollection[A]): Unit = super.setValue(if (isMultiSelect) v else v.asScala.headOption.orNull)

    //  /**
    //   * @return collection of selected items or empty collection if there is no selected item(s).
    //   */
    //  final override def getValue(): AnyRef = super.getValue |> { v =>
    //    println (">>>>>>>>>> " + v)
    //    val x = if (isMultiSelect) v else Option(v).toSeq.asJavaCollection
    //    x
    //  }
    //
    //  final override def setMultiSelect(multiSelect: Boolean) =
    //    if (value.isEmpty) super.setMultiSelect(multiSelect)
    //    else throw new IllegalStateException("Multi-select value can not be changed on non-empty select.")
    //
    //
    //  final override def setValue(v: AnyRef) {
    //    super.setValue(
    //      v match {
    //        case null => if (isMultiSelect) Collections.emptyList[AnyRef] else null
    //        case coll: JCollection[_] => if (isMultiSelect) coll else coll.asScala.headOption.orNull
    //        case _ => if (isMultiSelect) Collections.singletonList(v) else v
    //      }
    //    )
    //  }
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
}