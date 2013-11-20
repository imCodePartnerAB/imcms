package com.imcode.imcms.admin.doc.projection

import com.imcode.imcms.admin.doc.projection.filter.{ExtendedFilterWidget, BasicFilterWidget}
import com.vaadin.ui.{Panel, GridLayout}
import com.imcode.imcms.vaadin.component.{LightStyle, FullSize}
import com.imcode.imcms.admin.doc.projection.container.IndexedDocsWidget

class DocsProjectionWidget(
    basicFilterWidget: BasicFilterWidget,
    advancedFilterWidget: ExtendedFilterWidget,
    docsWidget: IndexedDocsWidget) extends GridLayout(1, 2) with FullSize {

  private val pnlExtendedFilterForm = new Panel with FullSize with LightStyle {
    setContent(advancedFilterWidget)
  }

  addComponent(basicFilterWidget)
  addComponent(docsWidget)
  setRowExpandRatio(1, 1f)

  def toggleExtendedFilter() { isExtendedFilterVisible = !isExtendedFilterVisible }

  def isExtendedFilterVisible = getComponent(0, 1) == pnlExtendedFilterForm

  def isExtendedFilterVisible_=(visible: Boolean) {
    removeComponent(0, 1)
    addComponent(if (visible) pnlExtendedFilterForm else docsWidget, 0, 1)
  }
}
