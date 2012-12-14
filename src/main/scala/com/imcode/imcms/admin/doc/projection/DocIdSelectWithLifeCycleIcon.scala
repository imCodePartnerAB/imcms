package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.AbstractSelect
import com.vaadin.terminal.ThemeResource
import com.imcode.imcms.vaadin.data.GenericContainer

trait DocIdSelectWithLifeCycleIcon extends AbstractSelect with GenericContainer[DocId] with ImcmsServicesSupport {
  override def getItemIcon(itemId: AnyRef): ThemeResource = itemId.asInstanceOf[DocId] |> { docId =>
    imcmsServices.getDocumentMapper.getDocument(docId) match {
      case null => null
      case doc => new ThemeResource("icons/docstatus/%s.gif".format(doc.getLifeCyclePhase.toString))
    }
  }
}
