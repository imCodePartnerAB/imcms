package com.imcode
package imcms
package admin.instance.file

import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._

class FileManagerView(browserView: FileBrowserView, previewView: FilePreviewView) extends GridLayout(2, 2) with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miFile = mb.addItem("file_mgr.mi.file".i)
  val miFileShow = miFile.addItem("file_mgr.mi.file.show".i)
  val miFileEdit = miFile.addItem("file_mgr.mi.file.edit".i)
  val miFileUpload = miFile.addItem("file_mgr.mi.file.upload".i)
  val miFileDownload = miFile.addItem("file_mgr.mi.file.download".i)
  val miNew = mb.addItem("file_mgr.mi.new".i)
  val miNewDir = miNew.addItem("file_mgr.mi.new.dir".i)
  val miEdit = mb.addItem("file_mgr.mi.edit".i)
  val miEditCopy = miEdit.addItem("file_mgr.mi.edit.copy".i)
  val miEditMove = miEdit.addItem("file_mgr.mi.edit.move".i)
  val miEditRename = miEdit.addItem("file_mgr.mi.edit.rename".i)
  val miEditDelete = miEdit.addItem("file_mgr.mi.edit.delete".i)
  val miView = mb.addItem("file_mgr.mi.view".i)
  val miViewReload = miView.addItem("file_mgr.mi.view.reload".i)
  val miViewPreview = miView.addItem("file_mgr.mi.view.toggle_preview".i)
  val miHelp = mb.addItem("file_mgr.mi.help".i)

  addComponent(mb, 0, 0, 1, 0)
  addComponents(browserView, previewView)
  setComponentAlignment(previewView, Alignment.MIDDLE_CENTER)
  setColumnExpandRatio(0, 1f)
  setRowExpandRatio(1, 1f)
}