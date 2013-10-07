package com.imcode.imcms.admin.doc.projection.filter

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

    txtParents.setVisible(ui.lytRelationships.cbParents.selection == "docs_projection.advanced_filter.cb_relationships_parents.item.has_parents")
  }

  ui.lytRelationships.cbChildren.addValueChangeHandler { _ =>
    val txtChildren = ui.lytRelationships.txtChildren

    txtChildren.setVisible(ui.lytRelationships.cbChildren.selection == "docs_projection.advanced_filter.cb_relationships_children.item.has_children")
  }

  def reset() {
    Seq(ui.chkCategories, ui.chkDates, ui.chkRelationships, ui.chkMaintainers).foreach {
      _.uncheck()
    }

    Seq(ui.lytDates.drCreated, ui.lytDates.drModified, ui.lytDates.drPublished, ui.lytDates.drExpired).foreach { dr =>
      dr.cbRangeType.value = DateRangeType.Undefined
    }

    Seq(ui.lytMaintainers.ulCreators, ui.lytMaintainers.ulPublishers).foreach { ul =>
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
      category.getImageUrl.asOption.foreach(url => ui.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    ui.lytRelationships.cbParents.value = "docs_projection.advanced_filter.cb_relationships_parents.item.undefined"
    ui.lytRelationships.cbChildren.value = "docs_projection.advanced_filter.cb_relationships_children.item.undefined"
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
      val hasParentsOpt = PartialFunction.condOpt(ui.lytRelationships.cbParents.value) {
        case "docs_projection.advanced_filter.cb_relationships_parents.item.has_parents" => true
        case "docs_projection.advanced_filter.cb_relationships_parents.item.no_parents" => false
      }

      val hasChildrenOpt = PartialFunction.condOpt(ui.lytRelationships.cbChildren.value) {
        case "docs_projection.advanced_filter.cb_relationships_children.item.has_children" => true
        case "docs_projection.advanced_filter.cb_relationships_children.item.no_children" => false
      }

      Relationship(hasParentsOpt, hasChildrenOpt)
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
    //todo: implement
  }
}
