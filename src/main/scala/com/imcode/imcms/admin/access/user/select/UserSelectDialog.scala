package com.imcode
package imcms
package admin
package access.user.select

import com.imcode.imcms.vaadin.component.dialog.{Resizable, CustomSizeDialog, OkCancelDialog}
import com.imcode.imcms.admin.access.user.projection.UsersProjection

import com.imcode.imcms.vaadin.component._

abstract class UserSelectDialog(caption: String, multiSelect: Boolean) extends OkCancelDialog(caption) with CustomSizeDialog with Resizable {
  val search = new UsersProjection(multiSelect = multiSelect)

  mainComponent = search.view

  search.listen(selection => btnOk.setEnabled(selection.nonEmpty))
  search.notifyListeners()

  this.setSize(600, 500)
}