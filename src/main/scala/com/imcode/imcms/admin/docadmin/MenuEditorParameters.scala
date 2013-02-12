package com.imcode.imcms.admin.docadmin

import _root_.imcode.server.document.textdocument.TextDocumentDomainObject

case class MenuEditorParameters(doc: TextDocumentDomainObject, menuNo: Int, title: String, returnUrl: String)
