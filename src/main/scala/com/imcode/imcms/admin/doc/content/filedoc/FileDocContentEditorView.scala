package com.imcode
package imcms
package admin.doc.content.filedoc

import com.vaadin.ui.{Table, MenuBar, VerticalLayout}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms._
import com.imcode.imcms.vaadin.data._


class FileDocContentEditorView extends VerticalLayout with Spacing with Margin with FullSize {
  val mb = new MenuBar
  val miUpload = mb.addItem("Upload", null)
  val miEditProperties = mb.addItem("Edit properties", null)
  val miDelete = mb.addItem("Delete", null)
  val miMarkAsDefault = mb.addItem("Mark as default", null)

  val tblFiles = new Table with MultiSelect[FileId] with Selectable with Immediate with FullSize {
    addContainerProperties(this,
      PropertyDescriptor[FileId]("Id"),
      PropertyDescriptor[String]("Name"))
  }

  this.addComponents(mb, tblFiles)
  setExpandRatio(tblFiles, 1.0f)
}
