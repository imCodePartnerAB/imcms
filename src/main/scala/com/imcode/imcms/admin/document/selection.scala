package com.imcode
package imcms
package admin.document

import scala.collection.JavaConversions._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}

import vaadin.{ImcmsApplication, FullSize}
import com.vaadin.ui.Table.CellStyleGenerator
import com.vaadin.ui._
import imcode.server.Imcms
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.vaadin.terminal.{ExternalResource, Resource}
import imcode.server.document.{LifeCyclePhase, DocumentDomainObject}
import com.vaadin.event.Action

class DocSelection(app: ImcmsApplication) {
  val ui = new DocSelectionUI

  ui.tblDocs.addActionHandler(new Action.Handler {
    import Actions._

    def getActions(target: AnyRef, sender: AnyRef) = Array(Exclude, View, Edit, Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case Exclude => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}


class DocSelectionUI extends VerticalLayout with Spacing with FullSize {
  val tblDocs = DocTableUI(fullSize = true)
  val mb = new MenuBar
  val miDoc = mb.addItem("Document")
  val miView = mb.addItem("Filter") // -> search in selection

  addComponents(this, mb, new DocBasicSearchUI, tblDocs)
  setExpandRatio(tblDocs, 1.0f)
}

class DocBasicSearch {

}

class DocBasicSearchUI extends GridLayout(3, 4) with Spacing with UndefinedSize {
  val lblRange = new Label("doc.search.frm.range.caption".i) with UndefinedSize
  val lytRange = new HorizontalLayout with Spacing with UndefinedSize {
    val txtFrom = new TextField("doc.search.frm.range.txt.from".i)
    val txtTo = new TextField("doc.search.frm.range.txt.to".i)

    addComponents(this, txtFrom, txtTo)
  }
  val btnClearRange = new Button("doc.search.frm.btn.clear".i) with LinkStyle

  val lblText = new Label("doc.search.frm.text.caption".i) with UndefinedSize
  val txtText = new TextField { setInputPrompt("doc.search.frm.text.prompt".i) }
  val btnClearText = new Button("doc.search.frm.btn.clear".i) with LinkStyle

  val lblStatus = new Label("doc.search.frm.status.caption".i) with UndefinedSize
  val lytStatus = new HorizontalLayout with Spacing with UndefinedSize {
    val chkNew = new CheckBox("doc.search.frm.ckh.status.new".i)
    val chkPublished = new CheckBox("doc.search.frm.chk.status.published".i)
    val chkExpired = new CheckBox("doc.search.frm.chk.status.expired".i)

    addComponents(this, chkNew, chkPublished, chkExpired)
  }
  val btnClearStatus = new Button("doc.search.frm.btn.clear".i) with LinkStyle

  val lytAdvanced = new HorizontalLayout with Spacing with UndefinedSize {
    val chkAdvanced = new CheckBox("doc.search.frm.chk.advanced".i)
    val btnAdvanced = new Button("...") { setStyleName("small") }

    addComponents(this, chkAdvanced, btnAdvanced)
  }
  val btnClearAll = new Button("doc.search.frm.btn.clear_all".i) with LinkStyle

  addComponents(this,
    lblRange, lytRange, btnClearRange,
    lblText, txtText, btnClearText,
    lblStatus, lytStatus, btnClearStatus)

  addComponent(lytAdvanced, 0, 3, 1, 3)
  addComponent(btnClearAll)
}


