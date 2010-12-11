package com.imcode.imcms.vaadin.flow

import com.imcode._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.ui.Window.Notification

//todo: connect flow and flowUI through (pageChanged) listeners ->
//  then we can rewind a flow to a custom page in case of an error


/**
 * Single flow page.
 * To extend flexibility page ui is not referenced directly but rather returned by a function.
 *
 * @param ui returns flow page ui
 * @param validator page validates page data and/or stores page data into associated a model;
 *        returns Some(error message) or None if page data is valid
 */
class FlowPage(val ui: () => ComponentContainer, val validator: Function0[Option[String]] = () => Option.empty)


/**
 * Page flow consist at least of one page.
 * Contains flow pages and logic to switch between flow pages and commit flow.
 *
 * @param fist the first page of the flow
 * @param rest rest pages of the flow
 * @param commit commits flow data - returns either error message or commit result.
 */
class Flow[T](val commit: () => Either[String, T], first: FlowPage, rest: FlowPage*) {
  val pages = first +: rest
  private var pageNoRef = new AtomicReference(0) // current page no ref
  private val lastPageNo = pages.length - 1;     // last page no

  /** Returns current flow page. */
  def page = pages(pageNoRef.get)

  /** Returns if the flow page is the first. */
  def isFirstPage = page == pages.head

  /** Returns if the flow page is the last. */
  def isLastPage = page == pages.last

  /**
   * If page validation fails returns failure message in Left.
   * Otherwise if current page is the last page in the flow returns None. If there are more pages,
   * next page become current and returned in Some.
   */
  def maybeGoNext(): Either[String, Option[FlowPage]] = let(pageNoRef.get) { pageNo =>
    pages(pageNo).validator() match {
      case Some(ex) => Left(ex)
      case _ if pageNo == lastPageNo => Right(None)
      case _ => let(pageNo + 1) { newPageNo =>
        pageNoRef.set(newPageNo)
        Right(Some(pages(newPageNo)))
      }
    }
  }

  /**
   * If current page is the first page return None.
   * Otherwise previous page become current and returned in Some.
   */
  def maybeGoPrev(): Option[FlowPage] = pageNoRef.get match {
    case 0 => None
    case pageNo => let(pageNo - 1) { newPageNo =>
      pageNoRef.set(newPageNo)
      Some(pages(newPageNo))
    }
  }
}


/**
 * Flow bar ui - just navigation buttons.
 * If used in a dialog then flow bar should replace (/be used instead of) dialog buttons bar.
 */
class FlowBarUI extends HorizontalLayout with Spacing with UndefinedSize {
  val btnPrev = new Button("Prev") with ResourceCaption
  val btnNext = new Button("Next") with ResourceCaption
  val btnFinish = new Button("Finish") with ResourceCaption
  val btnCancel = new Button("Cancel") with ResourceCaption

  addComponents(this, btnCancel, btnPrev, btnNext, btnFinish)
}


/**
 * FlowUI size must be fixed or full - never undefined.
 */
class FlowUI[T](flow: Flow[T], onCommit: T => Unit) extends VerticalLayout with FullSize with Spacing {
  val pnlPageUI = new Panel with FullSize {
    setStyleName(Panel.STYLE_LIGHT)
    setScrollable(true)
  }
  val bar = new FlowBarUI

  private def setPageUI(page: FlowPage) {
    val ui = page.ui()
    //todo: assert ui size - must be fixed or undefined
    pnlPageUI.setContent(ui)
  }

  bar.btnPrev addListener block {
    flow.maybeGoPrev match {
      case Some(page) => setPageUI(page)
      case _ => getWindow.showNotification("This is the first page", "Press 'Next' or 'Finish'", Notification.TYPE_WARNING_MESSAGE)
    }
  }

  bar.btnNext addListener block {
    flow.maybeGoNext match {
      case Left(errorMsg) => getWindow.showNotification("Can't go to the next page", errorMsg, Notification.TYPE_ERROR_MESSAGE)
      case Right(Some(page)) => setPageUI(page)
      case _ => getWindow().showNotification("This is the last page", "Press 'Finish'", Notification.TYPE_WARNING_MESSAGE)
    }
  }

  bar.btnFinish addListener block {
    flow.commit() match {
      case Left(errorMsg) => getWindow.showNotification("Can't commit flow", errorMsg, Notification.TYPE_ERROR_MESSAGE)
      case Right(result) => onCommit(result)
    }
  }

  setPageUI(flow.page)
  addComponents(this, pnlPageUI, bar)
  setExpandRatio(pnlPageUI, 1.0f)
  setComponentAlignment(bar, Alignment.TOP_CENTER)
}