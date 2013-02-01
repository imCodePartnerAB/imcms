package com.imcode
package imcms.admin.access.role

import _root_.imcode.server.{Imcms}
import _root_.imcode.server.user.{RoleId, RoleDomainObject}
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.security.{PermissionGranted, PermissionDenied}
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.vaadin.data._
import com.vaadin.server.Page
import com.imcode.imcms.vaadin.server._

//todo delete in use message
class RoleManager(app: UI) {
  private def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

  val ui = new RoleManagerUI |>> { ui =>
    ui.rc.btnReload addClickHandler { reload() }
    ui.tblRoles addValueChangeHandler { handleSelection() }

    ui.miNew setCommandHandler { editAndSave(new RoleDomainObject("")) }
    ui.miEdit setCommandHandler {
      whenSelected(ui.tblRoles) { id =>
        roleMapper.getRole(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    ui.miDelete.setCommandHandler {
      whenSelected(ui.tblRoles) { id =>
        new ConfirmationDialog("Delete selected role?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(roleMapper.getRole(id).asOption.foreach(roleMapper.deleteRole)) match {
                case Right(_) =>
                  Page.getCurrent.showInfoNotification("Role has been deleted")
                case Left(ex) =>
                  Page.getCurrent.showErrorNotification("Internal error")
                  throw ex
              }

              reload()
            }
          }
        } |> UI.getCurrent.addWindow
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = UI.getCurrent.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage roles")

  /** Edit in modal dialog. */
  private def editAndSave(vo: RoleDomainObject) {
    val id = vo.getId
    val isNew = id.intValue == 0
    val dialogTitle = if(isNew) "Create new role" else "Edit role"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainUI = new RoleEditorUI |>> { c =>
        val permsToChkBoxes = Map(
            RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION -> c.chkPermChangeImagesInArchive,
            RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION -> c.chkPermUseImagesFromArchive,
            RoleDomainObject.PASSWORD_MAIL_PERMISSION -> c.chkPermGetPasswordByEmail,
            RoleDomainObject.ADMIN_PAGES_PERMISSION -> c.chkPermAccessMyPages)

        c.txtId.value = if (isNew) "" else id.intValue.toString
        c.txtName.value = vo.getName
        for ((permission, chkBox) <- permsToChkBoxes) chkBox.value = vo.getPermissions.contains(permission)

        dlg.setOkButtonHandler {
          vo.clone |> { voc =>
            // todo: validate
            voc.setName(c.txtName.value)
            voc.removeAllPermissions
            for ((permission, chkBox) <- permsToChkBoxes if chkBox.booleanValue) voc.addPermission(permission)

            app.privileged(permission) {
              Ex.allCatch.either(roleMapper saveRole voc) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  Page.getCurrent.showErrorNotification("Internal error, please contact your administrator")
                  throw ex
                case _ =>
                  (if (isNew) "New role has been created" else "Role has been updated") |> { msg =>
                    Page.getCurrent.showInfoNotification(msg)
                  }

                  reload()
              }
            }
          }
        }
      }
    } |> UI.getCurrent.addWindow
  }

  def reload() {
    ui.tblRoles.removeAllItems

    for {
      vo <- roleMapper.getAllRoles
      id = vo.getId
    } ui.tblRoles.addItem(Array[AnyRef](Int box id.intValue, vo.getName), id)

    canManage |> { value =>
      ui.tblRoles.setSelectable(value)
      doto(ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled value }
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && ui.tblRoles.isSelected) |> { enabled =>
      doto(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
} // class RoleManager

class RoleManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblRoles = new Table with SingleSelect[RoleId] with Immediate
  val rc = new ReloadableContentUI(tblRoles)

  addContainerProperties(tblRoles,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"))

  this.addComponents(mb, rc)
}

class RoleEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name")
  val chkPermGetPasswordByEmail = new CheckBox("Permission to get password by email")
  val chkPermAccessMyPages = new CheckBox("""Permission to access "My pages" """)
  val chkPermUseImagesFromArchive = new CheckBox("Permission to use images from image archive")
  val chkPermChangeImagesInArchive = new CheckBox("Permission to change images in image archive")

  this.addComponents(txtName, chkPermGetPasswordByEmail, chkPermAccessMyPages, chkPermUseImagesFromArchive,
      chkPermChangeImagesInArchive)
}