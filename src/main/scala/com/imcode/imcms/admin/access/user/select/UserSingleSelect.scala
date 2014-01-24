package com.imcode
package imcms
package admin
package access.user.select

import _root_.imcode.server.user.UserDomainObject
import java.util.concurrent.atomic.AtomicReference
import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.vaadin.data._

class UserSingleSelect {

  private val selectionRef = new AtomicReference(Option.empty[UserDomainObject])

  val view = new UserSingleSelectView |>> { w =>
    w.btnSelect.addClickHandler { _ =>
      val dialog = new UserSingleSelectDialog
      dialog.setOkButtonHandler {
        selection = dialog.projection.selection.headOption
        dialog.close()
      }

      dialog.show()
    }

    w.btnClear.addClickHandler { _ => selection = None }
  }

  selection = None

  def selection: Option[UserDomainObject] = selectionRef.get
  def selection_=(userOpt: Option[UserDomainObject]) {
    view.btnClear.setEnabled(userOpt.isDefined)
    view.lblName.value = userOpt match {
      case Some(user) => s"[ <b>${user.getLoginName}</b> ]"
      case _ => "[ <i>not selected</i> ]"
    }

    selectionRef.set(userOpt)
  }
}
