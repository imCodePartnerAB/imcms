package com.imcode
package imcms.admin.access.ip

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcms.security.{PermissionGranted, PermissionDenied}
import imcms.dao.IPAccessDao
import imcms.api.IPAccess
import imcode.util.Utility.{ipLongToString, ipStringToLong}
import com.vaadin.ui.Window.Notification

// todo: ipv4; add/handle ipv6?
// todo: Should select user from user select!!
// todo: reload in case of internal error
// help: "Users from a specific IP number or an interval of numbers are given direct access to the system (so that the user does not have to log in)."

class IPAccessManager(app: ImcmsApplication) {
  private val ipAccessDao = Imcms.getSpringBean("ipAccessDao").asInstanceOf[IPAccessDao]
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val toDDN = ((_:String).toLong) andThen ipLongToString
  private val fromDDN = ipStringToLong(_:String).toString

  val ui = letret(new IPAccessManagerUI) { ui =>
    ui.rc.btnReload addListener block { reload() }
    ui.tblIP addListener block { handleSelection() }

    ui.miNew setCommand block { editAndSave(new IPAccess) }
    ui.miEdit setCommand block {
      whenSelected(ui.tblIP) { id =>
        ipAccessDao.get(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete setCommand block {
      whenSelected(ui.tblIP) { id =>
        app.initAndShow(new ConfirmationDialog("Delete selected IP access?")) { dlg =>
          dlg setOkHandler {
            app.privileged(permission) {
              EX.allCatch.either(ipAccessDao delete id) match {
                case Right(_) =>
                  app.showInfoNotification("IP access has been deleted")
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
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage IP access")

  /** Edit in modal dialog. */
  private def editAndSave(vo: IPAccess) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = if(isNew) "Create new IP access" else "Edit IP access"

    app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
      dlg.mainUI = letret(new IPAccessEditorUI) { c =>
        c.txtId.value = if (isNew) "" else id.toString
        roleMapper.getAllUsers foreach { u => c.sltUser.addItem(Int box u.getId, u.getLoginName) }
        ?(vo.getUserId) foreach { c.sltUser.select(_) }
        c.txtFrom.value = ?(vo.getStart) map toDDN getOrElse ""
        c.txtTo.value = ?(vo.getEnd) map toDDN getOrElse ""

        dlg setOkHandler {
          let(vo.clone) { voc =>
            // todo: validate
            voc.setUserId(c.sltUser.getValue.asInstanceOf[JInteger])
            voc.setStart(fromDDN(c.txtFrom.value))
            voc.setEnd(fromDDN(c.txtTo.value))

            app.privileged(permission) {
              EX.allCatch.either(ipAccessDao save voc) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  app.getMainWindow.showNotification("Internal error, please contact your administrator", Notification.TYPE_ERROR_MESSAGE)
                  throw ex
                case _ =>
                  let(if (isNew) "New IP access has been created" else "IP access has been updated") { msg =>
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

    let(canManage) { canManage =>
      ui.tblIP.setSelectable(canManage)
      forlet(ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled canManage }
    }

    handleSelection()
  }

  private def handleSelection() {
    let(canManage && ui.tblIP.isSelected) { enabled =>
      forlet(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
} // class IPAccessManager

class IPAccessManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblIP = new Table with SingleSelect2[JInteger] with Immediate
  val rc = new ReloadableContentUI(tblIP)

  addContainerProperties(tblIP,
    CP[JInteger]("Id"),
    CP[String]("Name"),
    CP[String]("IP range from"),
    CP[String]("IP range to"))

  addComponents(this, mb, rc)
}


class IPAccessEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val sltUser = new Select("Users") with XSelect[JInteger] with NoNullSelection
  val txtFrom = new TextField("From")
  val txtTo = new TextField("To")

  addComponents(this, txtId, sltUser, txtFrom, txtTo)
}