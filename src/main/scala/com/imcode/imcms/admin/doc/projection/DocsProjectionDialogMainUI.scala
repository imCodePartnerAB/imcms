package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.ui.{MenuBar, VerticalLayout}
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._

class DocsProjectionDialogMainUI(docsProjectionUI: DocsProjectionUI) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar with FullWidth
  val miNew = mb.addItem("doc.mgr.mi.new".i)
  val miNewTextDoc = miNew.addItem("doc.mgr.mi.new.text_doc".i)
  val miNewFileDoc = miNew.addItem("doc.mgr.mi.new.file_doc".i)
  val miNewUrlDoc = miNew.addItem("doc.mgr.mi.new.url_doc".i)

  val miCopySelectedDoc = mb.addItem("doc.mgr.mi.copy".i)
  val miDeleteSelectedDocs = mb.addItem("doc.mgr.action.delete".i)

  val miShowSelectedDoc = mb.addItem("doc.mgr.mi.show".i)
  val miHelp = mb.addItem("doc.mgr.mi.help".i)

  this.addComponents(mb, docsProjectionUI)
  setExpandRatio(docsProjectionUI, 1f)
}
