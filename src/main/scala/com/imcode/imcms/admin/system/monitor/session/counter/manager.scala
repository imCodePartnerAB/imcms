package com.imcode
package imcms.admin.system.monitor.session.counter

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcms.security.{PermissionGranted, PermissionDenied}
import imcms.dao.IPAccessDao
import imcms.api.IPAccess
import imcode.util.Utility.{ipLongToString, ipStringToLong}
import com.vaadin.ui.Window.Notification
import java.util.Date

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
  val ui = letret(new SessionCounterManagerUI) { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.miEdit setCommand block {
      app.initAndShow(new OkCancelDialog("Edit session counter")) { dlg =>
        dlg.mainUI = letret(new SessionCounterEditorUI) { c =>
          let(SessionCounter.get) { sc =>
            c.txtValue.value = sc.value.toString
            c.calStart.value = sc.date
          }

          dlg.setOkHandler {
            app.privileged(permission) {
              EX.allCatch.either(SessionCounter save SessionCounter(c.txtValue.value.toInt, c.calStart.value)) match {
                case Right(_) =>
                  app.showInfoNotification("Session counter has been updated")
                  reload()
                case Left(ex) =>
                  app.showErrorNotification("Internal error")
                  throw ex
              }
            }
          }
        }
      }
    }
    ui.miReset setCommand block {
      app.initAndShow(new ConfirmationDialog("Reset session counter?")) { dlg =>
        dlg setOkHandler {
          app.privileged(permission) {
            EX.allCatch.either(SessionCounter save SessionCounter(0, new Date)) match {
              case Right(_) =>
                app.showInfoNotification("Session counter has been reseted")
                reload()
              case Left(ex) =>
                app.showErrorNotification("Internal error")
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
    let(SessionCounter.get) { sc =>
      ui.dataUI.txtValue.setReadOnly(false)
      ui.dataUI.calStart.setReadOnly(false)

      ui.dataUI.txtValue.value = sc.value.toString
      ui.dataUI.calStart.value = sc.date

      ui.dataUI.txtValue.setReadOnly(true)
      ui.dataUI.calStart.setReadOnly(true)
    }

    forlet(ui.miEdit, ui.miReset) { _ setEnabled canManage }
  }
} // class SessionCounterManager

class SessionCounterManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miEdit = mb.addItem("Edit", Edit16)
  val miReset = mb.addItem("Reset", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val dataUI = new SessionCounterEditorUI
  val dataPanel = new Panel(new VerticalLayout with UndefinedSize with Margin) with UndefinedSize
  val rc = new ReloadableContentUI(dataPanel)

  dataPanel.addComponent(dataUI)
  addComponents(this, mb, rc)
}


class SessionCounterEditorUI extends FormLayout with UndefinedSize {
  val txtValue = new TextField("Value")
  val calStart = new DateField("Start date") with DayResolution

  addComponents(this, txtValue, calStart)
}