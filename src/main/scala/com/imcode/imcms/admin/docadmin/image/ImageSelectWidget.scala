package com.imcode
package imcms
package admin.docadmin.image

import com.vaadin.ui.{ListSelect, VerticalSplitPanel}

import com.imcode.imcms.vaadin.component._
import java.io.File
import com.vaadin.server.Sizeable

// todo: replace images widget with custom widget from v4.
class ImageSelectWidget extends VerticalSplitPanel with FullSize {

  val dirs = new ListSelect with SingleSelect[File] with Immediate with FullSize
  val images = new ListSelect with SingleSelect[File] with Immediate with FullSize

  setFirstComponent(dirs)
  setSecondComponent(images)
  setSplitPosition(25f, Sizeable.Unit.PERCENTAGE)
}
