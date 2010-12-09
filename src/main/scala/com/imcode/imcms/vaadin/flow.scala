package com.imcode.imcms.vaadin.flow

import com.imcode._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.ui.Window.Notification


/**
 * Single flow page.
 * To extend flexibility page ui is not referenced directly but rather returned by a function.
 *
 * @param ui return flow page ui
 * @param validator page data validator - returns None if page is ok or Some(error message)
 */
class FlowPage(val ui: () => AbstractComponent, val validator: () => Option[String])


/**
 * Page flow consist of at least one flow page.
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
 * Flow bar ui - just buttons.
 * If used in a dialog then flow bar should replace (/be used instead) dialog buttons bar.
 */
class FlowBarUI extends HorizontalLayout with Spacing with UndefinedSize {
  val btnPrev = new Button("Prev") with ResourceCaption
  val btnNext = new Button("Next") with ResourceCaption
  val btnFinish = new Button("Finish") with ResourceCaption
  val btnCancel = new Button("Cancel") with ResourceCaption

  addComponents(this, btnCancel, btnPrev, btnNext, btnFinish)
}


class FlowUI[T](flow: Flow[T], onCommit: T => Unit) extends VerticalLayout with FullSize with Spacing {
  val lytPageUI = new VerticalLayout with UndefinedSize
  val pnlPageUI = new Panel(lytPageUI) with FullSize {
    setStyleName(Panel.STYLE_LIGHT)
    setScrollable(true)
  }
  val flowBar = new FlowBarUI

  private def setPageUI(page: FlowPage) {
    val ui = page.ui()
    
    pnlPageUI.removeAllComponents
    pnlPageUI.addComponent(ui)
    lytPageUI.setComponentAlignment(ui, Alignment.TOP_CENTER)
  }

  flowBar.btnPrev addListener block {
    flow.maybeGoPrev match {
      case Some(page) => setPageUI(page)
      case _ => getWindow.showNotification("This is the first page", "Press 'Next' or 'Finish'", Notification.TYPE_WARNING_MESSAGE)
    }
  }

  flowBar.btnNext addListener block {
    flow.maybeGoNext match {
      case Left(errorMsg) => getWindow.showNotification("Can't go to the next page", errorMsg, Notification.TYPE_ERROR_MESSAGE)
      case Right(Some(page)) => setPageUI(page)
      case _ => getWindow().showNotification("This is the last page", "Press 'Finish'", Notification.TYPE_WARNING_MESSAGE)
    }
  }

  flowBar.btnFinish addListener block {
    flow.commit() match {
      case Left(errorMsg) => getWindow.showNotification("Can't commit flow", errorMsg, Notification.TYPE_ERROR_MESSAGE)
      case Right(result) => onCommit(result)
    }
  }

  setPageUI(flow.page)
  addComponents(this, pnlPageUI, flowBar)
  setExpandRatio(pnlPageUI, 1.0f)
  setComponentAlignment(flowBar, Alignment.TOP_CENTER)
}