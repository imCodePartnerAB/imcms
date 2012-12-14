package com.imcode.imcms.vaadin.ui

import com.imcode.imcms.vaadin._
import com.vaadin.ui.Table
import com.imcode.imcms.vaadin.data.GenericContainer

// implicit
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