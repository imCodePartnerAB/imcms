package com.imcode
package imcms
package admin.access.ip

import scala.util.control.{Exception => Ex}
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._

class IPAccessEditorView extends FormLayout with UndefinedSize {
  class UserPickerComponent extends HorizontalLayout with Spacing with UndefinedSize {
    val txtLoginName = new TextField  { setInputPrompt("No user selected") }    // with ReadOnly
    val btnChoose = new Button("...") { setStyleName("small") }

    this.addComponents(txtLoginName, btnChoose)
    setCaption("User")
  }

  val txtId = new TextField("Id") with Disabled
  val userPickerComponent = new UserPickerComponent
  val txtFrom = new TextField("From")
  val txtTo = new TextField("To")

  this.addComponents(txtId, userPickerComponent, txtFrom, txtTo)
}