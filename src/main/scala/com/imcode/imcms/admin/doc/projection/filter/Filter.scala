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
  val extendedView: ExtendedFilterView = new ExtendedFilterView

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
        condOpt(basicView.idRange.txtStart.trimmedValue) {
          case value if value.nonEmpty => value match {
            case NonNegInt(start) => start
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        },
        condOpt(basicView.idRange.txtEnd.trimmedValue) {
          case value if value.nonEmpty => value match {
            case NonNegInt(end) => end
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value".i)
          }
        }
      )
    }

    val textOpt = when(basicView.text.chkEnabled.checked)(basicView.text.txtText.trimmedValue)

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

    Seq(extendedView.categories.chkEnabled, extendedView.dates.chkEnabled, extendedView.relationships.chkEnabled, extendedView.maintainers.chkEnabled).foreach { chk =>
      chk.uncheck()
    }

    Seq(extendedView.dates.drCreated, extendedView.dates.drModified, extendedView.dates.drPublished, extendedView.dates.drExpired).foreach { dr =>
      dr.cbRangeType.selection = DateRangeType.Undefined
    }

    Seq(extendedView.maintainers.ulCreators, extendedView.maintainers.ulPublishers).foreach { ul =>
      ul.chkEnabled.uncheck()
      //ul.chkEnabled.fireValueChange(repaintIsNotNeeded = true)
      ul.lstUsers.removeAllItems()
    }

    extendedView.categories.tcsCategories.removeAllItems()

    for {
      categoryType <- imcmsServices.getCategoryMapper.getAllCategoryTypes
      category <- imcmsServices.getCategoryMapper.getAllCategoriesOfType(categoryType)
    } {
      extendedView.categories.tcsCategories.addItem(category)
      extendedView.categories.tcsCategories.setItemCaption(category, categoryType.getName + ":" + category.getName)
      category.getImageUrl.asOption.foreach(url => extendedView.categories.tcsCategories.setItemIcon(category, new ExternalResource(url)))
    }

    extendedView.relationships.cbParents.selection = "docs_projection.extended_filter.cb_relationships_parents.item.unspecified"
    extendedView.relationships.cbChildren.selection = "docs_projection.extended_filter.cb_relationships_children.item.unspecified"
  }


  private def createExtendedParamsOpt(): Option[Try[ExtendedFilterParams]] = {
    if (basicView.extended.chkEnabled.unchecked) None
    else Some(getExtendedParameters())
  }

  private def getExtendedParameters(): Try[ExtendedFilterParams] = Try {
    // Date meaning to DateRange
    val datesOpt: Option[Map[String, DateRange]] = when(extendedView.dates.chkEnabled.checked) {
      import extendedView.dates._

      val datesMap = for {
        (field, dr) <- Map(
          DocumentIndex.FIELD__CREATED_DATETIME -> drCreated,
          DocumentIndex.FIELD__MODIFIED_DATETIME -> drModified,
          DocumentIndex.FIELD__PUBLICATION_START_DATETIME -> drPublished,
          DocumentIndex.FIELD__ARCHIVED_DATETIME -> drArchived,
          DocumentIndex.FIELD__PUBLICATION_END_DATETIME -> drExpired
        )
        if dr.cbRangeType.firstSelected != DateRangeType.Undefined
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

    val relationshipOpt: Option[Relationship] = when(extendedView.relationships.chkEnabled.checked) {
      val withParents = extendedView.relationships.cbParents.firstSelected match {
        case "docs_projection.extended_filter.cb_relationships_parents.item.unspecified" => Relationship.Unspecified
        case "docs_projection.extended_filter.cb_relationships_parents.item.with_parents" => Relationship.Logical(true)
        case "docs_projection.extended_filter.cb_relationships_parents.item.without_parents" => Relationship.Logical(false)
        case "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of" => Relationship.Exact(extendedView.relationships.txtParents.value.toInt)
      }

      val withChildren = extendedView.relationships.cbChildren.firstSelected match {
        case "docs_projection.extended_filter.cb_relationships_children.item.unspecified" => Relationship.Unspecified
        case "docs_projection.extended_filter.cb_relationships_children.item.with_children" => Relationship.Logical(true)
        case "docs_projection.extended_filter.cb_relationships_children.item.without_children" => Relationship.Logical(false)
        case "docs_projection.extended_filter.cb_relationships_children.item.with_children_of" => Relationship.Exact(extendedView.relationships.txtChildren.value.toInt)
      }

      Relationship(withParents, withChildren)
    }

    val categoriesOpt: Option[Set[String]] = when(extendedView.categories.chkEnabled.checked) {
      extendedView.categories.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].asScala.to[Set]
    }

    val maintainersOpt: Option[Maintainers] = when(extendedView.maintainers.chkEnabled.checked) {
      val creatorsOpt: Option[Set[UserId]] = when(extendedView.maintainers.ulCreators.chkEnabled.checked) {
        extendedView.maintainers.ulCreators.lstUsers.itemIds.asScala.to[Set]
      }

      val publishersOpt: Option[Set[UserId]] = when(extendedView.maintainers.ulPublishers.chkEnabled.checked) {
        extendedView.maintainers.ulPublishers.lstUsers.itemIds.asScala.to[Set]
      }

      Maintainers(creatorsOpt, publishersOpt)
    }

    ExtendedFilterParams(datesOpt, categoriesOpt, relationshipOpt, maintainersOpt)
  }

  private def setExtendedParamsOpt(paramsOpt: Option[ExtendedFilterParams]) {
    basicView.extended.chkEnabled.checked = paramsOpt.isDefined

    val parameters = paramsOpt.getOrElse(ExtendedFilterParams())
    for (dates <- parameters.datesOpt) {
      extendedView.dates.chkEnabled.check()

      for ((dateRangeName, dateRange) <- dates) {
        import extendedView.dates._

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
      extendedView.categories.chkEnabled.check()

      categories.foreach(extendedView.categories.tcsCategories.select)
    }

    for (maintainers <- parameters.maintainersOpt) {
      extendedView.maintainers.chkEnabled.check()

      for (usersIds <- maintainers.creatorsOpt; userId <- usersIds) {
        extendedView.maintainers.ulCreators.lstUsers.addItem(userId)
      }

      for (usersIds <- maintainers.publishersOpt; userId <- usersIds) {
        extendedView.maintainers.ulCreators.lstUsers.addItem(userId)
      }
    }

    for (Relationship(parents, children) <- parameters.relationshipOpt) {
      extendedView.relationships.chkEnabled.check()

      parents match {
        case Relationship.Logical(value) if value =>
          extendedView.relationships.cbParents.selection = "docs_projection.extended_filter.cb_relationships_parents.item.with_parents"
        case Relationship.Logical(_) =>
          extendedView.relationships.cbParents.selection = "docs_projection.extended_filter.cb_relationships_parents.item.without_parents"
        case Relationship.Exact(metaId) =>
          extendedView.relationships.cbParents.selection = "docs_projection.extended_filter.cb_relationships_parents.item.with_parent_of"
          extendedView.relationships.txtParents.value = metaId.toString

        case _ =>
          extendedView.relationships.cbParents.selection = "docs_projection.extended_filter.cb_relationships_parents.item.unspecified"
      }

      children match {
        case Relationship.Logical(value) if value =>
          extendedView.relationships.cbChildren.selection = "docs_projection.extended_filter.cb_relationships_children.item.with_children"
        case Relationship.Logical(_) =>
          extendedView.relationships.cbChildren.selection = "docs_projection.extended_filter.cb_relationships_children.item.without_children"
        case Relationship.Exact(metaId) =>
          extendedView.relationships.cbChildren.selection = "docs_projection.extended_filter.cb_relationships_children.item.with_children_of"
          extendedView.relationships.txtChildren.value = metaId.toString

        case _ =>
          extendedView.relationships.cbChildren.selection = "docs_projection.extended_filter.cb_relationships_children.item.unspecified"
      }
    }
  }

}