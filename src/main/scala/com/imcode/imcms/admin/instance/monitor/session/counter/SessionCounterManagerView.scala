package com.imcode
package imcms.admin.instance.monitor.session.counter


import com.imcode.imcms.vaadin.component._
import com.vaadin.ui.{Panel, MenuBar, VerticalLayout}

class SessionCounterManagerView extends VerticalLayout with FullWidth {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miEdit = mb.addItem("Edit")
  val miReset = mb.addItem("Reset")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")
  val frmValues = new SessionCounterForm with Margin
//  val pnlValues = new Panel(new VerticalLayout with UndefinedSize) with UndefinedSize
//
//  pnlValues.setContent(frmValues)
  this.addComponents(mb, frmValues)
}