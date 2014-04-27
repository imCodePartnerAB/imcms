package com.imcode
package imcms
package admin.doc

import _root_.imcode.server.document.DocumentDomainObject
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.vaadin.ui.VerticalLayout


class DocEditorDialog(caption: String, doc: DocumentDomainObject) extends OkCancelDialog(caption)
with EditorDialog with OKCaptionIsSave
with CustomSizeDialog with Resizable {

  val editor = new DocEditor(doc)

  mainComponent = new VerticalLayout(editor.view) with FullSize |>> { _.addStyleName("doc_editor") }

  this.setSize(750, 600)
}


