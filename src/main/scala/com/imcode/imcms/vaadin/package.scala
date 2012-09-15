package com.imcode
package imcms

import com.vaadin.Application
import com.vaadin.data.Property.{ValueChangeNotifier, ValueChangeEvent, ValueChangeListener}
import com.vaadin.event.ItemClickEvent
import com.vaadin.data.{Item, Container, Property}
import com.vaadin.terminal.{UserError, Sizeable}
import com.vaadin.ui.Table.CellStyleGenerator
import com.vaadin.ui._

package object vaadin {

  def menuCommand(handler: MenuBar#MenuItem => Unit) = new MenuBar.Command {
    def menuSelected(mi: MenuBar#MenuItem) = handler(mi)
  }

  implicit def fn0ToMenuCommand(f: () => Unit) = menuCommand { _ => f() }

  def addComponentsTo(container: ComponentContainer, component: Component, components: Component*) = {
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


  type ItemId = AnyRef
  type PropertyId = AnyRef
  type ColumnId = AnyRef

  // table.properties = CP[Int]("id", "name") ~ CP[String]("id", "name") ~ ... ???

  implicit def fnToTableCellStyleGenerator(fn: (ItemId,  PropertyId) => String ) =
    new Table.CellStyleGenerator {
      def getStyle(itemId: AnyRef, propertyId: AnyRef) = fn(itemId, propertyId)
    }

  implicit def fnToTableColumnGenerator(fn: (Table, ItemId, ColumnId) => AnyRef) =
    new Table.ColumnGenerator {
      def generateCell(source: Table, itemId: ItemId, columnId: AnyRef) = fn(source, itemId, columnId)
    }

  class TableOps[A <: ItemId](table: Table with ItemIdType[A]) {
    def addRow(itemId: A, cells: AnyRef*): AnyRef = addTableRow(table, itemId, cells: _*)
    def addRowWithAutoId(cell: AnyRef, cells: AnyRef*): AnyRef = addTableRow(table, null, (cell +: cells) : _*)

    object generatedColumn {
      def update(columnId: ColumnId, generator: (Table, A, ColumnId) => AnyRef) {
        table.addGeneratedColumn(columnId, new Table.ColumnGenerator {
          def generateCell(source: Table, itemId: ItemId, columnId: AnyRef) = generator(source, itemId.asInstanceOf[A], columnId)
        })
      }
    }

    def columnHeaders = table.getColumnHeaders.toList
    def columnHeaders_=(headers: List[String]) { table setColumnHeaders headers.toArray }
  }

  implicit def tableOps[A <: ItemId](table: Table with ItemIdType[A]) = new TableOps[A](table)


  def addTableRow(table: Table, itemId: AnyRef, cells: AnyRef*): AnyRef = table.addItem(cells.toArray[AnyRef], itemId)

//  def whenSelected[A, B](property: Property)(fn: A => B): Option[B] = property.getValue match {
//    case null => None
//    case value: A => Some(fn(value))
//    case other => sys.error("Unexpected field value: %s." format other)
//  }

  def whenSelected[A <: AnyRef, B](property: ValueType[A] with AbstractSelect)(fn: A => B): Option[B] = property.value match {
    case null => None
    case value: JCollection[_] if value.isEmpty => None
    case value => Some(fn(value))
  }

  def whenSingle[A, B](seq: Seq[A])(fn: A => B): Option[B] = seq match {
    case Seq(a) => Some(fn(a))
    case _ => None
  }

  /** Convenient extension */
  trait CheckBoxOps { this: CheckBox =>
    def isChecked = checked
    def isUnchecked = !isChecked

    def checked = booleanValue
    def checked_=(value: Boolean) = setValue(value.asInstanceOf[AnyRef])

    def check() { checked = true }
    def uncheck() { checked = false }
  }

  /** Ensures setValue is called directly on wrapped property, and not on wrapper itself. */
  trait WrappedPropertyValueSetterDelegate extends Property with Property.Viewer {
    abstract override def setValue(value: AnyRef) = getPropertyDataSource match {
      case null => super.setValue(value)
      case property => property.setValue(value)
    }
  }

  /** Text field value type is always String */
  implicit def wrapTextField(textField: TextField) = new TextField(textField) with ValueType[String] with WrappedPropertyValueSetterDelegate

  /** Password field value type is always String */
  implicit def wrapPasswordField(field: PasswordField) = new PasswordField(field) with ValueType[String] with WrappedPropertyValueSetterDelegate

  /** Text area field value type is always String */
  implicit def wrapTextArea(textArea: TextArea) = new TextArea(textArea) with ValueType[String] with WrappedPropertyValueSetterDelegate

  /** Label value type is always String */
  implicit def wrapLabel(label: Label) = new Label(label) with ValueType[String] with WrappedPropertyValueSetterDelegate


  /** Checkbox value type is always JBoolean */
  implicit def wrapCheckBox(checkBox: CheckBox) = new CheckBox("", checkBox) with CheckBoxOps with ValueType[JBoolean] with WrappedPropertyValueSetterDelegate

  /** Date field value type is always Date */
  implicit def wrapDateField(dateField: DateField) = new DateField(dateField) with ValueType[java.util.Date]


  implicit def applicationToImcmsApplication(app: Application) = app.asInstanceOf[ImcmsApplication]

  implicit def wrapComponent(c: Component) = new ComponentWrapper(c)

  implicit def wrapApplication(app: Application) = new ApplicationWrapper(app)

  implicit def wrapWindow(window: Window) = new WindowWrapper(window)

  implicit def wrapMenuBar(mb: MenuBar) = new MenuBarWrapper(mb)

  implicit def wrapMenuItem(mi: MenuBar#MenuItem) = new MenuItemWrapper(mi)

  implicit def wrapButton(button: Button) = new ButtonWrapper(button)


  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier) = new {
    def addValueChangeListener(listener: Property.ValueChangeEvent => Unit) =
      vcn.addListener(new Property.ValueChangeListener {
        def valueChange(event: ValueChangeEvent) = listener(event)
      })

    def addValueChangeHandler(handler: => Unit) = addValueChangeListener(_ => handler)
  }

  implicit def wrapItemClickNotifier(notifier: ItemClickEvent.ItemClickNotifier) = new {
    def addItemClickListener(listener: ItemClickEvent => Unit) =
      notifier.addListener(new ItemClickEvent.ItemClickListener {
        def itemClick(event: ItemClickEvent) = listener(event)
      })
  }


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

  object SearchFormUtil {
    def toggle(layout: CustomLayout, name: String, checkBox: CheckBox, component: Component,
               stub: => Component = { new Label("search.frm.fld.lbl_any_value".i) with UndefinedSize }) {

      layout.addComponent(if (checkBox.checked) component else stub, name)
    }
  }

  // avstract component ops
  //implicit def wrapAbstractComponent(c: AbstractComponent) = new {
  //  def set
  //}
  implicit def strToUserError(str: String) = new UserError(str)
}