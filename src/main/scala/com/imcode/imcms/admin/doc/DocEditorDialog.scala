package com.imcode
package imcms
package admin.doc

import _root_.imcode.server.document.DocumentDomainObject
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog.{BottomMarginDialog, CustomSizeDialog, OkCancelDialog}


class DocEditorDialog(caption: String, doc: DocumentDomainObject) extends OkCancelDialog(caption) with CustomSizeDialog with BottomMarginDialog {

  val docEditor = new DocEditor(doc)

  mainUI = docEditor.ui

  this.setSize(600, 400)
}


