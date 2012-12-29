package com.imcode
package imcms
package admin.doc.meta

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._

/**
 * Editor UI's main component is a horizontal split panel.
 * -Left component - navigation tree.
 * -Right component - scrollable panel.
 */
class MetaEditorUI extends VerticalLayout with FullSize with NoMargin {

  val sp = new HorizontalSplitPanel with FullSize
  val treeMenu = new Tree with SingleSelect[MenuItemId] with NoChildrenAllowed with Immediate
  val pnlMenuItem = new Panel with LightStyle with FullSize

  sp.setFirstComponent(treeMenu)
  sp.setSecondComponent(pnlMenuItem)

  addComponent(sp)
}