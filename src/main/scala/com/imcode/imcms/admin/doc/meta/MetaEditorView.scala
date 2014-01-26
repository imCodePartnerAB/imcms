package com.imcode
package imcms
package admin.doc.meta

import com.vaadin.server.Sizeable
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.vaadin.ui.themes.Reindeer


class MetaEditorView extends HorizontalSplitPanel with FullSize {

  val treeEditors = new Tree with SingleSelect[MenuItemId] with NoChildrenAllowed with NoNullSelection with Immediate
                             with AlwaysFireValueChange[AnyRef]

  setFirstComponent(treeEditors)
  setSplitPosition(20, Sizeable.Unit.PERCENTAGE)
  addStyleName(Reindeer.SPLITPANEL_SMALL)
}