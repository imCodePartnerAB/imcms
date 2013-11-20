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

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.api.DocumentLanguage

/**
 *
 */
class Filter extends ImcmsServicesSupport {

  val basicWidget: BasicFilterWidget = new BasicFilterWidget
  val extendedWidget: ExtendedFilterWidget = new ExtendedFilterWidget

  def setMetaIdRangePrompt(range: Option[(MetaId, MetaId)]) {
    range.map {
      case (start, end) => (start.toString, end.toString)
    }.getOrElse("", "") |> {
      case (start, end) =>
        basicWidget.idRange.txtStart.setInputPrompt(start)
        basicWidget.idRange.txtEnd.setInputPrompt(end)
    }
  }


  def setParameters(parameters: FilterParameters) {
    resetWidget()

    setBasicParameters(parameters.basic)
    setExtendedParamsOpt(parameters.extendedOpt)
  }

  private def setBasicParameters(parameters: BasicFilterParams) {
    basicWidget.idRange.chkEnabled.checked = parameters.idRangeOpt.isDefined
    basicWidget.text.chkEnabled.checked = parameters.textOpt.isDefined
    basicWidget.types.chkEnabled.checked = parameters.docTypesOpt.isDefined
    basicWidget.languages.chkEnabled.checked = parameters.languagesOpt.isDefined
    basicWidget.phases.chkEnabled.checked = parameters.phasesOpt.isDefined
    //ui.extended.chkEnabled.checked = parameters.extendedSearchNameOpt.isDefined

    basicWidget.text.txtText.value = parameters.textOpt.getOrElse("")

    parameters.idRangeOpt.collect {
      case IdRange(start, end) => (start.map(_.toString).getOrElse(""), end.map(_.toString).getOrElse(""))
    } getOrElse("", "") match {
      case (start, end) =>
        basicWidget.idRange.txtStart.value = start
        basicWidget.idRange.txtEnd.value = end
    }

    basicWidget.types.chkText.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.TEXT))
    basicWidget.types.chkFile.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.FILE))
    basicWidget.types.chkHtml.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.HTML))

    val isChecked: (DocumentLanguage => Boolean) = {
      val languages = parameters.languagesOpt.getOrElse(Set.empty)
      language => (languages.isEmpty && imcmsServices.getDocumentI18nSupport.isDefault(language)) || languages.contains(language)
    }

    basicWidget.languages.layout.removeAllComponents()

    for (language <- imcmsServices.getDocumentI18nSupport.getLanguages.asScala) {
      val chkLanguage = new CheckBox(language.getNativeName) with TypedData[DocumentLanguage] |>> { chk =>
        chk.setIcon(Theme.Icon.Language.flag(language))
        chk.data = language
        chk.checked = isChecked(language)
      }

      basicWidget.languages.layout.addComponent(chkLanguage)
    }
  }

  def createParams(): Try[FilterParameters] = {
    createBasicParams() match {
      case Failure(error) => Failure(error)
      case Success(basicParams) if basicWidget.extended.chkEnabled.unchecked => Success(FilterParameters(basicParams))
      case Success(basicParams) => createExtendedParamsOpt() match {
        case None => Success(FilterParameters(basicParams, None))
        case Some(Failure(e)) => Failure(e)
        case Some(Success(extendedParams)) => Success(FilterParameters(basicParams, Some(extendedParams)))
      }
    }
  }

  private def createBasicParams(): Try[BasicFilterParams] = Try {
    val idRangeOpt = when(basicWidget.idRange.chkEnabled.checked) {
      IdRange(
        condOpt(basicWidget.idRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case NonNegInt(start) => start
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        },
        condOpt(basicWidget.idRange.txtEnd.trim) {
          case value if value.nonEmpty => value match {
            case NonNegInt(end) => end
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        }
      )
    }

    val textOpt = when(basicWidget.text.chkEnabled.checked)(basicWidget.text.txtText.trim)

    val typesOpt = when(basicWidget.types.chkEnabled.checked) {
      Set(
        when(basicWidget.types.chkText.checked) { DocumentTypeDomainObject.TEXT },
        when(basicWidget.types.chkFile.checked) { DocumentTypeDomainObject.FILE },
        when(basicWidget.types.chkHtml.checked) { DocumentTypeDomainObject.HTML },
        when(basicWidget.types.chkUrl.checked) { DocumentTypeDomainObject.URL }
      ).flatten
    }

    val phasesOpt: Option[Set[LifeCyclePhase]] = when(basicWidget.phases.chkEnabled.checked) {
      import basicWidget.phases._

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
  def selectedLanguagesOpt(): Option[Set[DocumentLanguage]] = when(basicWidget.languages.chkEnabled.checked) {
    (
      for {
        _chk@(chkLanguage: CheckBox with TypedData[DocumentLanguage]) <- basicWidget.languages.layout.iterator.asScala
        if chkLanguage.checked
      } yield {
        chkLanguage.data
      }
    ).to[Set]
  }


  private def resetWidget() {
    // reset all top-level checkboxes
    Seq(basicWidget.idRange.chkEnabled, basicWidget.text.chkEnabled, basicWidget.types.chkEnabled, basicWidget.phases.chkEnabled, basicWidget.extended.chkEnabled).foreach { chk =>
      chk.uncheck()
    }

    // reset all child level checkboxes
    Seq(basicWidget.phases.chkNew, basicWidget.phases.chkPublished, basicWidget.phases.chkUnpublished, basicWidget.phases.chkApproved, basicWidget.phases.chkDisapproved, basicWidget.phases.chkArchived).foreach { chk =>
      chk.uncheck()
    }

    Seq(extendedWidget.categories.chkEnabled, extendedWidget.dates.chkEnabled, extendedWidget.relationships.chkEnabled, extendedWidget.maintainers.chkEnabled).foreach { chk =>
      chk.uncheck()
    }

    Seq(extendedWidget.dates.drCreated, extendedWidget.dates.drModified, extendedWidget.dates.drPublished, extendedWidget.dates.drExpired).foreach { dr =>
      dr.cbRangeType.value = DateRangeType.Undefined
    }

    Seq(extendedWidget.maintainers.ulCreators, extendedWidget.maintainers.ulPublishers).foreach { ul =>
      ul.chkEnabled.uncheck()
      //ul.chkEnabled.fireValueChange(repaintIsNotNeeded = true)
      ul.lstUsers.removeAllItems()
    }

    extendedWidget.categories.tcsCategories.removeAllItems()

    for {
      categoryType <- imcmsServices.getCategoryMapper.getAllCategoryTypes
      category <- imcmsServices.getCategoryMapper.getAllCategoriesOfType(categoryType)
    } {
      extendedWidget.categories.tcsCategories.addItem(category)
      extendedWidget.categories.tcsCategories.setItemCaption(category, categoryType.getName + ":" + category.getName)
      category.getImageUrl.asOption.foreach(url => extendedWidget.categories.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    extendedWidget.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.unspecified"
    extendedWidget.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.unspecified"
  }


  private def createExtendedParamsOpt(): Option[Try[ExtendedFilterParams]] = {
    if (basicWidget.extended.chkEnabled.unchecked) None
    else Some(getExtendedParameters())
  }

  private def getExtendedParameters(): Try[ExtendedFilterParams] = Try {
    // Date meaning to DateRange
    val datesOpt: Option[Map[String, DateRange]] = when(extendedWidget.dates.chkEnabled.checked) {
      import extendedWidget.dates._

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

    val relationshipOpt: Option[Relationship] = when(extendedWidget.relationships.chkEnabled.checked) {
      val withParents = extendedWidget.relationships.cbParents.value match {
        case "docs_projection.extended_filter.cb_relationships_parents.item.unspecified" => Relationship.Unspecified
        case "docs_projection.extended_filter.cb_relationships_parents.item.with_parents" => Relationship.Logical(true)
        case "docs_projection.extended_filter.cb_relationships_parents.item.without_parents" => Relationship.Logical(false)
        case "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of" => Relationship.Exact(extendedWidget.relationships.txtParents.value.toInt)
      }

      val withChildren = extendedWidget.relationships.cbChildren.value match {
        case "docs_projection.extended_filter.cb_relationships_children.item.unspecified" => Relationship.Unspecified
        case "docs_projection.extended_filter.cb_relationships_children.item.with_children" => Relationship.Logical(true)
        case "docs_projection.extended_filter.cb_relationships_children.item.without_children" => Relationship.Logical(false)
        case "docs_projection.extended_filter.cb_relationships_children.item.with_children_of" => Relationship.Exact(extendedWidget.relationships.txtChildren.value.toInt)
      }

      Relationship(withParents, withChildren)
    }

    val categoriesOpt: Option[Set[String]] = when(extendedWidget.categories.chkEnabled.checked) {
      extendedWidget.categories.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].asScala.to[Set]
    }

    val maintainersOpt: Option[Maintainers] = when(extendedWidget.maintainers.chkEnabled.checked) {
      val creatorsOpt: Option[Set[UserId]] = when(extendedWidget.maintainers.ulCreators.chkEnabled.checked) {
        extendedWidget.maintainers.ulCreators.lstUsers.itemIds.asScala.to[Set]
      }

      val publishersOpt: Option[Set[UserId]] = when(extendedWidget.maintainers.ulPublishers.chkEnabled.checked) {
        extendedWidget.maintainers.ulPublishers.lstUsers.itemIds.asScala.to[Set]
      }

      Maintainers(creatorsOpt, publishersOpt)
    }

    ExtendedFilterParams(datesOpt, categoriesOpt, relationshipOpt, maintainersOpt)
  }

  private def setExtendedParamsOpt(paramsOpt: Option[ExtendedFilterParams]) {
    basicWidget.extended.chkEnabled.checked = paramsOpt.isDefined

    val parameters = paramsOpt.getOrElse(ExtendedFilterParams())
    for (dates <- parameters.datesOpt) {
      extendedWidget.dates.chkEnabled.check()

      for ((dateRangeName, dateRange) <- dates) {
        import extendedWidget.dates._

        val dateRangeWidget = dateRangeName match {
          case DocumentIndex.FIELD__CREATED_DATETIME => drCreated
          case DocumentIndex.FIELD__MODIFIED_DATETIME => drModified
          case DocumentIndex.FIELD__PUBLICATION_START_DATETIME => drPublished
          case DocumentIndex.FIELD__ARCHIVED_DATETIME => drArchived
          case DocumentIndex.FIELD__PUBLICATION_END_DATETIME => drExpired
        }

        dateRangeWidget.dtFrom.value = dateRange.start.orNull
        dateRangeWidget.dtTo.value = dateRange.end.orNull
      }
    }

    for (categories <- parameters.categoriesOpt) {
      extendedWidget.categories.chkEnabled.check()

      categories.foreach(extendedWidget.categories.tcsCategories.select)
    }

    for (maintainers <- parameters.maintainersOpt) {
      extendedWidget.maintainers.chkEnabled.check()

      for (usersIds <- maintainers.creatorsOpt; userId <- usersIds) {
        extendedWidget.maintainers.ulCreators.lstUsers.addItem(userId)
      }

      for (usersIds <- maintainers.publishersOpt; userId <- usersIds) {
        extendedWidget.maintainers.ulCreators.lstUsers.addItem(userId)
      }
    }

    for (Relationship(parents, children) <- parameters.relationshipOpt) {
      extendedWidget.relationships.chkEnabled.check()

      parents match {
        case Relationship.Logical(value) if value =>
          extendedWidget.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.with_parents"
        case Relationship.Logical(_) =>
          extendedWidget.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.without_parents"
        case Relationship.Exact(metaId) =>
          extendedWidget.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of"
          extendedWidget.relationships.txtParents.value = metaId.toString

        case _ =>
          extendedWidget.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.unspecified"
      }

      children match {
        case Relationship.Logical(value) if value =>
          extendedWidget.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.with_children"
        case Relationship.Logical(_) =>
          extendedWidget.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.without_children"
        case Relationship.Exact(metaId) =>
          extendedWidget.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.with_children_of"
          extendedWidget.relationships.txtChildren.value = metaId.toString

        case _ =>
          extendedWidget.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.unspecified"
      }
    }
  }

}