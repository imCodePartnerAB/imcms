package com.imcode
package imcms.admin.instance.settings.property

import scala.util.control.{Exception => Ex}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

class PropertyManagerView extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miEdit = mb.addItem("Edit", Edit16)
  val miHelp = mb.addItem("Help", Help16)
  val miReload = mb.addItem("Reload", Reload16)
  val propertyEditorView = new PropertyEditorView
  val dataPanel = new Panel(new VerticalLayout with UndefinedSize with Margin) with UndefinedSize

  dataPanel.setContent(propertyEditorView)
  this.addComponents(mb, dataPanel)
}