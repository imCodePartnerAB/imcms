package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.AbstractSelect
import com.imcode.imcms.vaadin.data.GenericContainer
import com.imcode.imcms.vaadin.Theme
import com.vaadin.server.ThemeResource

trait DocIdSelectWithLifeCycleIcon extends AbstractSelect with GenericContainer[DocId] with ImcmsServicesSupport {
  override def getItemIcon(itemId: AnyRef): ThemeResource = itemId.asInstanceOf[DocId] |> { docId =>
    imcmsServices.getDocumentMapper.getDocument(docId) |> Theme.Icon.Doc.phase
  }
}
