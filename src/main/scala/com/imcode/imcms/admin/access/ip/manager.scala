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

import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.component.dialog.{OkCancelDialog, ConfirmationDialog}
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
  private val ipAccessDao = Imcms.getServices.getManagedBean(classOf[IPAccessDao])
  private val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper
  private val toDDN = ((_:String).toLong) andThen ipLongToString
  private val fromDDN = ipStringToLong(_:String).toString

  val widget = new IPAccessManagerWidget |>> { w =>
    w.rc.btnReload.addClickHandler { _ => reload() }
    w.tblIP.addValueChangeHandler { _ => handleSelection() }

    w.miNew.setCommandHandler { _ => editAndSave(new IPAccess) }
    w.miEdit.setCommandHandler { _ =>
      whenSelected(w.tblIP) { id =>
        ipAccessDao.get(id) match {
          case null => reload()
          case vo => editAndSave(vo)
        }
      }
    }
    w.miDelete.setCommandHandler { _ =>
      whenSelected(w.tblIP) { id =>
        new ConfirmationDialog("Delete selected IP access?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(ipAccessDao delete id) match {
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

  def canManage = Current.ui.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage IP access")

  /** Edit in modal dialog. */
  private def editAndSave(vo: IPAccess) {
    val id = vo.getId
    val isNew = id == null
    val dialogTitle = if(isNew) "Create new IP access" else "Edit IP access"

    new OkCancelDialog(dialogTitle) |>> { dlg =>
      dlg.mainWidget = new IPAccessEditorWidget |>> { w =>

        w.txtId.value = if (isNew) "" else id.toString
        w.userPickerWidget.txtLoginName.value = vo.getUserId.asOption
          .map(userId => roleMapper.getUser(userId.intValue))
          .map(user => user.getLoginName).getOrElse("")

        w.txtFrom.value = vo.getStart.asOption.map(toDDN).getOrElse("")
        w.txtTo.value = vo.getEnd.asOption.map(toDDN).getOrElse("")
        w.userPickerWidget.btnChoose.addClickHandler { _ =>
          new UserSingleSelectDialog |>> { dlg =>
            dlg.setOkButtonHandler {
              w.userPickerWidget.txtLoginName.value = dlg.search.selection.head.getLoginName
            }
          } |> Current.ui.addWindow
        }

        dlg.setOkButtonHandler {
          vo.clone |> { voc =>
            // todo: validate
            voc.setUserId(roleMapper.getUser(w.userPickerWidget.txtLoginName.value).getId)
            voc.setStart(fromDDN(w.txtFrom.value))
            voc.setEnd(fromDDN(w.txtTo.value))

            app.privileged(permission) {
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
    widget.tblIP.removeAllItems
    for {
      vo <- ipAccessDao.getAll.asScala
      id = vo.getId
      user = roleMapper getUser vo.getUserId.intValue
    } widget.tblIP.addItem(Array[AnyRef](id, user.getLoginName, toDDN(vo.getStart), toDDN(vo.getEnd)), id)

    canManage |> { value =>
      widget.tblIP.setSelectable(value)
      Seq(widget.miNew, widget.miEdit, widget.miDelete).foreach(_.setEnabled(value))
    }

    handleSelection()
  }

  private def handleSelection() {
    (canManage && widget.tblIP.isSelected) |> { enabled =>
      Seq(widget.miEdit, widget.miDelete).foreach(_.setEnabled(enabled))
    }
  }
} // class IPAccessManager

class IPAccessManagerWidget extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblIP = new Table with SingleSelect[JInteger] with Immediate
  val rc = new ReloadableContentWidget(tblIP)

  addContainerProperties(tblIP,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("IP range from"),
    PropertyDescriptor[String]("IP range to"))

  this.addComponents(mb, rc)
}


class IPAccessEditorWidget extends FormLayout with UndefinedSize {
  class UserPickerWidget extends HorizontalLayout with Spacing with UndefinedSize {
    val txtLoginName = new TextField  { setInputPrompt("No user selected") }    // with ReadOnly
    val btnChoose = new Button("...") { setStyleName("small") }

    this.addComponents(txtLoginName, btnChoose)
    setCaption("User")
  }

  val txtId = new TextField("Id") with Disabled
  val userPickerWidget = new UserPickerWidget
  val txtFrom = new TextField("From")
  val txtTo = new TextField("To")

  this.addComponents(txtId, userPickerWidget, txtFrom, txtTo)
}