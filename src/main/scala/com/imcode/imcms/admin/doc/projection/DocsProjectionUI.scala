package com.imcode
package imcms
package admin.doc.projection

import com.imcode.imcms.admin.doc.projection.filter.{AdvancedFilterUI, BasicFilterUI}
import com.vaadin.ui.{Panel, GridLayout}
import com.imcode.imcms.vaadin.ui.{LightStyle, Scrollable, FullSize}

class DocsProjectionUI(
    basicFilterUI: BasicFilterUI,
    advancedFilterUI: AdvancedFilterUI,
    docsUI: IndexedDocsUI) extends GridLayout(1, 2) with FullSize {

  private val pnlAdvancedFilterForm = new Panel with Scrollable with FullSize with LightStyle {
    setContent(advancedFilterUI)
  }

  addComponent(basicFilterUI)
  addComponent(docsUI)
  setRowExpandRatio(1, 1f)

  def toggleAdvancedFilter() { isAdvancedFilterVisible = !isAdvancedFilterVisible }

  def isAdvancedFilterVisible = getComponent(0, 1) == pnlAdvancedFilterForm

  def isAdvancedFilterVisible_=(visible: Boolean) {
    removeComponent(0, 1)
    addComponent(if (visible) pnlAdvancedFilterForm else docsUI, 0, 1)
  }
}
