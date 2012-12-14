package com.imcode
package imcms

import scala.collection.JavaConverters._

import com.vaadin.Application
import com.vaadin.data.Property.{ValueChangeNotifier, ValueChangeEvent, ValueChangeListener}
import com.vaadin.event.ItemClickEvent
import com.vaadin.data.{Item, Container, Property}
import com.vaadin.ui.Table.CellStyleGenerator
import com.vaadin.ui._
import com.vaadin.data.Container.ItemSetChangeListener
import com.vaadin.terminal.{Resource, UserError, Sizeable}
import com.imcode.imcms.vaadin.data.{GenericProperty, PropertyDescriptor}

package object vaadin {

  type PropertyId = AnyRef
  type PropertyValue = AnyRef
  type ItemId = AnyRef
  type ColumnId = AnyRef


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

  def addContainerProperties(container: Container, descriptors: PropertyDescriptor[_]*): Unit =
    descriptors.foreach { pd =>
      container.addContainerProperty(pd.id, pd.clazz, pd.defaultValue)
    }

  implicit def fnToTableCellStyleGenerator(fn: (ItemId,  PropertyId) => String ) =
    new Table.CellStyleGenerator {
      def getStyle(itemId: AnyRef, propertyId: AnyRef) = fn(itemId, propertyId)
    }

  implicit def fnToTableColumnGenerator(fn: (Table, ItemId, ColumnId) => AnyRef) =
    new Table.ColumnGenerator {
      def generateCell(source: Table, itemId: ItemId, columnId: AnyRef) = fn(source, itemId, columnId)
    }




  implicit def applicationToImcmsApplication(app: Application): ImcmsApplication = app.asInstanceOf[ImcmsApplication]


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


  implicit def stringToUserError(string: String) = new UserError(string)
}