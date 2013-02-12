package com.imcode.imcms.admin.docadmin

import _root_.imcode.server.document.textdocument.TextDomainObject

case class TextEditorParameters(format: TextDomainObject.Format,
                              rowCountOpt: Option[Int],
                              canChangeFormat: Boolean,
                              showModeEditor: Boolean)