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

class DocBasicSearchUI extends CustomLayout("doc_search_basic") {
  setWidth("700px")

  val lblRange = new Label("doc.basic.search.frm.lbl.range".i) with UndefinedSize
  val lytRange = new HorizontalLayout with Spacing with UndefinedSize {
    val txtFrom = new TextField { setInputPrompt("doc.basic.search.frm.txt.range.from.prompt".i); setColumns(5) }
    val txtTo = new TextField { setInputPrompt("doc.basic.search.frm.txt.range.to.prompt".i); setColumns(5) }

    addComponents(this, txtFrom, txtTo)
  }

  val lblText = new Label("doc.basic.search.frm.lbl.text".i) with UndefinedSize
  val txtText = new TextField { setInputPrompt("doc.basic.search.frm.txt.text.prompt".i) }

  val lblStatus = new Label("doc.basic.search.frm.lbl.status".i) with UndefinedSize
  val lytStatus = new HorizontalLayout with Spacing with UndefinedSize {
    val chkNew = new CheckBox("doc.basic.search.frm.ckh.status.new".i)
    val chkPublished = new CheckBox("doc.basic.search.frm.chk.status.published".i)
    val chkExpired = new CheckBox("doc.basic.search.frm.chk.status.expired".i)

    addComponents(this, chkNew, chkPublished, chkExpired)
  }

  val lblAdvanced = new Label("doc.basic.search.frm.lbl.advanced".i) with UndefinedSize
  val lytAdvanced = new HorizontalLayout with UndefinedSize {
    val chkAdvanced = new CheckBox
    val btnAdvanced = new Button("doc.basic.search.frm.btn.advanced".i) with LinkStyle

    addComponents(this, chkAdvanced, btnAdvanced)
  }

  val lytButtons = new HorizontalLayout with UndefinedSize with Spacing {
    val btnClear = new Button("doc.basic.search.frm.btn.clear".i) { setStyleName("small") }
    val btnSearch = new Button("doc.basic.search.frm.btn.search".i) { setStyleName("small") }

    addComponents(this, btnClear, btnSearch)
  }

  addNamedComponents(this,
    "doc.basic.search.frm.lbl.range" -> lblRange,
    "doc.basic.search.frm.range" -> lytRange,
    "doc.basic.search.frm.lbl.text" -> lblText,
    "doc.basic.search.frm.txt.text" -> txtText,
    "doc.basic.search.frm.lbl.status" -> lblStatus,
    "doc.basic.search.frm.status" -> lytStatus,
    "doc.basic.search.frm.lbl.advanced" -> lblAdvanced,
    "doc.basic.search.frm.advanced" -> lytAdvanced,
    "doc.basic.search.frm.buttons" -> lytButtons
  )
}


class DocCustomSearchUI extends CustomLayout("doc_search_custom") {
  setWidth("700px")


//  addNamedComponents(this,
//    "doc.basic.search.frm.lbl.range" -> lblRange,
//    "doc.basic.search.frm.range" -> lytRange,
//    "doc.basic.search.frm.lbl.text" -> lblText,
//    "doc.basic.search.frm.txt.text" -> txtText,
//    "doc.basic.search.frm.lbl.status" -> lblStatus,
//    "doc.basic.search.frm.status" -> lytStatus,
//    "doc.basic.search.frm.lbl.custom" -> lblCustom,
//    "doc.basic.search.frm.custom" -> lytCustom,
//    "doc.basic.search.frm.buttons" -> lytButtons
//  )
}