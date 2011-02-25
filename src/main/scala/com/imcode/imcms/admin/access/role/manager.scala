package com.imcode
package imcms.admin.access.role

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.ui.Window.Notification
import imcms.security.{PermissionGranted, PermissionDenied}
import imcode.server.user.{RoleId, RoleDomainObject}

//todo delete in use message
class RoleManager(app: ImcmsApplication) {
  private def roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

  val ui = letret(new RoleManagerUI) { ui =>
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
    ui.miDelete setCommandHandler {
      whenSelected(ui.tblRoles) { id =>
        app.initAndShow(new ConfirmationDialog("Delete selected role?")) { dlg =>
          dlg wrapOkHandler {
            app.privileged(permission) {
              EX.allCatch.either(?(roleMapper getRole id) foreach roleMapper.deleteRole) match {
                case Right(_) =>
                  app.showInfoNotification("Role has been deleted")
                case Left(ex) =>
                  app.showErrorNotification("Internal error")
                  throw ex
              }

              reload()
            }
          }
        }
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage roles")

  /** Edit in modal dialog. */
  private def editAndSave(vo: RoleDomainObject) {
    val id = vo.getId
    val isNew = id.intValue == 0
    val dialogTitle = if(isNew) "Create new role" else "Edit role"

    app.initAndShow(new OkCancelDialog(dialogTitle)) { dlg =>
      dlg.mainUI = letret(new RoleEditorUI) { c =>
        val permsToChkBoxes = Map(
            RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION -> c.chkPermChangeImagesInArchive,
            RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION -> c.chkPermUseImagesFromArchive,
            RoleDomainObject.PASSWORD_MAIL_PERMISSION -> c.chkPermGetPasswordByEmail,
            RoleDomainObject.ADMIN_PAGES_PERMISSION -> c.chkPermAccessMyPages)

        c.txtId.value = if (isNew) "" else id.intValue.toString
        c.txtName.value = vo.getName
        for ((permission, chkBox) <- permsToChkBoxes) chkBox.value = vo.getPermissions.contains(permission)

        dlg wrapOkHandler {
          let(vo.clone) { voc =>
            // todo: validate
            voc.setName(c.txtName.value)
            voc.removeAllPermissions
            for ((permission, chkBox) <- permsToChkBoxes if chkBox.booleanValue) voc.addPermission(permission)

            app.privileged(permission) {
              EX.allCatch.either(roleMapper saveRole voc) match {
                case Left(ex) =>
                  // todo: log ex, provide custom dialog with details -> show stack
                  app.getMainWindow.showNotification("Internal error, please contact your administrator", Notification.TYPE_ERROR_MESSAGE)
                  throw ex
                case _ =>
                  let(if (isNew) "New role has been created" else "Role has been updated") { msg =>
                    app.getMainWindow.showNotification(msg, Notification.TYPE_HUMANIZED_MESSAGE)
                  }

                  reload()
              }
            }
          }
        }
      }
    }
  }

  def reload() {
    ui.tblRoles.removeAllItems

    for {
      vo <- roleMapper.getAllRoles
      id = vo.getId
    } ui.tblRoles.addItem(Array[AnyRef](Int box id.intValue, vo.getName), id)

    let(canManage) { canManage =>
      ui.tblRoles.setSelectable(canManage)
      forlet(ui.miNew, ui.miEdit, ui.miDelete) { _ setEnabled canManage }
    }

    handleSelection()
  }

  private def handleSelection() {
    let(canManage && ui.tblRoles.isSelected) { enabled =>
      forlet(ui.miEdit, ui.miDelete) { _ setEnabled enabled }
    }
  }
} // class RoleManager

class RoleManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblRoles = new Table with SingleSelect2[RoleId] with Immediate
  val rc = new ReloadableContentUI(tblRoles)

  addContainerProperties(tblRoles,
    CP[JInteger]("Id"),
    CP[String]("Name"))

  addComponents(this, mb, rc)
}

class RoleEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name")
  val chkPermGetPasswordByEmail = new CheckBox("Permission to get password by email")
  val chkPermAccessMyPages = new CheckBox("""Permission to access "My pages" """)
  val chkPermUseImagesFromArchive = new CheckBox("Permission to use images from image archive")
  val chkPermChangeImagesInArchive = new CheckBox("Permission to change images in image archive")

  addComponents(this, txtName, chkPermGetPasswordByEmail, chkPermAccessMyPages, chkPermUseImagesFromArchive,
      chkPermChangeImagesInArchive)
}