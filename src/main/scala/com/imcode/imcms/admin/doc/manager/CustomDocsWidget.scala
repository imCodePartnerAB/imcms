package com.imcode
package imcms
package admin.doc.manager

import com.vaadin.ui.{MenuBar, VerticalLayout, Component}
import com.imcode.imcms.vaadin.component._

class CustomDocsWidget(projectionWidget: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.selection.mi.doc".i)

  this.addComponents(mb, projectionWidget)
  setExpandRatio(projectionWidget, 1.0f)
}
