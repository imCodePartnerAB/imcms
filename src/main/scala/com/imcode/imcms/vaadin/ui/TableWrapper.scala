package com.imcode.imcms.vaadin.ui

import com.imcode.imcms.vaadin.data.{ContainerWithTypedItemId, TItemId, TColumnId}
import com.vaadin.ui.Table

// implicit
class TableWrapper[A <: TItemId](table: Table with ContainerWithTypedItemId[A]) {
  def addRow(itemId: A, column: AnyRef, columns: AnyRef*): AnyRef = table.addItem((column +: columns).toArray, itemId)
  def addRowWithAutoId(column: AnyRef, columns: AnyRef*): AnyRef = table.addItem((column +: columns).toArray, null)

  object generatedColumn {
    def update(columnId: TColumnId, generator: (Table, A, TColumnId) => AnyRef) {
      table.addGeneratedColumn(columnId,
        new Table.ColumnGenerator {
          def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = generator(source, itemId.asInstanceOf[A], columnId)
        }
      )
    }
  }

  def columnHeaders: Seq[String] = table.getColumnHeaders.toSeq
  def columnHeaders_=(headers: Seq[String]) { table.setColumnHeaders(headers.toArray: _*) }
}