package com.imcode
package imcms.admin.access.ip

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcms.security.{PermissionGranted, PermissionDenied}

// todo: ipv4; add/handle ipv6
class IPAccessManager(app: ImcmsApplication) {}

class IPAccessManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miHelp = mb.addItem("Help", Help16)
  val tblIp = new Table with SingleSelect2[JInteger] with Immediate
  val rc = new ReloadableContentUI(tblIp)

  addContainerProperties(tblIp,
    CP[JInteger]("Id"),
    CP[String]("Name"),
    CP[String]("IP range from"),
    CP[String]("IP range to"))

  addComponents(this, mb, rc)
}


class IPAccessEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val sltUser = new Select("Users")
  val txtFrom = new TextField("From")
  val txtTo = new TextField("To")

  addComponents(this, txtId, sltUser, txtFrom, txtTo)
}