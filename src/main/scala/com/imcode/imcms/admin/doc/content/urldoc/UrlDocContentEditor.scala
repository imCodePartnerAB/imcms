package com.imcode
package imcms
package admin.doc.content.urldoc

import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.admin.doc.content.DocContentEditor
import _root_.imcode.server.document.UrlDocumentDomainObject

class UrlDocContentEditor(doc: UrlDocumentDomainObject) extends DocContentEditor {

  override type Data = UrlDocumentDomainObject

  val widget = new UrlDocContentEditorWidget

  resetValues()

  override def resetValues() {
    widget.txtURL.value = doc.getUrl
  }

  override def collectValues(): ErrorsOrData = doc.clone() |>> { clone =>
    clone.setUrl(widget.txtURL.value)
    clone.setTarget(widget.cbTarget.value)
  } |> Right.apply
}

