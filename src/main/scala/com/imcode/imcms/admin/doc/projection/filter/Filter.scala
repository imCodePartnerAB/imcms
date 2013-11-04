package com.imcode
package imcms
package admin.doc.projection.filter

import scala.collection.JavaConverters._

import com.vaadin.ui.CheckBox
import com.vaadin.server.ExternalResource

import _root_.imcode.server.document.{LifeCyclePhase, DocumentTypeDomainObject}
import _root_.imcode.server.document.index.DocumentIndex

import scala.PartialFunction._
import scala.util.{Success, Failure, Try}

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.api.DocumentLanguage

/**
 *
 */
class Filter extends ImcmsServicesSupport {

  val basicUI: BasicFilterUI = new BasicFilterUI
  val extendedUI: ExtendedFilterUI = new ExtendedFilterUI

  def setMetaIdRangePrompt(range: Option[(MetaId, MetaId)]) {
    range.map {
      case (start, end) => (start.toString, end.toString)
    }.getOrElse("", "") |> {
      case (start, end) =>
        basicUI.idRange.txtStart.setInputPrompt(start)
        basicUI.idRange.txtEnd.setInputPrompt(end)
    }
  }


  def setParameters(parameters: FilterParameters) {
    resetUI()

    setBasicParameters(parameters.basic)
    setExtendedParamsOpt(parameters.extendedOpt)
  }

  private def setBasicParameters(parameters: BasicFilterParams) {
    basicUI.idRange.chkEnabled.checked = parameters.idRangeOpt.isDefined
    basicUI.text.chkEnabled.checked = parameters.textOpt.isDefined
    basicUI.types.chkEnabled.checked = parameters.docTypesOpt.isDefined
    basicUI.languages.chkEnabled.checked = parameters.languagesOpt.isDefined
    basicUI.phases.chkEnabled.checked = parameters.phasesOpt.isDefined
    //ui.extended.chkEnabled.checked = parameters.extendedSearchNameOpt.isDefined

    basicUI.text.txtText.value = parameters.textOpt.getOrElse("")

    parameters.idRangeOpt.collect {
      case IdRange(start, end) => (start.map(_.toString).getOrElse(""), end.map(_.toString).getOrElse(""))
    } getOrElse("", "") match {
      case (start, end) =>
        basicUI.idRange.txtStart.value = start
        basicUI.idRange.txtEnd.value = end
    }

    basicUI.types.chkText.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.TEXT))
    basicUI.types.chkFile.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.FILE))
    basicUI.types.chkHtml.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.HTML))

    val isChecked: (DocumentLanguage => Boolean) = {
      val languages = parameters.languagesOpt.getOrElse(Set.empty)
      language => (languages.isEmpty && imcmsServices.getDocumentI18nSupport.isDefault(language)) || languages.contains(language)
    }

    basicUI.languages.layout.removeAllComponents()

    for (language <- imcmsServices.getDocumentI18nSupport.getLanguages.asScala) {
      val chkLanguage = new CheckBox(language.getNativeName) with TypedData[DocumentLanguage] |>> { chk =>
        chk.setIcon(Theme.Icon.Language.flag(language))
        chk.data = language
        chk.checked = isChecked(language)
      }

      basicUI.languages.layout.addComponent(chkLanguage)
    }
  }

  def getParameters(): Try[FilterParameters] = {
    getBasicParameters() match {
      case Failure(error) => Failure(error)
      case Success(basicParams) if basicUI.extended.chkEnabled.unchecked => Success(FilterParameters(basicParams))
      case Success(basicParams) => getExtendedParametersOpt() match {
        case None => Success(FilterParameters(basicParams, None))
        case Some(Failure(e)) => Failure(e)
        case Some(Success(extendedParams)) => Success(FilterParameters(basicParams, Some(extendedParams)))
      }
    }
  }

  private def getBasicParameters(): Try[BasicFilterParams] = Try {
    val idRangeOpt = when(basicUI.idRange.chkEnabled.checked) {
      IdRange(
        condOpt(basicUI.idRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case NonNegInt(start) => start
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        },
        condOpt(basicUI.idRange.txtEnd.trim) {
          case value if value.nonEmpty => value match {
            case NonNegInt(end) => end
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        }
      )
    }

    val textOpt = when(basicUI.text.chkEnabled.checked)(basicUI.text.txtText.trim)

    val typesOpt = when(basicUI.types.chkEnabled.checked) {
      Set(
        when(basicUI.types.chkText.checked) { DocumentTypeDomainObject.TEXT },
        when(basicUI.types.chkFile.checked) { DocumentTypeDomainObject.FILE },
        when(basicUI.types.chkHtml.checked) { DocumentTypeDomainObject.HTML },
        when(basicUI.types.chkUrl.checked) { DocumentTypeDomainObject.URL }
      ).flatten
    }

    val phasesOpt: Option[Set[LifeCyclePhase]] = when(basicUI.phases.chkEnabled.checked) {
      import basicUI.phases._

      Map(chkNew -> LifeCyclePhase.NEW,
        chkPublished -> LifeCyclePhase.PUBLISHED,
        chkUnpublished -> LifeCyclePhase.UNPUBLISHED,
        chkApproved -> LifeCyclePhase.APPROVED,
        chkDisapproved -> LifeCyclePhase.DISAPPROVED,
        chkArchived -> LifeCyclePhase.ARCHIVED
      ).filterKeys(_.checked).values.to[Set]
    }


    val languagesOpt = selectedLanguagesOpt()

    BasicFilterParams(idRangeOpt, textOpt, typesOpt, languagesOpt, phasesOpt)
  }

  /**
   * @return None
   */
  def selectedLanguagesOpt(): Option[Set[DocumentLanguage]] = when(basicUI.languages.chkEnabled.checked) {
    (
      for {
        _chk@(chkLanguage: CheckBox with TypedData[DocumentLanguage]) <- basicUI.languages.layout.iterator.asScala
        if chkLanguage.checked
      } yield {
        chkLanguage.data
      }
    ).to[Set]
  }


  private def resetUI() {
    // reset all top-level checkboxes
    Seq(basicUI.idRange.chkEnabled, basicUI.text.chkEnabled, basicUI.types.chkEnabled, basicUI.phases.chkEnabled, basicUI.extended.chkEnabled).foreach { chk =>
      chk.uncheck()
    }

    // reset all child level checkboxes
    Seq(basicUI.phases.chkNew, basicUI.phases.chkPublished, basicUI.phases.chkUnpublished, basicUI.phases.chkApproved, basicUI.phases.chkDisapproved, basicUI.phases.chkArchived).foreach { chk =>
      chk.uncheck()
    }

    Seq(extendedUI.categories.chkEnabled, extendedUI.dates.chkEnabled, extendedUI.relationships.chkEnabled, extendedUI.maintainers.chkEnabled).foreach { chk =>
      chk.uncheck()
    }

    Seq(extendedUI.dates.drCreated, extendedUI.dates.drModified, extendedUI.dates.drPublished, extendedUI.dates.drExpired).foreach { dr =>
      dr.cbRangeType.value = DateRangeType.Undefined
    }

    Seq(extendedUI.maintainers.ulCreators, extendedUI.maintainers.ulPublishers).foreach { ul =>
      ul.chkEnabled.uncheck()
      //ul.chkEnabled.fireValueChange(repaintIsNotNeeded = true)
      ul.lstUsers.removeAllItems()
    }

    extendedUI.categories.tcsCategories.removeAllItems()

    for {
      categoryType <- imcmsServices.getCategoryMapper.getAllCategoryTypes
      category <- imcmsServices.getCategoryMapper.getAllCategoriesOfType(categoryType)
    } {
      extendedUI.categories.tcsCategories.addItem(category)
      extendedUI.categories.tcsCategories.setItemCaption(category, categoryType.getName + ":" + category.getName)
      category.getImageUrl.asOption.foreach(url => extendedUI.categories.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    extendedUI.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.unspecified"
    extendedUI.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.unspecified"
  }


  private def getExtendedParametersOpt(): Option[Try[ExtendedFilterParams]] = {
    if (basicUI.extended.chkEnabled.unchecked) None
    else Some(getExtendedParameters())
  }

  private def getExtendedParameters(): Try[ExtendedFilterParams] = Try {
    // Date meaning to DateRange
    val datesOpt: Option[Map[String, DateRange]] = when(extendedUI.dates.chkEnabled.checked) {
      import extendedUI.dates._

      val datesMap = for {
        (field, dr) <- Map(
          DocumentIndex.FIELD__CREATED_DATETIME -> drCreated,
          DocumentIndex.FIELD__MODIFIED_DATETIME -> drModified,
          DocumentIndex.FIELD__PUBLICATION_START_DATETIME -> drPublished,
          DocumentIndex.FIELD__ARCHIVED_DATETIME -> drArchived,
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

    val relationshipOpt: Option[Relationship] = when(extendedUI.relationships.chkEnabled.checked) {
      val withParents = extendedUI.relationships.cbParents.value match {
        case "docs_projection.extended_filter.cb_relationships_parents.item.unspecified" => Relationship.Unspecified
        case "docs_projection.extended_filter.cb_relationships_parents.item.with_parents" => Relationship.Logical(true)
        case "docs_projection.extended_filter.cb_relationships_parents.item.without_parents" => Relationship.Logical(false)
        case "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of" => Relationship.Exact(extendedUI.relationships.txtParents.value.toInt)
      }

      val withChildren = extendedUI.relationships.cbChildren.value match {
        case "docs_projection.extended_filter.cb_relationships_children.item.unspecified" => Relationship.Unspecified
        case "docs_projection.extended_filter.cb_relationships_children.item.with_children" => Relationship.Logical(true)
        case "docs_projection.extended_filter.cb_relationships_children.item.without_children" => Relationship.Logical(false)
        case "docs_projection.extended_filter.cb_relationships_children.item.with_children_of" => Relationship.Exact(extendedUI.relationships.txtChildren.value.toInt)
      }

      Relationship(withParents, withChildren)
    }

    val categoriesOpt: Option[Set[String]] = when(extendedUI.categories.chkEnabled.checked) {
      extendedUI.categories.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].asScala.to[Set]
    }

    val maintainersOpt: Option[Maintainers] = when(extendedUI.maintainers.chkEnabled.checked) {
      val creatorsOpt: Option[Set[UserId]] = when(extendedUI.maintainers.ulCreators.chkEnabled.checked) {
        extendedUI.maintainers.ulCreators.lstUsers.itemIds.asScala.to[Set]
      }

      val publishersOpt: Option[Set[UserId]] = when(extendedUI.maintainers.ulPublishers.chkEnabled.checked) {
        extendedUI.maintainers.ulPublishers.lstUsers.itemIds.asScala.to[Set]
      }

      Maintainers(creatorsOpt, publishersOpt)
    }

    ExtendedFilterParams(datesOpt, categoriesOpt, relationshipOpt, maintainersOpt)
  }

  private def setExtendedParamsOpt(paramsOpt: Option[ExtendedFilterParams]) {
    basicUI.extended.chkEnabled.checked = paramsOpt.isDefined

    val parameters = paramsOpt.getOrElse(ExtendedFilterParams())
    for (dates <- parameters.datesOpt) {
      extendedUI.dates.chkEnabled.check()

      for ((dateRangeName, dateRange) <- dates) {
        import extendedUI.dates._

        val dateRangeUI = dateRangeName match {
          case DocumentIndex.FIELD__CREATED_DATETIME => drCreated
          case DocumentIndex.FIELD__MODIFIED_DATETIME => drModified
          case DocumentIndex.FIELD__PUBLICATION_START_DATETIME => drPublished
          case DocumentIndex.FIELD__ARCHIVED_DATETIME => drArchived
          case DocumentIndex.FIELD__PUBLICATION_END_DATETIME => drExpired
        }

        dateRangeUI.dtFrom.value = dateRange.start.orNull
        dateRangeUI.dtTo.value = dateRange.end.orNull
      }
    }

    for (categories <- parameters.categoriesOpt) {
      extendedUI.categories.chkEnabled.check()

      categories.foreach(extendedUI.categories.tcsCategories.select)
    }

    for (maintainers <- parameters.maintainersOpt) {
      extendedUI.maintainers.chkEnabled.check()

      for (usersIds <- maintainers.creatorsOpt; userId <- usersIds) {
        extendedUI.maintainers.ulCreators.lstUsers.addItem(userId)
      }

      for (usersIds <- maintainers.publishersOpt; userId <- usersIds) {
        extendedUI.maintainers.ulCreators.lstUsers.addItem(userId)
      }
    }

    for (Relationship(parents, children) <- parameters.relationshipOpt) {
      extendedUI.relationships.chkEnabled.check()

      parents match {
        case Relationship.Logical(value) if value =>
          extendedUI.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.with_parents"
        case Relationship.Logical(_) =>
          extendedUI.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.without_parents"
        case Relationship.Exact(metaId) =>
          extendedUI.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of"
          extendedUI.relationships.txtParents.value = metaId.toString

        case _ =>
          extendedUI.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.unspecified"
      }

      children match {
        case Relationship.Logical(value) if value =>
          extendedUI.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.with_children"
        case Relationship.Logical(_) =>
          extendedUI.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.without_children"
        case Relationship.Exact(metaId) =>
          extendedUI.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.with_children_of"
          extendedUI.relationships.txtChildren.value = metaId.toString

        case _ =>
          extendedUI.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.unspecified"
      }
    }
  }

}