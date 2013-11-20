package com.imcode.imcms.admin.doc.content

import com.imcode.imcms.vaadin.Editor
import _root_.imcode.server.document.DocumentDomainObject


trait DocContentEditor extends Editor {
  override type Data <: DocumentDomainObject
}
