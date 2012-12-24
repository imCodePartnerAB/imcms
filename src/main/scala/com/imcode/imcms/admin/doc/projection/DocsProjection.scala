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
import com.imcode.imcms.admin.doc.projection.filter.{AdvancedFilter, BasicFilter, DateRange, IdRange, DateRangeType}
import org.apache.solr.client.solrj.SolrQuery
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.index.DocumentIndex
import org.apache.commons.lang.StringUtils


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
    updateUI()
    reload()
  }


  // todo: make private, should be called using listener
  def updateUI() {
    basicFilter.setVisibleDocsRangeInputPrompt(docsContainer.visibleDocsRange)
  }

  def reload() {
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
            case IntNum(start) => start
            case _ => sys.error("docs_projection.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        val endOpt = condOpt(basicFormUI.lytIdRange.txtEnd.trim) {
          case value if value.nonEmpty => value match {
            case IntNum(end) => end
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


    val typesOpt: Option[List[String]] =
      if (basicFormUI.chkType.isUnchecked) None
      else {
        import basicFormUI.lytType._

        Map(chkFile -> "file",
            chkText -> "text",
            chkHtml -> "html",
            chkUrl -> "url"
        ).filterKeys(_.isChecked).values.toList match {
          case Nil => None
          case values => Some(values)
        }
      }

    val statusesOpt: Option[List[String]] =
      if (basicFormUI.chkStatus.isUnchecked) None
      else {
        import basicFormUI.lytStatus._

        Map(chkNew -> "new",
            chkPublished -> "published",
            chkUnpublished -> "unpublished",
            chkApproved -> "approved",
            chkDisapproved -> "disapproved",
            chkExpired -> "expired"       // archived
        ).filterKeys(_.isChecked).values.toList match {
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
            (name, dr) <- Map("created" -> drCreated, "modified" -> drModified, "published" -> drPublished, "expired" -> drExpired)
            if dr.cbRangeType.value != DateRangeType.Undefined
            start = Option(dr.dtFrom.value)
            end = Option(dr.dtTo.value)
            if start.isDefined || end.isDefined

            // todo: check start/end value
          } yield
            name -> DateRange(start, end)

        if (datesMap.isEmpty) None else Some(datesMap.toMap)
      }

//    // Not yet defined how to make such query
//    val relationshipsOpt =
//      if (advancedFormUI.chkRelationships.isUnchecked) None
//      else {
//        val parentsOpt = advancedFormUI.lytRelationships.cbParents.value
//        val chidrenOpt = advancedFormUI.lytRelationships.cbChildren.value
//      }

    val categoriesOpt: Option[List[String]] =
      if (advancedFormUI.chkCategories.isUnchecked) None
      else {
        advancedFormUI.tcsCategories.getItemIds.asInstanceOf[JCollection[String]].asScala.toList match {
          case Nil => None
          case values => Some(values)
        }
      }

    val creatorsOpt: Option[List[String]] = None
    val publishersOpt: Option[List[String]] = None

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
        ).map(field => """%s:*"%s"*""".format(field, escapedText)).mkString("+(", " OR " ,")")
      },
      typesOpt.map(_.mkString("type:(", " OR ", ")")),
      statusesOpt.map(_.mkString("status:(", " OR ", ")"))
    ).flatten |> {
      case Nil => "*:*"
      case terms => terms.mkString(" AND ")
    } |> { solrQueryStirng =>
      if (logger.isDebugEnabled) logger.debug("Projection SOLr query string: %s.".format(solrQueryStirng))

      new SolrQuery(solrQueryStirng)
    } |>> { solrQuery =>
      for (idRange <- idRangeOpt) {
        solrQuery.addFilterQuery(
          "%s:[%s TO %s]".format(DocumentIndex.FIELD__META_ID, idRange.start.getOrElse("*"), idRange.end.getOrElse("*"))
        )
      }
    } |>> { solrQuery =>
      if (logger.isDebugEnabled) logger.debug("Projection final SOLr query: %s.".format(solrQuery))
    }
  } // def createSolrQuery()


  def selection: Seq[DocumentDomainObject] = selectionRef.get

  override def notifyListeners(): Unit = notifyListeners(selection)
}

//DocumentIndex.FIELD__META_ID,
//DocumentIndex.FIELD__META_HEADLINE,
//DocumentIndex.FIELD__META_TEXT,
//DocumentIndex.FIELD__TEXT,
//DocumentIndex.FIELD__KEYWORD,
//DocumentIndex.FIELD__ALIAS