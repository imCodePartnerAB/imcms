package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.AbstractSelect
import com.imcode.imcms.vaadin.data.{ContainerWithGenericItemId}
import com.imcode.imcms.vaadin.ui.Theme
import _root_.imcode.server.document.DocumentDomainObject


trait DocSelectWithLifeCycleIcon extends AbstractSelect with ContainerWithGenericItemId[DocumentDomainObject] {
  override def getItemIcon(itemId: AnyRef) = itemId.asInstanceOf[DocumentDomainObject] |> Theme.Icon.Doc.phase
}
