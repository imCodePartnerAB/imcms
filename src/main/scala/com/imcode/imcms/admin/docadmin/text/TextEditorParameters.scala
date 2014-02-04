package com.imcode.imcms.admin.docadmin.text

import _root_.imcode.server.document.textdocument.TextDomainObject

case class TextEditorParameters(format: TextDomainObject.Type,
                              rowCountOpt: Option[Int],
                              canChangeFormat: Boolean,
                              showModeEditor: Boolean)