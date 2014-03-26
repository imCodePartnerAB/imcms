package com.imcode.imcms.admin.docadmin.text

import _root_.imcode.server.document.textdocument.TextDomainObject

case class TextEditorOpts(format: TextDomainObject.Format,
                              rowCountOpt: Option[Int],
                              canChangeFormat: Boolean,
                              showModeEditor: Boolean)