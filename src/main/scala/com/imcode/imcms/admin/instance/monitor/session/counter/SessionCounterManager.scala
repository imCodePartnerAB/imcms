package com.imcode
package imcms
package admin
package instance.monitor.session.counter

import _root_.java.util.Date
import com.imcode.imcms.security.{PermissionDenied, PermissionGranted}
import com.imcode.imcms.vaadin.component.dialog.{ConfirmationDialog, OkCancelDialog}
import com.imcode.imcms.vaadin.Current

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._

import scala.util.control.{Exception => Ex}

class SessionCounterManager {
  val view = new SessionCounterManagerView |>> { w =>
    w.miReload.setCommandHandler { _ => reload() }
    w.miEdit.setCommandHandler { _ =>
      new OkCancelDialog("Edit session counter") |>> { dlg =>
        dlg.mainComponent = new SessionCounterForm |>> { c =>
          SessionCounter.get |> { sc =>
            c.txtValue.value = sc.value.toString
            c.calStart.value = sc.date
          }

          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              Ex.allCatch.either(SessionCounter.save(SessionCounter(c.txtValue.value.toInt, c.calStart.value))) match {
                case Right(_) =>
                  Current.page.showInfoNotification("Session counter has been updated")
                  reload()
                case Left(ex) =>
                  Current.page.showErrorNotification("Internal error")
                  throw ex
              }
            }
          }
        }
      } |> Current.ui.addWindow
    }
    w.miReset.setCommandHandler { _ =>
      new ConfirmationDialog("Reset session counter?") |>> { dlg =>
        dlg.setOkButtonHandler {
          Current.ui.privileged(permission) {
            Ex.allCatch.either(SessionCounter.save(SessionCounter(0, new Date))) match {
              case Right(_) =>
                Current.page.showInfoNotification("Session counter has been reseted")
                reload()
              case Left(ex) =>
                Current.page.showErrorNotification("Internal error")
                throw ex
            }
          }
        }
      } |> Current.ui.addWindow
    }
  } // val widget

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = Current.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage session counter")

  def reload() {
    SessionCounter.get |> { sc =>
      view.frmValues.txtValue.setReadOnly(false)
      view.frmValues.calStart.setReadOnly(false)

      view.frmValues.txtValue.value = sc.value.toString
      view.frmValues.calStart.value = sc.date

      view.frmValues.txtValue.setReadOnly(true)
      view.frmValues.calStart.setReadOnly(true)
    }

    Seq(view.miEdit, view.miReset).foreach(_.setEnabled(canManage))
  }
} // class SessionCounterManager
