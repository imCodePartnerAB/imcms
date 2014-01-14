package com.imcode
package imcms
package admin.access.user


import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._


class UserManagerView(val projectionView: Component) extends VerticalLayout with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miNew = mb.addItem("mi.new".i)
  val miEdit = mb.addItem("mi.edit".i)
  val miHelp = mb.addItem("mi.help".i)

  this.addComponents(mb, projectionView)
  this.setExpandRatio(projectionView, 1f)
}