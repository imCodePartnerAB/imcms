package com.imcode
package imcms.admin.instance.settings.property

import com.imcode.imcms.vaadin.Current
import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcms.security.{PermissionGranted, PermissionDenied}
import imcode.server.Imcms
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

//todo: move to system dir + monitor
// todo: updateReadOnly ->

class PropertyManagerManager {

  val view = new PropertyManagerView |>> { w =>
    w.rc.btnReload.addClickHandler { _ => reload() }
    w.miEdit.setCommandHandler { _ =>
      new OkCancelDialog("Edit system properties") |>> { dlg =>
        dlg.mainComponent = new PropertyEditorView |>> { eui =>
          Imcms.getServices.getSystemData |> { d =>
            eui.txtStartPageNumber.value = d.getStartDocument.toString
            eui.txaSystemMessage.value = d.getSystemMessage
            eui.webMaster.txtName.value = d.getWebMaster
            eui.webMaster.txtEmail.value = d.getWebMasterAddress
            eui.serverMaster.txtName.value = d.getServerMaster
            eui.serverMaster.txtEmail.value = d.getServerMasterAddress
          }

          dlg.setOkButtonHandler {
            Current.ui.privileged(permission) {
              val systemData = Imcms.getServices.getSystemData |>> { d =>
                d.setStartDocument(eui.txtStartPageNumber.value.toInt)
                d.setSystemMessage(eui.txaSystemMessage.value)
                d.setWebMaster(eui.webMaster.txtName.value)
                d.setWebMasterAddress(eui.webMaster.txtEmail.value)
                d.setServerMaster(eui.serverMaster.txtName.value)
                d.setServerMasterAddress(eui.serverMaster.txtEmail.value)
              }

              Ex.allCatch.either(Imcms.getServices.setSystemData(systemData)) match {
                case Right(_) =>
                  Current.page.showInfoNotification("System properties has been updated")
                  reload()
                case Left(ex) =>
                  Current.page.showErrorNotification("Internal error")
                  throw ex
              }
            }
          }
        }
      } |> Current.ui.addWindow
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = Current.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage system properties")

  def reload() {
    import view.propertyEditorView._

    Seq(txtStartPageNumber, txaSystemMessage, webMaster.txtName, webMaster.txtEmail, serverMaster.txtName,
        serverMaster.txtEmail).foreach { txt =>
      txt.setReadOnly(false)
    }

    Imcms.getServices.getSystemData |> { d =>
      txtStartPageNumber.value = d.getStartDocument.toString
      txaSystemMessage.value = d.getSystemMessage
      webMaster.txtName.value = d.getWebMaster
      webMaster.txtEmail.value = d.getWebMasterAddress
      serverMaster.txtName.value = d.getServerMaster
      serverMaster.txtEmail.value = d.getServerMasterAddress
    }

    Seq(txtStartPageNumber, txaSystemMessage, webMaster.txtName, webMaster.txtEmail, serverMaster.txtName,
        serverMaster.txtEmail).foreach { txt =>
      txt.setReadOnly(true)
    }

    Seq(view.miEdit).foreach(_.setEnabled(canManage))
  }
} // class PropertyManager

class PropertyManagerView extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miEdit = mb.addItem("Edit", Edit16)
  val miHelp = mb.addItem("Help", Help16)
  val propertyEditorView = new PropertyEditorView
  val dataPanel = new Panel(new VerticalLayout with UndefinedSize with Margin) with UndefinedSize
  val rc = new ReloadableContentView(dataPanel)

  dataPanel.setContent(propertyEditorView)
  this.addComponents(mb, rc)
}


class PropertyEditorView extends FormLayout with UndefinedSize {
  class ContactComponent(caption: String) extends VerticalLayout() with UndefinedSize with Spacing {
    val txtName = new TextField("Name")
    val txtEmail = new TextField("e-mail")

    setCaption(caption)
    this.addComponents(txtName, txtEmail)
  }

  val txtStartPageNumber = new TextField("Start page number")
  val txaSystemMessage = new TextArea("System message") |>> { _.setRows(5) }
  val serverMaster = new ContactComponent("Server master")
  val webMaster = new ContactComponent("Web master")

  this.addComponents(txtStartPageNumber, txaSystemMessage, serverMaster, webMaster)
}