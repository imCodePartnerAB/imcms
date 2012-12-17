package com.imcode
package imcms

import com.vaadin.Application
import com.vaadin.event.ItemClickEvent
import com.vaadin.ui._
import com.vaadin.terminal.UserError
import com.imcode.imcms.vaadin.data.{ItemId, PropertyId, ColumnId}

package object vaadin {

  implicit def stringToUserError(string: String) = new UserError(string)

  def menuCommand(handler: MenuBar#MenuItem => Unit) = new MenuBar.Command {
    def menuSelected(mi: MenuBar#MenuItem): Unit = handler(mi)
  }

  implicit def fn0ToMenuCommand(f: () => Unit) = menuCommand { _ => f() }


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

  implicit def wrapItemClickNotifier(notifier: ItemClickEvent.ItemClickNotifier) = new {
    def addItemClickListener(listener: ItemClickEvent => Unit): Unit =
      notifier.addListener(new ItemClickEvent.ItemClickListener {
        def itemClick(event: ItemClickEvent): Unit = listener(event)
      })
  }
}