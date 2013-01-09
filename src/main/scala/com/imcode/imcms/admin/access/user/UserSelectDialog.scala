package com.imcode.imcms.admin.access.user

import com.imcode.imcms.vaadin.ui.dialog.OkCancelDialog
import com.imcode.imcms.admin.access.user.projection.UsersProjection

abstract class UserSelectDialog(caption: String, multiSelect: Boolean) extends OkCancelDialog(caption) {
  val search = new UsersProjection(multiSelect = multiSelect)

  mainUI = search.ui

  search.listen(selection => btnOk.setEnabled(selection.nonEmpty))
  search.notifyListeners()
}
