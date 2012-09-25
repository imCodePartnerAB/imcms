package com.imcode
package imcms
package vaadin

import scala.collection.JavaConverters._
import scala.util.control.{Exception => Ex}
import com.vaadin.ui._
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.Application
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.terminal.{Resource, Sizeable, ThemeResource, UserError}
import com.vaadin.ui.Window.Notification
import com.imcode.imcms.security._
import com.vaadin.data.Container.ItemSetChangeListener
import com.vaadin.data.Container.ItemSetChangeListener._
import com.vaadin.ui.Layout.AlignmentHandler
import javax.servlet.http.HttpSession
import javax.servlet.ServletContext
import scala.annotation.tailrec
import com.vaadin.data.{Property, Item, Container}
import com.vaadin.data.Property.Viewer._

package object ui {

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
  implicit def wrapCheckBox(checkBox: CheckBox) = new CheckBox("", checkBox) with CheckBoxExt with GenericProperty[JBoolean] with WrappedPropertyValue

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


  /** Convenient extension */
  trait CheckBoxExt { this: CheckBox =>
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

  class WindowWrapper(window: Window) {
    def initAndShow[W <: Window](childWindow: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true)(init: W => Unit) {
      init(childWindow)
      childWindow.setModal(modal)
      childWindow.setResizable(resizable)
      childWindow.setDraggable(draggable)
      window.addWindow(childWindow)
    }

    def show(window: Window, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true): Unit =
      initAndShow(window, modal, resizable, draggable) { _ => }

    def showNotification(caption: String, description: String, notificationType: Int): Unit =
      window.showNotification(caption, description, notificationType)

    def showErrorNotification(caption: String, description: String = null): Unit =
      window.showNotification(caption, description, Notification.TYPE_ERROR_MESSAGE)

    def showWarningNotification(caption: String, description: String = null): Unit =
      window.showNotification(caption, description, Notification.TYPE_WARNING_MESSAGE)

    def showInfoNotification(caption: String, description: String = null): Unit =
      window.showNotification(caption, description, Notification.TYPE_HUMANIZED_MESSAGE)
  }

  class MenuBarWrapper(mb: MenuBar) {
    def addItem(caption: String, resource: Resource): MenuBar#MenuItem = mb.addItem(caption, resource, null)
    def addItem(caption: String): MenuBar#MenuItem = mb.addItem(caption, null)
  }

  class MenuItemWrapper(mi: MenuBar#MenuItem) {
    def addItem(caption: String, resource: Resource): MenuBar#MenuItem = mi.addItem(caption, resource, null)
    def addItem(caption: String): MenuBar#MenuItem = mi.addItem(caption, null)

    def setCommandListener(listener: MenuBar#MenuItem => Unit): Unit =
      mi.setCommand(new MenuBar.Command {
        def menuSelected(mi: MenuBar#MenuItem): Unit = listener(mi)
      })

    def setCommandHandler(handler: => Unit): Unit = setCommandListener(_ => handler)
  }


  class ComponentWrapper(component: Component) {
    def topWindow: Window = {
      @tailrec def findTopWindowOf(window: Window): Window = window.getParent match {
        case null => window
        case parent => findTopWindowOf(parent)
      }

      component.getWindow match {
        case null => null
        case window => findTopWindowOf(window)
      }
    }
  }

  class ButtonWrapper(button: Button) {

    def addClickListener(listener: Button#ClickEvent => Unit): Unit =
      button.addListener(new Button.ClickListener {
        def buttonClick(event: Button#ClickEvent): Unit = listener(event)
      })

    def addClickHandler(handler: => Unit): Unit = addClickListener(_ => handler)
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


  class TableWrapper[A <: ItemId](table: Table with GenericContainer[A]) {
    def addRow(itemId: A, cells: AnyRef*): AnyRef = table.addItem(cells.toArray[AnyRef], itemId)
    def addRowWithAutoId(cell: AnyRef, cells: AnyRef*): AnyRef = addRow(null.asInstanceOf[A], (cell +: cells) : _*)

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

  trait ContainerWithDefaultAlignment extends ComponentContainer with AlignmentHandler {

    protected def defaultAlignment: Alignment

    abstract override def addComponent(c: Component) {
      super.addComponent(c)
      setComponentAlignment(c, defaultAlignment)
    }
  }


  trait LeftBottomAlignment { this: ContainerWithDefaultAlignment =>
    protected val defaultAlignment = Alignment.BOTTOM_LEFT
  }


  trait MiddleLeftAlignment { this: ContainerWithDefaultAlignment =>
    protected val defaultAlignment = Alignment.MIDDLE_LEFT
  }


  trait NoChildrenAllowed extends Tree {
    override def addItem(itemId: AnyRef): Item = super.addItem(itemId) |>> { _ =>
      setChildrenAllowed(itemId, false)
    }
  }


  /**
   * Reload button is placed under the content with right alignment.
   */
  class ReloadableContentUI[T <: Component](val content: T) extends GridLayout(1,2) with Spacing {
    import com.imcode.imcms.vaadin.Theme.Icon._

    val btnReload = new Button("Reload") with LinkStyle {
      setIcon(Reload16)
    }

    addComponentsTo(this, content, btnReload)
    setComponentAlignment(content, Alignment.TOP_LEFT)
    setComponentAlignment(btnReload, Alignment.BOTTOM_RIGHT)
  }


  object SearchFormUtil {
    def toggle(layout: CustomLayout, name: String, checkBox: CheckBox, component: Component,
               stub: => Component = { new Label("search.frm.fld.lbl_any_value".i) with UndefinedSize }) {

      layout.addComponent(if (checkBox.checked) component else stub, name)
    }
  }
}