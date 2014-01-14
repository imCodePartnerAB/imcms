package com.imcode
package imcms
package admin.doc.projection.container

import com.vaadin.ui.Table
import com.imcode.imcms.vaadin.component.{Immediate, Selectable, MultiSelectBehavior}
import com.imcode.imcms._


class IndexedDocsView(container: IndexedDocsContainer) extends Table(null, container)
with MultiSelectBehavior[Index]
with Selectable with Immediate {

  setColumnHeaders(PropertyId.values.map(propertyId => propertyId.toString.i): _*)

  setColumnCollapsingAllowed(false)

  addStyleName("striped borderless")
}
