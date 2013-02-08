package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.AbstractSelect
import com.imcode.imcms.vaadin.data.{ContainerWithTypedItemId}
import com.imcode.imcms.vaadin.ui.Theme
import com.vaadin.server.ThemeResource

trait DocIdSelectWithLifeCycleIcon extends AbstractSelect with ContainerWithTypedItemId[DocId] with ImcmsServicesSupport {
  override def getItemIcon(itemId: AnyRef): ThemeResource = itemId.asInstanceOf[DocId] |> { docId =>
    imcmsServices.getDocumentMapper.getDocument(docId) |> Theme.Icon.Doc.phase
  }
}
