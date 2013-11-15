package com.imcode
package imcms
package admin.docadmin.image

import com.vaadin.ui.{Label, TabSheet, MenuBar, VerticalLayout}
import com.imcode.imcms.vaadin.ui.{FullWidth, FullSize, Spacing}

class ImageEditorUI extends VerticalLayout with Spacing with FullSize {

  val mb = new MenuBar with FullWidth
  val miHistory = mb.addItem("History")
  val miHelp = mb.addItem("Help")
  val tsImages = new TabSheet with FullSize

  this.addComponents(mb, tsImages)
  setExpandRatio(tsImages, 1f)
}
