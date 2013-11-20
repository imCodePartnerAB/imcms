package com.imcode.imcms.admin.doc.content

import imcode.server.document.DocumentDomainObject


class UnsupportedDocContentEditor(doc: DocumentDomainObject) extends DocContentEditor {

  override type Data = DocumentDomainObject

  override val widget = new UnsupportedDocContentEditorWidget

  override def resetValues() {}

  override def collectValues(): ErrorsOrData = Right(doc)
}
