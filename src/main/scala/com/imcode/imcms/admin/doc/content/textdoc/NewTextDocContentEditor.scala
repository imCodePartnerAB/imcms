package com.imcode
package imcms
package admin.doc.content.textdoc

import com.imcode.imcms.admin.doc.meta.MetaEditor
import com.imcode.imcms.admin.doc.content.DocContentEditor
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject


class NewTextDocContentEditor(doc: TextDocumentDomainObject, metaEditor: MetaEditor) extends DocContentEditor {
  override type Data = TextDocumentDomainObject

  override val view = new NewTextDocContentEditorView |>> { w =>

  }

  override def resetValues() {}

  override def collectValues() = Right(doc)
}
