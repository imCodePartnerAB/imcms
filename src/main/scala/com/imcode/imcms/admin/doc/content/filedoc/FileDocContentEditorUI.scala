package com.imcode
package imcms
package admin.doc.content.filedoc

import com.vaadin.ui.{Table, MenuBar, VerticalLayout}
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms._
import com.imcode.imcms.vaadin.data._

/**
 * File document editor UI
 * File document is `container` which may contain one or more related or unrelated files.
 * If there is more than one file then one of them must be set as default.
 * Default file content is returned when an user clicks on a doc link in a browser.
 */
class FileDocContentEditorUI extends VerticalLayout with Spacing with Margin with FullSize {
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
