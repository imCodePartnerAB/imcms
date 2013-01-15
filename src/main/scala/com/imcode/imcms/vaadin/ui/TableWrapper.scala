package com.imcode.imcms.vaadin.ui

import com.imcode.imcms.vaadin.data.{ContainerWithGenericItemId, TItemId, TColumnId}
import com.vaadin.ui.Table

// implicit
class TableWrapper[A <: TItemId](table: Table with ContainerWithGenericItemId[A]) {
  def addRow(itemId: A, cells: AnyRef*): AnyRef = table.addItem(cells.toArray, itemId)
  def addRowWithAutoId(cell: AnyRef, cells: AnyRef*): AnyRef = addRow(null.asInstanceOf[A], (cell +: cells) : _*)

  object generatedColumn {
    def update(columnId: TColumnId, generator: (Table, A, TColumnId) => AnyRef) {
      table.addGeneratedColumn(columnId,
        new Table.ColumnGenerator {
          def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = generator(source, itemId.asInstanceOf[A], columnId)
        }
      )
    }
  }

  def columnHeaders = table.getColumnHeaders.toSeq
  def columnHeaders_=(headers: Seq[String]) { table.setColumnHeaders(headers.toArray) }
}