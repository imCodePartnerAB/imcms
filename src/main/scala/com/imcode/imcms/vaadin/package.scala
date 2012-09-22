package com.imcode
package imcms

import scala.collection.JavaConverters._

import com.vaadin.Application
import com.vaadin.data.Property.{ValueChangeNotifier, ValueChangeEvent, ValueChangeListener}
import com.vaadin.event.ItemClickEvent
import com.vaadin.data.{Item, Container, Property}
import com.vaadin.ui.Table.CellStyleGenerator
import com.vaadin.ui._
import com.vaadin.terminal.gwt.server.WebApplicationContext
import javax.servlet.http.HttpSession
import javax.servlet.ServletContext
import com.vaadin.ui.Window.Notification
import com.vaadin.data.Container.ItemSetChangeListener
import com.vaadin.terminal.{Resource, UserError, Sizeable}

package object vaadin {

  type PropertyId = AnyRef
  type PropertyValue = AnyRef
  type ItemId = AnyRef
  type ColumnId = AnyRef

  class ApplicationWrapper(app: Application) {

    def context(): WebApplicationContext = app.getContext.asInstanceOf[WebApplicationContext]

    def session(): HttpSession = context().getHttpSession

    def servletContext(): ServletContext = session().getServletContext

    def initAndShow[W <: Window](window: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true)(init: W => Unit) {
      init(window)
      window.setModal(modal)
      window.setResizable(resizable)
      window.setDraggable(draggable)
      app.getMainWindow.addWindow(window)
    }

    def show(window: Window, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true): Unit =
      initAndShow(window, modal, resizable, draggable) { _ => }

    def showNotification(caption: String, description: String, notificationType: Int): Unit =
      app.getMainWindow.showNotification(caption, description, notificationType)

    def showErrorNotification(caption: String, description: String = null): Unit =
      showNotification(caption, description, Notification.TYPE_ERROR_MESSAGE)

    def showWarningNotification(caption: String, description: String = null): Unit =
      showNotification(caption, description, Notification.TYPE_WARNING_MESSAGE)

    def showInfoNotification(caption: String, description: String = null): Unit =
      showNotification(caption, description, Notification.TYPE_HUMANIZED_MESSAGE)
  }

  /**
   * A container property.
   *
   * @param A container property class
   * @pram id container property id
   * @pram defaultValue container property default value
   */
  case class ContainerProperty[A <: PropertyValue : Manifest](id: AnyRef, defaultValue: A = null) {
    val clazz = implicitly[Manifest[A]].erasure
  }

  /**
   * Property value type.
   *
   * Adds type-checked access to property value.
   */
  trait GenericProperty[A <: PropertyValue] extends Property {
    def value = getValue.asInstanceOf[A]
    def value_=(v: A): Unit = setValue(v)

    def clear(implicit ev: A =:= String) { setValue("") }
    def trim(implicit ev: A =:= String): String = value.trim
    def trimOpt(implicit ev: A =:= String): Option[String] = trim match {
      case "" => None
      case v => Some(v)
    }
    def isBlank(implicit ev: A =:= String): Boolean = trim.isEmpty
    def notBlank(implicit ev: A =:= String): Boolean = !isBlank
  }

  case class FunctionProperty[A](valueFn: () => A)(implicit mf: Manifest[A]) extends Property {
    val isReadOnly = true
    val getType = mf.erasure
    def setValue(newValue: AnyRef) = throw new UnsupportedOperationException
    def setReadOnly(newStatus: Boolean): Unit = throw new UnsupportedOperationException
    def getValue = valueFn().asInstanceOf[AnyRef]
    override def toString = Option(getValue).map(_.toString).getOrElse("")
  }

//case class ByNameProperty[A >: Null <: AnyRef](byName: => A)(implicit mf: Manifest[A]) extends Property {
//
//  def setReadOnly(newStatus: Boolean) = throw new UnsupportedOperationException
//
//  val isReadOnly = true
//
//  val getType = mf.erasure
//
//  def setValue(newValue: AnyRef) = throw new UnsupportedOperationException
//
//  def getValue = byName //.asInstanceOf[AnyRef]
//
//  override def toString = ?(getValue) map { _.toString } getOrElse ""
//}

// add memoized byNameProperty

  trait NullableProperty[A <: PropertyValue] extends GenericProperty[A] {
    def valueOpt: Option[A] = Option(value)
  }

  trait GenericContainer[A <: ItemId] extends Container {
    def itemIds: JCollection[A] = getItemIds.asInstanceOf[JCollection[A]]
    def itemIds_=(ids: JCollection[A]) {
      removeAllItems()
      ids.asScala.foreach(addItem _)
    }

    def item(id: A): Item = getItem(id)

    def firstItemIdOpt: Option[A] = itemIds.asScala.headOption
  }



  /**
   * Component data type.
   *
   * Adds type-checked access to data.
   */
  trait GenericData[A <: AnyRef] extends AbstractComponent {
    def data: A = getData.asInstanceOf[A]
    def data_=(d: A) { setData(d) }
  }

  def menuCommand(handler: MenuBar#MenuItem => Unit) = new MenuBar.Command {
    def menuSelected(mi: MenuBar#MenuItem): Unit = handler(mi)
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
    properties.foreach { p =>
      container.addContainerProperty(p.id, p.clazz, p.defaultValue)
    }

  implicit def fnToTableCellStyleGenerator(fn: (ItemId,  PropertyId) => String ) =
    new Table.CellStyleGenerator {
      def getStyle(itemId: AnyRef, propertyId: AnyRef) = fn(itemId, propertyId)
    }

  implicit def fnToTableColumnGenerator(fn: (Table, ItemId, ColumnId) => AnyRef) =
    new Table.ColumnGenerator {
      def generateCell(source: Table, itemId: ItemId, columnId: AnyRef) = fn(source, itemId, columnId)
    }





//  def whenSelected[A, B](property: Property)(fn: A => B): Option[B] = property.getValue match {
//    case null => None
//    case value: A => Some(fn(value))
//    case other => sys.error("Unexpected field value: %s." format other)
//  }

  def whenSelected[A <: AnyRef, B](property: GenericProperty[A] with AbstractSelect)(fn: A => B): Option[B] = property.value match {
    case null => None
    case value: JCollection[_] if value.isEmpty => None
    case value => Some(fn(value))
  }

  // todo: selection, not sec ???
  def whenSingle[A, B](seq: Seq[A])(fn: A => B): Option[B] = seq match {
    case Seq(a) => Some(fn(a))
    case _ => None
  }





  implicit def applicationToImcmsApplication(app: Application) = app.asInstanceOf[ImcmsApplication]


  implicit def wrapApplication(app: Application) = new ApplicationWrapper(app)



  implicit def wrapValueChangeNotifier(vcn: Property.ValueChangeNotifier) = new {
    def addValueChangeListener(listener: Property.ValueChangeEvent => Unit): Unit =
      vcn.addListener(new Property.ValueChangeListener {
        def valueChange(event: ValueChangeEvent): Unit = listener(event)
      })

    def addValueChangeHandler(handler: => Unit): Unit = addValueChangeListener(_ => handler)
  }

  implicit def wrapItemClickNotifier(notifier: ItemClickEvent.ItemClickNotifier) = new {
    def addItemClickListener(listener: ItemClickEvent => Unit): Unit =
      notifier.addListener(new ItemClickEvent.ItemClickListener {
        def itemClick(event: ItemClickEvent): Unit = listener(event)
      })
  }

  trait ContainerItemSetChangeNotifier extends Container.ItemSetChangeNotifier { container: Container =>

    private var listeners = Set.empty[ItemSetChangeListener]

    def removeListener(listener: ItemSetChangeListener) {
      listeners -= listener
    }

    def addListener(listener: ItemSetChangeListener) {
      listeners += listener
    }

    protected def notifyItemSetChanged() {
      val event = new Container.ItemSetChangeEvent {
        def getContainer = container
      }

      listeners.foreach(_ containerItemSetChange event)
    }
  }


  /** Tree item descriptor */
  class TreeMenuItem(val id: String = null, val icon: Resource = null) {

    val children: Seq[TreeMenuItem] = {
      val isMenuItemType: Class[_] => Boolean = classOf[TreeMenuItem].isAssignableFrom

      getClass.getDeclaredMethods
        .filter(_.getReturnType |> isMenuItemType)
        .sortBy(_.getAnnotation(classOf[OrderedMethod]) |> opt map(_.value()) getOrElse 0)
        .map(_.invoke(this).asInstanceOf[TreeMenuItem])
    }
  }

  implicit def stringToUserError(string: String) = new UserError(string)
}