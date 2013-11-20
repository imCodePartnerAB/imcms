package com.imcode
package imcms
package admin.doc.projection.filter

import com.imcode.imcms.vaadin.Current
import scala.collection.JavaConverters._
import com.imcode.imcms.admin.access.user.{UserMultiSelectDialog}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.vaadin.ui.UI

trait UserListWidgetSetup { this: UserListWidget =>
  val projectionDialogCaption: String

  chkEnabled.addValueChangeHandler { _ =>
    Seq(lstUsers, lytButtons).foreach(_.setEnabled(chkEnabled.value))
  }

  btnAdd.addClickHandler { _ =>
    new UserMultiSelectDialog |>> { dlg =>
      dlg.setOkButtonHandler {
        for (user <- dlg.search.selection) lstUsers.addItem(user.getId: JInteger, "#" + user.getLoginName)
      }
    } |> Current.ui.addWindow
  }

  btnRemove.addClickHandler { _ =>
    lstUsers.value.asScala.foreach(lstUsers.removeItem)
  }
}
