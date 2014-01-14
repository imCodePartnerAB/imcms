package com.imcode
package imcms.admin.instance.monitor.session.counter


import com.imcode.imcms.vaadin.component._
import com.vaadin.ui.{Panel, MenuBar, VerticalLayout}

class SessionCounterManagerView extends VerticalLayout with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miEdit = mb.addItem("Edit", Edit16)
  val miReset = mb.addItem("Reset", Delete16)
  val miReload = mb.addItem("Reload", Reload16)
  val miHelp = mb.addItem("Help", Help16)
  val frmValues = new SessionCounterForm with Margin
  val pnlValues = new Panel(new VerticalLayout with UndefinedSize) with UndefinedSize

  pnlValues.setContent(frmValues)
  this.addComponents(mb, pnlValues)
}