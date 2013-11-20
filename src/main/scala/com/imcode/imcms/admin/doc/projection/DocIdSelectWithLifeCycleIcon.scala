package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.AbstractSelect
import com.imcode.imcms.vaadin.data.{ContainerWithTypedItemId}
import com.imcode.imcms.vaadin.component.Theme
import com.vaadin.server.ThemeResource
import imcode.server.document.DocumentDomainObject

trait DocIdSelectWithLifeCycleIcon extends AbstractSelect with ContainerWithTypedItemId[DocId] with ImcmsServicesSupport {
  override def getItemIcon(itemId: AnyRef): ThemeResource = itemId.asInstanceOf[DocId] |> { docId =>
    imcmsServices.getDocumentMapper.getDocument[DocumentDomainObject](docId) |> Theme.Icon.Doc.phase
  }
}
