package com.imcode
package imcms
package admin.doc.projection

import com.imcode.imcms.admin.doc.projection.filter.{ExtendedFilterView, BasicFilterView}
import com.vaadin.ui.{VerticalLayout, Panel, GridLayout}
import com.imcode.imcms.vaadin.component.{LightStyle, FullSize}
import com.imcode.imcms.admin.doc.projection.container.IndexedDocsView

class DocsProjectionView(
    basicFilterView: BasicFilterView,
    extendedFilterView: ExtendedFilterView,
    docsView: IndexedDocsView) extends GridLayout(1, 2) with FullSize {

  addComponent(basicFilterView)
  addComponent(docsView)
  setRowExpandRatio(1, 1f)

  def toggleExtendedFilter() { isExtendedFilterVisible = !isExtendedFilterVisible }

  def isExtendedFilterVisible = getComponent(0, 1) == extendedFilterView

  def isExtendedFilterVisible_=(visible: Boolean) {
    removeComponent(0, 1)
    addComponent(if (visible) extendedFilterView else docsView, 0, 1)
    if (visible) {
      basicFilterView.extended.btnCustomize.addStyleName("down")
    } else {
      basicFilterView.extended.btnCustomize.removeStyleName("down")
    }
  }
}
