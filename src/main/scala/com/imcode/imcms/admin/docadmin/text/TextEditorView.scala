package com.imcode
package imcms
package admin.docadmin.text

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class TextEditorView extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar with FullWidth
  val miFormat = mb.addItem("Format")
  val miFormatHtml = miFormat.addItem("HTML") |>> {
    _.setCheckable(true)
  }
  val miFormatPlain = miFormat.addItem("Plain text") |>> {
    _.setCheckable(true)
  }
  val miHistory = mb.addItem("History")
  val miHelp = mb.addItem("Help")
  val tsTexts = new TabSheet with FullSize
  val lblStatus = new Label

  addComponents(mb, tsTexts, lblStatus)
  setExpandRatio(tsTexts, 1f)
}
