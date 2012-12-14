package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.Table
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui.{Immediate, Selectable, MultiSelectBehavior}


class DocsUI(container: FilterableDocsContainer) extends Table(null, container)
    with MultiSelectBehavior[DocId]
    with DocIdSelectWithLifeCycleIcon with Selectable with Immediate {
    //with DocTableItemIcon with Selectable with Immediate {

  setColumnCollapsingAllowed(true)
  setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY)

  setColumnHeaders(container.getContainerPropertyIds.asScala.map(_.toString.i).toArray)
  Seq("doc.tbl.col.parents", "doc.tbl.col.children").foreach { setColumnCollapsed(_, true) }
}
