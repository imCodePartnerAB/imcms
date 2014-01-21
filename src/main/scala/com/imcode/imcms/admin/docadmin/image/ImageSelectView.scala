package com.imcode
package imcms
package admin.docadmin.image

import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import java.io.File
import com.vaadin.server.Sizeable

// todo: replace images widget with custom widget from v4.
class ImageSelectView extends VerticalLayout with FullSize with Spacing {

  private val mb = new MenuBar
  private val hspContent = new HorizontalSplitPanel with FullSize

  val miUpload = mb.addItem("Upload")
  val miHelp = mb.addItem("Help")

  val dirs = new ListSelect with SingleSelect[File] with Immediate with FullSize
  val images = new ListSelect with SingleSelect[File] with Immediate with FullSize

  hspContent.setFirstComponent(dirs)
  hspContent.setSecondComponent(images)
  hspContent.setSplitPosition(27f, Sizeable.Unit.PERCENTAGE)

  addComponents(mb, hspContent)
  setExpandRatio(hspContent, 1.0f)
}
