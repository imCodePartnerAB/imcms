package com.imcode
package imcms.admin.docadmin

import com.imcode.imcms.vaadin.ui._
import com.vaadin.ui._
import imcode.server.document.textdocument.TextDomainObject
import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.dao.TextDao
import com.imcode.imcms.vaadin.ui.dialog.{CustomSizeDialog, OkCancelDialog}

class TextHistory(text: TextDomainObject) extends ImcmsServicesSupport {
  private val textDao = imcmsServices.getSpringBean(classOf[TextDao])

  val ui = new TextHistoryUI |>> { ui =>

  }
}

class TextHistoryUI extends VerticalLayout with FullSize {

  val spPreview = new VerticalSplitPanel with FullSize

  val cbHistoryRange = new ComboBox("Range", "Full", "Today", "Last week")
  val ttHistoryDetails = new TreeTable("Details")

  val btnSaveInTextFieldAndReload = new Button()
  val btnCopyThisVersionToTheTextEditor = new Button()

  val chkPreviewAsHtml = new CheckBox("As HTML")
  val lytPreview = new VerticalLayout()

  private val lytHistory = new VerticalLayout("History") with FullWidth

  lytHistory.addComponents(cbHistoryRange, ttHistoryDetails)

  spPreview.setFirstComponent(lytHistory)
  spPreview.setSecondComponent(lytPreview)

  addComponent(spPreview)
}


class TextHistoryDialog(caption: String, text: TextDomainObject) extends OkCancelDialog with CustomSizeDialog {

  val textHistory = new TextHistory(text)

  mainUI = textHistory.ui

  this.setSize(600, 600)
}
