package com.imcode
package imcms
package admin.doc.meta.access

import com.imcode.imcms.vaadin.component._

import imcode.server.document.TextDocumentPermissionSetDomainObject

class NonTextDocPermSetEditor(permSet: TextDocumentPermissionSetDomainObject) extends DocPermSetEditor {

  override val view = new NonTextDocPermSetEditorView

  resetValues()

  override def resetValues() {
    view.chkEditMeta.checked = permSet.getEditDocumentInformation
    view.chkEditPermissions.checked = permSet.getEditPermissions
    view.chkEditContent.checked = permSet.getEdit
  }

  override def collectValues(): ErrorsOrData = new TextDocumentPermissionSetDomainObject(permSet.getType) |>> { ps =>
    ps.setEditDocumentInformation(view.chkEditMeta.checked)
    ps.setEditPermissions(view.chkEditPermissions.checked)
    ps.setEdit(view.chkEditContent.checked)
  } |> Right.apply
}
