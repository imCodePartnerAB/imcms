package com.imcode
package imcms
package admin.document

import com.imcode.imcms.vaadin._

import scala.collection.JavaConversions._
import vaadin.{ImcmsApplication, FullSize}
import com.vaadin.ui.{GridLayout, MenuBar, Table, VerticalLayout}
import imcode.server.Imcms
import dao.MetaDao
import imcode.server.document.DocumentDomainObject
import api.Document


class DocManager(app: ImcmsApplication) {
  val docSelection = new DocSelection(app)

  val docSelectionDlg = letret(new OKDialog("Selected documents") with CustomSizeDialog) { dlg =>
    dlg.mainUI = docSelection.ui
    dlg.setSize(500, 500)
  }

  val ui = letret(new DocManagerUI) { ui =>
    ui.miViewSelection.setCommandHandler {
      app.show(docSelectionDlg, modal = false, resizable = true)
    }
  }

  reload()

  // temp
  def reload() {
    val metaDao = Imcms.getSpringBean("metaDao").asInstanceOf[MetaDao]

    metaDao.getAllDocumentIds.toList.foreach { id =>
      val meta = metaDao getMeta id
      val alias = meta.getProperties.get(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS) match {
        case null => ""
        case value => value
      }

      val status = meta.getPublicationStatus match {
        case Document.PublicationStatus.NEW => "New"
        case Document.PublicationStatus.APPROVED => "Approved"
        case Document.PublicationStatus.DISAPPROVED => "Disapproved"
      }

      ui.tblDocs.addItem(Array[AnyRef](alias, status, meta.getDocumentType, id.toString, Int box 0, Int box 0), id)
    }
  }
}

class DocManagerUI extends VerticalLayout with Spacing with FullSize {
  val tblDocs = DocTableUI()
  val mb = new MenuBar
  val miDoc = mb.addItem("Document")
  val miDocNew = miDoc.addItem("New")
  val miDocEdit = miDoc.addItem("Edit")
  val miDocDelete = miDoc.addItem("Delete")
  val miSearch = mb.addItem("Search")
  val miView = mb.addItem("View")
  val miViewSelection = miView.addItem("Selection")

  addComponents(this, mb, tblDocs)
}