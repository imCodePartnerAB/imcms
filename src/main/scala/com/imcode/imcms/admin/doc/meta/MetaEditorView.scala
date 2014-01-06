package com.imcode
package imcms
package admin.doc.meta

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class MetaEditorView extends CustomComponent with FullSize {

  val sp = new HorizontalSplitPanel with FullSize
  val treeEditors = new Tree with SingleSelect[MenuItemId] with NoChildrenAllowed with Immediate with AlwaysFireValueChange[AnyRef]
  val pnlCurrentEditor = new Panel with LightStyle with FullSize

  sp.setFirstComponent(treeEditors)
  sp.setSecondComponent(pnlCurrentEditor)

  setCompositionRoot(sp)
}