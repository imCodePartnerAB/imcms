package com.imcode
package imcms
package admin.doc.content.urldoc

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.admin.doc.content.DocContentEditor
import _root_.imcode.server.document.UrlDocumentDomainObject

class UrlDocContentEditor(doc: UrlDocumentDomainObject) extends DocContentEditor {
  type Data = UrlDocumentDomainObject

  val ui = new UrlDocContentEditorUI

  resetValues()

  def resetValues() {
    ui.txtURL.value = doc.getUrl
  }

  def collectValues() = doc.clone() |>> { clone =>
    clone.setUrl(ui.txtURL.value)
    clone.setTarget(ui.cbTarget.value)
  } |> Right.apply
}

