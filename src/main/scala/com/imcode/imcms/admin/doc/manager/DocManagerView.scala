package com.imcode
package imcms
package admin.doc.manager

import com.imcode.imcms.admin.doc.projection.DocsProjectionView
import com.vaadin.ui.{MenuBar, VerticalLayout}
import com.imcode.imcms.vaadin.component._


class DocManagerView(projectionView: DocsProjectionView) extends VerticalLayout with FullSize {
  val mb = new MenuBar with FullWidth
  val miNew = mb.addItem("doc.mgr.mi.new".i)
  val miNewTextDoc = miNew.addItem("doc.mgr.mi.new.text_doc".i)
  val miNewFileDoc = miNew.addItem("doc.mgr.mi.new.file_doc".i)
  val miNewUrlDoc = miNew.addItem("doc.mgr.mi.new.url_doc".i)
  val miNewHtmlDoc = miNew.addItem("doc.mgr.mi.new.html_doc".i)

  val miCopy = mb.addItem("doc.mgr.mi.copy".i)
  val miEdit = mb.addItem("doc.mgr.action.edit".i)
  val miDelete = mb.addItem("doc.mgr.action.delete".i)

  val miShow = mb.addItem("doc.mgr.mi.show".i)
  //val miViewContent = miView.addItem("doc.mgr.mi.view.content".i)
  //val miViewStructure = miView.addItem("doc.mgr.mi.view.structure".i)

  //val miSelection = mb.addItem("doc.mgr.mi.selection".i)
  //val miSelectionShow = miSelection.addItem("doc.mgr.mi.selection.show".i)
  val miProfile = mb.addItem("doc.mgr.mi.profile".i)

  val miProfileEditName = miProfile.addItem("doc.mgr.mi.profile.edit_name".i)

  this.addComponents(mb, projectionView)
  setExpandRatio(projectionView, 1.0f)
}
