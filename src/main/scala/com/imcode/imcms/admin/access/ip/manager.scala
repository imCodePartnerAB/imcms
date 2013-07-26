package com.imcode
package imcms
package admin.access.ip

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import com.imcode.imcms.security.{PermissionGranted, PermissionDenied}
import com.imcode.imcms.dao.IPAccessDao
import com.imcode.imcms.api.IPAccess
import com.imcode.imcms.admin.access.user.{UserSingleSelectDialog, UserSelectDialog}
import javax.persistence.{Id, Entity}

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.ui.dialog.{OkCancelDialog, ConfirmationDialog}
import _root_.imcode.server.Imcms
import _root_.imcode.util.Utility.{ipLongToString, ipStringToLong}
import com.vaadin.server.Page
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.security.PermissionDenied
import com.imcode.imcms.vaadin.data.PropertyDescriptor

// todo: ipv4; add/handle ipv6?
// todo: Should select user from user select!!
// todo: reload in case of internal error
// help: "Users from a specific IP number or an interval of numbers are given direct access to the system (so that the user does not have to log in)."

class IPAccessManager(app: UI) {
  private val ipAccessDao = Imcms.getServices.getSpringBean(classOf[IPAccessDao])
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val toDDN = ((_:String).toLong) andThen ipLongToString
  private val fromDDN = ipStringToLong(_:String).toString

  val ui = new IPAccessManagerUI |>> { ui =>
    ui.rc.btnReload.addClickHandler { reload() }
    ui.tblIP.addValueChangeHandler { handleSelection() }

    ui.miNew.setCommandHandler { editAndSave(new IPAccess) }
    ui.miEdit.setCommandHandler {
      whenSelected(ui.tblIP) { id =>
        ipAccessDao.get(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete setCommandHandler {
      whenSelected(ui.tblIP) { id =>
        new ConfirmationDialog("Delete selected IP access?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(ipAccessDao delete id) match {
                case Right(_) =>
                  Page.getCurrent.showInfoNotification("IP access has been deleted")
                  reload()
                case Left(ex) =>
                  Page.getCurrent.showErrorNotification("Internal error")
                  throw ex
              }
            }
          }
        } |> UI.getCurrent.addWindow
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = UI.getCurrent.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage IP access")

  /** Edit in modal dialog. */
  private def editAndSave(vo: IPAccess) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = if(isNew) "Create new IP access" else "Edit IP access"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainUI = new IPAccessEditorUI |>> { c =>

        c.txtId.value = if (isNew) "" else id.toString
        c.userPickerUI.txtLoginName.value = vo.getUserId.asOption
          .map(userId => roleMapper.getUser(userId.intValue))
          .map(user => user.getLoginName).getOrElse("")

        c.txtFrom.value = vo.getStart.asOption.map(toDDN).getOrElse("")
        c.txtTo.value = vo.getEnd.asOption.map(toDDN).getOrElse("")
        c.userPickerUI.btnChoose.addClickHandler {
          new UserSingleSelectDialog |>> { dlg =>
            dlg.setOkButtonHandler {
              c.userPickerUI.txtLoginName.value = dlg.search.selection.head.getLoginName
            }
          } |> UI.getCurrent.addWindow
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
                  Page.getCurrent.showErrorNotification("Internal error, please contact your administrator")
                  throw ex
                case _ =>
                  (if (isNew) "New IP access has been created" else "IP access has been updated") |> { msg =>
                    Page.getCurrent.showInfoNotification(msg)
                  }

                  reload()
              }
            }
          }
        }
      }
    }  |> UI.getCurrent.addWindow
  } // editAndSave


  def reload() {
    ui.tblIP.removeAllItems
    for {
      vo <- ipAccessDao.getAll.asScala
      id = vo.getId
      user = roleMapper getUser vo.getUserId.intValue
    } ui.tblIP.addItem(Array[AnyRef](id, user.getLoginName, toDDN(vo.getStart), toDDN(vo.getEnd)), id)

    canManage |> { value =>
      ui.tblIP.setSelectable(value)
      Seq(ui.miNew, ui.miEdit, ui.miDelete).foreach(_.setEnabled(value))
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && ui.tblIP.isSelected) |> { enabled =>
      Seq(ui.miEdit, ui.miDelete).foreach(_.setEnabled(enabled))
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
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("IP range from"),
    PropertyDescriptor[String]("IP range to"))

  this.addComponents(mb, rc)
}


class IPAccessEditorUI extends FormLayout with UndefinedSize {
  class UserPickerUI extends HorizontalLayout with Spacing with UndefinedSize {
    val txtLoginName = new TextField  { setInputPrompt("No user selected") }    // with ReadOnly
    val btnChoose = new Button("...") { setStyleName("small") }

    this.addComponents(txtLoginName, btnChoose)
    setCaption("User")
  }

  val txtId = new TextField("Id") with Disabled
  val userPickerUI = new UserPickerUI
  val txtFrom = new TextField("From")
  val txtTo = new TextField("To")

  this.addComponents(txtId, userPickerUI, txtFrom, txtTo)
}