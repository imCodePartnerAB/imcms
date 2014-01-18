package com.imcode
package imcms
package admin.doc.content.urldoc

import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.admin.doc.content.DocContentEditor
import _root_.imcode.server.document.UrlDocumentDomainObject

class UrlDocContentEditor(doc: UrlDocumentDomainObject) extends DocContentEditor {

  override type Data = UrlDocumentDomainObject

  val view = new UrlDocContentEditorView

  resetValues()

  override def resetValues() {
    view.txtURL.value = doc.getUrl
  }

  override def collectValues(): ErrorsOrData = doc.clone() |>> { clone =>
    clone.setUrl(view.txtURL.value)
    clone.setTarget(view.cbTarget.selection)
  } |> Right.apply
}

