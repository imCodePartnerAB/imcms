package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.Table
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui.{Immediate, Selectable, MultiSelectBehavior}


class IndexedDocsUI(container: IndexedDocsContainer) extends Table(null, container)
    with MultiSelectBehavior[Ix]
    with Selectable with Immediate {

  setColumnHeaders(container.getContainerPropertyIds.asScala.map(_.i).toArray)

  setColumnCollapsingAllowed(true)

  Seq("docs_projection.container_property.parents", "docs_projection.container_property.children").foreach {
    setColumnCollapsed(_, true)
  }
}
