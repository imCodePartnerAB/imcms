package com.imcode
package imcms
package admin.doc.projection.container

import com.vaadin.ui.Table
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui.{Immediate, Selectable, MultiSelectBehavior}
import com.imcode.imcms.admin.doc.projection.container.IndexedDocsContainer
import com.imcode.imcms._


class IndexedDocsUI(container: IndexedDocsContainer) extends Table(null, container)
with MultiSelectBehavior[Index]
with Selectable with Immediate {

  setColumnHeaders(PropertyId.values().map(propertyId => propertyId.toString.i): _*)

  setColumnCollapsingAllowed(false)

  addStyleName("striped")
}
