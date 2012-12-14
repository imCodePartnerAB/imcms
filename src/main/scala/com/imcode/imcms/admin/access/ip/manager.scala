package com.imcode
package imcms
package admin.access.ip

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConversions._
import com.vaadin.ui._
import _root_.imcode.server.Imcms
import com.imcode.imcms.vaadin.data.{PropertyDescriptor => CP}
import com.imcode.imcms.security.{PermissionGranted, PermissionDenied}
import com.imcode.imcms.dao.IPAccessDao
import com.imcode.imcms.api.IPAccess
import _root_.imcode.util.Utility.{ipLongToString, ipStringToLong}
import com.vaadin.ui.Window.Notification
import com.imcode.imcms.admin.access.user.UserSelectDialog
import javax.persistence.{Id, Entity}
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.vaadin.ImcmsApplication

// todo: ipv4; add/handle ipv6?
// todo: Should select user from user select!!
// todo: reload in case of internal error
// help: "Users from a specific IP number or an interval of numbers are given direct access to the system (so that the user does not have to log in)."

class IPAccessManager(app: ImcmsApplication) {
  private val ipAccessDao = Imcms.getServices.getSpringBean(classOf[IPAccessDao])
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val toDDN = ((_:String).toLong) andThen ipLongToString
  private val fromDDN = ipStringToLong(_:String).toString

  val ui = new IPAccessManagerUI |>> { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.tblIP addValueChangeHandler { handleSelection() }

    ui.miNew setCommandHandler { editAndSave(new IPAccess) }
    ui.miEdit setCommandHandler {
      whenSelected(ui.tblIP) { id =>
        ipAccessDao.get(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete setCommandHandler {
      whenSelected(ui.tblIP) { id =>
        app.getMainWindow.initAndShow(new ConfirmationDialog("Delete selected IP access?")) { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(ipAccessDao delete id) match {
                case Right(_) =>
                  app.getMainWindow.showInfoNotification("IP access has been deleted")
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
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage IP access")

  /** Edit in modal dialog. */
  private def editAndSave(vo: IPAccess) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = if(isNew) "Create new IP access" else "Edit IP access"

    app.getMainWindow.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
      dlg.mainUI = new IPAccessEditorUI |>> { c =>

        c.txtId.value = if (isNew) "" else id.toString
        c.userPickerUI.txtLoginName.value = (vo.getUserId |> opt map { roleMapper getUser _.intValue } map { _.getLoginName } getOrElse "")
        c.txtFrom.value = vo.getStart |> opt map toDDN getOrElse ""
        c.txtTo.value = vo.getEnd |> opt map toDDN getOrElse ""
        c.userPickerUI.btnChoose.addClickHandler {
          app.getMainWindow.initAndShow(new OkCancelDialog("Choose user") with UserSelectDialog) { userSelectDlg =>
            userSelectDlg.setOkButtonHandler {
              c.userPickerUI.txtLoginName.value = userSelectDlg.search.selection.head.getLoginName
            }
          }
        }

        dlg.setOkButtonHandler {
          vo.clone |> { voc =>
            // todo: validate
            voc.setUserId(roleMapper.getUser(c.userPickerUI.txtLoginName.value).getId)
            voc.setStart(fromDDN(c.txtFrom.value))
            voc.setEnd(fromDDN(c.txtTo.value))

            app.privileged(permission) {
              Ex.allCatch.either(ipAccessDao save voc) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  app.getMainWindow.showNotification("Internal error, please contact your administrator", Notification.TYPE_ERROR_MESSAGE)
                  throw ex
                case _ =>
                  (if (isNew) "New IP access has been created" else "IP access has been updated") |> { msg =>
                    app.getMainWindow.showNotification(msg, Notification.TYPE_HUMANIZED_MESSAGE)
                  }

                  reload()
              }
            }
          }
        }
      }
    }
  } // editAndSave


  def reload() {
    ui.tblIP.removeAllItems
    for {
      vo <- ipAccessDao.getAll.toList
      id = vo.getId
      user = roleMapper getUser vo.getUserId.intValue
    } ui.tblIP.addItem(Array[AnyRef](id, user.getLoginName, toDDN(vo.getStart), toDDN(vo.getEnd)), id)

    canManage |> { value =>
      ui.tblIP.setSelectable(value)
      doto(ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled value }
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && ui.tblIP.isSelected) |> { enabled =>
      doto(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
} // class IPAccessManager

class IPAccessManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblIP = new Table with SingleSelect[JInteger] with Immediate
  val rc = new ReloadableContentUI(tblIP)

  addContainerProperties(tblIP,
    CP[JInteger]("Id"),
    CP[String]("Name"),
    CP[String]("IP range from"),
    CP[String]("IP range to"))

  addComponentsTo(this, mb, rc)
}


class IPAccessEditorUI extends FormLayout with UndefinedSize {
  class UserPickerUI extends HorizontalLayout with Spacing with UndefinedSize {
    val txtLoginName = new TextField  { setInputPrompt("No user selected") }    // with ReadOnly
    val btnChoose = new Button("...") { setStyleName("small") }

    addComponentsTo(this, txtLoginName, btnChoose)
    setCaption("User")
  }

  val txtId = new TextField("Id") with Disabled
  val userPickerUI = new UserPickerUI
  val txtFrom = new TextField("From")
  val txtTo = new TextField("To")

  addComponentsTo(this, txtId, userPickerUI, txtFrom, txtTo)
}