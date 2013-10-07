package com.imcode.imcms.admin.doc.manager

import com.vaadin.ui.{MenuBar, VerticalLayout, Component}
import com.imcode.imcms.vaadin.ui._

class CustomDocsUI(projectionUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.selection.mi.doc".i)

  this.addComponents(mb, projectionUI)
  setExpandRatio(projectionUI, 1.0f)
}
