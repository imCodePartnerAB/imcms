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

  val basicView: BasicFilterView = new BasicFilterView
  val extendeView: ExtendedFilterView = new ExtendedFilterView

  def setMetaIdRangePrompt(range: Option[(MetaId, MetaId)]) {
    range.map {
      case (start, end) => (start.toString, end.toString)
    }.getOrElse("", "") |> {
      case (start, end) =>
        basicView.idRange.txtStart.setInputPrompt(start)
        basicView.idRange.txtEnd.setInputPrompt(end)
    }
  }


  def setParameters(parameters: FilterParameters) {
    resetView()

    setBasicParameters(parameters.basic)
    setExtendedParamsOpt(parameters.extendedOpt)
  }

  private def setBasicParameters(parameters: BasicFilterParams) {
    basicView.idRange.chkEnabled.checked = parameters.idRangeOpt.isDefined
    basicView.text.chkEnabled.checked = parameters.textOpt.isDefined
    basicView.types.chkEnabled.checked = parameters.docTypesOpt.isDefined
    basicView.languages.chkEnabled.checked = parameters.languagesOpt.isDefined
    basicView.phases.chkEnabled.checked = parameters.phasesOpt.isDefined
    //ui.extended.chkEnabled.checked = parameters.extendedSearchNameOpt.isDefined

    basicView.text.txtText.value = parameters.textOpt.getOrElse("")

    parameters.idRangeOpt.collect {
      case IdRange(start, end) => (start.map(_.toString).getOrElse(""), end.map(_.toString).getOrElse(""))
    } getOrElse("", "") match {
      case (start, end) =>
        basicView.idRange.txtStart.value = start
        basicView.idRange.txtEnd.value = end
    }

    basicView.types.chkText.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.TEXT))
    basicView.types.chkFile.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.FILE))
    basicView.types.chkHtml.checked = parameters.docTypesOpt.exists(types => types.contains(DocumentTypeDomainObject.HTML))

    val isChecked: (DocumentLanguage => Boolean) = {
      val languages = parameters.languagesOpt.getOrElse(Set.empty)
      language => (languages.isEmpty && imcmsServices.getDocumentI18nSupport.isDefault(language)) || languages.contains(language)
    }

    basicView.languages.layout.removeAllComponents()

    for (language <- imcmsServices.getDocumentI18nSupport.getLanguages.asScala) {
      val chkLanguage = new CheckBox(language.getNativeName) with TypedData[DocumentLanguage] |>> { chk =>
        chk.setIcon(Theme.Icon.Language.flag(language))
        chk.data = language
        chk.checked = isChecked(language)
      }

      basicView.languages.layout.addComponent(chkLanguage)
    }
  }

  def createParams(): Try[FilterParameters] = {
    createBasicParams() match {
      case Failure(error) => Failure(error)
      case Success(basicParams) if basicView.extended.chkEnabled.unchecked => Success(FilterParameters(basicParams))
      case Success(basicParams) => createExtendedParamsOpt() match {
        case None => Success(FilterParameters(basicParams, None))
        case Some(Failure(e)) => Failure(e)
        case Some(Success(extendedParams)) => Success(FilterParameters(basicParams, Some(extendedParams)))
      }
    }
  }

  private def createBasicParams(): Try[BasicFilterParams] = Try {
    val idRangeOpt = when(basicView.idRange.chkEnabled.checked) {
      IdRange(
        condOpt(basicView.idRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case NonNegInt(start) => start
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        },
        condOpt(basicView.idRange.txtEnd.trim) {
          case value if value.nonEmpty => value match {
            case NonNegInt(end) => end
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        }
      )
    }

    val textOpt = when(basicView.text.chkEnabled.checked)(basicView.text.txtText.trim)

    val typesOpt = when(basicView.types.chkEnabled.checked) {
      Set(
        when(basicView.types.chkText.checked) { DocumentTypeDomainObject.TEXT },
        when(basicView.types.chkFile.checked) { DocumentTypeDomainObject.FILE },
        when(basicView.types.chkHtml.checked) { DocumentTypeDomainObject.HTML },
        when(basicView.types.chkUrl.checked) { DocumentTypeDomainObject.URL }
      ).flatten
    }

    val phasesOpt: Option[Set[LifeCyclePhase]] = when(basicView.phases.chkEnabled.checked) {
      import basicView.phases._

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
  def selectedLanguagesOpt(): Option[Set[DocumentLanguage]] = when(basicView.languages.chkEnabled.checked) {
    (
      for {
        _chk@(chkLanguage: CheckBox with TypedData[DocumentLanguage]) <- basicView.languages.layout.iterator.asScala
        if chkLanguage.checked
      } yield {
        chkLanguage.data
      }
    ).to[Set]
  }


  private def resetView() {
    // reset all top-level checkboxes
    Seq(basicView.idRange.chkEnabled, basicView.text.chkEnabled, basicView.types.chkEnabled, basicView.phases.chkEnabled, basicView.extended.chkEnabled).foreach { chk =>
      chk.uncheck()
    }

    // reset all child level checkboxes
    Seq(basicView.phases.chkNew, basicView.phases.chkPublished, basicView.phases.chkUnpublished, basicView.phases.chkApproved, basicView.phases.chkDisapproved, basicView.phases.chkArchived).foreach { chk =>
      chk.uncheck()
    }

    Seq(extendeView.categories.chkEnabled, extendeView.dates.chkEnabled, extendeView.relationships.chkEnabled, extendeView.maintainers.chkEnabled).foreach { chk =>
      chk.uncheck()
    }

    Seq(extendeView.dates.drCreated, extendeView.dates.drModified, extendeView.dates.drPublished, extendeView.dates.drExpired).foreach { dr =>
      dr.cbRangeType.value = DateRangeType.Undefined
    }

    Seq(extendeView.maintainers.ulCreators, extendeView.maintainers.ulPublishers).foreach { ul =>
      ul.chkEnabled.uncheck()
      //ul.chkEnabled.fireValueChange(repaintIsNotNeeded = true)
      ul.lstUsers.removeAllItems()
    }

    extendeView.categories.tcsCategories.removeAllItems()

    for {
      categoryType <- imcmsServices.getCategoryMapper.getAllCategoryTypes
      category <- imcmsServices.getCategoryMapper.getAllCategoriesOfType(categoryType)
    } {
      extendeView.categories.tcsCategories.addItem(category)
      extendeView.categories.tcsCategories.setItemCaption(category, categoryType.getName + ":" + category.getName)
      category.getImageUrl.asOption.foreach(url => extendeView.categories.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    extendeView.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.unspecified"
    extendeView.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.unspecified"
  }


  private def createExtendedParamsOpt(): Option[Try[ExtendedFilterParams]] = {
    if (basicView.extended.chkEnabled.unchecked) None
    else Some(getExtendedParameters())
  }

  private def getExtendedParameters(): Try[ExtendedFilterParams] = Try {
    // Date meaning to DateRange
    val datesOpt: Option[Map[String, DateRange]] = when(extendeView.dates.chkEnabled.checked) {
      import extendeView.dates._

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

    val relationshipOpt: Option[Relationship] = when(extendeView.relationships.chkEnabled.checked) {
      val withParents = extendeView.relationships.cbParents.value match {
        case "docs_projection.extended_filter.cb_relationships_parents.item.unspecified" => Relationship.Unspecified
        case "docs_projection.extended_filter.cb_relationships_parents.item.with_parents" => Relationship.Logical(true)
        case "docs_projection.extended_filter.cb_relationships_parents.item.without_parents" => Relationship.Logical(false)
        case "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of" => Relationship.Exact(extendeView.relationships.txtParents.value.toInt)
      }

      val withChildren = extendeView.relationships.cbChildren.value match {
        case "docs_projection.extended_filter.cb_relationships_children.item.unspecified" => Relationship.Unspecified
        case "docs_projection.extended_filter.cb_relationships_children.item.with_children" => Relationship.Logical(true)
        case "docs_projection.extended_filter.cb_relationships_children.item.without_children" => Relationship.Logical(false)
        case "docs_projection.extended_filter.cb_relationships_children.item.with_children_of" => Relationship.Exact(extendeView.relationships.txtChildren.value.toInt)
      }

      Relationship(withParents, withChildren)
    }

    val categoriesOpt: Option[Set[String]] = when(extendeView.categories.chkEnabled.checked) {
      extendeView.categories.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].asScala.to[Set]
    }

    val maintainersOpt: Option[Maintainers] = when(extendeView.maintainers.chkEnabled.checked) {
      val creatorsOpt: Option[Set[UserId]] = when(extendeView.maintainers.ulCreators.chkEnabled.checked) {
        extendeView.maintainers.ulCreators.lstUsers.itemIds.asScala.to[Set]
      }

      val publishersOpt: Option[Set[UserId]] = when(extendeView.maintainers.ulPublishers.chkEnabled.checked) {
        extendeView.maintainers.ulPublishers.lstUsers.itemIds.asScala.to[Set]
      }

      Maintainers(creatorsOpt, publishersOpt)
    }

    ExtendedFilterParams(datesOpt, categoriesOpt, relationshipOpt, maintainersOpt)
  }

  private def setExtendedParamsOpt(paramsOpt: Option[ExtendedFilterParams]) {
    basicView.extended.chkEnabled.checked = paramsOpt.isDefined

    val parameters = paramsOpt.getOrElse(ExtendedFilterParams())
    for (dates <- parameters.datesOpt) {
      extendeView.dates.chkEnabled.check()

      for ((dateRangeName, dateRange) <- dates) {
        import extendeView.dates._

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
      extendeView.categories.chkEnabled.check()

      categories.foreach(extendeView.categories.tcsCategories.select)
    }

    for (maintainers <- parameters.maintainersOpt) {
      extendeView.maintainers.chkEnabled.check()

      for (usersIds <- maintainers.creatorsOpt; userId <- usersIds) {
        extendeView.maintainers.ulCreators.lstUsers.addItem(userId)
      }

      for (usersIds <- maintainers.publishersOpt; userId <- usersIds) {
        extendeView.maintainers.ulCreators.lstUsers.addItem(userId)
      }
    }

    for (Relationship(parents, children) <- parameters.relationshipOpt) {
      extendeView.relationships.chkEnabled.check()

      parents match {
        case Relationship.Logical(value) if value =>
          extendeView.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.with_parents"
        case Relationship.Logical(_) =>
          extendeView.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.without_parents"
        case Relationship.Exact(metaId) =>
          extendeView.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of"
          extendeView.relationships.txtParents.value = metaId.toString

        case _ =>
          extendeView.relationships.cbParents.value = "docs_projection.extended_filter.cb_relationships_parents.item.unspecified"
      }

      children match {
        case Relationship.Logical(value) if value =>
          extendeView.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.with_children"
        case Relationship.Logical(_) =>
          extendeView.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.without_children"
        case Relationship.Exact(metaId) =>
          extendeView.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.with_children_of"
          extendeView.relationships.txtChildren.value = metaId.toString

        case _ =>
          extendeView.relationships.cbChildren.value = "docs_projection.extended_filter.cb_relationships_children.item.unspecified"
      }
    }
  }

}