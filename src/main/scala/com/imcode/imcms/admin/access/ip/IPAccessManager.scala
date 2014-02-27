package com.imcode
package imcms
package admin.access.ip

import com.imcode.imcms.mapping.jpa.{IPAccessRepository, IPAccess}
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.imcode.imcms.security.PermissionGranted
import com.imcode.imcms.admin.access.user.select.UserSingleSelectDialog

import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.component.dialog.{OkCancelDialog, ConfirmationDialog}
import _root_.imcode.server.Imcms
import _root_.imcode.util.Utility.{ipLongToString, ipStringToLong}
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.security.PermissionDenied

// todo: ipv4; add/handle ipv6?
// todo: Should select user from user select!!
// todo: reload in case of internal error
// help: "Users from a specific IP number or an interval of numbers are given direct access to the system (so that the user does not have to log in)."

class IPAccessManager {
  private val ipAccessDao = Imcms.getServices.getManagedBean(classOf[IPAccessRepository])
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val toDDN = ((_:String).toLong) andThen ipLongToString
  private val fromDDN = ipStringToLong(_:String).toString

  val view = new IPAccessManagerView |>> { w =>
    w.miReload.setCommandHandler { _ => reload() }
    w.tblIP.addValueChangeHandler { _ => handleSelection() }

    w.miNew.setCommandHandler { _ => editAndSave(new IPAccess) }
    w.miEdit.setCommandHandler { _ =>
      whenSelected(w.tblIP) { id =>
        ipAccessDao.findOne(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    w.miDelete.setCommandHandler { _ =>
      whenSelected(w.tblIP) { id =>
        new ConfirmationDialog("Delete selected IP access?") |>> { dlg =>
          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              Ex.allCatch.either(ipAccessDao.delete(id)) match {
                case Right(_) =>
                  Current.page.showInfoNotification("IP access has been deleted")
                  reload()
                case Left(ex) =>
                  Current.page.showErrorNotification("Internal error")
                  throw ex
              }
            }
          }
        } |> Current.ui.addWindow
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = Current.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage IP access")

  /** Edit in modal dialog. */
  private def editAndSave(vo: IPAccess) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = if(isNew) "Create new IP access" else "Edit IP access"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainComponent = new IPAccessEditorView |>> { w =>

        w.txtId.value = if (isNew) "" else id.toString
        w.userPickerComponent.txtLoginName.value = vo.getUserId.asOption
          .map(userId => roleMapper.getUser(userId.intValue))
          .map(user => user.getLoginName).getOrElse("")

        w.txtFrom.value = vo.getStart.asOption.map(toDDN).getOrElse("")
        w.txtTo.value = vo.getEnd.asOption.map(toDDN).getOrElse("")
        w.userPickerComponent.btnChoose.addClickHandler { _ =>
          new UserSingleSelectDialog |>> { dlg =>
            dlg.setOkButtonHandler {
              w.userPickerComponent.txtLoginName.value = dlg.projection.selection.head.getLoginName
            }
          } |> Current.ui.addWindow
        }

        dlg.setOkButtonHandler {
          vo.clone |> { voc =>
          // todo: validate
            voc.setUserId(roleMapper.getUser(w.userPickerComponent.txtLoginName.value).getId)
            voc.setStart(fromDDN(w.txtFrom.value))
            voc.setEnd(fromDDN(w.txtTo.value))

            Current.ui.privileged(permission) {
              Ex.allCatch.either(ipAccessDao save voc) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  Current.page.showErrorNotification("Internal error, please contact your administrator")
                  throw ex
                case _ =>
                  (if (isNew) "New IP access has been created" else "IP access has been updated") |> { msg =>
                    Current.page.showInfoNotification(msg)
                  }

                  reload()
              }
            }
          }
        }
      }
    }  |> Current.ui.addWindow
  } // editAndSave


  def reload() {
    view.tblIP.removeAllItems
    for {
      vo <- ipAccessDao.findAll.asScala
      id = vo.getId
      user = roleMapper getUser vo.getUserId.intValue
    } view.tblIP.addRow(id, id, user.getLoginName, toDDN(vo.getStart), toDDN(vo.getEnd))

    canManage |> { value =>
      view.tblIP.setSelectable(value)
      Seq(view.miNew, view.miEdit, view.miDelete).foreach(_.setEnabled(value))
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && view.tblIP.isSelected) |> { enabled =>
      Seq(view.miEdit, view.miDelete).foreach(_.setEnabled(enabled))
    }
  }
} // class IPAccessManager