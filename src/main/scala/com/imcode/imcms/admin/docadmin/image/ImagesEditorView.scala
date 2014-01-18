package com.imcode
package imcms
package admin.docadmin.image

import com.vaadin.ui.{Label, TabSheet, MenuBar, VerticalLayout}
import com.imcode.imcms.vaadin.component.{FullWidth, FullSize, Spacing}

class ImagesEditorView extends VerticalLayout with Spacing with FullSize {

  val mb = new MenuBar with FullWidth
  val miChoose = mb.addItem("Choose")
  val miClear = mb.addItem("Clear")
  val miHistory = mb.addItem("History")
  val miHelp = mb.addItem("Help")
  val tsImages = new TabSheet with FullSize

  addComponents(mb, tsImages)
  setExpandRatio(tsImages, 1f)
}
