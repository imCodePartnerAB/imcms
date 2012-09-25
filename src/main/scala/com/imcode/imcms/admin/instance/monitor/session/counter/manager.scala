package com.imcode
package imcms.admin.instance.monitor.session.counter

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{PropertyDescriptor => CP, _}
import imcms.security.{PermissionGranted, PermissionDenied}
import imcms.dao.IPAccessDao
import imcms.api.IPAccess
import imcode.util.Utility.{ipLongToString, ipStringToLong}
import com.vaadin.ui.Window.Notification
import java.util.Date
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._

case class SessionCounter(value: Int, date: Date)

object SessionCounter {
  val services = Imcms.getServices
  def get() = new SessionCounter(services.getSessionCounter, services.getSessionCounterDate)
  def save(sc: SessionCounter) {
    services.setSessionCounter(sc.value)
    services.setSessionCounterDate(sc.date)
  }
}

class SessionCounterManager(app: ImcmsApplication) {
  val ui = new SessionCounterManagerUI |>> { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.miEdit setCommandHandler {
      app.getMainWindow.initAndShow(new OkCancelDialog("Edit session counter")) { dlg =>
        dlg.mainUI = new SessionCounterEditorUI |>> { c =>
          SessionCounter.get |> { sc =>
            c.txtValue.value = sc.value.toString
            c.calStart.value = sc.date
          }

          dlg.setOkHandler {
            app.privileged(permission) {
              Ex.allCatch.either(SessionCounter save SessionCounter(c.txtValue.value.toInt, c.calStart.value)) match {
                case Right(_) =>
                  app.getMainWindow.showInfoNotification("Session counter has been updated")
                  reload()
                case Left(ex) =>
                  app.getMainWindow.showErrorNotification("Internal error")
                  throw ex
              }
            }
          }
        }
      }
    }
    ui.miReset setCommandHandler {
      app.getMainWindow.initAndShow(new ConfirmationDialog("Reset session counter?")) { dlg =>
        dlg.setOkHandler {
          app.privileged(permission) {
            Ex.allCatch.either(SessionCounter save SessionCounter(0, new Date)) match {
              case Right(_) =>
                app.getMainWindow.showInfoNotification("Session counter has been reseted")
                reload()
              case Left(ex) =>
                app.getMainWindow.showErrorNotification("Internal error")
                throw ex
            }
          }
        }
      }
    }
  } // ui

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage session counter")

  def reload() {
    SessionCounter.get |> { sc =>
      ui.dataUI.txtValue.setReadOnly(false)
      ui.dataUI.calStart.setReadOnly(false)

      ui.dataUI.txtValue.value = sc.value.toString
      ui.dataUI.calStart.value = sc.date

      ui.dataUI.txtValue.setReadOnly(true)
      ui.dataUI.calStart.setReadOnly(true)
    }

    doto(ui.miEdit, ui.miReset) { _ setEnabled canManage }
  }
} // class SessionCounterManager

class SessionCounterManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icon._

  val mb = new MenuBar
  val miEdit = mb.addItem("Edit", Edit16)
  val miReset = mb.addItem("Reset", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val dataUI = new SessionCounterEditorUI
  val dataPanel = new Panel(new VerticalLayout with UndefinedSize with Margin) with UndefinedSize
  val rc = new ReloadableContentUI(dataPanel)

  dataPanel.addComponent(dataUI)
  addComponentsTo(this, mb, rc)
}


class SessionCounterEditorUI extends FormLayout with UndefinedSize {
  val txtValue = new TextField("Value")
  val calStart = new DateField("Start date") with DayResolution

  addComponentsTo(this, txtValue, calStart)
}