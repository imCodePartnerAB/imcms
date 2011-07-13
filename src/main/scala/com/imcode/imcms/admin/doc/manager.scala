package com.imcode
package imcms
package admin.doc

import com.imcode.imcms.vaadin._

import scala.collection.JavaConversions._
import vaadin.{ImcmsApplication, FullSize}
import com.vaadin.event.Action
import _root_.com.imcode.imcms.admin.doc.search.{DocSearch, AllDocsContainer, CustomDocsContainer}
import _root_.com.imcode.imcms.admin.doc.meta.{Properties => DocProperties}
import com.vaadin.ui._
import imcode.server.document.{FileDocumentDomainObject, HtmlDocumentDomainObject}
import imcode.server.document.textdocument.TextDocumentDomainObject

object Actions {
  val IncludeToSelection = new Action("doc.action.include_to_selection".i)
  val ExcludeFromSelection = new Action("doc.action.exclude_from_selection".i)
  val Delete = new Action("doc.action.delete".i)
  val ManageProperties = new Action("doc.action.manage_properties".i)
}


class DocManager(app: ImcmsApplication) extends ImcmsServicesSupport {
  val customDocs = new CustomDocs
  val search = new DocSearch(new AllDocsContainer)

  val docSelectionDlg = letret(new OKDialog("doc.dlg_selection.caption".i) with CustomSizeDialog) { dlg =>
    dlg.mainUI = customDocs.ui
    dlg.setSize(500, 500)
  }

  val ui = letret(new DocManagerUI(search.ui)) { ui =>
    ui.miShowSelection.setCommandHandler {
      app.show(docSelectionDlg, modal = false, resizable = true)
    }

    ui.miProperties.setCommandHandler {
      val dlg = new OKDialog("Doc properties") with CustomSizeDialog with BottomMarginDialog
      val properties = new DocProperties(app, imcmsServices.getDocumentMapper.getWorkingDocument(search.docsUI.first.get.intValue))

      dlg.mainUI = properties.ui

      dlg.setSize(500, 500)
      app.show(dlg, resizable = true)
    }

    ui.miDocNewURL.setCommandHandler {
      val dlg = new OKDialog("New URL Document") with CustomSizeDialog with BottomMarginDialog

      val doc = new HtmlDocumentDomainObject
      val properties = new DocProperties(app, doc)
      val contentUI = new URLDocEditorUI

      dlg.mainUI = letret(new com.vaadin.ui.TabSheet with FullSize) { ts =>
        ts.addTab(properties.ui, "Properties", null)
        ts.addTab(contentUI, "Content", null)
      }

      dlg.setSize(500, 500)
      app.show(dlg, resizable = true)
    }

    ui.miDocNewFile.setCommandHandler {
      val dlg = new OKDialog("New File Document") with CustomSizeDialog with BottomMarginDialog

      val doc = new FileDocumentDomainObject
      val properties = new DocProperties(app, doc)
      val contentUI = new FileDocEditor(app, doc, Seq.empty).ui

      dlg.mainUI = letret(new com.vaadin.ui.TabSheet with FullSize) { ts =>
        ts.addTab(properties.ui, "Properties", null)
        ts.addTab(contentUI, "Content", null)
      }

      dlg.setSize(500, 500)
      app.show(dlg, resizable = true)
    }

    ui.miDocNewText.setCommandHandler {
      val dlg = new OKDialog("New Text Document") with CustomSizeDialog with BottomMarginDialog

      val doc = new TextDocumentDomainObject
      val properties = new DocProperties(app, doc)
      val contentUI = new NewTextDocumentFlowPage2UI

      dlg.mainUI = letret(new com.vaadin.ui.TabSheet with FullSize) { ts =>
        ts.addTab(properties.ui, "Properties", null)
        ts.addTab(contentUI, "Content", null)
      }

      dlg.setSize(500, 500)
      app.show(dlg, resizable = true)
    }

    search.docsUI.addActionHandler(new Action.Handler {
      import Actions._

      def getActions(target: AnyRef, sender: AnyRef) = Array(IncludeToSelection, Delete)

      def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
        action match {
          case IncludeToSelection =>
            customDocs.search.docsContainer.addItem(target)
            customDocs.search.update()
            customDocs.search.search()
          case _ =>
        }
    })
  }
}

class DocManagerUI(searchUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.mgr.mi.doc".i)
  val miDocNew = miDoc.addItem("doc.action.new".i)
  val miDocNewFile = miDocNew.addItem("doc.action.new.file_doc".i)
  val miDocNewURL = miDocNew.addItem("doc.action.new.url_doc".i)
  val miDocNewText = miDocNew.addItem("doc.action.new.text_doc".i)
  val miDocEdit = miDoc.addItem("doc.action.edit".i)
  val miDocDelete = miDoc.addItem("doc.action.delete".i)
  val miView = mb.addItem("doc.mgr.mi.view".i)
  val miShowSelection = miView.addItem("doc.action.show_selection".i)
  val miProperties = mb.addItem("doc.action.manage_properties".i)

  addComponents(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}



/**
 * Custom docs selection.
 */
class CustomDocs {
  val search = new DocSearch(new CustomDocsContainer)
  val ui = new CustomDocsUI(search.ui)

  search.docsUI.addActionHandler(new Action.Handler {
    import Actions._

    def getActions(target: AnyRef, sender: AnyRef) = Array(ExcludeFromSelection, Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case ExcludeFromSelection => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}


class CustomDocsUI(searchUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("doc.selection.mi.doc".i)

  addComponents(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}