package com.imcode
package imcms.admin.doc.template

import scala.util.control.{Exception => Ex}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class TemplateRenameView extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}
