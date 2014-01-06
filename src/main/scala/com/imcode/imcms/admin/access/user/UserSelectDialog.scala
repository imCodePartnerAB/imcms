package com.imcode.imcms.admin.access.user

import com.imcode.imcms.vaadin.component.dialog.OkCancelDialog
import com.imcode.imcms.admin.access.user.projection.UsersProjection

abstract class UserSelectDialog(caption: String, multiSelect: Boolean) extends OkCancelDialog(caption) {
  val search = new UsersProjection(multiSelect = multiSelect)

  mainComponent = search.view

  search.listen(selection => btnOk.setEnabled(selection.nonEmpty))
  search.notifyListeners()
}
