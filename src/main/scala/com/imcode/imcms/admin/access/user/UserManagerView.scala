package com.imcode
package imcms
package admin.access.user


import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._


class UserManagerView(val searchView: Component) extends VerticalLayout {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("mi.new".i, New16)
  val miEdit = mb.addItem("mi.edit".i, Edit16)
  val miHelp = mb.addItem("mi.help".i, Help16)

  this.addComponents(mb, searchView)
}