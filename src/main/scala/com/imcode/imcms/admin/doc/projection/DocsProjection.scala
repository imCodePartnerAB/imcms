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
import _root_.imcode.server.user.UserDomainObject

class DocsProjection(user: UserDomainObject) extends Publisher[Seq[DocId]] {
  val basicFilter = new BasicFilter
  val advancedFilter = new AdvancedFilter
  val docsContainer = new IndexedDocsContainer(user)
  val docsUI = new IndexedDocsUI(docsContainer) with FullSize
  private val selectionRef = new AtomicReference(Seq.empty[DocId])

  val ui = new DocsProjectionUI(basicFilter.ui, advancedFilter.ui, docsUI) { ui =>
    val basicFilterUI = basicFilter.ui

    basicFilterUI.lytAdvanced.btnCustomize.addClickHandler { ui.toggleAdvancedFilter() }
    basicFilterUI.chkAdvanced.addValueChangeHandler {
      if (!basicFilterUI.chkAdvanced.booleanValue) ui.isAdvancedFilterVisible = false
    }

    basicFilterUI.lytButtons.btnFilter.addClickHandler { filter() }
    basicFilterUI.lytButtons.btnReset.addClickHandler { reset() }

    override def attach() {
      super.attach()
      reset()
    }
  }

  docsUI.addValueChangeHandler {
    selectionRef.set(docsUI.value.asScala.toSeq)
    notifyListeners()
  }


  def reset() {
    basicFilter.reset()
    advancedFilter.reset()
    update()
    filter()
  }


  def update() {
    basicFilter.setVisibleDocsRangeInputPrompt(docsContainer.visibleDocsRange)
  }

  def filter() {
    createQuery() match {
      case Left(throwable) =>
        ui.rootWindow.show(new ErrorDialog(throwable.getMessage.i))

      case Right(solrQueryOpt) =>
        println("Doc solr search query: " + solrQueryOpt)

        ui.removeComponent(0, 1)
        ui.addComponent(docsUI, 0, 1)

        docsContainer.setSolrQuery(if (solrQueryOpt.isDefined) solrQueryOpt else Some("*"))


    }
  }


  /**
   * Creates and returns query string.
   *
   * @return query string.
   */
  def createQuery(): Throwable Either Option[String] = Ex.allCatch.either {
    val basicFormUI = basicFilter.ui
    val advancedFormUI = advancedFilter.ui

    val idRangeOpt =
      if (basicFormUI.chkIdRange.isUnchecked) None
      else {
        val start = condOpt(basicFormUI.lytIdRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case IntNum(start) => start
            case _ => sys.error("doc.search.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        val end = condOpt(basicFormUI.lytIdRange.txtStart.trim) {
          case value if value.nonEmpty => value match {
            case IntNum(end) => end
            case _ => sys.error("doc.search.dlg_param_validation_err.msg.illegal_range_value")
          }
        }

        whenOpt(start.isDefined || end.isDefined) {
          IdRange(start, end)
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
            chkHtml -> "html"
        ).filterKeys(_.isChecked).values.toList match {
          case Nil => None
          case values => Some(values)
        }
    }

    val statusesOpt: Option[List[String]] =
      if (advancedFormUI.chkStatus.isUnchecked) None
      else {
      import advancedFormUI.lytStatus._

        Map(chkNew -> "new",
            chkPublished -> "published",
            chkUnpublished -> "unpublished",
            chkApproved -> "approved",
            chkDisapproved -> "disapproved",
            chkExpired -> "expired"
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

    List(
      idRangeOpt.map(range => "range:[%s TO %s]".format(range.start.getOrElse("*"), range.end.getOrElse("*"))),
      textOpt.map("text:" + _),
      typesOpt.map(_.mkString("type:(", " OR ", ")")),
      statusesOpt.map(_.mkString("status:(", " OR ", ")"))
    ).flatten match {
      case Nil => None
      case terms => Some(terms.mkString(" "))
    }
  } // def createQuery()

  def selection: Seq[DocId] = selectionRef.get

  override def notifyListeners(): Unit = notifyListeners(selection)
}
