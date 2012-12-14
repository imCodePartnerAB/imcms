package com.imcode
package imcms
package admin.doc.projection.filter

import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.ui._

class AdvancedFilterUI extends CustomLayout("admin/doc/search/advanced_form") with UndefinedSize {
  val chkStatus = new CheckBox("doc.search.advanced.frm.fld.chk_status".i) with Immediate
  val lytStatus = new HorizontalLayout with Spacing with UndefinedSize {
    val chkNew = new CheckBox("doc.search.advanced.frm.fld.chk_status_new".i)
    val chkPublished = new CheckBox("doc.search.advanced.frm.fld.chk_status_published".i)
    val chkUnpublished = new CheckBox("doc.search.advanced.frm.fld.chk_status_unpublished".i)
    val chkApproved = new CheckBox("doc.search.advanced.frm.fld.chk_status_approved".i)
    val chkDisapproved = new CheckBox("doc.search.advanced.frm.fld.chk_status_disapproved".i)
    val chkExpired = new CheckBox("doc.search.advanced.frm.fld.chk_status_expired".i)

    addComponentsTo(this, chkNew, chkPublished, chkUnpublished, chkApproved, chkDisapproved, chkExpired)
  }

  val chkDates = new CheckBox("doc.search.advanced.frm.fld.chk_dates".i) with Immediate
  val lytDates = new FormLayout with UndefinedSize {
    val drCreated = new DateRangeUI("doc.search.advanced.frm.fld.dr_created".i) with DateRangeUISetup
    val drModified = new DateRangeUI("doc.search.advanced.frm.fld.dr_modified".i) with DateRangeUISetup
    val drPublished = new DateRangeUI("doc.search.advanced.frm.fld.dr_published".i) with DateRangeUISetup
    val drExpired = new DateRangeUI("doc.search.advanced.frm.fld.dr_expired".i) with DateRangeUISetup

    addComponentsTo(this, drCreated, drModified, drPublished, drExpired)
  }

  val chkCategories = new CheckBox("doc.search.advanced.frm.fld.chk_categories".i) with Immediate
  val tcsCategories = new TwinColSelect with TCSDefaultI18n

  val chkRelationships = new CheckBox("doc.search.advanced.frm.fld.chk_relationships".i) with Immediate
  val lytRelationships = new HorizontalLayout with Spacing with UndefinedSize {
    val cbParents = new ComboBox("doc.search.advanced.frm.fld.chk_relationships_parents".i) with SingleSelect[String] with NoNullSelection
    val cbChildren = new ComboBox("doc.search.advanced.frm.fld.chk_relationships_children".i) with SingleSelect[String] with NoNullSelection

    Seq("doc.search.advanced.frm.fld.cb_relationships_parents.item.undefined",
      "doc.search.advanced.frm.fld.cb_relationships_parents.item.has_parents",
      "doc.search.advanced.frm.fld.cb_relationships_parents.item.no_parents"
    ).foreach(itemId => cbParents.addItem(itemId, itemId.i))

    Seq("doc.search.advanced.frm.fld.cb_relationships_children.item.undefined",
      "doc.search.advanced.frm.fld.cb_relationships_children.item.has_children",
      "doc.search.advanced.frm.fld.cb_relationships_children.item.no_children"
    ).foreach(itemId => cbChildren.addItem(itemId, itemId.i))

    addComponentsTo(this, cbParents, cbChildren)
  }

  val chkMaintainers = new CheckBox("doc.search.advanced.frm.fld.chk_maintainers".i) with Immediate
  val lytMaintainers = new HorizontalLayout with Spacing with UndefinedSize{
    val ulCreators = new UserListUI("doc.search.advanced.frm.fld.chk_maintainers_creators".i) with UserListUISetup {
      val projectionDialogCaption = "doc.search.advanced.dlg_select_creators.caption".i
    }

    val ulPublishers = new UserListUI("doc.search.advanced.frm.fld.chk_maintainers_publishers".i) with UserListUISetup {
      val projectionDialogCaption = "doc.search.advanced.dlg_select_publishers.caption".i
    }

    addComponentsTo(this, ulCreators, ulPublishers)
  }

  addNamedComponents(this,
    "doc.search.advanced.frm.fld.chk_status" -> chkStatus,
    "doc.search.advanced.frm.fld.status" -> lytStatus,
    "doc.search.advanced.frm.fld.chk_dates" -> chkDates,
    "doc.search.advanced.frm.fld.dates" -> lytDates,
    "doc.search.advanced.frm.fld.chk_relationships" -> chkRelationships,
    "doc.search.advanced.frm.fld.relationships" -> lytRelationships,
    "doc.search.advanced.frm.fld.chk_categories" -> chkCategories,
    "doc.search.advanced.frm.fld.categories" -> tcsCategories,
    "doc.search.advanced.frm.fld.chk_maintainers" -> chkMaintainers,
    "doc.search.advanced.frm.fld.maintainers" -> lytMaintainers
  )
}
