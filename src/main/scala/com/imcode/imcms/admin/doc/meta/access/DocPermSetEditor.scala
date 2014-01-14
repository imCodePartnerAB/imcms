package com.imcode
package imcms
package admin.doc.meta.access

import imcode.server.document._
import com.imcode.imcms.vaadin.Editor

trait DocPermSetEditor extends Editor {
  override type Data = TextDocumentPermissionSetDomainObject
}