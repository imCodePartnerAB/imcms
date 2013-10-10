package com.imcode
package imcms
package admin.doc.projection

import com.imcode.util.event.Publisher
import scala.collection.JavaConverters._
import java.util.concurrent.atomic.AtomicReference
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog.ErrorDialog
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.admin.doc.projection.filter._
import org.apache.solr.client.solrj.SolrQuery
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.index.DocumentIndex
import org.apache.commons.lang3.StringUtils
import java.net.URLDecoder
import org.apache.solr.common.util.DateUtil
import com.vaadin.ui.{Button, Component, UI}
import scala.util.Try
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.admin.doc.projection.filter.DateRange
import scala.util.Failure
import scala.Some
import com.imcode.imcms.admin.doc.projection.filter.BasicFilterParameters
import com.imcode.imcms.admin.doc.projection.filter.Maintainers
import com.imcode.imcms.admin.doc.projection.filter.AdvancedFilterParameters
import scala.util.Success
import com.imcode.imcms.admin.doc.projection.filter.IdRange


class DocsProjection(user: UserDomainObject, multiSelect: Boolean = true) extends Publisher[Seq[DocumentDomainObject]] with Log4jLoggerSupport with ImcmsServicesSupport {

  @transient
  private var history = List.empty[(BasicFilterParameters, AdvancedFilterParameters)]
  @transient
  private var currentValidFilterParams = (BasicFilterParameters(), AdvancedFilterParameters())

  private def parentsRenderer(doc: DocumentDomainObject): Component = imcmsServices.getDocumentMapper.getParentDocsIds(doc) match {
    case ids if ids.isEmpty => null
    case ids => new Button("docs_projection.result.show_parents".f(ids.size)) with LinkStyle |>> { btn =>
      btn.addClickHandler { _ =>
        val relationshipOpt = Some(
          Relationship(children = Relationship.Exact(doc.getId))
        )

        val basicFilterParams = new BasicFilterParameters(languagesOpt = basicFilter.selectedLanguagesOpt())
        val advancedFilterParams = new AdvancedFilterParameters(relationshipOpt = relationshipOpt)

        setFilterParameters(basicFilterParams, advancedFilterParams)
      }
    }
  }

  private def childrenRenderer(doc: DocumentDomainObject): Component = doc match {
    case textDoc: TextDocumentDomainObject =>
      textDoc.getChildDocumentIds match {
        case ids if ids.isEmpty => null
        case ids => new Button("docs_projection.result.show_children".f(ids.size)) with LinkStyle |>> { btn =>
          btn.addClickHandler { _ =>
            val relationshipOpt = Some(
              Relationship(parents = Relationship.Exact(doc.getId))
            )

            val basicFilterParams = new BasicFilterParameters(languagesOpt = basicFilter.selectedLanguagesOpt())
            val advancedFilterParams = new AdvancedFilterParameters(relationshipOpt = relationshipOpt)

            setFilterParameters(basicFilterParams, advancedFilterParams)
          }
        }
      }

    case _ => null
  }

  val basicFilter = new BasicFilter
  val advancedFilter = new AdvancedFilter
  val docsContainer = new IndexedDocsContainer(user, childrenRenderer = childrenRenderer, parentsRenderer = parentsRenderer)

  val docsUI = new IndexedDocsUI(docsContainer) with FullSize |>> { _.setMultiSelect(multiSelect) }
  private val selectionRef = new AtomicReference(Seq.empty[DocumentDomainObject])

  val ui = new DocsProjectionUI(basicFilter.ui, advancedFilter.ui, docsUI) { ui =>
    val basicFilterUI = basicFilter.ui

    basicFilterUI.lytAdvanced.btnCustomize.addClickHandler { _ => ui.toggleAdvancedFilter() }
    basicFilterUI.chkAdvanced.addValueChangeHandler { _ =>
      if (!basicFilterUI.chkAdvanced.value) ui.isAdvancedFilterVisible = false
    }

    basicFilterUI.lytButtons.btnFilter.addClickHandler { _ => reload() }
    basicFilterUI.lytButtons.btnReset.addClickHandler { _ => reset() }
    basicFilterUI.lytButtons.btnBack.addClickHandler { _ =>
      goBack()
    }

    override def attach() {
      super.attach()
      reset()
    }
  }

  docsUI.addValueChangeHandler { _ =>
    selectionRef.set(docsUI.value.asScala.map(docsContainer.getItem(_).doc).toSeq)
    notifyListeners()
  }


  def setFilterParameters(basicParams: BasicFilterParameters, advancedParams: AdvancedFilterParameters) {
    if (currentValidFilterParams._1 != basicParams || currentValidFilterParams._2 != advancedParams) {
      history +:= (currentValidFilterParams._1, currentValidFilterParams._2)
    }

    basicFilter.setParameters(basicParams)
    advancedFilter.setParameters(advancedParams)
    
    currentValidFilterParams = (basicParams, advancedParams)

    reload()
  }

  def goBack() {
    for ((basicParams, advancedParams) <- history.headOption) {
      history = history.drop(1)

      basicFilter.setParameters(basicParams)
      advancedFilter.setParameters(advancedParams)

      reload()
    }
  }


  def reset() {
    setFilterParameters(BasicFilterParameters(), AdvancedFilterParameters())
  }


  def reload() {
    basicFilter.setVisibleDocsRangeInputPrompt(docsContainer.visibleDocsRange())

    createSolrQuery() match {
      case Failure(throwable) =>
        docsContainer.setSolrQueryOpt(None)
        new ErrorDialog(throwable.getMessage.i) |> UI.getCurrent.addWindow

      case Success(solrQuery) =>
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
  def createSolrQuery(): Try[SolrQuery] = {
    for {
      basicParams <- basicFilter.getParameters()
      advancedParams <- advancedFilter.getParameters()
    } yield {
      createSolrQuery(basicParams, advancedParams)
    }
  }

  private def createSolrQuery(basicParams: BasicFilterParameters, advancedParams: AdvancedFilterParameters): SolrQuery = {
    def escape(text: String): String = {
      val SOLR_SPECIAL_CHARACTERS = Array("+", "-", "&", "|", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":", "\\", "/")
      val SOLR_REPLACEMENT_CHARACTERS = Array("\\+", "\\-", "\\&", "\\|", "\\!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\\\"", "\\~", "\\*", "\\?", "\\:", "\\\\", "\\/")

      StringUtils.replaceEach(text, SOLR_SPECIAL_CHARACTERS, SOLR_REPLACEMENT_CHARACTERS)
    }

    List(
      basicParams.textOpt.collect {
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
      for (IdRange(start, end) <- basicParams.idRangeOpt) {
        solrQuery.addFilterQuery(
          "%s:[%s TO %s]".format(DocumentIndex.FIELD__META_ID, start.getOrElse("*"), end.getOrElse("*"))
        )
      }

      for (types <- basicParams.docTypesOpt if types.nonEmpty) {
        solrQuery.addFilterQuery(
          "%s:%s".format(DocumentIndex.FIELD__DOC_TYPE_ID, types.mkString(" "))
        )
      }

      for (phases <- basicParams.phasesOpt if phases.nonEmpty) {
        val now = new java.util.Date
        val phasesFilterQuery = phases.map(_.asQuery(now)) match {
          case Seq(query) => query.toString
          case queries => queries.mkString("((", ") (" ,"))")
        }

        solrQuery.addFilterQuery(phasesFilterQuery)
      }

      for {
        dates <- advancedParams.datesOpt if dates.nonEmpty
        dateFormat = DateUtil.getThreadLocalDateFormat
        (field, DateRange(from, to)) <- dates
      } {
        solrQuery.addFilterQuery("%s:[%s TO %s]".format(field,
          from.map(dateFormat.format).getOrElse("*"),
          to.map(dateFormat.format).getOrElse("*"))
        )
      }

      for (Relationship(withParents, withChildren) <- advancedParams.relationshipOpt) {
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

      for (categories <- advancedParams.categoriesOpt if categories.nonEmpty) {
        solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__CATEGORY_ID, categories.mkString(" ")))
      }

      for (Maintainers(creatorsOpt, publishersOpt) <- advancedParams.maintainersOpt) {
        creatorsOpt.foreach { creators =>
          solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__CREATOR_ID, creators.mkString(" ")))
        }

        publishersOpt.foreach { publishers =>
          solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__PUBLISHER_ID, publishers.mkString(" ")))
        }
      }

      for (languages <- basicParams.languagesOpt if languages.nonEmpty) {
        solrQuery.addFilterQuery("%s:(%s)".format(DocumentIndex.FIELD__LANGUAGE_CODE, languages.map(_.getCode).mkString(" ")))
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