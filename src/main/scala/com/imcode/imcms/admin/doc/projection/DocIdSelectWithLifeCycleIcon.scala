package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.AbstractSelect
import com.vaadin.terminal.ThemeResource
import com.imcode.imcms.vaadin.data.GenericContainer
import com.imcode.imcms.vaadin.Theme

trait DocIdSelectWithLifeCycleIcon extends AbstractSelect with GenericContainer[DocId] with ImcmsServicesSupport {
  override def getItemIcon(itemId: AnyRef): ThemeResource = itemId.asInstanceOf[DocId] |> { docId =>
    imcmsServices.getDocumentMapper.getDocument(docId) |> Theme.Icon.Doc.phase
  }
}
