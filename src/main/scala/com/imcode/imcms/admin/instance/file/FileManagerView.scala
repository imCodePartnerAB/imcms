package com.imcode
package imcms
package admin.instance.file

import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._

class FileManagerView(browserView: FileBrowserView, previewView: FilePreviewView) extends GridLayout(2, 2) with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miFile = mb.addItem("file.mgr.menu.file".i)
  val miFileShow = miFile.addItem("file.mgr.menu.file.show".i)
  val miFileEdit = miFile.addItem("file.mgr.menu.file.edit".i)
  val miFileUpload = miFile.addItem("file.mgr.menu.file.upload".i)
  val miFileDownload = miFile.addItem("file.mgr.menu.file.download".i)
  val miNew = mb.addItem("file.mgr.menu.new".i)
  val miNewDir = miNew.addItem("file.mgr.menu.new.dir".i)
  val miEdit = mb.addItem("file.mgr.menu.edit".i)
  val miEditCopy = miEdit.addItem("file.mgr.menu.edit.copy".i)
  val miEditMove = miEdit.addItem("file.mgr.menu.edit.move".i)
  val miEditRename = miEdit.addItem("file.mgr.menu.edit.rename".i)
  val miEditDelete = miEdit.addItem("file.mgr.menu.edit.delete".i)
  val miView = mb.addItem("file.mgr.menu.view".i)
  val miViewReload = miView.addItem("file.mgr.menu.view.reload".i)
  val miViewPreview = miView.addItem("file.mgr.menu.view.toggle_preview".i)
  val miHelp = mb.addItem("file.mgr.menu.help".i)

  addComponent(mb, 0, 0, 1, 0)
  addComponents(browserView, previewView)
  setComponentAlignment(previewView, Alignment.MIDDLE_CENTER)
  setColumnExpandRatio(0, 1f)
  setRowExpandRatio(1, 1f)
}