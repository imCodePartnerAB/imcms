package com.imcode
package imcms.admin.access.role

import _root_.imcode.server.{Imcms}
import _root_.imcode.server.user.{RoleId, RoleDomainObject}
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._

import com.imcode.imcms.security.{PermissionGranted, PermissionDenied}
import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._

import com.imcode.imcms.vaadin.server._

//todo delete in use message
class RoleManager {
  private def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

  val view = new RoleManagerView |>> { w =>
    w.rc.btnReload.addClickHandler { _ => reload() }
    w.tblRoles.addValueChangeHandler { _ => handleSelection() }

    w.miNew.setCommandHandler { _ => editAndSave(new RoleDomainObject("")) }
    w.miEdit.setCommandHandler { _ =>
      whenSelected(w.tblRoles) { id =>
        roleMapper.getRole(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    w.miDelete.setCommandHandler { _ =>
      whenSelected(w.tblRoles) { id =>
        new ConfirmationDialog("Delete selected role?") |>> { dlg =>
          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              Ex.allCatch.either(roleMapper.getRole(id).asOption.foreach(roleMapper.deleteRole)) match {
                case Right(_) =>
                  dlg.close()
                  Current.page.showInfoNotification("Role has been deleted")
                case Left(ex) =>
                  Current.page.showErrorNotification("Internal error")
                  throw ex
              }

              reload()
            }
          }
        } |> Current.ui.addWindow
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

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainComponent = new RoleEditorView |>> { w =>
        val permsToChkBoxes = Map(
            RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION -> w.chkPermChangeImagesInArchive,
            RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION -> w.chkPermUseImagesFromArchive,
            RoleDomainObject.PASSWORD_MAIL_PERMISSION -> w.chkPermGetPasswordByEmail,
            RoleDomainObject.ADMIN_PAGES_PERMISSION -> w.chkPermAccessMyPages)

        w.txtId.value = if (isNew) "" else id.intValue.toString
        w.txtName.value = vo.getName
        for ((permission, chkBox) <- permsToChkBoxes) chkBox.value = vo.getPermissions.contains(permission)

        dlg.setOkButtonHandler {
          vo.clone |> { voc =>
            // todo: validate
            voc.setName(w.txtName.value)
            voc.removeAllPermissions()
            for ((permission, chkBox) <- permsToChkBoxes if chkBox.value) voc.addPermission(permission)

            Current.ui.privileged(permission) {
              Ex.allCatch.either(roleMapper saveRole voc) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  Current.page.showErrorNotification("Internal error, please contact your administrator")
                  throw ex
                case _ =>
                  (if (isNew) "New role has been created" else "Role has been updated") |> { msg =>
                    dlg.close()
                    Current.page.showInfoNotification(msg)
                  }

                  reload()
              }
            }
          }
        }
      }
    } |> Current.ui.addWindow
  }

  def reload() {
    view.tblRoles.removeAllItems()

    for {
      vo <- roleMapper.getAllRoles
      id = vo.getId
    } view.tblRoles.addItem(Array[AnyRef](Int box id.intValue, vo.getName), id)

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

class RoleManagerView extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblRoles = new Table with SingleSelect[RoleId] with Immediate
  val rc = new ReloadableContentView(tblRoles)

  addContainerProperties(tblRoles,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"))

  this.addComponents(mb, rc)
}

class RoleEditorView extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name")
  val chkPermGetPasswordByEmail = new CheckBox("Permission to get password by email")
  val chkPermAccessMyPages = new CheckBox("""Permission to access "My pages" """)
  val chkPermUseImagesFromArchive = new CheckBox("Permission to use images from image archive")
  val chkPermChangeImagesInArchive = new CheckBox("Permission to change images in image archive")

  this.addComponents(txtName, chkPermGetPasswordByEmail, chkPermAccessMyPages, chkPermUseImagesFromArchive,
      chkPermChangeImagesInArchive)
}