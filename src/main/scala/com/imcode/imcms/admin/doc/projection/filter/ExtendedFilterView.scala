package com.imcode
package imcms
package admin.doc.projection.filter

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.event._

class ExtendedFilterView extends CustomLayout("admin/doc/projection/extended_filter") with FullSize {

  object dates {
    val chkEnabled = new CheckBox("docs_projection.extended_filter.chk_dates".i) with Immediate

    val drCreated = new DateRangeComponent("docs_projection.extended_filter.dr_created".i) with DateRangeComponentSetup
    val drModified = new DateRangeComponent("docs_projection.extended_filter.dr_modified".i) with DateRangeComponentSetup
    val drPublished = new DateRangeComponent("docs_projection.extended_filter.dr_published".i) with DateRangeComponentSetup
    val drArchived = new DateRangeComponent("docs_projection.extended_filter.dr_archived".i) with DateRangeComponentSetup
    val drExpired = new DateRangeComponent("docs_projection.extended_filter.dr_expired".i) with DateRangeComponentSetup
  }

  object categories {
    val chkEnabled = new CheckBox("docs_projection.extended_filter.chk_categories".i) with Immediate

    val tcsCategories = new TwinColSelect with TCSDefaultI18n
  }

  object relationships {
    val chkEnabled = new CheckBox("docs_projection.extended_filter.chk_relationships".i) with Immediate

    val cbParents = new ComboBox("docs_projection.extended_filter.chk_relationships_parents".i) with SingleSelect[String] with NoNullSelection with Immediate
    val cbChildren = new ComboBox("docs_projection.extended_filter.chk_relationships_children".i) with SingleSelect[String] with NoNullSelection with Immediate

    val txtParents = new TextField with Invisible |>> { _.setInputPrompt("any") }   // todo: i18n
    val txtChildren = new TextField with Invisible |>> { _.setInputPrompt("any") }  // todo: i18n
  }

  object maintainers {
    val chkEnabled = new CheckBox("docs_projection.extended_filter.chk_maintainers".i) with Immediate

    val ulCreators = new UserListComponent("docs_projection.extended_filter.chk_maintainers_creators".i) with UserListComponentSetup {
      val projectionDialogCaption = "docs_projection.extended.dlg_select_creators.caption".i
    }

    val ulPublishers = new UserListComponent("docs_projection.extended_filter.chk_maintainers_publishers".i) with UserListComponentSetup {
      val projectionDialogCaption = "docs_projection.extended.dlg_select_publishers.caption".i
    }
  }

  private val lytDates = new FormLayout with UndefinedSize |>> { lyt =>
    import dates._

    lyt.addComponents(drCreated, drModified, drPublished, drArchived, drExpired)
  }


  private val lytRelationships = new FormLayout with UndefinedSize |>> { lyt =>
    import relationships._

    val lytParents = new HorizontalLayout with UndefinedSize with Spacing
    val lytChildren = new HorizontalLayout with UndefinedSize with Spacing

    lytParents.addComponents(cbParents, txtParents)
    lytChildren.addComponents(cbChildren, txtChildren)

    Seq("docs_projection.extended_filter.cb_relationships_parents.item.unspecified",
      "docs_projection.extended_filter.cb_relationships_parents.item.with_parents",
      "docs_projection.extended_filter.cb_relationships_parents.item.without_parents",
      "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of"
    ).foreach(itemId => cbParents.addItem(itemId, itemId.i))

    Seq("docs_projection.extended_filter.cb_relationships_children.item.unspecified",
      "docs_projection.extended_filter.cb_relationships_children.item.with_children",
      "docs_projection.extended_filter.cb_relationships_children.item.without_children",
      "docs_projection.extended_filter.cb_relationships_children.item.with_children_of"
    ).foreach(itemId => cbChildren.addItem(itemId, itemId.i))

    lyt.addComponents(lytParents, lytChildren)
  }


  private val lytMaintainers = new HorizontalLayout with Spacing with UndefinedSize {
    import maintainers._

    addComponents(ulCreators, ulPublishers)
  }

  this.addNamedComponents(
    "docs_projection.extended_filter.chk_dates" -> dates.chkEnabled,
    "docs_projection.extended_filter.dates" -> lytDates,
    "docs_projection.extended_filter.chk_relationships" -> relationships.chkEnabled,
    "docs_projection.extended_filter.relationships" -> lytRelationships,
    "docs_projection.extended_filter.chk_categories" -> categories.chkEnabled,
    "docs_projection.extended_filter.categories" -> categories.tcsCategories,
    "docs_projection.extended_filter.chk_maintainers" -> maintainers.chkEnabled,
    "docs_projection.extended_filter.maintainers" -> lytMaintainers
  )

  dates.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.extended_filter.dates", dates.chkEnabled, lytDates)
  }

  relationships.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.extended_filter.relationships", relationships.chkEnabled, lytRelationships)
  }

  categories.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.extended_filter.categories", categories.chkEnabled, categories.tcsCategories)
  }

  maintainers.chkEnabled.addValueChangeHandler { _ =>
    ProjectionFilterUtil.toggle(this, "docs_projection.extended_filter.maintainers", maintainers.chkEnabled, lytMaintainers)
  }

  relationships.cbParents.addValueChangeHandler { _ =>
    val txtParents = relationships.txtParents

    txtParents.setVisible(relationships.cbParents.firstSelected == "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of")
  }

  relationships.cbChildren.addValueChangeHandler { _ =>
    val txtChildren = relationships.txtChildren

    txtChildren.setVisible(relationships.cbChildren.firstSelected == "docs_projection.extended_filter.cb_relationships_children.item.with_children_of")
  }
}
