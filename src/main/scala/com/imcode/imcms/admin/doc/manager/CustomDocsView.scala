package com.imcode
package imcms
package admin.doc.manager

import com.vaadin.ui.{MenuBar, VerticalLayout, Component}
import com.imcode.imcms.vaadin.component._

class CustomDocsView(projectionView: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.selection.mi.doc".i)

  this.addComponents(mb, projectionView)
  setExpandRatio(projectionView, 1.0f)
}
