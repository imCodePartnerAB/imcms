package com.imcode
package imcms
package admin.doc

import com.vaadin.ui.{MenuBar, VerticalLayout}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.admin.doc.projection.DocsProjectionView

class DocSelectDialogView(docsProjectionView: DocsProjectionView) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar with FullWidth
  val miNew = mb.addItem("doc_mgr.mi.new".i)
  val miNewTextDoc = miNew.addItem("doc_mgr.mi.new.text_doc".i)
  val miNewFileDoc = miNew.addItem("doc_mgr.mi.new.file_doc".i)
  val miNewUrlDoc = miNew.addItem("doc_mgr.mi.new.url_doc".i)

  val miCopySelectedDoc = mb.addItem("doc_mgr.mi.copy".i)
  val miDeleteSelectedDocs = mb.addItem("doc_mgr.action.delete".i)

  val miShowSelectedDoc = mb.addItem("doc_mgr.mi.show".i)
  val miHelp = mb.addItem("doc_mgr.mi.help".i)

  addComponents(mb, docsProjectionView)
  setExpandRatio(docsProjectionView, 1f)
}
