package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.AbstractSelect
import com.vaadin.terminal.ThemeResource
import _root_.imcode.server.document.DocumentDomainObject
import com.imcode.imcms.vaadin.data.GenericContainer


trait DocSelectWithLifeCycleIcon extends AbstractSelect with GenericContainer[DocumentDomainObject] {
  override def getItemIcon(itemId: AnyRef) = itemId.asInstanceOf[DocumentDomainObject] |> { doc =>
    new ThemeResource("icons/docstatus/%s.gif".format(doc.getLifeCyclePhase.toString))
  }
}
