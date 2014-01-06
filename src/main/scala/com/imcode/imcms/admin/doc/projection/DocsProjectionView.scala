package com.imcode.imcms.admin.doc.projection

import com.imcode.imcms.admin.doc.projection.filter.{ExtendedFilterView, BasicFilterView}
import com.vaadin.ui.{Panel, GridLayout}
import com.imcode.imcms.vaadin.component.{LightStyle, FullSize}
import com.imcode.imcms.admin.doc.projection.container.IndexedDocsView

class DocsProjectionView(
    basicFilterView: BasicFilterView,
    extendedFilterView: ExtendedFilterView,
    docsView: IndexedDocsView) extends GridLayout(1, 2) with FullSize {

  private val pnlExtendedFilterForm = new Panel with FullSize with LightStyle {
    setContent(extendedFilterView)
  }

  addComponent(basicFilterView)
  addComponent(docsView)
  setRowExpandRatio(1, 1f)

  def toggleExtendedFilter() { isExtendedFilterVisible = !isExtendedFilterVisible }

  def isExtendedFilterVisible = getComponent(0, 1) == pnlExtendedFilterForm

  def isExtendedFilterVisible_=(visible: Boolean) {
    removeComponent(0, 1)
    addComponent(if (visible) pnlExtendedFilterForm else docsView, 0, 1)
  }
}
