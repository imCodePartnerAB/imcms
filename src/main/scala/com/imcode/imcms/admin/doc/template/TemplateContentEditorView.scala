package com.imcode
package imcms.admin.doc.template

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class TemplateContentEditorView extends VerticalLayout with FullSize {
  val txaContent = new TextArea with FullSize |>> {
    _.setRows(20)
  }

  addComponent(txaContent)
}