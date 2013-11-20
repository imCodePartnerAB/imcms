package com.imcode
package imcms
package admin.doc

import _root_.imcode.server.document.DocumentDomainObject
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog.{Resizable, BottomContentMarginDialog, CustomSizeDialog, OkCancelDialog}


class DocEditorDialog(caption: String, doc: DocumentDomainObject) extends OkCancelDialog(caption)
    with CustomSizeDialog with BottomContentMarginDialog with Resizable {

  val docEditor = new DocEditor(doc)

  mainWidget = docEditor.widget
  btnOk.setCaption("btn_save".i)

  this.setSize(700, 600)
}


