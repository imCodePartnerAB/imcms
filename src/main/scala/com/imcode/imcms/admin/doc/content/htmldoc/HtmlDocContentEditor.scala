package com.imcode
package imcms
package admin.doc.content.htmldoc

import com.imcode.imcms.admin.doc.content.DocContentEditor
import com.imcode.imcms.ImcmsServicesSupport
import _root_.imcode.server.document.HtmlDocumentDomainObject


// todo: validate html ???
class HtmlDocContentEditor(doc: HtmlDocumentDomainObject) extends DocContentEditor with ImcmsServicesSupport {

  override type Data = HtmlDocumentDomainObject

  override val view = new HtmlDocContentEditorView

  private val newHtmlTemplate =
    """
      |<!DOCTYPE html>
      |<html>
      |<body>
      |
      |</body>
      |</html>
    """.stripMargin

  resetValues()

  def resetValues() {
    view.editor.setValue(
      if (doc.isNew) newHtmlTemplate else doc.getHtml
    )
  }

  def collectValues(): ErrorsOrData = Right(doc.clone() |>> { _.setHtml(view.editor.getValue) })
}
