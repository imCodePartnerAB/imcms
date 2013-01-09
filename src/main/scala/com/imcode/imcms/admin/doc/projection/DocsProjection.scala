package com.imcode
package imcms
package admin.doc.projection

import com.imcode.util.event.Publisher
import scala.collection.JavaConverters._
import scala.util.control.{Exception => Ex}
import java.util.concurrent.atomic.AtomicReference
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog.ErrorDialog
import com.imcode.imcms.vaadin.data._
import scala.PartialFunction._
import com.imcode.imcms.admin.doc.projection.filter._
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.{LifeCyclePhase, DocumentTypeDomainObject, DocumentDomainObject}
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.index.DocumentIndex
import org.apache.commons.lang.StringUtils
import java.net.URLDecoder
import org.apache.solr.common.util.DateUtil
import com.imcode.imcms.admin.doc.projection.filter.DateRange
import com.imcode.imcms.admin.doc.projection.filter.IdRange
import com.imcode.imcms.api.I18nLanguage
import com.vaadin.ui.CheckBox


class DocsProjection(user: UserDomainObject) extends Publisher[Seq[DocumentDomainObject]] with Log4jLoggerSupport {
  val basicFilter = new BasicFilter
  val advancedFilter = new AdvancedFilter
  val docsContainer = new IndexedDocsContainer(user)
  val docsUI = new IndexedDocsUI(docsContainer) with FullSize
  private val selectionRef = new AtomicReference(Seq.empty[DocumentDomainObject])

  val ui = new DocsProjectionUI(basicFilter.ui, advancedFilter.ui, docsUI) { ui =>
    val basicFilterUI = basicFilter.ui

    basicFilterUI.lytAdvanced.btnCustomize.addClickHandler { ui.toggleAdvancedFilter() }
    basicFilterUI.chkAdvanced.addValueChangeHandler {
      if (!basicFilterUI.chkAdvanced.booleanValue) ui.isAdvancedFilterVisible = false
    }

    basicFilterUI.lytButtons.btnFilter.addClickHandler { reload() }
    basicFilterUI.lytButtons.btnReset.addClickHandler { reset() }

    override def attach() {
      super.attach()
      reset()
    }
  }

  docsUI.addValueChangeHandler {
    selectionRef.set(docsUI.value.asScala.map(docsContainer.getItem(_).doc).toSeq)
    notifyListeners()
  }


  def reset() {
    basicFilter.reset()
    advancedFilter.reset()
    reload()
  }


  def reload() {
    basicFilter.setVisibleDocsRangeInputPrompt(docsContainer.visibleDocsRange)

    createSolrQuery() match {
      case Left(throwable) =>
        docsContainer.setSolrQueryOpt(None)
        ui.rootWindow.show(new ErrorDialog(throwable.getMessage.i))

      case Right(solrQuery) =>
        ui.removeComponent(0, 1)
        ui.addComponent(docsUI, 0, 1)

        docsContainer.setSolrQueryOpt(Some(solrQuery))
    }
  }


  /**
   * Creates and returns Solr query string.
   *
   * @return query Solr query string.
   */
  def createSolrQuery(): Throwable Either SolrQuery = Ex.allCatch.either {
    val basicFormUI = basicFilter.ui
    val advancedFormUI = advancedFilter.ui

    val idRangeOpt =
      if (basicFormUI.chkIdRange.isUnchecked) None
      else {
        val startOpt = condOpt(basicFormUI.lytIdRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case AnyInt(start) => start
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        val endOpt = condOpt(basicFormUI.lytIdRange.txtEnd.trim) {
          case value if value.nonEmpty => value match {
            case AnyInt(end) => end
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        whenOpt(startOpt.isDefined || endOpt.isDefined) {
          IdRange(startOpt, endOpt)
        }
      }


    val textOpt: Option[String] =
      if (basicFormUI.chkText.isUnchecked) None
      else condOpt(basicFormUI.txtText.trim) {
        case value if value.nonEmpty => value
      }


    val typesOpt: Option[List[Int]] =
      if (basicFormUI.chkType.isUnchecked) None
      else {
        import basicFormUI.lytTypes._

        Map(chkFile -> DocumentTypeDomainObject.FILE_ID,
            chkText -> DocumentTypeDomainObject.TEXT_ID,
            chkHtml -> DocumentTypeDomainObject.HTML_ID,
            chkUrl -> DocumentTypeDomainObject.URL_ID
        ).filterKeys(_.isChecked).values.toList match {
          case Nil => None
          case values => Some(values)
        }
      }

    val phasesOpt: Option[Seq[LifeCyclePhase]] =
      if (basicFormUI.chkPhase.isUnchecked) None
      else {
        import basicFormUI.lytPhases._

        Map(chkNew -> LifeCyclePhase.NEW,
            chkPublished -> LifeCyclePhase.PUBLISHED,
            chkUnpublished -> LifeCyclePhase.UNPUBLISHED,
            chkApproved -> LifeCyclePhase.APPROVED,
            chkDisapproved -> LifeCyclePhase.DISAPPROVED,
            chkArchived -> LifeCyclePhase.ARCHIVED
        ).filterKeys(_.isChecked).values.toSeq match {
          case Nil => None
          case values => Some(values)
        }
      }

    val datesOpt: Option[Map[String, DateRange]] =
      if (advancedFormUI.chkDates.isUnchecked) None
      else {
        import advancedFormUI.lytDates._

        val datesMap =
          for {
            (field, dr) <- Map(
              DocumentIndex.FIELD__CREATED_DATETIME -> drCreated,
              DocumentIndex.FIELD__MODIFIED_DATETIME -> drModified,
              DocumentIndex.FIELD__PUBLICATION_START_DATETIME -> drPublished,
              DocumentIndex.FIELD__ACTIVATED_DATETIME -> drArchived,
              DocumentIndex.FIELD__PUBLICATION_END_DATETIME -> drExpired
            )
            if dr.cbRangeType.value != DateRangeType.Undefined
            start = dr.dtFrom.valueOpt
            end = dr.dtTo.valueOpt
            if start.isDefined || end.isDefined
          } yield {
            val startFixed = start.map { dt =>
              new org.joda.time.DateTime(dt).withMillisOfDay(0).toDate
            }

            val endFixed = end.map { dt =>
              new org.joda.time.DateTime(dt).plusDays(1).withMillisOfDay(0).minus(1).toDate
            }

            field -> DateRange(startFixed, endFixed)
          }

        whenNotEmpty(datesMap)(_.toMap)
      }


    val relationshipOpt: Option[Relationship] =
      if (advancedFormUI.chkRelationships.isUnchecked) None
      else {
        val hasParents = PartialFunction.condOpt(advancedFormUI.lytRelationships.cbParents.value) {
          case "docs_projection.advanced_filter.cb_relationships_parents.item.has_parents" => true
          case "docs_projection.advanced_filter.cb_relationships_parents.item.no_parents" => false
        }

        val hasChildren = PartialFunction.condOpt(advancedFormUI.lytRelationships.cbChildren.value) {
          case "docs_projection.advanced_filter.cb_relationships_children.item.has_children" => true
          case "docs_projection.advanced_filter.cb_relationships_children.item.no_children" => false
        }

        whenOpt(hasParents.isDefined || hasChildren.isDefined) {
          Relationship(hasParents, hasChildren)
        }
      }

    val categoriesOpt: Option[Seq[String]] =
      if (advancedFormUI.chkCategories.isUnchecked) None
      else {
        whenNotEmpty(advancedFormUI.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].asScala.toSeq)(identity)
      }

    val maintainersOpt: Option[Maintainers] =
      if (advancedFormUI.chkMaintainers.isUnchecked) None
      else {
        val creators: Option[Seq[UserId]] =
          if (advancedFormUI.lytMaintainers.ulCreators.chkEnabled.isUnchecked) None
          else whenNotEmpty(advancedFormUI.lytMaintainers.ulCreators.lstUsers.itemIds.asScala.toSeq)(identity)

        val publishers: Option[Seq[UserId]] =
          if (advancedFormUI.lytMaintainers.ulPublishers.chkEnabled.isUnchecked) None
          else whenNotEmpty(advancedFormUI.lytMaintainers.ulPublishers.lstUsers.itemIds.asScala.toSeq)(identity)

        whenOpt(creators.isDefined || publishers.isDefined) {
          Maintainers(creators, publishers)
        }
      }

    val languagesOpt: Option[Seq[I18nLanguage]] =
      if (basicFormUI.chkLanguage.isUnchecked) None
      else {
        val languages = (
          for {
            _chk@(chkLanguage: CheckBox with GenericData[I18nLanguage]) <- basicFormUI.lytLanguages.getComponentIterator.asScala
            if chkLanguage.isChecked
          } yield
            chkLanguage.data
        ).toSeq

        whenNotEmpty(languages)(identity)
      }

    def escape(text: String): String = {
      val SOLR_SPECIAL_CHARACTERS = Array("+", "-", "&", "|", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":", "\\", "/")
      val SOLR_REPLACEMENT_CHARACTERS = Array("\\+", "\\-", "\\&", "\\|", "\\!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\\\"", "\\~", "\\*", "\\?", "\\:", "\\\\", "\\/")

      StringUtils.replaceEach(text, SOLR_SPECIAL_CHARACTERS, SOLR_REPLACEMENT_CHARACTERS)
    }

    List(
      textOpt.map { text =>
        val escapedText = escape(text)

        Seq(DocumentIndex.FIELD__META_ID, DocumentIndex.FIELD__META_HEADLINE, DocumentIndex.FIELD__META_TEXT,
            DocumentIndex.FIELD__KEYWORD, DocumentIndex.FIELD__ALIAS, DocumentIndex.FIELD__TEXT
        ).map(field => """%s:"%s"""".format(field, escapedText)).mkString(" ")
      }
    ).flatten |> {
      case Nil => "*:*"
      case terms => terms.mkString(" ")
    } |> { solrQueryStirng =>
      if (logger.isDebugEnabled) logger.debug("Projection SOLr query Q parameter value: %s.".format(solrQueryStirng))

      new SolrQuery(solrQueryStirng)
    } |>> { solrQuery =>
      for (IdRange(start, end) <- idRangeOpt) {
        solrQuery.addFilterQuery(
          "%s:[%s TO %s]".format(DocumentIndex.FIELD__META_ID, start.getOrElse("*"), end.getOrElse("*"))
        )
      }

      for (types <- typesOpt) {
        solrQuery.addFilterQuery(
          "%s:%s".format(DocumentIndex.FIELD__DOC_TYPE_ID, types.mkString(" "))
        )
      }

      for (phases <- phasesOpt) {
        val now = new java.util.Date
        val phasesFilterQuery = phases.map(_.asQuery(now)) match {
          case Seq(query) => query.toString
          case queries => queries.mkString("((", ") (" ,"))")
        }

        solrQuery.addFilterQuery(phasesFilterQuery)
      }

      for {
        dates <- datesOpt
        dateFormat = DateUtil.getThreadLocalDateFormat
        (field, DateRange(from, to)) <- dates
      } {
        solrQuery.addFilterQuery("%s:[%s TO %s]".format(field,
          from.map(dateFormat.format).getOrElse("*"),
          to.map(dateFormat.format).getOrElse("*"))
        )
      }

      for (Relationship(hasParents, hasChildren) <- relationshipOpt) {
        hasParents.foreach { value =>
          solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__HAS_PARENTS, value))
        }

        hasChildren.foreach { value =>
          solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__HAS_CHILDREN, value))
        }
      }

      for (categories <- categoriesOpt) {
        solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__CATEGORY_ID, categories.mkString(" ")))
      }

      for (Maintainers(creators, publishers) <- maintainersOpt) {
        creators.foreach { creators =>
          solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__CREATOR_ID, creators.mkString(" ")))
        }

        publishers.foreach { publishers =>
          solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__PUBLISHER_ID, publishers.mkString(" ")))
        }
      }

      for (languages <- languagesOpt) {
        solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__LANGUAGE_CODE, languages.map(_.getCode).mkString(" ")))
      }
    } |>> { solrQuery =>
      solrQuery.setRows(20)
      if (logger.isDebugEnabled)
        logger.debug("Projection SOLr query: %s.".format(URLDecoder.decode(solrQuery.toString, "UTF-8")))
    }
  } // def createSolrQuery()


  def selection: Seq[DocumentDomainObject] = selectionRef.get

  override def notifyListeners(): Unit = notifyListeners(selection)
}