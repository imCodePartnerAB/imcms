package com.imcode
package imcms
package admin.doc.projection.filter

import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.vaadin.server.ExternalResource
import scala.util.Try
import _root_.imcode.server.document.index.DocumentIndex
import scala.collection.JavaConverters._


class AdvancedFilter extends ImcmsServicesSupport {
  val ui = new AdvancedFilterUI

  ui.chkCategories.addValueChangeHandler { _ => toggleCategories() }
  ui.chkDates.addValueChangeHandler { _ => toggleDates() }
  ui.chkRelationships.addValueChangeHandler { _ => toggleRelationships() }
  ui.chkMaintainers.addValueChangeHandler { _ => toggleMaintainers() }

  ui.lytRelationships.cbParents.addValueChangeHandler { _ =>
    val txtParents = ui.lytRelationships.txtParents

    txtParents.setVisible(ui.lytRelationships.cbParents.selection == "docs_projection.advanced_filter.cb_relationships_parents.item.with_parent_of")
  }

  ui.lytRelationships.cbChildren.addValueChangeHandler { _ =>
    val txtChildren = ui.lytRelationships.txtChildren

    txtChildren.setVisible(ui.lytRelationships.cbChildren.selection == "docs_projection.advanced_filter.cb_relationships_children.item.with_children_of")
  }

  def reset() {
    Seq(ui.chkCategories, ui.chkDates, ui.chkRelationships, ui.chkMaintainers).foreach { chk =>
      chk.uncheck()
    }

    Seq(ui.lytDates.drCreated, ui.lytDates.drModified, ui.lytDates.drPublished, ui.lytDates.drExpired).foreach { dr =>
      dr.cbRangeType.value = DateRangeType.Undefined
    }

    Seq(ui.lytMaintainers.ulCreators, ui.lytMaintainers.ulPublishers).foreach { ul =>
      ul.chkEnabled.check()
      ul.chkEnabled.fireValueChange(repaintIsNotNeeded = true)
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
      category.getImageUrl.asOption.foreach(url => ui.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    ui.lytRelationships.cbParents.value = "docs_projection.advanced_filter.cb_relationships_parents.item.unspecified"
    ui.lytRelationships.cbChildren.value = "docs_projection.advanced_filter.cb_relationships_children.item.unspecified"
  }

  private def toggleCategories() = ProjectionFilterUtil.toggle(ui, "docs_projection.advanced_filter.categories", ui.chkCategories, ui.tcsCategories)
  private def toggleMaintainers() = ProjectionFilterUtil.toggle(ui, "docs_projection.advanced_filter.maintainers", ui.chkMaintainers, ui.lytMaintainers)
  private def toggleRelationships() = ProjectionFilterUtil.toggle(ui, "docs_projection.advanced_filter.relationships", ui.chkRelationships, ui.lytRelationships)
  private def toggleDates() = ProjectionFilterUtil.toggle(ui, "docs_projection.advanced_filter.dates", ui.chkDates, ui.lytDates)


  def getParameters(): Try[AdvancedFilterParameters] = Try {
    // Date meaning to DateRange
    val datesOpt: Option[Map[String, DateRange]] = when(ui.chkDates.isChecked) {
      import ui.lytDates._

      val datesMap = for {
        (field, dr) <- Map(
          DocumentIndex.FIELD__CREATED_DATETIME -> drCreated,
          DocumentIndex.FIELD__MODIFIED_DATETIME -> drModified,
          DocumentIndex.FIELD__PUBLICATION_START_DATETIME -> drPublished,
          DocumentIndex.FIELD__ACTIVATED_DATETIME -> drArchived,
          DocumentIndex.FIELD__PUBLICATION_END_DATETIME -> drExpired
        )
        if dr.cbRangeType.value != DateRangeType.Undefined
        startOpt = dr.dtFrom.valueOpt
        endOpt = dr.dtTo.valueOpt
        if startOpt.isDefined || endOpt.isDefined
      } yield {
        val startFixedOpt = startOpt.map { dt =>
          new org.joda.time.DateTime(dt).withMillisOfDay(0).toDate
        }

        val endFixedOpt = endOpt.map { dt =>
          new org.joda.time.DateTime(dt).plusDays(1).withMillisOfDay(0).minus(1).toDate
        }

        field -> DateRange(startFixedOpt, endFixedOpt)
      }

      datesMap.toMap
    }

    val relationshipOpt: Option[Relationship] = when(ui.chkRelationships.isChecked) {
      val withParents = ui.lytRelationships.cbParents.value match {
        case "docs_projection.advanced_filter.cb_relationships_parents.item.unspecified" => Relationship.Unspecified
        case "docs_projection.advanced_filter.cb_relationships_parents.item.with_parents" => Relationship.Logical(true)
        case "docs_projection.advanced_filter.cb_relationships_parents.item.without_parents" => Relationship.Logical(false)
        case "docs_projection.advanced_filter.cb_relationships_parents.item.with_parent_of" => Relationship.Exact(ui.lytRelationships.txtParents.value.toInt)
      }

      val withChildren = ui.lytRelationships.cbChildren.value match {
        case "docs_projection.advanced_filter.cb_relationships_children.item.unspecified" => Relationship.Unspecified
        case "docs_projection.advanced_filter.cb_relationships_children.item.with_children" => Relationship.Logical(true)
        case "docs_projection.advanced_filter.cb_relationships_children.item.without_children" => Relationship.Logical(false)
        case "docs_projection.advanced_filter.cb_relationships_children.item.with_children_of" => Relationship.Exact(ui.lytRelationships.txtChildren.value.toInt)
      }

      Relationship(withParents, withChildren)
    }

    val categoriesOpt: Option[Set[String]] = when(ui.chkCategories.isChecked) {
      ui.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].asScala.to[Set]
    }

    val maintainersOpt: Option[Maintainers] = when(ui.chkMaintainers.isChecked) {
      val creatorsOpt: Option[Set[UserId]] = when(ui.lytMaintainers.ulCreators.chkEnabled.isChecked) {
        ui.lytMaintainers.ulCreators.lstUsers.itemIds.asScala.to[Set]
      }

      val publishersOpt: Option[Set[UserId]] = when(ui.lytMaintainers.ulPublishers.chkEnabled.isChecked) {
        ui.lytMaintainers.ulPublishers.lstUsers.itemIds.asScala.to[Set]
      }

      Maintainers(creatorsOpt, publishersOpt)
    }

    AdvancedFilterParameters(datesOpt, categoriesOpt, relationshipOpt, maintainersOpt)
  }

  def setParameters(parameters: AdvancedFilterParameters) {
    reset()

    for (Relationship(parents, children) <- parameters.relationshipOpt) {
      ui.chkRelationships.check()

      parents match {
        case Relationship.Logical(value) if value =>
          ui.lytRelationships.cbParents.value = "docs_projection.advanced_filter.cb_relationships_parents.item.with_parents"
        case Relationship.Logical(_) =>
          ui.lytRelationships.cbParents.value = "docs_projection.advanced_filter.cb_relationships_parents.item.without_parents"
        case Relationship.Exact(docId) =>
          ui.lytRelationships.cbParents.value = "docs_projection.advanced_filter.cb_relationships_parents.item.with_parent_of"
          ui.lytRelationships.txtParents.value = docId.toString

        case _ =>
          ui.lytRelationships.cbParents.value = "docs_projection.advanced_filter.cb_relationships_parents.item.unspecified"
      }

      children match {
        case Relationship.Logical(value) if value =>
          ui.lytRelationships.cbChildren.value = "docs_projection.advanced_filter.cb_relationships_children.item.with_children"
        case Relationship.Logical(_) =>
          ui.lytRelationships.cbChildren.value = "docs_projection.advanced_filter.cb_relationships_children.item.without_children"
        case Relationship.Exact(docId) =>
          ui.lytRelationships.cbChildren.value = "docs_projection.advanced_filter.cb_relationships_children.item.with_children_of"
          ui.lytRelationships.txtChildren.value = docId.toString

        case _ =>
          ui.lytRelationships.cbChildren.value = "docs_projection.advanced_filter.cb_relationships_children.item.unspecified"
      }
    }
  }
}
