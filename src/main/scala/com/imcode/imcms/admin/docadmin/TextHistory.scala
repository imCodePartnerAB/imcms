package com.imcode
package imcms.admin.docadmin

import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.vaadin.ui._
import imcode.server.document.textdocument.TextDomainObject
import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.dao.TextDao
import com.imcode.imcms.vaadin.ui.dialog._
import com.vaadin.server.Sizeable
import org.joda.time.DateTime
import java.util.Date
import scala.Some
import com.imcode.imcms.vaadin.data.PropertyDescriptor

class TextHistory(text: TextDomainObject) extends ImcmsServicesSupport {
  private val textDao = imcmsServices.getSpringBean(classOf[TextDao])

  val ui = new TextHistoryUI(s"Document history") |>> { ui =>
    def values(id: Long = 0, dateTime: DateTime = DateTime.now()): Stream[(Long, Option[Date], Date)] = {
      val nextId = id + 1
      val date = dateTime.toDate

      id % 10 match {
        case 0 => (id, Some(date), date) #:: values(nextId, dateTime.minusDays(1))
        case _ => (id, None,       date) #:: values(nextId, dateTime.minusHours(1))
      }
    }

    values().take(100).foreach {
      case (id, dateOpt, time) => ui.tblHistoryDetails.addRow(
        id,
        dateOpt.map(_.formatted("%1$td.%1$tm.%1$tY")).orNull,
        time.formatted("%1$tH:%1$tM:%1$tS"),
        "admin")
    }
  }
}

class TextHistoryUI(caption: String) extends CustomComponent with FullSize {

  private val pnlCompositionRoot = new Panel(caption) with FullSize

  val spPreview = new HorizontalSplitPanel with FullSize

  val cbHistoryRange = new ComboBox(null, java.util.Arrays.asList("Full", "Today", "Last week")) with NoNullSelection with Immediate with FullWidth
  val tblHistoryDetails = new Table with Selectable with SingleSelect[JLong] with Immediate with FullSize |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[JLong]("id"),
      PropertyDescriptor[String]("date"),
      PropertyDescriptor[String]("time"),
      PropertyDescriptor[String]("user"))

    tbl.setVisibleColumns(Array("date", "time", "user"))
    tbl.setSortEnabled(false)
  }

  //val btnSaveInTextFieldAndReload = new Button()
  //val btnCopyThisVersionToTheTextEditor = new Button()

  val chkPreviewAsHtml = new CheckBox("Show HTML")
  val txaPreview = new TextArea with FullSize
  private val lytPreview = new VerticalLayout(
    new HorizontalLayout(chkPreviewAsHtml) with Margin,
    txaPreview) with FullSize

  lytPreview.setExpandRatio(txaPreview, 1.0f)

  private val lytHistory = new VerticalLayout with FullSize

  lytHistory.addComponents(
    new HorizontalLayout(new Label("Range") with UndefinedSize, cbHistoryRange) with Margin with Spacing with FullWidth |>> {
      _.setExpandRatio(cbHistoryRange, 1.0f)
    },

    tblHistoryDetails)
  lytHistory.setExpandRatio(tblHistoryDetails, 1.0f)

  spPreview.setFirstComponent(lytHistory)
  spPreview.setSecondComponent(lytPreview)
  spPreview.setSplitPosition(30, Sizeable.Unit.PERCENTAGE)

  pnlCompositionRoot.setContent(spPreview)

  setCompositionRoot(pnlCompositionRoot)
}


class TextHistoryDialog(caption: String, text: TextDomainObject) extends OkCancelDialog("") with CustomSizeDialog with Resizable {

  val textHistory = new TextHistory(text)

  mainUI = textHistory.ui

  this.setSize(600, 600)
}
