package com.imcode
package imcms
package admin.doc.projection.filter

import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.vaadin.server.ExternalResource


class AdvancedFilter extends ImcmsServicesSupport {
  val ui = new AdvancedFilterUI

  ui.chkCategories.addValueChangeHandler { toggleCategories() }
  ui.chkDates.addValueChangeHandler { toggleDates() }
  ui.chkRelationships.addValueChangeHandler { toggleRelationships() }
  ui.chkMaintainers.addValueChangeHandler { toggleMaintainers() }

  def reset() {
    doto(ui.chkCategories, ui.chkDates, ui.chkRelationships, ui.chkMaintainers) {
      _.uncheck()
    }

    doto(ui.lytDates.drCreated, ui.lytDates.drModified, ui.lytDates.drPublished, ui.lytDates.drExpired) { dr =>
      dr.cbRangeType.value = DateRangeType.Undefined
    }

    doto(ui.lytMaintainers.ulCreators, ui.lytMaintainers.ulPublishers) { ul =>
      ul.chkEnabled.check()
      ul.chkEnabled.fireValueChange(true)
      ul.lstUsers.removeAllItems()
    }

    toggleCategories()
    toggleMaintainers()
    toggleRelationships()
    toggleDates()

    for {
      categoryType <- imcmsServices.getCategoryMapper.getAllCategoryTypes
      category <- imcmsServices.getCategoryMapper.getAllCategoriesOfType(categoryType)
    } {
      ui.tcsCategories.addItem(category)
      ui.tcsCategories.setItemCaption(category, categoryType.getName + ":" + category.getName)
      Option(category.getImageUrl).foreach(url => ui.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    ui.lytRelationships.cbParents.value = "docs_projection.advanced_filter.cb_relationships_parents.item.undefined"
    ui.lytRelationships.cbChildren.value = "docs_projection.advanced_filter.cb_relationships_children.item.undefined"
  }

  private def toggleCategories() = ProjectionFilterUtil.toggle(ui, "docs_projection.advanced_filter.categories", ui.chkCategories, ui.tcsCategories)
  private def toggleMaintainers() = ProjectionFilterUtil.toggle(ui, "docs_projection.advanced_filter.maintainers", ui.chkMaintainers, ui.lytMaintainers)
  private def toggleRelationships() = ProjectionFilterUtil.toggle(ui, "docs_projection.advanced_filter.relationships", ui.chkRelationships, ui.lytRelationships)
  private def toggleDates() = ProjectionFilterUtil.toggle(ui, "docs_projection.advanced_filter.dates", ui.chkDates, ui.lytDates)
}
