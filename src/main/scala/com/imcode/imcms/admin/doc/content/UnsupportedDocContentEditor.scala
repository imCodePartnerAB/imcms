package com.imcode.imcms.admin.doc.content

import imcode.server.document.DocumentDomainObject


class UnsupportedDocContentEditor(doc: DocumentDomainObject) extends DocContentEditor {

  type Data = DocumentDomainObject

  val ui = new UnsuppotedDocContentEditorUI

  def resetValues() {}

  def collectValues() = Right(doc)
}
