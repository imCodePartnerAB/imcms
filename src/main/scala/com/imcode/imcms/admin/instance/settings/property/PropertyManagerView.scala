package com.imcode
package imcms.admin.instance.settings.property

import scala.util.control.{Exception => Ex}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

class PropertyManagerView extends VerticalLayout with FullWidth {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miEdit = mb.addItem("Edit")
  val miHelp = mb.addItem("Help")
  val miReload = mb.addItem("Reload")
  val propertyEditorView = new PropertyEditorView with Margin
//  val dataPanel = new Panel(new VerticalLayout with UndefinedSize with Margin) with UndefinedSize
//
//  dataPanel.setContent(propertyEditorView)
  this.addComponents(mb, propertyEditorView)
}