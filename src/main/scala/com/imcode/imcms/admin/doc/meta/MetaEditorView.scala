package com.imcode
package imcms
package admin.doc.meta

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui.themes.Reindeer


class MetaEditorView extends CustomComponent with FullSize {

  val sp = new HorizontalSplitPanel with FullSize |>> { _.addStyleName(Reindeer.SPLITPANEL_SMALL) }
  val treeEditors = new Tree with SingleSelect[MenuItemId] with NoChildrenAllowed with NoNullSelection with Immediate with AlwaysFireValueChange[AnyRef]
  val pnlCurrentEditor = new Panel with LightStyle with FullSize

  sp.setFirstComponent(treeEditors)
  sp.setSecondComponent(pnlCurrentEditor)

  setCompositionRoot(sp)
}