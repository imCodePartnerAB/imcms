package com.imcode.imcms.admin.doc.content

import imcode.server.document.DocumentDomainObject


class UnavailableDocContentEditor(doc: DocumentDomainObject) extends DocContentEditor {

  type Data = DocumentDomainObject

  val ui = new UnavailableDocContentEditorUI

  def resetValues() {}

  def collectValues() = Right(doc)
}
