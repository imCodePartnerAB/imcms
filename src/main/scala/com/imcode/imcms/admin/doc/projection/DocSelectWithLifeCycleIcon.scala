package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.AbstractSelect
import com.imcode.imcms.vaadin.data.GenericContainer
import com.imcode.imcms.vaadin.Theme
import _root_.imcode.server.document.DocumentDomainObject


trait DocSelectWithLifeCycleIcon extends AbstractSelect with GenericContainer[DocumentDomainObject] {
  override def getItemIcon(itemId: AnyRef) = itemId.asInstanceOf[DocumentDomainObject] |> Theme.Icon.Doc.phase
}
