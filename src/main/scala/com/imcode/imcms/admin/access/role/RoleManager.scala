package com.imcode
package imcms.admin.access.role

import _root_.imcode.server.Imcms
import _root_.imcode.server.user.RoleDomainObject

import com.imcode.imcms.security.{PermissionGranted, PermissionDenied}
import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.event._

import com.imcode.imcms.vaadin.server._
import scala.util.{Failure, Success, Try}

//todo delete in use message
class RoleManager {
  private def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

  val view = new RoleManagerView |>> { v =>
    v.miReload.setCommandHandler { _ => reload() }
    v.tblRoles.addValueChangeHandler { _ => handleSelection() }

    v.miNew.setCommandHandler { _ => editAndSave(new RoleDomainObject("")) }
    v.miEdit.setCommandHandler { _ =>
      whenSelected(v.tblRoles) { id =>
        roleMapper.getRole(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    v.miDelete.setCommandHandler { _ =>
      whenSelected(v.tblRoles) { id =>
        val dialog = new ConfirmationDialog("Delete selected role?")

        dialog.setOkButtonHandler {
          Current.ui.privileged(permission) {
            Try(roleMapper.getRole(id).asOption.foreach(roleMapper.deleteRole)) match {
              case Success(_) =>
                dialog.close()
                Current.page.showInfoNotification("Role has been deleted")
              case Failure(e) =>
                Current.page.showUnhandledExceptionNotification(e)
            }

            reload()
          }
        }

        dialog.show()
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = Current.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage roles")

  /** Edit in modal dialog. */
  private def editAndSave(vo: RoleDomainObject) {
    val id = vo.getId
    val isNew = id.intValue == 0
    val dialogTitle = if(isNew) "Create new role" else "Edit role"
    val dialog = new OkCancelDialog(dialogTitle)

    dialog.mainComponent = new RoleEditorView |>> { v =>
      val permsToChkBoxes = Map(
        RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION -> v.chkPermChangeImagesInArchive,
        RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION -> v.chkPermUseImagesFromArchive,
        RoleDomainObject.PASSWORD_MAIL_PERMISSION -> v.chkPermGetPasswordByEmail,
        RoleDomainObject.ADMIN_PAGES_PERMISSION -> v.chkPermAccessMyPages
      )

      v.txtId.value = if (isNew) "" else id.intValue.toString
      v.txtName.value = vo.getName
      for ((permission, chkBox) <- permsToChkBoxes) chkBox.value = vo.getPermissions.contains(permission)

      dialog.setOkButtonHandler {
        val voc = vo.clone
        // todo: validate
        voc.setName(v.txtName.value)
        voc.removeAllPermissions()
        for ((permission, chkBox) <- permsToChkBoxes if chkBox.value) voc.addPermission(permission)

        Current.ui.privileged(permission) {
          Try(roleMapper saveRole voc) match {
            case Failure(e) => Current.page.showUnhandledExceptionNotification(e)
            case _ =>
              dialog.close()
              Current.page.showInfoNotification(
                if (isNew) "New role has been created" else "Role has been updated"
              )
              reload()
          }
        }
      }
    }
  }

  def reload() {
    view.tblRoles.removeAllItems()

    for {
      vo <- roleMapper.getAllRoles
      id = vo.getId
    } view.tblRoles.addRow(id, id.intValue : JInteger, vo.getName, null)

    canManage |> { value =>
      view.tblRoles.setSelectable(value)
      Seq(view.miNew, view.miEdit, view.miDelete).foreach(_.setEnabled(value))
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && view.tblRoles.isSelected) |> { enabled =>
      Seq(view.miEdit, view.miDelete).foreach(_.setEnabled(enabled))
    }
  }
} // class RoleManager
