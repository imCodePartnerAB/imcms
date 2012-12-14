package com.imcode
package imcms
package admin.doc.projection.filter

import scala.collection.JavaConverters._
import com.imcode.imcms.admin.access.user.UserSelectDialog
import com.imcode.imcms.vaadin.ui.dialog.OkCancelDialog
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._

trait UserListUISetup { this: UserListUI =>
  val projectionDialogCaption: String

  chkEnabled.addValueChangeHandler {
    doto(lstUsers, lytButtons) { _.setEnabled(chkEnabled.booleanValue) }
  }

  btnAdd.addClickHandler {
    new OkCancelDialog(projectionDialogCaption) with UserSelectDialog |>> { dlg =>
      dlg.setOkButtonHandler {
        for (user <- dlg.search.selection) lstUsers.addItem(user.getId: JInteger, "#" + user.getLoginName)
      }
    } |> this.rootWindow.addWindow
  }

  btnRemove.addClickHandler {
    lstUsers.value.asScala.foreach(lstUsers.removeItem)
  }
}
