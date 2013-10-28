package com.imcode
package imcms
package admin.doc.projection

import com.imcode.util.event.Publisher
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog.ErrorDialog
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._

import com.imcode.imcms.admin.doc.projection.filter._
import com.imcode.imcms.admin.doc.projection.filter.DateRange
import com.imcode.imcms.admin.doc.projection.filter.BasicFilterParameters
import com.imcode.imcms.admin.doc.projection.filter.Maintainers
import com.imcode.imcms.admin.doc.projection.filter.ExtendedFilterParameters
import com.imcode.imcms.admin.doc.projection.filter.IdRange

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.index.DocumentIndex
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject

import org.apache.commons.lang3.StringUtils
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.common.util.DateUtil
import com.vaadin.ui.{Button, Component, UI}

import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicReference

import scala.collection.JavaConverters._
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import com.imcode.imcms.admin.doc.projection.container.{IndexedDocsUI, IndexedDocsContainer}


class DocsProjection(user: UserDomainObject, multiSelect: Boolean = true) extends Publisher[Seq[DocumentDomainObject]] with Log4jLoggerSupport with ImcmsServicesSupport {

  @transient
  private var history = List.empty[(BasicFilterParameters, ExtendedFilterParameters)]
  @transient
  private var currentValidFilterParams = (BasicFilterParameters(), ExtendedFilterParameters())

  private def parentsRenderer(metaId: DocId, parentsIds: JCollection[DocId]): Component = {
    if (parentsIds.isEmpty) null
    else new Button("docs_projection.result.show_parents".f(parentsIds.size)) with LinkStyle |>> { btn =>
      btn.addClickHandler { _ =>
        val relationshipOpt = Some(
          Relationship(children = Relationship.Exact(metaId))
        )

        val basicFilterParams = new BasicFilterParameters(languagesOpt = filter.selectedLanguagesOpt())
        val extendedFilterParams = new ExtendedFilterParameters(relationshipOpt = relationshipOpt)

        setFilterParameters(FilterParameters(basicFilterParams, Some(extendedFilterParams)))
      }
    }
  }

  private def childrenRenderer(metaId: DocId, childrenIds: JCollection[DocId]): Component = {
    if (childrenIds.isEmpty) null
    else new Button("docs_projection.result.show_children".f(parentsIds.size)) with LinkStyle |>> { btn =>
      btn.addClickHandler { _ =>
        val relationshipOpt = Some(
          Relationship(parents = Relationship.Exact(metaId))
        )

        val basicFilterParams = new BasicFilterParameters(languagesOpt = filter.selectedLanguagesOpt())
        val extendedFilterParams = new ExtendedFilterParameters(relationshipOpt = relationshipOpt)

        setFilterParameters(FilterParameters(basicFilterParams, Some(extendedFilterParams)))
      }
    }
  }

  val filter = new Filter
  val docsContainer = new IndexedDocsContainer(user, childrenRenderer = childrenRenderer, parentsRenderer = parentsRenderer)

  val docsUI = new IndexedDocsUI(docsContainer) with FullSize |>> { _.setMultiSelect(multiSelect) }
  private val selectionRef = new AtomicReference(Seq.empty[DocumentDomainObject])

  val ui = new DocsProjectionUI(filter.basicUI, filter.extendedUI, docsUI) { ui =>
    val basicFilterUI = filter.basicUI

    basicFilterUI.extended.btnCustomize.addClickHandler { _ => ui.toggleExtendedFilter() }
    basicFilterUI.filterButtons.btnApplyFilter.addClickHandler { _ => reload() }
    basicFilterUI.filterButtons.btnReset.addClickHandler { _ => reset() }
    basicFilterUI.filterButtons.btnBack.addClickHandler { _ => goBack() }
    filter.basicUI.filterButtons.btnApplyPredefinedFilter.addClickHandler { _ =>
      new PredefinedFilterDialog |>> { dlg =>
        dlg.setOkButtonHandler {
          // combo box
          // published by me, created by me

          dlg.close()
        }
      } |> UI.getCurrent.addWindow
    }

    override def attach() {
      super.attach()
      reset()
    }
  }

  docsUI.addValueChangeHandler { _ =>
    selectionRef.set(docsUI.value.asScala.map(docIx => docsContainer.getItem(docIx).fields).to[Seq])
    notifyListeners()
  }


  def reset() {
    setFilterParameters(FilterParameters())
  }

  def setFilterParameters(params: FilterParameters) {
//    if (currentValidFilterParams._1 != basicParams || currentValidFilterParams._2 != extendedParams) {
//      history +:= (currentValidFilterParams._1, currentValidFilterParams._2)
//    }

    filter.setParameters(params)
    //currentValidFilterParams = (basicParams, extendedParams)

    reload()
  }

  def goBack() {
//    for ((basicParams, extendedParams) <- history.headOption) {
//      history = history.drop(1)
//
//      filter.setParameters(basicParams)
//      extendedFilter.setParameters(extendedParams)
//
//      reload()
//    }
  }


  def reload() {
    filter.setVisibleDocsRangeInputPrompt(docsContainer.visibleDocsRange())

    createSolrQuery() match {
      case Failure(throwable) =>
        docsContainer.setQueryOpt(None)
        new ErrorDialog(throwable.getMessage.i) |> UI.getCurrent.addWindow

      case Success(solrQuery) =>
        ui.removeComponent(0, 1)
        ui.addComponent(docsUI, 0, 1)

        docsContainer.setQueryOpt(Some(solrQuery))
    }
  }


  /**
   * Creates and returns Solr query string.
   *
   * @return query Solr query string.
   */
  def createSolrQuery(): Try[SolrQuery] = {
    for {
      params <- filter.getParameters()
    } yield {
      createSolrQuery(params)
    }
  }

  private def createSolrQuery(params: FilterParameters): SolrQuery = {
    def escape(text: String): String = {
      val SOLR_SPECIAL_CHARACTERS = Array("+", "-", "&", "|", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":", "\\", "/")
      val SOLR_REPLACEMENT_CHARACTERS = Array("\\+", "\\-", "\\&", "\\|", "\\!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\\\"", "\\~", "\\*", "\\?", "\\:", "\\\\", "\\/")

      StringUtils.replaceEach(text, SOLR_SPECIAL_CHARACTERS, SOLR_REPLACEMENT_CHARACTERS)
    }

    List(
      params.basic.textOpt.collect {
        case text if text.nonEmpty =>
          val escapedText = text // escape(text)

          Seq(DocumentIndex.FIELD__META_ID, DocumentIndex.FIELD__META_HEADLINE, DocumentIndex.FIELD__META_TEXT,
            DocumentIndex.FIELD__KEYWORD, DocumentIndex.FIELD__ALIAS, DocumentIndex.FIELD__TEXT
          ).map(field => "%s:%s".format(field, escapedText)).mkString(" ")
      }
    ).flatten |> {
      case Nil => "*:*"
      case terms => terms.mkString(" ")
    } |> { solrQueryStirng =>
      if (logger.isDebugEnabled) logger.debug("Projection SOLr query Q parameter value: %s.".format(solrQueryStirng))

      new SolrQuery(solrQueryStirng)
    } |>> { solrQuery =>
      for (IdRange(start, end) <- params.basic.idRangeOpt) {
        solrQuery.addFilterQuery(
          "%s:[%s TO %s]".format(DocumentIndex.FIELD__META_ID, start.getOrElse("*"), end.getOrElse("*"))
        )
      }

      for (types <- params.basic.docTypesOpt if types.nonEmpty) {
        solrQuery.addFilterQuery(
          "%s:%s".format(DocumentIndex.FIELD__DOC_TYPE_ID, types.mkString(" "))
        )
      }

      for (phases <- params.basic.phasesOpt if phases.nonEmpty) {
        val now = new java.util.Date
        val phasesFilterQuery = phases.map(_.asQuery(now)) match {
          case Seq(query) => query.toString
          case queries => queries.mkString("((", ") (" ,"))")
        }

        solrQuery.addFilterQuery(phasesFilterQuery)
      }

      for (languages <- params.basic.languagesOpt if languages.nonEmpty) {
        solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__LANGUAGE_CODE, languages.map(_.getCode).mkString(" ")))
      }

      params.extendedOpt.foreach { extendedParams =>
        for {
          dates <- extendedParams.datesOpt if dates.nonEmpty
          dateFormat = DateUtil.getThreadLocalDateFormat
          (field, DateRange(from, to)) <- dates
        } {
          solrQuery.addFilterQuery("%s:[%s TO %s]".format(field,
            from.map(dateFormat.format).getOrElse("*"),
            to.map(dateFormat.format).getOrElse("*"))
          )
        }

        for (Relationship(withParents, withChildren) <- extendedParams.relationshipOpt) {
          withParents match {
            case Relationship.Logical(value) =>
              solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__HAS_PARENTS, value))

            case Relationship.Exact(docId) =>
              solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__HAS_PARENTS, true))
              solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__PARENT_ID, docId))

            case _ =>
          }

          withChildren match {
            case Relationship.Logical(value) =>
              solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__HAS_CHILDREN, value))

            case Relationship.Exact(docId) =>
              solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__HAS_CHILDREN, true))
              solrQuery.addFilterQuery("%s:%s".format(DocumentIndex.FIELD__CHILD_ID, docId))

            case _ =>
          }
        }

        for (categories <- extendedParams.categoriesOpt if categories.nonEmpty) {
          solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__CATEGORY_ID, categories.mkString(" ")))
        }

        for (Maintainers(creatorsOpt, publishersOpt) <- extendedParams.maintainersOpt) {
          creatorsOpt.foreach { creators =>
            solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__CREATOR_ID, creators.mkString(" ")))
          }

          publishersOpt.foreach { publishers =>
            solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__PUBLISHER_ID, publishers.mkString(" ")))
          }
        }
      }
    } |>> { solrQuery =>
    //solrQuery.setRows(20)
      if (logger.isDebugEnabled)
        logger.debug("Projection SOLr query: %s.".format(URLDecoder.decode(solrQuery.toString, "UTF-8")))
    }
  }  // def createSolrQuery()


  def selection: Seq[DocumentDomainObject] = selectionRef.get

  override def notifyListeners(): Unit = notifyListeners(selection)
}