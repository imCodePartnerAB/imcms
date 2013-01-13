package com.imcode
package imcms
package admin.doc.content.textdoc

import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.admin.doc.content.DocContentEditor
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject


class NewTextDocContentEditor(doc: TextDocumentDomainObject, metaEditor: MetaEditor) extends DocContentEditor {
  type Data = TextDocumentDomainObject

  val ui = new NewTextDocContentEditorUI |>> { ui =>

  } //ui

  def resetValues() {}

  def collectValues() = Right(doc)
}
