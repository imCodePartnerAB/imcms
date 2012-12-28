package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.Table
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui.{Immediate, Selectable, MultiSelectBehavior}
import com.vaadin.terminal.ThemeResource


class IndexedDocsUI(container: IndexedDocsContainer) extends Table(null, container)
    with MultiSelectBehavior[Ix]
    with Selectable with Immediate {

  setColumnCollapsingAllowed(true)
  setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY)

  setColumnHeaders(container.getContainerPropertyIds.asScala.map(_.i).toArray)
  Seq("docs_projection.tbl_column_name.parents", "docs_projection.tbl_column_name.children").foreach { setColumnCollapsed(_, true) }

  override def getItemIcon(itemId: AnyRef): ThemeResource = container.getItem(itemId) match {
    case null => null
    case docItem => new ThemeResource("icons/docstatus/%s.gif".format(docItem.doc.getLifeCyclePhase.toString))
  }
}
