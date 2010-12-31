package com.imcode
package imcms.admin.settings.property

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcms.security.{PermissionGranted, PermissionDenied}
import imcode.util.Utility.{ipLongToString, ipStringToLong}
import com.vaadin.ui.Window.Notification

//todo: move to system dir + monitor

class PropertyManagerManager(app: ImcmsApplication) {

  val ui = letret(new PropertyManagerUI) { ui =>
    ui.rc.btnReload addListener block { reload() }
    ui.miEdit setCommand block {
//      let(new SystemData) { d =>
//        d setStartDocument pnlStartPage.txtNumber.getValue.asInstanceOf[String].toInt
//        d setSystemMessage pnlSystemMessage.txtMessage.getValue.asInstanceOf[String]
//        d setServerMaster pnlServerMaster.txtName.getValue.asInstanceOf[String]
//        d setServerMasterAddress pnlServerMaster.txtEmail.getValue.asInstanceOf[String]
//        d setWebMaster pnlWebMaster.txtName.getValue.asInstanceOf[String]
//        d setWebMasterAddress pnlWebMaster.txtEmail.getValue.asInstanceOf[String]
//
//        Imcms.getServices.setSystemData(d)
//      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage system properties")

  def reload() {
    import ui.dataUI._

    let(Imcms.getServices.getSystemData) { d =>
      frmStartPage.txtNumber.value = d.getStartDocument.toString
      frmSystemMessage.txtText.value = d.getSystemMessage
      frmWebMaster.txtName.value = d.getWebMaster
      frmWebMaster.txtEmail.value = d.getWebMasterAddress
      frmServerMaster.txtName.value = d.getServerMaster
      frmServerMaster.txtEmail.value = d.getServerMasterAddress
    }

    forlet(ui.miEdit) { _ setEnabled canManage }
  }
} // class PropertyManager

class PropertyManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miEdit = mb.addItem("Edit", Edit16)
  val miHelp = mb.addItem("Help", Help16)
  val dataUI = new PropertyEditorUI
  val rc = new ReloadableContentUI(dataUI)

  addComponents(this, mb, rc)
}


class PropertyEditorUI extends VerticalLayout with UndefinedSize {
  val frmStartPage = new Form(new VerticalLayout with UndefinedSize) {
    setCaption("Start page")
    val txtNumber = new TextField("Number")

    addComponents(this.getLayout, txtNumber)
  }

  val frmSystemMessage = new Form(new VerticalLayout with UndefinedSize) {
    setCaption("System message")
    val txtText = new TextField("Text") { setRows(5) }

    addComponents(this.getLayout, txtText)
  }

  val frmServerMaster = new Form(new VerticalLayout with UndefinedSize) {
    val txtName = new TextField("Name")
    val txtEmail = new TextField("e-mail")

    addComponents(this.getLayout, txtName, txtEmail)
  }

  val frmWebMaster = new Form(new VerticalLayout with UndefinedSize) {
    val txtName = new TextField("Name")
    val txtEmail = new TextField("e-mail")

    addComponents(this.getLayout, txtName, txtEmail)
  }

  addComponents(this, frmStartPage, frmSystemMessage, frmServerMaster, frmWebMaster)
}