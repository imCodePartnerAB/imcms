package com.imcode
package imcms
package admin.access.user

import _root_.imcode.server.user._

import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.data._

import scala.collection.JavaConverters._
import com.imcode.imcms.admin.access.user.projection.UsersProjection

// todo add security check, add editAndSave, add external UI
class UserManager extends ImcmsServicesSupport {
  private val search = new UsersProjection

  val view = new UserManagerView(search.view) |>> { w =>
    val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper

    w.miNew.setCommandHandler { _ =>
      new OkCancelDialog("user.dlg.new.caption".i) |>> { dlg =>
        dlg.mainComponent = new UserEditorView |>> { c =>
          for (role <- roleMapper.getAllRoles if role.getId != RoleId.USERS) {
            c.tcsRoles.addItem(role.getId, role.getName)
          }

          imcmsServices.getLanguageMapper.getDefaultLanguage |> { l =>
            c.sltUILanguage.addItem(l)
            c.sltUILanguage.select(l)
          }

          c.chkActivated.setValue(true)

          dlg.setOkButtonHandler {
            new UserDomainObject |> { u =>
              u.setActive(c.chkActivated.value)
              u.setFirstName(c.txtFirstName.value)
              u.setLastName(c.txtLastName.value)
              u.setLoginName(c.txtLogin.value)
              u.setPassword(c.txtPassword.value)
              u.setRoleIds(c.tcsRoles.value.asScala.toArray)
              u.setLanguageIso639_2(c.sltUILanguage.value)

              roleMapper.addUser(u)
              search.reset()
            }
          }
        }
      } |> Current.ui.addWindow
    }

    w.miEdit.setCommandHandler { _ =>
      whenSingleton(search.selection) { user =>
        new OkCancelDialog("user.dlg.edit.caption".f(user.getLoginName)) |>> { dlg =>
          dlg.mainComponent = new UserEditorView |>> { c =>
            c.chkActivated setValue user.isActive
            c.txtFirstName setValue user.getFirstName
            c.txtLastName setValue user.getLastName
            c.txtLogin setValue user.getLoginName
            c.txtPassword setValue user.getPassword

            for (role <- roleMapper.getAllRoles if role.getId != RoleId.USERS) {
              c.tcsRoles.addItem(role.getId, role.getName)
            }

            c.tcsRoles.value = user.getRoleIds.filterNot(_ == RoleId.USERS).toSeq.asJava

            imcmsServices.getLanguageMapper.getDefaultLanguage |> { l =>
              c.sltUILanguage.addItem(l)
            }

            c.sltUILanguage.select(user.getLanguageIso639_2)

            dlg.setOkButtonHandler {
              user.setActive(c.chkActivated.value)
              user.setFirstName(c.txtFirstName.value)
              user.setLastName(c.txtLastName.value)
              user.setLoginName(c.txtLogin.value)
              user.setPassword(c.txtPassword.value)
              user.setRoleIds(c.tcsRoles.value.asScala.toArray)
              user.setLanguageIso639_2(c.sltUILanguage.value)

              roleMapper.saveUser(user)
              search.reset()
            }
          }
        } |> Current.ui.addWindow
      }
    }

    search.listen { w.miEdit setEnabled _.size == 1 }
    search.notifyListeners()
  }
}
