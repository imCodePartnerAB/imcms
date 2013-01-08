package com.imcode
package imcms
package admin.doc.projection.filter

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._

class AdvancedFilterUI extends CustomLayout("admin/doc/projection/advanced_filter") with UndefinedSize {
  val chkDates = new CheckBox("docs_projection.advanced_filter.chk_dates".i) with Immediate
  val lytDates = new FormLayout with UndefinedSize {
    val drCreated = new DateRangeUI("docs_projection.advanced_filter.dr_created".i) with DateRangeUISetup
    val drModified = new DateRangeUI("docs_projection.advanced_filter.dr_modified".i) with DateRangeUISetup
    val drPublished = new DateRangeUI("docs_projection.advanced_filter.dr_published".i) with DateRangeUISetup
    val drArchived = new DateRangeUI("docs_projection.advanced_filter.dr_archived".i) with DateRangeUISetup
    val drExpired = new DateRangeUI("docs_projection.advanced_filter.dr_expired".i) with DateRangeUISetup

    this.addComponents(drCreated, drModified, drPublished, drArchived, drExpired)
  }

  val chkCategories = new CheckBox("docs_projection.advanced_filter.chk_categories".i) with Immediate
  val tcsCategories = new TwinColSelect with TCSDefaultI18n

  val chkRelationships = new CheckBox("docs_projection.advanced_filter.chk_relationships".i) with Immediate
  val lytRelationships = new FormLayout with UndefinedSize {
    val cbParents = new ComboBox("docs_projection.advanced_filter.chk_relationships_parents".i) with SingleSelect[String] with NoNullSelection
    val cbChildren = new ComboBox("docs_projection.advanced_filter.chk_relationships_children".i) with SingleSelect[String] with NoNullSelection

    Seq("docs_projection.advanced_filter.cb_relationships_parents.item.undefined",
      "docs_projection.advanced_filter.cb_relationships_parents.item.has_parents",
      "docs_projection.advanced_filter.cb_relationships_parents.item.no_parents"
    ).foreach(itemId => cbParents.addItem(itemId, itemId.i))

    Seq("docs_projection.advanced_filter.cb_relationships_children.item.undefined",
      "docs_projection.advanced_filter.cb_relationships_children.item.has_children",
      "docs_projection.advanced_filter.cb_relationships_children.item.no_children"
    ).foreach(itemId => cbChildren.addItem(itemId, itemId.i))

    this.addComponents(cbParents, cbChildren)
  }

  val chkMaintainers = new CheckBox("docs_projection.advanced_filter.chk_maintainers".i) with Immediate
  val lytMaintainers = new HorizontalLayout with Spacing with UndefinedSize{
    val ulCreators = new UserListUI("docs_projection.advanced_filter.chk_maintainers_creators".i) with UserListUISetup {
      val projectionDialogCaption = "docs_projection.advanced.dlg_select_creators.caption".i
    }

    val ulPublishers = new UserListUI("docs_projection.advanced_filter.chk_maintainers_publishers".i) with UserListUISetup {
      val projectionDialogCaption = "docs_projection.advanced.dlg_select_publishers.caption".i
    }

    this.addComponents(ulCreators, ulPublishers)
  }

  this.addNamedComponents(
    "docs_projection.advanced_filter.chk_dates" -> chkDates,
    "docs_projection.advanced_filter.dates" -> lytDates,
    "docs_projection.advanced_filter.chk_relationships" -> chkRelationships,
    "docs_projection.advanced_filter.relationships" -> lytRelationships,
    "docs_projection.advanced_filter.chk_categories" -> chkCategories,
    "docs_projection.advanced_filter.categories" -> tcsCategories,
    "docs_projection.advanced_filter.chk_maintainers" -> chkMaintainers,
    "docs_projection.advanced_filter.maintainers" -> lytMaintainers
  )
}
