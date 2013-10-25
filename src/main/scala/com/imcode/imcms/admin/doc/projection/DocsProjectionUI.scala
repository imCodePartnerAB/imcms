package com.imcode.imcms.admin.doc.projection

import com.imcode.imcms.admin.doc.projection.filter.{ExtendedFilterUI, BasicFilterUI}
import com.vaadin.ui.{Panel, GridLayout}
import com.imcode.imcms.vaadin.ui.{LightStyle, FullSize}
import com.imcode.imcms.admin.doc.projection.container.IndexedDocsUI

class DocsProjectionUI(
    basicFilterUI: BasicFilterUI,
    advancedFilterUI: ExtendedFilterUI,
    docsUI: IndexedDocsUI) extends GridLayout(1, 2) with FullSize {

  private val pnlExtendedFilterForm = new Panel with FullSize with LightStyle {
    setContent(advancedFilterUI)
  }

  addComponent(basicFilterUI)
  addComponent(docsUI)
  setRowExpandRatio(1, 1f)

  def toggleExtendedFilter() { isExtendedFilterVisible = !isExtendedFilterVisible }

  def isExtendedFilterVisible = getComponent(0, 1) == pnlExtendedFilterForm

  def isExtendedFilterVisible_=(visible: Boolean) {
    removeComponent(0, 1)
    addComponent(if (visible) pnlExtendedFilterForm else docsUI, 0, 1)
  }
}
