package com.imcode
package imcms
package admin.doc.manager

import com.imcode.imcms.admin.doc.projection.DocsProjectionView
import com.vaadin.ui.{MenuBar, VerticalLayout}
import com.imcode.imcms.vaadin.component._


class DocManagerView(projectionView: DocsProjectionView) extends VerticalLayout with FullSize {
  val mb = new MenuBar with MenuBarInTabStyle with FullWidth
  val miNew = mb.addItem("doc_mgr.mi.new".i)
  val miNewTextDoc = miNew.addItem("doc_mgr.mi.new.text_doc".i)
  val miNewFileDoc = miNew.addItem("doc_mgr.mi.new.file_doc".i)
  val miNewUrlDoc = miNew.addItem("doc_mgr.mi.new.url_doc".i)
  val miNewHtmlDoc = miNew.addItem("doc_mgr.mi.new.html_doc".i)

  val miCopy = mb.addItem("doc_mgr.mi.copy".i)
  val miEdit = mb.addItem("doc_mgr.action.edit".i)
  val miDelete = mb.addItem("doc_mgr.action.delete".i)

  val miShow = mb.addItem("doc_mgr.mi.show".i)
  //val miViewContent = miView.addItem("doc_mgr.mi.view.content".i)
  //val miViewStructure = miView.addItem("doc_mgr.mi.view.structure".i)

  //val miSelection = mb.addItem("doc_mgr.mi.selection".i)
  //val miSelectionShow = miSelection.addItem("doc_mgr.mi.selection.show".i)
  val miProfile = mb.addItem("doc_mgr.mi.profile".i)

  val miProfileEditName = miProfile.addItem("doc_mgr.mi.profile.edit_name".i)

  addComponents(mb, projectionView)
  setExpandRatio(projectionView, 1.0f)
}
