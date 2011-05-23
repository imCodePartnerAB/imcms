package com.imcode
package imcms
package admin.doc

import com.imcode.imcms.vaadin._

import scala.collection.JavaConversions._
import vaadin.{ImcmsApplication, FullSize}
import imcode.server.Imcms
import com.vaadin.event.Action
import com.vaadin.ui._


object Actions {
  val View = new Action("doc.tbl.action.view".i)
  val AddToSelection = new Action("doc.tbl.action.add_to_selection".i)
  val Edit = new Action("doc.tbl.action.edit".i)
  val Exclude = new Action("doc.tbl.action.exclude".i)
  val Delete = new Action("doc.tbl.action.delete".i)
}


class DocManager(app: ImcmsApplication) {
  val customDocs = new CustomDocs
  val search = new DocSearch(new DBDocsContainer)

  val docSelectionDlg = letret(new OKDialog("Custom documents") with CustomSizeDialog) { dlg =>
    dlg.mainUI = customDocs.ui
    dlg.setSize(500, 500)
  }

  val ui = letret(new DocManagerUI(search.ui)) { ui =>
    ui.miViewCustom.setCommandHandler {
      app.show(docSelectionDlg, modal = false, resizable = true)
    }

    search.docViewUI.addActionHandler(new Action.Handler {
      import Actions._

      def getActions(target: AnyRef, sender: AnyRef) = Array(AddToSelection, View, Edit, Delete)

      def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
        action match {
          case AddToSelection =>
            customDocs.search.docsContainer.addItem(target)
            customDocs.search.update()
            customDocs.search.submit()
          case _ =>
        }
    })
  }

  search.submit()
}

class DocManagerUI(searchUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("Document")
  val miDocNew = miDoc.addItem("New")
  val miDocEdit = miDoc.addItem("Edit")
  val miDocDelete = miDoc.addItem("Delete")
  val miView = mb.addItem("View")
  val miViewCustom = miView.addItem("Custom")

  addComponents(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}



class CustomDocs {
  val search = new DocSearch(new CustomDocsContainer)
  val ui = new CustomDocsUI(search.ui)

  search.docViewUI.addActionHandler(new Action.Handler {
    import Actions._

    def getActions(target: AnyRef, sender: AnyRef) = Array(Exclude, View, Edit, Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case Exclude => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}


class CustomDocsUI(searchUI: Component) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miDoc = mb.addItem("Document")

  addComponents(this, mb, searchUI)
  setExpandRatio(searchUI, 1.0f)
}