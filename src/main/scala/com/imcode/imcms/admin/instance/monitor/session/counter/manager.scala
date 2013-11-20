package com.imcode
package imcms.admin.instance.monitor.session.counter

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import imcms.security.{PermissionGranted, PermissionDenied}
import imcms.dao.IPAccessDao
import imcms.api.IPAccess
import imcode.util.Utility.{ipLongToString, ipStringToLong}
import java.util.Date
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

case class SessionCounter(value: Int, date: Date)

object SessionCounter {
  val services = Imcms.getServices
  def get() = new SessionCounter(services.getSessionCounter, services.getSessionCounterDate)
  def save(sc: SessionCounter) {
    services.setSessionCounter(sc.value)
    services.setSessionCounterDate(sc.date)
  }
}

class SessionCounterManager(app: UI) {
  val widget = new SessionCounterManagerWidget |>> { w =>
    w.rc.btnReload.addClickHandler { _ => reload() }
    w.miEdit.setCommandHandler { _ =>
      new OkCancelDialog("Edit session counter") |>> { dlg =>
        dlg.mainWidget = new SessionCounterEditorWidget |>> { c =>
          SessionCounter.get |> { sc =>
            c.txtValue.value = sc.value.toString
            c.calStart.value = sc.date
          }

          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(SessionCounter save SessionCounter(c.txtValue.value.toInt, c.calStart.value)) match {
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
          app.privileged(permission) {
            Ex.allCatch.either(SessionCounter save SessionCounter(0, new Date)) match {
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

  def canManage = Current.ui.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage session counter")

  def reload() {
    SessionCounter.get |> { sc =>
      widget.dataWidget.txtValue.setReadOnly(false)
      widget.dataWidget.calStart.setReadOnly(false)

      widget.dataWidget.txtValue.value = sc.value.toString
      widget.dataWidget.calStart.value = sc.date

      widget.dataWidget.txtValue.setReadOnly(true)
      widget.dataWidget.calStart.setReadOnly(true)
    }

    Seq(widget.miEdit, widget.miReset).foreach(_.setEnabled(canManage))
  }
} // class SessionCounterManager

class SessionCounterManagerWidget extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miEdit = mb.addItem("Edit", Edit16)
  val miReset = mb.addItem("Reset", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val dataWidget = new SessionCounterEditorWidget
  val dataPanel = new Panel(new VerticalLayout with UndefinedSize with Margin) with UndefinedSize
  val rc = new ReloadableContentWidget(dataPanel)

  dataPanel.setContent(dataWidget)
  this.addComponents(mb, rc)
}


class SessionCounterEditorWidget extends FormLayout with UndefinedSize {
  val txtValue = new TextField("Value")
  val calStart = new DateField("Start date") with DayResolution

  this.addComponents(txtValue, calStart)
}